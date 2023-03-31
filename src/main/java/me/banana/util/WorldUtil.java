package me.banana.util;

import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.world.CardinalDirection;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static meteordevelopment.meteorclient.utils.Utils.vec3d;
import static meteordevelopment.meteorclient.utils.world.BlockUtils.getPlaceSide;

public class WorldUtil {

    public static boolean doesBoxTouchBlock(Box box, Block block) {
        for (int x = (int) Math.floor(box.minX); x < Math.ceil(box.maxX); x++) {
            for (int y = (int) Math.floor(box.minY); y < Math.ceil(box.maxY); y++) {
                for (int z = (int) Math.floor(box.minZ); z < Math.ceil(box.maxZ); z++) {
                    if (mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() == block) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static BlockState getState(BlockPos p) {return mc.world.getBlockState(p);}
    public static boolean isReplacable(BlockPos pos) {return getState(pos).getMaterial().isReplaceable();}
    public static boolean isSolid(BlockPos pos) {return getState(pos).isSolidBlock(mc.world, pos);}

    public static boolean canPlace(BlockPos pos) {
        if (pos == null) return false;
        if (isSolid(pos) || !World.isValid(pos) || !isReplacable(pos)) return false;
        if (!mc.world.canPlace(mc.world.getBlockState(pos), pos, ShapeContext.absent())) return false;
        return mc.world.getBlockState(pos).isAir() || mc.world.getBlockState(pos).getFluidState().getFluid() instanceof FlowableFluid;
    }

    public static Item getItemFromSlot(Integer slot) {
        if (slot == -1) return null;
        if (slot == 45) return mc.player.getOffHandStack().getItem();
        return mc.player.getInventory().getStack(slot).getItem();
    }

    public static Item getMainHandItem() { return mc.player.getInventory().getMainHandStack().getItem(); }

    public static boolean isHolding(Item item) {return getMainHandItem().equals(item);}
    public static boolean isHolding(FindItemResult itemResult) {return isHolding(getItemFromSlot(itemResult.slot()));}
    public static int lastSlot = -1;
    public static void setSlot(int slot, boolean packet) {
        if (slot < 0) return;
        lastSlot = mc.player.getInventory().selectedSlot;
        if (packet) {
            updateSlot(slot);
        } else {
            InvUtils.swap(slot, false);
        }
    }
    public static void sendPacket(Packet packet) {
        if (packet == null) return;
        Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(packet);
    }
    public static void updateSlot(int slot) {
        if (slot == -1) return;
        sendPacket(new UpdateSelectedSlotC2SPacket(slot));
    }
    public static void place(BlockPos pos, FindItemResult item, boolean rotate, boolean packet) {
        boolean swap = false;
        if (item == null || !item.found() || !item.isHotbar() || !WorldUtil.canPlace(pos)) return;
        if (!isHolding(item)) {
            setSlot(item.slot() , false);
            swap = true;
        }
        Direction side = getPlaceSide(pos);
        if (side == null) sendInteract(item.getHand(), item, new BlockHitResult(vec3d(pos), Direction.UP, pos, false), rotate, false);
        else sendInteract(item.getHand(), item, new BlockHitResult(vec3d(pos).add((double) side.getOffsetX() * 0.5D, (double) side.getOffsetY() * 0.5D, (double) side.getOffsetZ() * 0.5D), side, pos.offset(side.getOpposite()), false), rotate, packet);
        if (swap) swapBack();
    }
    public static boolean isBlastRes(BlockPos pos) {return mc.world.getBlockState(pos).getBlock().getBlastResistance() >= 600;}
    public static boolean isInHole(PlayerEntity p) {
        BlockPos center = p.getBlockPos();
        for (CardinalDirection cd : CardinalDirection.values()) if (!isBlastRes(center.offset(cd.toDirection()))) return false;
        return true;
    }
    public static void sendInteract(Hand hand, FindItemResult item, BlockHitResult hitResult, boolean rotate, boolean packet) {
        if (hand == null || item == null || hitResult == null || !item.found() || pendingPlaces.contains(hitResult)) return;
        if (hand == Hand.MAIN_HAND && !isHolding(item)) setSlot(item.slot(), false);
        BlockPos pos = hitResult.getBlockPos();
        if (rotate) {
            if (isInHole(mc.player)) rotate(pos, () -> sendInteract(hand, hitResult, packet));
            else Rotations.rotate(Rotations.getYaw(pos), Rotations.getPitch(pos), () -> sendInteract(hand, hitResult, packet));
        } else sendInteract(hand, hitResult, packet);
    }

    public static ArrayList<BlockHitResult> pendingPlaces = new ArrayList<>();

    public static void rotate(BlockPos pos, Runnable task) {
        if (pos == null) return;
        sendPacket(new PlayerMoveC2SPacket.LookAndOnGround((float) Rotations.getYaw(pos), (float) Rotations.getPitch(pos), mc.player.isOnGround()));
        task.run();
    }

    public static ExecutorService modules = Executors.newFixedThreadPool(10);

    public static void shutdown() {
        modules.shutdown();
    }

    public static void sendInteract(Hand hand, BlockHitResult result, boolean packet) {
        modules.execute(() -> {
            pendingPlaces.add(result);
            try {Thread.sleep(60);} catch (Exception ignored) {}
            pendingPlaces.remove(result);
        });
        if (packet) { // "packet placing" (sending interaction + swing packet directly)
            sendPacket(new PlayerInteractBlockC2SPacket(hand, result, 0));
            swingHand(hand == Hand.OFF_HAND);
        } else { // client placing
            mc.interactionManager.interactBlock(mc.player, hand, result);
            mc.player.swingHand(hand);
        }
    }

    public static void swingHand(boolean offhand) {
        if (offhand) {
            sendPacket(new HandSwingC2SPacket(Hand.OFF_HAND));
        } else {
            sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
        }
    }

    public static void swapBack() {
        setSlot(lastSlot, false);
    }

    public static double distanceBetween(BlockPos pos1, BlockPos pos2) {
        double d = pos1.getX() - pos2.getX();
        double e = pos1.getY() - pos2.getY();
        double f = pos1.getZ() - pos2.getZ();
        return MathHelper.sqrt((float) (d * d + e * e + f * f));
    }

    public static List<BlockPos> getSphere(BlockPos centerPos, int radius, int height) {
        ArrayList<BlockPos> blocks = new ArrayList<>();
        for (int i = centerPos.getX() - radius; i < centerPos.getX() + radius; i++) {
            for (int j = centerPos.getY() - height; j < centerPos.getY() + height; j++) {
                for (int k = centerPos.getZ() - radius; k < centerPos.getZ() + radius; k++) {
                    BlockPos pos = new BlockPos(i, j, k);
                    if (distanceBetween(centerPos, pos) <= radius && !blocks.contains(pos)) blocks.add(pos);
                }
            }
        }
        return blocks;
    }

    public static BlockPos roundBlockPos(Vec3d vec) {
        return new BlockPos(vec.x, (int) Math.round(vec.y), vec.z);
    }
}
