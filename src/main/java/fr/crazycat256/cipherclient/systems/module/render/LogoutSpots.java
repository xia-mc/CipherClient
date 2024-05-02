/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.render;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.events.custom.PacketEvent;
import fr.crazycat256.cipherclient.gui.settings.BoolSetting;
import fr.crazycat256.cipherclient.gui.settings.IntSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.utils.EntityFakePlayer;
import fr.crazycat256.cipherclient.utils.GLUtils;
import fr.crazycat256.cipherclient.utils.RenderUtils;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class LogoutSpots extends Module {

    private final List<LogoutInfo> spots = new ArrayList<>();

    public LogoutSpots() {
        super("logout-spots", "Shows where players logged out", Category.RENDER);
    }

    private final Setting<Boolean> maxTime = addSetting(new BoolSetting.Builder()
        .name("max-time")
        .description("Whether to remove logout spots after a certain time")
        .defaultValue(true)
        .build()
    );

    private final Setting<Integer> timeout = addSetting(new IntSetting.Builder()
        .name("timeout")
        .description("The time in seconds before a logout spot is removed")
        .defaultValue(120)
        .min(0)
        .max(600)
        .visible(maxTime::get)
        .build()
    );

    private final Setting<Boolean> maxRange = addSetting(new BoolSetting.Builder()
        .name("max-range")
        .description("Whether to show logout spots outside of the range")
        .defaultValue(false)
        .build()
    );

    private final Setting<Integer> range = addSetting(new IntSetting.Builder()
        .name("range")
        .description("The maximum range to show logout spots in")
        .defaultValue(100)
        .min(0)
        .max(1000)
        .visible(maxRange::get)
        .build()
    );


    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        if (maxTime.get()) {
            long time = System.currentTimeMillis();
            spots.removeIf(info -> time - info.time > timeout.get() * 1000);
        }
    }

    @Handler
    private void onEntityJoin(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityPlayer && event.entity != mc.thePlayer && !(event.entity instanceof EntityFakePlayer)) {
            EntityPlayer p = (EntityPlayer) event.entity;
            spots.removeIf(info -> info.name.equals(p.getDisplayName()));
        }
    }

    @Handler
    private void onPacketReceived(PacketEvent.Receive event) {
        if (event.packet instanceof S13PacketDestroyEntities) {
            S13PacketDestroyEntities packet = (S13PacketDestroyEntities) event.packet;
            for (int id : packet.func_149098_c()) {
                if (mc.theWorld.getEntityByID(id) instanceof EntityPlayer) {
                    EntityPlayer p = (EntityPlayer) mc.theWorld.getEntityByID(id);
                    if (p == mc.thePlayer) continue;
                    spots.add(new LogoutInfo(p.getDisplayName(), Vec3.createVectorHelper(p.posX, p.posY, p.posZ), System.currentTimeMillis()));
                }
            }
        }
    }

    @Handler
    private void onRenderWorldLast(RenderWorldLastEvent event) {
        for (LogoutInfo info : spots) {
            if (!maxRange.get() || mc.thePlayer.getDistance(info.pos.xCoord, info.pos.yCoord, info.pos.zCoord) <= range.get()) {
                Vec3 p1 = info.pos.addVector(-0.3, 0, -0.3);
                Vec3 p2 = info.pos.addVector(0.3, 1.62, 0.3);
                AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(p1.xCoord, p1.yCoord, p1.zCoord, p2.xCoord, p2.yCoord, p2.zCoord);
                RenderUtils.renderBox(bb, GLUtils.getColor(128, 255, 0, 255), GLUtils.getColor(64, 255, 0, 255));
                renderNametag(info);
            }
        }
    }

    private void renderNametag(LogoutInfo info) {
        float pastTranslate = 0F;

        double distance = mc.thePlayer.getDistance(info.pos.xCoord, info.pos.yCoord, info.pos.zCoord);
        double factor = Math.max(1, distance / 10.0);
        float scale = (float) (0.026666672F * factor);

        GL11.glPushMatrix();
        GL11.glTranslatef((float) (info.pos.xCoord - RenderManager.renderPosX), (float) (info.pos.yCoord - RenderManager.renderPosY + 1.62), (float) (info.pos.zCoord - RenderManager.renderPosZ));
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-scale, -scale, scale);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Tessellator tessellator = Tessellator.instance;

        float padding = 2;
        int bgHeight = 6;
        int barHeight = 0;

        GL11.glTranslatef(0F, pastTranslate, 0F);

        float s = 0.5F;

        float size = mc.fontRenderer.getStringWidth(info.name) * s / 2F;


        // Background
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(0, 0, 0, 64);
        tessellator.addVertex(-size - padding, -bgHeight, 0.0D);
        tessellator.addVertex(-size - padding, barHeight + padding - 1, 0.0D);
        tessellator.addVertex(size + padding, barHeight + padding - 1, 0.0D);
        tessellator.addVertex(size + padding, -bgHeight, 0.0D);
        tessellator.draw();

        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glPushMatrix();
        GL11.glTranslatef(-size, -4.5F, 0F);
        GL11.glScalef(s, s, s);
        mc.fontRenderer.drawString(info.name, 0, 0, 0xFFFFFF);

        GL11.glPushMatrix();
        float s1 = 0.75F;
        GL11.glScalef(s1, s1, s1);

        GL11.glPopMatrix();

        GL11.glColor4f(1F, 1F, 1F, 1F);

        s1 = 0.5F;
        GL11.glScalef(s1, s1, s1);
        GL11.glTranslatef(size / (s * s1) * 2 - 16, 0F, 0F);

        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }


    private static class LogoutInfo {
        public final String name;
        public final Vec3 pos;
        public final long time;

        public LogoutInfo(String name, Vec3 pos, long time) {
            this.name = name;
            this.pos = pos;
            this.time = time;
        }
    }

}
