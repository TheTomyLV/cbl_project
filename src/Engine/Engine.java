package Engine;

import Engine.Inputs.Input;
import Engine.Networking.Client;
import Engine.Networking.Network;
import Engine.Networking.NetworkHandleRegister;
import Engine.Networking.Server;
import Engine.Sound.AudioPlayer;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFrame;

public class Engine {
    private static Server server;
    private static Client client;
    private static Scene currentScene;
    private static JFrame jFrame;
    private static boolean running;
    private static Duration deltaTime = Duration.ZERO;
    private static Duration tick = Duration.ZERO;
    private static Instant beginTime = Instant.now();
    private static Input input = new Input();

    public static void start() {
        jFrame = new JFrame();
        jFrame.setSize(500, 600);
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setTitle("Game");
        jFrame.addKeyListener(input);
        
        new Camera();
        running = true;
    }

    public static Scene getCurrentScene() {
        return Engine.currentScene;
    }

    public static boolean isRunning() {
        return running;
    }

    public static float getDeltaTIme() {
        return Engine.deltaTime.toNanos() / 1_000_000_000f;
    }

    private static void ClientNetworkUpdate() {
        if (Engine.client != null) {
            Engine.client.sendGameObjects(getCurrentScene().getGameObjects());

            // receiving server objects
            ArrayList<GameObject> serverObjects = Engine.client.gameObjects;
            ArrayList<GameObject> localObjects = getCurrentScene().getServerObject();
            for (GameObject gameObject : serverObjects) {
                boolean found = false;
                for (GameObject clientObject : localObjects) {
                    if (gameObject.equals(clientObject)) {
                        clientObject.updateValues(gameObject.position, gameObject.scale, gameObject.rotation);
                        found = true;
                        break;
                    }
                }
                if (found) {
                    continue;
                }

                if (!getCurrentScene().getGameObjects().contains(gameObject)) {
                    Engine.getCurrentScene().addServerObject(gameObject);
                }
            }

            for (int i = 0; i < localObjects.size(); i++) {
                if (!serverObjects.contains(localObjects.get(i))) {
                    localObjects.remove(i);
                }
            }
        }
    }

    public static void update() {
        if (tick.toMillis() >= 16) {
            Engine.ClientNetworkUpdate();
            tick = Duration.ZERO;
        }
        
        if (Engine.server != null) {
            Engine.server.update(Engine.getDeltaTIme());
        }

        currentScene.update(Engine.getDeltaTIme());
        

        deltaTime = Duration.between(beginTime, Instant.now());
        tick = tick.plus(deltaTime);
        beginTime = Instant.now();
    }

    /**
     * Replace the current scene.
     * @param scene scene
     */
    public static void changeScene(Scene scene) {
        if (getCurrentScene() != null) {
            Engine.getCurrentScene().removeMouseListener(input);
            Engine.getCurrentScene().removeMouseMotionListener(input);
            Engine.jFrame.remove(Engine.getCurrentScene());
        }
        
        Engine.currentScene = scene;
        
        Engine.jFrame.add(scene);
        scene.addMouseMotionListener(input);
        scene.addMouseListener(input);
        Engine.jFrame.validate();
        Engine.jFrame.requestFocus();
    }

    /**
     * Setup server for game. The host also joins as a client.
     * @param port port
     * @return true if successful server is made
     */
    public static boolean runServer(int port) {
        Engine.server = new Server();
        try {
            Engine.server.startServer(port);
        } catch (Exception e) {
            Engine.server = null;
            return false;
        }

        return runClient("localhost", port);
    }

    /**
     * Joins a hosted game.
     * @param host host ip
     * @param port port
     * @return true if joined the host
     */
    public static boolean runClient(String host, int port) {
        Engine.client = new Client();
        try {
            Engine.client.connect(host, port);
        } catch (Exception e) {
            Engine.client = null;
            return false;
        }
        return true;
    }

    /**
     * Destroy given GameObject in currently opened scene.
     */
    public static void destroy(GameObject gameObjecet) {
        getCurrentScene().destroyObject(gameObjecet);
    }

    public static void addObject(GameObject gameObject) {
        getCurrentScene().addObject(gameObject);
    }


}
