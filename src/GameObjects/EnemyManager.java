package GameObjects;

import Engine.GameObject;
import Engine.Networking.Server;
import Engine.Vector2;
import java.util.Random;

/**
 * An enemy manager that spawns enemies randomly on the map.
 */
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

        if (time >= 2) {
            float x = rng.nextFloat(-300, 300);
            float y = rng.nextFloat(-300, 300);
            Enemy enemy = new Enemy(new Vector2(x, y));
            Server.addObject(enemy);
            time = 0;
        }
    }
}
