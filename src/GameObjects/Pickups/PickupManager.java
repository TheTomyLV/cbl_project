package GameObjects.Pickups;

import java.util.ArrayList;

import Engine.GameObject;
import Engine.Vector2;
import Engine.Networking.Server;
import GameObjects.Player;

public class PickupManager {

    public static void createPickup(Vector2 position, String type) {
        switch (type) {
            case "health_pickup":
                HealthPickup healthPickup = new HealthPickup(20);
                healthPickup.position = position;
                Server.addObject(healthPickup);
                break;
        
            default:
                break;
        }
    }
}