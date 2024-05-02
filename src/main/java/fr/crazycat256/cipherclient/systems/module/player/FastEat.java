/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.player;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.systems.module.Category;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import org.lwjgl.input.Mouse;

public class FastEat extends Module {

    public FastEat() {
        super("fast-eat", "Allows you to eat food very fast", Category.PLAYER);
    }

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        if (this.isActive() && Mouse.getEventButton() == 1 && Mouse.isButtonDown(1)) {
            if (mc.thePlayer.inventory.getCurrentItem() == null) {
                return;
            }
            Item item = mc.thePlayer.inventory.getCurrentItem().getItem();
            if (mc.thePlayer.onGround && (item instanceof ItemFood || item instanceof ItemPotion) && (mc.thePlayer.getFoodStats().needFood() || item instanceof ItemPotion || item instanceof ItemAppleGold)) {
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                for (int i = 0; i < 1000; ++i) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(false));
                }
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(5, 0, 0, 0, 255));
            }
        }
    }
}
