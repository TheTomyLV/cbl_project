package GameObjects;

import Engine.GameObject;
import Engine.Networking.Server;
import Engine.Vector2;

public class Enemy extends GameObject {
    double time = 0;
    float speed = 2.0f;
    Vector2 velocity = new Vector2(0f, 0f);
    int health = 50;
    float hitAnim = 0f;

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
        

        if (hitAnim > 0) {
            hitAnim -= deltaTime;
        } else {
            scale = new Vector2(0.15f, 0.15f);
            setSprite("zombie");
        }

        position = position.add(velocity);
        velocity = velocity.multiply(0.99f); // Hacky for now
        rotation += deltaTime * 200f;
    }

    public void hit(int damage) {
        health -= damage;
        hitAnim = 0.05f;
        scale = new Vector2(0.14f, 0.14f);
        setSprite("zombie_hit");
        if (health <= 0) {
            Server.removeObject(this);
        }
    }
}
