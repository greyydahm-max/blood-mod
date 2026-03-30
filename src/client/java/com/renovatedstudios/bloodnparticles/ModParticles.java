package com.renovatedstudios.bloodnparticles;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.util.Identifier;

public class ModParticles {

    public static final ParticleType<BloodSplatParticleOption> BLOOD_SPLAT =
        FabricParticleTypes.complex(false, BloodSplatParticleOption.CODEC, BloodSplatParticleOption.STREAM_CODEC);

    public static void register() {
        Registry.register(
            BuiltInRegistries.PARTICLE_TYPE,
            Identifier.of("bloodnparticles", "blood_splat"),
            BLOOD_SPLAT
        );
    }
}
