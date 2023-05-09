package test;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import static test.GlobalState.guard;
import static test.GlobalState.shake;
import static test.Levels.*;
import static test.LodeRunner.*;
import static test.Runner.*;
import static test.Runner.runner;

/**
 * 
 * @author Leo
 */
public class Guard {

    public static class guard_t {

        // Flag indicating the guard is active
        public boolean active;

        // Guard x/y coordinates in tiles
        public int x;
        public int y;

        // Guard x/y offests in pixels (-4 .. 4)
        public int xOffset;
        public int yOffset;

        // x/y coordinates of guard's hole
        public int holePosX;
        public int holePosY;

        // Action
        public int action;

        // Index for sprite image
        public int idx;

        // Last direction (left/right)
        public int direction;

        // Animation sequence (shape)
        public int sequence;

        // Guard has gold
        public int hasGold;
        
        public int shakeX;
    }

    public static class shakeGuard_t {
        // Flag indicating this shake entry is active
        boolean active;
        // Guard ID
        int id;
        // animation index
        int idx;
        int pad;
        // count of how many time processGuardShake()
        // has been called for this guard
        int count;
    }

    // Animation sequences for guard
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

    public static int[][] guardSequences = { //[MAX_SEQUENCE][MAX_SEQ_LENGTH] = {
        // RUN_SEQUENCE
        { GUARD_1, GUARD_2, GUARD_3 },
        // CLIMB_SEQUENCE
        { GUARD_CLIMB_1, GUARD_CLIMB_2 },
        // RAPPEL_SEQUENCE
        { GUARD_RAPPEL_1, GUARD_RAPPEL_2, GUARD_RAPPEL_3 },
        // FALL_SEQUENCE
        { GUARD_FALLING } 
    };

    public static int[] guardSeqSizes = { RUN_LENGTH, CLIMB_LENGTH, RAPPEL_LENGTH, FALL_LENGTH }; // [MAX_SEQUENCE]
    
    public static final int NUM_MOVE_ITEMS = 6;

    // Original movePolicy array from Lode Runner Total Recall
    public static int[][] movePolicy = { //[12][NUM_MOVE_ITEMS] = {
        {0, 0, 0, 0, 0, 0}, {0, 1, 1, 0, 1, 1}, {1, 1, 1, 1, 1, 1}, {1, 2, 1, 1, 2, 1}, {1, 2, 2, 1, 2, 2}, {2, 2, 2, 2, 2, 2},
        {2, 2, 3, 2, 2, 3}, {2, 3, 3, 2, 3, 3}, {3, 3, 3, 3, 3, 3}, {3, 3, 4, 3, 3, 4}, {3, 4, 4, 3, 4, 4}, {4, 4, 4, 4, 4, 4} };

    // Offset into
    public static int moveOffset = 0;
    // The ID of the last guard that was moved
    public static int moveId = 0;

    public static int guardCount;

    public static void clearGuards() { 
        for (int i = 0; i < MAX_GUARDS; i++) {
            guard[i].active = false;

            // Disable the corresponding sprite
            //vpoke(0, SPRITE_ATTR6 + 8 * (i + 1));  // Attr6

            shake[i].active = false;
        }
        guardCount = 0;
        moveOffset = 0;
        moveId = 0;
    }

