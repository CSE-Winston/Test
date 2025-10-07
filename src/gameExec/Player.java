package gameExec;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import gameExec.KeyHandler;

public class Player extends Entity {
	GameUi gp;
	KeyHandler keyH;
	
	public final int screenX;
	public final int screenY;
	


	public Player(GameUi gp, KeyHandler keyH) {
		this.gp = gp;
		this.keyH = keyH;
		
		screenX = gp.screenWidth/2 - (gp.tileSize/2);
		screenY = gp.screenHeight/2 - (gp.tileSize/2);
		
		solidArea = new Rectangle();
		solidArea.x = 0;
		solidArea.y = 0;
		solidArea.width = gp.tileSize - 16;
		solidArea.height = gp.tileSize - 16;
		
		setDefaultValues();
	}
	public void setDefaultValues() {
		worldX = gp.tileSize * 23;
		worldY = gp.tileSize * 23;
		speed = 4;
		direction = "down";
		updateSprite();
	}
	
	public void updateSprite() {
		try {
			u1 = ImageIO.read(getClass().getResourceAsStream("/images/player.png"));
			u2 = ImageIO.read(getClass().getResourceAsStream("/images/player.png"));
			d1 = ImageIO.read(getClass().getResourceAsStream("/images/player.png"));
			d2 = ImageIO.read(getClass().getResourceAsStream("/images/player.png"));
			l1 = ImageIO.read(getClass().getResourceAsStream("/images/player.png"));
			l2 = ImageIO.read(getClass().getResourceAsStream("/images/player.png"));
			r1 = ImageIO.read(getClass().getResourceAsStream("/images/player.png"));
			r2 = ImageIO.read(getClass().getResourceAsStream("/images/player.png"));
			
		} catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	public void update() {
		
		if(keyH.upPressed == true) {
			
			direction = "up";
		} else if(keyH.downPressed == true) {
			
			direction = "down";
		} else if(keyH.leftPressed == true) {
			
			direction = "left";
		} else if(keyH.rightPressed == true) {
				
			direction = "right";
		}
		collisionOn = false;
		gp.cHandler.checkTile(this);
		
		if(collisionOn == false) {
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
	
	public void draw(Graphics2D g2) {
		BufferedImage image = null;
		
		switch(direction) {
		case "up":
			image = u1;
		default:
			image = u1;
		}
		g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
	}
}
