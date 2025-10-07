package gameExec;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        // Start with Menu
        MenuPanel menu = new MenuPanel(window);
        window.add(menu);

        window.pack();
        window.setLocationRelativeTo(null); // center screen
        window.setVisible(true);
    }
}
