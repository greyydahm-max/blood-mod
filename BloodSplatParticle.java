package com.renovatedstudios.bloodnparticles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;

@Environment(EnvType.CLIENT)
public class BloodSplatParticle extends TextureSheetParticle {

    private final float r, g, b;
    private final float halfSize;
    private final float cosYaw, sinYaw;

    protected BloodSplatParticle(ClientLevel level, double x, double y, double z,
                                  BloodSplatParticleOption opts, SpriteSet sprites) {
        super(level, x, y, z, 0, 0, 0);

        this.r = opts.r;
        this.g = opts.g;
        this.b = opts.b;
        this.halfSize = opts.scale * 0.5f;

        // Pre-compute rotation
        float yawRad = (float) Math.toRadians(opts.yaw);
        this.cosYaw = (float) Math.cos(yawRad);
        this.sinYaw = (float) Math.sin(yawRad);

        // Pick sprite by texture index (0-14 maps to blood1-blood15)
        this.setSprite(sprites.get(opts.texIndex % 15, 14));

        this.lifetime = 100 + random.nextInt(60); // 5-8 seconds
        this.alpha = 1.0f;
        this.hasPhysics = false;
        this.gravity = 0;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;

        // Snap exactly to block surface
        this.y = Math.floor(y) + 1.001;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        // Fade out over last 30 ticks
        float age = this.age + partialTick;
        float fade = age > (this.lifetime - 30) ? (this.lifetime - age) / 30.0f : 1.0f;
        if (fade <= 0) return;

        net.minecraft.world.phys.Vec3 camPos = camera.getPosition();
        float px = (float)(this.x - camPos.x);
        float py = (float)(this.y - camPos.y);
        float pz = (float)(this.z - camPos.z);

        net.minecraft.client.renderer.texture.TextureAtlasSprite sprite = this.sprite;
        float u0 = sprite.getU0(), u1 = sprite.getU1();
        float v0 = sprite.getV0(), v1 = sprite.getV1();

        float s = halfSize;
        float cos = this.cosYaw;
        float sin = this.sinYaw;

        // Four corners of a flat horizontal quad, rotated around Y axis
        // Local offsets: (-s,0,-s), (s,0,-s), (s,0,s), (-s,0,s)
        float[] lx = {-s,  s,  s, -s};
        float[] lz = {-s, -s,  s,  s};
        float[] us = {u0, u1, u1, u0};
        float[] vs = {v0, v0, v1, v1};

        int light = this.getLightColor(partialTick);

        for (int i = 0; i < 4; i++) {
            float wx = lx[i] * cos - lz[i] * sin;
            float wz = lx[i] * sin + lz[i] * cos;
            buffer.addVertex(px + wx, py, pz + wz)
                  .setColor(r, g, b, alpha * fade)
                  .setUv(us[i], vs[i])
                  .setLight(light);
        }
    }

    @Override
    public void tick() {
        super.tick();
        // No movement — stays locked to ground
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
                                       double dx, double dy, double dz) {
            return new BloodSplatParticle(level, x, y, z, opts, sprites);
        }
    }
}
