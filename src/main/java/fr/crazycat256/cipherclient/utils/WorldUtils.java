/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static fr.crazycat256.cipherclient.CipherClient.mc;

public class WorldUtils {

    /**
     * Get the X coordinate of an entity interpolated between ticks
     */
    public static double getX(Entity entity, float partialTicks) {
        return entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
    }

    /**
     * Get the Y coordinate of an entity interpolated between ticks
     */
    public static double getY(Entity entity, float partialTicks) {
        return entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
    }

    /**
     * Get the Z coordinate of an entity interpolated between ticks
     */
    public static double getZ(Entity entity, float partialTicks) {
        return entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
    }

    /**
     * Get a list of all loaded tile entities <br>
     * For some reason, some tile entities are not in {@code mc.theWorld.loadedTileEntityList} (e.g. drawers, iron chests, etc.)
     * @return A list of tile entities
     */
    public static List<TileEntity> getLoadedTileEntityList() {

        IChunkProvider chunkProvider = mc.theWorld.getChunkProvider();
        List<Chunk> chunks = ReflectUtils.get(ChunkProviderClient.class, (ChunkProviderClient) chunkProvider, "chunkListing");

        List<TileEntity> tileEntities = new ArrayList<>();
        for (Chunk chunk : chunks) {
            if (chunk != null) {

                chunkTilesLoop:
                for (Object o : chunk.chunkTileEntityMap.values()) {
                    if (o instanceof TileEntity) {

                        TileEntity tileEntity = (TileEntity) o;

                        for (TileEntity tile : tileEntities) {
                            if (tile.xCoord == tileEntity.xCoord && tile.yCoord == tileEntity.yCoord && tile.zCoord == tileEntity.zCoord) {
                                continue chunkTilesLoop;
                            }
                        }

                        tileEntities.add((TileEntity) o);
                    }
                }
            }
        }
        return tileEntities;
    }


    /**
     * Get the type of tile entity, useful for classifying storage blocks
     * @param tileEntity The tile entity to get the type of
     * @return The type of the tile entity
     */
    public static TileType getTileType(TileEntity tileEntity) {
        if (tileEntity == null) {
            return TileType.UNKNOWN;
        }

        String tileName = Block.blockRegistry.getNameForObject(tileEntity.getBlockType());

        if (tileEntity instanceof TileEntityChest) {
            return TileType.VANILLA_CHEST;
        }

        if (tileEntity instanceof TileEntityEnderChest || tileName.contains("paladium_ender_chest")) {
            return TileType.ENDER_CHEST;
        }

        try {
            if (Class.forName("cpw.mods.ironchest.TileEntityIronChest").isInstance(tileEntity)) {
                return TileType.IRON_CHEST;
            }
        } catch (Exception ignored) {}

        if (tileName.startsWith("palamod:") && tileName.toLowerCase().contains("chest") && tileEntity.getBlockType() instanceof BlockContainer) {
            for (String palaChest : palaChests) {
                if (tileName.contains(palaChest)) {
                    return TileType.PALA_CHEST;
                }
            }
        }

        if (tileName.startsWith("palamod:") && tileEntity.getBlockType() instanceof BlockContainer) {
            return TileType.PALA_STORAGE;
        }

        try {
            if (Class.forName("com.github.abrarsyed.secretroomsmod.blocks.BlockCamoChest").isInstance(tileEntity.getBlockType())) {
                return TileType.SECRET_CHEST;
            }
        } catch (Exception ignored) {}

        if (tileEntity instanceof TileEntityMobSpawner || (tileName.startsWith("palamod:") && tileName.contains("spawner"))) {
            return TileType.SPAWNER;
        }

        try {
            if (Class.forName("com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard").isInstance(tileEntity)) {
                return TileType.DRAWER;
            }
        } catch (Exception ignored) {}

        try {
            if (Class.forName("noppes.npcs.blocks.tiles.TileNpcContainer").isInstance(tileEntity)) {
                return TileType.CRATE;
            }
        } catch (Exception ignored) {}

        if (tileEntity.getBlockType() instanceof BlockContainer) {
            return TileType.OTHER_STORAGE;
        }

        return TileType.UNKNOWN;

    }

    public enum TileType {
        VANILLA_CHEST,
        ENDER_CHEST,
        IRON_CHEST,
        PALA_CHEST,
        PALA_STORAGE,
        SECRET_CHEST,
        DRAWER,
        CRATE,
        OTHER_STORAGE,
        SPAWNER,
        UNKNOWN
    }

    private static final String[] palaChests = {
        "amethyst",
        "titane",
        "pala",
        "endium",
        "safe"
    };

