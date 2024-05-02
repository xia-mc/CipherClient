/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.render;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.systems.module.Category;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

public class Breadcrumb extends Module {

    private final CopyOnWriteArrayList<double[]> positionsList = new CopyOnWriteArrayList<>();
    private Vec3 prevPos = null;

    public Breadcrumb() {
        super("breadcrumb", "Draws a line behind you", Category.RENDER);
    }

    @Override
    public void onDisable() {
        positionsList.clear();
    }

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        while (positionsList.size() >= 1024) {
            positionsList.remove(0);
        }
        Vec3 pos = mc.thePlayer.getPosition(1.0f);
        if (prevPos != null && (pos.xCoord != prevPos.xCoord || pos.yCoord != prevPos.yCoord || pos.zCoord != prevPos.zCoord)) {
            double x = RenderManager.renderPosX;
            double y = RenderManager.renderPosY;
            double z = RenderManager.renderPosZ;
            positionsList.add(new double[]{x, y - mc.thePlayer.height, z});
        }
        prevPos = pos;
    }

    @Handler
    private void onWorldRender(RenderWorldLastEvent event) {
        GL11.glPushMatrix();
        GL11.glLineWidth(2.0f);
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glEnable(3042);
        GL11.glDisable(2929);
        GL11.glBegin(3);
        positionsList.forEach((pos) -> {
            double distance = Math.abs(Math.hypot(pos[0] - RenderManager.renderPosX, pos[1] - RenderManager.renderPosY));
            if (!(distance > 100.0)) {
                GL11.glColor4f(0.0f, 1.0f, 0.0f, (1.0f - (float) (distance / 100.0)));
                GL11.glVertex3d((pos[0] - RenderManager.renderPosX), (pos[1] - RenderManager.renderPosY), (pos[2] - RenderManager.renderPosZ));
            }
        });
        GL11.glEnd();
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glEnable(2896);
        GL11.glPopMatrix();
    }
}
