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

        // Load all art assets
        String artAssetPath = "src\\Assets\\art\\";
        Sprite.loadImage("player", artAssetPath + "player.png", new Vector2(0.5f, 0.5f));
        Sprite.loadImage("bullet", artAssetPath + "bullet.png", new Vector2(0.5f, 0.5f));
        Sprite.loadImage("zombie", artAssetPath + "zombie.png", new Vector2(0.5f, 0.5f));
        
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
