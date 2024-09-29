/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.player;

import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.events.Handler;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.MouseEvent;

/**
 * Copied from <a href="https://github.com/radioegor146/ehacks-pro/blob/master/src/main/java/ehacks/mod/modulesystem/classes/vanilla/FakeDestroy.java">ehacks-pro</a>
 * @author radioegor146
 */
public class FakeDestroy extends Module {

    public FakeDestroy() {
        super("fake-destroy", "Destroy blocks on client with left click", Category.WORLD);
    }

    private ArrayList<BlockInfo> removedBlocks = new ArrayList<>();


    @Handler
    private void onMouse(MouseEvent event) {
        if (event.button == 0 && event.buttonstate && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            Block block = mc.theWorld.getBlock(mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ);
            int meta = mc.theWorld.getBlockMetadata(mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ);
            removedBlocks.add(new BlockInfo(new int[]{mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ}, block, meta));
            mc.theWorld.setBlockToAir(mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ);
            event.setCanceled(true);
        }
    }

    @Override
    public void onEnable() {
        removedBlocks = new ArrayList<>();
    }

    @Override
    public void onDisable() {
        for (BlockInfo removedBlock : removedBlocks) {
            mc.theWorld.setBlock(removedBlock.coords[0], removedBlock.coords[1], removedBlock.coords[2], removedBlock.block, removedBlock.meta, 3);
        }
    }


    private static class BlockInfo {

        public final int[] coords;
        public final Block block;
        public final int meta;

        public BlockInfo(int[] coords, Block block, int meta) {
            this.coords = coords;
            this.block = block;
            this.meta = meta;
        }
    }
}
