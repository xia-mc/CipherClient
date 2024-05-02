/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.player;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.gui.settings.BoolSetting;
import fr.crazycat256.cipherclient.gui.settings.IntSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.utils.InvUtils;
import fr.crazycat256.cipherclient.systems.module.Category;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.MouseEvent;

public class AutoTool extends Module {

    public AutoTool() {
        super("auto-tool", "Automatically switches to the best tool", Category.PLAYER);
    }

    private final Setting<Integer> minDurability = addSetting(new IntSetting.Builder()
        .name("min-durability")
        .description("The minimum durability of the tool to use")
        .min(0)
        .max(50)
        .defaultValue(5)
        .build()
    );

    private final Setting<Boolean> swapBack = addSetting(new BoolSetting.Builder()
        .name("swap-back")
        .description("Switches back to the previous item after breaking the block")
        .defaultValue(true)
        .build()
    );


    int lastSlot = -1;

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        if (!mc.gameSettings.keyBindAttack.getIsKeyPressed()) return;

        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            swap();
        } else if (lastSlot != -1 && swapBack.get()) {
            mc.thePlayer.inventory.currentItem = lastSlot;
            lastSlot = -1;
        }
    }



    @Handler
    private void onMouse(MouseEvent event) {
        if (event.button != 0) return;

        if (event.buttonstate) {
            if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                swap();
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }
        } else {
            if (lastSlot != -1 && swapBack.get()) {
                mc.thePlayer.inventory.currentItem = lastSlot;
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }
            lastSlot = -1;
        }
    }

    private void swap() {
        int[] bestTools = InvUtils.sortToolsFor(mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ);

        if (bestTools == null) return;

        for (int slot: bestTools) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(slot);
            if (stack != null && stack.getItem().getMaxDamage(stack) - stack.getItemDamage() < minDurability.get()) {
                continue;
            }

            if (lastSlot == -1 && swapBack.get()) {
                lastSlot = mc.thePlayer.inventory.currentItem;
            }
            mc.thePlayer.inventory.currentItem = slot;
            break;
        }
    }
}
