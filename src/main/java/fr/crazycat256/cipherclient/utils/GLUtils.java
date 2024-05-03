/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.utils;

import fr.crazycat256.cipherclient.systems.module.render.xray.XRayBlock;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.List;

public class GLUtils {

    public static boolean hasClearedDepth = false;


    public static int getColor(int a, int r, int g, int b) {
        return a << 24 | r << 16 | g << 8 | b;
    }

    public static int getColor(int r, int g, int b) {
        return 255 << 24 | r << 16 | g << 8 | b;
    }

    public static void drawRect(float paramXStart, float paramYStart, float paramXEnd, float paramYEnd, int paramColor) {

        float alpha = (paramColor >> 24 & 255) / 255.0f;
        float red = (paramColor >> 16 & 255) / 255.0f;
        float green = (paramColor >> 8 & 255) / 255.0f;
        float blue = (paramColor & 255) / 255.0f;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glPushMatrix();
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glBegin(7);
        GL11.glVertex2d(paramXEnd, paramYStart);
        GL11.glVertex2d(paramXStart, paramYStart);
        GL11.glVertex2d(paramXStart, paramYEnd);
        GL11.glVertex2d(paramXEnd, paramYEnd);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }


    public static void drawBorderedRect(double x, double y, double x2, double y2, int borderColor, int fillColor) {
        float f = (borderColor >> 24 & 255) / 255.0f;
        float f1 = (borderColor >> 16 & 255) / 255.0f;
        float f2 = (borderColor >> 8 & 255) / 255.0f;
        float f3 = (borderColor & 255) / 255.0f;
        GLUtils.drawRect((float) x, (float) y, (float) x2, (float) y2, fillColor);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glDisable(3042);
        GL11.glPushMatrix();
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glLineWidth(1f);
        GL11.glBegin(1);
        GL11.glVertex2d(x, y - 0.5);
        GL11.glVertex2d(x, y2 + 0.5);
        GL11.glVertex2d(x2, y2 + 0.5);
        GL11.glVertex2d(x2, y - 0.5);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3042);
        GL11.glEnable(3553);
        GL11.glDisable(2848);
    }

    public static int drawHoveringText(List<String> text, int x, int y, FontRenderer font) {
        int t = 0;
        if (!text.isEmpty()) {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            int k = 0;

            for (String s : text) {
                int l = font.getStringWidth(s);

                if (l > k) {
                    k = l;
                }
            }

            int j2 = x;
            int k2 = y;
            int i1 = 8;

            if (text.size() > 1) {
                i1 += 2 + (text.size() - 1) * 10;
            }

            t = i1;

            RenderItem.getInstance().zLevel = 300.0F;
            int j1 = -267386864;
            drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
            drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
            drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
            drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
            drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
            int k1 = 1347420415;
            int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
            drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
            drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
            drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
            drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);

            for (int i2 = 0; i2 < text.size(); ++i2) {
                String s1 = text.get(i2);
                font.drawStringWithShadow(s1, j2, k2, -1);

                if (i2 == 0) {
                    k2 += 2;
                }

                k2 += 10;
            }

            RenderItem.getInstance().zLevel = 0.0F;
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        }
        return t;
    }

    public static  void drawGradientRect(int p_73733_1_, int p_73733_2_, int p_73733_3_, int p_73733_4_, int p_73733_5_, int p_73733_6_) {
        float f = (p_73733_5_ >> 24 & 255) / 255.0F;
        float f1 = (p_73733_5_ >> 16 & 255) / 255.0F;
        float f2 = (p_73733_5_ >> 8 & 255) / 255.0F;
        float f3 = (p_73733_5_ & 255) / 255.0F;
        float f4 = (p_73733_6_ >> 24 & 255) / 255.0F;
        float f5 = (p_73733_6_ >> 16 & 255) / 255.0F;
        float f6 = (p_73733_6_ >> 8 & 255) / 255.0F;
        float f7 = (p_73733_6_ & 255) / 255.0F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(f1, f2, f3, f);
        tessellator.addVertex(p_73733_3_, p_73733_2_, 300f);
        tessellator.addVertex(p_73733_1_, p_73733_2_, 300f);
        tessellator.setColorRGBA_F(f5, f6, f7, f4);
        tessellator.addVertex(p_73733_1_, p_73733_4_, 300f);
        tessellator.addVertex(p_73733_3_, p_73733_4_, 300f);
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static void drawOutlinedBoundingBox(AxisAlignedBB par1AxisAlignedBB) {

        Tessellator var2 = Tessellator.instance;
        var2.startDrawing(3);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        var2.draw();
        var2.startDrawing(3);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        var2.draw();
        var2.startDrawing(1);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        var2.draw();
    }

    public static void drawBoundingBox(AxisAlignedBB AxisAlignedBB) {

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertex(AxisAlignedBB.minX, AxisAlignedBB.minY, AxisAlignedBB.minZ);
        tessellator.addVertex(AxisAlignedBB.minX, AxisAlignedBB.maxY, AxisAlignedBB.minZ);
        tessellator.addVertex(AxisAlignedBB.maxX, AxisAlignedBB.minY, AxisAlignedBB.minZ);
        tessellator.addVertex(AxisAlignedBB.maxX, AxisAlignedBB.maxY, AxisAlignedBB.minZ);
        tessellator.addVertex(AxisAlignedBB.maxX, AxisAlignedBB.minY, AxisAlignedBB.maxZ);
        tessellator.addVertex(AxisAlignedBB.maxX, AxisAlignedBB.maxY, AxisAlignedBB.maxZ);
        tessellator.addVertex(AxisAlignedBB.minX, AxisAlignedBB.minY, AxisAlignedBB.maxZ);
        tessellator.addVertex(AxisAlignedBB.minX, AxisAlignedBB.maxY, AxisAlignedBB.maxZ);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.addVertex(AxisAlignedBB.maxX, AxisAlignedBB.maxY, AxisAlignedBB.minZ);
        tessellator.addVertex(AxisAlignedBB.maxX, AxisAlignedBB.minY, AxisAlignedBB.minZ);
        tessellator.addVertex(AxisAlignedBB.minX, AxisAlignedBB.maxY, AxisAlignedBB.minZ);
        tessellator.addVertex(AxisAlignedBB.minX, AxisAlignedBB.minY, AxisAlignedBB.minZ);
        tessellator.addVertex(AxisAlignedBB.minX, AxisAlignedBB.maxY, AxisAlignedBB.maxZ);
        tessellator.addVertex(AxisAlignedBB.minX, AxisAlignedBB.minY, AxisAlignedBB.maxZ);
        tessellator.addVertex(AxisAlignedBB.maxX, AxisAlignedBB.maxY, AxisAlignedBB.maxZ);
        tessellator.addVertex(AxisAlignedBB.maxX, AxisAlignedBB.minY, AxisAlignedBB.maxZ);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.addVertex(AxisAlignedBB.minX, AxisAlignedBB.maxY, AxisAlignedBB.minZ);
        tessellator.addVertex(AxisAlignedBB.maxX, AxisAlignedBB.maxY, AxisAlignedBB.minZ);
        tessellator.addVertex(AxisAlignedBB.maxX, AxisAlignedBB.maxY, AxisAlignedBB.maxZ);
        tessellator.addVertex(AxisAlignedBB.minX, AxisAlignedBB.maxY, AxisAlignedBB.maxZ);
        tessellator.addVertex(AxisAlignedBB.minX, AxisAlignedBB.maxY, AxisAlignedBB.minZ);
        tessellator.addVertex(AxisAlignedBB.minX, AxisAlignedBB.maxY, AxisAlignedBB.maxZ);
        tessellator.addVertex(AxisAlignedBB.maxX, AxisAlignedBB.maxY, AxisAlignedBB.maxZ);
        tessellator.addVertex(AxisAlignedBB.maxX, AxisAlignedBB.maxY, AxisAlignedBB.minZ);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.addVertex(AxisAlignedBB.minX, AxisAlignedBB.minY, AxisAlignedBB.minZ);
        tessellator.addVertex(AxisAlignedBB.maxX, AxisAlignedBB.minY, AxisAlignedBB.minZ);
        tessellator.addVertex(AxisAlignedBB.maxX, AxisAlignedBB.minY, AxisAlignedBB.maxZ);
        tessellator.addVertex(AxisAlignedBB.minX, AxisAlignedBB.minY, AxisAlignedBB.maxZ);
        tessellator.addVertex(AxisAlignedBB.minX, AxisAlignedBB.minY, AxisAlignedBB.minZ);
        tessellator.addVertex(AxisAlignedBB.minX, AxisAlignedBB.minY, AxisAlignedBB.maxZ);
        tessellator.addVertex(AxisAlignedBB.maxX, AxisAlignedBB.minY, AxisAlignedBB.maxZ);
        tessellator.addVertex(AxisAlignedBB.maxX, AxisAlignedBB.minY, AxisAlignedBB.minZ);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.addVertex(AxisAlignedBB.minX, AxisAlignedBB.minY, AxisAlignedBB.minZ);
        tessellator.addVertex(AxisAlignedBB.minX, AxisAlignedBB.maxY, AxisAlignedBB.minZ);
        tessellator.addVertex(AxisAlignedBB.minX, AxisAlignedBB.minY, AxisAlignedBB.maxZ);
        tessellator.addVertex(AxisAlignedBB.minX, AxisAlignedBB.maxY, AxisAlignedBB.maxZ);
        tessellator.addVertex(AxisAlignedBB.maxX, AxisAlignedBB.minY, AxisAlignedBB.maxZ);
        tessellator.addVertex(AxisAlignedBB.maxX, AxisAlignedBB.maxY, AxisAlignedBB.maxZ);
        tessellator.addVertex(AxisAlignedBB.maxX, AxisAlignedBB.minY, AxisAlignedBB.minZ);
        tessellator.addVertex(AxisAlignedBB.maxX, AxisAlignedBB.maxY, AxisAlignedBB.minZ);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.addVertex(AxisAlignedBB.minX, AxisAlignedBB.maxY, AxisAlignedBB.maxZ);
        tessellator.addVertex(AxisAlignedBB.minX, AxisAlignedBB.minY, AxisAlignedBB.maxZ);
        tessellator.addVertex(AxisAlignedBB.minX, AxisAlignedBB.maxY, AxisAlignedBB.minZ);
        tessellator.addVertex(AxisAlignedBB.minX, AxisAlignedBB.minY, AxisAlignedBB.minZ);
        tessellator.addVertex(AxisAlignedBB.maxX, AxisAlignedBB.maxY, AxisAlignedBB.minZ);
        tessellator.addVertex(AxisAlignedBB.maxX, AxisAlignedBB.minY, AxisAlignedBB.minZ);
        tessellator.addVertex(AxisAlignedBB.maxX, AxisAlignedBB.maxY, AxisAlignedBB.maxZ);
        tessellator.addVertex(AxisAlignedBB.maxX, AxisAlignedBB.minY, AxisAlignedBB.maxZ);
        tessellator.draw();
    }



    public static void startDrawingESPs(AxisAlignedBB bb, float r, float b2, float g) {

        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(1.5f);
        GL11.glDisable(2896);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glColor4d(r, b2, g, 0.1850000023841858);
        GLUtils.drawBoundingBox(bb);
        GL11.glColor4d(r, b2, g, 1.0);
        GLUtils.drawOutlinedBoundingBox(bb);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2896);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void startDrawingLines(AxisAlignedBB bb, float r, float b2, float g, float a) {

        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(1.5f);
        GL11.glDisable(2896);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glColor4d(r, b2, g, a);
        GLUtils.drawOutlinedBoundingBox(bb);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2896);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void startDrawingSides(AxisAlignedBB bb, float r, float b2, float g, float a) {

        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(1.5f);
        GL11.glDisable(2896);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glColor4d(r, b2, g, a);
        GLUtils.drawBoundingBox(bb);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2896);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void renderBlock(int x, int y, int z, XRayBlock block) {

        GL11.glColor4ub(((byte) block.r), ((byte) block.g), ((byte) block.b), ((byte) block.a));
        GL11.glVertex3f(x, y, z);
        GL11.glVertex3f((x + 1), y, z);
        GL11.glVertex3f((x + 1), y, z);
        GL11.glVertex3f((x + 1), y, (z + 1));
        GL11.glVertex3f(x, y, z);
        GL11.glVertex3f(x, y, (z + 1));
        GL11.glVertex3f(x, y, (z + 1));
        GL11.glVertex3f((x + 1), y, (z + 1));
        GL11.glVertex3f(x, (y + 1), z);
        GL11.glVertex3f((x + 1), (y + 1), z);
        GL11.glVertex3f((x + 1), (y + 1), z);
        GL11.glVertex3f((x + 1), (y + 1), (z + 1));
        GL11.glVertex3f(x, (y + 1), z);
        GL11.glVertex3f(x, (y + 1), (z + 1));
        GL11.glVertex3f(x, (y + 1), (z + 1));
        GL11.glVertex3f((x + 1), (y + 1), (z + 1));
        GL11.glVertex3f(x, y, z);
        GL11.glVertex3f(x, (y + 1), z);
        GL11.glVertex3f(x, y, (z + 1));
        GL11.glVertex3f(x, (y + 1), (z + 1));
        GL11.glVertex3f((x + 1), y, z);
        GL11.glVertex3f((x + 1), (y + 1), z);
        GL11.glVertex3f((x + 1), y, (z + 1));
        GL11.glVertex3f((x + 1), (y + 1), (z + 1));
    }
}
