/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.command.commands;

import fr.crazycat256.cipherclient.CipherClient;
import fr.crazycat256.cipherclient.systems.command.Command;
import fr.crazycat256.cipherclient.systems.command.Commands;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.TreeMap;

import static fr.crazycat256.cipherclient.systems.command.CommandProcessor.format;

/**
 * Copied from <a href="https://github.com/radioegor146/ehacks-pro/blob/master/src/main/java/ehacks/mod/commands/classes/HelpCommand.java">ehacks-pro</a>
 * @author radioegor146
 */
public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", "Help about commands", "[page|name]");
    }

    @Override
    public void process(String[] args) {
        int page = 0;
        TreeMap<String, Command> commands = Commands.get().getCommands();
        if (args.length > 0) {
            if (Commands.get().getCommand(args[0]) != null) {
                CipherClient.consoleGui.printChatMessage(new ChatComponentText("\u00a7c/" + commands.get(args[0]).name + " " + commands.get(args[0]).args));
                return;
            }
            try {
                page = Integer.parseInt(args[0]);
                page--;
                if (page > commands.size() / 6) {
                    CipherClient.consoleGui.printChatMessage(format(EnumChatFormatting.RED, "commands.generic.num.tooBig", page + 1, commands.size() / 6 + 1));
                    return;
                }
                if (page < 0) {
                    CipherClient.consoleGui.printChatMessage(format(EnumChatFormatting.RED, "commands.generic.num.tooSmall", 1, 1));
                    return;
                }
            } catch (Exception e) {
                CipherClient.consoleGui.printChatMessage(format(EnumChatFormatting.RED, "commands.generic.notFound"));
                return;
            }
        }
        String[] keys = commands.keySet().toArray(new String[0]);
        CipherClient.consoleGui.printChatMessage(format(EnumChatFormatting.DARK_GREEN, "commands.help.header", page + 1, keys.length / 6 + 1));
        for (int i = page * 6; i < Math.min(page * 6 + 6, keys.length); i++) {
            CipherClient.consoleGui.printChatMessage(new ChatComponentText("/" + commands.get(keys[i]).name + " - " + commands.get(keys[i]).description));
        }
    }
}
