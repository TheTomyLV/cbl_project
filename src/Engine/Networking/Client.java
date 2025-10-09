package Engine.Networking;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import Engine.GameObject;
import Engine.Engine;

/**
 * A client class to join a server.
 */
public class Client extends Thread {
    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    private boolean running;
    private final UUID clientId;

    public ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();

    private byte[] sendingBuf = new byte[8192];
    private byte[] receivingBuf = new byte[8192];

    public Client() {
        clientId = UUID.randomUUID();
    }

    public UUID getClientId() {
        return clientId;
    }

    /**
     * Connect to a server.
     * @param host server address
     * @param port server port
     * @throws Exception failed to connect to server
     */
    public void connect(String host, int port) throws Exception {
        socket = new DatagramSocket();
        address = InetAddress.getByName("localhost");
        this.port = port;
        running = true;
        start();
    }

    /**
     * Send all local gameObject data to the server.
     * @param gameObjects Array of local game objects
     */
    public void sendGameObjects(ArrayList<GameObject> gameObjects) {
        if (!running) {
            return;
        }
        Packet dataPacket = new Packet(clientId, gameObjects);
        sendingBuf = dataPacket.getBytes();
        DatagramPacket packet = new DatagramPacket(sendingBuf, sendingBuf.length, address, port);
        
        try {
            socket.send(packet);
        } catch (Exception e) {
            System.out.println("Failed to send package");
            return;
        }
    }

    @Override
    public void run() {
        while (running) {
            DatagramPacket packet = new DatagramPacket(receivingBuf, receivingBuf.length);
            try {
                socket.receive(packet);
            } catch (Exception e) {
                System.out.println("Failed to receive packet");
                continue;
            }
            Packet dataPacket = new Packet(packet.getData());

            gameObjects = dataPacket.getGameObjects();
            
        }
        socket.close();

    }

    /**
     * Closes an opened connection.
     */
    public void close() {
        socket.close();
        running = false;
        port = 0;
        address = null;
    }
}