    public static int initGuard(int x, int y) { 
        //int spriteAttr0 = ((int)VERA_INC_1 << 16) | SPRITE_ATTR0;

        if (guardCount < MAX_GUARDS) {
            int i = guardCount;
            int xPos = (x + X_OFFSET) * TILE_W;
            int yPos = (y + Y_OFFSET) * TILE_H;

            guard[i].x = x;
            guard[i].y = y;
            guard[i].xOffset = 0;
            guard[i].yOffset = 0;
            guard[i].holePosX = 0;
            guard[i].holePosY = 0;
            guard[i].action = ACT_UNKNOWN;
            guard[i].direction = ACT_RIGHT;
            guard[i].idx = 0;

            guard[i].sequence = RUN_SEQUENCE;

            // Sprite attribute settings - memory increment set to 1
//            vpoke(SPRITE_ADDR_L(GUARD_1), spriteAttr0 + 8 * (i + 1));  // Attr0
//            VERA.data0 = SPRITE_ADDR_H(GUARD_1);                       // Attr1
//            VERA.data0 = SPRITE_X_L(xPos);                             // Attr2
//            VERA.data0 = SPRITE_X_H(xPos);                             // Attr3
//            VERA.data0 = SPRITE_Y_L(yPos);                             // Attr4
//            VERA.data0 = SPRITE_Y_H(yPos);                             // Attr5
//            VERA.data0 = SPRITE_LAYER1;                                // Attr6
//            VERA.data0 = 0;                                            // Attr7
            
            //#ifdef DEBUG
            // displayGuard(guardCount);
            //#endif
            
            guardCount++;
            return 1;
        }
        else {
            return 0;
        }
    }
    
    public static int guardId(int x, int y) { 
        int i = 0;
        for (i = 0; i < guardCount; i++) {
            if (guard[i].x == x && guard[i].y == y)
                return i;
        }
        return -1;
    }
    
    public static boolean guardAlive(int x, int y) { 
        int i = 0;
        for (i = 0; i < guardCount; i++) {
            if (guard[i].x == x && guard[i].y == y) {
                if (guard[i].action != ACT_REBORN)
                    return true;
            }
        }
        return false;
    }
    
    public static void rebornComplete(int id) {
        int x = guard[id].x;
        int y = guard[id].y;

        if (map[x][y].act == TILE_RUNNER)
            setRunnerDead();

        map[x][y].act = TILE_GUARD;
        guard[id].action = ACT_FALL;
        guard[id].direction = ACT_RIGHT;
    }

    public static void guardReborn(int x, int y) { 
        int id = guardId(x, y);
        if (id != -1) {
            guard_t curGuard = guard[id];
            int bornY = 1;  // Start on line 2
            int bornX = (int)((int) (0xffff * Math.random()) % MAX_TILE_X);
            int rndStart = bornX;

            while (map[bornX][bornY].act != TILE_BLANK || map[bornX][bornY].base == TILE_GOLD ||
                   map[bornX][bornY].base == TILE_BRICK) {
                if ((bornX = (int)((int) (0xffff * Math.random()) % MAX_TILE_X)) == rndStart) {
                    bornY++;
                }
            }

            map[bornX][bornY].act = TILE_GUARD;
            curGuard.x = bornX;
            curGuard.y = bornY;
            curGuard.xOffset = 0;
            curGuard.yOffset = 0;
            curGuard.sequence = RUN_SEQUENCE;
            curGuard.idx = 0;
            curGuard.action = ACT_REBORN;

            rebornComplete(id);
        }    
    };

    // Entry point from main loop to move guards
    public static void moveGuard() { 
        int moves = 0;
        guard_t curGuard = null;
        int x = 0, y = 0, yOffset = 0;

        if (guardCount == 0) return;  // No guard

        if (++moveOffset >= NUM_MOVE_ITEMS)
            moveOffset = 0;
        moves = movePolicy[guardCount][moveOffset];  // get next moves

        while (moves-- > 0) {
            if (++moveId >= guardCount)
                moveId = 0;
            curGuard = guard[moveId];

            if (curGuard.action == ACT_IN_HOLE || curGuard.action == ACT_REBORN) {
                continue;
            }

            guardMoveStep(moveId, bestMove(moveId));

            // Debug: display guard position
            //#ifdef DEBUG
            // displayGuard(moveId);
            //#endif
        };
    }


