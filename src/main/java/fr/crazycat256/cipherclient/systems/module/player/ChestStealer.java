/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.player;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.systems.module.Category;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.Container;

public class ChestStealer extends Module {

    private int delay = 0;

    public ChestStealer() {
        super("chest-stealer", "Steals all stuff from vanilla chest", Category.PLAYER);
    }

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        if (!mc.inGameHasFocus && mc.currentScreen instanceof GuiChest) {
            if (!this.isContainerEmpty(mc.thePlayer.openContainer)) {
                int slotId = this.getNextSlotInContainer(mc.thePlayer.openContainer);
                if (this.delay >= 5) {
                    mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, slotId, 0, 1, mc.thePlayer);
                    this.delay = 0;
                }
                ++this.delay;
            } else {
                mc.thePlayer.closeScreen();
            }
        }
    }

    private int getNextSlotInContainer(Container container) {
        int slotAmount;
        int n = slotAmount = container.inventorySlots.size() == 90 ? 54 : 27;
        for (int i = 0; i < slotAmount; ++i) {
            if (container.getInventory().get(i) == null) {
                continue;
            }
            return i;
        }
        return -1;
    }

    private boolean isContainerEmpty(Container container) {
        int slotAmount;
        int n = slotAmount = container.inventorySlots.size() == 90 ? 54 : 27;
        for (int i = 0; i < slotAmount; ++i) {
            if (!container.getSlot(i).getHasStack()) {
                continue;
            }
            return false;
        }
        return true;
    }
}
