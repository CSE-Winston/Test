package com.escape.gameExec;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.List;

public abstract class InteractiveObject {
    protected String objectId;
    protected String name;
    protected String type;
    protected double x, y;
    protected double width, height;
    protected String sprite;
    protected Image image;
    protected boolean visible;
    protected String description;
    
    public InteractiveObject(String objectId, String name, String type, 
                           double x, double y, String sprite) {
        this.objectId = objectId;
        this.name = name;
        this.type = type;
        this.x = x;
        this.y = y;
        this.sprite = sprite;
        this.visible = true;
        this.width = 64;  // Default size
        this.height = 64;
        loadImage();
    }
    
    protected void loadImage() {
        try {
            if (sprite != null && !sprite.isEmpty()) {
                image = new Image(getClass().getResourceAsStream("/objects/" + sprite));
                if (image.getWidth() > 0) {
                    width = image.getWidth();
                    height = image.getHeight();
                }
            }
        } catch (Exception e) {
            System.err.println("Could not load image: " + sprite);
        }
    }
    
    public abstract void interact();
    public abstract boolean canInteract();
    
    public void draw(GraphicsContext gc) {
        if (visible && image != null) {
            gc.drawImage(image, x, y, width, height);
        }
    }
    
    public boolean contains(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && 
               mouseY >= y && mouseY <= y + height;
    }
    
    // Getters
    public String getObjectId() { return objectId; }
    public String getName() { return name; }
    public String getType() { return type; }
    public double getX() { return x; }
    public double getY() { return y; }
    public boolean isVisible() { return visible; }
    public String getDescription() { return description; }
    
    public void setDescription(String description) { this.description = description; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public void setPosition(double x, double y) { this.x = x; this.y = y; }
}

// Collectible Item
class CollectibleObject extends InteractiveObject {
    private boolean collected;
    private String evidenceValue;
    
    public CollectibleObject(String objectId, String name, double x, double y, String sprite) {
        super(objectId, name, "COLLECTIBLE", x, y, sprite);
        this.collected = false;
    }
    
    @Override
    public void interact() {
        if (!collected) {
            collected = true;
            visible = false;
            System.out.println("Collected: " + name);
        }
    }
    
    @Override
    public boolean canInteract() {
        return !collected && visible;
    }
    
    public boolean isCollected() { return collected; }
    public void setEvidenceValue(String value) { this.evidenceValue = value; }
    public String getEvidenceValue() { return evidenceValue; }
}

// Container (safe, chest, drawer, etc.)
class ContainerObject extends InteractiveObject {
    private boolean locked;
    private String unlockCode;
    private String unlockCondition;
    private List<String> containedItemIds;
    private boolean opened;
    
    public ContainerObject(String objectId, String name, double x, double y, String sprite) {
        super(objectId, name, "CONTAINER", x, y, sprite);
        this.locked = true;
        this.opened = false;
        this.containedItemIds = new ArrayList<>();
    }
    
    @Override
    public void interact() {
        if (locked) {
            System.out.println(name + " is locked.");
        } else if (!opened) {
            opened = true;
            System.out.println("Opened " + name);
        }
    }
    
    @Override
    public boolean canInteract() {
        return visible && !opened;
    }
    
    public boolean tryUnlock(String code) {
        if (unlockCode != null && unlockCode.equals(code)) {
            locked = false;
            return true;
        }
        return false;
    }
    
    public void unlock() {
        locked = false;
    }
    
    public boolean isLocked() { return locked; }
    public boolean isOpened() { return opened; }
    public List<String> getContainedItems() { return new ArrayList<>(containedItemIds); }
    public void addItem(String itemId) { containedItemIds.add(itemId); }
    public void setUnlockCode(String code) { this.unlockCode = code; }
    public void setUnlockCondition(String condition) { this.unlockCondition = condition; }
}

// Cyclic Object (portraits with changing states)
class CyclicObject extends InteractiveObject {
    private List<String> states;
    private int currentStateIndex;
    
    public CyclicObject(String objectId, String name, double x, double y, String sprite) {
        super(objectId, name, "CYCLIC", x, y, sprite);
        this.states = new ArrayList<>();
        this.currentStateIndex = 0;
    }
    
    @Override
    public void interact() {
        currentStateIndex = (currentStateIndex + 1) % states.size();
        System.out.println(name + " changed to: " + getCurrentState());
    }
    
    @Override
    public boolean canInteract() {
        return visible && !states.isEmpty();
    }
    
