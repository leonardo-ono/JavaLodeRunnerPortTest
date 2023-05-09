package test;

import static test.Levels.currentGame;
import static test.LodeRunner.GAME_RUNNING;

/**
 *
 * @author Leo
 */
public class Splash {

    public static void splash() {
        currentGame.gameState = GAME_RUNNING;
    }
    
}
