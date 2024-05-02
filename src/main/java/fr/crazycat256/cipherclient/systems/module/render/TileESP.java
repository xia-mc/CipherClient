/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.render;

import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.gui.settings.BoolSetting;
import fr.crazycat256.cipherclient.gui.settings.DoubleSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.utils.GLUtils;
import fr.crazycat256.cipherclient.utils.RenderUtils;
import fr.crazycat256.cipherclient.utils.WorldUtils;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.systems.module.Category;
import net.minecraft.tileentity.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import java.util.ArrayList;

public class TileESP extends Module {
    public TileESP() {
        super("tile-esp", "Renders a box around tile entities", Category.RENDER);
    }

    private final Setting<Double> maxRange = addSetting(new DoubleSetting.Builder()
        .name("max-range")
        .description("The maximum range to render tile entities")
        .min(0)
        .max(1024.0)
        .defaultValue(256.0)
        .build()
    );

    private final Setting<Boolean> chests = addSetting(new BoolSetting.Builder()
        .name("chests")
        .description("Render vanilla chests")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> enderChests = addSetting(new BoolSetting.Builder()
        .name("ender-chests")
        .description("Render ender chests")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> ironChests = addSetting(new BoolSetting.Builder()
        .name("iron-chests")
        .description("Render iron chests")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> palaChests = addSetting(new BoolSetting.Builder()
            .name("pala-chests")
        .description("Render paladium chests")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> palaStorage = addSetting(new BoolSetting.Builder()
        .name("pala-storage")
        .description("Render paladium storage blocks")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> secretChests = addSetting(new BoolSetting.Builder()
        .name("secret-chests")
        .description("Render secret chests")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> drawers = addSetting(new BoolSetting.Builder()
        .name("drawers")
        .description("Render drawers")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> crates = addSetting(new BoolSetting.Builder()
        .name("crates")
        .description("Render crates")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> otherStorage = addSetting(new BoolSetting.Builder()
        .name("other-storage")
        .description("Render other storage blocks")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> spawners = addSetting(new BoolSetting.Builder()
        .name("spawners")
        .description("Render spawners")
        .defaultValue(true)
        .build()
    );




    @Handler
    public void onWorldRender(RenderWorldLastEvent event) {

        ArrayList<Vec3> alereadyRenderedChests = new ArrayList<>();

        for (TileEntity tile: WorldUtils.getLoadedTileEntityList()) {

            Vec3 tilePos = Vec3.createVectorHelper(tile.xCoord, tile.yCoord, tile.zCoord);
            if (mc.thePlayer.getPosition(1F).distanceTo(tilePos) > maxRange.get()) continue;

            AxisAlignedBB entityBox = tile.getRenderBoundingBox();
            Vec3 boxCenter = Vec3.createVectorHelper(
                entityBox.minX + (entityBox.maxX - entityBox.minX) / 2,
                entityBox.minY + (entityBox.maxY - entityBox.minY) / 2,
                entityBox.minZ + (entityBox.maxZ - entityBox.minZ) / 2
            );
            int lineAlpha = Math.min(255, ((int) mc.renderViewEntity.getPosition(1F).distanceTo(boxCenter) + 32) * 2);
            int sideAlpha = Math.min(128, (int) mc.renderViewEntity.getPosition(1F).distanceTo(boxCenter) + 26);

            int lineColor;
            int sideColor;

            switch (WorldUtils.getTileType(tile)) {
                case VANILLA_CHEST:
                    if (!chests.get()) continue;
                    if (contains(alereadyRenderedChests, Vec3.createVectorHelper(tile.xCoord, tile.yCoord, tile.zCoord))) continue;
                    TileEntityChest chest = (TileEntityChest) tile;
                    AxisAlignedBB chestBox = AxisAlignedBB.getBoundingBox(chest.xCoord + 0.0625, chest.yCoord, chest.zCoord + 0.0625, chest.xCoord + 0.9375, chest.yCoord + 0.875, chest.zCoord + 0.9375);
                    if (chest.adjacentChestXPos != null) {
                        chestBox = chestBox.addCoord(1, 0, 0);
                        alereadyRenderedChests.add(Vec3.createVectorHelper(chest.xCoord + 1, chest.yCoord, chest.zCoord));
                    } else if (chest.adjacentChestZPos != null) {
                        chestBox = chestBox.addCoord(0, 0, 1);
                        alereadyRenderedChests.add(Vec3.createVectorHelper(chest.xCoord, chest.yCoord, chest.zCoord + 1));
                    } else if (chest.adjacentChestXNeg != null) {
                        chestBox = chestBox.addCoord(-1, 0, 0);
                        alereadyRenderedChests.add(Vec3.createVectorHelper(chest.xCoord - 1, chest.yCoord, chest.zCoord));
                    } else if (chest.adjacentChestZNeg != null) {
                        chestBox = chestBox.addCoord(0, 0, -1);
                        alereadyRenderedChests.add(Vec3.createVectorHelper(chest.xCoord, chest.yCoord, chest.zCoord - 1));
                    }
                    RenderUtils.renderBox(chestBox, GLUtils.getColor(lineAlpha, 255, 255, 0), GLUtils.getColor(sideAlpha, 255, 255, 0));
                    continue;

                case ENDER_CHEST:
                    if (!enderChests.get()) continue;
                    lineColor = GLUtils.getColor(lineAlpha, 0, 0, 255);
                    sideColor = GLUtils.getColor(sideAlpha, 0, 0, 255);
                    break;

                case IRON_CHEST:
                    if (!ironChests.get()) continue;
                    lineColor = GLUtils.getColor(lineAlpha, 0, 255, 255);
                    sideColor = GLUtils.getColor(sideAlpha, 0, 255, 255);
                    break;

                case PALA_CHEST:
                    if (!palaChests.get()) continue;
                    lineColor = GLUtils.getColor(lineAlpha, 255, 128, 0);
                    sideColor = GLUtils.getColor(sideAlpha, 255, 128, 0);
                    break;

                case PALA_STORAGE:
                    if (!palaStorage.get()) continue;
                    lineColor = GLUtils.getColor(lineAlpha, 128, 64, 0);
                    sideColor = GLUtils.getColor(sideAlpha, 128, 64, 0);
                    break;

                case SECRET_CHEST:
                    if (!secretChests.get()) continue;
                    lineColor = GLUtils.getColor(lineAlpha, 255, 0, 0);
                    sideColor = GLUtils.getColor(sideAlpha, 255, 0, 0);
                    break;

                case DRAWER:
                    if (!drawers.get()) continue;
                    lineColor = GLUtils.getColor(lineAlpha, 0, 255, 0);
                    sideColor = GLUtils.getColor(sideAlpha, 0, 255, 0);
                    break;

                case CRATE:
                    if (!crates.get()) continue;
                    lineColor = GLUtils.getColor(lineAlpha, 128, 255, 0);
                    sideColor = GLUtils.getColor(sideAlpha, 128, 255, 0);
                    break;

                case OTHER_STORAGE:
                    if (!otherStorage.get()) continue;
                    lineColor = GLUtils.getColor(lineAlpha, 128, 128, 128);
                    sideColor = GLUtils.getColor(sideAlpha, 128, 128, 128);
                    break;

                case SPAWNER:
                    if (!spawners.get()) continue;
                    lineColor = GLUtils.getColor(lineAlpha, 192, 0, 255);
                    sideColor = GLUtils.getColor(sideAlpha, 192, 0, 255);
                    break;

                default:
                    continue;
            }

            RenderUtils.renderBox(tile.getRenderBoundingBox(), lineColor, sideColor);

        }
    }
    private boolean contains(ArrayList<Vec3> list, Vec3 vec) {
        for (Vec3 v: list) {
            if (v.xCoord == vec.xCoord && v.yCoord == vec.yCoord && v.zCoord == vec.zCoord) return true;
        }
        return false;
    }
}
