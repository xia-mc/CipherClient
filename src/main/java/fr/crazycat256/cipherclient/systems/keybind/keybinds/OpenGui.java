/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.keybind.keybinds;

import fr.crazycat256.cipherclient.CipherClient;
import fr.crazycat256.cipherclient.systems.keybind.Keybind;
import org.lwjgl.input.Keyboard;

import static fr.crazycat256.cipherclient.CipherClient.mc;

public class OpenGui extends Keybind {

    public OpenGui() {
        super("Open GUI", Keyboard.KEY_RSHIFT);
    }

    @Override
    public void onPress() {
        mc.displayGuiScreen(CipherClient.clickGui);
    }
}
