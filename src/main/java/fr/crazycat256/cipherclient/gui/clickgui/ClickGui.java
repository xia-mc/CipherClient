/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.clickgui;

import fr.crazycat256.cipherclient.gui.components.windows.CategoryWindow;
import fr.crazycat256.cipherclient.systems.SystemManager;
import fr.crazycat256.cipherclient.systems.macro.MacroControls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.crazycat256.cipherclient.systems.module.Category;
import net.minecraft.client.Minecraft;

public class ClickGui extends ClientGuiScreen {

    public boolean canInputConsole = false;
    private final List<CategoryWindow> categoryWindows;
    private final PlusButton plusButton;
    private final List<ConfigButton> configButtons;

    public ClickGui() {
        mc = Minecraft.getMinecraft();
        this.categoryWindows = new ArrayList<>();
        int x = 2;
        for (Category category : Category.values()) {
            CategoryWindow window = new CategoryWindow(category, x, 2);
            categoryWindows.add(window);
            x += window.getWidth() + 2;
        }

        this.plusButton = new PlusButton();

        this.configButtons = new ArrayList<>();
        configButtons.add(new ConfigButton("Keybinds", new KeybindsGui()));
        configButtons.add(new ConfigButton("Macros", new MacroControls(this)));

        int maxWidth = 0;
        for (ConfigButton button : configButtons) {
            maxWidth = Math.max(maxWidth, button.getWidth());
        }
        for (ConfigButton button : configButtons) {
            button.setWidth(maxWidth);
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        plusButton.setFocused(false);
        SystemManager.save();
    }

    public void sendPanelToFront(CategoryWindow window) {
        if (categoryWindows.contains(window)) {
            categoryWindows.remove(window);
            categoryWindows.add(categoryWindows.size(), window);
        }
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        for (CategoryWindow window : categoryWindows) {
            window.drawComponent(mouseX, mouseY);
        }
        plusButton.setPos(mc.displayWidth / FACTOR - 22, mc.displayHeight / FACTOR - 22);
        plusButton.drawComponent(mouseX, mouseY);

        if (plusButton.isFocused()) {
            int buttonY = plusButton.getY() - 2;
            for (int i = configButtons.size() - 1; i >= 0; i--) {
                ConfigButton button = configButtons.get(i);
                buttonY -= button.getHeight();
                button.setPos(plusButton.getX() + plusButton.getWidth() - button.getWidth(), buttonY);
                button.drawComponent(mouseX, mouseY);
            }
        }
    }

    @Override
    public void mouseClicked(int x, int y, int button) {
        if (button != 0 && button != 1) {
            return;
        }
        if (plusButton.mouseClicked(x, y, button)) {
            return;
        }
        if (plusButton.isFocused()) {
            for (ConfigButton configButton : configButtons) {
                if (configButton.mouseClicked(x, y, button)) {
                    return;
                }
            }
            plusButton.setFocused(false);
        }
        ArrayList<CategoryWindow> windows = new ArrayList<>(categoryWindows);
        Collections.reverse(windows);
        for (CategoryWindow window : windows) {
            if (window.mouseClicked(x, y, button)) {
                break;
            }
        }
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int state) {
        for (CategoryWindow window : categoryWindows) {
            window.mouseMovedOrUp(mouseX, mouseY, state);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

}
