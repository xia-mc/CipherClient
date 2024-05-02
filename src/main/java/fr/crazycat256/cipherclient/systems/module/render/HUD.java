/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.render;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.CipherClient;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.gui.Colors;
import fr.crazycat256.cipherclient.gui.settings.BoolSetting;
import fr.crazycat256.cipherclient.gui.settings.IntSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.gui.clickgui.ClickGui;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.systems.module.Modules;
import fr.crazycat256.cipherclient.utils.RandomUtils;
import fr.crazycat256.cipherclient.utils.ReflectUtils;
import fr.crazycat256.cipherclient.utils.ServerUtils;
import fr.crazycat256.cipherclient.utils.font.Fonts;
import fr.crazycat256.cipherclient.utils.font.GlyphPageFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.floor;

public class HUD extends Module {
    public HUD() {
        super("hud", "Renders the HUD", Category.RENDER);
        this.enabled = true; // Enable by default
    }

    private String labelValue = CipherClient.NAME;
    private int rot = RandomUtils.randint(1, 26);
    int ticks = 0;


    private final Setting<Boolean> label = addSetting(new BoolSetting.Builder()
        .name("label")
        .description("Whether to display the client label")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> animation = addSetting(new BoolSetting.Builder()
        .name("animation")
        .description("Whether to animate the label")
        .defaultValue(true)
        .visible(label::get)
        .onChanged((oldValue, newValue) -> labelValue = CipherClient.NAME)
        .build()
    );

    private final Setting<Integer> delay = addSetting(new IntSetting.Builder()
        .name("delay")
        .description("The delay in ticks between each animation")
        .defaultValue(300)
        .min(0)
        .max(1000)
        .visible(() -> label.get() && animation.get())
        .onChanged((oldValue, newValue) -> labelValue = CipherClient.NAME)
        .build()
    );

    private final Setting<Boolean> position = addSetting(new BoolSetting.Builder()
        .name("position")
        .description("Whether to display your position")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> fps = addSetting(new BoolSetting.Builder()
        .name("fps")
        .description("Whether to display the FPS")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> tps = addSetting(new BoolSetting.Builder()
        .name("tps")
        .description("Whether to display the TPS")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> ping = addSetting(new BoolSetting.Builder()
        .name("ping")
        .description("Whether to display the ping")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> activeModules = addSetting(new BoolSetting.Builder()
        .name("active-modules")
        .description("Whether to display the active modules")
        .defaultValue(true)
        .build()
    );

    private final GlyphPageFontRenderer nameFont = Fonts.getFont(40);
    private final GlyphPageFontRenderer infoFont = Fonts.getFont(20);

    @Handler
    private void onGameOverlay(RenderGameOverlayEvent.Text event) {
        if (mc.gameSettings.showDebugInfo || mc.currentScreen instanceof ClickGui) return;

        int leftY = 2;
        int rightY = 2;

        if (label.get()) {
            nameFont.drawStringWithShadow(labelValue, 2, leftY, Colors.cipherColor);
            leftY += 24;
        }
        if (position.get()) {
            String coords = "Pos: " +  (int) floor(mc.renderViewEntity.posX) + "/" + (int) floor(mc.renderViewEntity.posY - 1.62) + "/" + (int) floor(mc.renderViewEntity.posZ);
            infoFont.drawStringWithShadow(coords, 2, leftY, Colors.cipherColor);
            leftY += 12;
        }
        if (fps.get()) {
            String fps = "FPS: " + ReflectUtils.get(Minecraft.class, mc, "debugFPS");
            infoFont.drawStringWithShadow(fps, 2, leftY, Colors.cipherColor);
            leftY += 12;
        }
        if (tps.get()) {
            String tps = String.format("TPS: %.2f", ServerUtils.getTps());
            infoFont.drawStringWithShadow(tps, 2, leftY, Colors.cipherColor);
            leftY += 12;
        }
        if (ping.get()) {
            String ping = "Ping: " + ServerUtils.getPing() + "ms";
            infoFont.drawStringWithShadow(ping, 2, leftY, Colors.cipherColor);
        }

        if (activeModules.get()) {
            List<Module> modules = Modules.get().getAll().stream()
                .filter(Module::isActive)
                .sorted(Comparator.comparing(module -> infoFont.getStringWidth(module.formattedName), Comparator.reverseOrder()))
                .collect(Collectors.toList());

            int scaledX = mc.displayWidth / (mc.gameSettings.guiScale != 0 ? mc.gameSettings.guiScale : 1) - 2;
            for (Module module : modules) {
                int size = infoFont.getStringWidth(module.formattedName);
                infoFont.drawStringWithShadow(module.formattedName, scaledX - size, rightY, Colors.cipherColor);
                rightY += 10;
            }
        }
    }



    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {

        if (!animation.get()) {
            return;
        }

        ticks++;

        if (ticks < delay.get() * 2 || ticks % 2 != 0) {
            return;
        }

        int t = ticks / 2;

        // Encrypt the label
        if (t < delay.get() + labelValue.length()) {
            int start = labelValue.length();
            int end = labelValue.length() - (t - delay.get() + 2);
            for (int i = start - 1; i > end; i--) {
                char c = labelValue.charAt(i);
                c = rotate(c, rot);
                labelValue = labelValue.substring(0, i) + c + labelValue.substring(i + 1);
            }
        }

        // Decrypt the label
        else if (t < delay.get() + labelValue.length() * 2) {
            int start = t - (delay.get() + labelValue.length());
            int end = labelValue.length();
            for (int i = start; i < end; i++) {
                char c = labelValue.charAt(i);
                c = rotate(c, -rot);
                labelValue = labelValue.substring(0, i) + c + labelValue.substring(i + 1);
            }
        }
        else {
            ticks = 0;
            rot = RandomUtils.randint(1, 26);
            labelValue = CipherClient.NAME;
        }
    }



    /**
     * Rotate characters between 31 (Space) and 126 (~)
     * @param c character to rotate
     * @param rot rotation amount
     * @return rotated character
     */
    private static char rotate(char c, int rot) {
        int min = 31;
        int max = 127 - min + 1;
        return (char) ((c - min + rot + max) % max + min);
    }
}
