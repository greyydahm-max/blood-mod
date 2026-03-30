package com.renovatedstudios.bloodnparticles;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class BloodSplatParticleOption implements ParticleOptions {

    public static final MapCodec<BloodSplatParticleOption> CODEC =
        RecordCodecBuilder.mapCodec(inst -> inst.group(
            com.mojang.serialization.Codec.FLOAT.fieldOf("r").forGetter(o -> o.r),
            com.mojang.serialization.Codec.FLOAT.fieldOf("g").forGetter(o -> o.g),
            com.mojang.serialization.Codec.FLOAT.fieldOf("b").forGetter(o -> o.b),
            com.mojang.serialization.Codec.INT.fieldOf("tex").forGetter(o -> o.texIndex),
            com.mojang.serialization.Codec.FLOAT.fieldOf("scale").forGetter(o -> o.scale),
            com.mojang.serialization.Codec.FLOAT.fieldOf("yaw").forGetter(o -> o.yaw)
        ).apply(inst, BloodSplatParticleOption::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BloodSplatParticleOption> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.FLOAT, o -> o.r,
            ByteBufCodecs.FLOAT, o -> o.g,
            ByteBufCodecs.FLOAT, o -> o.b,
            ByteBufCodecs.INT,   o -> o.texIndex,
            ByteBufCodecs.FLOAT, o -> o.scale,
            ByteBufCodecs.FLOAT, o -> o.yaw,
            BloodSplatParticleOption::new
        );

    public final float r, g, b, scale, yaw;
    public final int texIndex;

    public BloodSplatParticleOption(float r, float g, float b, int texIndex, float scale, float yaw) {
        this.r = r; this.g = g; this.b = b;
        this.texIndex = texIndex;
        this.scale = scale;
        this.yaw = yaw;
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.BLOOD_SPLAT;
    }
}
