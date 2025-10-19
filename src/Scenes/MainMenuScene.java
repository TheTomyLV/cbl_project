package Scenes;

import Engine.Engine;
import Engine.Scene;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class MainMenuScene extends Scene {

    private final Image bgImage = new ImageIcon("src/Assets/art/lobby.jpg").getImage();

    //Status of joining/creating server
    private JLabel statusLabel;

    @Override
    public void setupScene() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 246, 250));

        // Fonts
        Font titleFont  = new Font("Segoe UI", Font.BOLD, 22);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 16);
        Font fieldFont  = new Font("Segoe UI", Font.PLAIN, 14);

        // Center container
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        add(center, BorderLayout.CENTER);

        // Card panel
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(true);
        card.setBackground(new Color(250, 253, 255));
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(208, 220, 240), 1, true),
                new EmptyBorder(18, 22, 18, 22)
        ));

        // Title
        JLabel title = new JLabel("Multiplayer Lobby", SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(titleFont);
        title.setForeground(new Color(40, 55, 80));

        JButton createLobbyButton = new JButton("Create lobby");
        JButton joinLobbyButton   = new JButton("Join lobby");
        JTextField ipTextField    = new JTextField("localhost");
        JTextField portTextField  = new JTextField("3345");

        Dimension itemSize = new Dimension(240, 36);
        styleButton(createLobbyButton, buttonFont, itemSize);
        styleButton(joinLobbyButton,   buttonFont, itemSize);
        styleField(ipTextField, fieldFont, itemSize);
        styleField(portTextField, fieldFont, itemSize);

        statusLabel = new JLabel(" "); 
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(180, 50, 50)); 
        statusLabel.setBorder(new EmptyBorder(4, 0, 0, 0));

        card.add(title);
        card.add(Box.createVerticalStrut(12));
        card.add(createLobbyButton);
        card.add(Box.createVerticalStrut(10));
        card.add(joinLobbyButton);
        card.add(Box.createVerticalStrut(12));
        card.add(ipTextField);
        card.add(Box.createVerticalStrut(8));
        card.add(portTextField);
        card.add(Box.createVerticalStrut(6));
        card.add(statusLabel); // <-- error/info area

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.CENTER;
        center.add(card, gbc);

        // Actions 
        createLobbyButton.addActionListener(e -> {
            Integer port = parsePort(portTextField.getText());
            if (port == null) {
                showError("Incorrect port number. Use 1–65535.");
                return;
            }
            showInfo("Starting server on port " + port + "…");
            boolean ok = Engine.runServer(port);
            if (ok) {
                statusLabel.setText(" "); // clear before switching
                Engine.changeScene(new GameScene());
            } else {
                showError("Couldn't start server.");
            }
        });

        joinLobbyButton.addActionListener(e -> {
            String ip = ipTextField.getText().trim();
            Integer port = parsePort(portTextField.getText());
            if (ip.isEmpty()) {
                showError("IP address is empty.");
                return;
            }
            if (port == null) {
                showError("Incorrect port number. Use 1–65535.");
                return;
            }
            showInfo("Connecting to " + ip + ":" + port + "…");
            boolean ok = Engine.runClient(ip, port);
            if (ok) {
                statusLabel.setText(" ");
                Engine.changeScene(new GameScene());
            } else {
                showError("Couldn't join game.");
            }
        });
    }


    private Integer parsePort(String text) {
        try {
            int p = Integer.parseInt(text.trim());
            return (p >= 1 && p <= 65535) ? p : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Show an error message in red below the inputs
    private void showError(String msg) {
        statusLabel.setForeground(new Color(200, 60, 60));
        statusLabel.setText(msg);
    }

    private void showInfo(String msg) {
        statusLabel.setForeground(new Color(60, 120, 60));
        statusLabel.setText(msg);
    }

    private void styleButton(JButton b, Font font, Dimension size) {
        b.setFont(font);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setPreferredSize(size);
        b.setMaximumSize(size);
        b.setFocusPainted(false);
        b.setBorder(new LineBorder(new Color(180, 200, 230), 1, true));
        b.setBackground(new Color(230, 238, 252));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(new Color(214, 229, 252)); }
            @Override public void mouseExited (java.awt.event.MouseEvent e) { b.setBackground(new Color(230, 238, 252)); }
        });
    }

    private void styleField(JTextField f, Font font, Dimension size) {
        f.setFont(font);
        f.setAlignmentX(Component.CENTER_ALIGNMENT);
        f.setPreferredSize(size);
        f.setMaximumSize(size);
        f.setOpaque(true);
        f.setBackground(Color.white);
        f.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 210, 230), 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth(), h = getHeight();
        int iw = bgImage.getWidth(null), ih = bgImage.getHeight(null);
        if (iw > 0 && ih > 0) {
            double sx = (double) w / iw, sy = (double) h / ih, s = Math.max(sx, sy);
            int dw = (int) (iw * s), dh = (int) (ih * s);
            int dx = (w - dw) / 2, dy = (h - dh) / 2;
            g2.drawImage(bgImage, dx, dy, dw, dh, null);
        }
        g2.dispose();
    }
}
