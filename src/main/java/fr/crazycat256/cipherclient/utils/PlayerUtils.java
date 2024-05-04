/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.utils;

import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

import java.util.List;

import static fr.crazycat256.cipherclient.CipherClient.mc;
import static java.lang.Math.floor;

public class PlayerUtils {

    /**
     * Set the player's position and previous position
     * @param player the player to set the position of
     * @param pos the position to set the player to
     */
    public static void setPos(EntityLivingBase player, Vec3 pos) {
        player.posX = player.prevPosX = player.lastTickPosX = pos.xCoord;
        player.posY = player.prevPosY = player.lastTickPosY = pos.yCoord;
        player.posZ = player.prevPosZ = player.lastTickPosZ = pos.zCoord;
    }

    /**
     * Set the player's rotation and previous rotation
     * @param player the player to set the rotation of
     * @param yaw the yaw to set the player to
     * @param pitch the pitch to set the player to
     */
    public static void setLook(EntityLivingBase player, float yaw, float pitch) {
        player.rotationYaw = player.prevRotationYaw  = yaw;
        player.rotationPitch = player.prevRotationPitch = pitch;
    }

    /**
     * Send a position packet to the server with the given coordinates
     */
    public static void sendPositionPacket(Vec3 pos, boolean onGround) {
        sendPositionPacket(pos.xCoord, pos.yCoord, pos.zCoord, onGround);
    }

