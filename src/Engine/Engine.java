package Engine;

import Engine.Networking.Client;
import Engine.Networking.Server;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JFrame;

public class Engine {
    Server server;
    Client client;
    Scene currentScene;
    JFrame jFrame;
    boolean running;
    Duration deltaTime = Duration.ZERO;
    Instant beginTime = Instant.now();
    static Engine engine;

    public Engine() {
        engine = this;
    }

    public void setup() {
        jFrame = new JFrame();
        jFrame.setSize(500, 600);
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setTitle("Game");
        jFrame.addKeyListener(new Input());
        running = true;
    }

    public static Scene getCurrentScene() {
        return engine.currentScene;
    }

    protected static Engine getEngine() {
        return engine;
    }

    public boolean isRunning() {
        return running;
    }

    public static float getDeltaTIme() {
        return engine.deltaTime.toMillis();
    }

    public void update() {
        if (getEngine().server == null && getEngine().client != null) {
            getEngine().client.sendGameObjects(getCurrentScene().getGameObjects());
        }
        if (getEngine().server != null) {
            ArrayList<GameObject> clientGameObjects = getEngine().server.gameObjects;
            ArrayList<GameObject> sceneObjects = getCurrentScene().getGameObjects();
            for (GameObject gameObject : clientGameObjects) {
                boolean found = false;
                for (GameObject sceneObject : sceneObjects) {
                    if (gameObject.equals(sceneObject)) {
                        sceneObject.x = gameObject.x;
                        sceneObject.y = gameObject.y;
                        found = true;
                        break;
                    }
                }
                if (found) {
                    continue;
                }
                getEngine().addObject(gameObject);
            }
        }
        
        currentScene.update();

        deltaTime = Duration.between(beginTime, Instant.now());
        beginTime = Instant.now();
    }

    /**
     * Replace the current scene.
     * @param scene scene
     */
    public static void changeScene(Scene scene) {
        if (getCurrentScene() != null) {
            getEngine().jFrame.remove(Engine.getCurrentScene());
        }
        
        getEngine().currentScene = scene;
        
        getEngine().jFrame.add(scene);
        getEngine().jFrame.validate();
        getEngine().jFrame.requestFocus();
    }

    /**
     * Setup server for game. The host also joins as a client.
     * @param port port
     * @return true if successful server is made
     */
    public static boolean runServer(int port) {
        getEngine().server = new Server();
        try {
            getEngine().server.startServer(port);
        } catch (Exception e) {
            getEngine().server = null;
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
        getEngine().client = new Client();
        try {
            getEngine().client.connect(host, port);
        } catch (Exception e) {
            getEngine().client = null;
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
