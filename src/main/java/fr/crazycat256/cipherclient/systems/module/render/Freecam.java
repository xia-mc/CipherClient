/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.render;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.events.custom.UpdateMoveStateEvent;
import fr.crazycat256.cipherclient.gui.settings.BoolSetting;
import fr.crazycat256.cipherclient.gui.settings.EnumSetting;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.events.custom.PacketEvent;
import fr.crazycat256.cipherclient.gui.settings.DoubleSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.utils.*;
import fr.crazycat256.cipherclient.systems.module.Category;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderHandEvent;
import org.lwjgl.input.Keyboard;

public class Freecam extends Module {

    public Freecam() {
        super("freecam", "Allows you to move without moving on the server", Category.RENDER);
        this.autoStartable = false;
    }

    private final Setting<Mode> mode = addSetting(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("The mode of freecam")
        .defaultValue(Mode.CAMERA)
        .onChanged((oldValue, newValue) -> {
            if (oldValue == Mode.PLAYER) {
                disablePlayerMode();
            }
            if (oldValue == Mode.CAMERA) {
                disableCameraMode();
            }
            if (newValue == Mode.PLAYER) {
                enablePlayerMode();
            }
            if (newValue == Mode.CAMERA) {
                enableCameraMode();
            }
        })
        .build()
    );

    private final Setting<Double> horizontalSpeed = addSetting(new DoubleSetting.Builder()
        .name("horizontal-speed")
        .description("Speed of horizontal movement")
        .min(0.0)
        .max(10.0)
        .defaultValue(1.0)
        .build()
    );

    private final Setting<Double> verticalSpeed = addSetting(new DoubleSetting.Builder()
        .name("vertical-speed")
        .description("Speed of vertical movement")
        .min(0.0)
        .max(10.0)
        .defaultValue(1.0)
        .build()
    );

    private final Setting<Boolean> renderHand = addSetting(new BoolSetting.Builder()
        .name("render-hand")
        .description("Whether to render your hand in freecam")
        .defaultValue(false)
        .build()
    );

    private EntityFakePlayer fakePlayer = null;
    private long lastTickTime;
    private float yaw, pitch;
    private float camYaw, camPitch;

    @Override
    public void onEnable() {
        if (mc.thePlayer == null || mc.theWorld == null) {
            disable();
        }
        switch (mode.get()) {
            case PLAYER: {
                enablePlayerMode();
                break;
            }
            case CAMERA: {
                enableCameraMode();
                break;
            }
        }
    }

    @Override
    public void onDisable() {
        switch (mode.get()) {
            case PLAYER: {
                disablePlayerMode();
                break;
            }
            case CAMERA: {
                disableCameraMode();
                break;
            }
        }
    }

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        mc.gameSettings.thirdPersonView = 0;

        if (mode.get() == Mode.CAMERA) {
            return;
        }

