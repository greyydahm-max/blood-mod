package com.renovatedstudios.bloodnparticles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

@Environment(EnvType.CLIENT)
public class BloodSplatParticle extends SingleQuadParticle {

    private final float cosYaw, sinYaw;

    protected BloodSplatParticle(ClientLevel level, double x, double y, double z,
                                  BloodSplatParticleOption opts, SpriteSet sprites) {
        super(level, x, y, z, sprites.get(opts.texIndex % 15, 14));

        this.rCol = opts.r;
        this.gCol = opts.g;
        this.bCol = opts.b;
        this.quadSize = opts.scale;

        float yawRad = (float) Math.toRadians(opts.yaw);
        this.cosYaw = (float) Math.cos(yawRad);
        this.sinYaw = (float) Math.sin(yawRad);

        // Snap to ground
        this.y = Math.floor(y) + 1.001;
        this.yo = this.y;

        this.lifetime = 100 + level.getRandom().nextInt(60);
        this.hasPhysics = false;
        this.gravity = 0;
    }

    @Override
    protected SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    @Override
    public SingleQuadParticle.FacingCameraMode getFacingCameraMode() {
        // Flat on the ground — only rotate around Y axis
        return (target, camera, partialTickTime) -> {
            // Lay flat: rotate 90 degrees around X, then apply our yaw
            target.rotationX((float)(Math.PI / 2));
            target.rotateY((float) Math.atan2(sinYaw, cosYaw));
        };
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
        // Fade out in last 30 ticks
        float normalizedAge = (float) this.age / this.lifetime;
        if (normalizedAge > 0.7f) {
            this.alpha = 1.0f - (normalizedAge - 0.7f) / 0.3f;
        }
    }

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.SINGLE_QUADS;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleProvider<BloodSplatParticleOption> {
        private final SpriteSet sprites;

        public Factory(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(BloodSplatParticleOption opts, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz,
                                       RandomSource random) {
            return new BloodSplatParticle(level, x, y, z, opts, sprites);
        }
    }
}
