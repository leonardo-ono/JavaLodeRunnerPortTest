package test;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import static test.GlobalState.guard;
import static test.Guard.*;
import static test.Level.drawMap;
import static test.Levels.*;
import static test.LodeRunner.*;
import static test.SoundFX.*;

/**
 * 
 * @author Leo
 */
public class Runner {
    
    // Animation sequences for runner
    public static final int RUN_SEQUENCE = 0;
    public static final int CLIMB_SEQUENCE = 1;
    public static final int RAPPEL_SEQUENCE = 2;
    public static final int FALL_SEQUENCE = 3;
    public static final int MAX_SEQUENCE = 4;

    // Animation sequence lengths
    public static final int RUN_LENGTH = 3;
    public static final int RAPPEL_LENGTH = 3;
    public static final int CLIMB_LENGTH = 2;
    public static final int FALL_LENGTH = 1;
    public static final int MAX_SEQ_LENGTH = 3;


    public static int runnerSequences[][] = {
        // RUN_SEQUENCE
        { RUNNER_1, RUNNER_2, RUNNER_3 },
        // CLIMB_SEQUENCE
        { RUNNER_CLIMB_1, RUNNER_CLIMB_2 },
        // RAPPEL_SEQUENCE
        { RUNNER_RAPPEL_1, RUNNER_RAPPEL_2, RUNNER_RAPPEL_3 },
        // FALL_SEQUENCE
        { RUNNER_FALLING }
    };

    // MAX_SEQUENCE
    public static final int runnerSeqSizes[] = {
        RUN_LENGTH, CLIMB_LENGTH, RAPPEL_LENGTH, FALL_LENGTH
    };
    
    public static final int STATE_OK_TO_MOVE = 1;
    public static final int STATE_FALLING = 2;

    public static final int DIG_LENGTH = 12;
    public static final int DIG_UPPER = 0;
    public static final int DIG_LOWER = 1;

    public static int[][] digLeft = { //[2][DIG_LENGTH]
        // DIG_UPPER
        {TILE_BLANK, TILE_DIG_LEFT_U1, TILE_DIG_LEFT_U1, TILE_DIG_LEFT_U2, TILE_DIG_LEFT_U2, TILE_DIG_LEFT_U3, TILE_DIG_LEFT_U4,
         TILE_DIG_LEFT_U4, TILE_DIG_LEFT_U5, TILE_DIG_LEFT_U6, TILE_DIG_LEFT_U6, TILE_BLANK},
        // DIG_LOWER
        {TILE_BRICK, TILE_DIG_LEFT_L1, TILE_DIG_LEFT_L1, TILE_DIG_LEFT_L2, TILE_DIG_LEFT_L2, TILE_DIG_LEFT_L3, TILE_DIG_LEFT_L4,
         TILE_DIG_LEFT_L4, TILE_DIG_LEFT_L5, TILE_DIG_LEFT_L6, TILE_DIG_LEFT_L6, TILE_BLANK},
    };

    public static int[][] digRight = { // [2][DIG_LENGTH]
        // DIG_UPPER
        {TILE_BLANK, TILE_DIG_RIGHT_U1, TILE_DIG_RIGHT_U1, TILE_DIG_RIGHT_U2, TILE_DIG_RIGHT_U2, TILE_DIG_RIGHT_U3,
         TILE_DIG_RIGHT_U4, TILE_DIG_RIGHT_U4, TILE_DIG_RIGHT_U5, TILE_DIG_RIGHT_U6, TILE_DIG_RIGHT_U6, TILE_BLANK},
        // DIG_LOWER
        {TILE_BRICK, TILE_DIG_RIGHT_L1, TILE_DIG_RIGHT_L1, TILE_DIG_RIGHT_L2, TILE_DIG_RIGHT_L2, TILE_DIG_RIGHT_L3,
         TILE_DIG_RIGHT_L4, TILE_DIG_RIGHT_L4, TILE_DIG_RIGHT_L5, TILE_DIG_RIGHT_L6, TILE_DIG_RIGHT_L6, TILE_BLANK},
    };

    
    public static class runner_t {
        // Runner x/y coordinates in tiles
        public int x;  // 0-27
        public int y;  // 0-15

