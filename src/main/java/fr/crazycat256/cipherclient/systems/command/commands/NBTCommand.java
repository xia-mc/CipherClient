/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.command.commands;

import fr.crazycat256.cipherclient.systems.command.Command;
import fr.crazycat256.cipherclient.utils.Utils;
import fr.crazycat256.cipherclient.utils.NBTUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;

public class NBTCommand extends Command {

    public NBTCommand() {
        super("nbt", "Allows you to copy/paste NBT data", "<copy/paste>");
    }

    @Override
    public void process(String[] args) {

        if (args.length < 1) {
            sendHelp();
            return;
        }
        if (args[0].equalsIgnoreCase("copy")) {
            NBTTagCompound tileTag = new NBTTagCompound();
            mc.thePlayer.getHeldItem().writeToNBT(tileTag);
            String nbt = NBTUtils.formatNBT(tileTag, 4, false);
            Utils.setClipboard(nbt);
        }
        else if (args[0].equalsIgnoreCase("paste")) {
            if (!mc.thePlayer.capabilities.isCreativeMode) {
                error("You can only paste NBT data in creative mode");
                return;
            }
            try {
                String string = Utils.getClipboard();
                String unFormattedNBT = "{itemNbt:" + NBTUtils.deIndent(string) + "}";
                NBTTagCompound nbt = (NBTTagCompound) JsonToNBT.func_150315_a(unFormattedNBT);
                NBTTagCompound tag = nbt.getCompoundTag("itemNbt").getCompoundTag("tag");
                ItemStack stack = mc.thePlayer.getHeldItem();
                stack.setTagCompound(tag);
            } catch (Exception e) {
                error("Failed to paste NBT data");
            }
        } else {
            sendHelp();
        }

    }

    @Override
    public String[] autoComplete(String[] args) {
        return new String[]{"copy", "paste"};
    }
}
