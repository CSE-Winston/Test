package com.escape.gameExec;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;

public class Entity {
    public int worldX, worldY;
    public int speed;
    
    public String direction;
    public Rectangle2D solidArea;
    
    public Image u1, u2, d1, d2, l1, l2, r1, r2;
    public boolean collisionOn = false;
}