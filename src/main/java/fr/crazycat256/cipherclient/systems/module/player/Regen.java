/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.player;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.gui.settings.IntSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Category;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Regen extends Module {

    public Regen() {
        super("regen", "Regenerates you", Category.PLAYER);
    }

    private final Setting<Integer> packetCount = addSetting(new IntSetting.Builder()
        .name("packet-count")
        .description("How many packets to send")
        .min(1)
        .max(1000)
        .defaultValue(100)
        .build()
    );

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer.onGround && mc.thePlayer.getHealth() <= mc.thePlayer.getMaxHealth() - 1.5 && mc.thePlayer.getFoodStats().getFoodLevel() > 8) {
            for (int i = 0; i < packetCount.get(); ++i) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(false));
            }
        }
    }
}
