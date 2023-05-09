package test;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import static test.Guard.clearGuards;
import static test.Guard.initGuard;
import static test.Levels.NO_OF_TILES_X;
import static test.Levels.NO_OF_TILES_Y;
import static test.Levels.currentLevel;
import static test.Levels.map;
import static test.LodeRunner.*;
import static test.Resource.MAP_COLS;
import static test.Resource.MAP_ROWS;
import static test.Resource.TILE_SIZE;
import static test.Runner.*;

/**
 * 
 * @author Leo
 */
public class Level {

    public static int[][] drawMap;

    public Level(int[][] drawMap) {
        Level.drawMap = drawMap;
        
        clearRunner();
        clearGuards();

        currentLevel.goldCount = 0;
        currentLevel.goldComplete = false;
        
        for (int row = 0; row < drawMap.length; row++) {
            int[] cols = drawMap[row];
            for (int col = 0; col < cols.length; col++) {
                int tile = cols[col];
                
                // tile = 1 -> guard
                if (tile == 1) {
                    initGuard(col, row);
                    tile = 0;
                }
                
                // tile = 2 -> runner / player
                if (tile == 2) {
                    initRunner(col, row);
                    tile = 0;
                }
                
                if (tile == 9) {
                    currentLevel.goldCount++;
                }
                
                int tileId =  switch (tile) {   case 0 -> TILE_BLANK; //    # Blank
                                                case 1 -> TILE_BLANK; //    # Guard (marker only)
                                                case 2 -> TILE_BLANK; //    # Runner (marker only)
                                                case 3 -> TILE_BRICK; //    # Brick
                                                case 4 -> TILE_BLOCK; //    # Block
                                                case 5 -> TILE_TRAP; //    # Trap (displays as brick)
                                                case 6 -> TILE_LADDER; //    # Ladder
                                                case 7 -> TILE_HIDDEN; //    # Hidden ladder (displays as blank)
                                                case 8 -> TILE_ROPE; //    # Rope
                                                case 9 -> TILE_GOLD; //    # Gold
                                                default -> 0; };
                map[col][row] = new Levels.map_t();
                map[col][row].base = tileId;
                map[col][row].act = tileId;
                
                drawMap[row][col] = tileId;
            }
        }

        for (int col = 0; col < NO_OF_TILES_X; col++) {
            map[col][NO_OF_TILES_Y] = new Levels.map_t();
        }
        for (int row = 0; row < NO_OF_TILES_Y; row++) {
            map[NO_OF_TILES_X][row] = new Levels.map_t();
        }
        
    }
    
    public void draw(Graphics2D g) {
        for (int row = 0; row < MAP_ROWS; row++) {
            for (int col = 0; col < MAP_COLS; col++) {
                //int tileIndex = map[col][row].base; // drawMap[row][col] + 61;
                //int tileIndex = drawMap[row][col] + 61;
                int tileIndex = drawMap[row][col];
                BufferedImage tile = Resource.tiles.get(tileIndex);
                g.drawImage(tile, col * TILE_SIZE, row * TILE_SIZE, null);
            }
        }
    }
    
}
