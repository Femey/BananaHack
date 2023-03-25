package me.banana.modules;

import me.banana.BananaHack;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public class WobblyAnchor extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Integer> pitch = sgGeneral.add(new IntSetting.Builder()
        .name("Pitch")
        .description("how far u gotta look down")
        .defaultValue(75)
        .min(1)
        .max(90)
        .sliderMax(90)
        .build()
    );

    private final Setting<Boolean> pull = sgGeneral.add(new BoolSetting.Builder()
        .name("Pull")
        .description("pulls you into the hole")
        .defaultValue(false)
        .build()
    );

    private final ArrayList<BlockPos> holes = new ArrayList<BlockPos>();
    int holeblocks;
    public static boolean AnchorING;

    public WobblyAnchor() {
        super(BananaHack.BANANAHACK, "WobblyAnchor", "Anchor that wobbly used (aka best anchor)");
    }

    public boolean isBlockHole(BlockPos blockpos) {
        holeblocks = 0;
        if (mc.world.getBlockState(blockpos.add(0, 3, 0)).getBlock() == Blocks.AIR) ++holeblocks;

        if (mc.world.getBlockState(blockpos.add(0, 2, 0)).getBlock() == Blocks.AIR) ++holeblocks;

        if (mc.world.getBlockState(blockpos.add(0, 1, 0)).getBlock() == Blocks.AIR) ++holeblocks;

        if (mc.world.getBlockState(blockpos.add(0, 0, 0)).getBlock() == Blocks.AIR) ++holeblocks;

        if (mc.world.getBlockState(blockpos.add(0, -1, 0)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(0, -1, 0)).getBlock() == Blocks.BEDROCK) ++holeblocks;

        if (mc.world.getBlockState(blockpos.add(1, 0, 0)).getBlock() == Blocks.OBSIDIAN ||mc.world.getBlockState(blockpos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK) ++holeblocks;

        if (mc.world.getBlockState(blockpos.add(-1, 0, 0)).getBlock() == Blocks.OBSIDIAN ||mc.world.getBlockState(blockpos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK) ++holeblocks;

        if (mc.world.getBlockState(blockpos.add(0, 0, 1)).getBlock() == Blocks.OBSIDIAN ||mc.world.getBlockState(blockpos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK) ++holeblocks;

        if (mc.world.getBlockState(blockpos.add(0, 0, -1)).getBlock() == Blocks.OBSIDIAN ||mc.world.getBlockState(blockpos.add(0, 0, -1)).getBlock() == Blocks.BEDROCK) ++holeblocks;

        if (holeblocks >= 9) return true;
        else return false;
    }
    private Vec3d Center = Vec3d.ZERO;

    public Vec3d GetCenter(double posX, double posY, double posZ) {
        double x = Math.floor(posX) + 0.5D;
        double y = Math.floor(posY);
        double z = Math.floor(posZ) + 0.5D ;

        return new Vec3d(x, y, z);
    }

    @EventHandler
    private void onPostTick(TickEvent.Post event) {
        if (mc.world == null) {
            return;
        }
        if (mc.player.getY() < 0) {
            return;
        }
        if (mc.player.getPitch() >= pitch.get()) {
            if (isBlockHole(getPlayerPos().down(1)) || isBlockHole(getPlayerPos().down(2)) ||
                isBlockHole(getPlayerPos().down(3)) || isBlockHole(getPlayerPos().down(4))) {
                AnchorING = true;

                if (!pull.get()) {
                    ((IVec3d) mc.player.getVelocity()).set(0, mc.player.getVelocity().y, 0);
                } else {
                    Center = GetCenter(mc.player.getX(), mc.player.getY(), mc.player.getZ());
                    double XDiff = Math.abs(Center.x - mc.player.getX());
                    double ZDiff = Math.abs(Center.z - mc.player.getZ());

                    if (XDiff <= 0.1 && ZDiff <= 0.1) {
                        Center = Vec3d.ZERO;
                    }
                    else {
                        double MotionX = Center.x-mc.player.getX();
                        double MotionZ = Center.z-mc.player.getZ();

                        ((IVec3d) mc.player.getVelocity()).set(MotionX/2, mc.player.getVelocity().y, MotionZ/2);
                    }
                }
            } else AnchorING = false;
        }
    }

    public void onDeactivate() {
        AnchorING = false;
        holeblocks = 0;
    }

    public BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.player.getX()), Math.floor(mc.player.getY()), Math.floor(mc.player.getZ()));
    }
}
