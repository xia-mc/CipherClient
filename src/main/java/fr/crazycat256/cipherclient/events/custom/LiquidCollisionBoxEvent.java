/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.events.custom;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * Fired when a liquid block is checked for collision boxes <br>
 * This replaces the {@link BlockLiquid#getCollisionBoundingBoxFromPool(World, int , int , int)} method
 */
public class LiquidCollisionBoxEvent extends Event {

    public final BlockLiquid block;
    public final int x;
    public final int y;
    public final int z;
    private AxisAlignedBB collisionBox;

    public LiquidCollisionBoxEvent(BlockLiquid block, int x, int y, int z) {
        this.block = block;
        this.x = x;
        this.y = y;
        this.z = z;
        this.collisionBox = null;
    }

    public AxisAlignedBB getCollisionBox() {
        return collisionBox;
    }

    public void setCollisionBox(AxisAlignedBB collisionBox) {
        this.collisionBox = collisionBox;
    }
}
