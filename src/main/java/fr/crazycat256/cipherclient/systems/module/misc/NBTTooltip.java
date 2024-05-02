/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.misc;

import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.gui.settings.BoolSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.utils.NBTUtils;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.utils.Utils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.List;

public class NBTTooltip extends Module {
    public NBTTooltip() {
        super("nbt-tooltip", "Shows NBT data in item tooltips", Category.MISC);
    }

    private final Setting<Boolean> onlyOnCtrl = addSetting(new BoolSetting.Builder()
        .name("only-on-ctrl")
        .description("Only show NBT data when CTRL is held")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> color = addSetting(new BoolSetting.Builder()
        .name("color")
        .description("Colorize NBT data")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> copy = addSetting(new BoolSetting.Builder()
        .name("copy-on-ctrl+c")
        .description("Copy NBT data to clipboard when CTRL+C is pressed")
        .defaultValue(true)
        .build()
    );



    private boolean copied = false;

    @Handler
    private void onItemTooltip(ItemTooltipEvent event) {
        if (onlyOnCtrl.get() && !Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
            return;
        }
        NBTTagCompound tileTag = new NBTTagCompound();
        event.itemStack.writeToNBT(tileTag);

        String formatted = NBTUtils.formatNBT(tileTag, 4, color.get());
        List<String> formattedList = Arrays.asList(formatted.split("\n"));
        formattedList.set(0, "\u00a7bNBT: " + formattedList.get(0));
        event.toolTip.addAll(formattedList);

        if (!copied && copy.get() && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_C)) {
            Utils.setClipboard(NBTUtils.formatNBT(tileTag, 4, false));
        } else if (!Keyboard.isKeyDown(Keyboard.KEY_C)) {
            copied = false;
        }
    }
}
