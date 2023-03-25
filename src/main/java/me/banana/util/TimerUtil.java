package me.banana.util;

import meteordevelopment.meteorclient.utils.world.TickRate;

public class TimerUtil {

    public static double TPSSync(boolean TPSSync) {
        return TPSSync ? (TickRate.INSTANCE.getTickRate() / 20) : 1;
    }

}
