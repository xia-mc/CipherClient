/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.render.xray;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cpw.mods.fml.common.gameevent.TickEvent;
import fr.crazycat256.cipherclient.events.Priority;
import fr.crazycat256.cipherclient.gui.components.widgets.Button;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.gui.settings.IntSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.gui.xraysettings.XRayGui;
import fr.crazycat256.cipherclient.utils.GLUtils;
import fr.crazycat256.cipherclient.systems.module.Category;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

/**
 * Copied from <a href="https://github.com/radioegor146/ehacks-pro/blob/master/src/main/java/ehacks/mod/modulesystem/classes/vanilla/XRay.java">ehacks-pro</a>
 * @author radioegor146
 */
public class XRay extends Module {

    private int displayListId = 0;
    public int cooldownTicks = 0;

    public final XRayGui xRayGui;
    public final ArrayList<XRayBlock> blocks = new ArrayList<>();

    public XRay() {
        super("xray", "Displays ores and other blocks through walls", Category.RENDER);
        this.xRayGui = new XRayGui();

        addWidget(new Button("Open Settings", () -> mc.displayGuiScreen(this.xRayGui)));

        // Remove invalid blocks
        for (int i = 0; i < blocks.size(); ++i) {
            XRayBlock block = blocks.get(i);
            if (Block.blockRegistry.containsKey(block.id)) {
                continue;
            }
            blocks.remove(block);
        }
    }


    private final Setting<Integer> radius = addSetting(new IntSetting.Builder()
        .name("radius")
        .description("The radius of the X-Ray")
        .defaultValue(45)
        .min(0)
        .max(250)
        .onChanged((oldVal, newVal) -> {
            if (this.displayListId != 0) {
                GL11.glDeleteLists(this.displayListId, 1);
            }
            this.displayListId = 0;
        })
        .build()
    );

    private final Setting<Integer> cooldown = addSetting(new IntSetting.Builder()
        .name("cooldown")
        .description("The cooldown of the X-Ray")
        .defaultValue(80)
        .min(0)
        .max(500)
        .onChanged((oldVal, newVal) -> this.cooldownTicks = newVal)
        .build()
    );

    @Override
    public void onEnable() {
        this.cooldownTicks = 0;
    }

    @Override
    public void onDisable() {
        if (this.displayListId != 0) {
            GL11.glDeleteLists(this.displayListId, 1);
        }
    }

    @Handler(priority = Priority.LOW)
    private void onWorldRender(RenderWorldLastEvent event) {
        if (mc.theWorld != null && this.displayListId != 0) {
            double doubleX = RenderManager.renderPosX;
            double doubleY = RenderManager.renderPosY;
            double doubleZ = RenderManager.renderPosZ;
            GL11.glPushMatrix();
            GL11.glTranslated((-doubleX), (-doubleY), (-doubleZ));
            GL11.glCallList(this.displayListId);
            GL11.glPopMatrix();
        }
    }

    @Handler
    private void onTick(TickEvent.ClientTickEvent event) {
        if (this.cooldownTicks < 1) {
            this.compileDL();
            this.cooldownTicks = cooldown.get();
        }
        --this.cooldownTicks;
    }

    private void compileDL() {
        if (mc.theWorld != null && mc.thePlayer != null) {
            if (this.displayListId == 0) {
                this.displayListId = GL11.glGenLists(5) + 3;
            }
            GL11.glNewList(this.displayListId, 4864);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glBegin(1);
            for (int i = (int) mc.thePlayer.posX - this.radius.get(); i <= (int) mc.thePlayer.posX + this.radius.get(); ++i) {
                for (int j = (int) mc.thePlayer.posZ - this.radius.get(); j <= (int) mc.thePlayer.posZ + this.radius.get(); ++j) {
                    int height = mc.theWorld.getHeightValue(i, j);
                    block2:
                    for (int k = 0; k <= height; ++k) {
                        Block bId = mc.theWorld.getBlock(i, k, j);
                        if (bId == Blocks.air || bId == Blocks.stone) {
                            continue;
                        }
                        for (XRayBlock block : blocks) {
                            if (!block.enabled || (Block.blockRegistry.getObject(block.id)) != bId || block.meta != -1 && block.meta != mc.theWorld.getBlockMetadata(i, k, j)) {
                                continue;
                            }
                            GLUtils.renderBlock(i, k, j, block);
                            continue block2;
                        }
                    }
                }
            }
            GL11.glEnd();
            GL11.glEnable(2929);
            GL11.glDisable(3042);
            GL11.glEnable(3553);
            GL11.glEndList();
        }
    }


    @Override
    public JsonObject serialize() {
        JsonObject data = super.serialize();
        JsonArray blocks = new JsonArray();
        for (XRayBlock block : this.blocks) {
            blocks.add(block.serialize());
        }
        data.add("blocks", blocks);

        return data;
    }

    @Override
    public void deserialize(JsonObject data) {
        super.deserialize(data);
        blocks.clear();
        JsonArray blocksData = data.getAsJsonArray("blocks");
        for (int i = 0; i < blocksData.size(); i++) {
            JsonObject block = blocksData.get(i).getAsJsonObject();
            XRayBlock xRayBlock = new XRayBlock();
            xRayBlock.deserialize(block);
            blocks.add(xRayBlock);
        }
    }
}
