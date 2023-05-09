package test;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import static test.LodeRunner.*;

/**
 * 
 * @author Leo
 */
public class Key extends KeyAdapter {
    
    public static final boolean[] KEYS_DOWN = new boolean[256];
    
    public static int keyAction() {
        int act = ACT_UNKNOWN;

        //int joy = joy_read(0);
        if (KEYS_DOWN[KeyEvent.VK_Z]) { //joy & JOY_BTN_1_MASK) {
            act = ACT_DIG_LEFT;
        } else if (KEYS_DOWN[KeyEvent.VK_X]) { //joy & JOY_BTN_2_MASK) {
            act = ACT_DIG_RIGHT;
        } else if (KEYS_DOWN[KeyEvent.VK_UP]) { //joy & JOY_UP_MASK) {
            act = ACT_UP;
        } else if (KEYS_DOWN[KeyEvent.VK_DOWN]) { //joy & JOY_DOWN_MASK) {
            act = ACT_DOWN;
        } else if (KEYS_DOWN[KeyEvent.VK_LEFT]) { //joy & JOY_LEFT_MASK) {
            act = ACT_LEFT;
        } else if (KEYS_DOWN[KeyEvent.VK_RIGHT]) { //joy & JOY_RIGHT_MASK) {
            act = ACT_RIGHT;
        } else if (KEYS_DOWN[KeyEvent.VK_ENTER]) { //joy & JOY_BTN_4_MASK) {
            act = ACT_START;
        }
        // else if (joy) {
        //     printf("joy mask 0x%x\n",joy);
        // }
        return act;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        KEYS_DOWN[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        KEYS_DOWN[e.getKeyCode()] = false;
    }

}
