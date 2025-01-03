package fr.crazycat256.cipherclient.systems.module.misc;

import fr.crazycat256.cipherclient.events.Handler;
import fr.crazycat256.cipherclient.gui.settings.BoolSetting;
import fr.crazycat256.cipherclient.gui.settings.KeySetting;
import fr.crazycat256.cipherclient.gui.settings.Setting;
import fr.crazycat256.cipherclient.systems.module.Category;
import fr.crazycat256.cipherclient.systems.module.Module;
import fr.crazycat256.cipherclient.utils.NBTUtils;
import fr.crazycat256.cipherclient.utils.Utils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class NBTViewer extends Module {

    public NBTViewer() {
        super("nbt-viewer", "Allows you to see NBT data of the entity/tile you are looking at", Category.MISC);
    }

    private final Setting<Boolean> tileEntities = addSetting(new BoolSetting.Builder()
        .name("tile-entities")
        .description("Render tile entities")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> entities = addSetting(new BoolSetting.Builder()
        .name("entities")
        .description("Render entities")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> color = addSetting(new BoolSetting.Builder()
        .name("color")
        .description("Colorize NBT data")
        .defaultValue(true)
        .build()
    );

    private final Setting<Integer> copyKey = addSetting(new KeySetting.Builder()
        .name("copy-key")
        .description("The key to copy the NBT data to the clipboard")
        .defaultValue(Keyboard.KEY_NONE)
        .build()
    );

    @Handler
    private void onGameOverlay(RenderGameOverlayEvent.Text event) {
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT) return;

        MovingObjectPosition mop = mc.objectMouseOver;

        NBTTagCompound tag = new NBTTagCompound();

        if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {

            if (!tileEntities.get()) return;
            TileEntity te = mc.theWorld.getTileEntity(mop.blockX, mop.blockY, mop.blockZ);
            if (te == null) return;
            te.writeToNBT(tag);

        } else if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {

            if (!entities.get()) return;
            mop.entityHit.writeToNBT(tag);

        } else {
            return;
        }

        String nbtString = NBTUtils.formatNBT(tag, 4, color.get());
        String[] lines = nbtString.split("\n");

        int width = 0;
        for (String line : lines) {
            width = Math.max(width, mc.fontRenderer.getStringWidth(line));
        }


        int height = lines.length * mc.fontRenderer.FONT_HEIGHT;

        double scale = 1;

        while (height * scale > event.resolution.getScaledHeight() - 35) {
            scale -= 0.01;
        }

        int x = (int) (((double) event.resolution.getScaledWidth() / 2 - (width * scale) / 2) / scale);
        int y = (int) (10 / scale);

        GL11.glPushMatrix();
        GL11.glScaled(scale, scale, 1);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(0, 0, 0, 0.75f);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(x - 2, y - 2);
        GL11.glVertex2f(x - 2, y + height + 2);
        GL11.glVertex2f(x + width + 2, y + height + 2);
        GL11.glVertex2f(x + width + 2, y - 2);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        for (int i = 0; i < lines.length; i++) {
            mc.fontRenderer.drawString(lines[i], x, y + i * mc.fontRenderer.FONT_HEIGHT, -1);
        }
        GL11.glPopMatrix();

        if (copyKey.get() != Keyboard.KEY_NONE && Keyboard.isKeyDown(copyKey.get())) {
            Utils.setClipboard(NBTUtils.formatNBT(tag, 4, false));
        }
    }
}
