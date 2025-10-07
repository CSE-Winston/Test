package com.escape.game;

import com.escape.objects.*;
import com.escape.puzzles.*;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.List;

public class HollowmoreUI extends Pane {
    private Canvas canvas;
    private GraphicsContext gc;
    private HollowmoreGameManager gameManager;
    
    private final int screenWidth = 768;
    private final int screenHeight = 576;
    
    // UI State
    private InteractiveObject selectedObject;
    private boolean showingDialogue;
    private List<String> currentDialogue;
    private int dialogueIndex;
    
    // Animation timer
    private AnimationTimer gameLoop;
    private long lastUpdate = 0;
    
    // Inventory UI
    private List<String> inventorySlots;
    private int selectedInventoryIndex = -1;
    
    public HollowmoreUI() {
        canvas = new Canvas(screenWidth, screenHeight);
        gc = canvas.getGraphicsContext2D();
        this.getChildren().add(canvas);
        
        gameManager = HollowmoreGameManager.getInstance();
        inventorySlots = new ArrayList<>();
        
        setupMouseHandlers();
        setupKeyHandlers();
    }
    
    private void setupMouseHandlers() {
        canvas.setOnMouseClicked(this::handleMouseClick);
        canvas.setOnMouseMoved(this::handleMouseMove);
    }
    
    private void setupKeyHandlers() {
        this.setFocusTraversable(true);
        this.setOnKeyPressed(e -> {
            switch(e.getCode()) {
                case ENTER:
                    if (showingDialogue) {
                        advanceDialogue();
                    }
                    break;
                case ESCAPE:
                    if (showingDialogue) {
                        closeDialogue();
                    }
                    break;
                case I:
                    toggleInventory();
                    break;
                default:
                    break;
            }
        });
    }
    
    private void handleMouseClick(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();
        
        if (showingDialogue) {
            advanceDialogue();
            return;
        }
        
        // Check if clicked on an interactive object
        Room currentRoom = gameManager.getCurrentRoom();
        if (currentRoom != null) {
            for (InteractiveObject obj : currentRoom.getObjects()) {
                if (obj.contains(x, y) && obj.canInteract()) {
                    handleObjectInteraction(obj);
                    return;
                }
            }
        }
        
        // Check inventory clicks
        if (y > screenHeight - 80) {
            handleInventoryClick(x, y);
        }
    }
    
    private void handleMouseMove(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();
        
        // Highlight objects on hover
        Room currentRoom = gameManager.getCurrentRoom();
        if (currentRoom != null) {
            selectedObject = null;
            for (InteractiveObject obj : currentRoom.getObjects()) {
                if (obj.contains(x, y) && obj.canInteract()) {
                    selectedObject = obj;
                    break;
                }
            }
        }
    }
    
    private void handleObjectInteraction(InteractiveObject obj) {
        String type = obj.getType();
        
        switch(type) {
            case "COLLECTIBLE":
                CollectibleObject collectible = (CollectibleObject) obj;
                collectible.interact();
                gameManager.addToInventory(obj.getObjectId());
                checkLedgerPuzzleProgress();
                break;
                
            case "CONTAINER":
                ContainerObject container = (ContainerObject) obj;
                if (container.isLocked()) {
                    showDialogue("It's locked. You need a code.");
                    // Show code input prompt
                    promptForCode(container);
                } else {
                    container.interact();
                    // Give items to player
                    for (String itemId : container.getContainedItems()) {
                        gameManager.addToInventory(itemId);
                    }
                }
                break;
                
            case "CYCLIC":
                CyclicObject cyclic = (CyclicObject) obj;
                cyclic.interact();
                checkPortraitPuzzle();
                break;
                
            case "EXAMINE":
                ExamineObject examine = (ExamineObject) obj;
                examine.interact();
                showDialogue(examine.getExamineText());
                break;
                
            default:
                obj.interact();
                break;
        }
    }
    
