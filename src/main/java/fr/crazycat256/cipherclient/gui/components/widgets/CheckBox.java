/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.components.widgets;

import fr.crazycat256.cipherclient.utils.GLUtils;

public class CheckBox extends InputWidget<Boolean> {


    public CheckBox(boolean defaultValue) {
        super(defaultValue);
        this.setWidth(10);
        this.setHeight(10);
    }


    @Override
    protected void draw(int mouseX, int mouseY) {
        GLUtils.drawBorderedRect(0, 0, getWidth(), getHeight(), borderColor, highlightColor);
        if (this.value) {
            GLUtils.drawRect(1.5f, 1.5f, getWidth() - 1.5f, getHeight() - 1.5f, guiColor);
        }
    }

    @Override
    protected void onMouseClick(int mouseX, int mouseY, int button) {
        this.value = !this.value;
        onChanged(this.value);
    }
}
