package me.banana.util;

import meteordevelopment.meteorclient.mixininterface.IExplosion;
import meteordevelopment.meteorclient.mixininterface.IRaycastContext;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.GameMode;
import net.minecraft.world.RaycastContext;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.mixininterface.IExplosion;
import meteordevelopment.meteorclient.mixininterface.IRaycastContext;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.PostInit;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.world.Dimension;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameMode;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.explosion.Explosion;

import java.util.Objects;

import static meteordevelopment.meteorclient.MeteorClient.mc;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class EntityUtil {

    private static final Vec3d vec3d = new Vec3d(0, 0, 0);
    private static Explosion explosion;
    private static RaycastContext raycastContext;

    @PostInit
    public static void init() {
        MeteorClient.EVENT_BUS.subscribe(EntityUtil.class);
    }

    @EventHandler
    private static void onGameJoined(GameJoinedEvent event) {
        explosion = new Explosion(mc.world, null, 0, 0, 0, 6, false, Explosion.DestructionType.DESTROY);
        raycastContext = new RaycastContext(null, null, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, mc.player);
    }

    public static BlockPos playerPos(PlayerEntity targetEntity) {
        return WorldUtil.roundBlockPos(targetEntity.getPos());
    }

    public static boolean isWebbed(PlayerEntity targetEntity) {
        return WorldUtil.doesBoxTouchBlock(targetEntity.getBoundingBox(), Blocks.COBWEB);
    }

    public static boolean isTopTrapped(PlayerEntity targetEntity, BlastResistantType type) {
        return isBlastResistant(playerPos(targetEntity).add(0, 2, 0), type);
    }

    public static boolean isBothTrapped(PlayerEntity targetEntity, BlastResistantType type) {
        return isTopTrapped(targetEntity, type) && isFaceTrapped(targetEntity, type);
    }

    public static boolean isBedrock(BlockPos pos) {
        return mc.world.getBlockState(pos).isOf(Blocks.BEDROCK);
    }

    private static float getDamageForDifficulty(float damage) {
        return switch (mc.world.getDifficulty()) {
            case PEACEFUL -> 0;
            case EASY     -> Math.min(damage * 0.5f + 1, damage);
            case HARD     -> damage * 1.5f;
            default       -> damage;
        };
    }

    private static float resistanceReduction(LivingEntity player, float damage) {
        if (player.hasStatusEffect(StatusEffects.RESISTANCE)) {
            int lvl = (player.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1);
            damage *= (1 - (lvl * 0.2));
        }

        return damage < 0 ? 0 : damage;
    }

    private static float blastProtReduction(Entity player, float damage, Explosion explosion) {
        int protLevel = EnchantmentHelper.getProtectionAmount(player.getArmorItems(), DamageSource.explosion(explosion));
        if (protLevel > 20) protLevel = 20;

        damage *= (1 - (protLevel * 0.04));
        return damage < 0 ? 0 : damage;
    }

    public static float crystalDamage(PlayerEntity player, Vec3d crystal, boolean predictMovement, double explosionRadius, boolean ignoreTerrain, boolean fullBlocks) {
        if (player == null) return 0;
        if (EntityUtils.getGameMode(player) == GameMode.CREATIVE && !(player instanceof FakePlayerEntity)) return 0;

        ((IVec3d) vec3d).set(player.getPos().x, player.getPos().y, player.getPos().z);
        if (predictMovement) ((IVec3d) vec3d).set(vec3d.x + player.getVelocity().x, vec3d.y + player.getVelocity().y, vec3d.z + player.getVelocity().z);

        float modDistance = (float) Math.sqrt(vec3d.squaredDistanceTo(crystal));
        if (modDistance > explosionRadius) return 0;

        float exposure = getExposure(crystal, player, predictMovement, raycastContext, ignoreTerrain, fullBlocks);
        float impact = (1 - (modDistance / 12)) * exposure;
        float damage = (impact * impact + impact) * 42 + 1;

        // Multiply damage by difficulty
        damage = getDamageForDifficulty(damage);
        // Reduce by Armor
        damage = DamageUtil.getDamageLeft(damage, (float) player.getArmor(), (float) player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).getValue());
        // Reduce by Resistance
        damage = resistanceReduction(player, damage);
        // Set the IExplosion
        ((IExplosion) explosion).set(crystal, 6, false);
        // Reduce by Blast Protection
        damage = blastProtReduction(player, damage, explosion);

        return damage < 0 ? 0 : damage;
    }


    private static float getExposure(Vec3d source, Entity entity, boolean predictMovement, RaycastContext raycastContext, boolean ignoreTerrain, boolean fullBlocks) {
        Box box = entity.getBoundingBox();
        if (predictMovement) {
            Vec3d v = entity.getVelocity();
            box.offset(v.x, v.y, v.z);
        }

        double d = 1 / ((box.maxX - box.minX) * 2 + 1);
        double e = 1 / ((box.maxY - box.minY) * 2 + 1);
        double f = 1 / ((box.maxZ - box.minZ) * 2 + 1);
        double g = (1 - Math.floor(1 / d) * d) * 0.5;
        double h = (1 - Math.floor(1 / f) * f) * 0.5;

        if (!(d < 0) && !(e < 0) && !(f < 0)) {
            int i = 0;
            int j = 0;

            for (float k = 0; k <= 1; k += d) {
                for (float l = 0; l <= 1; l += e) {
                    for (float m = 0; m <= 1; m += f) {
                        double n = MathHelper.lerp(k, box.minX, box.maxX);
                        double o = MathHelper.lerp(l, box.minY, box.maxY);
                        double p = MathHelper.lerp(m, box.minZ, box.maxZ);

                        ((IVec3d) vec3d).set(n + g, o, p + h);
                        ((IRaycastContext) raycastContext).set(vec3d, source, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity);

                        if (raycast(raycastContext, ignoreTerrain, fullBlocks).getType() == HitResult.Type.MISS) i++;

                        j++;
                    }
                }
            }

            return (float) i / j;
        }

        return 0;
    }

    private static BlockHitResult raycast(RaycastContext context, boolean ignoreTerrain, boolean fullBlocks) {
        return BlockView.raycast(context.getStart(), context.getEnd(), context, (raycastContext, blockPos) -> {
            BlockState blockState;

            blockState = mc.world.getBlockState(blockPos);
            if (blockState.getBlock() instanceof AnvilBlock && fullBlocks) blockState = Blocks.OBSIDIAN.getDefaultState();
            else if (blockState.getBlock() instanceof EnderChestBlock && fullBlocks) blockState = Blocks.OBSIDIAN.getDefaultState();
            else if (blockState.getBlock().getBlastResistance() < 600 && ignoreTerrain) blockState = Blocks.AIR.getDefaultState();

            Vec3d vec3d = raycastContext.getStart();
            Vec3d vec3d2 = raycastContext.getEnd();

            VoxelShape voxelShape = raycastContext.getBlockShape(blockState, mc.world, blockPos);
            BlockHitResult blockHitResult = mc.world.raycastBlock(vec3d, vec3d2, blockPos, voxelShape, blockState);
            VoxelShape voxelShape2 = VoxelShapes.empty();
            BlockHitResult blockHitResult2 = voxelShape2.raycast(vec3d, vec3d2, blockPos);

            double d = blockHitResult == null ? Double.MAX_VALUE : raycastContext.getStart().squaredDistanceTo(blockHitResult.getPos());
            double e = blockHitResult2 == null ? Double.MAX_VALUE : raycastContext.getStart().squaredDistanceTo(blockHitResult2.getPos());

            return d <= e ? blockHitResult : blockHitResult2;
        }, (raycastContext) -> {
            Vec3d vec3d = raycastContext.getStart().subtract(raycastContext.getEnd());
            return BlockHitResult.createMissed(raycastContext.getEnd(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), new BlockPos(raycastContext.getEnd()));
        });
    }

    public static float crystalDamage(PlayerEntity player, Vec3d crystal, double explosionRadius) {
        return crystalDamage(player, crystal, false, explosionRadius, false, false);
    }

    public static boolean isBurrowed(PlayerEntity targetEntity, BlastResistantType type) {
        BlockPos playerPos = WorldUtil.roundBlockPos(new Vec3d(targetEntity.getX(), targetEntity.getY() + 0.4, targetEntity.getZ()));
        // Adding a 0.4 to the Y check since sometimes when the player moves around weirdly/ after chorusing they tend to clip into the block under them
        return isBlastResistant(playerPos, type);
    }

    public enum BlastResistantType {
        Any, // Any blast resistant block
        Unbreakable, // Can't be mined
        Mineable, // You can mine the block
        NotAir // Doesn't matter as long it's not air
    }

    public static boolean isSurrounded(PlayerEntity player, BlastResistantType type) {
        BlockPos blockPos = player.getBlockPos();

        for (Direction direction : Direction.values()) {
            if (direction == Direction.UP || direction == Direction.DOWN) continue;
            if (!isBlastResistant(blockPos, type)) return false;
        }

        return true;
    }


    public static boolean isAnyTrapped(PlayerEntity targetEntity, BlastResistantType type) {
        return isTopTrapped(targetEntity, type) || isFaceTrapped(targetEntity, type);
    }


    public static boolean isFaceTrapped(PlayerEntity player, BlastResistantType type) {
        BlockPos blockPos = player.getBlockPos();

        for (Direction direction : Direction.values()) {
            if (direction == Direction.UP || direction == Direction.DOWN) continue;
            if (!isBlastResistant(blockPos, type)) return false;
        }

        return true;
    }
    public static boolean isBlastResistant(BlockPos pos, BlastResistantType type) {
        Block block = mc.world.getBlockState(pos).getBlock();
        switch (type) {
            case Any, Mineable -> {
                return block == Blocks.OBSIDIAN
                    || block == Blocks.CRYING_OBSIDIAN
                    || block instanceof AnvilBlock
                    || block == Blocks.NETHERITE_BLOCK
                    || block == Blocks.ENDER_CHEST
                    || block == Blocks.RESPAWN_ANCHOR
                    || block == Blocks.ANCIENT_DEBRIS
                    || block == Blocks.ENCHANTING_TABLE
                    || (block == Blocks.BEDROCK && type == BlastResistantType.Any)
                    || (block == Blocks.END_PORTAL_FRAME && type == BlastResistantType.Any);
            }
            case Unbreakable -> {
                return block == Blocks.BEDROCK
                    || block == Blocks.END_PORTAL_FRAME;
            }
            case NotAir -> {
                return block != Blocks.AIR;
            }
        }
        return false;
    }

}
