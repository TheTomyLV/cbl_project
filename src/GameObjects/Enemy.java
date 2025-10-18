package GameObjects;

import Engine.Camera;
import Engine.Engine;
import Engine.GameObject;
import Engine.Vector2;
import Engine.Inputs.Input;
import Engine.Sound.AudioClip;
import Engine.Sound.AudioPlayer;

public class Enemy extends GameObject {
    double time = 0;
    float speed = 2.0f;
    Vector2 velocity = new Vector2(0f, 0f);

    public Enemy(Vector2 position) {
        this.position = position;
    }

    @Override
    protected void setup() {
        setSprite("zombie");
        //setRotation(45);
        scale = new Vector2(0.15f, 0.15f);
    }

    @Override
    public void update(float deltaTime) {
        time += deltaTime;

        position = position.add(velocity);
        velocity = velocity.multiply(0.99f); // Hacky for now
    }
}
