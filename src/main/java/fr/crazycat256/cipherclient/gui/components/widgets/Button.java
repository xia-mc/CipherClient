/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.components.widgets;

import fr.crazycat256.cipherclient.utils.GLUtils;

public class Button extends Widget {

    protected String label;
    private final Runnable action;

    public Button(String label, Runnable action) {
        this.label = label;
        this.action = action;

        this.setWidth(this.labelFont.getStringWidth(label) + 4);
        this.setHeight(12);
    }

    @Override
    protected void draw(int mouseX, int mouseY) {
        GLUtils.drawBorderedRect(0, 0, getWidth(), getHeight(), borderColor, highlightColor);
        labelFont.drawCenteredStringWithShadow(label, (getWidth() - 0.5f) / 2f, (getHeight() - labelFont.getFontHeight()) / 2f, textColor);
    }

    @Override
    protected void onMouseClick(int mouseX, int mouseY, int button) {
        if (button == 0) {
            action.run();
        }
    }
}
