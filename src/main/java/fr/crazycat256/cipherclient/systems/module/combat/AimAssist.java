/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.combat;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.friend.Friends;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.gui.settings.DoubleSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Category;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

public class AimAssist extends Module {

    private EntityPlayer currentTarget;
    private final List<EntityPlayer> targetlist = new ArrayList<>();

    public AimAssist() {
        super("aim-assist", "Helps you aim at a player", Category.COMBAT);
    }

    private final Setting<Double> range = addSetting(new DoubleSetting.Builder()
        .name("range")
        .description("The range of the aim assist")
        .min(1.0)
        .max(10.0)
        .defaultValue(3.8)
        .build()
    );

    @Override
    public void onEnable() {
        this.currentTarget = null;
    }

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        Entity entity;
        this.targetlist.clear();
        for (Object e : mc.theWorld.playerEntities) {
            if (!this.isAttackable((Entity) e)) continue;
            if (Friends.get().isFriend(((Entity) e).getCommandSenderName())) continue;
            this.targetlist.add((EntityPlayer) e);
        }
        if (mc.objectMouseOver == null) {
            return;
        }
        if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && (entity = mc.objectMouseOver.entityHit) instanceof EntityPlayer) {
            this.currentTarget = (EntityPlayer) entity;
            return;
        }
        if (!this.targetlist.contains(this.currentTarget) && this.currentTarget != null) {
            this.currentTarget = null;
            return;
        }
        if (this.currentTarget == null) {
            return;
        }
        mc.thePlayer.rotationYaw = (float) (mc.thePlayer.rotationYaw - (mc.thePlayer.rotationYaw - this.getAngles(this.currentTarget)[0]) * 0.5);
        mc.thePlayer.rotationPitch = (float) (mc.thePlayer.rotationPitch - (mc.thePlayer.rotationPitch - this.getAngles(this.currentTarget)[1]) * 0.5);
    }

    private float[] getAngles(Entity entity) {
        float xDiff = (float) (entity.posX - mc.thePlayer.posX);
        float yDiff = (float) (entity.boundingBox.minY + entity.getEyeHeight() - mc.thePlayer.boundingBox.maxY);
        float zDiff = (float) (entity.posZ - mc.thePlayer.posZ);
        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0 / 3.141592653589793 - 90.0);
        float pitch = (float) (-Math.toDegrees(Math.atan(yDiff / Math.sqrt(zDiff * zDiff + xDiff * xDiff))));
        return new float[]{yaw, pitch};
    }

    private boolean isAttackable(Entity e) {
        if (e == null) {
            return false;
        }
        if (e instanceof EntityPlayer) {
            EntityPlayer p2 = (EntityPlayer) e;
            return !p2.isDead && !p2.isInvisible() && mc.thePlayer.getDistanceToEntity(p2) <= range.get() && mc.thePlayer.canEntityBeSeen(p2) && p2 != mc.thePlayer;
        }
        return false;
    }
}
