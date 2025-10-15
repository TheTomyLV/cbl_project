package GameObjects;

import Engine.Engine;
import Engine.GameObject;
import Engine.Vector2;

public class Bullet extends GameObject {
    float time = 0f;
    float speed = 200f;

    public Bullet(Vector2 position) {
        this.position = position;
    }

    @Override
    protected void setup() {
        loadImage("src\\Assets\\art\\Player.png");
        scale = new Vector2(0.01f, 0.01f);
    }

    @Override
    protected void update(float deltaTime) {
        time += deltaTime;
        position.x += speed * deltaTime;

        if (time >= 1) {
            Engine.destroy(this);
        }
    }
}
