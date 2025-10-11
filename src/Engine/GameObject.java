package Engine;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

/**
 * Abstract GameObject that exsists in scenes.
 */
public class GameObject implements Serializable {
    private static final float LERP_SPEED = 10.0f;

    private final UUID id;
    public float x = 0;
    public float y = 0;
    public float scale = 1.0f;
    float rotation = 0.0f;
    Scene scene;
    BufferedImage currentImage;

    private float targetX;
    private float targetY;

    public GameObject() {
        this.id = UUID.randomUUID();
        setup();
    }

    GameObject(UUID id, float x, float y, float scale, float rotation) {
        loadImage("", "src\\Assets\\Player.png");
        this.id = id;
        this.x = x;
        targetX = x;
        this.y = y;
        targetY = y;
        this.scale = scale;
        this.rotation = rotation;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof GameObject)) {
            return false;
        }
        GameObject that = (GameObject) obj;
        return id.equals(that.id);
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

        at.translate(x - Camera.currentCamera.x + Engine.getCurrentScene().getWidth() / 2, y - Camera.currentCamera.y + Engine.getCurrentScene().getHeight() / 2);
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
        if (getClass() != GameObject.class) {
            return;
        }
        float t = (LERP_SPEED * deltaTime);

        if (t > 1.0f) {
            t = 1.0f;
        }

        x += (targetX - x) * t;
        y += (targetY - y) * t;
    }

    protected void updateValues(float x, float y, float scale, float rotation) {
        targetX = x;
        targetY = y;
        this.scale = scale;
        this.rotation = rotation;
    }

    /**
     * Unsure for now, but would later use it to send objects over the network.
     * @return packed bytes
     */
    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeLong(id.getMostSignificantBits());
        dos.writeLong(id.getLeastSignificantBits());
        dos.writeFloat(x);
        dos.writeFloat(y);
        dos.writeFloat(scale);
        dos.writeFloat(rotation);

        dos.flush();
        return baos.toByteArray();
    }

    public static GameObject fromBytes(byte[] data) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
        long most = dis.readLong();
        long least = dis.readLong();
        UUID id = new UUID(most, least);
        float x = dis.readFloat();
        float y = dis.readFloat();
        float scale = dis.readFloat();
        float rotation = dis.readFloat();
        return new GameObject(id, x, y, scale, rotation);
    }
}

