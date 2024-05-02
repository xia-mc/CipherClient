/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.command.commands;

import fr.crazycat256.cipherclient.systems.command.Command;

public class HclipCommand extends Command {

    public HclipCommand() {
        super("hclip", "Lets you clip through blocks horizontally", "<blocks>");
    }


    @Override
    public void process(String[] args) {
        if (args.length > 0) {
            try {
                double blocks = Double.parseDouble(args[0]);
                double x = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F)) * blocks;
                double z = Math.sin(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F)) * blocks;
                mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
            } catch (NumberFormatException e) {
                error("Invalid number");
            }
        } else {
            sendHelp();
        }
    }


    @Override
    public String[] autoComplete(String[] args) {
        return new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    }
}
