package GameObjects;

import Engine.Engine;
import Engine.GameObject;
import Engine.Vector2;

public class Bullet extends GameObject {
    float time = 0f;
    float speed = 200f;

    public Bullet(Vector2 position, float rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    @Override
    protected void setup() {
        loadImage("src\\Assets\\art\\Player.png");
        scale = new Vector2(0.05f, 0.05f);
    }

    @Override
    protected void update(float deltaTime) {
        time += deltaTime;
        position = position.add(Vector2.fromRotation((float) Math.toRadians(rotation)).multiply(speed * deltaTime));

        if (time >= 5) {
            Engine.destroy(this);
        }
    }
}
