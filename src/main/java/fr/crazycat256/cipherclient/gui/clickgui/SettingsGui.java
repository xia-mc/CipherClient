/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.clickgui;

import fr.crazycat256.cipherclient.CipherClient;
import fr.crazycat256.cipherclient.gui.components.windows.SettingsWindow;
import fr.crazycat256.cipherclient.systems.module.Module;
import org.lwjgl.input.Keyboard;

public class SettingsGui extends ClientGuiScreen {

    public final SettingsWindow mainWindow;

    public SettingsGui(Module module) {
        this.mainWindow = new SettingsWindow(module);
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        mainWindow.drawComponent(mouseX, mouseY);
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        mainWindow.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseMovedOrUp(int mouseX, int mouseY, int button) {
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
