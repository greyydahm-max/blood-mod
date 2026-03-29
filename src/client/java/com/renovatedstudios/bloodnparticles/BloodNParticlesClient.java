package com.renovatedstudios.bloodnparticles;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Environment(EnvType.CLIENT)
public class BloodNParticlesClient implements ClientModInitializer {

    private final Map<Integer, Integer> lastSeenHurtTime = new HashMap<>();
    private final Random rng = new Random();

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_WORLD_TICK.register(this::onTick);
    }

    // ─── Tick ────────────────────────────────────────────────────────────────

    private void onTick(ClientWorld world) {
        if (MinecraftClient.getInstance().player == null) return;

        for (Entity entity : world.getEntities()) {
            if (!(entity instanceof LivingEntity living)) continue;

            int id = living.getId();
            int hurt = living.hurtTime;

            // Fire exactly once when hurtTime first becomes 1
            if (hurt == 1 && lastSeenHurtTime.getOrDefault(id, 0) != 1) {
                Vec3d pos = living.getPos().add(0, living.getHeight() * 0.5, 0);
                handleEntity(world, living, pos);
            }

            lastSeenHurtTime.put(id, hurt);
        }

        // Remove dead/unloaded entities from map
        lastSeenHurtTime.entrySet().removeIf(e -> world.getEntityById(e.getKey()) == null);
    }

    // ─── Entity dispatch ─────────────────────────────────────────────────────

    private void handleEntity(ClientWorld world, LivingEntity e, Vec3d pos) {

        // ── Skeleton-type: powder/snow particles ──────────────────────────────
        if (e instanceof SkeletonEntity) {
            fallingDust(world, pos, net.minecraft.block.Blocks.LIGHT_GRAY_CONCRETE_POWDER.getDefaultState(), 12, 0.4, 0.6);
            return;
        }
        if (e instanceof WitherSkeletonEntity) {
            fallingDust(world, pos.add(0, 0.3, 0), net.minecraft.block.Blocks.COAL_BLOCK.getDefaultState(), 20, 0.4, 0.6);
            return;
        }
        if (e instanceof StrayEntity) {
            particles(world, pos, ParticleTypes.SNOWFLAKE, 16, 0.4, 0.6);
            return;
        }
        if (e instanceof BoggedEntity) {
            dust(world, pos.add(0, 0.4, 0), 0.675f, 0.741f, 0.090f, 1.0f, 10, 0.4, 0.4);
            return;
        }
        if (e instanceof SilverfishEntity) {
            fallingDust(world, pos.subtract(0, 0.2, 0), net.minecraft.block.Blocks.LIGHT_GRAY_CONCRETE_POWDER.getDefaultState(), 8, 0.4, 0.2);
            return;
        }
        if (e instanceof SnowGolemEntity) {
            particles(world, pos.add(0, 0.5, 0), ParticleTypes.SNOWFLAKE, 17, 0.4, 0.5);
            return;
        }

        // ── Zombie group: greenish blood ──────────────────────────────────────
        if (e instanceof ZombieEntity || e instanceof ZombieVillagerEntity
                || e instanceof ZombifiedPiglinEntity || e instanceof ZoglinEntity) {
            colorBlood(world, pos, 0.35f, 0.55f, 0.1f, 5);
            return;
        }

        // ── Special entity blood ──────────────────────────────────────────────
        if (e instanceof DrownedEntity)      { colorBlood(world, pos, 0.0f,  0.45f, 0.6f,  5); return; }
        if (e instanceof CaveSpiderEntity)   { colorBlood(world, pos, 0.05f, 0.3f,  0.05f, 5); return; }
        if (e instanceof SpiderEntity)       { colorBlood(world, pos, 0.1f,  0.5f,  0.1f,  5); return; }
        if (e instanceof WitchEntity)        { colorBlood(world, pos, 0.5f,  0.0f,  0.5f,  5); return; }
        if (e instanceof AllayEntity)        { colorBlood(world, pos, 0.4f,  0.7f,  1.0f,  4); return; }
        if (e instanceof ArmadilloEntity)    { colorBlood(world, pos, 0.6f,  0.45f, 0.35f, 5); return; }
        if (e instanceof AxolotlEntity ax)   { axolotlBlood(world, pos, ax); return; }
        if (e instanceof BeeEntity)          { colorBlood(world, pos, 0.9f,  0.7f,  0.1f,  4); return; }
        if (e instanceof BreezeEntity)       { colorBlood(world, pos, 0.3f,  0.6f,  0.9f,  5); return; }
        if (e instanceof ElderGuardianEntity){ colorBlood(world, pos, 0.5f,  0.8f,  0.6f,  6); return; }
        if (e instanceof GuardianEntity)     { colorBlood(world, pos, 0.2f,  0.7f,  0.5f,  5); return; }
        if (e instanceof GlowSquidEntity)    { colorBlood(world, pos, 0.05f, 0.85f, 0.7f,  5); return; }
        if (e instanceof HuskEntity husk)    { huskBlood(world, pos, husk); return; }
        if (e instanceof RavagerEntity)      { colorBlood(world, pos, 0.55f, 0.35f, 0.25f, 8); return; }
        if (e instanceof SquidEntity)        { colorBlood(world, pos, 0.1f,  0.1f,  0.4f,  5); return; }
        if (e instanceof VexEntity)          { colorBlood(world, pos, 0.7f,  0.7f,  0.9f,  4); return; }
        if (e instanceof WardenEntity)       { colorBlood(world, pos, 0.05f, 0.05f, 0.2f,  6); return; }
        if (e instanceof CreakingEntity)     { colorBlood(world, pos, 0.3f,  0.2f,  0.1f,  5); return; }
        if (e instanceof MagmaCubeEntity mc) { magmaBlood(world, pos, mc); return; }
        if (e instanceof StriderEntity)      { colorBlood(world, pos, 0.7f,  0.3f,  0.6f,  5); return; }
        if (e instanceof BlazeEntity)        { colorBlood(world, pos, 1.0f,  0.5f,  0.0f,  5); return; }
        if (e instanceof EndermiteEntity)    { colorBlood(world, pos, 0.3f,  0.0f,  0.5f,  4); return; }
        if (e instanceof ShulkerEntity)      { colorBlood(world, pos, 0.6f,  0.3f,  0.8f,  5); return; }
        if (e instanceof EndermanEntity)     { colorBlood(world, pos, 0.4f,  0.0f,  0.6f,  5); return; }
        if (e instanceof PhantomEntity)      { colorBlood(world, pos, 0.2f,  0.1f,  0.4f,  5); return; }
        if (e instanceof GhastEntity)        { colorBlood(world, pos, 0.9f,  0.8f,  0.8f,  8); return; }
        if (e instanceof SlimeEntity sl)     { slimeBlood(world, pos, sl); return; }

        // ── Illagers (pillager, vindicator, evoker, illusioner) ───────────────
        if (e instanceof IllagerEntity) { redBlood(world, pos, "medium"); return; }

        // ── Piglin variants ───────────────────────────────────────────────────
        if (e instanceof PiglinBruteEntity || e instanceof PiglinEntity) {
            redBlood(world, pos, "medium"); return;
        }

        // ── Size-based red blood ──────────────────────────────────────────────
        if (isSmallRed(e))  { redBlood(world, pos, "small");  return; }
        if (isMediumRed(e)) { redBlood(world, pos, "medium"); return; }
        if (isBigRed(e))    { redBlood(world, pos, "big");    return; }

        // ── Players ───────────────────────────────────────────────────────────
        if (e instanceof net.minecraft.entity.player.PlayerEntity) {
            redBlood(world, pos, "medium");
        }
    }

    // ─── Specific blood types ────────────────────────────────────────────────

    private void redBlood(ClientWorld world, Vec3d pos, String size) {
        float scale;
        int count;
        switch (size) {
            case "small"  -> { scale = 1.0f + rng.nextFloat() * 0.3f; count = 4; }
            case "big"    -> { scale = 1.7f + rng.nextFloat() * 0.4f; count = 8; }
            default       -> { scale = 1.3f + rng.nextFloat() * 0.3f; count = 6; }
        }
        dust(world, pos, 1.0f, 0.0f, 0.0f, scale, count, 0.35, 0.15);
        squish(world, pos);
    }

    private void colorBlood(ClientWorld world, Vec3d pos, float r, float g, float b, int count) {
        dust(world, pos, r, g, b, 1.2f, count, 0.35, 0.2);
        squish(world, pos);
    }

    private void axolotlBlood(ClientWorld world, Vec3d pos, AxolotlEntity ax) {
        float r, g, b;
        switch (ax.getVariant()) {
            case LUCY  -> { r = 0.95f; g = 0.6f;  b = 0.7f; }
            case WILD  -> { r = 0.50f; g = 0.3f;  b = 0.2f; }
            case GOLD  -> { r = 0.90f; g = 0.7f;  b = 0.1f; }
            case CYAN  -> { r = 0.10f; g = 0.8f;  b = 0.7f; }
            default    -> { r = 0.20f; g = 0.3f;  b = 0.9f; } // blue
        }
        colorBlood(world, pos, r, g, b, 5);
    }

    private void huskBlood(ClientWorld world, Vec3d pos, HuskEntity husk) {
        if (husk.isBaby()) {
            colorBlood(world, pos, 0.8f, 0.65f, 0.3f, 4);
        } else {
            colorBlood(world, pos, 0.75f, 0.55f, 0.2f, 5);
            fallingDust(world, pos, net.minecraft.block.Blocks.SAND.getDefaultState(), 30, 0.4, 0.6);
        }
    }

    private void slimeBlood(ClientWorld world, Vec3d pos, SlimeEntity slime) {
        float scale = switch (slime.getSize()) {
            case 3  -> 1.6f;
            case 2  -> 1.3f;
            default -> 1.0f;
        };
        dust(world, pos, 0.4f, 0.8f, 0.4f, scale, 5, 0.35, 0.2);
        squish(world, pos);
    }

    private void magmaBlood(ClientWorld world, Vec3d pos, MagmaCubeEntity mc) {
        float scale = switch (mc.getSize()) {
            case 3  -> 1.6f;
            case 2  -> 1.3f;
            default -> 1.0f;
        };
        dust(world, pos, 1.0f, 0.3f, 0.0f, scale, 6, 0.4, 0.3);
        squish(world, pos);
    }

    // ─── Particle helpers ────────────────────────────────────────────────────

    private void dust(ClientWorld world, Vec3d pos,
                      float r, float g, float b, float scale,
                      int count, double spread, double spreadY) {
        DustParticleEffect effect = new DustParticleEffect(new Vector3f(r, g, b), scale);
        for (int i = 0; i < count; i++) {
            world.addParticle(effect,
                pos.x + rand(spread), pos.y + rand(spreadY), pos.z + rand(spread),
                rand(0.05), rand(0.05) + 0.05, rand(0.05));
        }
    }

    private void fallingDust(ClientWorld world, Vec3d pos,
                             net.minecraft.block.BlockState state,
                             int count, double spread, double spreadY) {
        BlockStateParticleEffect effect = new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, state);
        for (int i = 0; i < count; i++) {
            world.addParticle(effect,
                pos.x + rand(spread), pos.y + rand(spreadY), pos.z + rand(spread),
                rand(0.2), 0.4, rand(0.2));
        }
    }

    private void particles(ClientWorld world, Vec3d pos,
                           ParticleEffect type, int count,
                           double spread, double spreadY) {
        for (int i = 0; i < count; i++) {
            world.addParticle(type,
                pos.x + rand(spread), pos.y + rand(spreadY), pos.z + rand(spread),
                0, 0, 0);
        }
    }

    private void squish(ClientWorld world, Vec3d pos) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        double dist = mc.player.getPos().distanceTo(pos);
        if (dist > 16) return;
        float vol = (float) Math.max(0, 1.0 - dist / 16.0) * 0.05f;
        world.playSound(pos.x, pos.y, pos.z,
            SoundEvents.BLOCK_SLIME_BLOCK_BREAK,
            SoundCategory.BLOCKS, vol, 1.8f, false);
    }

    private double rand(double range) {
        return (rng.nextDouble() - 0.5) * 2.0 * range;
    }

    // ─── Entity categories ───────────────────────────────────────────────────

    private boolean isSmallRed(LivingEntity e) {
        return e instanceof ChickenEntity || e instanceof CatEntity
            || e instanceof BatEntity    || e instanceof OcelotEntity
            || e instanceof CodEntity    || e instanceof PufferfishEntity
            || e instanceof SalmonEntity || e instanceof ParrotEntity
            || e instanceof FrogEntity   || e instanceof RabbitEntity
            || e instanceof TropicalFishEntity;
    }

    private boolean isMediumRed(LivingEntity e) {
        return e instanceof DolphinEntity   || e instanceof VillagerEntity
            || e instanceof WanderingTraderEntity || e instanceof CowEntity
            || e instanceof MooshroomEntity || e instanceof PigEntity
            || e instanceof SheepEntity     || e instanceof LlamaEntity
            || e instanceof TraderLlamaEntity || e instanceof WolfEntity
            || e instanceof FoxEntity       || e instanceof TurtleEntity
            || e instanceof GoatEntity;
    }

    private boolean isBigRed(LivingEntity e) {
        return e instanceof CamelEntity  || e instanceof DonkeyEntity
            || e instanceof HorseEntity  || e instanceof MuleEntity
            || e instanceof HoglinEntity || e instanceof ZombieHorseEntity;
    }
}
