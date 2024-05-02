/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.render;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.systems.module.Category;

import java.util.Arrays;

public class Fullbright extends Module {

    public Fullbright() {
        super("fullbright", "Brights all area around you", Category.RENDER);
    }

    private int cooldownTicks = 0;

    @Override
    public void onEnable() {
        float[] bright = mc.theWorld.provider.lightBrightnessTable;
        Arrays.fill(bright, 1.0f);
    }

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        if (cooldownTicks == 0) {
            float[] bright = mc.theWorld.provider.lightBrightnessTable;
            Arrays.fill(bright, 1.0f);
        }
        cooldownTicks = (cooldownTicks + 1) % 80;
    }

    @Override
    public void onDisable() {
        mc.theWorld.provider.registerWorld(mc.theWorld);
    }
}