    public static int bestMove(int id) {
        guard_t guarder = guard[id];
        int x = guarder.x;
        int y = guarder.y;
        int xOffset = guarder.xOffset;
        int yOffset = guarder.yOffset;

        int curToken = map[x][y].base;
        int nextBelow = 0;
        int nextMove;
        boolean checkSameLevelOnly = false;

        if (guarder.action == ACT_CLIMB_OUT) {
            if (y == guarder.holePosY) {
                // Climb out of hole
                return ACT_UP;
            }
            else {
                checkSameLevelOnly = true;
                if (x != guarder.holePosX) {
                    // out of hole
                    guarder.action = ACT_LEFT;
                }
            }
        }

        if (!checkSameLevelOnly) {
            // Next check if guard must fall; if so then return ACT_FALL and
            // skip remaining logic
            if (curToken == TILE_LADDER || (curToken == TILE_ROPE && yOffset == 0)) {
                // No guard fall
            }
            else if (yOffset < 0) {
                return ACT_FALL;
            }
            else if (y < MAX_TILE_Y) {
                nextBelow = map[x][y + 1].act;

                if ((nextBelow == TILE_BLANK || nextBelow == TILE_RUNNER)) {
                    return ACT_FALL;
                }
                else if (nextBelow == TILE_BRICK || nextBelow == TILE_BLOCK || nextBelow == TILE_GUARD ||
                         nextBelow == TILE_LADDER) {
                    // No guard fall
                }
                else {
                    return ACT_FALL;
                }
            }
        }

        // Next check if runner is on same level and whether guard can get him.  Ignore walls
        if (y == runner.y && runner.action != ACT_FALL) {
            // Guard on ladder and falling . don't catch it
            while (x != runner.x) {
                if (y < MAX_TILE_Y) {
                    nextBelow = map[x][y + 1].base;
                }
                else {
                    nextBelow = TILE_BLOCK;
                }
                curToken = map[x][y].base;

                if (curToken == TILE_LADDER || curToken == TILE_ROPE || nextBelow == TILE_BLOCK || nextBelow == TILE_LADDER ||
                    nextBelow == TILE_BRICK || map[x][y + 1].act == TILE_GUARD || nextBelow == TILE_ROPE ||
                    nextBelow == TILE_GOLD) {
                    if (x < runner.x) {
                        // Guard to left of runner
                        ++x;
                    }
                    else if (x > runner.x) {
                        // Guard to right of runner
                        --x;
                    }
                }
                else {
                    // Exit loop with closest x if no path to runner
                    break;
                }
            }

            if (x == runner.x) {
                // scan for a path ignoring walls is a success
                if (guarder.x < runner.x) {
                    nextMove = ACT_RIGHT;
                }
                else if (guarder.x > runner.x) {
                    nextMove = ACT_LEFT;
                }
                else {
                    // Guard x == runner x
                    if (guarder.xOffset < runner.xOffset) {
                        nextMove = ACT_RIGHT;
                    }
                    else {
                        nextMove = ACT_LEFT;
                    }
                }
                return nextMove;
            }
        }
        // If guard can't reach runner on current level then scan floor
        // (ignoring walls) and look up and down for best move
        return scanFloor(id);
    }

    public static int bestRating = 0;
    public static int bestPath = 0;
    public static int curRating = 0;
    public static int startX = 0;
    public static int startY = 0;
    public static int leftEnd = 0;
    public static int rightEnd = 0;

