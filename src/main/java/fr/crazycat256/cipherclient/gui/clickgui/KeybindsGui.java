/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.clickgui;

import fr.crazycat256.cipherclient.CipherClient;
import fr.crazycat256.cipherclient.gui.components.windows.KeybindsWindow;
import org.lwjgl.input.Keyboard;

public class KeybindsGui extends ClientGuiScreen {

    private final KeybindsWindow mainWindow;

    public KeybindsGui() {
        this.mainWindow = new KeybindsWindow();
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        mainWindow.drawComponent(mouseX, mouseY);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        mainWindow.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        mainWindow.mouseMovedOrUp(mouseX, mouseY, button);
    }

    @Override
    protected void keyTyped(char c, int key) {
        if (!mainWindow.keyTyped(c, key) && key == Keyboard.KEY_ESCAPE) {
            this.mc.displayGuiScreen(CipherClient.clickGui);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
