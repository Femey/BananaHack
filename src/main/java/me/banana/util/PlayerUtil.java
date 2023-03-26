package me.banana.util;

import net.minecraft.entity.Entity;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class PlayerUtil {
    public static double distanceFromEye(Entity entity) {
        double feet = distanceFromEye(entity.getX(), entity.getY(), entity.getZ());
        double head = distanceFromEye(entity.getX(), entity.getY() + entity.getHeight(), entity.getZ());
        return Math.min(head, feet);
    }

    public static double distanceFromEye(double x, double y, double z) {
        double f = (mc.player.getX() - x);
        double g = (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()) - y);
        double h = (mc.player.getZ() - z);
        return Math.sqrt(f * f + g * g + h * h);
    }
}
