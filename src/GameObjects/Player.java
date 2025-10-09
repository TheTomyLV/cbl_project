package GameObjects;

import Engine.GameObject;
import Engine.Input;
import java.awt.event.KeyEvent;

public class Player extends GameObject {

    double time = 0;
    float speed = 5.0f;
    float xVel = 0;
    float yVel = 0;
    int xPos;
    int yPos;

    public Player(int x, int y) {
        xPos = x;
        yPos = y;
        this.x = x;
        this.y = y;
    }

    @Override
    protected void setup() {
        loadImage("player", "src\\Assets\\Player.png");
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

        x += xVel;
        y += yVel;
        yVel *= 0.99f; // Hacky for now
        xVel *= 0.99f;
        // x = (float) MouseInfo.getPointerInfo().getLocation().getX();
        // y = (float) MouseInfo.getPointerInfo().getLocation().getY();
    }
    
}
