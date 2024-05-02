/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.world;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.gui.settings.DoubleSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.utils.ReflectUtils;
import net.minecraft.client.Minecraft;


public class Timer extends Module {

    private final net.minecraft.util.Timer timer = ReflectUtils.get(Minecraft.class, mc, "timer");


    public Timer() {
        super("timer", "Runs the game at a different speed", Category.WORLD);
    }

    private final Setting<Double> speed = addSetting(new DoubleSetting.Builder()
        .name("speed")
        .description("Speed of the game")
        .min(0.05)
        .max(10.0)
        .defaultValue(1.0)
        .build()
    );

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        timer.timerSpeed = speed.get().floatValue();
    }

    @Override
    public void onDisable() {
        timer.timerSpeed = 1.0f;
    }
}
