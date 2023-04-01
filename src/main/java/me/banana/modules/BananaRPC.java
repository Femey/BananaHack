package me.banana.modules;

import club.minnced.discord.rpc.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ClientModInitializer;

public class BananaRPC implements ClientModInitializer {

    private static final String CLIENT_ID = "Your Discord Application Client ID Here";
    private static final String LARGE_IMAGE_KEY = "your_large_image_key";
    private static final String LARGE_IMAGE_TEXT = "Your Large Image Text";
    private static final String SMALL_IMAGE_KEY = "your_small_image_key";
    private static final String SMALL_IMAGE_TEXT = "Your Small Image Text";

    @Override
    public void onInitializeClient() {
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        DiscordRPC.INSTANCE.Discord_Initialize(CLIENT_ID, handlers, true, "");

        DiscordRichPresence discordPresence = new DiscordRichPresence();
        discordPresence.largeImageKey = LARGE_IMAGE_KEY;
        discordPresence.largeImageText = LARGE_IMAGE_TEXT;
        discordPresence.smallImageKey = SMALL_IMAGE_KEY;
        discordPresence.smallImageText = SMALL_IMAGE_TEXT;

        DiscordRPC.INSTANCE.Discord_UpdatePresence(discordPresence);

        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                DiscordRPC.INSTANCE.Discord_RunCallbacks();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {}
            }
        }, "RPC-Callback-Handler").start();
    }
}
