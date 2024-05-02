/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.world;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.systems.module.Category;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

public class AutoMlg extends Module {

    private int delay;

    public AutoMlg() {
        super("auto-mlg", "Automatically place water bucket on fall", Category.WORLD);
    }

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer.fallDistance >= 5.0f) {
            this.switchToItem(I18n.format("item.bucketWater.name"));
            Block blocks = mc.theWorld.getBlock((int) mc.thePlayer.posX, (int) mc.thePlayer.posY - 3, (int) mc.thePlayer.posZ);
            if (blocks.getMaterial() != Material.air && this.hasItem(I18n.format("item.bucketWater.name"))) {
                this.useItem();
                ++this.delay;
                if (this.delay >= 20) {
                    this.switchToItem(I18n.format("item.bucket.name"));
                    this.useItem();
                    this.delay = 0;
                }
            }
        }
    }

    private void useItem() {
        ItemStack item = mc.thePlayer.inventory.getCurrentItem();
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(90.0f, 90.0f, false));
        mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, item);
        mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement((int) mc.thePlayer.posX, mc.theWorld.getHeightValue((int) mc.thePlayer.posX, (int) mc.thePlayer.posZ), (int) mc.thePlayer.posZ, -1, item, 0.0f, 0.0f, 0.0f));
    }

    private boolean hasItem(String blockTileName) {
        for (int i = 36; i <= 44; ++i) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() || !(mc.thePlayer.inventoryContainer.getSlot(i).getStack().getDisplayName()).equalsIgnoreCase(blockTileName)) {
                continue;
            }
            return true;
        }
        return false;
    }

    private void switchToItem(String itemName) {
        for (int i = 36; i <= 44; ++i) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() || !(mc.thePlayer.inventoryContainer.getSlot(i).getStack().getDisplayName()).equalsIgnoreCase(itemName)) {
                continue;
            }
            mc.thePlayer.inventory.currentItem = i - 36;
            break;
        }
    }
}