        // Runner x/y offsets in pixels (-4 .. 4)
        public int xOffset;
        public int yOffset;

        // Action
        public int action;

        // Index for sprite image
        public int idx;

        // Last direction (left/right)
        public int direction;

        // Animation sequence (shape)
        public int sequence;
    };    
    
    public static runner_t runner = new runner_t();

    public static void clearRunner() {
        runner.x = 0;
        runner.y = 0;
        runner.xOffset = 0;
        runner.yOffset = 0;
        runner.action = 0;
        runner.idx = 0;
        runner.direction = 0;
        runner.sequence = 0;
    }
    
    public static void initRunner(int x, int y) {
        runner.x = x;
        runner.y = y;
        runner.xOffset = 0;
        runner.yOffset = 0;
        runner.action = ACT_UNKNOWN;
        runner.idx = 0;
        runner.direction = ACT_RIGHT;
        runner.sequence = RUN_SEQUENCE;
    }

    public static void removeGold(int x, int y) {
        map[x][y].base = TILE_BLANK;
        setTile(x, y, TILE_BLANK, 0);
        //#ifdef DEBUG
        //    displayGold();
        //#endif
    }
    
    public static void addGold(int x, int y) {
        map[x][y].base = TILE_GOLD;
        setTile(x, y, TILE_GOLD, 0);
        //#ifdef DEBUG
        //    displayGold();
        //#endif
    }
    
    public static void decGold() {
        currentLevel.goldCount--;
        if (currentLevel.goldCount <= 0) {
            completeLevel();
            if (runner.y > 0) {
                // TODO: Sound for finishing all gold
            }
        }
        //#ifdef DEBUG
        //    displayGold();
        //#endif
    }

    public static void setRunnerDead() {
        System.out.println("Runner DEAD !");
    }

