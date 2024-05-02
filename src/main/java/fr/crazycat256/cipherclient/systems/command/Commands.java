/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.command;

import fr.crazycat256.cipherclient.systems.IPopulable;
import fr.crazycat256.cipherclient.systems.System;
import fr.crazycat256.cipherclient.systems.SystemManager;
import fr.crazycat256.cipherclient.systems.command.commands.*;

import java.util.TreeMap;


public class Commands extends System<Command> implements IPopulable {

    public Commands() {
        super("commands");
    }

    public static Commands get() {
        return SystemManager.get(Commands.class);
    }

    public Command getCommand(String name) {
        for (Command command : this.getAll()) {
            if (command.name.equalsIgnoreCase(name)) {
                return command;
            }
        }
        return null;
    }

    public TreeMap<String, Command> getCommands() {
        TreeMap<String, Command> commands = new TreeMap<>();
        for (Command command : this.getAll()) {
            commands.put(command.name, command);
        }
        return commands;
    }

    @Override
    public void populate() {
        add(new FriendCommand());
        add(new HclipCommand());
        add(new HelpCommand());
        add(new BindCommand());
        add(new NBTCommand());
        add(new OpItemCommand());
        add(new VClipCommand());
    }
}
