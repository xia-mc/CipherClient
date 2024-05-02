/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.gui.clickgui;

import fr.crazycat256.cipherclient.gui.Colors;
import fr.crazycat256.cipherclient.utils.GLUtils;
import fr.crazycat256.cipherclient.utils.ReflectUtils;
import fr.crazycat256.cipherclient.utils.font.Fonts;
import fr.crazycat256.cipherclient.utils.font.GlyphPageFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class ClientGuiScreen extends GuiScreen implements Colors {

    public static final int FACTOR = 2;
    private final GlyphPageFontRenderer tooltipFont = Fonts.getFont(18);
    private String tooltip;

    public ClientGuiScreen() {
        super();
        mc = Minecraft.getMinecraft();
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        tooltip = null;

        float scaleFactor = (float) new ScaledResolution(mc, mc.displayWidth, mc.displayHeight).getScaleFactor() / FACTOR;
        mouseX = (int) (mouseX * scaleFactor);
        mouseY = (int) (mouseY * scaleFactor);

        GL11.glScalef(1f / scaleFactor, 1f / scaleFactor, 1f / scaleFactor);

        this.draw(mouseX, mouseY);
        if (tooltip != null && !tooltip.isEmpty()) {
            GLUtils.drawRect(mouseX + 5, mouseY - 12, tooltipFont.getStringWidth(tooltip) + mouseX + 8, mouseY, accentColor);
            tooltipFont.drawStringWithShadow(tooltip, mouseX + 6, mouseY - 11, textColor);
        }

        GL11.glScalef(scaleFactor, scaleFactor, scaleFactor);
    }

    @Override
    public void handleMouseInput() {
        int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        int k = Mouse.getEventButton();

        // Start of the modified code
        float scaleFactor = (float) new ScaledResolution(mc, mc.displayWidth, mc.displayHeight).getScaleFactor() / FACTOR;
        i = (int) (i * scaleFactor);
        j = (int) (j * scaleFactor);
        // End of the modified code

        if (Mouse.getEventButtonState())
        {
            if (this.mc.gameSettings.touchscreen) {
                int field_146298_h = ReflectUtils.get(GuiScreen.class, this, "field_146298_h");
                field_146298_h++;
                ReflectUtils.set(GuiScreen.class, this, "field_146298_h", field_146298_h);
                if (field_146298_h > 0){
                    return;
                }
            }

            ReflectUtils.set(GuiScreen.class, this, "eventButton", k);

            ReflectUtils.set(GuiScreen.class, this, "lastMouseEvent", Minecraft.getSystemTime());
            this.mouseClicked(i, j, ReflectUtils.get(GuiScreen.class, this, "eventButton"));
        }
        else if (k != -1)
        {
            if (this.mc.gameSettings.touchscreen) {
                int field_146298_h = ReflectUtils.get(GuiScreen.class, this, "field_146298_h");
                field_146298_h--;
                ReflectUtils.set(GuiScreen.class, this, "field_146298_h", field_146298_h);
                if (field_146298_h > 0){
                    return;
                }
            }

            ReflectUtils.set(GuiScreen.class, this, "eventButton", -1);
            this.mouseMovedOrUp(i, j, k);
        }
        else if ((int) ReflectUtils.get(GuiScreen.class, this, "eventButton") != -1
                && (long) ReflectUtils.get(GuiScreen.class, this, "lastMouseEvent") > 0L)
        {
            long l = Minecraft.getSystemTime() - (long) ReflectUtils.get(GuiScreen.class, this, "lastMouseEvent");
            this.mouseClickMove(i, j, ReflectUtils.get(GuiScreen.class, this, "eventButton"), l);
        }
    }

    public abstract void draw(int mouseX, int mouseY);
}
