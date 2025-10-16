package GameObjects;


import Engine.Camera;
import Engine.Engine;
import Engine.GameObject;
import Engine.Inputs.Input;
import Engine.Sound.AudioClip;
import Engine.Sound.AudioPlayer;
import Engine.Vector2;
import java.awt.event.KeyEvent;

public class Player extends GameObject {

    double time = 0;
    float speed = 5.0f;
    Vector2 velocity = new Vector2(0f, 0f);
    AudioClip shooting = new AudioClip("src\\Assets\\audio\\shoot.wav");

    public Player(Vector2 position) {
        this.position = position;
    }

    @Override
    protected void setup() {
        loadImage("src\\Assets\\art\\Player.png");
        //setRotation(45);
        scale = new Vector2(0.1f, 0.1f);
    }

    @Override
    public void update(float deltaTime) {
        time += deltaTime;

        if (Input.isKeyPressed(KeyEvent.VK_W)) {
            velocity.y -= deltaTime * speed;
        }
        if (Input.isKeyPressed(KeyEvent.VK_S)) {
            velocity.y += deltaTime * speed;
        }
        if (Input.isKeyPressed(KeyEvent.VK_A)) {
            velocity.x -= deltaTime * speed;
        }
        if (Input.isKeyPressed(KeyEvent.VK_D)) {
            velocity.x += deltaTime * speed;
        }


        position = position.add(velocity);
        velocity = velocity.multiply(0.99f); // Hacky for now
        rotation = Input.mouse.getWorldPosition().subtract(position).getRotation();
        rotation = (float) Math.toDegrees(rotation);

        if (Input.mouse.isClicked(0) && time >= 0.1) {
            AudioPlayer.playAudio(shooting, false);
            Engine.addObject(new Bullet(position, rotation));
            time = 0;
        }

        Camera.currentCamera.position = position;
    }
    
}