    public String getCurrentState() {
        if (!states.isEmpty()) {
            return states.get(currentStateIndex);
        }
        return null;
    }
    
    public void addState(String state) { states.add(state); }
    public void setStates(List<String> states) { this.states = new ArrayList<>(states); }
    public void setCurrentState(String state) {
        int index = states.indexOf(state);
        if (index >= 0) {
            currentStateIndex = index;
        }
    }
}

// Draggable Object (for arranging puzzles)
class DraggableObject extends InteractiveObject {
    private boolean beingDragged;
    private double dragOffsetX, dragOffsetY;
    private double originalX, originalY;
    
    public DraggableObject(String objectId, String name, double x, double y, String sprite) {
        super(objectId, name, "DRAGGABLE", x, y, sprite);
        this.originalX = x;
        this.originalY = y;
        this.beingDragged = false;
    }
    
    @Override
    public void interact() {
        // Start dragging
        beingDragged = true;
    }
    
    @Override
    public boolean canInteract() {
        return visible;
    }
    
    public void startDrag(double mouseX, double mouseY) {
        beingDragged = true;
        dragOffsetX = mouseX - x;
        dragOffsetY = mouseY - y;
    }
    
    public void updateDrag(double mouseX, double mouseY) {
        if (beingDragged) {
            x = mouseX - dragOffsetX;
            y = mouseY - dragOffsetY;
        }
    }
    
    public void stopDrag() {
        beingDragged = false;
    }
    
    public void resetPosition() {
        x = originalX;
        y = originalY;
    }
    
    public boolean isBeingDragged() { return beingDragged; }
}

// Examine Object (for clues, notes, etc.)
class ExamineObject extends InteractiveObject {
    private String examineText;
    private boolean examined;
    
    public ExamineObject(String objectId, String name, double x, double y, String sprite) {
        super(objectId, name, "EXAMINE", x, y, sprite);
        this.examined = false;
    }
    
    @Override
    public void interact() {
        examined = true;
        System.out.println(name + ": " + examineText);
    }
    
    @Override
    public boolean canInteract() {
        return visible;
    }
    
    public boolean isExamined() { return examined; }
    public void setExamineText(String text) { this.examineText = text; }
    public String getExamineText() { return examineText; }
}

// Evidence Holder (mannequins in greenhouse)
class EvidenceHolderObject extends InteractiveObject {
    private String correctItemId;
    private String placedItemId;
    private boolean correctlyPlaced;
    
    public EvidenceHolderObject(String objectId, String name, double x, double y, String sprite) {
        super(objectId, name, "EVIDENCE_HOLDER", x, y, sprite);
        this.correctlyPlaced = false;
    }
    
    @Override
    public void interact() {
        System.out.println("Place an evidence token here.");
    }
    
    @Override
    public boolean canInteract() {
        return visible;
    }
    
    public boolean placeItem(String itemId) {
        placedItemId = itemId;
        correctlyPlaced = itemId.equals(correctItemId);
        return correctlyPlaced;
    }
    
    public void removeItem() {
        placedItemId = null;
        correctlyPlaced = false;
    }
    
    public boolean isCorrectlyPlaced() { return correctlyPlaced; }
    public void setCorrectItem(String itemId) { this.correctItemId = itemId; }
    public String getPlacedItem() { return placedItemId; }
}

// Token Slot (for cellar puzzle)
class TokenSlotObject extends InteractiveObject {
    private String correctTokenId;
    private String placedTokenId;
    private int slotNumber;
    
    public TokenSlotObject(String objectId, String name, double x, double y, int slotNum) {
        super(objectId, name, "TOKEN_SLOT", x, y, "slot.png");
        this.slotNumber = slotNum;
    }
    
    @Override
    public void interact() {
        System.out.println("Slot " + slotNumber + " - Place a token here.");
    }
    
    @Override
    public boolean canInteract() {
        return visible && placedTokenId == null;
    }
    
    public boolean placeToken(String tokenId) {
        if (placedTokenId == null) {
            placedTokenId = tokenId;
            return true;
        }
        return false;
    }
    
    public void removeToken() {
        placedTokenId = null;
    }
    
    public boolean isCorrect() {
        return placedTokenId != null && placedTokenId.equals(correctTokenId);
    }
    
    public void setCorrectToken(String tokenId) { this.correctTokenId = tokenId; }
    public String getPlacedToken() { return placedTokenId; }
    public int getSlotNumber() { return slotNumber; }
}