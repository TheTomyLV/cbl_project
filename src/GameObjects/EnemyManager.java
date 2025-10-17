package GameObjects;

import java.util.Random;

import Engine.GameObject;
import Engine.Vector2;
import Engine.Networking.Server;

public class EnemyManager extends GameObject {

    private float time = 0;
    private Random rng;

    @Override
    protected void setup() {
        rng = new Random();
    }

    @Override
    public void update(float deltaTime) {
        time += deltaTime;

        if (time >= 1) {
            Enemy enemy = new Enemy(new Vector2(rng.nextFloat(0, 460), rng.nextFloat(0, 460)));
            Server.addObject(enemy);
            time = 0;
        }
    }
}
