/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.transform.transformers;

import fr.crazycat256.cipherclient.events.EventManager;
import fr.crazycat256.cipherclient.events.custom.PlayerUpdateEvent;
import fr.crazycat256.cipherclient.transform.Transform;
import fr.crazycat256.cipherclient.transform.Transformer;
import fr.crazycat256.cipherclient.utils.ASMUtils;
import net.minecraft.client.entity.EntityClientPlayerMP;
import org.objectweb.asm.tree.*;

public class EntityClientPlayerMPTransformer extends Transformer {

    public EntityClientPlayerMPTransformer() {
        super(EntityClientPlayerMP.class);
    }

    /**
     * Calls {@link #postPreEvent(Object)} and {@link #postPostEvent(Object)} before and after the method
     */
    @Transform(methodName = "sendMotionUpdates")
    private void sendMotionUpdates(MethodNode mn) {
        AbstractInsnNode first = mn.instructions.getFirst();
        AbstractInsnNode last = ASMUtils.getLastReturn(mn);

        InsnList pre = new InsnList();
        pre.add(new VarInsnNode(ALOAD, 0));
        pre.add(new MethodInsnNode(INVOKESTATIC, selfPath, "postPreEvent", "(Ljava/lang/Object;)V", false));


        InsnList post = new InsnList();
        post.add(new VarInsnNode(ALOAD, 0));
        post.add(new MethodInsnNode(INVOKESTATIC, selfPath, "postPostEvent", "(Ljava/lang/Object;)V", false));


        mn.instructions.insertBefore(first, pre);
        mn.instructions.insertBefore(last, post);
    }


    @SuppressWarnings("unused")
    public static void postPreEvent(Object o) {
        EntityClientPlayerMP player = (EntityClientPlayerMP) o;
        PlayerUpdateEvent event = new PlayerUpdateEvent.Pre(player);
        EventManager.postEvent(event);
    }

    @SuppressWarnings("unused")
    public static void postPostEvent(Object o) {
        EntityClientPlayerMP player = (EntityClientPlayerMP) o;
        EventManager.postEvent(new PlayerUpdateEvent.Post(player));
    }

}