        Vec3 move = PlayerUtils.getMovementVec3(mc.renderViewEntity);
        move.xCoord *= horizontalSpeed.get();
        move.yCoord *= verticalSpeed.get();
        move.zCoord *= horizontalSpeed.get();
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode())) {
            move = MathUtils.multiply(move, 1.5);
        }
        mc.thePlayer.setVelocity(move.xCoord, move.yCoord, move.zCoord);
    }

    @Handler
    private void onMoveState(UpdateMoveStateEvent event) {
        if (mode.get() == Mode.CAMERA) {
            mc.thePlayer.rotationYaw = ReflectUtils.get(EntityClientPlayerMP.class, mc.thePlayer, "oldRotationYaw");
            mc.thePlayer.rotationPitch = ReflectUtils.get(EntityClientPlayerMP.class, mc.thePlayer, "oldRotationPitch");
            ReflectUtils.setPressed(mc.gameSettings.keyBindForward, false);
            ReflectUtils.setPressed(mc.gameSettings.keyBindBack, false);
            ReflectUtils.setPressed(mc.gameSettings.keyBindLeft, false);
            ReflectUtils.setPressed(mc.gameSettings.keyBindRight, false);
            ReflectUtils.setPressed(mc.gameSettings.keyBindJump, false);
            ReflectUtils.setPressed(mc.gameSettings.keyBindSneak, false);
            ReflectUtils.setPressed(mc.gameSettings.keyBindSprint, false);
        }
    }

    @Handler
    private void onRenderTick(TickEvent.RenderTickEvent event) {
        if (mode.get() != Mode.CAMERA) return;

        double delta = (System.currentTimeMillis() - lastTickTime) / 50.0;
        lastTickTime = System.currentTimeMillis();

        Vec3 vec = MathUtils.vec3FromPolar(0, mc.renderViewEntity.rotationYaw).normalize();
        Vec3 move = Vec3.createVectorHelper(0, 0, 0);
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()))
            move = move.addVector(vec.xCoord, 0, vec.zCoord);
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()))
            move = move.addVector(-vec.xCoord, 0, -vec.zCoord);
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()))
            move = move.addVector(vec.zCoord, 0, -vec.xCoord);
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()))
            move = move.addVector(-vec.zCoord, 0, vec.xCoord);
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()))
            move = move.addVector(0, 1, 0);
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()))
            move = move.addVector(0, -1, 0);
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode()))
            move = MathUtils.multiply(move, 1.5);


        move.xCoord *= horizontalSpeed.get();
        move.yCoord *= verticalSpeed.get();
        move.zCoord *= horizontalSpeed.get();

        move = MathUtils.multiply(move, delta);


        Vec3 newPos = mc.renderViewEntity.getPosition(1F).addVector(move.xCoord, move.yCoord, move.zCoord);

        camYaw -= yaw - mc.thePlayer.rotationYaw;
        camPitch -= pitch - mc.thePlayer.rotationPitch;

        mc.thePlayer.rotationYaw = yaw;
        mc.thePlayer.rotationPitch = pitch;

        camPitch = MathHelper.clamp_float(camPitch, -90, 90);

        PlayerUtils.setPos(mc.renderViewEntity, newPos);
        PlayerUtils.setLook(mc.renderViewEntity, camYaw, camPitch);
    }

    @Handler
    private void onRenderHand(RenderHandEvent event) {
        if (!renderHand.get()) {
            event.setCanceled(true);
        }
    }

    @Handler
    private void onPacketSend(PacketEvent.Send event) {
        if (event.packet instanceof C03PacketPlayer && mode.get() == Mode.PLAYER) {
            C03PacketPlayer packet = (C03PacketPlayer) event.packet;

            ReflectUtils.set(C03PacketPlayer.class, packet, "field_149474_g", this.fakePlayer.onGround);

            if (event.packet instanceof C03PacketPlayer.C04PacketPlayerPosition || event.packet instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
                ReflectUtils.set(C03PacketPlayer.class, packet, "field_149479_a", this.fakePlayer.posX);
                ReflectUtils.set(C03PacketPlayer.class, packet, "field_149477_b", this.fakePlayer.posY - 1.62);
                ReflectUtils.set(C03PacketPlayer.class, packet, "field_149475_d", this.fakePlayer.posY);
                ReflectUtils.set(C03PacketPlayer.class, packet, "field_149478_c", this.fakePlayer.posZ);
            }

            if (event.packet instanceof C03PacketPlayer.C05PacketPlayerLook || event.packet instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
                ReflectUtils.set(C03PacketPlayer.class, packet, "field_149476_e", this.fakePlayer.rotationYaw);
                ReflectUtils.set(C03PacketPlayer.class, packet, "field_149473_f", this.fakePlayer.rotationPitch);
            }
        }
    }

    private void enablePlayerMode() {
        this.fakePlayer = new EntityFakePlayer(mc.theWorld, mc.thePlayer.getGameProfile());
        this.fakePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        this.fakePlayer.inventory = mc.thePlayer.inventory;
        this.fakePlayer.yOffset = mc.thePlayer.yOffset;
        this.fakePlayer.ySize = mc.thePlayer.ySize;
        this.fakePlayer.rotationPitch = mc.thePlayer.rotationPitch;
        this.fakePlayer.rotationYaw = mc.thePlayer.rotationYaw;
        this.fakePlayer.rotationYawHead = mc.thePlayer.rotationYawHead;
        mc.theWorld.spawnEntityInWorld(this.fakePlayer);
        mc.thePlayer.noClip = true;
    }

    private void disablePlayerMode() {
        if (mc.theWorld != null) {
            mc.thePlayer.setPosition(this.fakePlayer.posX, this.fakePlayer.posY, this.fakePlayer.posZ);
            mc.thePlayer.yOffset = this.fakePlayer.yOffset;
            mc.thePlayer.ySize = this.fakePlayer.ySize;
            mc.thePlayer.rotationPitch = this.fakePlayer.rotationPitch;
            mc.thePlayer.rotationYaw = this.fakePlayer.rotationYaw;
            mc.thePlayer.rotationYawHead = this.fakePlayer.rotationYawHead;
            mc.thePlayer.setVelocity(0, 0, 0);
            mc.theWorld.removeEntity(this.fakePlayer);
            this.fakePlayer = null;
            mc.thePlayer.noClip = false;
        }
    }

    private void enableCameraMode() {
        lastTickTime = System.currentTimeMillis();
        fakePlayer = new EntityFakePlayer(mc.theWorld, mc.thePlayer.getGameProfile());
        fakePlayer.inventory.copyInventory(mc.thePlayer.inventory);
        fakePlayer.lastTickPosX = fakePlayer.prevPosX = mc.thePlayer.posX;
        fakePlayer.lastTickPosY = fakePlayer.prevPosY = mc.thePlayer.posY  - 1.62;
        fakePlayer.lastTickPosZ = fakePlayer.prevPosZ = mc.thePlayer.posZ;
        fakePlayer.prevRotationYaw = mc.thePlayer.rotationYaw;
        fakePlayer.prevRotationPitch = mc.thePlayer.rotationPitch;
        fakePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1.62, mc.thePlayer.posZ);

        mc.theWorld.addEntityToWorld(-1, fakePlayer);
        mc.renderViewEntity = fakePlayer;

        yaw = camYaw = mc.thePlayer.rotationYaw;
        pitch = camPitch = mc.thePlayer.rotationPitch;

        mc.thePlayer.setSprinting(false);
        mc.thePlayer.setSneaking(false);
    }

    private void disableCameraMode() {
        mc.theWorld.removeEntityFromWorld(-1);
        mc.renderViewEntity = mc.thePlayer;
        fakePlayer = null;
    }

    public boolean isPlayerMode() {
        return mode.get() == Mode.PLAYER;
    }

    public enum Mode {
        PLAYER,
        CAMERA
    }
}
