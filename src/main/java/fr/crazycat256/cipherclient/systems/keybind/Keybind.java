/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.keybind;

import fr.crazycat256.cipherclient.systems.Element;

public abstract class Keybind extends Element {

    public final String name;
    public final int defaultKey;
    private int key;

    public Keybind(String name, int defaultKey) {
        super(name);
        this.name = name;
        this.defaultKey = defaultKey;
        this.key = defaultKey;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public void onPress() {}

}
