/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.clickgui;

import fr.crazycat256.cipherclient.gui.components.widgets.Button;
import fr.crazycat256.cipherclient.utils.GLUtils;
import fr.crazycat256.cipherclient.utils.font.Fonts;
import fr.crazycat256.cipherclient.utils.font.GlyphPageFontRenderer;

public class PlusButton extends Button {

    private boolean focused = false;
    private final GlyphPageFontRenderer font = Fonts.getFont(40);

    public PlusButton() {
        super("+", null);
        this.setSize(20, 20);

    }

    public boolean isFocused() {
        return focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Override
    protected void onMouseClick(int mouseX, int mouseY, int button) {
        if (button == 0) {
            focused = !focused;
        }
    }

    @Override
    protected void draw(int mouseX, int mouseY) {
        this.label = isFocused() ? "-" : "+";
        float heightOffset = isFocused() ? -1f : 0.5f;
        if (isMouseOver(mouseX, mouseY)) {
            GLUtils.drawRect(0, 0, getWidth(), getHeight(), accentColor);
        } else {
            GLUtils.drawRect(0, 0, getWidth(), getHeight(), backColor);
        }
        font.drawCenteredString(this.label, getWidth() / 2f, (getHeight() - font.getFontHeight()) / 2f + heightOffset, textColor);
    }
}
