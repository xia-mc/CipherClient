/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.utils;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.world.World;

public class EntityFakePlayer extends EntityOtherPlayerMP {

    public EntityFakePlayer(World world, GameProfile gameProfile) {
        super(world, gameProfile);
        // Note: Neodymium break modules that use this as mc.renderViewEntity
    }

    @Override
    public void moveEntity(double x, double y, double z) {
        this.onGround = true;
        super.moveEntity(x, y, z);
        this.onGround = true;
    }

    @Override
    public boolean isSneaking() {
        return false;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.motionX = 0.0;
        this.motionY = 0.0;
        this.motionZ = 0.0;
        this.noClip = false;
    }
}
