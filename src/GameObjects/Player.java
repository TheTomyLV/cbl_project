package GameObjects;

import Engine.Camera;
import Engine.GameObject;
import Engine.Input;
import Engine.Sound.AudioClip;
import Engine.Sound.AudioPlayer;

import java.awt.event.KeyEvent;

public class Player extends GameObject {

    double time = 0;
    float speed = 5.0f;
    float xVel = 0;
    float yVel = 0;
    int xPos;
    int yPos;
    AudioClip shooting = new AudioClip("src\\Assets\\audio\\shoot.wav");

    public Player(int x, int y) {
        xPos = x;
        yPos = y;
        this.x = x;
        this.y = y;
    }

    @Override
    protected void setup() {
        loadImage("src\\Assets\\art\\Player.png");
        //setRotation(45);
        scale = 0.1f;
        y = 100;

    }

    @Override
    public void update(float deltaTime) {
        time += deltaTime;
        // y = (int) (Math.sin(time * 0.01) * 200) + yPos;
        // x = (int) (Math.cos(time * 0.01) * 200) + xPos;
        // setRotation((float) time);

        if (Input.isKeyPressed(KeyEvent.VK_W)) {
            yVel -= deltaTime * speed;
        }
        if (Input.isKeyPressed(KeyEvent.VK_S)) {
            yVel += deltaTime * speed;
        }
        if (Input.isKeyPressed(KeyEvent.VK_A)) {
            xVel -= deltaTime * speed;
        }
        if (Input.isKeyPressed(KeyEvent.VK_D)) {
            xVel += deltaTime * speed;
        }
        if (Input.isKeyPressed(KeyEvent.VK_SPACE) && time >= 0.1) {
            AudioPlayer.playAudio(shooting, false);
            time = 0;
        }

        x += xVel;
        y += yVel;
        yVel *= 0.99f; // Hacky for now
        xVel *= 0.99f;

        Camera.currentCamera.x = x;
        Camera.currentCamera.y = y;
        // x = (float) MouseInfo.getPointerInfo().getLocation().getX();
        // y = (float) MouseInfo.getPointerInfo().getLocation().getY();
    }
    
}
