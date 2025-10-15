package Engine.Networking;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

/**
 * Compact UDP packet with a small header (seq/ack/timestamp/flags) and raw payload.
 * Keep this class simple and allocation-light; it's on the hot path.
 */
public class Packet {
    // --- header constants ---
    private static final int MAGIC = 0xCB1CB1;     // identifies our protocol
    private static final short VERSION = 1;        // bump if header format changes

    // --- header fields ---
    private int sequence;          // local sequence number (sender increments)
    private int ack;               // last sequence seen from peer
    private int timestampMs;       // sender clock when sending (ms)
    private short flags;           // bitfield: 1 = snapshot, 2 = ping, 4 = reliable
    private short payloadType;     // app-level discriminator if you need multiple message kinds
    private UUID senderId;         // who sent this (optional but useful on server)

    // --- body ---
    private byte[] payload;        // application data (already serialized elsewhere)

    public Packet() {
        this.senderId = new UUID(0L, 0L);
        this.payload = new byte[0];
    }

    public Packet(UUID senderId, byte[] payload) {
        this.senderId = senderId == null ? new UUID(0L, 0L) : senderId;
        this.payload = payload == null ? new byte[0] : payload;
    }

    // --- header accessors ---
    public void setHeader(int sequence, int ack, int timestampMs, short flags, short payloadType) {
        this.sequence = sequence;
        this.ack = ack;
        this.timestampMs = timestampMs;
        this.flags = flags;
        this.payloadType = payloadType;
    }

    public int getSequence() { return sequence; }
    public int getAck() { return ack; }
    public int getTimestampMs() { return timestampMs; }
    public short getFlags() { return flags; }
    public short getPayloadType() { return payloadType; }
    public UUID getSenderId() { return senderId; }
    public byte[] getPayload() { return payload; }

    // --- helpers ---
    public boolean isReliable() { return (flags & 4) != 0; }
    public boolean isPing() { return (flags & 2) != 0; }

    /** Serialize header + payload into a single byte array (little-endian for compactness). */
    public byte[] toBytes() {
        // header layout (bytes):
        // magic(4) | version(2) | seq(4) | ack(4) | ts(4) | flags(2) | ptype(2)
        // | sender MSB(8) | sender LSB(8) | length(4) | payload(len)
        int headerSize = 4 + 2 + 4 + 4 + 4 + 2 + 2 + 8 + 8 + 4;
        int len = payload == null ? 0 : payload.length;
        ByteBuffer bb = ByteBuffer.allocate(headerSize + len).order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(MAGIC);
        bb.putShort(VERSION);
        bb.putInt(sequence);
        bb.putInt(ack);
        bb.putInt(timestampMs);
        bb.putShort(flags);
        bb.putShort(payloadType);
        bb.putLong(senderId.getMostSignificantBits());
        bb.putLong(senderId.getLeastSignificantBits());
        bb.putInt(len);
        if (len > 0) bb.put(payload);
        return bb.array();
    }

    /** Parse bytes into a Packet. Throws if MAGIC/VERSION mismatch or truncated. */
    public static Packet fromBytes(byte[] bytes, int length) throws IOException {
        if (bytes == null || length < 4 + 2) throw new EOFException("packet too short");
        ByteBuffer bb = ByteBuffer.wrap(bytes, 0, length).order(ByteOrder.LITTLE_ENDIAN);
        int magic = bb.getInt();
        short version = bb.getShort();
        if (magic != MAGIC || version != VERSION) throw new StreamCorruptedException("bad packet header");

        Packet p = new Packet();
        p.sequence = bb.getInt();
        p.ack = bb.getInt();
        p.timestampMs = bb.getInt();
        p.flags = bb.getShort();
        p.payloadType = bb.getShort();
        long msb = bb.getLong();
        long lsb = bb.getLong();
        p.senderId = new UUID(msb, lsb);
        int len = bb.getInt();
        if (len < 0 || len > (length - bb.position())) throw new EOFException("invalid payload length");
        p.payload = new byte[len];
        if (len > 0) bb.get(p.payload);
        return p;
    }
}