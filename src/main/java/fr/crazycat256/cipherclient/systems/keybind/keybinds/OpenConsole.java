/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.keybind.keybinds;

import fr.crazycat256.cipherclient.systems.keybind.Keybind;
import org.lwjgl.input.Keyboard;

public class OpenConsole extends Keybind {

    public OpenConsole() {
        super("Open Console", Keyboard.KEY_PERIOD);
    }


}
