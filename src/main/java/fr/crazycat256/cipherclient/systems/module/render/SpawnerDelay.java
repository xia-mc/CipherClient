package fr.crazycat256.cipherclient.systems.module.render;

import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.utils.WorldUtils;
import fr.crazycat256.cipherclient.utils.font.Fonts;
import fr.crazycat256.cipherclient.utils.font.GlyphPageFontRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class SpawnerDelay extends Module {

    public SpawnerDelay() {
        super("spawner-delay", "Shows the delay of a spawner", Category.RENDER);
    }

    @Handler
    private void onRenderWorldLast(RenderWorldLastEvent event) {
        List<TileEntity> tileEntities = WorldUtils.getLoadedTileEntityList();

        for (TileEntity te : tileEntities) {
            if (!(te instanceof TileEntityMobSpawner)) continue;
            TileEntityMobSpawner spawner = (TileEntityMobSpawner) te;

            int delay = spawner.func_145881_a().spawnDelay;

            renderSpawner(spawner, delay);
        }
    }

    public void renderSpawner(TileEntityMobSpawner spawner, int delay) {

        GlyphPageFontRenderer font = Fonts.getFont(100);

        int color;
        if (delay == 20) {
            color = 0xC0C0C0;
        } else {
            color = 0xFF0000;
        }

        double x = spawner.xCoord + 0.5;
        double y = spawner.yCoord + 0.5;
        double z = spawner.zCoord + 0.5;

        double distance = spawner.getDistanceFrom(RenderManager.renderPosX, RenderManager.renderPosY, RenderManager.renderPosZ);
        double factor = MathHelper.clamp_double(distance / 10_000, 0.02, 0.1);

        GL11.glPushMatrix();
        GL11.glTranslatef((float) (x - RenderManager.renderPosX), (float) (y - RenderManager.renderPosY), (float) (z - RenderManager.renderPosZ));
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScaled(-factor, -factor, factor);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        float s = 0.5F;

        String label = String.valueOf(delay);
        float labelSize = font.getStringWidth(label) * s;

        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glPushMatrix();
        GL11.glScalef(s, s, s);
        GL11.glTranslatef(0, -font.getFontHeight(), 0);
        font.drawCenteredString(label, 0, 0, color);

        GL11.glPushMatrix();
        float s1 = 0.75F;
        GL11.glScalef(s1, s1, s1);

        GL11.glPopMatrix();

        GL11.glColor4f(1F, 1F, 1F, 1F);

        s1 = 0.5F;
        GL11.glScalef(s1, s1, s1);
        GL11.glTranslatef(labelSize / (s * s1) * 2 - 16, 0F, 0F);

        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }
}
