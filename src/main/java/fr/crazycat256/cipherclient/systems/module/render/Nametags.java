/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.render;

import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.gui.settings.BoolSetting;
import fr.crazycat256.cipherclient.gui.settings.DoubleSetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.systems.module.Modules;
import fr.crazycat256.cipherclient.utils.CombatUtils;
import fr.crazycat256.cipherclient.utils.EntityFakePlayer;
import fr.crazycat256.cipherclient.utils.ReflectUtils;
import fr.crazycat256.cipherclient.utils.WorldUtils;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Set;

public class Nametags extends Module {

    // Ported from Neat https://github.com/VazkiiMods/Neat/tree/c5961631ddcdb02a95f262e910ddd7b46c168278
    public Nametags() {
        super("nametags", "Renders nametags above entities", Category.RENDER);
    }

    private final Setting<Double> scale = addSetting(new DoubleSetting.Builder()
        .name("scale-multiplier")
        .description("Scale multiplier for the health bar")
        .defaultValue(1.0)
        .min(0.0)
        .max(10.0)
        .build()
    );

    private final Setting<Double> maxSize = addSetting(new DoubleSetting.Builder()
        .name("max-size")
        .description("Max size of the health bar")
        .defaultValue(10.0)
        .min(0.0)
        .max(100.0)
        .build()
    );

