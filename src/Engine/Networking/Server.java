package Engine.Networking;

import Engine.GameObject;
import Engine.Scene;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;

/**
 * A server class to send and receive data.
 */
public class Server extends Thread {
    private DatagramSocket socket;
    private int port;
    private boolean running;
    private byte[] buf = new byte[8192];
    private byte[] sendingBuf;
    private HashMap<ClientData, ArrayList<GameObject>> allObjects = new HashMap<>();
    private HashSet<ClientData> clients = new HashSet<>();
    private HashMap<ClientData, ArrayList<Integer>> ackMessages = new HashMap<>();
    private final UUID serverId = new UUID(0, 0);
    private float tick = 0f;
    private Scene currentScene;

    private static Server server;
    private static final int PLAYER_COUNT = 4;

    public Server() {
        Server.server = this;
    }

    public HashMap<ClientData, ArrayList<GameObject>> getAllObjects() {
        return allObjects;
    }

    /**
     * Set a port and start server.
     * @param port port
     * @throws Exception throws an exception if server cannot be opened
     */
    public void startServer(int port) throws Exception {
        allObjects.put(null, new ArrayList<>());
        socket = new DatagramSocket(port);
        this.port = port;
        running = true;
        start();
    }

    /**
     * An update method that updates all server objects.
     * @param deltaTime engine delta time
     */
    public void update(float deltaTime) {
        tick += deltaTime;
        if (tick >= 0.0016) {
            sendServerObjects(allObjects);
            tick = 0;
        }
        ArrayList<GameObject> gameObjects = Server.server.allObjects.get(null);

        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject gameObject = gameObjects.get(i);
            gameObject.update(deltaTime);
        }
    }

    private void executeMessages(ArrayList<NetMessage> messages, ClientData client) {
        ArrayList<Integer> clientAck = ackMessages.get(client);
        for (int i = 0; i < messages.size(); i++) {
            NetMessage message = messages.get(i);
            if (!clientAck.contains(message.getId())) {
                Network.onMessageReceived(message);
                clientAck.add(message.getId());
            }
        }
        cleanupAck(clientAck, messages);
    }

    private void cleanupAck(ArrayList<Integer> clientAck, ArrayList<NetMessage> messages) {
        for (int i = 0; i < clientAck.size(); i++) {
            int ackId = clientAck.get(i);
            boolean found = false;
            for (int j = 0; j < messages.size(); j++) {
                if (messages.get(i).getId() == ackId) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                clientAck.remove(i);
                i--;
            }
        }
    }

    /**
     * Adds a new server object.
     * @param gameObject gameObject
     */
    public static void addObject(GameObject gameObject) {
        if (Server.server == null) {
            return;
        }
        ArrayList<GameObject> gameObjects = Server.server.allObjects.get(null);
        if (!gameObjects.contains(gameObject)) {
            gameObjects.add(gameObject);
        }
    }

    /**
     * Removes a server object.
     * @param gameObject gameObject
     */
    public static void removeObject(GameObject gameObject) {
        if (Server.server == null) {
            return;
        }
        ArrayList<GameObject> gameObjects = Server.server.allObjects.get(null);
        if (gameObjects.contains(gameObject)) {
            gameObjects.remove(gameObject);
        }
    }

    public static ArrayList<GameObject> getObjects() {
        return Server.server.allObjects.get(null);
    }

    public static void changeScene(Scene scene) {
        Server.server.currentScene = scene;
    }

    private void sendServerObjects(HashMap<ClientData, ArrayList<GameObject>> serverObjects) {
        if (!running) {
            return;
        }
        
        

        for (Iterator<ClientData> it = clients.iterator(); it.hasNext();) {
            ClientData client = it.next();
            Packet dataPacket = new Packet(serverId, serverObjects, new ArrayList<>(), ackMessages.get(client));
            sendingBuf = dataPacket.getBytes();
            
            DatagramPacket packet = new DatagramPacket(sendingBuf, 
                sendingBuf.length, client.getAddress(), client.getPort());

            try {
                socket.send(packet);
            } catch (Exception e) {
                System.out.println("Failed to send package");
                return;
            }
        }
    }

    /**
     * Updates client gameObject states.
     * @param newGameObjects new client gameObjects
     * @param clientData client that the objects belong to
     */
    private void updateGameObjects(ArrayList<GameObject> newGameObjects, ClientData clientData) {
        ArrayList<GameObject> clientGameObjects = allObjects.get(clientData);
        for (Iterator<GameObject> it = newGameObjects.iterator(); it.hasNext();) {
            GameObject newClientObject = it.next();
            boolean found = false;

            for (int i = 0; i < clientGameObjects.size(); i++) {
                GameObject serverObject = clientGameObjects.get(i);
                if (newClientObject.equals(serverObject)) {
                    serverObject.position = newClientObject.position;
                    serverObject.scale = newClientObject.scale;
                    serverObject.rotation = newClientObject.rotation;
                    if (newClientObject.currentSprite != null) {
                        serverObject.currentSprite = newClientObject.currentSprite;
                    }
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
                clientGameObjects.remove(serverObject);
            }
        }
    }

    private void addNewClient(ClientData client) {
        if (clients.size() >= PLAYER_COUNT) {
            return;
        }

        if (!clients.contains(client)) {
            clients.add(client);
            allObjects.put(client, new ArrayList<>());
            ackMessages.put(client, new ArrayList<>());
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

            ClientData currentClient = new ClientData(packet.getAddress(), 
                packet.getPort(), dataPacket.id);

            addNewClient(currentClient);
            
            if (clients.contains(currentClient)) {
                updateGameObjects(dataPacket.getGameObjects(), currentClient);
                executeMessages(dataPacket.getMessages(), currentClient);
            }
        }
        socket.close();
    }
}
