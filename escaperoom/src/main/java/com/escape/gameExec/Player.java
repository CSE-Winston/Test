package com.escape.gameExec;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.geometry.Rectangle2D;

public class Player extends Entity {
    GameUi gp;
    KeyHandler keyH;
    
    public final int screenX;
    public final int screenY;
    
    // JavaFX Images instead of BufferedImage
    private Image u1, u2, d1, d2, l1, l2, r1, r2;
    
    public Player(GameUi gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        
        screenX = gp.screenWidth/2 - (gp.tileSize/2);
        screenY = gp.screenHeight/2 - (gp.tileSize/2);
        
        solidArea = new Rectangle2D(0, 0, gp.tileSize - 16, gp.tileSize - 16);
        
        setDefaultValues();
        loadSprites();
    }
    
    public void setDefaultValues() {
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 23;
        speed = 4;
        direction = "down";
    }
    
    public void loadSprites() {
        try {
            // Load images using JavaFX Image class
            String imagePath = "/images/player.png";
            u1 = new Image(getClass().getResourceAsStream(imagePath));
            u2 = new Image(getClass().getResourceAsStream(imagePath));
            d1 = new Image(getClass().getResourceAsStream(imagePath));
            d2 = new Image(getClass().getResourceAsStream(imagePath));
            l1 = new Image(getClass().getResourceAsStream(imagePath));
            l2 = new Image(getClass().getResourceAsStream(imagePath));
            r1 = new Image(getClass().getResourceAsStream(imagePath));
            r2 = new Image(getClass().getResourceAsStream(imagePath));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void update() {
        if(keyH.upPressed) {
            direction = "up";
        } else if(keyH.downPressed) {
            direction = "down";
        } else if(keyH.leftPressed) {
            direction = "left";
        } else if(keyH.rightPressed) {
            direction = "right";
        }
        
        collisionOn = false;
        gp.cHandler.checkTile(this);
        
        if(!collisionOn) {
            switch(direction) {
                case "up":
                    worldY -= speed;
                    break;
                case "down":
                    worldY += speed;
                    break;
                case "left":
                    worldX -= speed;
                    break;
                case "right":
                    worldX += speed;
                    break;
            } 
        }
    }
    
    public void draw(GraphicsContext gc) {
        Image image = null;
        
        switch(direction) {
            case "up":
                image = u1;
                break;
            case "down":
                image = d1;
                break;
            case "left":
                image = l1;
                break;
            case "right":
                image = r1;
                break;
            default:
                image = u1;
        }
        
        gc.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize);
    }
}