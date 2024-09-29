/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.movement;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.gui.settings.IntSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Category;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * Copied from <a href="https://github.com/radioegor146/ehacks-pro/blob/master/src/main/java/ehacks/mod/modulesystem/classes/vanilla/HighJump.java">ehacks-pro</a>
 * @author radioegor146
 */
public class HighJump extends Module {

    public HighJump() {
        super("high-jump", "Gives you Jump Boost effect", Category.MOVEMENT);
    }

    private final Setting<Integer> level = addSetting(new IntSetting.Builder()
        .name("level")
        .description("Level of the jump boost")
        .min(1)
        .max(20)
        .defaultValue(2)
        .build()
    );

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        mc.thePlayer.removePotionEffect(Potion.jump.getId());
        mc.thePlayer.addPotionEffect(new PotionEffect(Potion.jump.getId(), 1, level.get() - 1));
    }

    @Override
    public void onDisable() {
        mc.thePlayer.removePotionEffect(Potion.jump.getId());
    }
}