    private static void runnerMoveStep(int action, boolean stayCurrentPos) {
        int x = runner.x;
        int xOffset = runner.xOffset;
        int y = runner.y;
        int yOffset = runner.yOffset;

        int curToken = 0;
        int nextToken = 0;
        int centerX = 0;
        int centerY = 0;
        //int *imgPtr = NULL;

        // printf("Here w/x=%d y=%d action=%d stayCurrentPos=%d\n",x,y,action,stayCurrentPos);

        centerX = centerY = ACT_STOP;

        switch (action) {
            case ACT_DIG_LEFT:
            case ACT_DIG_RIGHT:
                xOffset = 0;
                yOffset = 0;
                break;
            case ACT_UP:
            case ACT_DOWN:
            case ACT_FALL:
                if (xOffset > 0)
                    centerX = ACT_LEFT;
                else if (xOffset < 0)
                    centerX = ACT_RIGHT;
                break;
            case ACT_LEFT:
            case ACT_RIGHT:
                if (yOffset > 0)
                    centerY = ACT_UP;
                else if (yOffset < 0)
                    centerY = ACT_DOWN;
                break;
        }

        curToken = map[x][y].base;

        if (action == ACT_UP) {
            yOffset -= 1;  // yMove

            if (stayCurrentPos && yOffset < 0) {
                // Stay on current position
                yOffset = 0;
            }
            else if (yOffset < -H2) {
                // Move to y-1 position
                if (curToken == TILE_BRICK || curToken == TILE_HIDDEN) {
                    // In hole or hidden ladder
                    // Note: This somehow breaks climbing up the last ladder tile
                    // curToken = TILE_BLANK;
                }
                // Runner moves to [x][y-1] so set [x][y].act to previous state
                map[x][y].act = curToken;
                y--;
                yOffset = TILE_H + yOffset;
                if (map[x][y].act == TILE_GUARD && guardAlive(x, y))
                    setRunnerDead();
            }
            // shape = climb
            runner.sequence = CLIMB_SEQUENCE;
            // runner.idx = 0;
        }

        if (centerY == ACT_UP) {
            yOffset -= 1;  // yMove
            if (yOffset < 0)
                yOffset = 0;  // Move to center Y
        }

        if (action == ACT_DOWN || action == ACT_FALL) {
            int holdOnBar = 0;
            if (curToken == TILE_ROPE) {
                if (yOffset < 0)
                    holdOnBar = 1;
                else {
                    // if runner is on rope and action is down then switch to falling state
                    // except if ladder or guard is below
                    if (action == ACT_DOWN && y < MAX_TILE_Y && map[x][y + 1].act != TILE_LADDER &&
                        map[x][y + 1].act != TILE_GUARD) {
                        action = ACT_FALL;
                    }
                }
            }

            yOffset += 1;  // yMove

            if (holdOnBar == 1 && yOffset >= 0) {
                yOffset = 0;  // fall and hold on bar
                action = ACT_FALL_BAR;
            }
            if (stayCurrentPos && yOffset > 0) {
                // Stay on current position
                yOffset = 0;
            }
            else if (yOffset > H2) {
                // Move to y + 1 position
                if (curToken == TILE_BRICK || curToken == TILE_HIDDEN) {
                    // In hole or hidden ladder
                    curToken = TILE_BLANK;
                }
                map[x][y].act = curToken;
                y++;
                yOffset = yOffset - TILE_H;
                if (map[x][y].act == TILE_GUARD && guardAlive(x, y))
                    setRunnerDead();
            }

            if (action == ACT_DOWN) {
                // shape = runUpDown
                runner.sequence = CLIMB_SEQUENCE;
                // runner.idx = 0;
            }
            else {
                // ACT_FALL or ACT_FALL_BAR
                if (y < MAX_TILE_Y && map[x][y + 1].act == TILE_GUARD) {
                    // Over guard, not a collision
                    // int id = getGuardID(x,y+1);
                    // if (yOffset > guard[id].pos.yOffset) yOffset = guard[id].pos.yOffset;
                }

                if (action == ACT_FALL_BAR) {
                    // Caught the rope
                    runner.sequence = RAPPEL_SEQUENCE;
                    runner.idx = 0;
                }
                else {
                    // Falling
                    runner.sequence = FALL_SEQUENCE;
                    // runner.idx = 0;
                }
            }
        }

        // ajusta posicao Y enquanto da escada vai em direção a plataforma não alinhada
        if (centerY == ACT_DOWN) {
            yOffset += 1;  // yMove
            if (yOffset > 0)
                yOffset = 0;  // Move to center Y
        }

        if (action == ACT_LEFT) {
            xOffset -= 1;  // xMove

            if (stayCurrentPos && xOffset < 0) {
                // stay on current position
                xOffset = 0;
            }
            else if (xOffset < -W2) {
                // Move to x-1 position
                if (curToken == TILE_BRICK) {  // || curToken == TILE_LADDER) {
                    // In hole or hidden ladder
                    curToken = TILE_BLANK;
                    // Debug: show when we hit this condition
                }
                // Runner moves to map[x-1][y] so set map[x][y].act to previous state
                map[x][y].act = curToken;
                x--;
                xOffset += TILE_W;

                if (map[x][y].act == TILE_GUARD && guardAlive(x, y))
                    setRunnerDead();
            }
            if (curToken == TILE_ROPE) {
                runner.sequence = RAPPEL_SEQUENCE;
                if (runner.direction != ACT_LEFT) {
                    runner.direction = ACT_LEFT;
                    // runner.idx = 0;
                }
            }
            else {
                runner.sequence = RUN_SEQUENCE;
                if (runner.direction != ACT_LEFT) {
                    runner.direction = ACT_LEFT;
                    // runner.idx = 0;
                }
            }
        }

        // ajusta posicao X enquanto sobe/desce escadas ou está em queda livre
        if (centerX == ACT_LEFT) {
            xOffset -= 1;  // xMove
            if (xOffset < 0)
                xOffset = 0;
        }

        if (action == ACT_RIGHT) {
            xOffset += 1;  // xMove

            if (stayCurrentPos && xOffset > 0) {
                // stay on current position
                xOffset = 0;
            }
            else if (xOffset > W2) {
                // Move to x+1 position
                if (curToken == TILE_BRICK) {  // || curToken == TILE_LADDER) {
                    // In hole or hidden ladder
                    curToken = TILE_BLANK;
                }
                // runner moves to map[x+1][y], so set map[x][y].act to previous state
                map[x][y].act = curToken;  // runner move to [x+1][y], so set [x][y].act to previous state
                x++;
                xOffset -= TILE_W;
                if (map[x][y].act == TILE_GUARD && guardAlive(x, y))
                    setRunnerDead();
            }
            if (curToken == TILE_ROPE) {
                runner.sequence = RAPPEL_SEQUENCE;
                if (runner.direction != ACT_RIGHT) {
                    runner.direction = ACT_RIGHT;
                    // runner.idx = 0;
                }
            }
            else {
                runner.sequence = RUN_SEQUENCE;
                if (runner.direction != ACT_RIGHT) {
                    runner.direction = ACT_RIGHT;
                    // runner.idx = 0;
                }
            }
        }

        if (centerX == ACT_RIGHT) {
            xOffset += 1;  // xMove
            if (xOffset > 0)
                xOffset = 0;
        }

        if (action == ACT_STOP) {
            // stop falling sound
            if (runner.action == ACT_FALL) {
//                stopFallingFx();
            }

            if (runner.action != ACT_STOP) {
                runner.action = ACT_STOP;
            }
        }
        else {
            int xPos = (x + X_OFFSET) * TILE_W + xOffset;
            int yPos = (y + Y_OFFSET) * TILE_H + yOffset;
            int dir = (runner.direction == ACT_LEFT) ? 1 : 0;

            // Update the sprite image and flip bit based on the current sequence and index
            runner.idx += 1;
            runner.idx = runner.idx % runnerSeqSizes[runner.sequence];
            
// video hardware            
//            vpoke(runnerSequences[runner.sequence][runner.idx], SPRITE_ATTR0);
//            vpoke(SPRITE_LAYER1 | dir, SPRITE_ATTR6);
//
//            // sprite x position
//            vpoke(SPRITE_X_L(xPos), SPRITE_ATTR2);
//            vpoke(SPRITE_X_H(xPos), SPRITE_ATTR3);
//
//            // sprite y position
//            vpoke(SPRITE_Y_L(yPos), SPRITE_ATTR4);
//            vpoke(SPRITE_Y_H(yPos), SPRITE_ATTR5);

            runner.x = x;
            runner.y = y;
            runner.xOffset = xOffset;
            runner.yOffset = yOffset;

            if (action != runner.action) {
                if (runner.action == ACT_FALL) {
                    stopFallingFx();
                }
                else if (action == ACT_FALL) {
                    playFallingFx();
                }
            }
            runner.action = action;
            
            //#ifdef DEBUG
            //        displayPos();
            //#endif
            
        }
        if (action == ACT_LEFT || action == ACT_RIGHT)
            runner.direction = action;
        map[x][y].act = TILE_RUNNER;

        
//        if (map[x][y].base == TILE_GOLD &&
//            ((xOffset != 0 && yOffset > 0 && yOffset < H4) || (yOffset != 0 && xOffset >= 0 && xOffset < W4) ||
//             (y < MAX_TILE_Y && map[x][y + 1].base == TILE_LADDER && yOffset < H4))) {
        if (map[x][y].base == TILE_GOLD) {
            removeGold(x, y);
            decGold();
            playGoldFx();
//            displayScore(SCORE_GET_GOLD);
        }        
    }
    
