package com.renovatedstudios.bloodnparticles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.RandomSource;
import net.minecraft.core.BlockPos;

@Environment(EnvType.CLIENT)
public class BloodSplatParticle extends SingleQuadParticle {

    private final float yawAngle;
    private final boolean isWall;

    protected BloodSplatParticle(ClientLevel level, double x, double y, double z,
                                  BloodSplatParticleOption opts, SpriteSet sprites) {
        super(level, x, y, z, sprites.get(opts.texIndex % 15, 14));

        this.rCol = opts.r;
        this.gCol = opts.g;
        this.bCol = opts.b;
        this.quadSize = opts.scale;
        this.yawAngle = (float) Math.toRadians(opts.yaw);
        this.isWall = opts.isWall;

        if (!opts.isWall) {
            double groundY = y;
            for (int i = 0; i < 8; i++) {
                BlockPos check = BlockPos.containing(x, groundY - 0.1, z);
                if (!level.getBlockState(check).isAir()) {
                    groundY = check.getY() + 1.0;
                    break;
                }
                groundY -= 1.0;
            }
            this.y = groundY + 0.02;
        } else {
            this.y = y;
        }

        this.yo = this.y;
        this.x = x;
        this.xo = x;
        this.z = z;
        this.zo = z;

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
        float yaw = this.yawAngle;
        boolean wall = this.isWall;
        return (target, camera, partialTickTime) -> {
            if (wall) {
                target.identity().rotateY(yaw);
            } else {
                target.identity()
                      .rotateY(yaw)
                      .rotateX((float)(-Math.PI / 2));
            }
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
