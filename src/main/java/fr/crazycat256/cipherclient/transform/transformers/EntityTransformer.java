/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.transform.transformers;

import fr.crazycat256.cipherclient.events.EventManager;
import fr.crazycat256.cipherclient.events.custom.EntityMoveEvent;
import fr.crazycat256.cipherclient.transform.Transform;
import fr.crazycat256.cipherclient.transform.Transformer;
import net.minecraft.entity.Entity;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class EntityTransformer extends Transformer {

    public EntityTransformer() {
        super(Entity.class);
    }

    /**
     * Add a call to {@link EntityTransformer#postEvent(Entity)} at the beginning of the method
     * @param mn The method node
     */
    @Transform(methodName = "moveEntity", args = {double.class, double.class, double.class})
    public void moveEntity(MethodNode mn) {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(ALOAD, 0));
        list.add(new MethodInsnNode(INVOKESTATIC, selfPath, "postEvent", "(Lnet/minecraft/entity/Entity;)V", false));
        mn.instructions.insert(list);
    }

    @SuppressWarnings("unused")
    public static void postEvent(Entity entity) {
        EntityMoveEvent event = new EntityMoveEvent(entity);
        EventManager.postEvent(event);
    }
}
