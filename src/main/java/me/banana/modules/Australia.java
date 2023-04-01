package me.banana.modules;

import me.banana.BananaHack;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.gl.PostProcessShader;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class Australia extends Module {
    private PostProcessShader shader;
    private static final Identifier SHADER_LOCATION = new Identifier("shaders/post/flip.json");

    private final Setting<Boolean> fire = new BoolSetting.Builder()
        .name("set-on-fire")
        .description("Sets you on fire.")
        .defaultValue(true)
        .build();

    public Australia() {
        super(BananaHack.BANANATROLL, "Australia", "Makes you Australian.");
    }

    @Override
    public void onActivate() {
        if (mc.getResourceManager() == null) return;

        shader = new PostProcessShader(mc.getResourceManager(), SHADER_LOCATION, mc.getFramebuffer(), mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight());
        mc.getFramebuffer().setShader(shader);
        if (fire.get()) {
            assert mc.player != null;
            mc.player.setOnFireFor(1);
        }
    }

    @Override
    public void onDeactivate() {
        if (shader != null) {
            shader.close();
            shader = null;
        }
    }

    @EventHandler
    private void onGameJoined(GameJoinedEvent event) {
        onActivate();
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) throws IOException {
        if (shader != null) {
            shader.close();
            shader = new PostProcessShader(manager, SHADER_LOCATION, mc.getFramebuffer(), mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight());
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!(mc.getCameraEntity() instanceof AbstractClientPlayerEntity)) return;

        if (shader != null) {
            shader.render(event.delta);
        }
        if (fire.get()) {
            assert mc.player != null;
            mc.player.setOnFireFor(1);
        }
    }
}