    // Foyer Ledger Puzzle Logic
    private void checkLedgerPuzzleProgress() {
        Puzzle puzzle = gameManager.getCurrentPuzzle();
        if (puzzle instanceof LedgerAssemblyPuzzle) {
            LedgerAssemblyPuzzle ledgerPuzzle = (LedgerAssemblyPuzzle) puzzle;
            
            // Count collected pages
            Room room = gameManager.getCurrentRoom();
            int pagesCollected = 0;
            for (InteractiveObject obj : room.getObjects()) {
                if (obj instanceof CollectibleObject) {
                    CollectibleObject coll = (CollectibleObject) obj;
                    if (coll.isCollected() && obj.getName().contains("Page")) {
                        pagesCollected++;
                        ledgerPuzzle.collectPage(coll.getEvidenceValue());
                    }
                }
            }
            
            if (ledgerPuzzle.hasAllPages()) {
                showDialogue("You have all the pages. The dates form a pattern: 18__, 47__");
            }
        }
    }
    
    private void promptForCode(ContainerObject container) {
        // In a full implementation, this would show a text input dialog
        // For demo purposes, we'll just attempt the known code
        String code = "1847";
        boolean unlocked = container.tryUnlock(code);
        
        if (unlocked) {
            gameManager.solvePuzzle(code);
            container.interact();
            for (String itemId : container.getContainedItems()) {
                gameManager.addToInventory(itemId);
            }
            showDialogue("The credenza unlocks! You found items inside.");
        }
    }
    
    // Portrait Puzzle Logic
    private void checkPortraitPuzzle() {
        Puzzle puzzle = gameManager.getCurrentPuzzle();
        if (puzzle instanceof PortraitEyesPuzzle) {
            PortraitEyesPuzzle portraitPuzzle = (PortraitEyesPuzzle) puzzle;
            
            boolean solved = gameManager.solvePuzzle(null);
            if (solved) {
                showDialogue("The portraits align! You hear a click from the safe.");
                unlockSafe();
            }
        }
    }
    
    private void unlockSafe() {
        Room room = gameManager.getCurrentRoom();
        for (InteractiveObject obj : room.getObjects()) {
            if (obj.getObjectId().equals("hidden_safe")) {
                ContainerObject safe = (ContainerObject) obj;
                safe.unlock();
            }
        }
    }
    
    // Dialogue System
    private void showDialogue(String text) {
        showingDialogue = true;
        currentDialogue = new ArrayList<>();
        currentDialogue.add(text);
        dialogueIndex = 0;
    }
    
    private void showDialogue(List<String> lines) {
        showingDialogue = true;
        currentDialogue = new ArrayList<>(lines);
        dialogueIndex = 0;
    }
    
    private void advanceDialogue() {
        dialogueIndex++;
        if (dialogueIndex >= currentDialogue.size()) {
            closeDialogue();
        }
    }
    
    private void closeDialogue() {
        showingDialogue = false;
        currentDialogue = null;
        dialogueIndex = 0;
    }
    