    public static int scanFloor(int id) {
        int x = startX = guard[id].x;
        int y = startY = guard[id].y;
        int curToken;
        int curPath = 0;
        int nextBelow;

        // Start with worst rating
        bestRating = 255;
        curRating = 255;
        bestPath = ACT_STOP;

        // Get ends for the search along floor
        while (x > 0) {
            curToken = map[x - 1][y].act;
            if (curToken == TILE_BRICK || curToken == TILE_BLOCK) {
                break;
            }
            if (curToken == TILE_LADDER || curToken == TILE_ROPE || y >= MAX_TILE_Y ||
                (y < MAX_TILE_Y &&
                 ((nextBelow = map[x - 1][y + 1].base) == TILE_BRICK || nextBelow == TILE_BLOCK || nextBelow == TILE_LADDER))) {
                --x;
            }
            else {
                // Go left anyway
                --x;
                break;
            }
        }
        leftEnd = x;
        x = startX;
        while (x < MAX_TILE_X) {
            curToken = map[x + 1][y].act;
            if (curToken == TILE_BRICK || curToken == TILE_BLOCK) {
                break;
            }
            if (curToken == TILE_LADDER || curToken == TILE_ROPE || y >= MAX_TILE_Y ||
                (y < MAX_TILE_Y &&
                 ((nextBelow = map[x + 1][y + 1].base) == TILE_BRICK || nextBelow == TILE_BLOCK || nextBelow == TILE_LADDER))) {
                ++x;
            }
            else {
                // Go right anyway
                ++x;
                break;
            }
        }
        rightEnd = x;

        // Do middle scan first for best rating and direction
        x = startX;
        if (y < MAX_TILE_Y && (nextBelow = map[x][y + 1].base) != TILE_BRICK && nextBelow != TILE_BLOCK) {
            scanDown(x, ACT_DOWN);
        }

        if (map[x][y].base == TILE_LADDER) {
            scanUp(x, ACT_UP);
        }

        // Next scan both sides of floor for best rating
        curPath = ACT_LEFT;
        x = leftEnd;
        while (true) {
            if (x == startX) {
                if (curPath == ACT_LEFT && rightEnd != startX) {
                    curPath = ACT_RIGHT;
                    x = rightEnd;
                }
                else {
                    break;
                }
            }

            if (y < MAX_TILE_Y && (nextBelow = map[x][y + 1].base) != TILE_BRICK && nextBelow != TILE_BLOCK) {
                scanDown(x, curPath);
            }

            if (map[x][y].base == TILE_LADDER) {
                scanUp(x, curPath);
            }

            if (curPath == ACT_LEFT) {
                x++;
            }
            else {
                x--;
            }
        }

        return bestPath;
    }


    public static void scanDown(int x, int curPath) {
        int y;
        int nextBelow;
        int runnerX = runner.x;
        int runnerY = runner.y;

        y = startY;

        while (y < MAX_TILE_Y && (nextBelow = map[x][y + 1].base) != TILE_BRICK && nextBelow != TILE_BLOCK) {
            if (map[x][y].base != TILE_BLANK && map[x][y].base != TILE_HIDDEN) {
                // If not falling, try to move left or right
                if (x > 0) {
                    // If not at left edge check left side
                    if ((nextBelow = map[x - 1][y + 1].base) == TILE_BRICK || nextBelow == TILE_LADDER || nextBelow == TILE_BLOCK ||
                        map[x - 1][y].base == TILE_ROPE) {
                        // Can move left
                        if (y >= runnerY) {
                            // No need to go on, already below runner
                            break;
                        }
                    }
                }
                if (x < MAX_TILE_X) {
                    // If not at right edge check right side
                    if ((nextBelow = map[x + 1][y + 1].base) == TILE_BRICK || nextBelow == TILE_LADDER || nextBelow == TILE_BLOCK ||
                        map[x + 1][y].base == TILE_ROPE) {
                        // Can move right
                        if (y >= runnerY) {
                            // No need to go on, already below runner
                            break;
                        }
                    }
                }
            }
            y++;
        }
        if (y == runnerY) {
            curRating = Math.abs(startX - x);
        }
        else if (y > runnerY) {
            // Position below runner
            curRating = y - runnerY + 200;
        }
        else {
            curRating = runnerY - y + 100;
        }

        if (curRating < bestRating) {
            bestRating = curRating;
            bestPath = curPath;
        }
    }

