/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.movement;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.systems.module.Category;
import net.minecraft.network.play.client.C03PacketPlayer;

public class NoFall extends Module {

    public NoFall() {
        super("no-fall", "Gives you zero damage on fall", Category.MOVEMENT);
    }

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer.fallDistance > 2.0f) {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.motionX, -999.0, -999.0, mc.thePlayer.motionZ, !mc.thePlayer.onGround));
        }
    }
}
