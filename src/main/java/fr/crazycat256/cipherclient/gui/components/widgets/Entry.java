/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.components.widgets;

import fr.crazycat256.cipherclient.utils.GLUtils;

/**
 * This will be a widget that allows the user to input text.
 * But currently, it's just a label with a border around it
 * @param <T> The type of the value that this entry will hold
 */
public abstract class Entry<T> extends InputWidget<T> {

    // TODO: Implement the key-pressing functionality
    public Entry(T defaultValue) {
        super(defaultValue);

        setWidth(getMaxWidth());
        setHeight(10);
    }

    @Override
    protected void draw(int mouseX, int mouseY) {
        GLUtils.drawBorderedRect(0, 0, width, height, borderColor, highlightColor);
        labelFont.drawCenteredString(getText(), this.width / 2f, (this.height - labelFont.getFontHeight()) / 2f + 0.5f, textColor);
    }

    public abstract String getText();

    @Override
    public abstract int getMaxWidth();
}
