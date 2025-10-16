package Engine.Networking;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;

import Engine.Engine;
import Engine.GameObject;

/**
 * A server class to send and receive data.
 */
public class Server extends Thread {
    private DatagramSocket socket;
    private int port;
    private boolean running;
    private byte[] buf = new byte[8192];
    private byte[] sendingBuf;
    private static final int PLAYER_COUNT = 4;
    private HashMap<ClientData, ArrayList<GameObject>> gameObjects = new HashMap<>();
    private HashSet<ClientData> clients = new HashSet<>();
    private final UUID serverId = new UUID(0, 0);

    public HashMap<ClientData, ArrayList<GameObject>> getGameObjects() {
        return gameObjects;
    }

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

    public void sendServerObjects(HashMap<ClientData, ArrayList<GameObject>> serverObjects) {
        if (!running) {
            return;
        }
        Packet dataPacket = new Packet(serverId, serverObjects);
        sendingBuf = dataPacket.getBytes();

        for (Iterator<ClientData> it = clients.iterator(); it.hasNext();) {
            ClientData client = it.next();
            DatagramPacket packet = new DatagramPacket(sendingBuf, sendingBuf.length, client.getAddress(), client.getPort());
            try {
                socket.send(packet);
            } catch (Exception e) {
                System.out.println("Failed to send package");
                return;
            }
        }
    }

    private void updateGameObjects(ArrayList<GameObject> newGameObjects, ClientData clientData) {
        ArrayList<GameObject> clientGameObjects = gameObjects.get(clientData);
        for (Iterator<GameObject> it = newGameObjects.iterator(); it.hasNext();) {
            GameObject newClientObject = it.next();
            boolean found = false;

            for (int i = 0; i < clientGameObjects.size(); i++) {
                GameObject serverObject = clientGameObjects.get(i);
                if (newClientObject.equals(serverObject)) {
                    serverObject.position = newClientObject.position;
                    serverObject.scale = newClientObject.scale;
                    serverObject.rotation = newClientObject.rotation;
                    found = true;
                    break;
                }
            }
            if (found) {
                continue;
            }
            clientGameObjects.add(newClientObject);
        }

        for (int i = 0; i < clientGameObjects.size(); i++) {
            GameObject serverObject = clientGameObjects.get(i);
            boolean found = newGameObjects.contains(serverObject);
            
            if (!found) {
                System.out.println(clientGameObjects.size());
                clientGameObjects.remove(serverObject);
                System.out.println(clientGameObjects.size());
            }
        }
    }

    private void addNewClient(ClientData client) {
        if (clients.size() >= PLAYER_COUNT) {
            return;
        }

        if (!clients.contains(client)) {
            clients.add(client);
            gameObjects.put(client, new ArrayList<>());
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

            addNewClient(currentClient);
            
            if (clients.contains(currentClient)) {
                updateGameObjects(dataPacket.getGameObjects(), currentClient);
            }
        }
        socket.close();
    }
}
