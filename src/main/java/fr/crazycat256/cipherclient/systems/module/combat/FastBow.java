/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.combat;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.systems.module.Category;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

public class FastBow extends Module {

    public FastBow() {
        super("fast-bow", "Shoots arrows very fast", Category.COMBAT);
    }

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        if (!mc.isSingleplayer()) {
            new Thread(() -> {
                if (mc.thePlayer.isUsingItem() && mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemBow && mc.thePlayer.onGround) {
                    try {
                        mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(-1, -1, -1, 255, mc.thePlayer.inventory.getCurrentItem(), -1.0f, -1.0f, -1.0f));
                        for (int i = 0; i < 25; ++i) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer.onGround));
                            Thread.sleep(1L);
                        }
                        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(5, 0, 0, 0, 255));
                    } catch (Exception e) {
                        // Empty catch block
                    }
                }
            }).start();
        } else if (mc.thePlayer.isUsingItem() && mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemBow && mc.thePlayer.onGround) {
            try {
                mc.thePlayer.setSprinting(true);
                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(-1, -1, -1, 255, mc.thePlayer.inventory.getCurrentItem(), 1.0f, 1.0f, 1.0f));
                for (int i = 0; i < 20; ++i) {
                    Thread.sleep(1L);
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer.onGround));
                }
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(5, 0, 0, 0, 255));
                mc.thePlayer.setSprinting(false);
            } catch (Exception e) {
                // Empty catch block
            }
        }
    }

}
