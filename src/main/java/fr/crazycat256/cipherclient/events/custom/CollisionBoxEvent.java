/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.events.custom;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * Fired when a block is checked for collision boxes <br>
 * This replaces the {@link Block#getCollisionBoundingBoxFromPool(World, int , int , int)} method <br>
 * Note: This is only implemented for {@link net.minecraft.block.BlockLiquid} and {@link net.minecraftforge.fluids.BlockFluidBase}
 */
public class CollisionBoxEvent extends Event {

    public final Block block;
    public final int x;
    public final int y;
    public final int z;
    private AxisAlignedBB collisionBox;

    public CollisionBoxEvent(Block block, int x, int y, int z) {
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