    /**
     * Send a position packet to the server with the given coordinates
     */
    public static void sendPositionPacket(double x, double y, double z, boolean onGround) {
        double stance = mc.thePlayer.posY - mc.thePlayer.boundingBox.minY;
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y - stance, y, z, onGround));
    }

    /**
     * Get the movement vector of the player based on the keys pressed
     * @return the movement vector of the player
     */
    public static Vec3 getMovementVec3() {
        return getMovementVec3(mc.thePlayer);
    }

    /**
     * Get the movement vector of the player based on the keys pressed
     * @param entity the entity to get the movement vector of. This is usually the player or a fake player
     * @return the movement vector of the player
     */
    public static Vec3 getMovementVec3(EntityLivingBase entity) {
        Vec3 vec = MathUtils.vec3FromPolar(0, entity.rotationYaw).normalize();
        Vec3 move = Vec3.createVectorHelper(0, 0, 0);

        if (mc.gameSettings.keyBindForward.getIsKeyPressed())
            move = move.addVector(vec.xCoord, 0, vec.zCoord);
        if (mc.gameSettings.keyBindBack.getIsKeyPressed())
            move = move.addVector(-vec.xCoord, 0, -vec.zCoord);
        if (mc.gameSettings.keyBindLeft.getIsKeyPressed())
            move = move.addVector(vec.zCoord, 0, -vec.xCoord);
        if (mc.gameSettings.keyBindRight.getIsKeyPressed())
            move = move.addVector(-vec.zCoord, 0, vec.xCoord);
        if (mc.gameSettings.keyBindJump.getIsKeyPressed())
            move = move.addVector(0, 1, 0);
        if (mc.gameSettings.keyBindSneak.getIsKeyPressed())
            move = move.addVector(0, -1, 0);

        return move;
    }

    /**
     * Check if the player can stand at the given position without being obstructed
     * @param x x coordinate of the position
     * @param y y coordinate of the position
     * @param z z coordinate of the position
     * @return true if the position is obstructed, false otherwise
     */
    public static boolean isPosObstructed(double x, double y, double z) {
        List<AxisAlignedBB> collidingBoxes = getCollidingBoxes(x, y, z);

        // We cannot just check if collidingBoxes is empty because the Jesus module can add hitboxes for liquid blocks
        for (AxisAlignedBB boundingBox : collidingBoxes) {
            if (boundingBox == null) continue;
            Vec3i min = new Vec3i(floor(boundingBox.minX), floor(boundingBox.minY), floor(boundingBox.minZ));
            Block block = mc.theWorld.getBlock(min.x, min.y, min.z);
            if (block.isBlockSolid(mc.theWorld, min.x, min.y, min.z, 1)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the list of colliding bounding boxes at the given position
     * @param x x coordinate of the position
     * @param y y coordinate of the position
     * @param z z coordinate of the position
     * @return the list of colliding bounding boxes at the given position
     */
    @SuppressWarnings("unchecked")
    public static List<AxisAlignedBB> getCollidingBoxes(double x, double y, double z) {
        double stance = mc.thePlayer.posY - mc.thePlayer.boundingBox.minY;
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x - 0.3, y, z - 0.3, x + 0.3, y + stance, z + 0.3);
        return (List<AxisAlignedBB>) mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, box.contract(0.01, 0.01, 0.01));
    }

    /**
     * Check if the player can stand at the given position without being obstructed
     * @param pos the position to check
     * @return true if the position is obstructed, false otherwise
     */
    public static boolean isPosObstructed(Vec3 pos) {
        return isPosObstructed(pos.xCoord, pos.yCoord, pos.zCoord);
    }

    /**
     * Check if any part of the player's hitbox is inside a wall
     * @return true if the player is inside a wall, false otherwise
     */
    public static boolean isInsideWall() {
        return isPosObstructed(getFootPos());
    }

    /**
     * Check if the server will accept a teleport packet from initialPos to finalPos
     * @param initialPos the initial position of the player
     * @param finalPos the final position of the player
     * @return true if the teleport is valid, false otherwise
     */
    public static boolean isTPValid(Vec3 initialPos, Vec3 finalPos) {
        return isTPValid(initialPos, finalPos, mc.thePlayer.onGround);
    }

    /**
     * Check if the server will accept a teleport packet from initialPos to finalPos
     * @param initialPos the initial position of the player
     * @param finalPos the final position of the player
     * @param onGround if the player is on the ground
     * @return true if the teleport is valid, false otherwise
     */
    public static boolean isTPValid(Vec3 initialPos, Vec3 finalPos, boolean onGround) {

        if (isPosObstructed(finalPos) && !isPosObstructed(initialPos)) {
            return false;
        }

        EntityClientPlayerMP player = mc.thePlayer;

        double d1 = finalPos.xCoord;
        double d2 = finalPos.yCoord;
        double d3 = finalPos.zCoord;

        double d4 = d1 - player.posX;
        double d5 = d2 - player.posY;
        double d6 = d3 - player.posZ;

        EntityClientPlayerMP fakePlayer = new EntityClientPlayerMP(mc, mc.theWorld, mc.getSession(), mc.getNetHandler(), mc.thePlayer.getStatFileWriter());
        double stance = player.posY - player.boundingBox.minY;
        fakePlayer.setPosition(initialPos.xCoord, initialPos.yCoord + stance, initialPos.zCoord);
        fakePlayer.ySize = 0.0F;
        fakePlayer.setSneaking(false);
        fakePlayer.movementInput = player.movementInput;
        fakePlayer.onGround = onGround;
        fakePlayer.stepHeight = 0.5f;

        try {
            fakePlayer.isSneaking();
        } catch (Exception e) {
            return false;
        }
        try {
            fakePlayer.moveEntity(d4, d5, d6);
        } catch (Exception e) {
            return false;
        }
        fakePlayer.addMovementStat(d4, d5, d6);


        d4 = d1 - fakePlayer.posX;
        d5 = d2 - fakePlayer.posY;

        if (d5 > -0.5D || d5 < 0.5D) {
            d5 = 0.0D;
        }

        d6 = d3 - fakePlayer.posZ;
        double d10 = d4 * d4 + d5 * d5 + d6 * d6;

        return d10 <= 0.0625D;
    }

    public static Vec3 getFootPos() {
        return Vec3.createVectorHelper(mc.thePlayer.posX, mc.thePlayer.boundingBox.minY, mc.thePlayer.posZ);
    }


}
