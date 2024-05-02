/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.friend;

import fr.crazycat256.cipherclient.systems.Element;
import fr.crazycat256.cipherclient.systems.MultiInstance;

@MultiInstance
public class Friend extends Element {

    public final String name;

    public Friend(String name) {
        super("Friend");
        this.name = name;
    }
}
