/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.combat;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.friend.Friends;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.systems.module.Modules;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.gui.settings.IntSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Category;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class AimBot extends Module {

    public AimBot() {
        super("aim-bot", "Automatically points towards player", Category.COMBAT);
    }

    public void faceEntity(Entity e) {
        double x = e.posX - mc.thePlayer.posX;
        double y = e.posY - mc.thePlayer.posY;
        double z = e.posZ - mc.thePlayer.posZ;
        double d1 = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - (e.posY + e.getEyeHeight());
        double d3 = MathHelper.sqrt_double((x * x + z * z));
        float f = (float) (Math.atan2(z, x) * 180.0 / 3.141592653589793) - 90.0f;
        float f1 = (float) (-Math.atan2(d1, d3) * 180.0 / 3.141592653589793);
        mc.thePlayer.setPositionAndRotation(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, f, -f1);
    }

    private final Setting<Integer> distance = addSetting(new IntSetting.Builder()
        .name("distance")
        .description("Distance to aimbot")
        .defaultValue(3)
        .min(1)
        .max(16)
        .build()
    );

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        try {
            if (Modules.get().get(KillAura.class).isActive()) {
                return;
            }
            for (Object o : mc.theWorld.loadedEntityList) {
                if (!(o instanceof EntityPlayer)) {
                    continue;
                }
                EntityPlayer e = (EntityPlayer) o;

                if (e instanceof EntityPlayerSP || mc.thePlayer.getDistanceToEntity(e) > distance.get() ||
                        e.isDead || !mc.thePlayer.canEntityBeSeen(e) || !e.isEntityAlive() ||
                        Friends.get().isFriend(e.getCommandSenderName())) {
                    continue;
                }


                faceEntity(e);
                break;
            }
        } catch (Exception ignored) {}
    }
}
