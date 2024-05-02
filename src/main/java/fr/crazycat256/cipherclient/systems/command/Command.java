/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.command;

import fr.crazycat256.cipherclient.systems.Element;
import net.minecraft.client.Minecraft;

public abstract class Command extends Element {

    protected final Minecraft mc;

    public final String name;
    public final String description;
    public final String args;

    public Command(String name, String description, String args) {
        super(name);
        this.name = name;
        this.description = description;
        this.args = args;
        this.mc = Minecraft.getMinecraft();
    }

    public abstract void process(String[] args);

    public String[] autoComplete(String[] args) {
        return new String[0];
    }

    protected void sendHelp() {
        info(this.name + " " + args);
    }

}
