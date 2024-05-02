/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.events.custom;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import fr.crazycat256.cipherclient.systems.module.Module;

@Cancelable
public abstract class ModuleEvent extends Event {

    public final Module module;

    @Override
    public boolean isCancelable() {
        return true;
    }

    public ModuleEvent(Module module) {
        this.module = module;
    }

    public static class Enable extends ModuleEvent {
        public Enable(Module module) {
            super(module);
        }
    }

    public static class Disable extends ModuleEvent {
        public Disable(Module module) {
            super(module);
        }
    }

}
