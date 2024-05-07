/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.misc;

import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.utils.JVMUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class DrawerPeek extends Module {

    public DrawerPeek() {
        super("drawer-peek", "Gives you information about the drawer you are looking at", Category.MISC);
    }

    @Override
    public boolean isWorking() {
        return JVMUtils.classExists("com.jaquadro.minecraft.storagedrawers.item.ItemDrawers");
    }

    @Handler
    private void onItemTooltip(ItemTooltipEvent event) {
        try {
            Class<?> drawerClass = Class.forName("com.jaquadro.minecraft.storagedrawers.item.ItemDrawers");
            if (!drawerClass.isInstance(event.itemStack.getItem())) return;

            NBTTagCompound tileTag = new NBTTagCompound();
            event.itemStack.writeToNBT(tileTag);
            NBTTagCompound drawerTag = tileTag.getCompoundTag("tag").getCompoundTag("tile");

            int x = drawerTag.getInteger("x");
            int y = drawerTag.getInteger("y");
            int z = drawerTag.getInteger("z");
            event.toolTip.add("\u00a74" + x + ", " + y + ", " + z);

            NBTTagList slots = drawerTag.getTagList("Slots", 10);

            for (int i = 0; i < slots.tagCount(); i++) {
                NBTTagCompound slotTag = slots.getCompoundTagAt(i);

                int itemCount = slotTag.getInteger("Count");
                short itemId = slotTag.getShort("Item");
                short itemMeta = slotTag.getShort("Meta");
                if (itemId != 0) {
                    String displayName = new ItemStack(Item.getItemById(itemId), 0, itemMeta).getDisplayName();
                    event.toolTip.add("\u00a76" + displayName + " x" + itemCount);
                } else {
                    event.toolTip.add("\u00a76Empty");
                }
            }

        } catch (ClassNotFoundException e) {
            // Empty catch block
        }
    }
}
