package Engine;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.sound.sampled.Clip;
import javax.swing.*;

/**
 * A scene class that can be extended to act as the main game panel.
 */
public abstract class Scene extends JPanel {
    ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
    ArrayList<GameObject> serverObjects = new ArrayList<GameObject>();
    ArrayList<GameObject> toAddObject = new ArrayList<>();
    ArrayList<GameObject> toRemoveObject = new ArrayList<>();


    /**
     * Set scene layout and call setupScene.
     */
    public Scene() {
        this.setLayout(null);
        setupScene();
    }

    @Override
    protected synchronized void paintComponent(Graphics g) {
        super.paintComponent(g);

        draw(g);
    }

    private void draw(Graphics g) {
        for (int i = 0; i < gameObjects.size(); i++) {
            gameObjects.get(i).draw((Graphics2D) g);
        }

        for (int i = 0; i < serverObjects.size(); i++) {
            serverObjects.get(i).draw((Graphics2D) g);
        }
    }

    /**
     * Internal method to call update function for all scene gameObjects.
     */
    void update(float deltaTime) {

        for (Iterator<GameObject> it = gameObjects.iterator(); it.hasNext();) {
            GameObject gameObject = it.next();
            gameObject.update(deltaTime);
        }

        for (Iterator<GameObject> it = serverObjects.iterator(); it.hasNext();) {
            GameObject gameObject = it.next();
            gameObject.update(deltaTime);
        }

        for (GameObject gameObject : toAddObject) {
            if (!gameObjects.contains(gameObject)) {
                gameObjects.add(gameObject);
            }
        }
        toAddObject.clear();

        for (GameObject gameObject : toRemoveObject) {
            if (gameObjects.contains(gameObject)) {
                gameObject.onDestroy();
                gameObjects.remove(gameObject);
            }
        }
        toRemoveObject.clear();

        Graphics2D g2d = (Graphics2D) getGraphics();
        if (g2d == null) {
            return;
        }
        repaint();
    }

    public abstract void setupScene();

    public void addObject(GameObject gameObject) {
        toAddObject.add(gameObject);
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
        toRemoveObject.add(gameObject);
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
