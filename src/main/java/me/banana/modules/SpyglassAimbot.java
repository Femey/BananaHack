package me.banana.modules;

import baritone.api.utils.RotationUtils;
import me.banana.BananaHack;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GoatHornItem;
import net.minecraft.util.math.Vec3d;

public class SpyglassAimbot extends Module {
    public SpyglassAimbot() {
        super(BananaHack.BANANAHACK, "SpyglassAimbot", "Looks at people when using a spy glass");
    }


    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> height = sgGeneral.add(new DoubleSetting.Builder()
        .name("Height")
        .description("how high u want the aimbot to look")
        .defaultValue(0)
        .min(0)
        .sliderMax(2)
        .build()
    );

    private PlayerEntity target;

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player.isUsingSpyglass()) {
            target = TargetUtils.getPlayerTarget(100000, SortPriority.ClosestAngle);
            if (target == null) {
                return;
            }

            Vec3d deez = new Vec3d(target.getX(), target.getY() + height.get(), target.getZ());

            mc.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, deez);
        }
    }
}
