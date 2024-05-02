/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.events.custom;

import cpw.mods.fml.common.eventhandler.Event;


public class MouseOverEvent extends Event {

    public double blockReach;
    public double entityReach;
    public double hitboxExpand;
    public MouseOverEvent(double blockReach, double entityReach) {
        this.blockReach = blockReach;
        this.entityReach = entityReach;
        this.hitboxExpand = 0;
    }
}
