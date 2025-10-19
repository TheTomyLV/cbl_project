package GameObjects.Pickups;

import Engine.GameObject;
import Engine.Vector2;
import Engine.Networking.Server;

public class HealthPickup extends Pickup {
    
    int givenHealth;

    public HealthPickup(int givenHealth) {
        this.givenHealth = givenHealth;
    }

    @Override
    protected void setup() {
        setSprite("health_pickup");
        scale = new Vector2(0.1f, 0.1f);
    }

    @Override
    public void onPickUp(GameObject player) {
        sendMessage("health_pickup", player.getOwnerUUID(), givenHealth);
        Server.removeObject(this);
    }
}
