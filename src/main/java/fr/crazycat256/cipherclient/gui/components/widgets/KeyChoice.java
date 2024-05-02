/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.components.widgets;

import fr.crazycat256.cipherclient.utils.GLUtils;
import org.lwjgl.input.Keyboard;

public class KeyChoice extends KeyInputWidget<Integer> {

    private final int maxWidth;

    public KeyChoice(int defaultValue) {
        super(defaultValue);
        this.maxWidth = labelFont.getStringWidth(Keyboard.getKeyName(Keyboard.KEY_ESCAPE)) + 2;
        this.setHeight(10);
    }

    @Override
    protected void draw(int mouseX, int mouseY) {

        String label = isFocused() ? "..." : Keyboard.getKeyName(this.value);
        this.width = labelFont.getStringWidth(label) + 2;

        GLUtils.drawBorderedRect(0, 0, width, height, borderColor, highlightColor);
        labelFont.drawCenteredString(label, width / 2f, (height - labelFont.getFontHeight()) / 2f + 0.5f, textColor);
    }

    @Override
    protected void onKeyTyped(char typedChar, int keyCode) {
        if (isFocused()) {
            if (keyCode == Keyboard.KEY_ESCAPE) {
                keyCode = Keyboard.KEY_NONE;
            }
            this.value = keyCode;
            setFocused(false);
            onChanged(this.value);
        }
    }

    @Override
    public int getMaxWidth() {
        return maxWidth;
    }
}
