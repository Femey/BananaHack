package me.banana.modules;

import me.banana.BananaHack;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GoatHornItem;
import net.minecraft.util.Hand;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class AutoHorn extends Module {
    public AutoHorn() {
        super(BananaHack.BANANAHACK, "AutoHorn", "blows a horn automatically when someone gets into render distance");
    }

    @EventHandler
    private void onEntityAdded(EntityAddedEvent event) {
        if (!(event.entity instanceof PlayerEntity)) return;
        int oldslot = mc.player.getInventory().selectedSlot;
        FindItemResult horn = InvUtils.findInHotbar(itemStack -> itemStack.getItem() instanceof GoatHornItem);
        if (!horn.found()) return;
        mc.player.getInventory().selectedSlot = horn.slot();
        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        mc.player.getInventory().selectedSlot = oldslot;
    }
}
