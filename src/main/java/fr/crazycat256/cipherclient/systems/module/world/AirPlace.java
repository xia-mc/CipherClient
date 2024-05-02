/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.world;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.events.Priority;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.systems.module.Modules;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.gui.settings.DoubleSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.utils.GLUtils;
import fr.crazycat256.cipherclient.utils.ReflectUtils;
import fr.crazycat256.cipherclient.utils.RenderUtils;
import fr.crazycat256.cipherclient.systems.module.Category;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class AirPlace extends Module {

    public AirPlace() {
        super("air-place", "Allows you to place blocks in the air", Category.WORLD);
    }

    private final Setting<Double> range = addSetting(new DoubleSetting.Builder()
        .name("range")
        .description("The range of the block placement")
        .min(1.0)
        .max(10.0)
        .defaultValue(4.5)
        .build()
    );

    private int blockX = 0;
    private int blockY = 0;
    private int blockZ = 0;

    @Handler(priority = Priority.LOW) // Must be called before FastPlace
    private void onTick(TickEvent.ClientTickEvent event) {

        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.MISS) {

            if (getTimer() != 0) {
                return;
            }

            if (mc.gameSettings.keyBindUseItem.getIsKeyPressed()) {
                mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, null, blockX, blockY, blockZ, 1, mc.thePlayer.getLookVec());
                mc.thePlayer.swingItem();

                ItemStack item = mc.thePlayer.getCurrentEquippedItem();
                if (item == null) return;
                Block block = Block.getBlockFromItem(item.getItem());
                if (block != null) {
                    mc.theWorld.setBlock(blockX, blockY, blockZ, block);
                }
                setTimer(5);
            }
        }
    }

    @Handler
    private void onWorldRender(RenderWorldLastEvent event) {
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.MISS) {
            Vec3 pos = mc.thePlayer.getPosition(1.0F);
            Vec3 look = mc.thePlayer.getLook(1.0F);
            Vec3 blockPos = pos.addVector(look.xCoord * range.get(), look.yCoord * range.get(), look.zCoord * range.get());
            blockX = (int) Math.floor(blockPos.xCoord);
            blockY = (int) Math.floor(blockPos.yCoord);
            blockZ = (int) Math.floor(blockPos.zCoord);
            RenderUtils.renderBlock(blockX, blockY, blockZ, GLUtils.getColor(255, 0, 0));
        }
    }

    @Handler
    private void onMouse(MouseEvent event) {
        if (Modules.get().get(FastPlace.class).isActive()) return;
        if (event.button == 1 && event.buttonstate && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.MISS) {
            mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, null, blockX, blockY, blockZ, 1, mc.thePlayer.getLookVec());
            mc.thePlayer.swingItem();
            setTimer(5);
        }
    }

    private int getTimer() {
        return ReflectUtils.get(Minecraft.class, mc, "rightClickDelayTimer");
    }

    private void setTimer(int value) {
        ReflectUtils.set(Minecraft.class, mc, "rightClickDelayTimer", value);
    }
}
