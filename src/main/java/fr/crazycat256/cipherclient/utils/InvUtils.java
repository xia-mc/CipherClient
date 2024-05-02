/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.utils;

import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import static fr.crazycat256.cipherclient.CipherClient.mc;

public class InvUtils {

    /**
     * Swap two items in the player's inventory
     * @param slot1 the slot index of the first item
     * @param slot2 the slot index of the second item
     */
    public static void swap(int slot1, int slot2) {
        mc.playerController.windowClick(0, slot1, 0, 0, mc.thePlayer);
        mc.playerController.windowClick(0, slot2, 0, 0, mc.thePlayer);
        mc.playerController.windowClick(0, slot1, 0, 0, mc.thePlayer);
    }

    /**
     * Swap two items, at least one of which is in the hotbar
     * @param invSlot the slot index of the item in the inventory
     * @param hotbarSlot the slot index of the item in the hotbar
     */
    public static void swapHotbar(int invSlot, int hotbarSlot) {
        mc.playerController.windowClick(0, invSlot, hotbarSlot, 2, mc.thePlayer);
    }

    /**
     * Returns the sorted list of the best tools to break a block
     * @param x x coordinate of the block
     * @param y y coordinate of the block
     * @param z z coordinate of the block
     * @return an array of integers representing the slot index of the best tool to break the block
     */
    public static int[] sortToolsFor(int x, int y, int z) {
        Block block = mc.theWorld.getBlock(x, y, z);
        int metadata = mc.theWorld.getBlockMetadata(x, y, z);
        if (block.getBlockHardness(mc.theWorld, x, y, z) < 0) {
            return null;
        }
        float[] slots = new float[9];
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
            if (stack == null) {
                slots[i] = 0;
            } else {
                int efficiencyLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack);
                float efficiencyBonus = efficiencyLevel > 0 ? (1 + efficiencyLevel * efficiencyLevel) : 0;
                slots[i] = stack.getItem().getDigSpeed(stack, block, metadata) + efficiencyBonus;
            }
        }
        int[] sorted = new int[9];
        for (int i = 0; i < 9; i++) {
            int maxIndex = 0;
            for (int j = 0; j < 9; j++) {
                if (slots[j] > slots[maxIndex]) {
                    maxIndex = j;
                }
            }
            sorted[i] = maxIndex;
            slots[maxIndex] = 0;
        }
        return sorted;
    }

    /**
     * Find a way to equip the armor in the slotId
     * @param slotId the slot index of the armor to equip
     */
    public static void equipArmor(int slotId) {
        EntityClientPlayerMP player = mc.thePlayer;
        ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(slotId);
        if (itemStack == null) {
            return;
        }
        int armorSlot = 5 + ((ItemArmor) itemStack.getItem()).armorType;

        if (slotId < 9) {
            slotId += 36;
        }

        boolean slotEmpty = player.inventory.armorItemInSlot(3 - ((ItemArmor) itemStack.getItem()).armorType) == null;
        if (isInvFull()) {
            if (slotEmpty) {
                mc.playerController.windowClick(0, slotId, 0, 1, player);
            }
            else {
                mc.playerController.windowClick(0, slotId, 0, 0, player);
                mc.playerController.windowClick(0, armorSlot, 0, 0, player);
                mc.playerController.windowClick(0, slotId, 0, 0, player);
            }
        } else {
            if (!slotEmpty) {
                mc.playerController.windowClick(0, armorSlot, 0, 1, player);
            }

            mc.playerController.windowClick(0, slotId, 0, 1, player);
        }
    }

    /**
     * Check if the player's inventory is full
     * @return true if the inventory is full, false otherwise
     */
    public static boolean isInvFull() {
        for (int i = 9; i < 36; i++) {
            if (mc.thePlayer.inventory.getStackInSlot(i) == null) {
                return false;
            }
        }
        return true;
    }
}
