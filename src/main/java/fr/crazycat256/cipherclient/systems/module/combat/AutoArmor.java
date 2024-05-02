/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.combat;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.gui.settings.BoolSetting;
import fr.crazycat256.cipherclient.gui.settings.DoubleSetting;
import fr.crazycat256.cipherclient.gui.settings.IntSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.utils.InvUtils;
import fr.crazycat256.cipherclient.systems.module.Category;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingEvent;

public class AutoArmor extends Module {

    public AutoArmor() {
        super("auto-armor", "Automatically equips the best armor", Category.COMBAT);
    }

    private final Setting<Integer> delay = addSetting(new IntSetting.Builder()
        .name("delay")
        .description("The delay between armor swaps")
        .min(0)
        .max(20)
        .defaultValue(0)
        .build()
    );

    private final Setting<Integer> minDurability = addSetting(new IntSetting.Builder()
        .name("min-durability")
        .description("The minimum durability of the armor to use")
        .min(0)
        .max(500)
        .defaultValue(250)
        .build()
    );

    private final Setting<Boolean> onlyOnDamage = addSetting(new BoolSetting.Builder()
        .name("only-on-damage")
        .description("Only equips armor if the player has taken damage since the module was enabled")
        .defaultValue(false)
        .build()
    );

    private final Setting<Double> minDamage = addSetting(new DoubleSetting.Builder()
        .name("min-damage")
        .description("The minimum damage to take before equipping armor")
        .min(0)
        .max(20)
        .defaultValue(1.5)
        .visible(onlyOnDamage::get)
        .onChanged((old, newValue) -> {
            damageTaken = 0;
            prevHealth = mc.thePlayer.getHealth();
        })
        .build()
    );


    private int ticks;
    private double damageTaken;
    private double prevHealth;


    @Override
    public void onEnable() {
        ticks = 0;
        damageTaken = 0;
        prevHealth = mc.thePlayer.getHealth();
    }

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        if (ticks > 0) {
            ticks--;
            return;
        }
        if (mc.thePlayer.capabilities.isCreativeMode || mc.thePlayer.openContainer.windowId != 0) {
            return;
        }
        if (onlyOnDamage.get() && damageTaken < minDamage.get()) {
            return;
        }

        // 0: helmet, 1: chestplate, 2: leggings, 3: boots
        ArmorPiece[] bestArmor = new ArmorPiece[4];


        for (int i = 0; i < 36; i++) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack == null) {
                continue;
            }
            if (itemStack.getItem() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor) itemStack.getItem();
                ArmorPiece armorPiece = new ArmorPiece(i, itemStack);
                if (bestArmor[armor.armorType] == null || armorPiece.compareTo(bestArmor[armor.armorType]) >= 0) {
                    bestArmor[armor.armorType] = armorPiece;
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            ItemStack armor = mc.thePlayer.inventory.armorItemInSlot(i);
            if (armor != null) {
                ArmorPiece armorPiece = new ArmorPiece(-1, armor);
                if (bestArmor[3-i] == null || armorPiece.compareTo(bestArmor[3-i]) >= 0) {
                    bestArmor[3-i] = armorPiece;
                }
            }
        }

        for (ArmorPiece armorPiece : bestArmor) {
            if (armorPiece != null && armorPiece.slot != -1) {
                InvUtils.equipArmor(armorPiece.slot);
                ticks = delay.get();
                return;
            }
        }
    }

    @Handler
    private void onLiving(LivingEvent.LivingUpdateEvent event) {
        if (event.entityLiving == mc.thePlayer && prevHealth > mc.thePlayer.getHealth()) {
            damageTaken += prevHealth - mc.thePlayer.getHealth();
            prevHealth = mc.thePlayer.getHealth();
        }
    }


    private class ArmorPiece implements Comparable<ArmorPiece> {
        public final int slot;
        public final int damageReduction;
        public final int durability;

        public ArmorPiece(int slot, ItemStack itemStack) {
            if (!(itemStack.getItem() instanceof ItemArmor)) {
                throw new IllegalArgumentException("ItemStack is not an armor");
            }
            ItemArmor armor = (ItemArmor)itemStack.getItem();
            int enchantmentModifierDamage = EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{itemStack}, DamageSource.anvil);
            this.slot = slot;
            this.damageReduction = armor.damageReduceAmount + enchantmentModifierDamage;
            this.durability = itemStack.getMaxDamage() - itemStack.getItemDamage();
        }

        @Override
        public int compareTo(ArmorPiece o) {
            // If the durability of one of the armor pieces is less than the minimum durability, it should be prioritized
            if (this.durability < minDurability.get() ^ o.durability < minDurability.get()) {
                return this.durability == o.durability ? 0 : this.durability > minDurability.get() ? 1 : -1;
            } else {
                return Integer.compare(this.damageReduction, o.damageReduction);
            }
        }
    }
}
