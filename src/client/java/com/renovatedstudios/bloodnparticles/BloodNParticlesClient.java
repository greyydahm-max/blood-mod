package com.renovatedstudios.bloodnparticles;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.Ocelot;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.fish.Cod;
import net.minecraft.world.entity.animal.fish.Pufferfish;
import net.minecraft.world.entity.animal.fish.Salmon;
import net.minecraft.world.entity.animal.fish.TropicalFish;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.animal.cow.MushroomCow;
import net.minecraft.world.entity.animal.dolphin.Dolphin;
import net.minecraft.world.entity.animal.equine.Donkey;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.animal.equine.Llama;
import net.minecraft.world.entity.animal.equine.Mule;
import net.minecraft.world.entity.animal.equine.TraderLlama;
import net.minecraft.world.entity.animal.equine.ZombieHorse;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.animal.squid.GlowSquid;
import net.minecraft.world.entity.animal.squid.Squid;
import net.minecraft.world.entity.animal.turtle.Turtle;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.monster.skeleton.Bogged;
import net.minecraft.world.entity.monster.skeleton.Stray;
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton;
import net.minecraft.world.entity.monster.spider.CaveSpider;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.zombie.Drowned;
import net.minecraft.world.entity.monster.zombie.Husk;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Environment(EnvType.CLIENT)
public class BloodNParticlesClient implements ClientModInitializer {

