package Engine.Networking;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import Engine.GameObject;
import Engine.Engine;
import Engine.Networking.Packet.PacketType;

/**
 * A server class to send and receive data.
 */
public class Server extends Thread {
    private DatagramSocket socket;
    private int port;
    private boolean running;
    private byte[] buf = new byte[8192];
    public ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();

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
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            Packet dataPacket = new Packet(packet.getData());

            gameObjects = dataPacket.getGameObjects();

            // try {
            //     System.out.println(send);
            //     dataPacket = new Packet(PacketType.Data, send.getBytes());
            //     buf = dataPacket.getBytes();

            //     packet = new DatagramPacket(buf, buf.length, address, port);
            //     socket.send(packet);
            // } catch (Exception e) {
            //     System.out.println("Failed to send packet");
            //     continue;
            // }
            
        }
        socket.close();
    }
}
