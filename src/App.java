import Engine.ClassManager;
import Engine.Engine;
import Engine.Sprite;
import Engine.Vector2;
import Engine.Networking.NetworkHandleRegister;
import Scenes.LobbyScene;

/**
 * Main app for the program.
 */
public class App {

    public void run() {
        // Initializes engine
        Engine.start();

        // Register netowork events
        NetworkHandleRegister.registerAllGameObjectHandlers("GameObjects");

        // Register GameObjects
        ClassManager.registerClassesFromBasePackage("GameObjects");
        // Register scenes
        ClassManager.registerClassesFromBasePackage("Scenes");

        // Art asset path
        String artAssetPath = "src\\Assets\\art\\";

        // Pistol sprites
        Sprite.loadImage("player_pistol", artAssetPath + "player\\smith_pistol.png", new Vector2(0.25f, 0.5f));
        Sprite.loadImage("player_pistol1", artAssetPath + "player\\smith_pistol1.png", new Vector2(0.18f, 0.5f));
        Sprite.loadImage("player_pistol2", artAssetPath + "player\\smith_pistol2.png", new Vector2(0.18f, 0.5f));
        Sprite.loadImage("player_pistol3", artAssetPath + "player\\smith_pistol3.png", new Vector2(0.18f, 0.5f));

        // Rifle sprites
        Sprite.loadImage("player_mg", artAssetPath + "player\\Smith_mg.png", new Vector2(0.25f, 0.6f));
        Sprite.loadImage("player_mg1", artAssetPath + "player\\Smith_mg1.png", new Vector2(0.18f, 0.6f));
        Sprite.loadImage("player_mg2", artAssetPath + "player\\Smith_mg2.png", new Vector2(0.25f, 0.6f));

        // Shotgun sprites
        Sprite.loadImage("player_sg", artAssetPath + "player\\Smith_sg.png", new Vector2(0.25f, 0.6f));
        Sprite.loadImage("player_sg1", artAssetPath + "player\\Smith_sg1.png", new Vector2(0.2f, 0.6f));
        Sprite.loadImage("player_sg2", artAssetPath + "player\\Smith_sg2.png", new Vector2(0.2f, 0.6f));
        Sprite.loadImage("player_sg3", artAssetPath + "player\\Smith_sg3.png", new Vector2(0.2f, 0.6f));
        Sprite.loadImage("player_sg4", artAssetPath + "player\\Smith_sg4.png", new Vector2(0.25f, 0.6f));
        Sprite.loadImage("player_sg5", artAssetPath + "player\\Smith_sg5.png", new Vector2(0.25f, 0.6f));
        Sprite.loadImage("player_sg6", artAssetPath + "player\\Smith_sg6.png", new Vector2(0.25f, 0.6f));
        Sprite.loadImage("player_sg7", artAssetPath + "player\\Smith_sg7.png", new Vector2(0.25f, 0.6f));

        Sprite.loadImage("bullet", artAssetPath + "bullet.png", new Vector2(0.85f, 0.5f));
        Sprite.loadImage("zombie", artAssetPath + "descrom.png", new Vector2(0.3f, 0.5f));
        Sprite.loadImage("zombie_hit", artAssetPath + "descrom_hit.png", new Vector2(0.3f, 0.5f));
        Sprite.loadImage("grass", artAssetPath + "grass.png", new Vector2(0.5f, 0.5f));
        Sprite.loadImage("health", artAssetPath + "health.png", new Vector2(0f, 0.5f));
        Sprite.loadImage("health_pickup", artAssetPath + "health_pickup.png", new Vector2(0.5f, 0.5f));
        
        // Load starting scene
        Engine.changeScene(new LobbyScene());

        // Update loop
        while (Engine.isRunning()) {
            Engine.update();
        }
    }

    public static void main(String[] args) throws Exception {
        new App().run();
    }
}
