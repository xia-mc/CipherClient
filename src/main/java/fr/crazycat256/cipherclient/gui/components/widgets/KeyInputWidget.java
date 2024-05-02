/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.components.widgets;

import org.lwjgl.input.Keyboard;

public abstract class KeyInputWidget<T> extends InputWidget<T> {

    /**
     * Whether the element is focused
     */
    private boolean focused = false;


    public KeyInputWidget(T defaultValue) {
        super(defaultValue);
    }

    public boolean isFocused() {
        return focused;
    }
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Override
    protected void onMouseClick(int mouseX, int mouseY, int button) {
        focused = !focused;
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode) {
        if (focused && keyCode == Keyboard.KEY_ESCAPE) {
            super.keyTyped(typedChar, Keyboard.KEY_NONE);
            focused = false;
            return true;
        }
        super.keyTyped(typedChar, keyCode);
        return focused;
    }
}
