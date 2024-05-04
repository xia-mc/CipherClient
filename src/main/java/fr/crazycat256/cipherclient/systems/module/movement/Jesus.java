/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.movement;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.events.custom.LiquidCollisionBoxEvent;
import fr.crazycat256.cipherclient.gui.settings.BoolSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.systems.module.Modules;
import fr.crazycat256.cipherclient.utils.MathUtils;
import fr.crazycat256.cipherclient.utils.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

import static net.minecraft.realms.RealmsMth.floor;

public class Jesus extends Module {

    public Jesus() {
        super("jesus", "Allows you to walk on water", Category.MOVEMENT);
    }

    private final Setting<Boolean> water = addSetting(new BoolSetting.Builder()
        .name("water")
        .description("Walk on water")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> lava = addSetting(new BoolSetting.Builder()
        .name("lava")
        .description("Walk on lava")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> other = addSetting(new BoolSetting.Builder()
        .name("other")
        .description("Walk on other liquids")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> dipOnSneak = addSetting(new BoolSetting.Builder()
        .name("dip-on-sneak")
        .description("Dip into water when sneaking")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> dipOnFall = addSetting(new BoolSetting.Builder()
        .name("dip-on-fall")
        .description("Dip into water when falling")
        .defaultValue(true)
        .build()
    );

    private boolean touchingLiquid = false;

    @Override
    public void onEnable() {
        touchingLiquid = false;
    }

    @Handler
    private void onLiquidCollisionBox(LiquidCollisionBoxEvent event) {
        if (mc.thePlayer == null || mc.thePlayer.movementInput == null) return;

        if (event.y + 1 > PlayerUtils.getFootPos().yCoord) return;
        if (mc.thePlayer.fallDistance >= 3.0f && dipOnFall.get()) return;
        if (mc.thePlayer.isSneaking() && dipOnSneak.get()) return;
        if (touchingLiquid) return;

        if (!water.get() && isWater(event.block)) return;
        if (!lava.get() && isLava(event.block)) return;
        if (other.get() && !isWater(event.block) && !isLava(event.block)) return;

        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(event.x, event.y, event.z, event.x + 1, event.y + 1, event.z + 1);
        event.setCollisionBox(box);
    }


    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        if (Modules.get().get(Flight.class).isActive()) return;

        AxisAlignedBB box = mc.thePlayer.boundingBox;
        double meanY = MathUtils.mean(box.minY, box.maxY);
        Vec3[] checkPositions = {
            Vec3.createVectorHelper(box.minX, box.minY, box.minZ),
            Vec3.createVectorHelper(box.minX, box.minY, box.maxZ),
            Vec3.createVectorHelper(box.maxX, box.minY, box.minZ),
            Vec3.createVectorHelper(box.maxX, box.minY, box.maxZ),
            Vec3.createVectorHelper(box.minX, meanY, box.minZ),
            Vec3.createVectorHelper(box.minX, meanY, box.maxZ),
            Vec3.createVectorHelper(box.maxX, meanY, box.minZ),
            Vec3.createVectorHelper(box.maxX, meanY, box.maxZ),
            Vec3.createVectorHelper(box.minX, box.maxY, box.minZ),
            Vec3.createVectorHelper(box.minX, box.maxY, box.maxZ),
            Vec3.createVectorHelper(box.maxX, box.maxY, box.minZ),
            Vec3.createVectorHelper(box.maxX, box.maxY, box.maxZ)
        };

        boolean isTuchingLiquid = false;
        for (Vec3 pos : checkPositions) {
            Block block = mc.theWorld.getBlock(floor(pos.xCoord), floor(pos.yCoord), floor(pos.zCoord));
            if (block instanceof BlockLiquid) {
                isTuchingLiquid = (isWater(block) && water.get())
                    || (isLava(block) && lava.get())
                    || (other.get() && !isWater(block) && !isLava(block));
                if (isTuchingLiquid) {
                    break;
                }
            }
        }

        if (touchingLiquid && !isTuchingLiquid) {
            mc.thePlayer.motionY = 0.2;
        }

        touchingLiquid = isTuchingLiquid;

        if (isTuchingLiquid && !(mc.thePlayer.isSneaking() && dipOnSneak.get())) {
            mc.thePlayer.motionY = 0.06000000238418583;
            touchingLiquid = true;
        }
    }

    private boolean isWater(Block block) {
        return block == Blocks.water || block == Blocks.flowing_water;
    }

    private boolean isLava(Block block) {
        return block == Blocks.lava || block == Blocks.flowing_lava;
    }
}
