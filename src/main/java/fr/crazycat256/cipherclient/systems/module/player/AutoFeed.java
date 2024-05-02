/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.player;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.systems.module.Category;

public class AutoFeed extends Module {
    public AutoFeed() {
        super("auto-feed", "Automatically feeds you", Category.PLAYER);
    }

    private int ticks = 0;
    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        if (ticks > 0) {
            ticks--;
            return;
        }
        if (mc.thePlayer.getFoodStats().needFood()){
            mc.thePlayer.sendChatMessage("/feed");
            ticks = 20;
        }
    }
}
