package gameExec;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import gameExec.Dialogue;

public class UI {
	Dialogue di;
	GameUi gp;
	Graphics2D g2;
	Font arial_40, arial_80B;
	public boolean messageOn = false;
	public String message = "";
	public boolean gameFinished = false;
	public String currentText = "Welcome to the Escape Room";
	String[] dialogues = new String[20];
	int currentDialogueIndex = 0;

	
	
	public void draw(Graphics2D g2) {
		this.g2 = g2;
		g2.setFont(arial_40);
		g2.setColor(Color.white);
		
		if(gp.gameState == gp.playState) {
			
		}
		if(gp.gameState == gp.dialogueState) {
			drawDialogueScreen();
		}
		if(gp.gameState == gp.pauseState) {
			//drawPauseMenu();
			drawPauseScreen();
		}
	}
	public void setDialogue() {
	    dialogues[0] = "Welcome to the Escape Room";
	    dialogues[1] = "Make sure to have fun";
	    dialogues[2] = "Now go and escape!";
	    dialogues[3] = "Ciao!";
	    currentDialogueIndex = 0; // reset when starting dialogue
	    currentText = dialogues[currentDialogueIndex];
	}

	public String getLevel() {
		return "1";
		
	}
	
	public void drawDialogueScreen() {
		
		int x = gp.tileSize*2;
		int y = gp.tileSize/2;
		int width = gp.screenWidth - (gp.tileSize*4);
		int height = gp.tileSize*4;
		drawSubWindow(x, y, width, height);
		
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN,32F));
		x += gp.tileSize;
		y += gp.tileSize;
		g2.drawString(currentText, x, y);
	}
	public void drawPauseScreen() {
		int x = gp.tileSize * 4;
		int y = gp.tileSize / 2;
		int width = 30;
		int height = 50;
		drawSubWindow(x,y, 350, 500);
		
		x += gp.tileSize;
		y += gp.tileSize;

		g2.setFont(arial_40);
		g2.drawString("Pause Menu", x, y);
		
		g2.setFont(arial_40);
		g2.drawString("Current Level:" + getLevel(), x, y*4);
		
		
	}
	private void drawSubWindow(int x, int y, int width, int height) {
		Color c = new Color(0,0,0);
		g2.setColor(Color.black);
		g2.fillRoundRect(x,  y,  width,  height,  35,  35);
		
		c = new Color(255,255,255);
		g2.setColor(c);
		g2.setStroke(new BasicStroke(5));
		
	}
	
	public void drawPauseMenu() {
		
		int x = 0;
		int y = 0;
		
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN,32F));
		x += gp.tileSize*2;
		y += gp.tileSize;

		
		g2.setFont(arial_40);
		g2.drawString("Paused", x, y);
	}
	
	public UI(GameUi gp) {
		this.gp = gp;
		arial_40 = new Font("Arial", Font.PLAIN, 40);
	}
	
}