    private final Map<Integer, Integer> lastSeenHurtTime = new HashMap<>();
    private final Random rng = new Random();

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_LEVEL_TICK.register(this::onTick);
    }

    private void onTick(ClientLevel world) {
        if (Minecraft.getInstance().player == null) return;

        for (Entity entity : world.entitiesForRendering()) {
            if (!(entity instanceof LivingEntity living)) continue;

            int id = living.getId();
            int hurt = living.hurtTime;

            if (hurt == 1 && lastSeenHurtTime.getOrDefault(id, 0) != 1) {
                Vec3 pos = living.position().add(0, living.getBbHeight() * 0.5, 0);
                Vec3 feet = living.position().add(0, 0.05, 0);
                handleEntity(world, living, pos, feet);
            }

            lastSeenHurtTime.put(id, hurt);
        }

        lastSeenHurtTime.entrySet().removeIf(e -> world.getEntity(e.getKey()) == null);
    }

    private void handleEntity(ClientLevel world, LivingEntity e, Vec3 pos, Vec3 feet) {

        if (e instanceof WitherSkeleton) {
            fallingDust(world, pos.add(0, 0.3, 0), Blocks.COAL_BLOCK.defaultBlockState(), 20, 0.4, 0.6);
            splat(world, feet, 0.1f, 0.1f, 0.1f, "medium");
            return;
        }
        if (e instanceof Stray) {
            particles(world, pos, ParticleTypes.SNOWFLAKE, 16, 0.4, 0.6);
            splat(world, feet, 0.8f, 0.95f, 1.0f, "medium");
            return;
        }
        if (e instanceof Bogged) {
            dust(world, pos.add(0, 0.4, 0), 0.675f, 0.741f, 0.090f, 1.0f, 10, 0.4, 0.4);
            splat(world, feet, 0.675f, 0.741f, 0.090f, "medium");
            return;
        }
        if (e instanceof AbstractSkeleton) {
            fallingDust(world, pos, Blocks.LIGHT_GRAY_CONCRETE_POWDER.defaultBlockState(), 12, 0.4, 0.6);
            splat(world, feet, 0.8f, 0.8f, 0.8f, "medium");
            return;
        }
        if (e instanceof Silverfish) {
            fallingDust(world, pos.subtract(0, 0.2, 0), Blocks.LIGHT_GRAY_CONCRETE_POWDER.defaultBlockState(), 8, 0.4, 0.2);
            splat(world, feet, 0.75f, 0.75f, 0.75f, "small");
            return;
        }
        if (e instanceof SnowGolem) {
            particles(world, pos.add(0, 0.5, 0), ParticleTypes.SNOWFLAKE, 17, 0.4, 0.5);
            splat(world, feet, 0.9f, 0.97f, 1.0f, "medium");
            return;
        }

        if (e instanceof Drowned || e instanceof Zombie || e instanceof ZombieVillager
                || e instanceof ZombifiedPiglin || e instanceof Zoglin) {
            colorBlood(world, pos, feet, 0.35f, 0.55f, 0.1f, 5, "medium");
            return;
        }

        if (e instanceof CaveSpider)        { colorBlood(world, pos, feet, 0.05f, 0.3f,  0.05f, 5, "small");  return; }
        if (e instanceof Spider)            { colorBlood(world, pos, feet, 0.1f,  0.5f,  0.1f,  5, "medium"); return; }
        if (e instanceof Witch)             { colorBlood(world, pos, feet, 0.5f,  0.0f,  0.5f,  5, "medium"); return; }
        if (e instanceof Allay)             { colorBlood(world, pos, feet, 0.4f,  0.7f,  1.0f,  4, "small");  return; }
        if (e instanceof Armadillo)         { colorBlood(world, pos, feet, 0.6f,  0.45f, 0.35f, 5, "medium"); return; }
        if (e instanceof Axolotl ax)        { axolotlBlood(world, pos, feet, ax); return; }
        if (e instanceof Bee)               { colorBlood(world, pos, feet, 0.9f,  0.7f,  0.1f,  4, "small");  return; }
        if (e instanceof Breeze)            { colorBlood(world, pos, feet, 0.3f,  0.6f,  0.9f,  5, "medium"); return; }
        if (e instanceof ElderGuardian)     { colorBlood(world, pos, feet, 0.5f,  0.8f,  0.6f,  6, "big");    return; }
        if (e instanceof Guardian)          { colorBlood(world, pos, feet, 0.2f,  0.7f,  0.5f,  5, "medium"); return; }
        if (e instanceof GlowSquid)         { colorBlood(world, pos, feet, 0.05f, 0.85f, 0.7f,  5, "medium"); return; }
        if (e instanceof Husk husk)         { huskBlood(world, pos, feet, husk); return; }
        if (e instanceof Ravager)           { colorBlood(world, pos, feet, 0.55f, 0.35f, 0.25f, 8, "big");    return; }
        if (e instanceof Squid)             { colorBlood(world, pos, feet, 0.1f,  0.1f,  0.4f,  5, "medium"); return; }
        if (e instanceof Vex)               { colorBlood(world, pos, feet, 0.7f,  0.7f,  0.9f,  4, "small");  return; }
        if (e instanceof Warden)            { colorBlood(world, pos, feet, 0.05f, 0.05f, 0.2f,  6, "big");    return; }
        if (e instanceof Creaking)          { colorBlood(world, pos, feet, 0.3f,  0.2f,  0.1f,  5, "medium"); return; }
        if (e instanceof MagmaCube mc)      { magmaBlood(world, pos, feet, mc); return; }
        if (e instanceof Strider)           { colorBlood(world, pos, feet, 0.7f,  0.3f,  0.6f,  5, "medium"); return; }
        if (e instanceof Blaze)             { colorBlood(world, pos, feet, 1.0f,  0.5f,  0.0f,  5, "medium"); return; }
        if (e instanceof Endermite)         { colorBlood(world, pos, feet, 0.3f,  0.0f,  0.5f,  4, "small");  return; }
        if (e instanceof Shulker)           { colorBlood(world, pos, feet, 0.6f,  0.3f,  0.8f,  5, "medium"); return; }
        if (e instanceof EnderMan)          { colorBlood(world, pos, feet, 0.4f,  0.0f,  0.6f,  5, "big");    return; }
        if (e instanceof Phantom)           { colorBlood(world, pos, feet, 0.2f,  0.1f,  0.4f,  5, "medium"); return; }
        if (e instanceof Ghast)             { colorBlood(world, pos, feet, 0.9f,  0.8f,  0.8f,  8, "big");    return; }
        if (e instanceof Slime sl)          { slimeBlood(world, pos, feet, sl); return; }
        if (e instanceof AbstractIllager)   { redBlood(world, pos, feet, "medium"); return; }
        if (e instanceof PiglinBrute || e instanceof Piglin) { redBlood(world, pos, feet, "medium"); return; }
        if (e instanceof Hoglin)            { redBlood(world, pos, feet, "big"); return; }

        if (isSmallRed(e))  { redBlood(world, pos, feet, "small");  return; }
        if (isMediumRed(e)) { redBlood(world, pos, feet, "medium"); return; }
        if (isBigRed(e))    { redBlood(world, pos, feet, "big");    return; }

        if (e instanceof Player) { redBlood(world, pos, feet, "medium"); }
    }

    // ── splat: the ground puddle ─────────────────────────────────────────────

    /**
     * Spawns a large irregular flat puddle at ground level.
     * Uses many overlapping dust particles with zero Y velocity,
     * large scale, and randomised XZ spread to fake an organic splat shape.
     */
    private void splat(ClientLevel world, Vec3 feet, float r, float g, float b, String size) {
        int color = argb(r, g, b);

        // Each size gets more particles and wider spread
        int layers;
        float baseScale;
        double spread;
        switch (size) {
            case "small"  -> { layers = 18; baseScale = 2.8f; spread = 0.45; }
            case "big"    -> { layers = 45; baseScale = 4.2f; spread = 0.90; }
            default       -> { layers = 30; baseScale = 3.5f; spread = 0.65; }
        }

        for (int i = 0; i < layers; i++) {
            // Vary scale per particle for organic look
            float scale = baseScale + rng.nextFloat() * 1.5f;
            DustParticleOptions effect = new DustParticleOptions(color, scale);

            // Clump more particles near centre, fewer at edges (squared distribution)
            double ox = randSq(spread);
            double oz = randSq(spread);
            // Tiny random Y so particles don't z-fight each other
            double oy = rng.nextDouble() * 0.02;

            world.addParticle(effect,
                feet.x + ox, feet.y + oy, feet.z + oz,
                0, 0, 0);
        }

        // Second pass: a few extra large blobs off-centre for the "splatter" arms
        int arms = switch (size) { case "small" -> 3; case "big" -> 8; default -> 5; };
        for (int i = 0; i < arms; i++) {
            float scale = baseScale * 0.9f + rng.nextFloat() * 2.0f;
            DustParticleOptions effect = new DustParticleOptions(color, scale);
            double angle = rng.nextDouble() * Math.PI * 2;
            double dist  = spread * 0.6 + rng.nextDouble() * spread * 0.8;
            world.addParticle(effect,
                feet.x + Math.cos(angle) * dist,
                feet.y + rng.nextDouble() * 0.02,
                feet.z + Math.sin(angle) * dist,
                0, 0, 0);
        }
    }

    // ── blood helpers ────────────────────────────────────────────────────────

    private void redBlood(ClientLevel world, Vec3 pos, Vec3 feet, String size) {
        float scale;
        int count;
        switch (size) {
            case "small"  -> { scale = 1.0f + rng.nextFloat() * 0.3f; count = 4; }
            case "big"    -> { scale = 1.7f + rng.nextFloat() * 0.4f; count = 8; }
            default       -> { scale = 1.3f + rng.nextFloat() * 0.3f; count = 6; }
        }
        dust(world, pos, 1.0f, 0.0f, 0.0f, scale, count, 0.35, 0.15);
        splat(world, feet, 1.0f, 0.0f, 0.0f, size);
        squish(world, pos);
    }

    private void colorBlood(ClientLevel world, Vec3 pos, Vec3 feet,
                            float r, float g, float b, int count, String splatSize) {
        dust(world, pos, r, g, b, 1.2f, count, 0.35, 0.2);
        splat(world, feet, r, g, b, splatSize);
        squish(world, pos);
    }

    private void axolotlBlood(ClientLevel world, Vec3 pos, Vec3 feet, Axolotl ax) {
        float r, g, b;
        switch (ax.getVariant()) {
            case LUCY  -> { r = 0.95f; g = 0.6f;  b = 0.7f; }
            case WILD  -> { r = 0.50f; g = 0.3f;  b = 0.2f; }
            case GOLD  -> { r = 0.90f; g = 0.7f;  b = 0.1f; }
            case CYAN  -> { r = 0.10f; g = 0.8f;  b = 0.7f; }
            default    -> { r = 0.20f; g = 0.3f;  b = 0.9f; }
        }
        colorBlood(world, pos, feet, r, g, b, 5, "small");
    }

    private void huskBlood(ClientLevel world, Vec3 pos, Vec3 feet, Husk husk) {
        if (husk.isBaby()) {
            colorBlood(world, pos, feet, 0.8f, 0.65f, 0.3f, 4, "small");
        } else {
            colorBlood(world, pos, feet, 0.75f, 0.55f, 0.2f, 5, "medium");
            fallingDust(world, pos, Blocks.SAND.defaultBlockState(), 30, 0.4, 0.6);
        }
    }

    private void slimeBlood(ClientLevel world, Vec3 pos, Vec3 feet, Slime slime) {
        String size = switch (slime.getSize()) { case 3 -> "big"; case 2 -> "medium"; default -> "small"; };
        float scale = switch (slime.getSize()) { case 3 -> 1.6f;  case 2 -> 1.3f;    default -> 1.0f; };
        dust(world, pos, 0.4f, 0.8f, 0.4f, scale, 5, 0.35, 0.2);
        splat(world, feet, 0.4f, 0.8f, 0.4f, size);
        squish(world, pos);
    }

    private void magmaBlood(ClientLevel world, Vec3 pos, Vec3 feet, MagmaCube mc) {
        String size = switch (mc.getSize()) { case 3 -> "big"; case 2 -> "medium"; default -> "small"; };
        float scale = switch (mc.getSize()) { case 3 -> 1.6f;  case 2 -> 1.3f;    default -> 1.0f; };
        dust(world, pos, 1.0f, 0.3f, 0.0f, scale, 6, 0.4, 0.3);
        splat(world, feet, 1.0f, 0.3f, 0.0f, size);
        squish(world, pos);
    }

    // ── low-level particle emitters ──────────────────────────────────────────

    private void dust(ClientLevel world, Vec3 pos,
                      float r, float g, float b, float scale,
                      int count, double spread, double spreadY) {
        DustParticleOptions effect = new DustParticleOptions(argb(r, g, b), scale);
        for (int i = 0; i < count; i++) {
            world.addParticle(effect,
                pos.x + rand(spread), pos.y + rand(spreadY), pos.z + rand(spread),
                rand(0.05), rand(0.05) + 0.05, rand(0.05));
        }
    }

    private void fallingDust(ClientLevel world, Vec3 pos,
                             net.minecraft.world.level.block.state.BlockState state,
                             int count, double spread, double spreadY) {
        BlockParticleOption effect = new BlockParticleOption(ParticleTypes.FALLING_DUST, state);
        for (int i = 0; i < count; i++) {
            world.addParticle(effect,
                pos.x + rand(spread), pos.y + rand(spreadY), pos.z + rand(spread),
                rand(0.2), 0.4, rand(0.2));
        }
    }

    private void particles(ClientLevel world, Vec3 pos,
                           ParticleOptions type, int count,
                           double spread, double spreadY) {
        for (int i = 0; i < count; i++) {
            world.addParticle(type,
                pos.x + rand(spread), pos.y + rand(spreadY), pos.z + rand(spread),
                0, 0, 0);
        }
    }

    private void squish(ClientLevel world, Vec3 pos) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        double dist = mc.player.position().distanceTo(pos);
        if (dist > 16) return;
        float vol = (float) Math.max(0, 1.0 - dist / 16.0) * 0.05f;
        world.playLocalSound(pos.x, pos.y, pos.z,
            SoundEvents.SLIME_BLOCK_BREAK,
            SoundSource.BLOCKS, vol, 1.8f, false);
    }

    // ── utils ────────────────────────────────────────────────────────────────

    private int argb(float r, float g, float b) {
        return (255 << 24) | ((int)(r * 255) << 16) | ((int)(g * 255) << 8) | (int)(b * 255);
    }

    private double rand(double range) {
        return (rng.nextDouble() - 0.5) * 2.0 * range;
    }

    /** Squared distribution — more particles near centre, fewer at edges */
    private double randSq(double range) {
        double v = (rng.nextDouble() - 0.5) * 2.0;
        return Math.copySign(v * v, v) * range;
    }

    // ── entity category helpers ──────────────────────────────────────────────

    private boolean isSmallRed(LivingEntity e) {
        return e instanceof Chicken || e instanceof Cat
            || e instanceof Bat     || e instanceof Ocelot
            || e instanceof Cod     || e instanceof Pufferfish
            || e instanceof Salmon  || e instanceof Parrot
            || e instanceof Frog    || e instanceof Rabbit
            || e instanceof TropicalFish;
    }

    private boolean isMediumRed(LivingEntity e) {
        return e instanceof Dolphin         || e instanceof Villager
            || e instanceof WanderingTrader || e instanceof Cow
            || e instanceof MushroomCow     || e instanceof Pig
            || e instanceof Sheep           || e instanceof Llama
            || e instanceof TraderLlama     || e instanceof Wolf
            || e instanceof Fox             || e instanceof Turtle
            || e instanceof Goat;
    }

    private boolean isBigRed(LivingEntity e) {
        return e instanceof Camel  || e instanceof Donkey
            || e instanceof Horse  || e instanceof Mule
            || e instanceof ZombieHorse;
    }
}
