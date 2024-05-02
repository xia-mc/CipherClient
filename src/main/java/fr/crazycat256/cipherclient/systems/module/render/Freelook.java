/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.render;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.gui.settings.BoolSetting;
import fr.crazycat256.cipherclient.gui.settings.KeySetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.systems.module.Modules;
import fr.crazycat256.cipherclient.utils.EntityFakePlayer;
import fr.crazycat256.cipherclient.utils.PlayerUtils;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.*;
import org.lwjgl.input.Keyboard;

import static fr.crazycat256.cipherclient.utils.MathUtils.*;

public class Freelook extends Module {

    public Freelook() {
        super("freelook", "Allows you to look around without moving your player", Category.RENDER);
    }

    private final Setting<Integer> key = addSetting(new KeySetting.Builder()
        .name("key")
        .description("The key to toggle freelook")
        .defaultValue(Keyboard.KEY_LMENU)
        .build()
    );

    private final Setting<Boolean> scrollZoom = addSetting(new BoolSetting.Builder()
        .name("scroll-zoom")
        .description("Zoom in and out with the scroll wheel")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> noPitchLimit = addSetting(new BoolSetting.Builder()
        .name("no-pitch-limit")
        .description("Disables the pitch limit")
        .defaultValue(false)
        .build()
    );

    private EntityFakePlayer spectator;
    private boolean lastPressed;
    private double dist = 4;
    private float yaw, pitch;
    private float camYaw, camPitch;


    @Handler
    private void onRenderTick(TickEvent.RenderTickEvent event) {
        if (mc.currentScreen != null || mc.theWorld == null) return;

        if (Keyboard.isKeyDown(key.get()) && !Modules.get().get(FreezeCam.class).isActive()) {
            if (!lastPressed) {
                lastPressed = true;
                doFreelook();
            }

            camYaw -= yaw - mc.thePlayer.rotationYaw;
            camPitch -= pitch - mc.thePlayer.rotationPitch;

            mc.thePlayer.rotationYaw = yaw;
            mc.thePlayer.rotationPitch = pitch;

            if (!noPitchLimit.get()) {
                camPitch = MathHelper.clamp_float(camPitch, -90, 90);
            }

            mc.renderViewEntity.rotationYaw = camYaw;
            mc.renderViewEntity.rotationPitch = camPitch;
            Vec3 camVec = negate(vec3FromPolar(camPitch, camYaw));
            Vec3 pos = resize(camVec, dist).addVector(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

            mc.renderViewEntity.setPosition(pos.xCoord, pos.yCoord - 1.62, pos.zCoord);
            mc.gameSettings.thirdPersonView = 0;

        } else if (lastPressed) {
            lastPressed = false;
            undoFreelook();
        }
    }

    @Handler
    private void onRenderHand(RenderHandEvent event) {
        if (lastPressed) {
            event.setCanceled(true);
        }
    }

    @Handler
    private void onRenderPlayer(RenderPlayerEvent.Pre event) {
        if (lastPressed && event.entityPlayer == spectator) {
            event.setCanceled(true);
        }
    }

    @Handler
    private void onMouse(MouseEvent event) {
        if (lastPressed && scrollZoom.get() && event.dwheel != 0) {
            dist -= dist * event.dwheel / 480;
            dist = MathHelper.clamp_double(dist, 1, 4096);
            event.setCanceled(true);
        }
    }

    private void doFreelook() {
        if (mc.theWorld != null) {
            spectator = new EntityFakePlayer(mc.theWorld, mc.thePlayer.getGameProfile());
            spectator.inventory.copyInventory(mc.thePlayer.inventory);
            PlayerUtils.setPos(spectator, mc.thePlayer.getPosition(1).addVector(0, -1.62, 0));
            PlayerUtils.setLook(spectator, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);

            mc.theWorld.addEntityToWorld(-1, spectator);
            mc.renderViewEntity = spectator;
            yaw = camYaw = mc.thePlayer.rotationYaw;
            pitch = camPitch = mc.thePlayer.rotationPitch;
        }
    }

    private void undoFreelook() {
        mc.theWorld.removeEntityFromWorld(-1);
        mc.renderViewEntity = mc.thePlayer;
        dist = 4;
        spectator = null;
    }

    public boolean freeLooking() {
        return isActive() && lastPressed;
    }
}