    public static void scanUp(int x, int curPath) {
        int y;
        int nextBelow;
        int runnerX = runner.x;
        int runnerY = runner.y;

        y = startY;

        while (y > 0 && map[x][y].base == TILE_LADDER) {
            // While guard can go up
            --y;
            if (x > 0) {
                // If not at left edge check left side
                if ((nextBelow = map[x - 1][y + 1].base) == TILE_BRICK || nextBelow == TILE_BLOCK || nextBelow == TILE_LADDER ||
                    map[x - 1][y].base == TILE_ROPE) {
                    if (y <= runnerY) {
                        break;
                    }
                }
            }

            if (x < MAX_TILE_X) {
                // If not at right edge check right side
                if ((nextBelow = map[x + 1][y + 1].base) == TILE_BRICK || nextBelow == TILE_BLOCK || nextBelow == TILE_LADDER ||
                    map[x + 1][y].base == TILE_ROPE) {
                    if (y <= runnerY) {
                        break;
                    }
                }
            }
        }

        if (y == runnerY) {
            curRating = Math.abs(startX - x);
        }
        else if (y > runnerY) {
            // position below runner
            curRating = y - runnerY + 200;
        }
        else {
            // position above runner
            curRating = runnerY - y + 100;
        }

        if (curRating < bestRating) {
            bestRating = curRating;
            bestPath = curPath;
        }
    }
    

