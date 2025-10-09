package Engine.Networking;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

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

    private byte[] sendingBuf = new byte[8192];
    private byte[] receivingBuf = new byte[8192];

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
     * Testing message to send to server.
     * @param msg message to send
     */
    public void sendGameObjects(ArrayList<GameObject> gameObjects) {
        if (!running) {
            return;
        }
        Packet dataPacket = new Packet(Packet.PacketType.Data, gameObjects);
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
            
            try {
                sleep(100);
            } catch (Exception e) {
                continue;
            }
            
        }

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
