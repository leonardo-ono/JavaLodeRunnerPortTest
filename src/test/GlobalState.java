package test;

import test.Guard.guard_t;
import test.Guard.shakeGuard_t;
import static test.LodeRunner.*;
import test.Runner.runner_t;

/**
 * 
 * @author Leo
 */
public class GlobalState {
    // Global game state information

    // Runner state information
    public static runner_t runner = new Runner.runner_t();

    // Input handler - default to keyAction() which gets input from joystick routine
    //keyFcn inputHandler = 0;

    // Guard state information
    public static guard_t[] guard = new guard_t[MAX_GUARDS];

    // Guards currently in holes
    public static shakeGuard_t[] shake = new shakeGuard_t[MAX_GUARDS];

    static {
        for (int i = 0; i < guard.length; i++) {
            guard[i] = new guard_t();
        }
        for (int i = 0; i < shake.length; i++) {
            shake[i] = new shakeGuard_t();
        }
    }
    
}
