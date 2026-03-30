package com.renovatedstudios.bloodnparticles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class BloodSplatParticle extends Particle {

    private final float r, g, b;
    private final float halfSize;
    private final float cosYaw, sinYaw;
    private final TextureAtlasSprite sprite;
    private final int maxLife;

    protected BloodSplatParticle(ClientLevel level, double x, double y, double z,
                                  BloodSplatParticleOption opts, SpriteSet sprites) {
        super(level, x, y, z);

        this.r = opts.r;
        this.g = opts.g;
        this.b = opts.b;
        this.halfSize = opts.scale * 0.5f;
        this.maxLife = 100 + level.getRandom().nextInt(60);

        float yawRad = (float) Math.toRadians(opts.yaw);
        this.cosYaw = (float) Math.cos(yawRad);
        this.sinYaw = (float) Math.sin(yawRad);

        this.sprite = sprites.get(opts.texIndex % 15, 14);

        this.y = Math.floor(y) + 1.001;
        this.lifetime = this.maxLife;
        this.hasPhysics = false;
        this.gravity = 0;
    }

    @Override
    public ParticleGroup getGroup() {
        return null; // no group limit
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        float ageF = this.age + partialTick;
        float fade = ageF > (this.maxLife - 30) ? (this.maxLife - ageF) / 30.0f : 1.0f;
        if (fade <= 0) return;

        Vec3 camPos = camera.getPosition();
        float px = (float)(this.x - camPos.x);
        float py = (float)(this.y - camPos.y);
        float pz = (float)(this.z - camPos.z);

        float u0 = sprite.getU0(), u1 = sprite.getU1();
        float v0 = sprite.getV0(), v1 = sprite.getV1();

        float s = halfSize;
        float cos = this.cosYaw;
        float sin = this.sinYaw;

        float[] lx = {-s,  s,  s, -s};
        float[] lz = {-s, -s,  s,  s};
        float[] us = {u0, u1, u1, u0};
        float[] vs = {v0, v0, v1, v1};

        int light = getPackedLightCoords(partialTick);

        for (int i = 0; i < 4; i++) {
            float wx = lx[i] * cos - lz[i] * sin;
            float wz = lx[i] * sin + lz[i] * cos;
            buffer.addVertex(px + wx, py, pz + wz)
                  .setColor(r, g, b, fade)
                  .setUv(us[i], vs[i])
                  .setLight(light);
        }
    }

    @Override
    public void tick() {
        this.age++;
        if (this.age >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
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
