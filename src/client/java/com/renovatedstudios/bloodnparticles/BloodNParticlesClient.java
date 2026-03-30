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
import net.minecraft.world.entity.animal.cat.Cat;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.cod.Cod;
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
import net.minecraft.world.entity.animal.ocelot.Ocelot;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.animal.pufferfish.Pufferfish;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.animal.salmon.Salmon;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.animal.snowgolem.SnowGolem;
import net.minecraft.world.entity.animal.squid.GlowSquid;
import net.minecraft.world.entity.animal.squid.Squid;
import net.minecraft.world.entity.animal.tropicalfish.TropicalFish;
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
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
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
                handleEntity(world, living, pos);
            }

            lastSeenHurtTime.put(id, hurt);
        }

        lastSeenHurtTime.entrySet().removeIf(e -> world.getEntity(e.getKey()) == null);
    }

    private void handleEntity(ClientLevel world, LivingEntity e, Vec3 pos) {

        // Skeleton types — check subclasses first, then AbstractSkeleton for plain skeleton
        if (e instanceof WitherSkeleton) {
            fallingDust(world, pos.add(0, 0.3, 0), Blocks.COAL_BLOCK.defaultBlockState(), 20, 0.4, 0.6);
            return;
        }
        if (e instanceof Stray) {
            particles(world, pos, ParticleTypes.SNOWFLAKE, 16, 0.4, 0.6);
            return;
        }
        if (e instanceof Bogged) {
            dust(world, pos.add(0, 0.4, 0), 0.675f, 0.741f, 0.090f, 1.0f, 10, 0.4, 0.4);
            return;
        }
        if (e instanceof AbstractSkeleton) {
            fallingDust(world, pos, Blocks.LIGHT_GRAY_CONCRETE_POWDER.defaultBlockState(), 12, 0.4, 0.6);
            return;
        }
        if (e instanceof Silverfish) {
            fallingDust(world, pos.subtract(0, 0.2, 0), Blocks.LIGHT_GRAY_CONCRETE_POWDER.defaultBlockState(), 8, 0.4, 0.2);
            return;
        }
        if (e instanceof SnowGolem) {
            particles(world, pos.add(0, 0.5, 0), ParticleTypes.SNOWFLAKE, 17, 0.4, 0.5);
            return;
        }

        // Zombie group
        if (e instanceof Drowned || e instanceof Zombie || e instanceof ZombieVillager
                || e instanceof ZombifiedPiglin || e instanceof Zoglin) {
            colorBlood(world, pos, 0.35f, 0.55f, 0.1f, 5);
            return;
        }

        // Special entities
        if (e instanceof CaveSpider)        { colorBlood(world, pos, 0.05f, 0.3f,  0.05f, 5); return; }
        if (e instanceof Spider)            { colorBlood(world, pos, 0.1f,  0.5f,  0.1f,  5); return; }
        if (e instanceof Witch)             { colorBlood(world, pos, 0.5f,  0.0f,  0.5f,  5); return; }
        if (e instanceof Allay)             { colorBlood(world, pos, 0.4f,  0.7f,  1.0f,  4); return; }
        if (e instanceof Armadillo)         { colorBlood(world, pos, 0.6f,  0.45f, 0.35f, 5); return; }
        if (e instanceof Axolotl ax)        { axolotlBlood(world, pos, ax); return; }
        if (e instanceof Bee)               { colorBlood(world, pos, 0.9f,  0.7f,  0.1f,  4); return; }
        if (e instanceof Breeze)            { colorBlood(world, pos, 0.3f,  0.6f,  0.9f,  5); return; }
        if (e instanceof ElderGuardian)     { colorBlood(world, pos, 0.5f,  0.8f,  0.6f,  6); return; }
        if (e instanceof Guardian)          { colorBlood(world, pos, 0.2f,  0.7f,  0.5f,  5); return; }
        if (e instanceof GlowSquid)         { colorBlood(world, pos, 0.05f, 0.85f, 0.7f,  5); return; }
        if (e instanceof Husk husk)         { huskBlood(world, pos, husk); return; }
        if (e instanceof Ravager)           { colorBlood(world, pos, 0.55f, 0.35f, 0.25f, 8); return; }
        if (e instanceof Squid)             { colorBlood(world, pos, 0.1f,  0.1f,  0.4f,  5); return; }
        if (e instanceof Vex)               { colorBlood(world, pos, 0.7f,  0.7f,  0.9f,  4); return; }
        if (e instanceof Warden)            { colorBlood(world, pos, 0.05f, 0.05f, 0.2f,  6); return; }
        if (e instanceof Creaking)          { colorBlood(world, pos, 0.3f,  0.2f,  0.1f,  5); return; }
        if (e instanceof MagmaCube mc)      { magmaBlood(world, pos, mc); return; }
        if (e instanceof Strider)           { colorBlood(world, pos, 0.7f,  0.3f,  0.6f,  5); return; }
        if (e instanceof Blaze)             { colorBlood(world, pos, 1.0f,  0.5f,  0.0f,  5); return; }
        if (e instanceof Endermite)         { colorBlood(world, pos, 0.3f,  0.0f,  0.5f,  4); return; }
        if (e instanceof Shulker)           { colorBlood(world, pos, 0.6f,  0.3f,  0.8f,  5); return; }
        if (e instanceof EnderMan)          { colorBlood(world, pos, 0.4f,  0.0f,  0.6f,  5); return; }
        if (e instanceof Phantom)           { colorBlood(world, pos, 0.2f,  0.1f,  0.4f,  5); return; }
        if (e instanceof Ghast)             { colorBlood(world, pos, 0.9f,  0.8f,  0.8f,  8); return; }
        if (e instanceof Slime sl)          { slimeBlood(world, pos, sl); return; }
        if (e instanceof AbstractIllager)   { redBlood(world, pos, "medium"); return; }
        if (e instanceof PiglinBrute || e instanceof Piglin) { redBlood(world, pos, "medium"); return; }
        if (e instanceof Hoglin)            { redBlood(world, pos, "big"); return; }

        if (isSmallRed(e))  { redBlood(world, pos, "small");  return; }
        if (isMediumRed(e)) { redBlood(world, pos, "medium"); return; }
        if (isBigRed(e))    { redBlood(world, pos, "big");    return; }

        if (e instanceof Player) { redBlood(world, pos, "medium"); }
    }

    private void redBlood(ClientLevel world, Vec3 pos, String size) {
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

    private void colorBlood(ClientLevel world, Vec3 pos, float r, float g, float b, int count) {
        dust(world, pos, r, g, b, 1.2f, count, 0.35, 0.2);
        squish(world, pos);
    }

    private void axolotlBlood(ClientLevel world, Vec3 pos, Axolotl ax) {
        float r, g, b;
        switch (ax.getVariant()) {
            case LUCY  -> { r = 0.95f; g = 0.6f;  b = 0.7f; }
            case WILD  -> { r = 0.50f; g = 0.3f;  b = 0.2f; }
            case GOLD  -> { r = 0.90f; g = 0.7f;  b = 0.1f; }
            case CYAN  -> { r = 0.10f; g = 0.8f;  b = 0.7f; }
            default    -> { r = 0.20f; g = 0.3f;  b = 0.9f; }
        }
        colorBlood(world, pos, r, g, b, 5);
    }

    private void huskBlood(ClientLevel world, Vec3 pos, Husk husk) {
        if (husk.isBaby()) {
            colorBlood(world, pos, 0.8f, 0.65f, 0.3f, 4);
        } else {
            colorBlood(world, pos, 0.75f, 0.55f, 0.2f, 5);
            fallingDust(world, pos, Blocks.SAND.defaultBlockState(), 30, 0.4, 0.6);
        }
    }

    private void slimeBlood(ClientLevel world, Vec3 pos, Slime slime) {
        float scale = switch (slime.getSize()) {
            case 3  -> 1.6f;
            case 2  -> 1.3f;
            default -> 1.0f;
        };
        dust(world, pos, 0.4f, 0.8f, 0.4f, scale, 5, 0.35, 0.2);
        squish(world, pos);
    }

    private void magmaBlood(ClientLevel world, Vec3 pos, MagmaCube mc) {
        float scale = switch (mc.getSize()) {
            case 3  -> 1.6f;
            case 2  -> 1.3f;
            default -> 1.0f;
        };
        dust(world, pos, 1.0f, 0.3f, 0.0f, scale, 6, 0.4, 0.3);
        squish(world, pos);
    }

    private void dust(ClientLevel world, Vec3 pos,
                      float r, float g, float b, float scale,
                      int count, double spread, double spreadY) {
        int color = (255 << 24) | ((int)(r * 255) << 16) | ((int)(g * 255) << 8) | (int)(b * 255);
        DustParticleOptions effect = new DustParticleOptions(color, scale);
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

    private double rand(double range) {
        return (rng.nextDouble() - 0.5) * 2.0 * range;
    }

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
