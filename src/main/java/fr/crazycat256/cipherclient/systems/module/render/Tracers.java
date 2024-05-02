/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.render;

import fr.crazycat256.cipherclient.events.Priority;
import fr.crazycat256.cipherclient.gui.settings.BoolSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.systems.module.Modules;
import fr.crazycat256.cipherclient.utils.EntityFakePlayer;
import fr.crazycat256.cipherclient.utils.MathUtils;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

public class Tracers extends Module {

    public Tracers() {
        super("tracers", "Traces a line between you and other entities", Category.RENDER);
    }

    private final Setting<Boolean> distanceColor = addSetting(new BoolSetting.Builder()
        .name("distance-color")
        .description("Color the tracers based on distance")
        .defaultValue(true)
        .build()
    );

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

    private final Setting<Boolean> other = addSetting(new BoolSetting.Builder()
        .name("other")
        .description("Render other entities")
        .defaultValue(false)
        .build()
    );


    @Handler(priority = Priority.LOW)
    private void onWorldRender(RenderWorldLastEvent event) {
        try {
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3042);
            GL11.glLineWidth(2.0f);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            for (Object o : mc.theWorld.loadedEntityList) {
                if (!(o instanceof EntityLivingBase)) continue;
                if (o == mc.renderViewEntity) continue;

                Entity entity = (Entity) o;

                if (entity.isDead) continue;
                if (entity.isInvisible()) continue;

                if (!players.get() && entity instanceof EntityPlayer) continue;
                if (!hostile.get() && entity instanceof EntityMob) continue;
                if (!passive.get() && (entity instanceof EntityAnimal || entity instanceof EntityAmbientCreature)) continue;
                if (!other.get() && !(entity instanceof EntityPlayer || entity instanceof EntityMob || entity instanceof EntityAnimal || entity instanceof EntityAmbientCreature)) continue;


                float distance = mc.renderViewEntity.getDistanceToEntity(entity);
                Vec3 entityPos = ((EntityLivingBase) entity).getPosition(event.partialTicks);
                double posX = entityPos.xCoord - RenderManager.renderPosX;
                double posY = entityPos.yCoord + (entity.height / 2.0f) - RenderManager.renderPosY;
                double posZ = entityPos.zCoord - RenderManager.renderPosZ;
                Vec3 look = MathUtils.resize(mc.renderViewEntity.getLook(event.partialTicks), 16);
                if (entity instanceof EntityFakePlayer || entity == mc.thePlayer) {
                    posY -= 1.62;
                }
                if (mc.renderViewEntity instanceof EntityFakePlayer) {
                    look.yCoord += 1.62;
                }
                if (Modules.get().get(Freelook.class).freeLooking()) {
                    if (entity == mc.thePlayer) {
                        continue;
                    }
                    distance = mc.thePlayer.getDistanceToEntity(entity);
                }
                distance = Math.max(0, distance - 10f);

                int r;
                int g;
                int b;
                if (distanceColor.get()) {
                    int maxDist = 110; // The distance at which the color will be green
                    int midDist = maxDist / 2;
                    if (distance < midDist) {
                        r = 255;
                        g = (int) (255 * (distance / midDist));
                    } else {
                        r = Math.max(0, (int) (255 * ((maxDist - distance) / midDist)));
                        g = 255;
                    }
                    b = 0;
                }
                else {
                    if (entity instanceof EntityPlayer) {
                        r = 255;
                        g = 0;
                        b = 0;
                    } else if (entity instanceof EntityMob) {
                        r = 255;
                        g = 255;
                        b = 0;
                    } else if (entity instanceof EntityAnimal) {
                        r = 0;
                        g = 255;
                        b = 0;
                    } else {
                        r = 0;
                        g = 0;
                        b = 255;
                    }
                }
                float a = Math.max(0.25f, (255f - (distance * 1.5f)) / 255f);

                GL11.glColor4f(r / 255.0f, g / 255.0f, b / 255.0f, a);
                GL11.glBegin(1);
                GL11.glVertex3d(look.xCoord, look.yCoord, look.zCoord);
                GL11.glVertex3d(posX, posY, posZ);
                GL11.glEnd();
            }
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
        } catch (Exception exception) {
            // empty catch block
        }
    }
}
