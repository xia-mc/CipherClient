/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.misc;

import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.gui.settings.IntSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.events.Handler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class CoordLogger extends Module {


    public CoordLogger() {
        super("coord-logger", "Logs leaked coordinates to the console", Category.MISC);
    }

    private final Setting<Integer> minRange = addSetting(new IntSetting.Builder()
        .name("min-range")
        .description("Won't log coords if they are closer than this")
        .min(0)
        .max(1000)
        .defaultValue(100)
        .build()
    );

    private final ArrayList<ChunkCoordinates> coords = new ArrayList<>();

    @Override
    public void onEnable() {

        for (Object o : mc.theWorld.loadedEntityList) {
            if (o instanceof EntityItem) {
                EntityItem item = (EntityItem) o;
                ChunkCoordinates coords = checkItem(item.getEntityItem());
                if (coords == null) continue;
                String displayName = new ItemStack(item.getEntityItem().getItem(), 0).getDisplayName();
                info("Found coords in " + displayName + ": &4" + coords.posX + ", " + coords.posY + ", " + coords.posZ);
            }
        }
        for (Object o : mc.theWorld.playerEntities) {
            if (o instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) o;
                for (ItemStack item : player.inventory.mainInventory) {
                    if (item == null) continue;
                    ChunkCoordinates coords = checkItem(item);
                    if (coords == null) continue;
                    String displayName = new ItemStack(item.getItem(), 0).getDisplayName();
                    info("Found coords in " + displayName + " in " + player.getDisplayName() + "'s hand: &4" + coords.posX + ", " + coords.posY + ", " + coords.posZ);
                }
                for (ItemStack item : player.inventory.armorInventory) {
                    if (item == null) continue;
                    ChunkCoordinates coords = checkItem(item);
                    if (coords == null) continue;
                    String displayName = new ItemStack(item.getItem(), 0).getDisplayName();
                    info("Found coords in " + displayName + " in " + player.getDisplayName() + "'s armor: &4" + coords.posX + ", " + coords.posY + ", " + coords.posZ);
                }
            }
        }
    }

    @Handler
    private void onEntityJoinWorld(EntityJoinWorldEvent event) {
        Entity entity = event.entity;
        if (entity instanceof EntityItem) {
            EntityItem item = (EntityItem) entity;
            ChunkCoordinates coords = checkItem(item.getEntityItem());
            if (coords == null) return;
            String displayName = new ItemStack(item.getEntityItem().getItem(), 0).getDisplayName();
            info("Found coords in " + displayName + ": &4" + coords.posX + ", " + coords.posY + ", " + coords.posZ);
        }
    }

    private ChunkCoordinates checkItem(ItemStack item) {
        try {
            Class<?> drawerClass = Class.forName("com.jaquadro.minecraft.storagedrawers.item.ItemDrawers");
            if (drawerClass.isInstance(item.getItem())) {
                NBTTagCompound tileTag = new NBTTagCompound();
                item.writeToNBT(tileTag);
                NBTTagCompound drawerTag = tileTag.getCompoundTag("tag").getCompoundTag("tile");
                ChunkCoordinates coords = new ChunkCoordinates(drawerTag.getInteger("x"), drawerTag.getInteger("y"), drawerTag.getInteger("z"));
                if (!this.coords.contains(coords) && mc.thePlayer.getDistance(coords.posX, coords.posY, coords.posZ) > minRange.get()) {
                    this.coords.add(coords);
                    return coords;
                }
            }
        } catch (ClassNotFoundException ignored) {
        }

        if (Arrays.asList(names).contains(item.getUnlocalizedName())) {
            NBTTagCompound tag = item.getTagCompound();
            if (tag == null) return null;
            NBTTagCompound security = tag.getCompoundTag("SECURITY");
            if (security == null) return null;
            NBTTagCompound player = security.getCompoundTag("PLAYER");
            if (player == null) return null;
            NBTTagCompound pos = player.getCompoundTag("POS");
            if (pos == null) return null;
            ChunkCoordinates coords = new ChunkCoordinates(pos.getInteger("X"), pos.getInteger("Y"), pos.getInteger("Z"));
            if (!this.coords.contains(coords) && mc.thePlayer.getDistance(coords.posX, coords.posY, coords.posZ) > minRange.get()) {
                this.coords.add(coords);
                return coords;
            }
        }
        return null;
    }

    private final static String[] names = {
        "tile.endium.nugget.name",
        "item.endium.ingot.name",
        "item.endiumGauntlet.name",
        "item.LEGENDARYSTONE_RANDOM.name",
        "item.LEGENDARYSTONE_TELEPORTATION.name",
        "item.LEGENDARYSTONE_INVISIBILITY.name",
        "item.LEGENDARYSTONE_FORTUNE.name",
        "item.LEGENDARYSTONE_POWER.name",
        "item.LEGENDARYSTONE_JOBS.name",
        "item.LEGENDARYSTONE_CHAOS.name",
    };
}
