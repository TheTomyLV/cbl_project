package GameObjects;

import java.util.ArrayList;
import java.util.Random;

import Engine.GameObject;
import Engine.Networking.Server;
import GameObjects.Pickups.Pickup;
import Engine.Vector2;
import Engine.Inputs.Input;
import GameObjects.Pickups.PickupManager;

public class Enemy extends GameObject {
    double time = 0;
    float speed = 0.5f;
    Vector2 velocity = new Vector2(0f, 0f);
    int health = 50;
    float hitAnim = 0f;
    float attackTimer = 0f;
    Random rng;

    public Enemy(Vector2 position) {
        this.position = position;
    }

    @Override
    protected void setup() {
        rng = new Random();
        setSprite("zombie");
        //setRotation(45);
        scale = new Vector2(0.15f, 0.15f);
    }

    @Override
    public void update(float deltaTime) {
        time += deltaTime;
        attackTimer += deltaTime;

        if (hitAnim > 0) {
            hitAnim -= deltaTime;
        } else {
            scale = new Vector2(0.15f, 0.15f);
            setSprite("zombie");
        }

        ArrayList<GameObject> playerObjects = Server.getClientObjectOfClass(Player.class);

        float closestDistance = Float.MAX_VALUE;
        GameObject closestPlayer = null;
        for (GameObject player : playerObjects) {
            float distance = player.position.subtract(position).length();
            if (distance < closestDistance) {
                closestDistance = distance;
                closestPlayer = player;
            }
        }

        rotation = closestPlayer.position.subtract(position).getRotation();

        if (closestDistance < 25f) {
            if (attackTimer > 0.5f) {
                sendMessage("player_hit", closestPlayer.getOwnerUUID(), 10);
                attackTimer = 0;
            }
        } else {
            
            velocity = velocity.add(new Vector2(deltaTime * speed, 0f).rotate(rotation));
        }



        position = position.add(velocity);
        velocity = velocity.multiply(0.99f); // Hacky for now
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

    @Override
    public void onDestroy() {
        if (rng.nextFloat() >= 0.5f) {
            PickupManager.createPickup(position, "health_pickup");
        }
    }
}