    // Inventory Management
    private void handleInventoryClick(double x, double y) {
        // Simple inventory bar at bottom
        int slotWidth = 60;
        int slotSpacing = 10;
        int startX = 10;
        
        List<String> inventory = gameManager.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            int slotX = startX + i * (slotWidth + slotSpacing);
            if (x >= slotX && x <= slotX + slotWidth) {
                selectedInventoryIndex = i;
                break;
            }
        }
    }
    
    private void toggleInventory() {
        // In full version, this would open detailed inventory view
        gameManager.printStatus();
    }
    
    // Rendering
    public void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }
                
                long elapsed = now - lastUpdate;
                if (elapsed >= 16_666_666) { // ~60 FPS
                    update();
                    render();
                    lastUpdate = now;
                }
            }
        };
        gameLoop.start();
    }
    
    private void update() {
        gameManager.updateTimer();
        
        if (gameManager.isGameOver()) {
            gameLoop.stop();
        }
    }
    
    private void render() {
        // Clear screen
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, screenWidth, screenHeight);
        
        if (gameManager.isGameOver()) {
            drawGameOver();
            return;
        }
        
        // Draw room background
        drawRoomBackground();
        
        // Draw interactive objects
        drawObjects();
        
        // Draw UI overlays
        drawTimer();
        drawInventory();
        
        // Draw dialogue if showing
        if (showingDialogue && currentDialogue != null) {
            drawDialogue();
        }
        
        // Draw object highlight
        if (selectedObject != null) {
            drawObjectHighlight(selectedObject);
        }
    }
    
    private void drawRoomBackground() {
        Room room = gameManager.getCurrentRoom();
        if (room != null) {
            // Draw room name
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 32));
            gc.setFill(Color.WHITE);
            gc.fillText(room.getName(), 20, 40);
        }
    }
    
    private void drawObjects() {
        Room room = gameManager.getCurrentRoom();
        if (room != null) {
            for (InteractiveObject obj : room.getObjects()) {
                obj.draw(gc);
                
                // Draw object name on hover
                if (obj == selectedObject) {
                    gc.setFont(Font.font("Arial", 16));
                    gc.setFill(Color.YELLOW);
                    gc.fillText(obj.getName(), obj.getX(), obj.getY() - 10);
                }
            }
        }
    }
    
    private void drawTimer() {
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        gc.setFill(Color.RED);
        String timeText = "Time: " + gameManager.getTimeRemainingFormatted();
        gc.fillText(timeText, screenWidth - 120, 30);
    }
    
    private void drawInventory() {
        int slotSize = 60;
        int slotSpacing = 10;
        int startX = 10;
        int startY = screenHeight - 70;
        
        List<String> inventory = gameManager.getInventory();
        
        for (int i = 0; i < inventory.size(); i++) {
            int x = startX + i * (slotSize + slotSpacing);
            
            // Draw slot background
            if (i == selectedInventoryIndex) {
                gc.setFill(Color.YELLOW);
            } else {
                gc.setFill(Color.DARKGRAY);
            }
            gc.fillRect(x, startY, slotSize, slotSize);
            
            // Draw item (placeholder - would use actual sprite)
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", 10));
            gc.fillText(inventory.get(i).substring(0, Math.min(8, inventory.get(i).length())), 
                       x + 5, startY + 30);
        }
        
        // Draw evidence counter
        gc.setFont(Font.font("Arial", 16));
        gc.setFill(Color.LIGHTBLUE);
        gc.fillText("Evidence: " + gameManager.getEvidenceCount() + "/3", 10, 30);
    }
    
    private void drawDialogue() {
        if (currentDialogue == null || dialogueIndex >= currentDialogue.size()) {
            return;
        }
        
        // Dialogue box
        int boxX = 50;
        int boxY = screenHeight - 150;
        int boxWidth = screenWidth - 100;
        int boxHeight = 100;
        
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 10, 10);
        
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        gc.strokeRoundRect(boxX, boxY, boxWidth, boxHeight, 10, 10);
        
        // Dialogue text
        gc.setFont(Font.font("Arial", 18));
        gc.setFill(Color.WHITE);
        String text = currentDialogue.get(dialogueIndex);
        gc.fillText(text, boxX + 20, boxY + 40);
        
        // Continue indicator
        gc.setFont(Font.font("Arial", 14));
        gc.fillText("Press ENTER to continue...", boxX + 20, boxY + boxHeight - 20);
    }
    
    private void drawObjectHighlight(InteractiveObject obj) {
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(2);
        gc.strokeRect(obj.getX() - 5, obj.getY() - 5, 
                     obj.getX() + 69, obj.getY() + 69);
    }
    
    private void drawGameOver() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, screenWidth, screenHeight);
        
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        gc.setFill(Color.RED);
        
        String endingTitle = "";
        switch(gameManager.getEndingType()) {
            case "WIN":
                gc.setFill(Color.GREEN);
                endingTitle = "VICTORY";
                break;
            case "PARTIAL_WIN":
                gc.setFill(Color.YELLOW);
                endingTitle = "ESCAPED";
                break;
            case "FRAMED":
                gc.setFill(Color.ORANGE);
                endingTitle = "FRAMED";
                break;
            case "FAIL":
                gc.setFill(Color.RED);
                endingTitle = "GAME OVER";
                break;
        }
        
        gc.fillText(endingTitle, screenWidth/2 - 150, screenHeight/2);
        
        gc.setFont(Font.font("Arial", 20));
        gc.fillText("Press ESC to exit", screenWidth/2 - 100, screenHeight/2 + 50);
    }
}