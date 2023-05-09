package test;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 
 * @author Leo
 */
public class Resource {
    
    public static final int SPRITE_SIZE = 8;
    public static final int TILE_SIZE = 8;
    
    public static final int MAP_COLS = 28;
    public static final int MAP_ROWS = 16;
    
    public static final List<BufferedImage> sprites = new ArrayList<>();
    public static final List<BufferedImage> tiles = new ArrayList<>();
    public static final List<Level> levels = new ArrayList<>();
    
    public static void extractSprites() {
        try (
            Scanner sc = new Scanner(Resource.class.getResourceAsStream("/res/sprites.txt"));
        ) {
            BufferedImage sprite = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
            int spriteY = 0;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.isEmpty() || line.startsWith("#")) continue;
                
                System.out.println("line: " + line);
                for (int spriteX = 0; spriteX < 8; spriteX++) {
                    char c = line.charAt(spriteX);
                    int spriteColor = switch (c) {  case '1' -> 0xff0000ff;
                                                    case '2' -> 0xff00ff00;
                                                    case '3' -> 0xffff0000;
                                                    default -> 0x00000000; };
                    
                    sprite.setRGB(spriteX, spriteY, spriteColor);
                }
                spriteY++;
                if (spriteY == 8) {
                    sprites.add(sprite);
                    sprite = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
                    spriteY = 0;
                }
            }
        }
    }

    public static void extractTiles() {
        try (
            Scanner sc = new Scanner(Resource.class.getResourceAsStream("/res/tiles.txt"));
        ) {
            BufferedImage tile = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
            int tileY = 0;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.isEmpty() || line.startsWith("#")) continue;
                
                System.out.println("line: " + line);
                for (int tileX = 0; tileX < 8; tileX++) {
                    char c = line.charAt(tileX);
                    int spriteColor = switch (c) {  case '1' -> 0xff0000ff;
                                                    case '2' -> 0xffffffff;
                                                    case '3' -> 0xffff0000;
                                                    default -> 0x00000000; };
                    
                    tile.setRGB(tileX, tileY, spriteColor);
                }
                tileY++;
                if (tileY == 8) {
                    tiles.add(tile);
                    tile = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
                    tileY = 0;
                }
            }
        }
    }

    public static void extractLevels() {
        try (
            Scanner sc = new Scanner(Resource.class.getResourceAsStream("/res/levels.txt"));
        ) {
            int[][] drawMap = new int[MAP_ROWS][MAP_COLS];
            int mapY = 0;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.isEmpty() || line.startsWith(";")) continue;
                
                System.out.println("line: " + line);
                for (int mapX = 0; mapX < MAP_COLS; mapX++) {
                    char c = line.charAt(mapX);
                    int tileIndex = switch (c) {    case ' ' -> 0; //    # Blank
                                                    case '0' -> 1; //    # Guard (marker only)
                                                    case '&' -> 2; //    # Runner (marker only)
                                                    case '#' -> 3; //    # Brick
                                                    case '@' -> 4; //    # Block
                                                    case 'X' -> 5; //    # Trap (displays as brick)
                                                    case 'H' -> 6; //    # Ladder
                                                    case 'S' -> 7; //    # Hidden ladder (displays as blank)
                                                    case '-' -> 8; //    # Rope
                                                    case '$' -> 9; //    # Gold
                                                    default -> 0; };
                    
                    drawMap[mapY][mapX] = tileIndex;
                }
                mapY++;
                if (mapY == MAP_ROWS) {
                    Level level = new Level(drawMap);
                    levels.add(level);
                    drawMap = new int[MAP_ROWS][MAP_COLS];
                    mapY = 0;
                }
            }
        }
    }
                
}
