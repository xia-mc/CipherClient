/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.events;

import fr.crazycat256.cipherclient.events.custom.PacketEvent;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.Packet;

import static fr.crazycat256.cipherclient.CipherClient.mc;

/**
 * Copied from <a href="https://github.com/radioegor146/https://github.com/radioegor146/ehacks-pro/blob/master/src/main/blob/master/src/main/java/ehacks/mod/wrapper/PacketHandler.java">ehacks-pro</a>
 * @author radioegor146
 */
public class PacketHandler extends ChannelDuplexHandler {

    public PacketHandler() {
        try {
            ChannelPipeline pipeline = mc.getNetHandler().getNetworkManager().channel().pipeline();
            pipeline.addBefore("packet_handler", "PacketHandler", this);
        } catch (NullPointerException e) {
            // Empty catch block
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object o) throws Exception {
        if (o instanceof Packet) {
            Packet packet = (Packet) o;
            PacketEvent event = new PacketEvent.Receive(packet);
            EventManager.postEvent(event);
            if (event.isCanceled()) {
                return;
            }
        }
        super.channelRead(ctx, o);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object o, ChannelPromise promise) throws Exception {
        if (o instanceof Packet) {
            Packet packet = (Packet) o;
            PacketEvent event = new PacketEvent.Send(packet);
            EventManager.postEvent(event);
            if (event.isCanceled()) {
                return;
            }
        }
        super.write(ctx, o, promise);
    }
}
