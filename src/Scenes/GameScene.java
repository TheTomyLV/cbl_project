package Scenes;

import Engine.Networking.Server;

import java.awt.Color;

import Engine.Scene;
import Engine.Vector2;
import GameObjects.EnemyManager;
import GameObjects.Player;
import GameObjects.Grass;

public class GameScene extends Scene {
    

    @Override
    public void setupScene() {
        setBackground(new Color(91, 207, 128));
        
        addObject(new Player(new Vector2(230, 230)));

        Server.addObject(new EnemyManager());
        Server.addObject(new Grass());
    }
}
