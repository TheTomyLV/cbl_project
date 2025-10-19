package GameObjects.Pickups;

import java.util.ArrayList;

import Engine.GameObject;
import Engine.Networking.Server;
import GameObjects.Player;

public class Pickup extends GameObject {
    
    float pickupDistance = 20f;

    public void onPickUp(GameObject player) {
        Server.removeObject(this);
    }

    @Override
    public void update(float deltaTime) {
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

        if (closestDistance < pickupDistance) {
            onPickUp(closestPlayer);
        }
    }
}