    private final Setting<Boolean> players = addSetting(new BoolSetting.Builder()
        .name("players")
        .description("Render players")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> hostile = addSetting(new BoolSetting.Builder()
        .name("hostile")
        .description("Render hostile mobs")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> passive = addSetting(new BoolSetting.Builder()
        .name("passive")
        .description("Render passive mobs")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> other = addSetting(new BoolSetting.Builder()
        .name("other")
        .description("Render other entities")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> items = addSetting(new BoolSetting.Builder()
        .name("items")
        .description("Render items")
        .defaultValue(false)
        .build()
    );



    @Handler
    public void onRenderWorldLast(RenderWorldLastEvent event) {


        EntityLivingBase cameraEntity = mc.renderViewEntity;
        Vec3 renderingVector = cameraEntity.getPosition(event.partialTicks);
        Frustrum frustrum = new Frustrum();

        double viewX = WorldUtils.getX(cameraEntity, event.partialTicks);
        double viewY = WorldUtils.getY(cameraEntity, event.partialTicks);
        double viewZ = WorldUtils.getZ(cameraEntity, event.partialTicks);
        frustrum.setPosition(viewX, viewY, viewZ);

        WorldClient client = mc.theWorld;
        Set<Entity> entities = ReflectUtils.get(WorldClient.class, client, "entityList");
        for(Entity entity : entities) {

            if (!entity.isInRangeToRender3d(renderingVector.xCoord, renderingVector.yCoord, renderingVector.zCoord))
                continue;
            if (!(entity.ignoreFrustumCheck || frustrum.isBoundingBoxInFrustum(entity.boundingBox)) || !entity.isEntityAlive())
                continue;

            if (entity instanceof EntityLivingBase) {

                if (entity == cameraEntity && mc.gameSettings.thirdPersonView == 0) {
                    continue;
                }

                Freecam freecam = Modules.get().get(Freecam.class);
                if (entity instanceof EntityClientPlayerMP) {
                    if (!players.get()) continue;
                    if (freecam.isActive() && freecam.isPlayerMode()) {
                        continue;
                    }
                }
                if (entity instanceof EntityFakePlayer) {
                    if (!players.get()) continue;
                    if (!freecam.isActive() || !freecam.isPlayerMode()) {
                        continue;
                    }
                } else {
                    if (!CombatUtils.checkEntity(entity, players.get(), hostile.get(), passive.get(), other.get())) continue;
                }

                renderLiving((EntityLivingBase) entity, event.partialTicks);
            }
            else if (entity instanceof EntityItem) {
                if (!items.get()) continue;

                renderItem((EntityItem) entity, event.partialTicks);
            }
        }
    }

    @Handler
    private void onRenderLiving(RenderLivingEvent.Specials.Pre event) {
        if (players.get() && event.entity instanceof EntityPlayer) {
            event.setCanceled(true); // Prevent original nametag rendering
        }
    }

    public void renderLiving(EntityLivingBase passedEntity, float partialTicks) {
        if(passedEntity.riddenByEntity != null) {
            return;
        }

        EntityLivingBase entity = passedEntity;
        while(entity.ridingEntity instanceof EntityLivingBase)
            entity = (EntityLivingBase) entity.ridingEntity;


        float pastTranslate = 0F;
        while(true) {
            processing: {

                double x = WorldUtils.getX(passedEntity, partialTicks);
                double y = WorldUtils.getY(passedEntity, partialTicks);
                double z = WorldUtils.getZ(passedEntity, partialTicks);

                if (entity == mc.thePlayer || entity instanceof EntityFakePlayer) {
                    y -= 1.62;
                }

                double distance = passedEntity.getDistanceToEntity(mc.renderViewEntity);
                double factor = MathHelper.clamp_double(scale.get() * distance / 10, scale.get(), maxSize.get());
                float scale = (float) (0.026666672F * factor);
                float maxHealth = entity.getMaxHealth();
                float health = Math.min(maxHealth, entity.getHealth());

                if(maxHealth <= 0)
                    break processing;

                float percent = (int) ((health / maxHealth) * 100F);

                GL11.glPushMatrix();
                GL11.glTranslatef((float) (x - RenderManager.renderPosX), (float) (y - RenderManager.renderPosY + passedEntity.height + 0.6), (float) (z - RenderManager.renderPosZ));
                GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
                GL11.glScalef(-scale, -scale, scale);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDepthMask(false);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                Tessellator tessellator = Tessellator.instance;

                float padding = 2;
                int bgHeight = 6;
                int barHeight = 4;
                float size = 25;

                ItemStack stack = entity.getHeldItem();

                float hue = Math.max(0F, (health / maxHealth) / 3F - 0.07F);
                Color color = Color.getHSBColor(hue, 1F, 1F);
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();

                GL11.glTranslatef(0F, pastTranslate, 0F);

                float s = 0.5F;
                String name = StatCollector.translateToLocal("entity." + EntityList.getEntityString(entity) + ".name");
                if(entity instanceof EntityLiving && ((EntityLiving) entity).hasCustomNameTag())
                    name = EnumChatFormatting.ITALIC + ((EntityLiving) entity).getCustomNameTag();
                float namel = mc.fontRenderer.getStringWidth(name) * s;
                if(namel + 20 > size * 2)
                    size = namel / 2F + 10F;
                if (entity instanceof EntityPlayer) {
                    name = ((EntityPlayer) entity).getDisplayName();
                    size = mc.fontRenderer.getStringWidth(name) * s / 2 + 10;
                }
                float healthSize = size * (health / maxHealth);

                // Background
                tessellator.startDrawingQuads();
                tessellator.setColorRGBA(0, 0, 0, 64);
                tessellator.addVertex(-size - padding, -bgHeight, 0.0D);
                tessellator.addVertex(-size - padding, barHeight + padding, 0.0D);
                tessellator.addVertex(size + padding, barHeight + padding, 0.0D);
                tessellator.addVertex(size + padding, -bgHeight, 0.0D);
                tessellator.draw();

                // Gray Space
                tessellator.startDrawingQuads();
                tessellator.setColorRGBA(127, 127, 127, 127);
                tessellator.addVertex(-size, 0, 0.0D);
                tessellator.addVertex(-size, barHeight, 0.0D);
                tessellator.addVertex(size, barHeight, 0.0D);
                tessellator.addVertex(size, 0, 0.0D);
                tessellator.draw();

                // Health Bar
                tessellator.startDrawingQuads();
                tessellator.setColorRGBA(r, g, b, 127);
                tessellator.addVertex(-size, 0, 0.0D);
                tessellator.addVertex(-size, barHeight, 0.0D);
                tessellator.addVertex(healthSize * 2 - size, barHeight, 0.0D);
                tessellator.addVertex(healthSize * 2 - size, 0, 0.0D);
                tessellator.draw();

                GL11.glEnable(GL11.GL_TEXTURE_2D);

                GL11.glPushMatrix();
                GL11.glTranslatef(-size, -4.5F, 0F);
                GL11.glScalef(s, s, s);
                mc.fontRenderer.drawString(name, 0, 0, 0xFFFFFF);

                GL11.glPushMatrix();
                float s1 = 0.75F;
                GL11.glScalef(s1, s1, s1);

                int h = 14;
                String maxHpStr = EnumChatFormatting.BOLD + "" + Math.round(maxHealth * 100.0) / 100.0;
                String hpStr = "" + Math.round(health * 100.0) / 100.0;
                String percStr = (int) percent + "%";

                if(maxHpStr.endsWith(".0"))
                    maxHpStr = maxHpStr.substring(0, maxHpStr.length() - 2);
                if(hpStr.endsWith(".0"))
                    hpStr = hpStr.substring(0, hpStr.length() - 2);

                mc.fontRenderer.drawString(hpStr, 2, h, 0xFFFFFF);
                mc.fontRenderer.drawString(maxHpStr, (int) (size / (s * s1) * 2) - 2 - mc.fontRenderer.getStringWidth(maxHpStr), h, 0xFFFFFF);
                mc.fontRenderer.drawString(percStr, (int) (size / (s * s1)) - mc.fontRenderer.getStringWidth(percStr) / 2, h, 0xFFFFFFFF);
                GL11.glPopMatrix();

                GL11.glColor4f(1F, 1F, 1F, 1F);
                int off = 0;

                s1 = 0.5F;
                GL11.glScalef(s1, s1, s1);
                GL11.glTranslatef(size / (s * s1) * 2 - 16, 0F, 0F);
                if(stack != null) {
                    renderIcon(stack, off, 0, 16);
                    off -= 16;
                }


                for (int i = 1; i <= 4; i++) {
                    stack = entity.getEquipmentInSlot(i);
                    if(stack != null) {
                        renderIcon(stack, off, 0, 16);
                        off -= 16;
                    }
                }

                GL11.glPopMatrix();

                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glDepthMask(true);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glPopMatrix();

                pastTranslate = -(bgHeight + barHeight + padding);
            }

            Entity riddenBy = entity.riddenByEntity;
            if(riddenBy instanceof EntityLivingBase)
                entity = (EntityLivingBase) riddenBy;
            else return;
        }
    }

    public void renderItem(EntityItem passedEntity, float partialTicks) {
        if(passedEntity.riddenByEntity != null) {
            return;
        }

        float pastTranslate = 0F;

        double x = passedEntity.lastTickPosX + (passedEntity.posX - passedEntity.lastTickPosX) * partialTicks;
        double y = passedEntity.lastTickPosY + (passedEntity.posY - passedEntity.lastTickPosY) * partialTicks - 0.2;
        double z = passedEntity.lastTickPosZ + (passedEntity.posZ - passedEntity.lastTickPosZ) * partialTicks;

        double distance = passedEntity.getDistanceToEntity(mc.thePlayer);
        double factor = MathHelper.clamp_double(scale.get() * distance / 10, scale.get(), maxSize.get());
        float scale = (float) (0.026666672F * factor);

        GL11.glPushMatrix();
        GL11.glTranslatef((float) (x - RenderManager.renderPosX), (float) (y - RenderManager.renderPosY + passedEntity.height + 0.6), (float) (z - RenderManager.renderPosZ));
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-scale, -scale, scale);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Tessellator tessellator = Tessellator.instance;

        float padding = 2;
        int bgHeight = 6;
        int barHeight = 0;
        float size = 10;

        ItemStack stack = passedEntity.getEntityItem();

        GL11.glTranslatef(0F, pastTranslate, 0F);

        float s = 0.5F;

        int itemId = Item.getIdFromItem(passedEntity.getEntityItem().getItem());
        String name = new ItemStack(Item.getItemById(itemId), 0, passedEntity.getEntityItem().getItemDamage()).getDisplayName();
        String label = name + " x" + passedEntity.getEntityItem().stackSize;

        float namel = mc.fontRenderer.getStringWidth(label) * s;
        if (namel + 20 > size * 2)
            size = namel / 2F + 4F;


        // Background
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(0, 0, 0, 64);
        tessellator.addVertex(-size - padding, -bgHeight, 0.0D);
        tessellator.addVertex(-size - padding, barHeight + padding - 1, 0.0D);
        tessellator.addVertex(size + padding, barHeight + padding - 1, 0.0D);
        tessellator.addVertex(size + padding, -bgHeight, 0.0D);
        tessellator.draw();

        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glPushMatrix();
        GL11.glTranslatef(-size, -4.5F, 0F);
        GL11.glScalef(s, s, s);
        mc.fontRenderer.drawString(label, 0, 0, 0xFFFFFF);

        GL11.glPushMatrix();
        float s1 = 0.75F;
        GL11.glScalef(s1, s1, s1);

        GL11.glPopMatrix();

        GL11.glColor4f(1F, 1F, 1F, 1F);

        s1 = 0.5F;
        GL11.glScalef(s1, s1, s1);
        GL11.glTranslatef(size / (s * s1) * 2 - 16, 0F, 0F);
        renderIcon(stack, -4, -4, 24);

        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();

    }

    private void renderIcon(ItemStack stack, int x, int y, int size) {
        if (stack.getItem() instanceof ItemBlock) {
            mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        } else {
            mc.renderEngine.bindTexture(TextureMap.locationItemsTexture);
        }
        RenderItem.getInstance().renderIcon(x, y, stack.getIconIndex(), size, size);
    }
}
