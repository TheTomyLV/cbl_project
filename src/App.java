import Engine.Engine;
import Engine.Sprite;
import Scenes.LobbyScene;

/**
 * Main app for the program.
 */
public class App {

    public void run() {
        Engine engine = new Engine();
        engine.setup();
        Sprite.loadAssets("src\\Assets\\art");
        
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
