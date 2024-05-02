/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.world;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.gui.settings.*;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.systems.module.Modules;
import fr.crazycat256.cipherclient.systems.module.movement.Flight;
import fr.crazycat256.cipherclient.utils.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Comparator;

import static java.lang.Math.floor;

public class Scaffold extends Module {

    public Scaffold() {
        super("scaffold", "Automatically places blocks under you", Category.WORLD);
    }

    private final Setting<Mode> mode = addSetting(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("The mode of scaffold")
        .defaultValue(Mode.NORMAL)
        .build()
    );

    private final Setting<Boolean> allowSwap = addSetting(new BoolSetting.Builder()
        .name("allow-swap")
        .description("Allows you to place blocks you're not holding")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> fastTower = addSetting(new BoolSetting.Builder()
        .name("fast-tower")
        .description("Allows you to tower up faster")
        .defaultValue(false)
        .build()
    );

    private final Setting<Double> towerSpeed = addSetting(new DoubleSetting.Builder()
        .name("tower-speed")
        .description("The speed of tower")
        .defaultValue(0.5)
        .min(0)
        .max(1)
        .visible(fastTower::get)
        .build()
    );

    private final Setting<Boolean> towerWhileMoving = addSetting(new BoolSetting.Builder()
        .name("tower-while-moving")
        .description("Towers while moving")
        .defaultValue(false)
        .visible(() -> mode.get() == Mode.NORMAL)
        .build()
    );

    private final Setting<Boolean> onlyOnClick = addSetting(new BoolSetting.Builder()
        .name("only-on-click")
        .description("Only places blocks when you click")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> swing = addSetting(new BoolSetting.Builder()
        .name("swing")
        .description("Swings your arm when placing blocks")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> render = addSetting(new BoolSetting.Builder()
        .name("render")
        .description("Renders the block you're placing")
        .defaultValue(true)
        .build()
    );

    private final Setting<Double> placeRange = addSetting(new DoubleSetting.Builder()
        .name("place-range")
        .description("The range to place blocks")
        .defaultValue(5.0)
        .min(0)
        .max(6)
        .visible(() -> mode.get() == Mode.NORMAL)
        .build()
    );

    private final Setting<Double> aheadDistance = addSetting(new DoubleSetting.Builder()
        .name("ahead-distance")
        .description("How far ahead to place blocks")
        .defaultValue(0.0)
        .min(0)
        .max(6)
        .visible(() -> mode.get() == Mode.NORMAL)
        .build()
    );

    private final Setting<Double> airPlaceRadius = addSetting(new DoubleSetting.Builder()
        .name("air-place-radius")
        .description("How far horizontally you can place blocks in air")
        .defaultValue(0.0)
        .min(0)
        .max(6)
        .visible(() -> mode.get() == Mode.AIR_PLACE)
        .build()
    );


    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {

        int blockSlot = -1;
        int originalSlot = mc.thePlayer.inventory.currentItem;
        if (isPlacable(mc.thePlayer.inventory.getCurrentItem())) {
            blockSlot = mc.thePlayer.inventory.currentItem;
        } else {
            if (allowSwap.get()) {
                for (int i = 0; i < 9; i++) {
                    if (isPlacable(mc.thePlayer.inventory.getStackInSlot(i))) {
                        blockSlot = i;
                        break;
                    }
                }
                if (blockSlot == -1) {
                    return;
                }
            } else {
                return;
            }
        }

        if (onlyOnClick.get() && !mc.gameSettings.keyBindUseItem.getIsKeyPressed()) return;
        if (mc.gameSettings.keyBindSneak.getIsKeyPressed()) {
            ReflectUtils.setPressed(mc.gameSettings.keyBindSneak, false);
        }

        Vec3 inputMove = PlayerUtils.getMovementVec3();
        Vec3 playerPos = Vec3.createVectorHelper(mc.thePlayer.posX, mc.thePlayer.posY - 1.62 + mc.thePlayer.ySize, mc.thePlayer.posZ); // Feet position
        Vec3 velocity = Vec3.createVectorHelper(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ);
        Vec3 targetPos = playerPos.addVector(velocity.xCoord, velocity.yCoord - 0.75, velocity.zCoord);

        if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
            targetPos.yCoord -= 1;
        }
        if (mode.get() == Mode.NORMAL && aheadDistance.get() != 0) {
            targetPos = targetPos.addVector(inputMove.xCoord * aheadDistance.get(), 0, inputMove.zCoord * aheadDistance.get());
        }

        if (targetPos.yCoord >= floor(playerPos.yCoord)) {
            targetPos.yCoord = playerPos.yCoord - 1;
        }

        Vec3i blockPos = new Vec3i(targetPos);

        if (mode.get() == Mode.NORMAL && !mc.theWorld.getBlock(blockPos.x, blockPos.y, blockPos.z).getMaterial().isSolid()) {
            if (WorldUtils.getPlaceSide(new Vec3i(targetPos)) == null) {
                ArrayList<Vec3i> blocks = new ArrayList<>();
                for (double x = playerPos.xCoord - placeRange.get(); x < playerPos.xCoord + placeRange.get(); x++) {
                    for (double z = playerPos.zCoord - placeRange.get(); z < playerPos.zCoord + placeRange.get(); z++) {
                        for (double y = Math.max(0, playerPos.yCoord - placeRange.get()); y < Math.min(playerPos.yCoord + placeRange.get(), 256); y++) {
                            Vec3i bp = new Vec3i(x, y, z);
                            if (WorldUtils.getPlaceSide(bp) == null) continue;
                            if (!WorldUtils.isPlaceable(bp)) continue;
                            if (mc.thePlayer.getPosition(1F).distanceTo(MathUtils.centerOf(bp.offset(WorldUtils.getClosestPlaceSide(bp)))) > placeRange.get()) continue;
                            blocks.add(bp);
                        }
                    }
                }
                if (blocks.isEmpty()) {
                    return;
                }
                final Vec3 finalTargetPos = targetPos;
                blocks.sort(Comparator.comparingDouble(bp -> MathUtils.centerOf(bp).squareDistanceTo(finalTargetPos)));
                blockPos = blocks.get(0);
            }

            if (blockSlot != originalSlot) {
                swapTo(blockSlot);
            }
            place(blockPos);
            if (blockSlot != originalSlot) {
                swapTo(originalSlot);
            }
        }

        else if (mode.get() == Mode.AIR_PLACE) {
            ArrayList<Vec3i> blocks = new ArrayList<>();
            for (double x = targetPos.xCoord - airPlaceRadius.get(); x <= targetPos.xCoord + airPlaceRadius.get(); x++) {
                for (double z = targetPos.zCoord - airPlaceRadius.get(); z <= targetPos.zCoord + airPlaceRadius.get(); z++) {
                    blockPos = new Vec3i(x, targetPos.yCoord, z);
                    if (!mc.theWorld.getBlock(blockPos.x, blockPos.y, blockPos.z).getMaterial().isReplaceable()) continue;
                    if (playerPos.distanceTo(MathUtils.centerOf(blockPos)) <= airPlaceRadius.get() || (floor(x) == blockPos.x && floor(z) == blockPos.z)) {
                        blocks.add(blockPos);
                    }
                }
            }
            if (blocks.isEmpty()) {
                return;
            }
            blocks.sort(Comparator.comparingDouble(bp -> MathUtils.centerOf(bp).distanceTo(mc.thePlayer.getPosition(1F))));
            if (blockSlot != originalSlot) {
                swapTo(blockSlot);
            }
            for (Vec3i bp : blocks) {
                if (place(bp)) {
                    break;
                }
            }
            if (blockSlot != originalSlot) {
                swapTo(originalSlot);
            }
        }

        if (mc.gameSettings.keyBindJump.getIsKeyPressed()
                && (fastTower.get() && (towerWhileMoving.get() || (inputMove.xCoord == 0 && inputMove.zCoord == 0)))
                && !Modules.get().get(Flight.class).isActive()) {
            mc.thePlayer.motionY = towerSpeed.get();
        }
    }

    private boolean place(Vec3i bp) {
        if (WorldUtils.placeBlock(bp.x, bp.y, bp.z, true)) {
            if (swing.get()) {
                mc.thePlayer.swingItem();
            }
            if (render.get()) {
                RenderUtils.renderTickingBlock(bp, 15, GLUtils.getColor(128, 104, 254, 255), GLUtils.getColor(64, 104, 254, 255));
            }
            return true;
        } else {
            return false;
        }
    }

    private void swapTo(int slot) {
        mc.thePlayer.inventory.currentItem = slot;
        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
    }

    private boolean isPlacable(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemBlock) {
            Block block = ((ItemBlock) stack.getItem()).field_150939_a;
            return block.getMaterial().isSolid()
                    && block.renderAsNormalBlock()
                    && !block.getMaterial().isReplaceable()
                    && !block.hasTileEntity(stack.getItemDamage())
                    && !(block instanceof BlockFalling);
        } else {
            return false;
        }
    }


    public enum Mode {
        NORMAL,
        AIR_PLACE,
    }
}
