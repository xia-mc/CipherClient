/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.player;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.systems.module.Category;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;

/**
 * Copied from <a href="https://github.com/radioegor146/ehacks-pro/blob/master/src/main/java/ehacks/mod/modulesystem/classes/vanilla/AntiPotion.java">ehacks-pro</a>
 * @author radioegor146
 */
public class AntiPotion extends Module {

    private final Potion[] badEffects = new Potion[]{Potion.moveSlowdown, Potion.digSlowdown, Potion.harm, Potion.confusion, Potion.blindness, Potion.hunger, Potion.weakness, Potion.poison, Potion.wither};

    public AntiPotion() {
        super("anti-potion", "Removes potion effects", Category.PLAYER);
    }

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer.isPotionActive(Potion.blindness)) {
            mc.thePlayer.removePotionEffect(Potion.blindness.id);
        }
        if (mc.thePlayer.isPotionActive(Potion.confusion)) {
            mc.thePlayer.removePotionEffect(Potion.confusion.id);
        }
        if (mc.thePlayer.isPotionActive(Potion.digSlowdown)) {
            mc.thePlayer.removePotionEffect(Potion.digSlowdown.id);
        }
        if (mc.thePlayer.onGround) {
            for (Potion effect : this.badEffects) {
                if (!mc.thePlayer.isPotionActive(effect)) {
                    continue;
                }
                for (int a2 = 0; a2 <= 20; ++a2) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer.onGround));
                }
            }
            if (mc.thePlayer.getHealth() <= 15.0f && mc.thePlayer.isPotionActive(Potion.regeneration)) {
                for (int a3 = 0; a3 <= 10; ++a3) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer.onGround));
                }
            }
        }
    }
}
