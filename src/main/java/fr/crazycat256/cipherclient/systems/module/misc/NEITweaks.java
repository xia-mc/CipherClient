/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.misc;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.gui.settings.BoolSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.utils.Utils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiOpenEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.lang.reflect.Method;

public class NEITweaks extends Module {

    public NEITweaks() {
        super("nei-tweaks", "Some tweaks for NEI", Category.MISC);
    }

    @Override
    public boolean isWorking() {
        return Utils.classExists("codechicken.nei.guihook.GuiContainerManager");
    }

    public final Setting<Boolean> showHidden = addSetting(new BoolSetting.Builder()
        .name("show-hidden")
        .description("Show hidden items in NEI")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> give = addSetting(new BoolSetting.Builder()
        .name("give-on-ctrl+click")
        .description("Give item on click")
        .defaultValue(false)
        .build()
    );

    private boolean prevState = false;

    @Handler
    private void onGuiOpen(GuiOpenEvent event) {
        if (showHidden.get() && event.gui instanceof GuiContainer) {
            try {
                Class<?> itemStackMap = Class.forName("codechicken.nei.ItemStackMap");
                Class<?> itemInfo = Class.forName("codechicken.nei.api.ItemInfo");
                Method clear = itemStackMap.getDeclaredMethod("clear");
                clear.invoke(itemInfo.getDeclaredField("hiddenItems").get(null));
            } catch (Exception ignored) {}
        }
    }

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {

        if (!give.get() || !mc.playerController.isInCreativeMode()) {
            return;
        }

        boolean newState = Mouse.isButtonDown(0) && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
        if (newState && !prevState) {
            prevState = true;
            try {
                GuiContainer container = mc.currentScreen instanceof GuiContainer ? ((GuiContainer) mc.currentScreen) : null;
                if (container == null) {
                    return;
                }
                Object checkItem = Class.forName("codechicken.nei.guihook.GuiContainerManager").getDeclaredMethod("getStackMouseOver", GuiContainer.class).invoke(null, container);
                if (checkItem instanceof ItemStack) {
                    ItemStack item = (ItemStack) checkItem;
                    int count = GuiContainer.isShiftKeyDown() ? item.getMaxStackSize() : 1;
                    ItemStack itemStack = item.copy().splitStack(count);

                    if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                        itemStack.stackSize = item.getMaxStackSize();
                    }

                    // Find empty slot
                    int slot = -1;
                    for (int i = 9; i < 45; i++) {
                        if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                            continue;
                        }
                        slot = i;
                        break;
                    }

                    if (slot == -1) {
                        error("No empty slot");
                        return;
                    }

                    mc.playerController.sendSlotPacket(itemStack, slot);

                    info("Given " + itemStack.getDisplayName() + " x" + itemStack.stackSize);
                }
            } catch (Exception ignored) {

            }
        }
        prevState = newState;
    }
}
