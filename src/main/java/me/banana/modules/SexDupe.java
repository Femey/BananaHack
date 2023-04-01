package me.banana.modules;

import me.banana.BananaHack;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.entity.ItemEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;

import java.util.Random;

public class SexDupe extends Module {
    public SexDupe() {
        super(BananaHack.BANANAHACK, "SexDupe", "Dupe Cum");
    }

    private final Random random = new Random();

    public void onActivate() {
        ClientPlayerEntity player = mc.player;
        ClientWorld world = mc.world;

        if (player == null || world == null) return;

        ItemStack itemStack = player.getMainHandStack();

        if (itemStack.isEmpty()) {
            setDisabledMessage("You need to hold an item in hand to dupe!");
            toggle();
            return;
        }

        int count = random.nextInt(31) + 1;

        for (int i = 0; i <= count; i++) {
            ItemEntity entityItem = player.dropItem(itemStack.copy(), false, true);
            if (entityItem != null) {
                world.spawnEntity(entityItem);
            }
        }

        int total = count * itemStack.getCount();
        ChatUtils.sendPlayerMsg("I just used BananaHack to dupe " + total + " " + itemStack.getName().getString() + " thanks to the developers!");
        toggle();
    }

    private void setDisabledMessage(String s) {
    }
}
