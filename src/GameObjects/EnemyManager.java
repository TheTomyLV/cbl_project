package GameObjects;

import Engine.GameObject;
import Engine.Networking.Server;
import Engine.Vector2;
import java.util.Random;

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
            Enemy enemy = new Enemy(new Vector2(rng.nextFloat(-300, 300), rng.nextFloat(-300, 300)));
            Server.addObject(enemy);
            time = 0;
        }
    }
}
