package Engine;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.*;

/**
 * A scene class that can be extended to act as the main game panel.
 */
public abstract class Scene extends JPanel {
    ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
    ArrayList<GameObject> serverObjects = new ArrayList<GameObject>();

    /**
     * Set scene layout and call setupScene.
     */
    public Scene() {
        this.setLayout(null);
        setupScene();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (GameObject gameObject : gameObjects) {
            gameObject.draw((Graphics2D) g);
        }

        for (GameObject gameObject : serverObjects) {
            gameObject.draw((Graphics2D) g);
        }
    }

    /**
     * Internal method to call update function for all scene gameObjects.
     */
    void update() {
        for (GameObject gameObject : gameObjects) {
            gameObject.update(Engine.getDeltaTIme());
        }

        if (Engine.getEngine().server == null) {
            for (GameObject gameObject : serverObjects) {
                gameObject.update(Engine.getDeltaTIme());
            }
        }

        repaint();
    }

    public abstract void setupScene();

    public void addObject(GameObject gameObject) {
        if (gameObjects.contains(gameObject)) {
            return;
        }
        gameObjects.add(gameObject);
    }

    protected void addServerObject(GameObject gameObject) {
        if (serverObjects.contains(gameObject)) {
            return;
        }
        serverObjects.add(gameObject);
    }

    /**
     * Internal method for destroying scene gameObjects.
     * @param gameObject gameObject that is scene
     */
    void destroyObject(GameObject gameObject) {
        if (gameObjects.contains(gameObject)) {
            gameObject.onDestroy();
            gameObjects.remove(gameObject);
        }
    }

    protected void destroyServerObject(GameObject gameObject) {
        if (serverObjects.contains(gameObject)) {
            gameObject.onDestroy();
            serverObjects.remove(gameObject);
        }
    }

    public ArrayList<GameObject> getGameObjects() {
        return gameObjects;
    }

    protected ArrayList<GameObject> getServerObject() {
        return serverObjects;
    }

}
