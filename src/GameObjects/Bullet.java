package GameObjects;

import java.util.ArrayList;
import java.util.Random;

import Engine.Engine;
import Engine.GameObject;
import Engine.Vector2;
import Engine.Networking.Server;

public class Bullet extends GameObject {
    float time = 0f;
    float speed = 400f;
    Vector2 velocity = new Vector2(0f, 0f);

    public Bullet(Vector2 position, float rotation) {
        Random rng = new Random();
        this.position = position;
        this.rotation = rotation + rng.nextFloat(-10.0f, 10.0f);
    }

    @Override
    protected void setup() {
        setSprite("bullet");
        scale = new Vector2(0.025f, 0.025f);
    }

    @Override
    public void update(float deltaTime) {
        time += deltaTime;
        float rotationInRad = (float) Math.toRadians(rotation);
        position = position.add(Vector2.fromRotation(rotationInRad).multiply(speed * deltaTime));

        ArrayList<GameObject> gameObjects = Server.getObjects();

        for (int i = 0; i < gameObjects.size(); i++) {
            if (gameObjects.get(i) instanceof Enemy) {
                float distance = gameObjects.get(i).position.subtract(position).length();
                if (distance <= 9f) {
                    sendMessage("hit", Server.getClientUUIDs().get(0), 5);
                    Enemy enemy = (Enemy) gameObjects.get(i);
                    enemy.hit(10);
                    Server.removeObject(this);
                }
            }
        }


        if (time >= 2) {
            Server.removeObject(this);
        }
    }
}
