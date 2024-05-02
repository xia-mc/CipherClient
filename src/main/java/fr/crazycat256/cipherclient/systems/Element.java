/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems;

import fr.crazycat256.cipherclient.CipherClient;
import net.minecraft.util.ChatComponentText;

/**
 * Base class for modules, commands, etc.
 */
public abstract class Element {

    private final String elementName;
    public Element() {
        elementName = this.getClass().getSimpleName();
    }

    public Element(String elementName) {
        this.elementName = elementName;
    }

    protected void info(String message) {
        log(message, 'r');
    }

    protected void warn(String message) {
        log(message, 'e');
    }

    protected void error(String message) {
        log(message, 'c');
    }

    private void log(String message, char color) {
        message = "&9[&b" + elementName + "&9] &" + color + message;
        CipherClient.consoleGui.printChatMessage(new ChatComponentText(message.replace("&", "\u00a7").replace("\u00a7\u00a7", "&")));
    }

}
