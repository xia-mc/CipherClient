/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.world;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.gui.settings.BoolSetting;
import fr.crazycat256.cipherclient.gui.settings.IntSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.utils.ReflectUtils;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class FastBreak extends Module {
    public FastBreak() {
        super("fast-break", "Gives you haste effect", Category.WORLD);
    }

    private final Setting<Integer> level = addSetting(new IntSetting.Builder()
        .name("level")
        .description("The level of haste effect")
        .min(0)
        .max(25)
        .defaultValue(1)
        .build()
    );

    private final Setting<Boolean> noDelay = addSetting(new BoolSetting.Builder()
        .name("no-delay")
        .description("Suppresses the delay between block breaking")
        .defaultValue(false)
        .build()
    );

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        if (level.get() != 0){
            mc.thePlayer.removePotionEffect(Potion.digSpeed.id);
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 1, level.get() - 1, true));
        } else {
            mc.thePlayer.removePotionEffect(Potion.digSpeed.id);
        }
        if (noDelay.get()) {
            ReflectUtils.set(PlayerControllerMP.class, mc.playerController, "blockHitDelay", 0);
        }
    }

    @Override
    public void onDisable() {
        mc.thePlayer.removePotionEffect(Potion.digSpeed.id);
    }
}
