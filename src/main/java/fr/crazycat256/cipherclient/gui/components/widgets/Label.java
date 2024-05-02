/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.components.widgets;

public class Label extends Widget {

    protected String text;

    public Label(String text) {
        this.text = text;

        setWidth(labelFont.getStringWidth(text) + 2);
        setHeight(labelFont.getFontHeight() + 2);
    }

    public String getText() {
        return text;
    }

    @Override
    protected void draw(int mouseX, int mouseY) {
        labelFont.drawString(text, 1, 1, textColor);
    }
}
