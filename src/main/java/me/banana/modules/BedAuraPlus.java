package me.banana.modules;

import me.banana.BananaHack;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.meteorclient.utils.world.CardinalDirection;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BedItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class BedAuraPlus extends Module {
    public BedAuraPlus() {
        super(BananaHack.BANANAHACK, "BedAura+", "Actually good bed aura");
    }


    private BlockPos placePos, breakPos, enemyPos;
    private PlayerEntity target;

    @EventHandler
    private void onTick(TickEvent.Post event) {
        target = TargetUtils.getPlayerTarget(12, SortPriority.LowestHealth);
        if (target == null) {
            placePos = null;
            breakPos = null;
            return;
        }
        enemyPos(target);
        placeBed(enemyPos);
        breakBed(breakPos);
    }

    private void enemyPos(PlayerEntity enemy){
        enemyPos = enemy.getBlockPos();
    }

    private void placeBed(BlockPos pos) {
        FindItemResult bed = InvUtils.findInHotbar(itemStack -> itemStack.getItem() instanceof BedItem);
        if (bed.getHand() == null) {
            return;
        }

        placePos = new BlockPos(pos.getX(), pos.getY(), pos.getZ());

        BlockUtils.place(placePos, bed, false, 0, true, true);
        breakPos = placePos;
    }

    private void breakBed(BlockPos pos){
        if (pos == null) return;
        if (!(mc.world.getBlockState(pos).getBlock() instanceof BedBlock)) return;

        boolean wasSneaking = mc.player.isSneaking();
        if (wasSneaking) mc.player.setSneaking(false);

        mc.interactionManager.interactBlock(mc.player, Hand.OFF_HAND, new BlockHitResult(Vec3d.ofCenter(pos), Direction.UP, pos, false));
 
        mc.player.setSneaking(wasSneaking);
    }


}
