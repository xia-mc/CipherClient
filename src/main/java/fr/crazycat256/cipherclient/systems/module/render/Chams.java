/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.render;

import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.gui.settings.BoolSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.systems.module.Category;

import fr.crazycat256.cipherclient.utils.CombatUtils;
import fr.crazycat256.cipherclient.utils.GLUtils;
import fr.crazycat256.cipherclient.utils.WorldUtils;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;


import static net.minecraft.client.renderer.entity.RenderManager.*;

public class Chams extends Module {
    public Chams() {
        super("chams", "Allows you to see entities around you", Category.RENDER);
    }

    private final Setting<Boolean> players = addSetting(new BoolSetting.Builder()
        .name("players")
        .description("Render players")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> hostile = addSetting(new BoolSetting.Builder()
        .name("hostile")
        .description("Render hostile mobs")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> passive = addSetting(new BoolSetting.Builder()
        .name("passive")
        .description("Render passive mobs")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> items = addSetting(new BoolSetting.Builder()
        .name("items")
        .description("Render items")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> itemFrames = addSetting(new BoolSetting.Builder()
        .name("item-frames")
        .description("Render item frames")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> other = addSetting(new BoolSetting.Builder()
        .name("other")
        .description("Render other entities")
        .defaultValue(false)
        .build()
    );

    @Handler
    private void onWorldRender(RenderWorldLastEvent event) {
        if (!GLUtils.hasClearedDepth) {
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            GLUtils.hasClearedDepth = true;
        }

        for (Object o : mc.theWorld.loadedEntityList) {
            if (!(o instanceof EntityLivingBase) && !(o instanceof EntityItem) && !(o instanceof EntityItemFrame)) continue;

            Entity entity = (Entity) o;
            Vec3 position = mc.renderViewEntity.getPosition(event.partialTicks);

            if (!CombatUtils.checkEntity(entity, players.get(), hostile.get(), passive.get(), true)) continue;
            if (!items.get() && entity instanceof EntityItem) continue;
            if (!itemFrames.get() && entity instanceof EntityItemFrame) continue;
            if (!other.get() && !(entity instanceof EntityPlayer || entity instanceof EntityMob || entity instanceof EntityAnimal ||
                    entity instanceof EntityAmbientCreature|| entity instanceof EntityItem || entity instanceof EntityItemFrame)) continue;

            if (!entity.isInRangeToRender3d(position.xCoord, position.yCoord, position.zCoord)) continue;
            if ((entity == mc.renderViewEntity && mc.gameSettings.thirdPersonView == 0 && !mc.renderViewEntity.isPlayerSleeping())) continue;

            double xPos = WorldUtils.getX(entity, event.partialTicks);
            double yPos = WorldUtils.getY(entity, event.partialTicks);
            double zPos = WorldUtils.getZ(entity, event.partialTicks);

            float f1 = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * event.partialTicks;

            RenderHelper.enableStandardItemLighting();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderManager.instance.func_147939_a(entity, xPos - renderPosX, yPos - renderPosY, zPos - renderPosZ, f1, event.partialTicks, false);
        }
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glDisable(GL11.GL_LIGHTING);

    }

}