    /**
     * Check if the player can click on a block to interact normally (without opening a GUI)
     */
    private static boolean isClickable(Vec3i blockPos) {
        Block block = mc.theWorld.getBlock(blockPos.x, blockPos.y, blockPos.z);
        return block.getMaterial().isSolid() && !block.hasTileEntity(mc.theWorld.getBlockMetadata(blockPos.x, blockPos.y, blockPos.z));
    }

    /**
     * Check if a block can be placed at the specified position
     */
    public static boolean isPlaceable(Vec3i pos) {
        return isPlaceable(pos.x, pos.y, pos.z);
    }

    /**
     * Check if a block can be placed at the specified coordinates
     */
    public static boolean isPlaceable(int x, int y, int z) {
        if (!mc.theWorld.canPlaceEntityOnSide(Blocks.stone, x, y, z, false, 0, mc.thePlayer, mc.thePlayer.getHeldItem())) {
            return false;
        }
        AxisAlignedBB blockBox = AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
        AxisAlignedBB playerBox = mc.thePlayer.boundingBox;
        return playerBox == null || !blockBox.intersectsWith(playerBox);
    }

    /**
     * Try to place a block at the specified position
     * @return {@code true} if the block was placed successfully
     */
    public static boolean placeBlock(int x, int y, int z, boolean checkEntities) {
        ItemStack stack = mc.thePlayer.getCurrentEquippedItem();
        Block block = mc.theWorld.getBlock(x, y, z);
        if (!block.isReplaceable(mc.theWorld, x, y, z)) {
            return false;
        }
        if (checkEntities && !mc.theWorld.canPlaceEntityOnSide(block, x, y, z, false, 0, mc.thePlayer, stack)) {
            return false;
        }
        Direction direction = null;

        List<Direction> dirs = Arrays.stream(Direction.values()).sorted((d1, d2) -> {
            double dist1 = MathUtils.centerOf(x, y, z).addVector(d1.offsetX, d1.offsetY, d1.offsetZ).squareDistanceTo(mc.thePlayer.getPosition(1F));
            double dist2 = MathUtils.centerOf(x, y, z).addVector(d2.offsetX, d2.offsetY, d2.offsetZ).squareDistanceTo(mc.thePlayer.getPosition(1F));
            return Double.compare(dist1, dist2);
        }).collect(Collectors.toList());

        for (Direction dir : dirs) {
            Vec3i offPos = new Vec3i(x, y, z).add(dir.offsetVec);
            if (MathUtils.centerOf(offPos).squareDistanceTo(mc.thePlayer.getPosition(1F)) > 36) {
                break;
            }
            if (isClickable(offPos)) {
                direction = dir;
                break;
            }
        }
        if (direction == null) {
            if (MathUtils.centerOf(x, y, z).squareDistanceTo(mc.thePlayer.getPosition(1F)) > 36) {
                return false;
            }
            return mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, stack, x, y, z, 1, mc.thePlayer.getLookVec());
        }
        Vec3i pos = new Vec3i(x, y, z).add(direction.offsetVec);
        return mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, stack, pos.x, pos.y, pos.z, direction.oppositeSide, mc.thePlayer.getLookVec());
    }

    /**
     * Get a side where a block can be placed at the specified position
     * @return The direction of the side where a block can be placed, or {@code null} if no side is available
     */
    public static Direction getPlaceSide(Vec3i pos) {
        return getPlaceSide(pos.x, pos.y, pos.z);
    }

    /**
     * Get a side where a block can be placed at the specified coordinates
     * @return The direction of the side where a block can be placed, or {@code null} if no side is available
     */
    public static Direction getPlaceSide(int x, int y, int z) {
        for (Direction dir : Direction.values()) {
            Vec3i offPos = new Vec3i(x, y, z).add(dir.offsetVec);
            if (isClickable(offPos)) {
                return dir;
            }
        }
        return null;
    }

    /**
     * Get the closest side where a block can be placed at the specified position
     * @return The direction of the closest side where a block can be placed
     */
    public static Direction getClosestPlaceSide(Vec3i pos) {
        return getClosestPlaceSide(pos.x, pos.y, pos.z);
    }

    /**
     * Get the closest side where a block can be placed at the specified coordinates
     * @return The direction of the closest side where a block can be placed
     */
    public static Direction getClosestPlaceSide(int x, int y, int z) {
        Direction closest = null;
        double closestDist = Double.MAX_VALUE;
        for (Direction dir : Direction.values()) {
            Vec3i offPos = new Vec3i(x, y, z).add(dir.offsetVec);
            if (isClickable(offPos)) {
                double dist = MathUtils.centerOf(offPos).distanceTo(mc.thePlayer.getPosition(1F));
                if (dist < closestDist) {
                    closest = dir;
                    closestDist = dist;
                }
            }
        }
        return closest;
    }
}
