/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.render;

import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.utils.GLUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

/**
 * Copied from <a href="https://github.com/radioegor146/ehacks-pro/blob/master/src/main/java/ehacks/mod/modulesystem/classes/vanilla/BlockOverlay.java">ehacks-pro</a>
 * @author radioegor146
 */
public class BlockOverlay extends Module {

    public BlockOverlay() {
        super("block-overlay", "Shows AABB of selected block", Category.RENDER);
    }

    @Handler
    private void onWorldRender(RenderWorldLastEvent event) {
        MovingObjectPosition position = mc.objectMouseOver;
        Block block = mc.theWorld.getBlock(position.blockX, position.blockY, position.blockZ);

        if (block.getMaterial() != Material.air) {
            mc.entityRenderer.disableLightmap(0);

            GL11.glPushMatrix();
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glLineWidth(1.0f);
            GL11.glDisable(2896);
            GL11.glDisable(3553);
            GL11.glEnable(2848);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            double blockX = RenderManager.renderPosX;
            double blockY = RenderManager.renderPosY;
            double blockZ = RenderManager.renderPosZ;
            Block block2 = mc.theWorld.getBlock(mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ);
            GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.2f);
            GLUtils.drawOutlinedBoundingBox(block2.getSelectedBoundingBoxFromPool(mc.theWorld, position.blockX, position.blockY, position.blockZ).expand(0.002, 0.002, 0.002).getOffsetBoundingBox(-blockX, -blockY, -blockZ));
            GL11.glColor4f(0.0f, 0.4f, 0.0f, 0.2f);
            GLUtils.drawBoundingBox(block2.getSelectedBoundingBoxFromPool(mc.theWorld, position.blockX, position.blockY, position.blockZ).expand(0.002, 0.002, 0.002).getOffsetBoundingBox(-blockX, -blockY, -blockZ));
            GL11.glLineWidth(1.0f);
            GL11.glDisable(2848);
            GL11.glEnable(3553);
            GL11.glEnable(2896);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
            GL11.glPopMatrix();

            mc.entityRenderer.enableLightmap(0);
        }
    }
}
