package com.escape.gameExec;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class GameUi extends Pane {
    final int originalTileSize = 16;
    final int scale = 3;
    
    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    final int screenWidth = tileSize * maxScreenCol;
    final int screenHeight = tileSize * maxScreenRow;
    
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;
    
    // JavaFX Canvas for drawing
    private Canvas canvas;
    private GraphicsContext gc;
    
    // Game components
    KeyHandler keyH;
    CollisionHandler cHandler;
    TileManager tileM;
    Player player;
    UI ui;
    
    // Game states
    public int gameState;
    public int playState = 1;
    public int pauseState = 2;
    public int dialogueState = 3;
    
    // Animation timer for game loop
    private AnimationTimer gameTimer;
    private long lastUpdate = 0;
    private final long FRAME_TIME = 1_000_000_000 / 60; // 60 FPS
    
    public GameUi() {
        // Create canvas
        canvas = new Canvas(screenWidth, screenHeight);
        gc = canvas.getGraphicsContext2D();
        
        // Add canvas to pane
        this.getChildren().add(canvas);
        
        // Initialize game components
        keyH = new KeyHandler(this);
        cHandler = new CollisionHandler(this);
        tileM = new TileManager(this);
        player = new Player(this, keyH);
        ui = new UI(this);
        
        // Set up key listeners
        setupKeyHandlers();
    }
    
    private void setupKeyHandlers() {
        this.setOnKeyPressed(this::handleKeyPressed);
        this.setOnKeyReleased(this::handleKeyReleased);
        
        // Make sure pane can receive key events
        this.setFocusTraversable(true);
    }
    
    private void handleKeyPressed(KeyEvent e) {
        KeyCode code = e.getCode();
        
        if (gameState == playState) {
            if (code == KeyCode.W) keyH.upPressed = true;
            if (code == KeyCode.S) keyH.downPressed = true;
            if (code == KeyCode.A) keyH.leftPressed = true;
            if (code == KeyCode.D) keyH.rightPressed = true;
            if (code == KeyCode.P) gameState = pauseState;
        } 
        else if (gameState == dialogueState) {
            if (code == KeyCode.ENTER) {
                ui.currentDialogueIndex++;
                if (ui.currentDialogueIndex < ui.dialogues.length && 
                    ui.dialogues[ui.currentDialogueIndex] != null) {
                    ui.currentText = ui.dialogues[ui.currentDialogueIndex];
                } else {
                    gameState = playState;
                }
            }
        }
        else if (gameState == pauseState) {
            if (code == KeyCode.P) {
                gameState = playState;
            }
        }
    }
    
    private void handleKeyReleased(KeyEvent e) {
        KeyCode code = e.getCode();
        if (code == KeyCode.W) keyH.upPressed = false;
        if (code == KeyCode.S) keyH.downPressed = false;
        if (code == KeyCode.A) keyH.leftPressed = false;
        if (code == KeyCode.D) keyH.rightPressed = false;
    }
    
    public void setupGame() {
        ui.setDialogue();
        gameState = dialogueState;
    }
    
    public void startGameThread() {
        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }
                
                long elapsed = now - lastUpdate;
                if (elapsed >= FRAME_TIME) {
                    update();
                    render();
                    lastUpdate = now;
                }
            }
        };
        gameTimer.start();
    }
    
    public void stopGameThread() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }
    
    private void update() {
        if (gameState == playState) {
            player.update();
        }
    }
    
    private void render() {
        // Clear canvas
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, screenWidth, screenHeight);
        
        // Draw game elements
        tileM.draw(gc);
        player.draw(gc);
        ui.draw(gc);
    }
}