/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems;

import fr.crazycat256.cipherclient.systems.command.Commands;
import fr.crazycat256.cipherclient.systems.friend.Friends;
import fr.crazycat256.cipherclient.systems.keybind.Keybinds;
import fr.crazycat256.cipherclient.systems.macro.Macros;
import fr.crazycat256.cipherclient.systems.module.Modules;

import java.util.ArrayList;


/**
 * Manages all systems in the client.
 */
public class SystemManager {

    private static final ArrayList<System<?>> SYSTEMS = new ArrayList<>();

    public static void add(System<?> system) {
        for (System<?> sys : SYSTEMS) {
            if (sys.getClass() == system.getClass()) {
                throw new IllegalArgumentException("System " + system.getClass().getName() + " already exists");
            }
        }
        SYSTEMS.add(system);
    }

    @SuppressWarnings("unchecked")
    public static <T extends System<?>> T get(Class<T> klass) {
        for (System<?> system : SYSTEMS) {
            if (klass.isInstance(system)) {
                return (T) system;
            }
        }
        throw new IllegalArgumentException("System " + klass.getName() + " not found");
    }

    public static ArrayList<System<?>> getAll() {
        return new ArrayList<>(SYSTEMS);
    }

    public static void init() {

        add(new Modules());
        add(new Keybinds());
        add(new Commands());
        add(new Macros());
        add(new Friends());

        for (System<?> system : SYSTEMS) {
            if (system instanceof IPopulable) {
                ((IPopulable) system).populate();
            }
            if (system instanceof ISavable) {
                try {
                    ((ISavable) system).load();
                } catch (Exception e) {
                    // Empty catch block
                }
            }
        }

        save();
    }

    public static void save() {
        for (System<?> system : SYSTEMS) {
            if (system instanceof ISavable) {
                ((ISavable) system).save();
            }
        }
    }
}
