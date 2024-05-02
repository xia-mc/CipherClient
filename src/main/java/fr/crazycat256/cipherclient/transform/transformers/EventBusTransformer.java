/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.transform.transformers;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventBus;
import fr.crazycat256.cipherclient.events.EventManager;
import fr.crazycat256.cipherclient.transform.Transform;
import fr.crazycat256.cipherclient.transform.Transformer;
import fr.crazycat256.cipherclient.utils.ASMUtils;
import org.objectweb.asm.tree.*;

public class EventBusTransformer extends Transformer {

    public EventBusTransformer() {
        super(EventBus.class);
    }

    /**
     * Add a call to {@link fr.crazycat256.cipherclient.events.EventManager#postEvent(Object)} at the beginning of the method
     */
    @Transform(methodName = "post", args = {Event.class})
    private void post(MethodNode mn) {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(ALOAD, 1));
        list.add(new MethodInsnNode(INVOKESTATIC, ASMUtils.getPath(EventManager.class), "postEvent", "(Ljava/lang/Object;)V", false));
        mn.instructions.insert(list);
    }

}
