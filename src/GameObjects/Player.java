package GameObjects;

import java.awt.MouseInfo;
import java.awt.event.KeyEvent;

import Engine.GameObject;
import Engine.Input;

public class Player extends GameObject {

    double time = 0;
    float speed = 1.2f;
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
    protected void onLoad() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onLoad'");
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onDestroy'");
    }

    @Override
    public void update(float deltaTime) {
        time += deltaTime;
        // y = (int) (Math.sin(time * 0.01) * 200) + yPos;
        // x = (int) (Math.cos(time * 0.01) * 200) + xPos;
        // setRotation((float) time);

        if (Input.keyPressed(KeyEvent.VK_W)) {
            y -= deltaTime * speed;
        }
        if (Input.keyPressed(KeyEvent.VK_S)) {
            y += deltaTime * speed;
        }
        if (Input.keyPressed(KeyEvent.VK_A)) {
            x -= deltaTime * speed;
        }
        if (Input.keyPressed(KeyEvent.VK_D)) {
            x += deltaTime * speed;
        }
        // x = (float) MouseInfo.getPointerInfo().getLocation().getX();
        // y = (float) MouseInfo.getPointerInfo().getLocation().getY();
    }
    
}
