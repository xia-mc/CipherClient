/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.command.commands;

import fr.crazycat256.cipherclient.CipherClient;
import fr.crazycat256.cipherclient.systems.command.Command;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;

public class OpItemCommand extends Command {

    public OpItemCommand() {
        super("opitem", "Tries to get an op item", "");
    }

    @Override
    public void process(String[] args) {
        ItemStack item = mc.thePlayer.getHeldItem();
        if (item != null) {

            item.stackSize = 64;
            item.setStackDisplayName("\u00a76\u00a7lOP Item");

            NBTTagList ench = new NBTTagList();
            int[] ids = {7, 16, 20, 35, 21, 33};
            for (int id : ids) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setShort("id", (short) id);
                tag.setShort("lvl", (short) (id != 21 ? 32767 : 1024));
                ench.appendTag(tag);
            }
            item.setTagInfo("ench", ench);

            NBTTagList attributeModifiers = new NBTTagList();
            NBTTagCompound healthAttribute = new NBTTagCompound();
            healthAttribute.setString("AttributeName", "generic.maxHealth");
            healthAttribute.setString("Name", "generic.maxHealth");
            healthAttribute.setDouble("Amount", 8);
            healthAttribute.setInteger("Operation", 1);
            healthAttribute.setLong("UUIDMost", 13337);
            healthAttribute.setLong("UUIDLeast", 105828);
            attributeModifiers.appendTag(healthAttribute);
            NBTTagCompound attackDamageAttribute = new NBTTagCompound();
            attackDamageAttribute.setString("AttributeName", "generic.attackDamage");
            attackDamageAttribute.setString("Name", "generic.attackDamage");
            attackDamageAttribute.setDouble("Amount", 32767);
            attackDamageAttribute.setInteger("Operation", 1);
            attackDamageAttribute.setLong("UUIDMost", 13337);
            attackDamageAttribute.setLong("UUIDLeast", 105828);
            attributeModifiers.appendTag(attackDamageAttribute);

            item.setTagInfo("AttributeModifiers", attributeModifiers);

        } else {
            CipherClient.consoleGui.printChatMessage(new ChatComponentText("\u00a7cYou must be holding an item in your hand"));
        }
    }
}
