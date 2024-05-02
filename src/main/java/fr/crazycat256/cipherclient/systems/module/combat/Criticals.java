/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.combat;

import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.utils.CombatUtils;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.events.Handler;
import net.minecraftforge.client.event.MouseEvent;

public class Criticals extends Module {

    public Criticals() {
        super("criticals", "Performs critical hits", Category.COMBAT);
    }

    @Handler
    private void onMouse(MouseEvent event) {
        if (event.button == 0 && event.buttonstate && mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null) {
            CombatUtils.performCrit();
        }
    }
}
