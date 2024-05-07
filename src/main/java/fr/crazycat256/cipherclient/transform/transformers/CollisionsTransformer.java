/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.transform.transformers;

import fr.crazycat256.cipherclient.events.EventManager;
import fr.crazycat256.cipherclient.events.custom.CollisionBoxEvent;
import fr.crazycat256.cipherclient.transform.Transform;
import fr.crazycat256.cipherclient.transform.Transformer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import org.objectweb.asm.tree.MethodNode;

public class CollisionsTransformer extends Transformer {

    public CollisionsTransformer() {
        super(BlockLiquid.class, BlockFluidBase.class);
    }

    @Transform(methodName = "getCollisionBoundingBoxFromPool", args = {World.class, int.class, int.class, int.class})
    public void isBlockSolid(MethodNode mn) {
        mn.instructions.clear();
        mn.visitVarInsn(ALOAD, 0);
        mn.visitVarInsn(ILOAD, 2);
        mn.visitVarInsn(ILOAD, 3);
        mn.visitVarInsn(ILOAD, 4);
        mn.visitMethodInsn(INVOKESTATIC, selfPath, "postEvent", "(Lnet/minecraft/block/Block;III)Lnet/minecraft/util/AxisAlignedBB;", false);
        mn.visitInsn(ARETURN);
    }

    @SuppressWarnings("unused")
    public static AxisAlignedBB postEvent(Block block, int x, int y, int z) {
        CollisionBoxEvent event = new CollisionBoxEvent(block, x, y, z);
        EventManager.postEvent(event);
        return event.getCollisionBox();
    }
}
