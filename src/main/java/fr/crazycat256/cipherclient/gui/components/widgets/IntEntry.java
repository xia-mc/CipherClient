/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.components.widgets;

public class IntEntry extends Entry<Integer> {

    public IntEntry(Integer defaultValue) {
        super(defaultValue);
    }

    @Override
    public String getText() {
        return String.valueOf(getValue());
    }

    @Override
    public int getMaxWidth() {
        return 24;
    }
}
