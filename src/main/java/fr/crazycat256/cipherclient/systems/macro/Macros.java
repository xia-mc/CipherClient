/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.macro;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.crazycat256.cipherclient.systems.System;
import fr.crazycat256.cipherclient.systems.ISavable;
import fr.crazycat256.cipherclient.systems.SystemManager;
import fr.crazycat256.cipherclient.utils.Utils;

import java.util.HashSet;

import static fr.crazycat256.cipherclient.CipherClient.mc;

public class Macros extends System<Macro> implements ISavable {

    private final HashSet<Integer> pressedKeys = new HashSet<>();
    private final boolean[] keyStates = new boolean[256];

    public Macros() {
        super("macros");
    }

    public static Macros get() {
        return SystemManager.get(Macros.class);
    }

    public boolean checkAndSaveKeyState(int key) {
        if (mc.currentScreen != null) {
            return false;
        }
        if (Utils.isKeyDown(key) != this.keyStates[key]) {
            pressedKeys.add(key);
            return Utils.isKeyDown(key);
        }
        return false;
    }

    public void handle() {
        for (Macro keyBinding : this.getAll()) {
            if (mc.theWorld == null || !this.checkAndSaveKeyState(keyBinding.getKeyCode())) {
                continue;
            }
            keyBinding.press();
        }
        for (int key : pressedKeys) {
            this.keyStates[key] = !this.keyStates[key];
        }
        pressedKeys.clear();
    }

    @Override
    public JsonObject serialize() {
        JsonObject macroData = new JsonObject();
        JsonArray macros = new JsonArray();
        for (Macro macro : this.getAll()) {
            macros.add(macro.serialize());
        }
        macroData.add("macros", macros);
        return macroData;
    }

    @Override
    public void deserialize(JsonObject data) {
        this.clear();
        JsonArray macros = data.getAsJsonArray("macros");
        for (int i = 0; i < macros.size(); i++) {
            JsonObject macroData = macros.get(i).getAsJsonObject();
            Macro macro = new Macro(macroData);
            this.add(macro);
        }
    }
}
