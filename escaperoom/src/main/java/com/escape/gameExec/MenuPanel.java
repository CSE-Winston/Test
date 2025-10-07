package com.escape.gameExec;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MenuPanel extends VBox {
    
    public MenuPanel(Stage stage) {
        // Set background color
        this.setStyle("-fx-background-color: #2b2b2b;");
        this.setAlignment(Pos.CENTER);
        this.setSpacing(20);
        
        // Create title
        Label title = new Label("Escape Game");
        title.setFont(Font.font("Arial", 48));
        title.setTextFill(Color.WHITE);
        
        // Create start button
        Button startButton = new Button("Start Game");
        startButton.setFont(Font.font("Arial", 24));
        startButton.setPrefWidth(200);
        startButton.setPrefHeight(50);
        
        // Button action
        startButton.setOnAction(e -> {
            GameUi gamePanel = new GameUi();
            Scene gameScene = new Scene(gamePanel, 768, 576);
            
            stage.setScene(gameScene);
            gamePanel.requestFocus();
            gamePanel.setupGame();
            gamePanel.startGameThread();
        });
        
        // Add components
        this.getChildren().addAll(title, startButton);
    }
}