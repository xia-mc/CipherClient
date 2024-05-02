/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.command.commands;

import fr.crazycat256.cipherclient.systems.command.Command;
import net.minecraft.util.Vec3;

public class VClipCommand extends Command {

    public VClipCommand() {
        super("vclip", "Lets you clip through blocks vertically", "<blocks>");
    }

    @Override
    public void process(String[] args) {
        if (args.length > 0) {

            try {
                Vec3 pos = mc.thePlayer.getPosition(1);
                int blocks = Integer.parseInt(args[0]);
                Vec3 newPos = Vec3.createVectorHelper(pos.xCoord, pos.yCoord + blocks, pos.zCoord);

                mc.thePlayer.setPosition(newPos.xCoord, newPos.yCoord, newPos.zCoord);

            } catch (NumberFormatException e) {
                error("Invalid number");
            }

        }
        else {
            sendHelp();
        }
    }

    @Override
    public String[] autoComplete(String[] args) {
        return new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    }
}