    // Entry point from main loop to move the runner
    public static void moveRunner() {
        int x = runner.x;
        int xOffset = runner.xOffset;
        int y = runner.y;
        int yOffset = runner.yOffset;

        if (map[x][y] == null) {
            System.out.println("");
        }
        
        int curState = 0;
        int curToken = map[x][y].base;
        int nextToken = 0;

        int act = 0;
        boolean stayCurrentPos = false;
        int moveStep = 0;

        //#ifdef DEBUG
        //displayPos();
        //displayTiles(x, y);
        //#endif
                
        if (curToken == TILE_LADDER || (curToken == TILE_ROPE && yOffset == 0)) {
            // OK to move (on ladder or bar)
            curState = STATE_OK_TO_MOVE;
        }
        else if (yOffset < 0) {
            // no ladder and yOffset < 0 ==> falling
            curState = STATE_FALLING;
        }
        else if (y < MAX_TILE_Y) {
            // No ladder and y < maxTileY and yOffset >= 0
            nextToken = map[x][y + 1].act;

            if (nextToken == TILE_BLANK) {
                curState = STATE_FALLING;
            }
            else if (nextToken == TILE_BLOCK || nextToken == TILE_LADDER || nextToken == TILE_BRICK) {
                curState = STATE_OK_TO_MOVE;
            }
            else if (nextToken == TILE_GUARD) {
                curState = STATE_OK_TO_MOVE;
            }
            else {
                curState = STATE_FALLING;
            }
        }
        else {
            // No ladder and y == maxTileY
            curState = STATE_OK_TO_MOVE;
        }

        if (curState == STATE_FALLING) {
            nextToken = map[x][y + 1].act;
            stayCurrentPos = (y >= MAX_TILE_Y || nextToken == TILE_BRICK || nextToken == TILE_BLOCK || nextToken == TILE_GUARD);

            // Debug: display state for falling runner
            //#ifdef DEBUG
            //    displayState(curState, stayCurrentPos, ACT_FALL);
            //    displayPos();
            //#endif
                    
            runnerMoveStep(ACT_FALL, stayCurrentPos);
            return;
        }

        // Check key action
        act = Key.keyAction(); // inputHandler();  // keyAction();
        
        // Debug: show key action @ 38,3
        // if (act != ACT_UNKNOWN) {
        //     setTile(38, 3, act + 48, 0);
        // }
        
        stayCurrentPos = true;
        switch (act) {
            case ACT_START:
                setRunnerDead();
                break;
            case ACT_UP:
                nextToken = map[x][y - 1].act;
                stayCurrentPos = (y <= 0 || nextToken == TILE_BRICK || nextToken == TILE_BLOCK || nextToken == TILE_TRAP);

                if (y > 0 && map[x][y].base != TILE_LADDER && yOffset < H4 && yOffset > 0 && map[x][y + 1].base == TILE_LADDER) {
                    stayCurrentPos = true;
                    moveStep = ACT_UP;
                }
                else if (!(map[x][y].base != TILE_LADDER && (yOffset <= 0 || map[x][y + 1].base != TILE_LADDER) ||
                           (yOffset <= 0 && stayCurrentPos))) {
                    moveStep = ACT_UP;
                }

                break;
            case ACT_DOWN:
                nextToken = map[x][y + 1].act;
                stayCurrentPos = (y >= MAX_TILE_Y || nextToken == TILE_BRICK || nextToken == TILE_BLOCK);

                if (!(yOffset >= 0 && stayCurrentPos)) {
                    moveStep = ACT_DOWN;
                }
                break;
            case ACT_LEFT:
                nextToken = map[x - 1][y].act;
                stayCurrentPos = (x <= 0 || nextToken == TILE_BRICK || nextToken == TILE_BLOCK || nextToken == TILE_TRAP);

                if (!(xOffset <= 0 && stayCurrentPos)) {
                    moveStep = ACT_LEFT;
                }
                break;
            case ACT_RIGHT:
                nextToken = map[x + 1][y].act;
                stayCurrentPos = (x >= MAX_TILE_X || nextToken == TILE_BRICK || nextToken == TILE_BLOCK || nextToken == TILE_TRAP);

                if (!(xOffset >= 0 && stayCurrentPos)) {
                    moveStep = ACT_RIGHT;
                }
                break;
            case ACT_DIG_LEFT:
            case ACT_DIG_RIGHT:
                if (ok2dig(act)) {
                    runnerMoveStep(act, stayCurrentPos);
                    digHole(act);
                }
                else {
                    runnerMoveStep(ACT_STOP, stayCurrentPos);
                }
                return;
            default:
                break;
        }
    
        // Debug: show moveStep and stayCurrentPos @ 36,4 and 38,4
        //#ifdef DEBUG
        //    displayState(curState, stayCurrentPos, moveStep);
        //#endif

        runnerMoveStep(moveStep, stayCurrentPos);    
    }

