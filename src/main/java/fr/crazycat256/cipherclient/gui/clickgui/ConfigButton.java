/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.clickgui;

import fr.crazycat256.cipherclient.gui.components.widgets.Button;
import fr.crazycat256.cipherclient.utils.GLUtils;
import fr.crazycat256.cipherclient.utils.font.Fonts;
import fr.crazycat256.cipherclient.utils.font.GlyphPageFontRenderer;
import net.minecraft.client.gui.GuiScreen;

import static fr.crazycat256.cipherclient.CipherClient.mc;

public class ConfigButton extends Button {

    private final GuiScreen screen;
    private final GlyphPageFontRenderer font = Fonts.getFont(20);

    public ConfigButton(String label, GuiScreen screen) {
        super(label, null);
        this.screen = screen;
        this.setWidth(font.getStringWidth(label) + 10);
        this.setHeight(20);
    }

    @Override
    protected void draw(int mouseX, int mouseY) {
        if (isMouseOver(mouseX, mouseY)) {
            GLUtils.drawRect(0, 0, getWidth(), getHeight(), accentColor);
        } else {
            GLUtils.drawRect(0, 0, getWidth(), getHeight(), backColor);
        }
        font.drawCenteredStringWithShadow(label, (getWidth() - 1) / 2f, (getHeight() - font.getFontHeight()) / 2f, textColor);
    }

    @Override
    protected void onMouseClick(int mouseX, int mouseY, int button) {
        if (button == 0) {
            mc.displayGuiScreen(screen);
        }
    }
}
