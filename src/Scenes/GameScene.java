package Scenes;

import Engine.Scene;
import Engine.Vector2;
import GameObjects.Player;

public class GameScene extends Scene {

    @Override
    public void setupScene() {
        addObject(new Player(new Vector2(230, 230)));
    }
}
