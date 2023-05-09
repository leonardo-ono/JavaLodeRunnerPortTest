package test;

/**
 * 
 * @author Leo
 */
public class LodeRunner {

    public static final int SPLASH = 21;

    // Tile definitions
    public static final int TILE_BRICK  = 64;
    public static final int TILE_BLOCK  = 65;
    public static final int TILE_TRAP   = 66;
    public static final int TILE_LADDER = 67;
    public static final int TILE_HIDDEN = 68;
    public static final int TILE_ROPE   = 69;
    public static final int TILE_GOLD   = 70;
    public static final int TILE_GROUND = 71;
    public static final int TILE_DIG_LEFT_U1 = 72;
    public static final int TILE_DIG_LEFT_L1 = 73;
    public static final int TILE_DIG_LEFT_U2 = 74;
    public static final int TILE_DIG_LEFT_L2 = 75;
    public static final int TILE_DIG_LEFT_U3 = 76;
    public static final int TILE_DIG_LEFT_L3 = 77;
    public static final int TILE_DIG_LEFT_U4 = 78;
    public static final int TILE_DIG_LEFT_L4 = 79;
    public static final int TILE_DIG_LEFT_U5 = 80;
    public static final int TILE_DIG_LEFT_L5 = 81;
    public static final int TILE_DIG_LEFT_U6 = 82;
    public static final int TILE_DIG_LEFT_L6 = 83;

    public static final int TILE_DIG_RIGHT_U1 = 84;
    public static final int TILE_DIG_RIGHT_L1 = 85;
    public static final int TILE_DIG_RIGHT_U2 = 86;
    public static final int TILE_DIG_RIGHT_L2 = 87;
    public static final int TILE_DIG_RIGHT_U3 = 88;
    public static final int TILE_DIG_RIGHT_L3 = 89;
    public static final int TILE_DIG_RIGHT_U4 = 90;
    public static final int TILE_DIG_RIGHT_L4 = 91;
    public static final int TILE_DIG_RIGHT_U5 = 92;
    public static final int TILE_DIG_RIGHT_L5 = 93;
    public static final int TILE_DIG_RIGHT_U6 = 94;
    public static final int TILE_DIG_RIGHT_L6 = 95;
    public static final int TILE_REGEN1 = 96;
    public static final int TILE_REGEN2 = 97;
    public static final int TILE_REGEN3 = 98;

    public static final int TILE_BLANK  = 32;
    public static final int TILE_GUARD  = 36;
    public static final int TILE_RUNNER = 37;

    // Level definitions
    public static final int LEVEL_COUNT = 0xa000;
    public static final int LEVEL_BASE  = 0xa002;
    public static final int LEVEL_ROW_OFFSET = 28;
    public static final int LEVEL_ROW_BANK_OFFSET = 14;
    public static final int LEVEL_ROW_COUNT = 16;
    public static final int LEVEL_OFFSET = 224;

    // Sprite images
    public static final int RUNNER_1        = 0; //0x1e000;
    public static final int RUNNER_2        = 1; //0x1e020;
    public static final int RUNNER_3        = 2; //0x1e040;
    public static final int RUNNER_CLIMB_1  = 3; //0x1e060;
    public static final int RUNNER_CLIMB_2  = 4; //0x1e080;
    public static final int RUNNER_FALLING  = 5; //0x1e0a0;
    public static final int RUNNER_RAPPEL_1 = 6; //0x1e0c0;
    public static final int RUNNER_RAPPEL_2 = 7; //0x1e0e0;
    public static final int RUNNER_RAPPEL_3 = 8; //0x1e100;
    public static final int GUARD_1         = 9; //0x1e120;
    public static final int GUARD_2         = 10; //0x1e140;
    public static final int GUARD_3         = 11; //0x1e160;
    public static final int GUARD_CLIMB_1   = 12; //0x1e180;
    public static final int GUARD_CLIMB_2   = 13; //0x1e1a0;
    public static final int GUARD_FALLING   = 14; //0x1e1c0;
    public static final int GUARD_RAPPEL_1  = 15; //0x1e1e0;
    public static final int GUARD_RAPPEL_2  = 16; //0x1e200;
    public static final int GUARD_RAPPEL_3  = 17; //0x1e220;

    public static final int MAX_GUARDS = 6;

    // Actions
    public static final int ACT_UNKNOWN  = -1;
    public static final int ACT_STOP    = 0;
    public static final int ACT_LEFT    = 1;
    public static final int ACT_RIGHT   = 2;
    public static final int ACT_UP      = 3;
    public static final int ACT_DOWN    = 4;
    public static final int ACT_FALL    = 5;
    public static final int ACT_FALL_BAR    = 6;
    public static final int ACT_DIG_LEFT    = 7;
    public static final int ACT_DIG_RIGHT   = 8;
    public static final int ACT_DIGGING     = 9;
    public static final int ACT_IN_HOLE     = 10;
    public static final int ACT_CLIMB_OUT   = 11;
    public static final int ACT_REBORN      = 12;
    public static final int ACT_START       = 13;

    // Score increments
    public static final int SCORE_GET_GOLD      = 250;
    public static final int SCORE_IN_HOLE       = 75;
    public static final int SCORE_GUARD_DEAD    = 75;
    public static final int SCORE_COMPLETE_LEVEL    = 1500;
    public static final int SCORE_COUNT = 50;
    public static final int SCORE_INCREMENT = (SCORE_COMPLETE_LEVEL / SCORE_COUNT);

    // Hole regeneration timixng
    public static final int HOLE_REGEN1     = 490;
    public static final int HOLE_REGEN2     = 498;
    public static final int HOLE_REGEN3     = 506;
    public static final int HOLE_REGEN4     = 514;

    // Game states
    public static final int GAME_SPLASH = 0;
    public static final int GAME_RUNNING = 1;
    public static final int GAME_NEW_LEVEL = 2;
    public static final int GAME_NEXT_LEVEL = 3;
    public static final int GAME_PREV_LEVEL = 4;
    public static final int GAME_RUNNER_DEAD = 5;
    public static final int GAME_FINISH = 6;
    public static final int GAME_WIN = 7;
    public static final int GAME_OVER = 8;
    public static final int GAME_FINISH_SCORE_COUNT = 9;

    // God mode (immortal runner)
    public static final int GOD_MODE = 1;
    public static final int MORTAL = 0;

    // Declarations of all functions

    // Loading resources from .BIN files - loader.c
    //extern int loadFiles();

    // Screen configuration and tile set/get - screen.c
    //extern int screenConfig();
    //extern void screenReset();
    //extern void setTileOffsets(uint8_t x, uint8_t y);
    //extern void setTile(uint8_t x, uint8_t y, uint8_t tile, uint8_t paletteOffset);
    //extern uint8_t getTile(uint8_t x, uint8_t y);
    //extern uint8_t getTileXY (uint16_t x, uint16_t y);
    //extern uint8_t getTileBelowXY (uint16_t x, uint16_t y);

    //extern void splash(void);
    
}
