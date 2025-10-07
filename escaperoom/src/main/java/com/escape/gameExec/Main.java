package com.escape.gameExec;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // Create menu
        MenuPanel menu = new MenuPanel(primaryStage);
        
        Scene scene = new Scene(menu, 768, 576);
        
        primaryStage.setTitle("Escape Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}