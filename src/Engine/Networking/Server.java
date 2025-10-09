package Engine.Networking;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.UUID;

import Engine.GameObject;

/**
 * A server class to send and receive data.
 */
public class Server extends Thread {
    private DatagramSocket socket;
    private int port;
    private boolean running;
    private byte[] buf = new byte[8192];
    private byte[] sendingBuf = new byte[8192];
    public ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
    ArrayList<ClientData> clients = new ArrayList<ClientData>();
    private final UUID serverId = new UUID(0, 0);

    /**
     * Set a port and start server.
     * @param port port
     * @throws Exception throws an exception if server cannot be opened
     */
    public void startServer(int port) throws Exception {
        socket = new DatagramSocket(port);
        this.port = port;
        running = true;
        start();
    }

    public void sendServerObjects(ArrayList<GameObject> serverObjects) {
        if (!running) {
            return;
        }

        Packet dataPacket = new Packet(serverId, serverObjects);
        sendingBuf = dataPacket.getBytes();

        ArrayList<ClientData> clientData = new ArrayList<ClientData>();

        for (ClientData client : clients) {
            clientData.add(client);
        }

        for (ClientData client : clientData) {
            DatagramPacket packet = new DatagramPacket(sendingBuf, sendingBuf.length, client.getAddress(), client.getPort());
            try {
                socket.send(packet);
            } catch (Exception e) {
                System.out.println("Failed to send package");
                return;
            }
        }
    }

    @Override
    public void run() {
        while (running) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (Exception e) {
                System.out.println("Failed to receive packet");
                continue;
            }
            
            Packet dataPacket = new Packet(packet.getData());

            ClientData currentClient = new ClientData(packet.getAddress(), packet.getPort(), dataPacket.id);

            boolean newClient = true;
            for (ClientData client : clients) {
                if (client.equals(currentClient)) {
                    newClient = false;
                }
            }

            if (newClient) {
                clients.add(currentClient);
            }

            gameObjects = dataPacket.getGameObjects();
            
        }
        socket.close();
    }
}
