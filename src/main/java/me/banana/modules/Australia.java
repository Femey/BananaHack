package me.banana.modules;

import me.banana.BananaHack;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.gl.PostProcessShader;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class Australia extends Module {
    private PostProcessShader shader;
    private static final Identifier SHADER_LOCATION = new Identifier("shaders/post/flip.json");

    public Australia() {
        super(BananaHack.BANANATROLL, "Australia", "Makes you Australian");
    }

    @Override
    public void onEnable() {
        if (mc.getResourceManager() == null) return;

        shader = new PostProcessShader(mc.getResourceManager(), SHADER_LOCATION, mc.getFramebuffer(), mc.getFramebufferWidth(), mc.getFramebufferHeight());
        mc.getFramebuffer().Shader(shader);
        assert mc.player != null;
        mc.player.setFireTicks(1);
    }

    @Override
    public void onDisable() {
        if (shader != null) {
            shader.close();
            shader = null;
        }
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        if (shader != null) {
            shader.close();
            shader = new PostProcessShader(manager, SHADER_LOCATION, mc.getFramebuffer(), mc.getFramebufferWidth(), mc.getFramebufferHeight());
        }
    }

    @Override
    public void onUpdate() {
        if (!(mc.getCameraEntity() instanceof AbstractClientPlayerEntity)) return;

        if (shader != null) {
            shader.render(mc.getTickDelta());
        }
        assert mc.player != null;
        mc.player.setFireTicks(1);
    }
}
