package me.banana.util;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import static meteordevelopment.meteorclient.MeteorClient.mc;

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
}
