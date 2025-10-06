package Scenes;

import java.awt.event.*;

import javax.swing.*;

import Engine.Engine;
import Engine.Scene;
import GameObjects.Player;

public class LobbyScene extends Scene {
    
    @Override
    public void setupScene() {

        JButton createLobbyButton = new JButton("Create lobby");
        JButton joinLobbyButton = new JButton("Join lobby");
        JTextField textField = new JTextField();

        createLobbyButton.setBounds(150, 200, 220, 50);
        joinLobbyButton.setBounds(150, 260, 220, 50);
        textField.setBounds(150, 320, 220, 50);

        createLobbyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Engine.changeScene(new CreateLobbyScene());
            }
        });

        joinLobbyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });

        add(textField);
        add(joinLobbyButton);
        add(createLobbyButton);

        addObject(new Player(230, 20));
    }
    
}
