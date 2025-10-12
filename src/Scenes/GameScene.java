package Scenes;

import Engine.Scene;
import GameObjects.Player;

public class GameScene extends Scene {

    @Override
    public void setupScene() {
        addObject(new Player(230, 230));
    }
    
}
