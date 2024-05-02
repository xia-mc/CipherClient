/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.misc;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.systems.friend.Friend;
import fr.crazycat256.cipherclient.systems.friend.Friends;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.systems.module.Category;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;


public class MiddleClickFriend extends Module {

    public MiddleClickFriend() {
        super("middle-click-friend", "Add and remove friends by clicking on them", Category.MISC);
    }

    public boolean prevState = false;

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        MovingObjectPosition position = mc.objectMouseOver;
        boolean nowState = Mouse.isButtonDown(2);
        if (position != null && position.entityHit != null && nowState && !prevState) {
            if (position.entityHit instanceof EntityPlayer) {
                Friend friend = Friends.get().getFriend(position.entityHit.getCommandSenderName());
                if (friend != null) {
                    Friends.get().remove(friend);
                    info("Player " + position.entityHit.getCommandSenderName() + " was removed from friend list");
                } else {
                    Friends.get().add(new Friend(position.entityHit.getCommandSenderName()));
                    info("Player " + position.entityHit.getCommandSenderName() + " was added to friend list");
                }
                Friends.get().save();
            }
        }
        prevState = nowState;
    }
}
