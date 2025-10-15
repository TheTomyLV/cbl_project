package Engine;

import Engine.Inputs.Input;
import Engine.Networking.Client;
import Engine.Networking.ClientData;
import Engine.Networking.Server;
import Engine.Networking.Packet;

import javax.swing.JFrame;
import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

public class Engine {
    Server server;
    Client client;
    Scene currentScene;
    JFrame jFrame;
    boolean running;
    Duration deltaTime = Duration.ZERO;
    Instant beginTime = Instant.now();
    Duration tick = Duration.ZERO;
    static Engine engine;

    // snapshot payload type
    private static final short PT_SNAPSHOT = 1;

    // client-side buffer of server snapshot
    private final AtomicReference<ArrayList<GameObject>> receivedServerObjects = new AtomicReference<>(null);

    public Engine() { engine = this; }

    public void setup() {
        jFrame = new JFrame();
        jFrame.setSize(500, 600);
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setTitle("Game");
        Input input = new Input();
        jFrame.addKeyListener(input);
        jFrame.addMouseListener(input);
        jFrame.addMouseMotionListener(input);
        new Camera();
        running = true;
    }

    public static Scene getCurrentScene() { return engine.currentScene; }
    protected static Engine getEngine() { return engine; }
    public boolean isRunning() { return running; }
    public static float getDeltaTIme() { return engine.deltaTime.toNanos() / 1_000_000_000f; }

    // --- networking glue for the new UDP layer ---
    private static byte[] serializeObjects(ArrayList<GameObject> list) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8_192);
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeInt(list.size());
            for (GameObject go : list) oos.writeObject(go);
        }
        return bos.toByteArray();
    }

    @SuppressWarnings("unchecked")
    private static ArrayList<GameObject> deserializeObjects(byte[] bytes) throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            int n = ois.readInt();
            ArrayList<GameObject> out = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                Object o = ois.readObject();
                out.add((GameObject) o);
            }
            return out;
        } catch (ClassNotFoundException e) {
            throw new IOException("class not found: " + e.getMessage(), e);
        }
    }

    private void applyServerSnapshot(ArrayList<GameObject> serverObjects) {
        if (serverObjects == null) return;
        ArrayList<GameObject> localServerObjects = getCurrentScene().getServerObject();

        // update existing
        for (GameObject incoming : serverObjects) {
            boolean found = false;
            for (GameObject local : localServerObjects) {
                if (incoming.equals(local)) {
                    local.updateValues(incoming.position, incoming.scale, incoming.rotation);
                    found = true;
                    break;
                }
            }
            if (!found) {
                if (!getCurrentScene().getGameObjects().contains(incoming)) {
                    getCurrentScene().addServerObject(incoming);
                }
            }
        }
        // optional: remove stale
        Iterator<GameObject> it = localServerObjects.iterator();
        while (it.hasNext()) {
            GameObject local = it.next();
            if (!serverObjects.contains(local)) {
                // keep or remove depending on your game rules
                // it.remove();
            }
        }
    }

    private void networkUpdate() {
        try {
            // client: send local objects to server
            if (client != null) {
                byte[] payload = serializeObjects(getCurrentScene().getGameObjects());
                client.send(payload, PT_SNAPSHOT, false, (short) 1);
                ArrayList<GameObject> snapshot = receivedServerObjects.getAndSet(null);
                if (snapshot != null) applyServerSnapshot(snapshot);
            }
            // server: broadcast authoritative snapshot to all clients
            if (server != null) {
                byte[] payload = serializeObjects(gameObjects());

                server.broadcast(payload, PT_SNAPSHOT, false, (short) 1);
            }
        } catch (IOException ignored) {}
    }

    public void update() {
        if (tick.toMillis() >= 16) {
            networkUpdate();
            tick = Duration.ZERO;
        }
        currentScene.update();
        deltaTime = Duration.between(beginTime, Instant.now());
        tick = tick.plus(deltaTime);
        beginTime = Instant.now();
    }

    /** Replace the current scene. */
    public static void changeScene(Scene scene) {
        if (getCurrentScene() != null) getEngine().jFrame.remove(Engine.getCurrentScene());
        getEngine().currentScene = scene;
        getEngine().jFrame.add(scene);
        getEngine().jFrame.validate();
        getEngine().jFrame.requestFocus();
    }

    public static boolean runServer(int port) {
        getEngine().server = new Server(port, new Server.Handler() {
            @Override public void onReceive(ClientData from, Packet pkt) {
                if (pkt.getPayloadType() == PT_SNAPSHOT) {
                    // TODO: handle client->server messages if you need them
                }
            }
            @Override public void onClientTimeout(ClientData cd) { /* optional */ }
        });
        try { getEngine().server.start(); }
        catch (Exception e) { getEngine().server = null; return false; }

        return runClient("localhost", port);
    }


    /** Join a host. */
    public static boolean runClient(String host, int port) {
        getEngine().client = new Client(new Client.Listener() {
            @Override public void onPacket(Packet pkt) {
                if (pkt.getPayloadType() == PT_SNAPSHOT) {
                    try {
                        ArrayList<GameObject> list = deserializeObjects(pkt.getPayload());
                        engine.receivedServerObjects.set(list);
                    } catch (IOException ignored) {}
                }
            }
            @Override public void onDisconnected(String reason) { /* optional UI */ }
        });
        try { getEngine().client.connect(host, port); }
        catch (Exception e) { getEngine().client = null; return false; }
        return true;
    }

    /** Destroy given GameObject in current scene. */
    public static void destroy(GameObject gameObjecet) {
        getCurrentScene().destroyObject(gameObjecet);
    }

    public static void addObject(GameObject gameObject) {
        getCurrentScene().addObject(gameObject);
    }

    // server authoritative list for broadcast (adjust to your design)
    private ArrayList<GameObject> gameObjects() {
        // If host is authoritative, use currentScene.getGameObjects()
        return getCurrentScene() != null ? getCurrentScene().getGameObjects() : new ArrayList<>();
    }
}
