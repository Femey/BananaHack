package me.banana.util;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;

public class EntityUtil {

    public static boolean isWebbed(PlayerEntity targetEntity) {
        return WorldUtil.doesBoxTouchBlock(targetEntity.getBoundingBox(), Blocks.COBWEB);
    }

}
