package Engine.Networking;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * UDP server tracking clients, echoing seq/ack, and providing broadcast/send APIs.
 * Integrate by calling send/broadcast with your serialized game state payloads.
 */
public class Server implements Runnable {
    private static final int MAX_DATAGRAM = 1500;
    private static final int SOCKET_TIMEOUT_MS = 25;
    private static final int FLOOD_WINDOW_MS = 1000;
    private static final int FLOOD_LIMIT = 200; // packets per window per client

    private final int port;
    private DatagramSocket socket;
    private Thread ioThread;
    private volatile boolean running = false;

    // Key clients by (address:port)
    private final Map<String, ClientData> clients = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> nextSeqOut = new ConcurrentHashMap<>();

    // simple flood window
    private long windowStartMs = System.currentTimeMillis();
    private final Map<UUID, Integer> windowCount = new HashMap<>();

    // Hook to deliver incoming packets to game logic
    public interface Handler {
        void onReceive(ClientData from, Packet pkt);
        void onClientTimeout(ClientData cd);
    }

    private final Handler handler;

    public Server(int port, Handler handler) {
        this.port = port;
        this.handler = handler;
    }

    public void start() throws SocketException {
        if (running) return;
        socket = new DatagramSocket(port);
        socket.setSoTimeout(SOCKET_TIMEOUT_MS);
        running = true;
        ioThread = new Thread(this, "Server-UDP-IO");
        ioThread.start();
    }

    public void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) socket.close();
        clients.clear();
        nextSeqOut.clear();
    }

    @Override public void run() {
        byte[] buf = new byte[MAX_DATAGRAM];
        while (running) {
            try {
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                socket.receive(dp);

                Packet pkt = Packet.fromBytes(dp.getData(), dp.getLength());

                String key = dp.getAddress().getHostAddress() + ":" + dp.getPort();
                ClientData cd = clients.get(key);
                if (cd == null) {
                    // first contact: register client id from packet
                    UUID id = pkt.getSenderId();
                    cd = new ClientData(id, dp.getAddress(), dp.getPort());
                    clients.put(key, cd);
                    nextSeqOut.put(id, 1);
                }

                // basic validation: ignore if mismatched id (prevents spoof across same ip:port)
                if (!pkt.getSenderId().equals(cd.id)) continue;

                // flood window
                long now = System.currentTimeMillis();
                if (now - windowStartMs > FLOOD_WINDOW_MS) {
                    windowCount.clear();
                    windowStartMs = now;
                }
                windowCount.put(cd.id, windowCount.getOrDefault(cd.id, 0) + 1);
                if (windowCount.get(cd.id) > FLOOD_LIMIT) continue;

                // seq tracking
                int cseq = pkt.getSequence();
                if (cseq <= cd.lastSeqFromClient) {
                    // duplicate/old; optionally drop
                } else {
                    cd.lastSeqFromClient = cseq;
                }
                cd.lastHeardAtMs = now;

                if (handler != null) handler.onReceive(cd, pkt);
            } catch (SocketTimeoutException ignore) {
                // check timeouts periodically
                sweepTimeouts();
            } catch (SocketException se) {
                break;
            } catch (IOException ioe) {
                // ignore malformed packet
            }
        }
    }

    /** Send a payload to a specific client. */
    public void sendTo(ClientData cd, byte[] appPayload, short payloadType, boolean reliable, short flagsExtra) {
        if (cd == null || socket == null) return;
        int now = (int) (System.currentTimeMillis() & 0x7fffffff);
        int seqOut = nextSeqOut.merge(cd.id, 1, Integer::sum);

        Packet pkt = new Packet(cd.id, appPayload);
        short flags = (short) (flagsExtra | (reliable ? 4 : 0));
        pkt.setHeader(seqOut, cd.lastSeqFromClient, now, flags, payloadType);

        byte[] bytes = pkt.toBytes();
        DatagramPacket dp = new DatagramPacket(bytes, bytes.length, cd.address, cd.port);
        try { socket.send(dp); } catch (IOException ignored) {}
        cd.lastAckSent = seqOut;
    }

    /** Broadcast to all known clients. */
    public void broadcast(byte[] appPayload, short payloadType, boolean reliable, short flagsExtra) {
        for (ClientData cd : clients.values()) {
            sendTo(cd, appPayload, payloadType, reliable, flagsExtra);
        }
    }

    /** Remove quiet clients (callable from the IO thread). */
    private void sweepTimeouts() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, ClientData>> it = clients.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ClientData> e = it.next();
            ClientData cd = e.getValue();
            if (now - cd.lastHeardAtMs > 10_000) { // 10s quiet -> drop
                it.remove();
                nextSeqOut.remove(cd.id);
                if (handler != null) handler.onClientTimeout(cd);
            }
        }
    }
}