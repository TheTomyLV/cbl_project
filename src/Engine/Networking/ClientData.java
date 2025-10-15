package Engine.Networking;

import java.net.InetAddress;
import java.util.UUID;

/** Lightweight server-side record for a connected client. */
public class ClientData {
    public final UUID id;
    public final InetAddress address;
    public final int port;

    public volatile int lastSeqFromClient = 0;
    public volatile int lastAckSent = 0;
    public volatile long lastHeardAtMs = 0L;

    public ClientData(UUID id, InetAddress address, int port) {
        this.id = id;
        this.address = address;
        this.port = port;
    }
}