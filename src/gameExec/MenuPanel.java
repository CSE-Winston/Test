package gameExec;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPanel extends JPanel {
    private JButton startButton;

    public MenuPanel(JFrame window) {
        this.setPreferredSize(new Dimension(768, 576)); // same size as your game screen
        this.setBackground(Color.DARK_GRAY);
        this.setLayout(new GridBagLayout());

        JLabel title = new JLabel("Escape Game");
        title.setFont(new Font("Arial", Font.BOLD, 48));
        title.setForeground(Color.WHITE);

        startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.PLAIN, 24));

        // Button listener to switch panels
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.getContentPane().removeAll();   // remove menu
                GameUi gamePanel = new GameUi();       // make the game
                window.add(gamePanel);                 // add game
                window.revalidate();                   // refresh layout
                window.repaint();                      // redraw
                gamePanel.requestFocusInWindow();      // give game keyboard focus
                gamePanel.startGameThread();           // start game loop
                gamePanel.setupGame();
            }
        });

        // Layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.add(title, gbc);
        gbc.gridy = 1;
        this.add(startButton, gbc);
    }
}
