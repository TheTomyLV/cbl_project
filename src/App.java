import Engine.Engine;
import Scenes.LobbyScene;

/**
 * Main app for the program.
 */
public class App {

    public void run() {
        Engine engine = new Engine();
        engine.setup();
        
        LobbyScene lobbyScene = new LobbyScene();
        engine.changeScene(lobbyScene);

        while (engine.isRunning()) {
            engine.update();
        }
        
    }

    public static void main(String[] args) throws Exception {
        new App().run();
    }
}
