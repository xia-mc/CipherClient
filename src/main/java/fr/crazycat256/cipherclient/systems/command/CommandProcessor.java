/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.command;

import fr.crazycat256.cipherclient.CipherClient;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandProcessor {
    public static void processString(String message) {
        message = message.trim();

        if (message.startsWith("/")) {
            message = message.substring(1);
        }

        String[] temp = message.split(" ");
        String[] args = new String[temp.length - 1];
        String commandName = temp[0];
        System.arraycopy(temp, 1, args, 0, args.length);
        try {
            processCommand(Commands.get().getCommand(commandName), args);
        } catch (Exception e) {
            CipherClient.consoleGui.printChatMessage(format(EnumChatFormatting.RED, "commands.generic.exception"));
        }
    }

    public static ChatComponentTranslation format(EnumChatFormatting color, String str, Object... args) {
        ChatComponentTranslation ret = new ChatComponentTranslation(str, args);
        ret.getChatStyle().setColor(color);
        return ret;
    }

    public static void processCommand(Command command, String[] args) {
        if (command == null) {
            CipherClient.consoleGui.printChatMessage(format(EnumChatFormatting.RED, "commands.generic.notFound"));
            return;
        }
        command.process(args);
    }

    public static String[] autoComplete(String message) {
        if (message.startsWith("/")) {
            message = message.substring(1);
        }

        String[] temp = message.trim().split(" ");
        String[] args = new String[temp.length - 1];
        String commandName = temp[0];
        System.arraycopy(temp, 1, args, 0, args.length);

        Command command = Commands.get().getCommand(commandName);
        if (command != null) {
            ArrayList<String> targs = new ArrayList<>(Arrays.asList(args));
            if (message.endsWith(" ")) {
                targs.add("");
            }
            return command.autoComplete(targs.toArray(new String[0]));
        } else if (args.length == 0 && !message.endsWith(" ")) {
            ArrayList<String> availableNames = new ArrayList<>();
            for (String name : Commands.get().getCommands().keySet()) {
                if (name.startsWith(commandName)) {
                    availableNames.add("/" + name);
                }
            }
            return availableNames.toArray(new String[0]);
        }
        return new String[0];
    }
}
