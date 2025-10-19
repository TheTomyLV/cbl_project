package GameObjects;

import Engine.Camera;
import Engine.Engine;
import Engine.GameObject;
import Engine.Inputs.Input;
import Engine.Networking.NetEvent;
import Engine.Networking.Server;
import Engine.Sound.AudioClip;
import Engine.Sound.AudioPlayer;
import Engine.Vector2;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Player extends GameObject {

    double time = 0;
    float speed = 2.0f;
    Vector2 velocity = new Vector2(0f, 0f);
    AudioClip shooting = new AudioClip("src\\Assets\\audio\\shoot.wav");
    String[] animation = {"player_shoot1", "player_shoot2", "player_shoot3", "player"};
    boolean playAnimation = false;
    int animationIndex = 0;
    float animTime = 0f;

    public Player(Vector2 position) {
        this.position = position;
    }

    @NetEvent("shoot")
    public static void shoot(Vector2 position, float rotation) {
        Server.addObject(new Bullet(position, rotation));
    }

    @NetEvent("hit")
    public static void hit(int damage) {
        System.out.println("Took " + damage + " damage!");
    }

    @Override
    protected void setup() {
        setSprite("player");
        //setRotation(45);
        scale = new Vector2(0.15f, 0.15f);
    }

    @Override
    public void update(float deltaTime) {
        time += deltaTime;
        animTime += deltaTime;

        // Animation
        if (playAnimation) {
            if (animTime >= 0.03f) {
                setSprite(animation[animationIndex]);
                animationIndex++;
                animTime = 0;
                if (animationIndex >= animation.length) {
                    animationIndex = 0;
                    playAnimation = false;
                }
            }
        }

        // Movement
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

        // Shooting
        if (Input.mouse.isClicked(0) && time >= 0.1) {
            
            AudioPlayer.playAudio(shooting, false);
            Vector2 bulletPosition = position;
            double rotationRad = (double) Math.toRadians(rotation);
            float xOffset = (float) (Math.cos(rotationRad) * 50f - Math.sin(rotationRad) * 7f);
            float yOffset = (float) (Math.sin(rotationRad) * 50f + Math.cos(rotationRad) * 7f);
            Vector2 offset = new Vector2(xOffset, yOffset);
            bulletPosition = bulletPosition.add(offset);
            sendMessage("shoot", bulletPosition, rotation);
            time = 0;
            playAnimation = true;
        }

        Camera.currentCamera.position = position;
    }
    
}
