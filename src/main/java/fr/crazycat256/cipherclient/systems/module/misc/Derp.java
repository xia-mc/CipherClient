/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.misc;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.gui.settings.BoolSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.utils.Rotations;

public class Derp extends Module {

    public Derp() {
        super("derp", "Just derp", Category.MISC);
    }

    private final Setting<Boolean> pitchLimit = addSetting(new BoolSetting.Builder()
        .name("pitch-limit")
        .description("Whether to limit the pitch")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> swing = addSetting(new BoolSetting.Builder()
        .name("swing")
        .description("Whether to swing the hand")
        .defaultValue(true)
        .build()
    );

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        float yaw = (float) Math.random() * 360;
        float pitch = pitchLimit.get() ? (float) Math.random() * 180 - 90 : (float) Math.random() * 360 - 180;
        Rotations.rotate(yaw, pitch, 1);
        if (swing.get()) {
            mc.thePlayer.swingItem();
        }
    }
}
