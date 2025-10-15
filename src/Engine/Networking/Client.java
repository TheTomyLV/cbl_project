package Engine.Networking;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * UDP client with simple seq/ack, lightweight reliable resend, and keep-alive.
 * Integrate by feeding/consuming byte[] payloads at your game layer.
 */
public class Client implements Runnable {
    private static final int SOCKET_TIMEOUT_MS = 25;     // prevents blocking forever
    private static final int MAX_DATAGRAM = 1500;        // safe upper bound
    private static final int RESEND_INTERVAL_MS = 350;   // naive resend cadence for reliable packets
    private static final int KEEPALIVE_MS = 1000;        // send ping if quiet

    private final UUID clientId = UUID.randomUUID();

    private DatagramSocket socket;
    private InetAddress serverAddr;
    private int serverPort;
    private Thread ioThread;
    private volatile boolean running = false;

    private final Map<Integer, Pending> pending = new ConcurrentHashMap<>(); // seq -> pending
    private int nextSeq = 1;
    private volatile int lastReceivedSeq = 0;
    private volatile long lastHeardAtMs = 0L;
    private volatile long rttMs = 100;

    // Hook for your game layer
    public interface Listener {
        void onPacket(Packet pkt);
        void onDisconnected(String reason);
    }

    private final Listener listener;

    public Client(Listener listener) {
        this.listener = listener;
    }

    public void connect(String host, int port) throws IOException {
        if (running) return;
        this.serverAddr = InetAddress.getByName(host);
        this.serverPort = port;
        this.socket = new DatagramSocket();
        this.socket.setSoTimeout(SOCKET_TIMEOUT_MS);
        this.running = true;
        this.ioThread = new Thread(this, "Client-UDP-IO");
        this.ioThread.start();
    }

    public void close() {
        running = false;
        if (socket != null && !socket.isClosed()) socket.close();
        pending.clear();
        if (listener != null) listener.onDisconnected("closed");
    }

    /** Send an application payload. Set reliable=true for important messages. */
    public void send(byte[] appPayload, short payloadType, boolean reliable, short flagsExtra) throws IOException {
        if (!running) throw new SocketException("client not running");
        int now = (int) (System.currentTimeMillis() & 0x7fffffff);

        Packet pkt = new Packet(clientId, appPayload);
        short flags = (short) (flagsExtra | (reliable ? 4 : 0));
        pkt.setHeader(nextSeq, lastReceivedSeq, now, flags, payloadType);
        byte[] bytes = pkt.toBytes();

        DatagramPacket dp = new DatagramPacket(bytes, bytes.length, serverAddr, serverPort);
        socket.send(dp);

        if (reliable) pending.put(nextSeq, new Pending(bytes));
        nextSeq++;
    }

    /** Convenience ping. */
    public void ping() throws IOException {
        send(new byte[0], (short) 0, false, (short) 2);
    }

    @Override public void run() {
        byte[] buf = new byte[MAX_DATAGRAM];
        long lastResendSweep = System.currentTimeMillis();
        while (running) {
            // receive
            try {
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                socket.receive(dp);
                lastHeardAtMs = System.currentTimeMillis();

                Packet pkt = Packet.fromBytes(dp.getData(), dp.getLength());
                // dedupe/out-of-order discard if you want stricter ordering
                if (pkt.getSequence() > lastReceivedSeq) {
                    lastReceivedSeq = pkt.getSequence();
                }

                // ack-based cleanup
                if (!pending.isEmpty()) {
                    int ack = pkt.getAck();
                    Iterator<Map.Entry<Integer, Pending>> it = pending.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<Integer, Pending> e = it.next();
                        if (e.getKey() <= ack) it.remove();
                    }
                }

                // RTT sample using server timestamp
                int sentAt = pkt.getTimestampMs();
                int now = (int) (System.currentTimeMillis() & 0x7fffffff);
                int sample = now - sentAt;
                if (sample >= 0 && sample < 5000) {
                    rttMs = (long) (0.9 * rttMs + 0.1 * sample);
                }

                if (listener != null) listener.onPacket(pkt);
            } catch (SocketTimeoutException ignore) {
                // fall through for keepalive/resend
            } catch (SocketException se) {
                // closed while blocking
                break;
            } catch (IOException ioe) {
                // swallow but inform once
                if (listener != null) listener.onDisconnected("io error: " + ioe.getMessage());
                break;
            }

            long nowMs = System.currentTimeMillis();
            // keepalive
            if (nowMs - lastHeardAtMs > KEEPALIVE_MS) {
                try { ping(); } catch (IOException ignored) {}
            }
            // naive resend sweep
            if (nowMs - lastResendSweep >= 15) {
                for (Pending p : pending.values()) {
                    if (nowMs - p.firstSentAt >= RESEND_INTERVAL_MS) {
                        try {
                            DatagramPacket dp = new DatagramPacket(p.bytes, p.bytes.length, serverAddr, serverPort);
                            socket.send(dp);
                            p.firstSentAt = nowMs; // reuse field as lastSentAt to avoid more bookkeeping
                        } catch (IOException ignored) {}
                    }
                }
                lastResendSweep = nowMs;
            }
        }
    }

    public long getRttMs() { return rttMs; }
    public UUID getClientId() { return clientId; }

    private static final class Pending {
        final byte[] bytes;
        long firstSentAt;
        Pending(byte[] bytes) { this.bytes = bytes; this.firstSentAt = System.currentTimeMillis(); }
    }
}