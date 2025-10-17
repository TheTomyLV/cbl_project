package Scenes;

import Engine.Networking.Server;
import Engine.Scene;
import Engine.Vector2;
import GameObjects.EnemyManager;
import GameObjects.Player;

public class GameScene extends Scene {
    

    @Override
    public void setupScene() {
        
        addObject(new Player(new Vector2(230, 230)));

        Server.addObject(new EnemyManager());
    }
}
