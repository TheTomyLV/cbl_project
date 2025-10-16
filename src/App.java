import java.util.Vector;

import Engine.Engine;
import Engine.Sprite;
import Engine.Vector2;
import Scenes.LobbyScene;

/**
 * Main app for the program.
 */
public class App {

    public void run() {
        Engine engine = new Engine();
        engine.setup();

        String artAssetPath = "src\\Assets\\art\\";
        Sprite.loadImage("player", artAssetPath + "player.png", new Vector2(0.5f, 0.5f));
        Sprite.loadImage("bullet", artAssetPath + "bullet.png", new Vector2(0.5f, 0.5f));
        
        LobbyScene lobbyScene = new LobbyScene();
        Engine.changeScene(lobbyScene);

        while (engine.isRunning()) {
            engine.update();
        }
    }

    public static void main(String[] args) throws Exception {
        new App().run();
    }
}
