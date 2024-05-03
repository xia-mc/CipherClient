/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.utils;

import fr.crazycat256.cipherclient.systems.friend.Friends;
import fr.crazycat256.cipherclient.systems.module.Modules;
import fr.crazycat256.cipherclient.systems.module.combat.Criticals;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.Vec3;

import static fr.crazycat256.cipherclient.CipherClient.mc;

public class CombatUtils {


    public static void performCrit() {
        performCrit(mc.thePlayer.getPosition(1));
    }

    public static void performCrit(Vec3 playerPos) {
        PlayerUtils.sendPositionPacket(playerPos.xCoord, playerPos.yCoord + 0.0625D, playerPos.zCoord, true);
        PlayerUtils.sendPositionPacket(playerPos.xCoord, playerPos.yCoord, playerPos.zCoord, false);
    }

    public static void teleportHit(EntityLivingBase entity) {
        teleportHit(entity, false);
    }
    public static void teleportHit(EntityLivingBase entity, boolean noSwing) {
        Vec3 playerPos = mc.thePlayer.getPosition(1F);
        Vec3 dir = mc.thePlayer.getPosition(1).subtract(entity.getPosition(1)).normalize();
        double distance = Math.min(mc.thePlayer.getDistanceToEntity(entity) - 3, 10);
        distance = Math.min(distance, 10);
        dir = Vec3.createVectorHelper(dir.xCoord * distance, dir.yCoord * distance, dir.zCoord * distance);
        Vec3 pos = playerPos.addVector(dir.xCoord, dir.yCoord, dir.zCoord);
        pos.yCoord = Math.floor(pos.yCoord + 1.8);

        double stance = mc.thePlayer.posY - mc.thePlayer.boundingBox.minY;
        if (!PlayerUtils.isTPValid(PlayerUtils.getFootPos(), pos.addVector(0, -stance, 0))) {
            return;
        }

        PlayerUtils.sendPositionPacket(pos, true);
        mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, 2));
        if ((Modules.get().get(Criticals.class)).isActive() && !mc.thePlayer.isInWater()) {
            PlayerUtils.sendPositionPacket(pos.addVector(0, 0.0625, 0), true);
            PlayerUtils.sendPositionPacket(pos, false);
        }
        CombatUtils.attack(entity, noSwing, true, 0);
        PlayerUtils.sendPositionPacket(playerPos, true);
    }

    public static boolean checkEntity(Entity entity, boolean players, boolean hostile, boolean passive, boolean other) {
        if (entity instanceof EntityFakePlayer) return false;
        if (!players && entity instanceof EntityPlayer) return false;
        if (!hostile && entity instanceof EntityMob) return false;
        if (!passive && (entity instanceof EntityAnimal || entity instanceof EntityAmbientCreature)) return false;
        if (!other && !(entity instanceof EntityPlayer || entity instanceof EntityMob || entity instanceof EntityAnimal || entity instanceof EntityAmbientCreature)) return false;
        return true;
    }

    public static boolean canHit(Entity entity, boolean players, boolean hostile, boolean passive, boolean other) {
        if (Friends.get().isFriend(entity.getCommandSenderName())) return false;
        if (entity == mc.thePlayer) return false;
        if (entity instanceof EntityFakePlayer) return false;
        return checkEntity(entity, players, hostile, passive, other);
    }


    public static boolean isValid(EntityLivingBase ent) {
        if(ent == null)
            return false;

        if(ent == mc.thePlayer || ent instanceof EntityFakePlayer || ent.isDead)
            return false;

        return ent.isEntityAlive();
    }

    public static void attack(EntityLivingBase entity, boolean noSwing, boolean keepSprint, int crackSize) {
        if(noSwing) {
            mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
        }
        else {
            mc.thePlayer.swingItem();
        }

        if(keepSprint) {
            mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
        }        else {
            mc.thePlayer.setSprinting(false);
            mc.playerController.attackEntity(mc.thePlayer, entity);
        }

        final float sharpLevel = EnchantmentHelper.func_152377_a(mc.thePlayer.getHeldItem(), entity.getCreatureAttribute());
        if (sharpLevel > 0.0f && crackSize == 0) {
            mc.thePlayer.onEnchantmentCritical(entity);
        }
        for(int i = 0; i < crackSize; i++) {
            mc.thePlayer.onCriticalHit(entity);
            mc.thePlayer.onEnchantmentCritical(entity);
        }
    }

    public static boolean isInReach(EntityLivingBase entity, double reach) {
        return mc.thePlayer.getDistanceToEntity(entity) <= reach;
    }
}
