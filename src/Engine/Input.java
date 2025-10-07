package Engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

/**
 * Input class for engine.
 */
public class Input implements KeyListener {

    private static HashMap<Integer, Boolean> keyMap = new HashMap<Integer, Boolean>();

    /**
     * Check if the given key is pressed.
     * @param keyCode KeyCode
     * @return true if the given key is pressed
     */
    public static boolean isKeyPressed(int keyCode) {
        if (!keyMap.containsKey(keyCode)) {
            return false;
        }
        return keyMap.get(keyCode);
    }

    // public boolean keyReleased(int keyCode) {
    //     return false;
    // }

    // public boolean wasKeyPressedThisFrame(int keyCode) {
    //     return false;
    // }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (!keyMap.containsKey(keyCode)) {
            keyMap.put(keyCode, true);
        }
        keyMap.replace(keyCode, true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (!keyMap.containsKey(keyCode)) {
            keyMap.put(keyCode, false);
        }
        keyMap.replace(keyCode, false);
    }

    
}