    public static void guardMoveStep(int id, int action) {
        guard_t curGuard = guard[id];
        int x = curGuard.x;
        int xOffset = curGuard.xOffset;
        int y = curGuard.y;
        int yOffset = curGuard.yOffset;

        int curToken = 0, nextToken = 0;
        int centerX = 0, centerY = 0;
        boolean stayCurrentPos = false;

        centerX = centerY = ACT_STOP;

        if (curGuard.action == ACT_CLIMB_OUT && action == ACT_STOP) {
            curGuard.action = ACT_STOP;
        }

        switch (action) {
            case ACT_UP:
            case ACT_DOWN:
            case ACT_FALL:
                if (action == ACT_UP) {
                    stayCurrentPos = (y <= 0 || (nextToken = map[x][y - 1].act) == TILE_BRICK || nextToken == TILE_BLOCK ||
                                      nextToken == TILE_TRAP || nextToken == TILE_GUARD);
                    if (yOffset <= 0 && stayCurrentPos) {
                        action = ACT_STOP;
                    }
                }
                else {
                    // ACT_DOWN || ACT_FALL
                    stayCurrentPos = (y >= MAX_TILE_Y || (nextToken = map[x][y + 1].act) == TILE_BRICK || nextToken == TILE_BLOCK ||
                                      nextToken == TILE_GUARD);

                    if (action == ACT_FALL && yOffset < 0 && map[x][y].base == TILE_BRICK) {
                        action = ACT_IN_HOLE;
                        stayCurrentPos = true;
                    }
                    else {
                        if (yOffset >= 0 && stayCurrentPos) {
                            action = ACT_STOP;
                        }
                    }
                }

                if (action != ACT_STOP) {
                    if (xOffset > 0) {
                        centerX = ACT_LEFT;
                    }
                    else if (xOffset < 0) {
                        centerX = ACT_RIGHT;
                    }
                }
                break;
            case ACT_LEFT:
            case ACT_RIGHT:
                if (action == ACT_LEFT) {
                    stayCurrentPos = (x <= 0 || (nextToken = map[x - 1][y].act) == TILE_BRICK || nextToken == TILE_BLOCK ||
                                      nextToken == TILE_GUARD || map[x - 1][y].base == TILE_TRAP);
                    if (xOffset <= 0 && stayCurrentPos) {
                        action = ACT_STOP;
                    }
                }
                else {
                    // ACT_RIGHT
                    stayCurrentPos = (x >= MAX_TILE_X || (nextToken = map[x + 1][y].act) == TILE_BRICK || nextToken == TILE_BLOCK ||
                                      nextToken == TILE_GUARD || map[x + 1][y].base == TILE_TRAP);
                    if (xOffset >= 0 && stayCurrentPos) {
                        action = ACT_STOP;
                    }
                }

                if (action != ACT_STOP) {
                    if (yOffset > 0) {
                        centerY = ACT_UP;
                    }
                    else if (yOffset < 0) {
                        centerY = ACT_DOWN;
                    }
                }
                break;
        }

        curToken = map[x][y].base;

        if (action == ACT_UP) {
            yOffset -= 1;  // yMove

            if (stayCurrentPos && yOffset < 0)
                yOffset = 0;
            else if (yOffset < -H2) {
                if (curToken == TILE_BRICK || curToken == TILE_HIDDEN) {
                    // In hole or on hidden ladder, so this check makes
                    // sure that the runner won't be able to run over a
                    // hole that a guard has climbed out of
                    curToken = TILE_BLANK;
                }
                // Move to y-1 position
                map[x][y].act = curToken;
                y--;
                yOffset += TILE_H;
                if (map[x][y].act == TILE_RUNNER)
                    setRunnerDead();
            }

            if (yOffset <= 0 && yOffset > -1) {
                dropGold(id);
            }
            curGuard.sequence = CLIMB_SEQUENCE;
        }

        if (centerY == ACT_UP) {
            yOffset -= 1;  // yMove
            if (yOffset < 0)
                yOffset = 0;
        }

        if (action == ACT_DOWN || action == ACT_FALL || action == ACT_IN_HOLE) {
            int holdOnBar = 0;
            if (curToken == TILE_ROPE) {
                if (yOffset < 0)
                    holdOnBar = 1;
                else if (action == ACT_DOWN && y < MAX_TILE_Y && map[x][y + 1].act != TILE_LADDER) {
                    action = ACT_FALL;
                }
            }

            yOffset += 1;  // yMove

            if (holdOnBar == 1 && yOffset >= 0) {
                // Fall and hold on bar
                yOffset = 0;
                action = ACT_FALL_BAR;
            }
            if (stayCurrentPos && yOffset > 0)
                yOffset = 0;
            else if (yOffset > H2) {
                // Move to y+1 position
                // if (curToken == TILE_BRICK || curToken == TILE_HIDDEN) curToken = TILE_BLANK; // In hole or hidden ladder
                map[x][y].act = curToken;
                y++;
                yOffset -= TILE_H;
                if (map[x][y].act == TILE_RUNNER)
                    setRunnerDead();
            }

            // Drop gold while guard falls
            if ((action == ACT_FALL || action == ACT_DOWN) && yOffset >= 0 && yOffset < 1) {
                dropGold(id);
            }

            if (action == ACT_IN_HOLE) {
                // Check whether guard is in a hole or still falling
                if (yOffset < 0) {
                    action = ACT_FALL;  // still falling

                    // If guard has gold then drop it before falling into hole totally
                    if (curGuard.hasGold > 0) {
                        if (map[x][y - 1].base == TILE_BLANK) {
                            // Drop gold above
                            addGold(x, y - 1);
                        }
                        else {
                            decGold();  // Gold disappears
                        }
                        curGuard.hasGold = 0;
                    }
                }
                else {
                    // Fall into hole (yOffset must = 0)
                    if (curGuard.hasGold > 0) {
                        if (map[x][y - 1].base == TILE_BLANK) {
                            // Drop ggold above
                            addGold(x, y - 1);
                        }
                        else {
                            decGold();  // Gold disappears
                        }
                        curGuard.hasGold = 0;
                    }

                    curGuard.sequence = FALL_SEQUENCE;
                    curGuard.idx = 0;
                    
                    addGuardToShakeQueue(id);
                    
                    // displayScore(SCORE_IN_HOLE); // TODO <--------------------------
                }
            }

            if (action == ACT_DOWN) {
                curGuard.sequence = CLIMB_SEQUENCE;
            }
            else {
                // ACT_FALL or ACT_FALL_BAR
                if (action == ACT_FALL_BAR) {
                    curGuard.sequence = RAPPEL_SEQUENCE;
                }
                else {
                    curGuard.sequence = FALL_SEQUENCE;
                }
            }
        }

        if (centerY == ACT_DOWN) {
            yOffset += 1;  // yMove
            if (yOffset > 0)
                yOffset = 0;
        }

        if (action == ACT_LEFT) {
            xOffset -= 1;  // xMove

            if (stayCurrentPos && xOffset < 0)
                xOffset = 0;
            else if (xOffset < -W2) {
                // Move to x-1 position
                // if (curToken == TILE_BRICK || curToken == TILE_LADDER) curToken = TILE_BLANK;
                map[x][y].act = curToken;  // Runner move to [x-1][y], so set [x][y] to previous state
                x--;
                xOffset += TILE_W;
                if (map[x][y].act == TILE_RUNNER)
                    setRunnerDead();
            }
            if (xOffset <= 0 && xOffset > -1) {
                dropGold(id);  // Try to drop gold
            }
            if (curToken == TILE_ROPE)
                curGuard.sequence = RAPPEL_SEQUENCE;
            else
                curGuard.sequence = RUN_SEQUENCE;
            curGuard.direction = ACT_LEFT;
        }

        if (centerX == ACT_LEFT) {
            xOffset -= 1;  // xMove
            if (xOffset < 0)
                xOffset = 0;
        }

        if (action == ACT_RIGHT) {
            xOffset += 1;  // xMove

            if (stayCurrentPos && xOffset > 0)
                xOffset = 0;
            else if (xOffset > W2) {
                // Move to x+1 position
                // if (curToken == TILE_BRICK || curToken == TILE_LADDER) curToken = TILE_BLANK;
                map[x][y].act = curToken;
                x++;
                xOffset = xOffset - TILE_W;
                if (map[x][y].act == TILE_RUNNER)
                    setRunnerDead();
            }
            if (xOffset >= 0 && xOffset < 1) {
                dropGold(id);
            }
            if (curToken == TILE_ROPE)
                curGuard.sequence = RAPPEL_SEQUENCE;
            else
                curGuard.sequence = RUN_SEQUENCE;
            curGuard.direction = ACT_RIGHT;
        }

        if (centerX == ACT_RIGHT) {
            xOffset += 1;  // xMove
            if (xOffset > 0)
                xOffset = 0;
        }

        if (action == ACT_STOP) {
            if (curGuard.action != ACT_CLIMB_OUT) {
                curGuard.action = ACT_STOP;
            }
        }
        else {
            int xPos = (x + X_OFFSET) * TILE_W + xOffset;
            int yPos = (y + Y_OFFSET) * TILE_H + yOffset;
            int dir = (curGuard.direction == ACT_LEFT) ? 1 : 0;
            if (curGuard.action == ACT_CLIMB_OUT)
                action = ACT_CLIMB_OUT;

            // Update the sprite image and flip bit
            curGuard.idx++;
            curGuard.idx = curGuard.idx % guardSeqSizes[curGuard.sequence];
            
            // sprite video hardware update
//            vpoke(guardSequences[curGuard.sequence][curGuard.idx], SPRITE_ATTR0 + 8 * (id + 1));
//            vpoke(SPRITE_LAYER1 | dir, SPRITE_ATTR6 + 8 * (id + 1));
//
//            // sprite x position
//            vpoke(SPRITE_X_L(xPos), SPRITE_ATTR2 + 8 * (id + 1));
//            vpoke(SPRITE_X_H(xPos), SPRITE_ATTR3 + 8 * (id + 1));
//
//            // sprite y position
//            vpoke(SPRITE_Y_L(yPos), SPRITE_ATTR4 + 8 * (id + 1));
//            vpoke(SPRITE_Y_H(yPos), SPRITE_ATTR5 + 8 * (id + 1));

            curGuard.x = x;
            curGuard.y = y;
            curGuard.xOffset = xOffset;
            curGuard.yOffset = yOffset;
            curGuard.action = action;
        }
        map[x][y].act = TILE_GUARD;

        // check if there is gold to pick up and cary
        if (map[x][y].base == TILE_GOLD && curGuard.hasGold == 0 &&
            ((xOffset != 0 && yOffset >= 0 && yOffset < H4) || (yOffset != 0 && xOffset >= 0 && xOffset < W4) ||
             (y < MAX_TILE_Y && map[x][y + 1].base == TILE_LADDER && yOffset < H4)  // gold above ladder
             )) {
            curGuard.hasGold = ((int) (0xffff * Math.random())) % 37;
            removeGold(x, y);
        }
    }

    







