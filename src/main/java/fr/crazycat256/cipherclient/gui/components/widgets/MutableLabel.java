/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.components.widgets;

public class MutableLabel extends Label {

    public final int maxWidth;

    public MutableLabel(String text, int maxWidth) {
        super(text);
        this.maxWidth = maxWidth;
    }

    public void setText(String text) {
        this.text = text;
        setWidth(labelFont.getStringWidth(text) + 2);
    }

    @Override
    public int getMaxWidth() {
        return maxWidth;
    }
}
