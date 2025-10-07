package gameExec;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

public class TileManager {

	GameUi gp;
	Tile[] tile;
	public int mapTileNum[][];
	
	public TileManager(GameUi gp) {
		mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
		this.gp = gp;
		tile = new Tile[10];
		
		getTileImage();
		loadMap("map2.txt");
		
	}
	
	public void getTileImage() {
		try {
			tile[0] = new Tile();
			tile[0].image = ImageIO.read(getClass().getResourceAsStream("newgrass.png"));
			tile[0].collision = false;
			
			tile[1] = new Tile();
			tile[1].image = ImageIO.read(getClass().getResourceAsStream("purble.png"));
			tile[1].collision = false;
			
			tile[2] = new Tile();
			tile[2].image = ImageIO.read(getClass().getResourceAsStream("goldblock.png"));
			tile[2].collision = false;
			
			tile[3] = new Tile();
			tile[3].image = ImageIO.read(getClass().getResourceAsStream("barrier.png"));
			tile[3].collision = true;
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadMap (String m) {
		try {
		InputStream is = getClass().getResourceAsStream(m);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
		int col = 0;
		int row = 0;
		
		while(col < gp.maxWorldCol && row < gp.maxWorldRow) {
			
			String line = br.readLine();
			
			while(col < gp.maxWorldCol) {
				String numbers[] = line.split(" ");
				
				int num = Integer.parseInt(numbers[col]);
				
				mapTileNum[col][row] = num;
				col++;
			}
			if(col == gp.maxWorldCol) {
				col = 0;
				row++;
				
			}
		}
		}
		 catch(Exception e) {
			 
		 }
				
	}
	public void draw(Graphics2D g2) {
	    for (int worldCol = 0; worldCol < gp.maxWorldCol; worldCol++) {
	        for (int worldRow = 0; worldRow < gp.maxWorldRow; worldRow++) {
	            
	            int tileNum = mapTileNum[worldCol][worldRow];

	            int worldX = worldCol * gp.tileSize;
	            int worldY = worldRow * gp.tileSize;
	            int screenX = worldX - gp.player.worldX + gp.player.screenX;
	            int screenY = worldY - gp.player.worldY + gp.player.screenY;

	            // Only draw tiles if they are visible on screen
	            if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
	                worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
	                worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
	                worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
	                
	                g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
	            }
	        }
	    }
	}

}