    public static int dropGold(int id) {
        guard_t curGuard = guard[id];
        int nextToken = 0;
        int drop = 0;

        if (curGuard.hasGold > 1) {
            // Decrease count but don't drop gold
            curGuard.hasGold--;
        }
        else if (curGuard.hasGold == 1) {
            int x = curGuard.x;
            int y = curGuard.y;
            if (map[x][y].base == TILE_BLANK && (y >= MAX_TILE_Y || (nextToken = map[x][y + 1].base) == TILE_BRICK ||
                                                 nextToken == TILE_BLOCK || nextToken == TILE_LADDER)) {
                addGold(x, y);
                curGuard.hasGold = -1;
                drop = 1;
            }
        }
        else if (curGuard.hasGold < 0) {
            curGuard.hasGold++;
        }
        return drop;
    }


    // --- climb out ---
    
    public static void climbOut(int id) {
        guard_t curGuard = guard[id];
        curGuard.action = ACT_CLIMB_OUT;
        curGuard.sequence = CLIMB_SEQUENCE;
        curGuard.holePosX = curGuard.x;
        curGuard.holePosY = curGuard.y;
    }
    
    // --- shake ---

    public static void addGuardToShakeQueue(int id) {
        int i = 0;
        for (i = 0; i < guardCount; i++) {
            if (!shake[i].active) {
                shake[i].active = true;
                shake[i].id = id;
                shake[i].count = 0;
                shake[i].idx = 0;
            }
        }
    }

