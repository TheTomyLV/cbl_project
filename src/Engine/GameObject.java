package Engine;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;

/**
 * Abstract GameObject that exsists in scenes.
 */
public abstract class GameObject {
    public float x = 0;
    public float y = 0;
    public float scale = 1.0f;
    float rotation = 0.0f;
    Scene scene;
    HashMap<String, BufferedImage> images = new HashMap<String, BufferedImage>();
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
        try {
            BufferedImage image = ImageIO.read(new File(path));
            if (currentImage == null) {
                currentImage = image;
            }
            images.put(name, image);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    protected abstract void setup();

    protected abstract void onLoad();

    protected abstract void onDestroy();

    protected abstract void update(float deltaTime);

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

