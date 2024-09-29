/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.module.render.damagepopoff;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.awt.Color;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import static fr.crazycat256.cipherclient.CipherClient.mc;

/**
 * Copied from <a href="https://github.com/radioegor146/ehacks-pro/blob/master/src/main/java/ehacks/mod/util/damageindicator/Particle.java">ehacks-pro</a>
 */
@SideOnly(value = Side.CLIENT)
public class Particle extends EntityFX {

    public final boolean criticalhit = false;
    public int Damage;
    boolean heal = false;
    boolean grow = true;
    float ul;
    float ur;
    float vl;
    float vr;
    float locX;
    float locY;
    float locZ;
    float red;
    float green;
    float blue;
    float alpha;
    public boolean shouldOnTop = false;
    public static final boolean isOptifinePresent;

    public Particle(World par1World, double par2, double par4, double par6, double par8, double par10, double par12, int damage) {
        super(par1World, par2, par4, par6, par8, par10, par12);
        this.Damage = damage;
        this.setSize(0.2f, 0.2f);
        this.yOffset = this.height * 1.1f;
        this.setPosition(par2, par4, par6);
        this.motionX = par8;
        this.motionY = par10;
        this.motionZ = par12;
        float var15 = MathHelper.sqrt_double((this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ));
        this.motionX = this.motionX / var15 * 0.12;
        this.motionY = this.motionY / var15 * 0.12;
        this.motionZ = this.motionZ / var15 * 0.12;
        this.particleTextureJitterX = 1.5f;
        this.particleTextureJitterY = 1.5f;
        this.particleGravity = 0.8f;
        this.particleScale = 3f;
        this.particleMaxAge = 12;
        this.particleAge = 0;
        if (this.Damage < 0) {
            this.heal = true;
            this.Damage = Math.abs(this.Damage);
        }
        try {
            int baseColor = this.heal ? 65280 : 16755200;
            this.red = (baseColor >> 16 & 255) / 255.0f;
            this.green = (baseColor >> 8 & 255) / 255.0f;
            this.blue = (baseColor & 255) / 255.0f;
            this.alpha = 0.9947f;
            this.ul = (this.Damage - MathHelper.floor_float(this.Damage / 16.0f) * 16.0f) % 16.0f / 16.0f;
            this.ur = this.ul + 0.0624375f;
            this.vl = MathHelper.floor_float(this.Damage / 16.0f) * 16.0f / 16.0f / 16.0f;
            this.vr = this.vl + 0.0624375f;
        } catch (Throwable ex) {
            // empty catch block
        }
    }

    @SideOnly(value = Side.CLIENT)
    @Override
    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7) {
        this.rotationYaw = -mc.thePlayer.rotationYaw;
        this.rotationPitch = mc.thePlayer.rotationPitch;
        try {
            this.locX = (float) (this.prevPosX + (this.posX - this.prevPosX) * par2 - interpPosX);
            this.locY = (float) (this.prevPosY + (this.posY - this.prevPosY) * par2 - interpPosY);
            this.locZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * par2 - interpPosZ);
        } catch (Throwable ex) {
            // Empty catch block
        }
        GL11.glPushMatrix();
        if (this.shouldOnTop) {
            GL11.glDepthFunc(519);
        } else {
            GL11.glDepthFunc(515);
        }
        GL11.glTranslatef(this.locX, this.locY, this.locZ);
        GL11.glRotatef(this.rotationYaw, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(this.rotationPitch, 1.0f, 0.0f, 0.0f);
        GL11.glScalef(-1.0f, -1.0f, 1.0f);
        GL11.glScaled((this.particleScale * 0.008), (this.particleScale * 0.008), (this.particleScale * 0.008));
        if (this.criticalhit) {
            GL11.glScaled(0.5, 0.5, 0.5);
        }
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 0.003662109f);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(2896);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glEnable(3008);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (this.criticalhit) {
            String critical = "Critical!";
            mc.fontRenderer.drawString(critical, -MathHelper.floor_float((mc.fontRenderer.getStringWidth(critical) / 2.0f)) + 1, -MathHelper.floor_float((mc.fontRenderer.FONT_HEIGHT / 2.0f)) + 1, 0);
            mc.fontRenderer.drawString(critical, -MathHelper.floor_float((mc.fontRenderer.getStringWidth(critical) / 2.0f)), -MathHelper.floor_float((mc.fontRenderer.FONT_HEIGHT / 2.0f)), -7600622);
        } else {
            int color = this.heal ? 65280 : 16755200;
            Color c_Color = new Color(color);
            c_Color = new Color(c_Color.getRed() / 5.0f / 255.0f, c_Color.getGreen() / 5.0f / 255.0f, c_Color.getBlue() / 5.0f / 255.0f);
            mc.fontRenderer.drawString(String.valueOf(this.Damage), -MathHelper.floor_float((mc.fontRenderer.getStringWidth("" + this.Damage) / 2.0f)) + 1, -MathHelper.floor_float((mc.fontRenderer.FONT_HEIGHT / 2.0f)) + 1, c_Color.getRGB());
            mc.fontRenderer.drawString(String.valueOf(this.Damage), -MathHelper.floor_float((mc.fontRenderer.getStringWidth("" + this.Damage) / 2.0f)), -MathHelper.floor_float((mc.fontRenderer.FONT_HEIGHT / 2.0f)), color);
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDepthFunc(515);
        GL11.glPopMatrix();
        if (this.grow) {
            this.particleScale *= 1.08f;
            if (this.particleScale > 9.0) {
                this.grow = false;
            }
        } else {
            this.particleScale *= 0.96f;
        }
    }

    @Override
    public int getFXLayer() {
        return 3;
    }

    static {
        isOptifinePresent = false;
    }
}
