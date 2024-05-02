/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.misc;

import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.events.Handler;
import java.util.ArrayList;
import java.util.List;

import fr.crazycat256.cipherclient.utils.GLUtils;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ShowArmor extends Module {

    public ShowArmor() {
        super("show-armor", "Allows you to see armor of player or info of item", Category.MISC);
    }

    @Handler
    private void onGameOverlay(RenderGameOverlayEvent.Text event) {
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT) {
            Entity entityHit = getMouseOver(event.partialTicks, 5000, false);
            if (entityHit instanceof EntityPlayer) {
                EntityPlayer entity = (EntityPlayer) entityHit;
                int t = 0;
                for (int i = 3; i >= 0; i--) {
                    if (entity.inventory.armorInventory[i] == null) {
                        continue;
                    }
                    drawItemStack(entity.inventory.armorInventory[i], 4 + 8, 30 + t);
                    t += Math.max(GLUtils.drawHoveringText(getItemToolTip(entity.inventory.armorInventory[i]), 4 + 16 + 4 + 4, 34 + t, mc.fontRenderer), 16) + 10;
                }
                if (entity.inventory.getCurrentItem() != null) {
                    drawItemStack(entity.inventory.getCurrentItem(), 4 + 8, 30 + t);
                    t += Math.max(GLUtils.drawHoveringText(getItemToolTip(entity.inventory.getCurrentItem()), 4 + 16 + 4 + 4, 34 + t, mc.fontRenderer), 16) + 10;
                }
                return;
            }
            if (entityHit instanceof EntityLiving) {
                EntityLiving entity = (EntityLiving) entityHit;
                int t = 0;
                for (int i = 4; i >= 0; i--) {
                    if (entity.getEquipmentInSlot(i) == null) {
                        continue;
                    }
                    drawItemStack(entity.getEquipmentInSlot(i), 4 + 8, 30 + t);
                    t += Math.max(GLUtils.drawHoveringText(getItemToolTip(entity.getEquipmentInSlot(i)), 4 + 16 + 4 + 4, 34 + t, mc.fontRenderer), 16) + 10;
                }
                return;
            }
            if (entityHit instanceof EntityItem && ((EntityItem) entityHit).getEntityItem() != null) {
                EntityItem entity = (EntityItem) entityHit;
                int t = 0;
                drawItemStack(entity.getEntityItem(), 4 + 8, 30 + t);
                t += Math.max(GLUtils.drawHoveringText(getItemToolTip(entity.getEntityItem()), 4 + 16 + 4 + 4, 34 + t, mc.fontRenderer), 16) + 10;
            }
        }
    }

    public Entity getMouseOver(float partialTicks, double distance, boolean canBeCollidedWith) {
        Entity pointedEntity = null;

        if (mc.renderViewEntity != null) {
            if (mc.theWorld != null) {
                Vec3 positionVec = mc.renderViewEntity.getPosition(partialTicks);
                Vec3 lookVec = mc.renderViewEntity.getLook(partialTicks);
                Vec3 posDistVec = positionVec.addVector(lookVec.xCoord * distance, lookVec.yCoord * distance, lookVec.zCoord * distance);
                double boxExpand = 1.0F;
                @SuppressWarnings("unchecked")
                List<Entity> entities = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.renderViewEntity, mc.renderViewEntity.boundingBox.addCoord(lookVec.xCoord * distance, lookVec.yCoord * distance, lookVec.zCoord * distance).expand(boxExpand, boxExpand, boxExpand));
                double mincalc = Double.MAX_VALUE;
                for (Entity entity : entities) {
                    if (!canBeCollidedWith || entity.canBeCollidedWith()) {
                        double borderSize = entity.getCollisionBorderSize();
                        AxisAlignedBB expEntityBox = entity.boundingBox.expand(borderSize, borderSize, borderSize);
                        MovingObjectPosition calculateInterceptPos = expEntityBox.calculateIntercept(positionVec, posDistVec);
                        if (calculateInterceptPos != null) {
                            double calcInterceptPosDist = positionVec.distanceTo(calculateInterceptPos.hitVec);
                            if (mincalc > calcInterceptPosDist) {
                                mincalc = calcInterceptPosDist;
                                pointedEntity = entity;
                            }
                        }
                    }
                }
                return pointedEntity;
            }
        }

        return null;
    }

    private List<String> getItemToolTip(ItemStack itemStack) {
        if (itemStack == null) {
            return new ArrayList<>();
        }
        @SuppressWarnings("unchecked")
        List<String> list = itemStack.getTooltip(mc.thePlayer, true);

        for (int i = 0; i < list.size(); ++i) {
            if (i == 0) {
                list.set(i, itemStack.getRarity().rarityColor + list.get(i));
            } else {
                list.set(i, "\u00a77" + list.get(i));
            }
        }

        return list;
    }

    private void drawItemStack(ItemStack itemStack, int x, int y) {
        if (itemStack == null) {
            return;
        }
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderItem.getInstance().renderItemAndEffectIntoGUI(mc.fontRenderer, mc.renderEngine, itemStack, x - 8, y);
        RenderItem.getInstance().renderItemOverlayIntoGUI(mc.fontRenderer, mc.renderEngine, itemStack, x - 8, y);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
    }
}
