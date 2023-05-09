package test;

import static test.LodeRunner.*;
import static test.Runner.setTile;

/**
 * 
 * @author Leo
 */
public class Levels {

    // World definitions - starting bank number for each world
    public static final int WORLD_CLASSIC   = 1;
    public static final int WORLD_CHAMP     = 6;
    public static final int WORLD_PRO       = 8;
    public static final int WORLD_FANBOOK   = 13;
    public static final int WORLD_REVENGE   = 15;
    public static final int WORLD_CUSTOM    = 16;

    public static final int WORLD_IDX_CLASSIC     = 0;
    public static final int WORLD_IDX_CHAMP       = 1;
    public static final int WORLD_IDX_PRO         = 2;
    public static final int WORLD_IDX_FANBOOK     = 3;
    public static final int WORLD_IDX_REVENGE     = 4;
    public static final int WORLD_IDX_CUSTOM      = 5;
    public static final int MAX_WORLD       = 6;

    public static final int NO_OF_TILES_X   = 28;
    public static final int NO_OF_TILES_Y   = 16;

    public static final int MAX_TILE_X  = 27;
    public static final int MAX_TILE_Y  = 15;

    // X/Y Offsets to center the screen
    public static final int X_OFFSET    = 6;
    public static final int Y_OFFSET    = 6;

    // Tile dimensions
    public static final int TILE_H  = 8;
    public static final int TILE_W  = 8;

    // Half tile dimensions
    public static final int W2  = 4;
    public static final int H2  = 4;

    // Quarter tile dimensions
    public static final int W4  = 2;
    public static final int H4  = 2;

    // Struct defining each tile of the map
    public static class map_t {
        
        // Default tile value as stored in banked RAM
        public int base;
        
        // Current tile value based on game play
        public int act;
        
    }

    // Struct defining hole being dug by the runner
    public static class dig_t {
        // Is hole being dug?
        public int action;
        // x/y location of hole
        public int x;
        public int y;
        // Count of times dig has been checked from processDigHole()
        public int idx;
    }

    // Struct defining each hole dug by the runner
    public static class hole_t {
        
        // Is hole "active"
        public boolean active;
        // x/y location of hole 
        public int x;
        public int y;

        public int pad;
        // Count of times hole has been checked from processFillHole()
        public int count;
        
    }
    
    public static final int MAX_HOLES = 30;

    // Struct defining state for the current level
    public static class level_t {
        // Count of gold pieces remaining
        public int goldCount;
        // Flag that gold collection is complete
        public boolean goldComplete;
    }

    // Struct defining state for the current game
    public static class game_t {
        
        // Score
        public int currentScore;
        // Game state
        public int gameState;
        // Lives
        public int lives;
        // World
        public int world;
        // Level
        public int level;
        // God mode indicator
        public int godMode;
        // Speed selection
        public int speed;
        // Sound selection
        public int sound;
        // Max level info
        public int[] maxLevels = new int[MAX_WORLD];
    }

    // Map of data for the current level
    public static map_t[][] map = new map_t[NO_OF_TILES_X + 1][NO_OF_TILES_Y + 1];

    // State information for holes dug by the runner
    public static hole_t[] holes = new hole_t[MAX_HOLES];

    // State information for hole being dug by the runner
    public static dig_t hole = new dig_t();

    // State information for current level
    public static level_t currentLevel = new level_t();
    
    // State information for current game
    public static game_t currentGame = new game_t();

    static {
        currentGame.level = 0;              // Level <----------------------?repete
        currentGame.gameState = GAME_NEW_LEVEL; // Game state
        currentGame.lives = 5;              // Lives
        currentGame.world = WORLD_CLASSIC;  // World
        currentGame.level = 1;              // Level <----------------------?repete
        currentGame.godMode = MORTAL;          // God mode
        
        for (int i = 0; i < holes.length; i++) {
            holes[i] = new hole_t();
        }
    }   
    
    public static void initLevels() {    
//        VIA1.pra = WORLD_CLASSIC;
//        currentGame.maxLevels[WORLD_IDX_CLASSIC] = *(int *)(LEVEL_COUNT + 1);
//        printf("Max=%d\n",currentGame.maxLevels[WORLD_IDX_CLASSIC]);
//        VIA1.pra = WORLD_CHAMP;
//        currentGame.maxLevels[WORLD_IDX_CHAMP] = *(int *)(LEVEL_COUNT + 1);
//        printf("Max=%d\n",currentGame.maxLevels[WORLD_IDX_CHAMP]);
//        VIA1.pra = WORLD_PRO;
//        currentGame.maxLevels[WORLD_IDX_PRO] = *(int *)(LEVEL_COUNT + 1);
//        printf("Max=%d\n",currentGame.maxLevels[WORLD_IDX_PRO]);
//        VIA1.pra = WORLD_FANBOOK;
//        currentGame.maxLevels[WORLD_IDX_FANBOOK] = *(int *)(LEVEL_COUNT + 1);
//        printf("Max=%d\n",currentGame.maxLevels[WORLD_IDX_FANBOOK]);
//        VIA1.pra = WORLD_REVENGE;
//        currentGame.maxLevels[WORLD_IDX_REVENGE] = *(int *)(LEVEL_COUNT + 1);
//        printf("Max=%d\n",currentGame.maxLevels[WORLD_IDX_REVENGE]);
//        VIA1.pra = WORLD_CUSTOM;
//        currentGame.maxLevels[WORLD_IDX_CUSTOM] = *(int *)(LEVEL_COUNT + 1);
//        printf("Max=%d\n",currentGame.maxLevels[WORLD_IDX_CUSTOM]);
    }

    public static void completeLevel() {
        System.out.println("all gold collected !");
        int row = 0;
        int col = 0;

        for (row = 0; row < LEVEL_ROW_COUNT; row++) {
            for (col = 0; col < LEVEL_ROW_OFFSET; col++) {
                if (map[col][row].base == TILE_HIDDEN) {
                    map[col][row].base = TILE_LADDER;
                    map[col][row].act = TILE_LADDER;
                    setTile(col,row,TILE_LADDER,0);
                }
            }
        }
        currentLevel.goldComplete = true;
    }
    
}
