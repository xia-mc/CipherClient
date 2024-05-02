/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.keybind;

import com.google.gson.JsonObject;
import fr.crazycat256.cipherclient.systems.System;
import fr.crazycat256.cipherclient.systems.IPopulable;
import fr.crazycat256.cipherclient.systems.ISavable;
import fr.crazycat256.cipherclient.systems.SystemManager;
import fr.crazycat256.cipherclient.systems.keybind.keybinds.*;

public class Keybinds extends System<Keybind> implements IPopulable, ISavable {

    public Keybinds() {
        super("keybinds");
    }

    public static Keybinds get() {
        return SystemManager.get(Keybinds.class);
    }

    @Override
    public void populate() {
        add(new OpenGui());
        add(new OpenConsole());
    }

    @Override
    public JsonObject serialize() {
        JsonObject keybindData = new JsonObject();
        for (Keybind keybind : this.getAll()) {
            keybindData.addProperty(keybind.name, keybind.getKey());
        }
        return keybindData;
    }

    @Override
    public void deserialize(JsonObject data) {
        for (Keybind keybind : this.getAll()) {
            keybind.setKey(data.get(keybind.name).getAsInt());
        }
    }
}
