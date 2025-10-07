package Engine;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Abstract GameObject that exsists in scenes.
 */
public abstract class GameObject {
    public float x = 0;
    public float y = 0;
    public float scale = 1.0f;
    float rotation = 0.0f;
    Scene scene;
    BufferedImage currentImage;

    public GameObject() {
        setup();
    }

    /**
     * Load all the required images for this component.
     * @param name Name of the image
     * @param path path to the image
     */
    public void loadImage(String name, String path) {
        BufferedImage image = Sprite.getImage(path);
        currentImage = image;
    }

    public void setRotation(float degrees) {
        rotation = degrees;
    }

    /**
     * Draws the object on game panel.
     * @param g2d the Graphics2D component of the game panel
     */
    protected void draw(Graphics2D g2d) {
        AffineTransform at = new AffineTransform();

        at.translate(x, y);
        at.rotate(Math.toRadians(rotation));
        at.scale(scale, scale);
        at.translate(-currentImage.getWidth() / 2, -currentImage.getHeight() / 2);

        g2d.drawImage(currentImage, at, null);
    }

    protected void setup() {
        return;
    }

    protected void onLoad() {
        return;
    }

    protected void onDestroy() {
        return;
    }

    protected void update(float deltaTime) {
        return;
    }

    /**
     * Unsure for now, but would later use it to send objects over the network.
     * @return packed bytes
     */
    byte[] pack() {
        //id, x, y, size, frameId
        return new byte[0];
    }

    void unpack(byte[] data) {
        
    }
}

