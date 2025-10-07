package Engine.Networking;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * A client class to join a server.
 */
public class Client {
    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    private boolean running;

    private byte[] buf;

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
    }

    /**
     * Testing message to send to server.
     * @param msg message to send
     * @return message received
     */
    public String sendEcho(String msg) {
        if (!running) {
            return "";
        }
        buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
        
        try {
            socket.send(packet);
        } catch (Exception e) {
            System.out.println("Failed to send package");
            return "";
        }
        
        packet = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(packet);
        } catch (Exception e) {
            System.out.println("Failed to receive package");
            return "";
        }
        
        String received = new String(packet.getData(), 0, packet.getLength());
        return received;
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
