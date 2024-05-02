/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.combat;

import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.events.custom.MouseOverEvent;
import fr.crazycat256.cipherclient.gui.settings.DoubleSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.systems.module.Category;

public class Hitboxes extends Module {

    public Hitboxes() {
        super("hitboxes", "Increases the size of entity hitboxes", Category.COMBAT);
    }

    private final Setting<Double> expand = addSetting(new DoubleSetting.Builder()
        .name("expand")
        .description("How much to expand the hitboxes")
        .min(0.0)
        .max(1.0)
        .defaultValue(0.5)
        .build()
    );

    @Handler
    private void onMouseOver(MouseOverEvent event) {
        event.hitboxExpand = expand.get();
    }
}
