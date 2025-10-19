package GameObjects;

import Engine.Camera;
import Engine.Engine;
import Engine.GameObject;
import Engine.Inputs.Input;
import Engine.Networking.NetEvent;
import Engine.Networking.Server;
import Engine.Sound.AudioClip;
import Engine.Sound.AudioPlayer;
import Engine.Vector2;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Player extends GameObject {
    double time = 0;
    float speed = 2.0f;
    Vector2 velocity = new Vector2(0f, 0f);
    AudioClip shooting = new AudioClip("src\\Assets\\audio\\shoot.wav");
    String[] pistolAnim = {"player_pistol1", "player_pistol2", "player_pistol3", "player_pistol"};
    String[] mgAnim = {"player_mg1", "player_mg2", "player_mg"};
    String[] shotgunAnim = {"player_sg", "player_sg1", "player_sg2", "player_sg3", "player_sg", "player_sg4", "player_sg5", "player_sg6", "player_sg7", "player_sg4", "player_sg"};
    String[] currentAnim = pistolAnim;
    boolean playAnimation = false;
    int animationIndex = 0;
    float animTime = 0f;
    int health = 100;
    int maxHealth = 100;
    int selectedWeapon = 0;
    float reloadTime = 0.3f;
    String weaponAttackType = "shoot_pistol";

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    private void selectWeapon(int index) {
        animationIndex = 0;
        switch (index) {
            case 0:
                reloadTime = 0.3f;
                weaponAttackType = "shoot_pistol";
                setSprite("player_pistol");
                currentAnim = pistolAnim;
                break;
            case 1:
                reloadTime = 0.8f;
                weaponAttackType = "shoot_shotgun";
                setSprite("player_sg");
                currentAnim = shotgunAnim;
                break;
            case 2:
                reloadTime = 0.1f;
                weaponAttackType = "shoot_minigun";
                setSprite("player_mg");
                currentAnim = mgAnim;
            default:
                break;
        }
    }

    public Player(Vector2 position) {
        this.position = position;
    }

    @NetEvent("player_hit")
    public static void hit(int damage) {
        ArrayList<GameObject> players = Engine.getCurrentScene().getObjectsOfClass(Player.class);
        Player player;
        if (players.size() == 0) {
            return;
        }
        player = (Player) players.get(0);
        player.health -= damage;
        if (player.getHealth() < 0) {
            player.health = 0;
        }
    }

    @NetEvent("health_pickup")
    public static void healthPickup(int health) {
        ArrayList<GameObject> players = Engine.getCurrentScene().getObjectsOfClass(Player.class);
        Player player;
        if (players.size() == 0) {
            return;
        }
        player = (Player) players.get(0);
        player.health += health;
        if (player.getHealth() > player.maxHealth) {
            player.health = player.maxHealth;
        }
    }

    @Override
    protected void setup() {
        setSprite("player_pistol");
        //setRotation(45);
        scale = new Vector2(0.15f, 0.15f);
        selectWeapon(0);
    }

    @Override
    public void update(float deltaTime) {
        time += deltaTime;
        animTime += deltaTime;

        // Animation
        if (playAnimation) {
            if (animTime >= 0.03f) {
                setSprite(currentAnim[animationIndex]);
                animationIndex++;
                animTime = 0;
                if (animationIndex >= currentAnim.length) {
                    animationIndex = 0;
                    playAnimation = false;
                }
            }
        }

        if (Input.isKeyPressed(KeyEvent.VK_1)) {
            selectWeapon(0);
        }
        if (Input.isKeyPressed(KeyEvent.VK_2)) {
            selectWeapon(1);
        }
        if (Input.isKeyPressed(KeyEvent.VK_3)) {
            selectWeapon(2);
        }

        // Movement
        if (Input.isKeyPressed(KeyEvent.VK_W)) {
            velocity.y -= deltaTime * speed;
        }
        if (Input.isKeyPressed(KeyEvent.VK_S)) {
            velocity.y += deltaTime * speed;
        }
        if (Input.isKeyPressed(KeyEvent.VK_A)) {
            velocity.x -= deltaTime * speed;
        }
        if (Input.isKeyPressed(KeyEvent.VK_D)) {
            velocity.x += deltaTime * speed;
        }

        position = position.add(velocity);
        velocity = velocity.multiply(0.99f); // Hacky for now
        rotation = Input.mouse.getWorldPosition().subtract(position).getRotation();

        // Shooting
        if (Input.mouse.isClicked(0) && time >= reloadTime) {
            
            AudioPlayer.playAudio(shooting, false);
            Vector2 bulletPosition = position;
            sendMessage(weaponAttackType, bulletPosition, rotation);
            time = 0;
            playAnimation = true;
        }

        Camera.currentCamera.position = position;

    }
    
}
