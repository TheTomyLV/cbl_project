package Engine;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.*;

/**
 * A scene class that can be extended to act as the main game panel.
 */
public abstract class Scene extends JPanel {
    ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();

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
    }

    void update() {
        for (GameObject gameObject : gameObjects) {
            gameObject.update(Engine.getDeltaTIme());
        }
        repaint();
    }

    public abstract void setupScene();

    public void addObject(GameObject gameObject) {
        gameObjects.add(gameObject);
    }

}
