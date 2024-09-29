/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.command.commands;

import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.systems.module.Modules;
import fr.crazycat256.cipherclient.systems.command.Command;
import java.util.ArrayList;
import org.lwjgl.input.Keyboard;

/**
 * Copied from <a href="https://github.com/radioegor146/ehacks-pro/blob/master/src/main/java/ehacks/mod/commands/classes/KeybindCommand.java">ehacks-pro</a>
 * @author radioegor146
 */
public class BindCommand extends Command {

    public BindCommand() {
        super("bind", "Set the keybind of a module", "<module> <key>");
    }

    private String escape(String text) {
        return text.replace("&", "&&");
    }

    @Override
    public void process(String[] args) {
        if (args.length > 0) {
            Module smod = null;
            for (Module mod : Modules.get().getAll()) {
                if (mod.getClass().getSimpleName().toLowerCase().replace(" ", "").equals(args[0])) {
                    smod = mod;
                }
            }
            if (smod == null) {
                error("No such module '" + escape(args[0]) + "'");
                return;
            }
            if (args.length == 1) {
                smod.setKeybind(0);
                info("Keybinds cleared");
                Modules.get().save();
                return;
            }
            if (Keyboard.getKeyIndex(args[1].toUpperCase()) == 0) {
                error("No such key '" + escape(args[1].toUpperCase()) + "'");
                return;
            }
            smod.setKeybind(Keyboard.getKeyIndex(args[1].toUpperCase()));
            info("Keybinding set");
            Modules.get().save();
            return;
        }
        sendHelp();
    }

    @Override
    public String[] autoComplete(String[] args) {

        if (args.length == 1) {
            ArrayList<String> allModules = new ArrayList<>();
            for (Module mod : Modules.get().getAll()) {
                if (mod.getClass().getSimpleName().replace(" ", "").toLowerCase().startsWith(args[0].toLowerCase())) {
                    allModules.add(mod.getClass().getSimpleName().toLowerCase().replace(" ", ""));
                }
            }
            return allModules.toArray(new String[0]);
        }
        return new String[0];
    }

}
