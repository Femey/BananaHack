package me.banana.modules;

import com.google.common.eventbus.Subscribe;
import me.banana.BananaHack;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import meteordevelopment.meteorclient.events.entity.player.SendMovementPacketsEvent;
import net.minecraft.item.PotionItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

public class NoSlowPlus
    extends Module {

    public NoSlowPlus() {
        super(BananaHack.BANANAHACK, "NoSlow+", "only works with strafe and not sideways");
    }

    public static NoSlowPlus instance = new NoSlowPlus();

    public boolean isSneaking() {
        return sneaking;
    }

    boolean sneaking;

    public void onUpdate() {
        if(mc.world != null) {
            Item item = mc.player.getActiveItem().getItem();
            if (sneaking && ((!mc.player.isUsingItem() && item.isFood() || item instanceof PotionItem || item instanceof BowItem))) {
                sneaking = false;
            }
        }
    }

    @Subscribe
    public void onUseItem() {
        if (!sneaking) {
            sneaking = true;
        }
    }

}
