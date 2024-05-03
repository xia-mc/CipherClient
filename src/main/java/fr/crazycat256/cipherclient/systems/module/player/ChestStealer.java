/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.player;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.gui.settings.BoolSetting;
import fr.crazycat256.cipherclient.gui.settings.IntSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.systems.module.Category;
import net.minecraft.inventory.Container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ChestStealer extends Module {

    private int ticks = 0;

    private static final Class<?>[] validChests = new Class<?>[] {
        net.minecraft.inventory.ContainerChest.class,
        net.minecraft.inventory.ContainerDispenser.class,
        net.minecraft.inventory.ContainerFurnace.class,
        net.minecraft.inventory.ContainerHopper.class,
        net.minecraft.inventory.ContainerBrewingStand.class,
        net.minecraft.inventory.ContainerBeacon.class,
        net.minecraft.inventory.ContainerRepair.class,
        net.minecraft.inventory.ContainerEnchantment.class,
        tryGetClass("cpw.mods.ironchest.ContainerIronChest"),
        tryGetClass("noppes.npcs.containers.ContainerCrate"),
    };

    public ChestStealer() {
        super("chest-stealer", "Steals all stuff from chests", Category.PLAYER);
    }

    private final Setting<Integer> delay = addSetting(new IntSetting.Builder()
        .name("delay")
        .description("The delay in ticks between each item steal")
        .defaultValue(1)
        .min(0)
        .max(10)
        .build()
    );

    private final Setting<Boolean> autoClose = addSetting(new BoolSetting.Builder()
        .name("auto-close")
        .description("Automatically closes the chest after stealing all items")
        .defaultValue(true)
        .build()
    );


    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        ticks--;
        if (event.phase != TickEvent.Phase.START) {
            return;
        }
        if (mc.inGameHasFocus) {
            return;
        }
        System.out.println(mc.thePlayer.openContainer.getClass().getName());
        if (Arrays.stream(validChests)
            .filter(Objects::nonNull)
            .noneMatch(klass -> klass.isInstance(mc.thePlayer.openContainer))) {
            return;
        }
        List<Integer> slots = getNonEmptySlots(mc.thePlayer.openContainer);
        if (slots.isEmpty()) {
            if (autoClose.get()) {
                mc.thePlayer.closeScreen();
            }
            return;
        }
        for (int slotId : slots) {
            if (ticks > 0) {
                break;
            }
            mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, slotId, 0, 1, mc.thePlayer);
            ticks = delay.get();
        }
    }

    private List<Integer> getNonEmptySlots(Container container) {
        int slotAmount = container.inventorySlots.size() - 36;
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < slotAmount; ++i) {
            if (container.getInventory().get(i) != null) {
                slots.add(i);
            }
        }
        return slots;
    }

    private static Class<?> tryGetClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
