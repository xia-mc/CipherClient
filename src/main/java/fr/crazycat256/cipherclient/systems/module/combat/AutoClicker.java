/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.combat;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.systems.module.Category;
import java.lang.reflect.Method;

import fr.crazycat256.cipherclient.systems.module.Modules;
import fr.crazycat256.cipherclient.utils.CombatUtils;
import fr.crazycat256.cipherclient.utils.ReflectUtils;
import net.minecraft.client.Minecraft;

public class AutoClicker extends Module {

    @SuppressWarnings("rawtypes")
    private final Method click = ReflectUtils.getMethod(Minecraft.class, "func_147116_af", (Class[]) null);

    public AutoClicker() {
        super("auto-clicker", "Automatically clicks for you", Category.COMBAT);
    }

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        if (mc.gameSettings.keyBindAttack.getIsKeyPressed()) {
            if ((Modules.get().get(Criticals.class)).isActive() && !mc.thePlayer.isInWater()) {
                CombatUtils.performCrit();
            }
            ReflectUtils.call(click, mc);
        }
    }
}
