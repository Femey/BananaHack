package me.banana.modules;

import me.banana.BananaHack;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;


public class SprintPlus extends Module {
    public SprintPlus() {
        super(BananaHack.BANANAHACK, "Sprint+", "Better sprint");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if ((mc.options.forwardKey.isPressed() || mc.options.backKey.isPressed() || mc.options.leftKey.isPressed() || mc.options.rightKey.isPressed()) && !mc.player.isSneaking() && !mc.player.horizontalCollision && !((float)mc.player.getHungerManager().getFoodLevel() <= 6.0f)) {
            mc.player.setSprinting(true);
        }
    }
}
