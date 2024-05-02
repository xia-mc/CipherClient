/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.components.widgets;

public class DoubleEntry extends Entry<Double> {

    public DoubleEntry(Double defaultValue) {
        super(defaultValue);
    }

    @Override
    public String getText() {
        return String.format("%.2f", this.getValue());
    }

    @Override
    public int getMaxWidth() {
        return 32;
    }
}
