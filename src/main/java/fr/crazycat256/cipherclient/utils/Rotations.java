/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.utils;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.events.custom.PlayerUpdateEvent;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderLivingEvent;

import static fr.crazycat256.cipherclient.CipherClient.mc;

public class Rotations {

    private static Object target;
    private static int remainingTicks;

    private static float playerYaw, playerPitch;
    private static float lastYaw, lastPrevYaw;
    private static float lastPitch, lastPrevPitch;

    private static boolean rollback = false;


    public static void rotate(float yaw, float pitch, int ticks) {
        target = new float[]{yaw, pitch};
        remainingTicks = ticks;
    }

    public static void rotate(Vec3 targetPos, int ticks) {
        Vec3 playerPos = Vec3.createVectorHelper(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        Vec3 diff = targetPos.addVector(-playerPos.xCoord, -playerPos.yCoord, -playerPos.zCoord);
        double yaw = Math.toDegrees(Math.atan2(diff.zCoord, diff.xCoord)) - 90;
        double pitch = -Math.toDegrees(Math.atan2(diff.yCoord, Math.sqrt(diff.xCoord * diff.xCoord + diff.zCoord * diff.zCoord)));
        rotate((float) yaw, (float) pitch, ticks);
    }

    public static void lookAt(Vec3 targetPos, int ticks) {
        target = targetPos;
        remainingTicks = ticks;
    }

    public static void lookAt(Entity targetEntity, int ticks) {
        target = targetEntity;
        remainingTicks = ticks;
    }

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        if (remainingTicks > 0) {
            remainingTicks--;
        } else if (target != null) {
            target = null;
        }
    }

    @Handler
    private void onPreRenderLiving(RenderLivingEvent.Pre event) {
        if (event.entity == mc.thePlayer && target != null) {

            float[] rotations = getRotations();
            float rotationYaw = rotations[0];
            float rotationPitch = rotations[1];

            lastYaw = mc.thePlayer.rotationYaw;
            lastPrevYaw = mc.thePlayer.prevRotationYaw;
            mc.thePlayer.rotationYaw = rotationYaw;
            mc.thePlayer.prevRotationYaw = rotationYaw;
            mc.thePlayer.renderYawOffset = rotationYaw;
            mc.thePlayer.rotationYawHead = rotationYaw;
            mc.thePlayer.prevRotationYawHead = rotationYaw;
            mc.thePlayer.prevDistanceWalkedModified = 0;
            mc.thePlayer.distanceWalkedModified = 0;

            lastPitch = mc.thePlayer.rotationPitch;
            lastPrevPitch = mc.thePlayer.prevRotationPitch;
            mc.thePlayer.rotationPitch = rotationPitch;
            mc.thePlayer.prevRotationPitch = mc.thePlayer.rotationPitch;

            rollback = true;
        }
    }

    @Handler
    private void onPostRenderLiving(RenderLivingEvent.Post event) {
        if (event.entity == mc.thePlayer && rollback) {
            mc.thePlayer.rotationYaw = lastYaw;
            mc.thePlayer.prevRotationYaw = lastPrevYaw;

            mc.thePlayer.rotationPitch = lastPitch;
            mc.thePlayer.prevRotationPitch = lastPrevPitch;
            rollback = false;
        }
    }

    @Handler
    private void onPrePlayerUpdate(PlayerUpdateEvent.Pre event) {
        if (target != null) {
            float[] rotations = getRotations();
            playerYaw = event.player.rotationYaw;
            playerPitch = event.player.rotationPitch;
            event.player.rotationYaw = rotations[0];
            event.player.rotationPitch = rotations[1];
        }
    }

    @Handler
    private void onPostPlayerUpdate(PlayerUpdateEvent.Post event) {
        if (target != null) {
            mc.thePlayer.rotationYaw = playerYaw;
            mc.thePlayer.rotationPitch = playerPitch;
        }
    }

    private static float[] getRotations() {
        float rotationYaw, rotationPitch;
        if (target instanceof float[]) {
            float[] rotation = (float[]) target;
            rotationYaw = rotation[0];
            rotationPitch = rotation[1];
        } else {
            Vec3 targetPos;
            if (target instanceof Entity) {
                AxisAlignedBB bb = ((Entity) target).boundingBox;
                targetPos = Vec3.createVectorHelper(bb.minX + (bb.maxX - bb.minX) / 2, bb.minY + (bb.maxY - bb.minY) / 2, bb.minZ + (bb.maxZ - bb.minZ) / 2);
            } else if (target instanceof Vec3) {
                targetPos = (Vec3) target;
            } else {
                throw new IllegalStateException("Invalid target type");
            }
            Vec3 diff = targetPos.addVector(-mc.thePlayer.posX, -mc.thePlayer.posY, -mc.thePlayer.posZ);
            double dist = MathHelper.sqrt_double(diff.xCoord * diff.xCoord + diff.zCoord * diff.zCoord);
            rotationYaw = (float) (Math.atan2(diff.zCoord, diff.xCoord) * 180.0 / Math.PI) - 90.0f;
            rotationPitch = (float) -(Math.atan2(diff.yCoord, dist) * 180.0 / Math.PI);
        }
        return new float[]{rotationYaw, rotationPitch};
    }
}


