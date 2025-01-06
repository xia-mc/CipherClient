package fr.crazycat256.cipherclient.events.custom;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.util.MovementInputFromOptions;

/**
 * Fired before {@link MovementInputFromOptions#updatePlayerMoveState()} is called
 */
public class UpdateMoveStateEvent extends Event {

    public UpdateMoveStateEvent() {

    }

}
