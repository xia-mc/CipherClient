/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.components.widgets;

import fr.crazycat256.cipherclient.utils.GLUtils;

public class ResetButton extends Button {

    public ResetButton(Runnable action) {
        super("X", action);
        this.setSize(10, 10);
    }
    @Override
    protected void draw(int mouseX, int mouseY) {
        GLUtils.drawBorderedRect(0, 0, getWidth(), getHeight(), borderColor, highlightColor);
        labelFont.drawString(label, 1.75f, 0.5f, textColor);
    }
}
