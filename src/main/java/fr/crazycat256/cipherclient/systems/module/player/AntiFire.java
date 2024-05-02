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

public class AntiFire extends Module {

    public AntiFire() {
        super("anti-fire", "Removes fire", Category.PLAYER);
    }

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer.isBurning() && mc.thePlayer.onGround) {
            for (int i = 0; i < 10; ++i) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(false));
            }
        }
    }
}