    public static void setTile(int x, int y, int tileId, int w) {
        drawMap[y][x] = tileId;
    }
    
    // Entry point from main loop to fill holes
    public static void processFillHole() {
        int i = 0;
        
        //#ifdef DEBUG_FILL
        //    displayFill();
        //#endif
                
        for (i = 0; i < MAX_HOLES; i++) {
            if (holes[i].active) {
                int x = holes[i].x;
                int y = holes[i].y;
                holes[i].count++;
                if (holes[i].count == 1) {
                    // Clear the blast debris from the tile above the hole
                    setTile(x, y - 1, TILE_BLANK, 0);
                    //map[x][y - 1].base = TILE_BLANK;
                }
                else if (holes[i].count == HOLE_REGEN1) {
                    setTile(x, y, TILE_REGEN1, 0);
                    //map[x][y].base = TILE_REGEN1;
                }
                else if (holes[i].count == HOLE_REGEN2) {
                    setTile(x, y, TILE_REGEN2, 0);
                    //map[x][y].base = TILE_REGEN2;
                }
                else if (holes[i].count == HOLE_REGEN3) {
                    setTile(x, y, TILE_REGEN3, 0);
                    //map[x][y].base = TILE_REGEN3;
                }
                else if (holes[i].count >= HOLE_REGEN4) {
                    setTile(x, y, TILE_BRICK, 0);
                    //map[x][y].base = TILE_BRICK;
                    fillComplete(i);
                }
            }
        }
    }

