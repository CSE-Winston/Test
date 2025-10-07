package gameExec;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class GameUi extends JPanel implements Runnable{
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
	int FPS = 60;
	
	KeyHandler keyH = new KeyHandler(this);
	
	CollisionHandler cHandler = new CollisionHandler(this);
	
	TileManager tileM = new TileManager(this);
	Thread gameThread;
	
	public Player player = new Player(this,keyH);
	
	public UI ui = new UI(this);
	
	int playerX = 100;
	int playerY = 100;
	int playerSpeed = 4;
	
	public int gameState;
	public int playState = 1;
	public int pauseState = 2;
	public int dialogueState = 3;

			
	public GameUi () {
		
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.setFocusable(true);
	}
	
	public void setupGame( ) {
		ui.setDialogue();
		gameState = dialogueState;
	}
	public void startGameThread() {
		gameThread = new Thread(this);
		gameThread.start();
	}
	@Override
	public void run() {
		double drawInterval = 1000000000/FPS;
		double nextDrawTime = System.nanoTime() + drawInterval;
		while(gameThread != null) {
			
			update();
			
			repaint();
			
			
			
			try {
				double remainingTime = nextDrawTime - System.nanoTime();
				remainingTime = remainingTime/1000000;
				if(remainingTime < 0) {
					remainingTime = 0;
				}
				Thread.sleep((long) remainingTime);
				
				nextDrawTime += drawInterval;
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public void update() {
		if (gameState == playState) {
			player.update();
		}
		if (gameState == dialogueState) {
			
		}
		if (gameState == pauseState) {
			
		}
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		
		
		tileM.draw(g2);
		player.draw(g2);
		ui.draw(g2);
		g2.dispose();
	}
}
