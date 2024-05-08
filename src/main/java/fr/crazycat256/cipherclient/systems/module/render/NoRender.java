/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.render;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.gui.settings.BoolSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Category;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import org.lwjgl.opengl.GL11;

public class NoRender extends Module {

    public NoRender() {
        super("no-render", "Disables rendering of certain things", Category.RENDER);
    }

    private final Setting<Boolean> weather = addSetting(new BoolSetting.Builder()
        .name("weather")
        .description("Stops the rain rendering")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> fog = addSetting(new BoolSetting.Builder()
        .name("fog")
        .description("Disables fog rendering")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> blockOverlay = addSetting(new BoolSetting.Builder()
        .name("block-overlay")
        .description("Disables block overlay rendering")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> waterOverlay = addSetting(new BoolSetting.Builder()
        .name("water-overlay")
        .description("Disables water overlay rendering")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> fireOverlay = addSetting(new BoolSetting.Builder()
        .name("fire-overlay")
        .description("Disables fire overlay rendering")
        .defaultValue(false)
        .build()
    );


    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        if (weather.get() && mc.theWorld != null) {
            mc.theWorld.setRainStrength(0.0f);
        }
    }

    @Handler
    private void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        if (fog.get()) {
            GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
            event.density = 0.0f;
            event.setCanceled(true);
        }
    }


    @Handler
    private void onBlockOverlayRender(RenderBlockOverlayEvent event) {
        switch (event.overlayType) {
            case BLOCK: {
                if (blockOverlay.get()){
                    event.setCanceled(blockOverlay.get());
                }
                break;
            }
            case WATER: {
                if (waterOverlay.get()) {
                    event.setCanceled(blockOverlay.get());
                }
                break;
            }
            case FIRE: {
                if (fireOverlay.get()) {
                    event.setCanceled(blockOverlay.get());
                }
                break;
            }
        }
    }
}
