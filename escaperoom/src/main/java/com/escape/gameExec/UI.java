package com.escape.gameExec;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.shape.StrokeLineJoin;

public class UI {
    GameUi gp;
    Font arial_40, arial_80B;
    public boolean messageOn = false;
    public String message = "";
    public boolean gameFinished = false;
    public String currentText = "Welcome to the Escape Room";
    public String[] dialogues = new String[20];
    public int currentDialogueIndex = 0;
    
    public UI(GameUi gp) {
        this.gp = gp;
        arial_40 = Font.font("Arial", FontWeight.NORMAL, 40);
        arial_80B = Font.font("Arial", FontWeight.BOLD, 80);
    }
    
    public void draw(GraphicsContext gc) {
        gc.setFont(arial_40);
        gc.setFill(Color.WHITE);
        
        if(gp.gameState == gp.playState) {
            // Play state UI
        }
        if(gp.gameState == gp.dialogueState) {
            drawDialogueScreen(gc);
        }
        if(gp.gameState == gp.pauseState) {
            drawPauseScreen(gc);
        }
    }
    
    public void setDialogue() {
        dialogues[0] = "Welcome to the Escape Room";
        dialogues[1] = "Make sure to have fun";
        dialogues[2] = "Now go and escape!";
        dialogues[3] = "Ciao!";
        currentDialogueIndex = 0;
        currentText = dialogues[currentDialogueIndex];
    }
    
    public String getLevel() {
        return "1";
    }
    
    public void drawDialogueScreen(GraphicsContext gc) {
        int x = gp.tileSize * 2;
        int y = gp.tileSize / 2;
        int width = gp.screenWidth - (gp.tileSize * 4);
        int height = gp.tileSize * 4;
        
        drawSubWindow(gc, x, y, width, height);
        
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 32));
        x += gp.tileSize;
        y += gp.tileSize;
        
        gc.setFill(Color.WHITE);
        gc.fillText(currentText, x, y);
    }
    
    public void drawPauseScreen(GraphicsContext gc) {
        int x = gp.tileSize * 4;
        int y = gp.tileSize / 2;
        
        drawSubWindow(gc, x, y, 350, 500);
        
        x += gp.tileSize;
        y += gp.tileSize;
        
        gc.setFont(arial_40);
        gc.setFill(Color.WHITE);
        gc.fillText("Pause Menu", x, y);
        gc.fillText("Current Level: " + getLevel(), x, y * 4);
    }
    
    private void drawSubWindow(GraphicsContext gc, int x, int y, int width, int height) {
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(x, y, width, height, 35, 35);
        
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(5);
        gc.setLineJoin(StrokeLineJoin.ROUND);
        gc.strokeRoundRect(x, y, width, height, 35, 35);
    }
}