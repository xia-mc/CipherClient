/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.clickgui;

import fr.crazycat256.cipherclient.CipherClient;
import fr.crazycat256.cipherclient.gui.components.widgets.Button;
import fr.crazycat256.cipherclient.gui.components.windows.CategoryWindow;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.utils.GLUtils;

import static fr.crazycat256.cipherclient.CipherClient.mc;

public class ModuleButton extends Button {

    public final CategoryWindow window;
    public final Module module;
    public final String title;
    protected boolean state;

    public ModuleButton(CategoryWindow window, Module handler, String title, int y) {
        super(title, null);
        this.window = window;
        this.module = handler;
        this.title = title;

        this.setY(y);
        this.setHeight(14);
        this.setTooltip(module.description);
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public int getMinWidth() {
        return labelFont.getStringWidth(title) + 10;
    }

    @Override
    protected void draw(int mouseX, int mouseY) {
        int color;
        if (state) {
            if (isMouseOver(mouseX, mouseY)) {
                color = highlightHoverColor;
            } else {
                color = highlightColor;
            }
        } else {
            if (isMouseOver(mouseX, mouseY)) {
                color = accentColor;
            } else {
                color = backColor;
            }
        }
        if (state) {
            GLUtils.drawRect(2, 0, width, height, color);
            GLUtils.drawRect(0, 0, 2, height, guiColor);
        } else {
            GLUtils.drawRect(0, 0, width, height, color);
        }
        labelFont.drawCenteredString(title, width / 2f, height / 2f - labelFont.getFontHeight() / 2f, textColor);
    }

    @Override
    protected void onMouseClick(int mouseX, int mouseY, int button) {
        CipherClient.clickGui.sendPanelToFront(this.window);
        if (button == 0 && module.isWorking()) {
            this.module.toggle();
        } else if (button == 1) {
            mc.displayGuiScreen(module.gui);
        }
    }

}
