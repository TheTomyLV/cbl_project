package Scenes;

import Engine.Engine;
import Engine.Scene;
import java.awt.event.*;
import javax.swing.*;

public class LobbyScene extends Scene {
    
    @Override
    public void setupScene() {

        JButton createLobbyButton = new JButton("Create lobby");
        JButton joinLobbyButton = new JButton("Join lobby");
        JTextField ipTextField = new JTextField("localhost");
        JTextField portTextField = new JTextField("3345");

        createLobbyButton.setBounds(150, 200, 220, 50);
        joinLobbyButton.setBounds(150, 260, 220, 50);
        ipTextField.setBounds(150, 320, 220, 50);
        portTextField.setBounds(150, 380, 220, 50);

        createLobbyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean output = Engine.runServer(Integer.parseInt(portTextField.getText()));
                if (output) {
                    Engine.changeScene(new GameScene());
                }
            }
        });

        joinLobbyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ip = ipTextField.getText();
                int port = Integer.parseInt(portTextField.getText());
                boolean output = Engine.runClient(ip, port);
                if (output) {
                    Engine.changeScene(new GameScene());
                }
            }
        });

        add(ipTextField);
        add(portTextField);
        add(joinLobbyButton);
        add(createLobbyButton);
    }
    
}
