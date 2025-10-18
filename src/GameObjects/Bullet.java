package GameObjects;

import Engine.Engine;
import Engine.GameObject;
import Engine.Vector2;

public class Bullet extends GameObject {
    float time = 0f;
    float speed = 400f;

    public Bullet(Vector2 position, float rotation) {
        this.position = position;
        this.rotation = rotation;
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

        if (time >= 2) {
            Engine.destroy(this);
        }
    }
}
