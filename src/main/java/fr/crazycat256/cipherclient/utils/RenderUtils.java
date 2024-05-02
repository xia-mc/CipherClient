/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.utils;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.events.Handler;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class RenderUtils {

    private static final ArrayList<TickingBox> thickingBoxes = new ArrayList<>();

    public static void renderBlock(int x, int y, int z, int color) {

        double renderX = x - RenderManager.renderPosX;
        double renderY = y - RenderManager.renderPosY;
        double renderZ = z - RenderManager.renderPosZ;
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        GL11.glPushMatrix();
        GL11.glTranslated(renderX, renderY, renderZ);
        GL11.glColor3f(red, green, blue);
        AxisAlignedBB boundingBox = AxisAlignedBB.getBoundingBox(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        GL11.glColor4f(red, green, blue, 0.1f);
        GLUtils.startDrawingESPs(boundingBox, red, green, blue);
        GL11.glPopMatrix();

    }

    public static void renderBox(AxisAlignedBB box, int lineColor, int sideColor) {
        renderBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, lineColor, sideColor);
    }
    public static void renderBox(double x1, double y1, double z1, double x2, double y2, double z2, int lineColor, int sideColor) {
        double renderX = x1 - RenderManager.renderPosX;
        double renderY = y1 - RenderManager.renderPosY;
        double renderZ = z1 - RenderManager.renderPosZ;
        float lineAlpha = (lineColor >> 24 & 255) / 255.0F;
        float lineRed = (lineColor >> 16 & 255) / 255.0F;
        float lineGreen = (lineColor >> 8 & 255) / 255.0F;
        float lineBlue = (lineColor & 255) / 255.0F;
        float sideAlpha = (sideColor >> 24 & 255) / 255.0F;
        float sideRed = (sideColor >> 16 & 255) / 255.0F;
        float sideGreen = (sideColor >> 8 & 255) / 255.0F;
        float sideBlue = (sideColor & 255) / 255.0F;
        GL11.glPushMatrix();
        GL11.glTranslated(renderX, renderY, renderZ);
        GL11.glColor4f(lineRed, lineGreen, lineBlue, lineAlpha);
        AxisAlignedBB boundingBox = AxisAlignedBB.getBoundingBox(0.0, 0.0, 0.0, x2 - x1, y2 - y1, z2 - z1);
        GLUtils.startDrawingLines(boundingBox, lineRed, lineGreen, lineBlue, lineAlpha);
        GL11.glColor4f(sideRed, sideGreen, sideBlue, sideAlpha);
        GLUtils.startDrawingSides(boundingBox, sideRed, sideGreen, sideBlue, sideAlpha);
        GL11.glPopMatrix();

    }



    public static void renderTickingBlock(int x, int y, int z, int ticks, int lineColor, int sideColor) {
        renderTickingBlock(new Vec3i(x, y, z), ticks, lineColor, sideColor);
    }

    public static void renderTickingBlock(Vec3i block, int ticks, int lineColor, int sideColor) {
        thickingBoxes.add(new TickingBox(block, ticks, lineColor, sideColor));
    }

    public static void renderTickingBox(AxisAlignedBB box, int ticks, int lineColor, int sideColor) {
        thickingBoxes.add(new TickingBox(box, ticks, lineColor, sideColor));
    }


    @Handler
    private void onRenderWorldLast(RenderWorldLastEvent event) {
        thickingBoxes.forEach(TickingBox::render);
    }

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        thickingBoxes.removeIf(TickingBox::isDead);
    }


    private static class TickingBox {
        public final AxisAlignedBB box;

        private final long startTime;
        private final int duration;

        private final Color lineColor;
        private final Color sideColor;

        public TickingBox(AxisAlignedBB box, int ticks, int lineColor, int sideColor) {
            this.box = box;
            this.startTime = System.currentTimeMillis();
            this.duration = ticks * 50;
            this.lineColor = new Color(lineColor);
            this.sideColor = new Color(sideColor);
        }

        public TickingBox(Vec3i vec3i, int ticks, int lineColor, int sideColor) {
            this(AxisAlignedBB.getBoundingBox(vec3i.x, vec3i.y, vec3i.z, vec3i.x + 1, vec3i.y + 1, vec3i.z + 1), ticks, lineColor, sideColor);
        }

        public boolean isDead() {
            return System.currentTimeMillis() - startTime > duration;
        }

        public void render() {
            Color line = new Color(lineColor.red, lineColor.green, lineColor.blue, (int) (lineColor.alpha * (1 - (System.currentTimeMillis() - startTime) / (double) duration)));
            Color side = new Color(sideColor.red, sideColor.green, sideColor.blue, (int) (sideColor.alpha * (1 - (System.currentTimeMillis() - startTime) / (double) duration)));
            renderBox(box, line.getValue(), side.getValue());
        }
    }
}
