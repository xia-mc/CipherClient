/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.utils;

import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.events.custom.PacketEvent;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.S00PacketKeepAlive;

import java.util.ArrayList;
import java.util.List;

public class ServerUtils {

    private static final int HISTORY_SIZE = 5;
    private static int ping = -1;
    private static double tps = 20;
    private static final List<Integer> pingHistory = new ArrayList<>();

    /**
     * Get the current ping of the player in milliseconds
     * @return The current ping
     */
    public static int getPing() {
        return ping;
    }

    /**
     * Get the current TPS of the server (20 is the maximum)
     * @return The current TPS
     */
    public static double getTps() {
        return tps;
    }

    /**
     * @see NetHandlerPlayServer#onNetworkTick()
     */
    @Handler
    private void onPacket(PacketEvent.Receive event) {
        if (event.packet instanceof S00PacketKeepAlive) {
            int packetTime = ((S00PacketKeepAlive) event.packet).func_149134_c();

            while (pingHistory.size() >= HISTORY_SIZE) {
                pingHistory.remove(0);
            }
            pingHistory.add(packetTime);

            if (pingHistory.size() >= 2) {
                int diff = pingHistory.get(pingHistory.size() - 1) - pingHistory.get(0);
                int delay = 41 * 50 * (pingHistory.size() - 1); // The packet is sent every 41 ticks
                tps = 20.0 * delay / diff;
                if (tps > 20 && tps < 20.5) {
                    tps = 20;
                }
            }

            int lag = (int) (System.nanoTime() / 1000000L - packetTime);
            if (lag < 10_000 && lag >= 0) {
                ping = lag;
            } else {
                ping = -1;
            }
        }
    }
}
