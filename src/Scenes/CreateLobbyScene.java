package Scenes;

import java.awt.event.*;

import javax.swing.*;

import Engine.Scene;
import GameObjects.Player;

public class CreateLobbyScene extends Scene {
    
    @Override
    public void setupScene() {

        JLabel createLobbyText = new JLabel("New lobby");

        createLobbyText.setBounds(150, 200, 220, 50);


        add(createLobbyText);
        addObject(new Player(230, 230));
        addObject(new Player(230, 330));
    }
    
}