    public static void fillComplete(int holeIdx) {
        int x = holes[holeIdx].x;
        int y = holes[holeIdx].y;

        // Debug: show the tile for anything currently in the hole
        // setTile(1,20,map[x][y].act,0);

        switch (map[x][y].act) {
            case TILE_RUNNER:
                // Runner dead
                setRunnerDead();
                break;
            case TILE_GUARD: {
                // Guard dead
                int id = guardId(x, y);
                removeFromShake(id);
                if (id != -1) {
                    if (guard[id].hasGold > 0) {
                        decGold();
                        guard[id].hasGold = 0;
                    }
                }
                guardReborn(x, y);
                
                //displayScore(SCORE_GUARD_DEAD); // <------------------------------- TODO
            } break;
        }
        // Restore the tile in the map
        map[x][y].act = TILE_BRICK;
        // Hole is no longer active
        holes[holeIdx].active = false;
    }

    // Entry point from main loop to dig hole
    public static boolean isDigging() { 
        boolean rc = false;
        
        // Debug: display info regarding any dig currently in process
        //#ifdef DEBUG
        //    displayDig();
        //#endif
        
        if (hole.action != ACT_STOP) {
            int x = hole.x;
            int y = hole.y;
            if (map[x][y].act == TILE_GUARD) {
                int id = guardId(x, y);
                if (hole.idx < DIG_LENGTH && guard[id].yOffset > -H4) {
                    // Check if guard is too close to the digging
                    stopDigging(x, y);
                }
                else {
                    map[x][y].act = TILE_BLANK;  // assume hole complete
                    rc = true;
                }
            }
            else {
                // No need to change runner image (run left or run right)
                runner.action = ACT_STOP;
                rc = true;
            }
        }
        return rc;        
    }
    