    public static void removeFromShake(int id) {
        int i = 0;
        for (i = 0; i < guardCount; i++) {
            if (shake[i].active && shake[i].id == id) {
                shake[i].active = false;
            }
        }
    }

    

    public static int SHAKE_LENGTH = 5;
    public static int shakeTimes[] = {140, 146, 152, 158, 162};

    // Entry point from main loop to shake guards prior to 
    // them climbing out of holes
    public static void processGuardShake() {
        int i = 0;
        for (i = 0; i < guardCount; i++) {
            if (shake[i].active) {
                guard_t curGuard = guard[shake[i].id];

                ++shake[i].count;

                if (shake[i].count >= shakeTimes[shake[i].idx]) {
                    // Shake the guard by shifting its X position
                    //int xPos = (curGuard.x + X_OFFSET) * TILE_W;
                    int xPos = 0; //(curGuard.x + X_OFFSET) * TILE_W;
                    if (shake[i].idx % 2 != 0) {
                        xPos -= 2;
                    }
                    else {
                        xPos += 2;
                    }
                    curGuard.shakeX = xPos;
                    
                    // video hardware
                    // vpoke(xPos & 0xff, SPRITE_ATTR2 + 8 * (shake[i].id + 1));
                    // vpoke(xPos >> 8, SPRITE_ATTR3 + 8 * (shake[i].id + 1));

                    shake[i].idx++;

                    if (shake[i].idx == SHAKE_LENGTH) {
                        // Shake sequence complete
                        shake[i].active = false;
                        
                        curGuard.shakeX = 0;
                        
                        climbOut(shake[i].id);
                        continue;
                    }
                }
            }
        }
    }

    // --- render ---
    
    public static void drawActiveGuards(Graphics2D g) { 
        int i = 0;
        for (i = 0; i < guardCount; i++) {
            //if (guard[i].active) {

                int spriteId = guardSequences[guard[i].sequence][guard[i].idx];
                BufferedImage sprite = Resource.sprites.get(spriteId);
                if (guard[i].direction == ACT_RIGHT) {
                    g.drawImage(sprite, guard[i].shakeX + guard[i].x * TILE_W + guard[i].xOffset, guard[i].y * TILE_H + guard[i].yOffset, null);
                }
                else {
                    g.drawImage(sprite, guard[i].shakeX + guard[i].x * TILE_W + guard[i].xOffset + TILE_W, guard[i].y * TILE_H + guard[i].yOffset, -TILE_W, TILE_H, null);
                }

            //}
        }
    }    
    
}
