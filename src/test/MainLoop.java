package test;

import static test.Guard.*;
import static test.Levels.*;
import static test.LodeRunner.*;
import static test.Runner.*;
import static test.SoundFX.*;
import static test.Splash.splash;

/**
 * 
 * @author Leo
 */
public class MainLoop {

    public static int main() {
        int result = 0;
        // Read VIA register in memory as a seed
        //int *seed = (int *)0x9f64;
        //srand(*seed);

        // Install CX16 joystick driver
        //joy_install(cx16_std_joy);

        //inputHandler = keyAction;

        // Not supported for CX16
        // kbrepeat(KBREPEAT_ALL);

        currentGame.world = WORLD_CLASSIC;
        // Test world for benchmarks and isolated testing
        //world = WORLD_CUSTOM;
        currentGame.level = 1;
        currentGame.gameState = GAME_SPLASH;
        currentGame.lives = 5;

        //printf("loading resources...\n");
        //result = loadFiles();

        //if (result) {
        //    printf("loaded resources successfully\n");
        //} else {
        //    printf("failed to load all resources\n");
        //    return result;
        //}
        
        initLevels();
        currentGame.currentScore = 0;

        //screenConfig();

        // Enable sprites
        //vpoke(0x01, 0x1f4000);

//        do {
//            // Wait for next VSYNC interrupt
//            waitvsync();
//
//            mainTick();
//
//        } 
//        while (1);

        return 0;
    }

    public static int scoreCount;

    public static void mainTick() {
        switch(currentGame.gameState) {
            case GAME_SPLASH:
                splash();
                break;
            case GAME_RUNNING:
                playGame();
                break;
            case GAME_NEW_LEVEL:
//                if (loadLevel(currentGame.world,currentGame.level)) {
//                    displayLevel(currentGame.level-1);
//                    // Enable sprites
//                    vpoke(0x01, 0x1f4000);
//                    currentGame.gameState = GAME_RUNNING;
//                } else {
//                    worldComplete();
//                    currentGame.level = 1;
//                    currentGame.gameState = GAME_OVER;
//                }
                break;
            case GAME_FINISH:
//                // Disable sprites
//                vpoke(0x0, 0x1f4000);
//                // Increase score for level completion
//                scoreCount = 0;
//                // Start the sound effect for updating the score
//                playScoringFx();
//                currentGame.gameState = GAME_FINISH_SCORE_COUNT;
//                break;
            case GAME_FINISH_SCORE_COUNT:
//                while (scoreCount <= SCORE_COUNT) {
//                    ++scoreCount;
//                    displayScore(SCORE_INCREMENT);
//
//                    playSoundFx();
//
//                    waitvsync();
//                }
//
//                stopScoringFx();
//                currentGame.lives++;
//                currentGame.gameState = GAME_NEXT_LEVEL;
//                break;
            case GAME_NEXT_LEVEL:
                currentGame.level++;
                currentGame.gameState = GAME_NEW_LEVEL;
                break;
            case GAME_PREV_LEVEL:
//                if (currentGame.level) currentGame.level--;
//                currentGame.gameState = GAME_NEW_LEVEL;
//                break;
            case GAME_RUNNER_DEAD:
//                currentGame.lives--;
//                displayLives();
//                if (currentGame.lives <= 0) {
//                    // TODO: Game Over
//                    gameOver();
//                    currentGame.gameState = GAME_OVER;
//                } else {
//                    currentGame.gameState = GAME_NEW_LEVEL;
//                }
//                break;
            case GAME_OVER:
            {
                try {
                    // Keep "Game Over" or "WORLD COMPLETE" displayed for 5 seconds then go back to splash
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                }
            }
                currentGame.gameState = GAME_SPLASH;
                break;

            default:
                break;
        }
    }

    // Main loop while playing the game
    public static void playGame() {
        //int tick = 0;

        //if (processTick[currentGame.speed][tick] ) {
            if (currentLevel.goldComplete && runner.y == 0 && runner.yOffset == 0) {
                currentGame.gameState = GAME_FINISH;
                return;
            }

            if (!isDigging()) {
                moveRunner();
            } else {
                processDigHole();
            }
            if (currentGame.gameState != GAME_RUNNER_DEAD) moveGuard();

            processGuardShake();
        //}

        //tick++;
        //if (tick == SPEED_TICKS) tick = 0;

        processFillHole();
        playSoundFx();
    }

}
