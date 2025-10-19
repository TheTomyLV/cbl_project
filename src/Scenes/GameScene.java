package Scenes;

import Engine.Networking.Server;

import java.awt.Color;

import Engine.Scene;
import Engine.Vector2;
import GameObjects.EnemyManager;
import GameObjects.Player;
import GameObjects.Grass;
import GameObjects.HealthBar;

public class GameScene extends Scene {
    

    @Override
    public void setupScene() {
        setBackground(new Color(91, 207, 128));
        
        Player player = new Player(new Vector2(230, 230));
        addObject(player);
        addObject(new HealthBar(player));

        Server.addObject(new EnemyManager());
        Server.addObject(new Grass());
    }
}