    public static void stopDigging(int x, int y) {
        // Disable hole
        hole.action = ACT_STOP;

        // Fill hole and clear tile above hole
        y++;
        map[x][y].act = map[x][y].base;  // TILE_BRICK
        
        setTile(x, y, TILE_BRICK, 0);
        setTile(x, y - 1, TILE_BLANK, 0);
        //map[x][y].base = TILE_BRICK;
        //map[x][y - 1].base = TILE_BLANK;

        // Change runner action
        runner.action = ACT_STOP;

        // stop sound of digging
        stopDiggingFx();
    }

    public static void processDigHole() {
        // Do nothing if we aren't digging
        if (hole.action == ACT_STOP)
            return;

        hole.idx++;
        if (hole.idx < DIG_LENGTH) {
            if (hole.action == ACT_DIG_LEFT) {
                setTile(hole.x, hole.y, digLeft[DIG_UPPER][hole.idx], 0);
                setTile(hole.x, hole.y + 1, digLeft[DIG_LOWER][hole.idx], 0);
                //map[hole.x][hole.y].base = digLeft[DIG_UPPER][hole.idx]; //, 0);
                //map[hole.x][hole.y + 1].base = digLeft[DIG_LOWER][hole.idx]; //, 0);
        }
        else {
                setTile(hole.x, hole.y, digRight[DIG_UPPER][hole.idx], 0);
                setTile(hole.x, hole.y + 1, digRight[DIG_LOWER][hole.idx], 0);
                //map[hole.x][hole.y].base = digRight[DIG_UPPER][hole.idx]; //, 0);
                //map[hole.x][hole.y + 1].base = digRight[DIG_LOWER][hole.idx]; //, 0);
            }
        }
        else {
            digComplete();
        }
    }

    private static boolean ok2dig(int action) {
        int x = runner.x;
        int y = runner.y;
        boolean rc = false;
        switch (action) {
            case ACT_DIG_LEFT:
                if (y < MAX_TILE_Y && x > 0 && map[x - 1][y + 1].act == TILE_BRICK && map[x - 1][y].act == TILE_BLANK &&
                    map[x - 1][y].base != TILE_GOLD) {
                    rc = true;
                }
                break;
            case ACT_DIG_RIGHT:
                if (y < MAX_TILE_Y && x < MAX_TILE_X && map[x + 1][y + 1].act == TILE_BRICK && map[x + 1][y].act == TILE_BLANK &&
                    map[x + 1][y].base != TILE_GOLD) {
                    rc = true;
                }
                break;
            default:
                break;
        }
        return rc;
    }

    private static void digHole(int action) {
        int x = 0;
        int y = 0;
        if (action == ACT_DIG_LEFT) {
            x = runner.x - 1;
            y = runner.y;
        }
        else {  // Dig right
            x = runner.x + 1;
            y = runner.y;
        }
        hole.action = action;
        hole.x = x;
        hole.y = y;
        hole.idx = 0;
        playDiggingFx();        
    }
    
    public static void digComplete() {
        int x = hole.x;
        int y = hole.y + 1;
        map[x][y].act = TILE_BLANK;
        // No longer digging
        hole.action = ACT_STOP;

        fillHole(x, y);
    }
    
    public static void fillHole(int x, int y) {
        int i = 0;
        for (i = 0; i < MAX_HOLES; i++) {
            if (!holes[i].active) {
                holes[i].active = true;
                holes[i].x = x;
                holes[i].y = y;
                holes[i].count = 0;
                break;
            }
        }
    }

    public static void draw(Graphics2D g) {
        //g.setColor(Color.RED);
        //g.drawRect(runner.x * TILE_W, runner.y * TILE_H, 8, 8);
        
        int spriteId = runnerSequences[runner.sequence][runner.idx];
        BufferedImage sprite = Resource.sprites.get(spriteId);
        if (runner.direction == ACT_RIGHT) {
            g.drawImage(sprite, runner.x * TILE_W + runner.xOffset, runner.y * TILE_H + runner.yOffset, null);
        }
        else {
            g.drawImage(sprite, runner.x * TILE_W + runner.xOffset + TILE_W, runner.y * TILE_H + runner.yOffset, -TILE_W, TILE_H, null);
        }
        
    }
    
}
