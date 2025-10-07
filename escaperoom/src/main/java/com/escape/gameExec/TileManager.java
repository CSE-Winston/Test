package com.escape.gameExec;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TileManager {
    GameUi gp;
    public Tile[] tile;
    public int mapTileNum[][];
    
    public TileManager(GameUi gp) {
        this.gp = gp;
        tile = new Tile[10];
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        
        getTileImage();
        loadMap("/maps/map2.txt");
    }
    
    public void getTileImage() {
        try {
            tile[0] = new Tile();
            tile[0].image = new Image(getClass().getResourceAsStream("/tiles/newgrass.png"));
            tile[0].collision = false;
            
            tile[1] = new Tile();
            tile[1].image = new Image(getClass().getResourceAsStream("/tiles/purble.png"));
            tile[1].collision = false;
            
            tile[2] = new Tile();
            tile[2].image = new Image(getClass().getResourceAsStream("/tiles/goldblock.png"));
            tile[2].collision = false;
            
            tile[3] = new Tile();
            tile[3].image = new Image(getClass().getResourceAsStream("/tiles/barrier.png"));
            tile[3].collision = true;
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void loadMap(String mapPath) {
        try {
            InputStream is = getClass().getResourceAsStream(mapPath);
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
            br.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void draw(GraphicsContext gc) {
        for (int worldCol = 0; worldCol < gp.maxWorldCol; worldCol++) {
            for (int worldRow = 0; worldRow < gp.maxWorldRow; worldRow++) {
                int tileNum = mapTileNum[worldCol][worldRow];
                
                int worldX = worldCol * gp.tileSize;
                int worldY = worldRow * gp.tileSize;
                int screenX = worldX - gp.player.worldX + gp.player.screenX;
                int screenY = worldY - gp.player.worldY + gp.player.screenY;
                
                if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
                    worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                    worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
                    worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
                    
                    gc.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize);
                }
            }
        }
    }
}