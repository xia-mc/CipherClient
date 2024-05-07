/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.transform.transformers;

import fr.crazycat256.cipherclient.events.EventManager;
import fr.crazycat256.cipherclient.events.custom.MouseOverEvent;
import fr.crazycat256.cipherclient.transform.Transform;
import fr.crazycat256.cipherclient.transform.Transformer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.List;

import static fr.crazycat256.cipherclient.CipherClient.mc;

public class EntityRendererTransformer extends Transformer {

    public EntityRendererTransformer() {
        super(EntityRenderer.class);
    }

    /**
     * Replace the method's body by a call to {@link EntityRendererTransformer#getMouseOverTransformed(float)}
     */
    @Transform(methodName = "getMouseOver", args = {float.class})
    private void getMouseOver(MethodNode mn) {
        mn.instructions.clear();
        mn.instructions.add(new VarInsnNode(FLOAD, 1));
        mn.instructions.add(new MethodInsnNode(INVOKESTATIC, selfPath, "getMouseOverTransformed", "(F)V", false));
        mn.instructions.add(new InsnNode(RETURN));
    }


    /**
     * Original method: {@link net.minecraft.client.renderer.EntityRenderer#getMouseOver(float)} <br>
     * Changes: <br>
     * - Added MouseOverEvent to allow for custom reach distance and hitbox expansion <br>
     * - Some minor changes to make the event work, this should not affect the original functionality
     */
    @SuppressWarnings("all")
    public static void getMouseOverTransformed(float p_78473_1_)
    {
        if (mc.thePlayer != null && mc.renderViewEntity != null)
        {
            if (mc.theWorld != null)
            {
                mc.pointedEntity = null;
                double d0 = (double)mc.playerController.getBlockReachDistance();
                //mc.objectMouseOver = mc.renderViewEntity.rayTrace(d0, p_78473_1_);
                double d1 = d0;
                Vec3 vec3 = mc.thePlayer.getPosition(p_78473_1_);

                if (mc.playerController.extendedReach())
                {
                    d0 = 6.0D;
                    d1 = 6.0D;
                }
                else
                {
                    if (d0 > 3.0D)
                    {
                        d1 = 3.0D;
                    }

                    d0 = d1;
                }

                MouseOverEvent event = new MouseOverEvent(mc.playerController.getBlockReachDistance(), d1);
                EventManager.postEvent(event);

                d0 = d1 = event.entityReach;
                mc.objectMouseOver = mc.thePlayer.rayTrace(event.blockReach, p_78473_1_);

                if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.MISS)
                {
                    d1 = mc.objectMouseOver.hitVec.distanceTo(vec3);
                }

                Vec3 vec31 = mc.thePlayer.getLook(p_78473_1_);
                Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
                Entity pointedEntity = null;
                Vec3 vec33 = null;
                float f1 = 1.0F;
                List list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.renderViewEntity, mc.thePlayer.boundingBox.addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand((double)f1, (double)f1, (double)f1));
                double d2 = d1;

                for (int i = 0; i < list.size(); ++i)
                {
                    Entity entity = (Entity)list.get(i);

                    if (entity == mc.thePlayer) {
                        continue;
                    }

                    if (entity.canBeCollidedWith())
                    {
                        float f2 = entity.getCollisionBorderSize();
                        AxisAlignedBB axisalignedbb = entity.boundingBox.expand((double)f2, (double)f2, (double)f2);
                        axisalignedbb = axisalignedbb.expand(event.hitboxExpand, event.hitboxExpand, event.hitboxExpand);
                        MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                        if (axisalignedbb.isVecInside(vec3))
                        {
                            if (0.0D < d2 || d2 == 0.0D)
                            {
                                pointedEntity = entity;
                                vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                                d2 = 0.0D;
                            }
                        }
                        else if (movingobjectposition != null)
                        {
                            double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                            if (d3 < d2 || d2 == 0.0D)
                            {
                                if (entity == mc.thePlayer.ridingEntity && !entity.canRiderInteract())
                                {
                                    if (d2 == 0.0D)
                                    {
                                        pointedEntity = entity;
                                        vec33 = movingobjectposition.hitVec;
                                    }
                                }
                                else
                                {
                                    pointedEntity = entity;
                                    vec33 = movingobjectposition.hitVec;
                                    d2 = d3;
                                }
                            }
                        }
                    }
                }

                if (pointedEntity != null && (d2 < d1 || mc.objectMouseOver == null))
                {
                    mc.objectMouseOver = new MovingObjectPosition(pointedEntity, vec33);

                    if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame)
                    {
                        mc.pointedEntity = pointedEntity;
                    }
                }
            }
        }
    }
}
