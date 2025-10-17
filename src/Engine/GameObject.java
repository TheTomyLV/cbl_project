package Engine;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

/**
 * Abstract GameObject that exsists in scenes.
 */
public class GameObject implements Serializable {
    private static final float LERP_SPEED = 10.0f;

    private final UUID id;
    public Vector2 position = new Vector2(0, 0);
    public Vector2 scale = new Vector2(1, 1);
    public float rotation = 0.0f;
    public Sprite currentSprite;
    private BufferedImage currentImage;


    // For smooth server object movement
    private Vector2 targetPos;

    public GameObject() {
        this.id = UUID.randomUUID();
        setup();
    }

    GameObject(UUID id, Vector2 position, Vector2 scale, float rotation, int imageIndex) {
        if (imageIndex != -1) {
            setSprite(imageIndex);
        }
        this.id = id;
        this.position = position;
        this.scale = scale;
        this.scale = scale;
        this.rotation = rotation;
        targetPos = position;
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
     * @param path path to the image
     */
    public void setSprite(String name) {
        currentSprite = Sprite.getSprite(name);
        currentImage = currentSprite.getImage();
    }

    private void setSprite(int index) {
        currentSprite = Sprite.getSpriteFromIndex(index);
        currentImage = currentSprite.getImage();
    }

    public void setRotation(float degrees) {
        rotation = degrees;
    }

    /**
     * Draws the object on game panel.
     * @param g2d the Graphics2D component of the game panel
     */
    protected void draw(Graphics2D g2d) {
        if (currentImage == null) {
            return;
        }
        AffineTransform at = new AffineTransform();

        Vector2 panelDimensions = new Vector2(Engine.getCurrentScene().getWidth() / 2, Engine.getCurrentScene().getHeight() / 2);
        Vector2 panelPos = position.subtract(Camera.currentCamera.position);
        panelPos = panelPos.add(panelDimensions);
        at.translate(panelPos.x, panelPos.y);
        at.rotate(Math.toRadians(rotation));
        at.scale(scale.x, scale.y);
        at.translate(-currentImage.getWidth() * currentSprite.pivot.x, -currentImage.getHeight() * currentSprite.pivot.y);

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

    public void update(float deltaTime) {
        if (getClass() != GameObject.class) {
            return;
        }
        float t = (LERP_SPEED * deltaTime);

        if (t > 1.0f) {
            t = 1.0f;
        }
        position = position.add(targetPos.subtract(position).multiply(t));
    }

    public void updateValues(Vector2 position, Vector2 scale, float rotation) {
        targetPos = position;
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
        dos.writeFloat(position.x);
        dos.writeFloat(position.y);
        dos.writeFloat(scale.x);
        dos.writeFloat(scale.y);
        dos.writeFloat(rotation);
        if (currentSprite == null) {
            dos.writeInt(-1);
        } else {
            dos.writeInt(currentSprite.getIndex());
        }
        dos.flush();
        return baos.toByteArray();
    }

    public static GameObject fromBytes(byte[] data) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
        long most = dis.readLong();
        long least = dis.readLong();
        float x = dis.readFloat();
        float y = dis.readFloat();
        float scaleX = dis.readFloat();
        float scaleY = dis.readFloat();
        float rotation = dis.readFloat();
        int imageIndex = dis.readInt();
        UUID id = new UUID(most, least);
        Vector2 position = new Vector2(x, y);
        Vector2 scale = new Vector2(scaleX, scaleY);
        return new GameObject(id, position, scale, rotation, imageIndex);
    }
}

