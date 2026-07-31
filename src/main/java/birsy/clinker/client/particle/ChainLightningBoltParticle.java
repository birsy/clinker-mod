package birsy.clinker.client.particle;

import birsy.clinker.core.registry.ClinkerParticles;
import birsy.clinker.core.util.codecs.ExtraByteBufCodecs;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class ChainLightningBoltParticle extends Particle {
    final double startX, startY, startZ;
    final double endX, endY, endZ;
    final Vector3f scratch = new Vector3f();
    final float startV, endV;

    final int travelTicks, decayTicks;

    protected ChainLightningBoltParticle(ClientLevel level, double x1, double y1, double z1, double x2, double y2, double z2, double r, double g, double b, double thickness, int travelTicks, int decayTicks) {
        super(level, (x1 + x2) * 0.5, (y1 + y2) * 0.5, (z1 + z2) * 0.5);

        this.startX = x1; this.startY = y1; this.startZ = z1;
        this.endX = x2; this.endY = y2; this.endZ = z2;
        this.setBoundingBox(new AABB(
                Math.min(x1,x2), Math.min(y1,y2), Math.min(z1,z2),
                Math.max(x1,x2), Math.max(y1,y2), Math.max(z1,z2)
        ));
        this.rCol = (float) r; this.gCol = (float) g; this.bCol = (float) b; this.alpha = (float) thickness;

        this.hasPhysics = false;

        // lightning bolt texture is 128 px = 8 blocks long
        float length = (float) Mth.length(x1 - x2, y1 - y2, z1 - z2) / 8.0F;
        this.startV = level.random.nextFloat() * length; this.endV = startV + length;

        this.travelTicks = travelTicks; this.decayTicks = decayTicks;
        this.setLifetime(travelTicks + decayTicks);
    }

    @Override
    public void move(double x, double y, double z) {}

    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 cameraPos = camera.getPosition();

        float startX = (float) (this.startX - cameraPos.x()),
              startY = (float) (this.startY - cameraPos.y()),
              startZ = (float) (this.startZ - cameraPos.z());
        float endX = (float) (this.endX - cameraPos.x()),
              endY = (float) (this.endY - cameraPos.y()),
              endZ = (float) (this.endZ - cameraPos.z());

        float progress = this.age / (float) this.travelTicks;
        float clampedProgress = Math.clamp(progress, 0, 0.8F);
        float middleX = Mth.clampedLerp(startX, endX, clampedProgress),
              middleY = Mth.clampedLerp(startY, endY, clampedProgress),
              middleZ = Mth.clampedLerp(startZ, endZ, clampedProgress);
        float middleV = Mth.clampedLerp(startV, endV, clampedProgress);

        endX = Mth.clampedLerp(startX, endX, clampedProgress + 0.2F);
        endY = Mth.clampedLerp(startY, endY, clampedProgress + 0.2F);
        endZ = Mth.clampedLerp(startZ, endZ, clampedProgress + 0.2F);
        float endV = Mth.clampedLerp(startV, this.endV, clampedProgress + 0.2F);

        float startFade = Mth.clampedMap(progress, 0, 0.5F, 0, 1);
        float decayFade = Mth.clampedMap(age, travelTicks, lifetime, 1, 0);
        float startAlpha = this.alpha * startFade * startFade * decayFade * 0.2F,
              middleAlpha = this.alpha * startFade * decayFade,
              endAlpha = 0;

        scratch.set(endX - startX, endY - startY, endZ - startZ);
        scratch.cross(camera.forwards);
        scratch.normalize();
        float offsetX = scratch.x,
              offsetY = scratch.y,
              offsetZ = scratch.z;

        int packedLight = this.getLightColor(partialTicks);
        // start -> middle quad
        buffer.addVertex(startX - offsetX, startY - offsetY, startZ - offsetZ)
                .setColor(this.rCol, this.gCol, this.bCol, startAlpha)
                .setUv(0, startV)
                .setLight(packedLight);
        buffer.addVertex(middleX - offsetX, middleY - offsetY, middleZ - offsetZ)
                .setUv(0, middleV)
                .setColor(this.rCol, this.gCol, this.bCol, middleAlpha)
                .setLight(packedLight);
        buffer.addVertex(middleX + offsetX, middleY + offsetY, middleZ + offsetZ)
                .setColor(this.rCol, this.gCol, this.bCol, middleAlpha)
                .setUv(1, middleV)
                .setLight(packedLight);
        buffer.addVertex(startX + offsetX, startY + offsetY, startZ + offsetZ)
                .setColor(this.rCol, this.gCol, this.bCol, startAlpha)
                .setUv(1, startV)
                .setLight(packedLight);
        // middle -> end quad
        buffer.addVertex(middleX - offsetX, middleY - offsetY, middleZ - offsetZ)
                .setColor(this.rCol, this.gCol, this.bCol, middleAlpha)
                .setUv(0, middleV)
                .setLight(packedLight);
        buffer.addVertex(endX - offsetX, endY - offsetY, endZ - offsetZ)
                .setColor(this.rCol, this.gCol, this.bCol, endAlpha)
                .setUv(0, endV)
                .setLight(packedLight);
        buffer.addVertex(endX + offsetX, endY + offsetY, endZ + offsetZ)
                .setColor(this.rCol, this.gCol, this.bCol, endAlpha)
                .setUv(1, endV)
                .setLight(packedLight);
        buffer.addVertex(middleX + offsetX, middleY + offsetY, middleZ + offsetZ)
                .setColor(this.rCol, this.gCol, this.bCol, middleAlpha)
                .setUv(1, middleV)
                .setLight(packedLight);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ClinkerParticleRenderTypes.CHAIN_LIGHTNING;
    }

    public static class Provider implements ParticleProvider<Options> {
        @Override
        public @Nullable Particle createParticle(Options type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ChainLightningBoltParticle(
                    level,
                    type.startX, type.startY, type.startZ,
                    type.endX, type.endY, type.endZ,
                    FastColor.ARGB32.red(type.color) / 255.0F,
                    FastColor.ARGB32.green(type.color) / 255.0F,
                    FastColor.ARGB32.blue(type.color) / 255.0F,
                    FastColor.ARGB32.alpha(type.color) / 255.0F,
                    type.travelTicks,
                    5
            );
        }
    }

    public record Options(double startX, double startY, double startZ, double endX, double endY, double endZ, int color, int travelTicks) implements ParticleOptions {
        public static final MapCodec<Options> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Codec.DOUBLE.fieldOf("start_x").forGetter(Options::startX),
                        Codec.DOUBLE.fieldOf("start_y").forGetter(Options::startY),
                        Codec.DOUBLE.fieldOf("start_z").forGetter(Options::startZ),
                        Codec.DOUBLE.fieldOf("end_x").forGetter(Options::endX),
                        Codec.DOUBLE.fieldOf("end_y").forGetter(Options::endY),
                        Codec.DOUBLE.fieldOf("end_z").forGetter(Options::endZ),
                        ExtraCodecs.ARGB_COLOR_CODEC.fieldOf("color").forGetter(Options::color),
                        Codec.INT.fieldOf("travel_ticks").forGetter(Options::travelTicks)
                        ).apply(instance, Options::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, Options> STREAM_CODEC = ExtraByteBufCodecs.composite(
                ByteBufCodecs.DOUBLE, Options::startX,
                ByteBufCodecs.DOUBLE, Options::startY,
                ByteBufCodecs.DOUBLE, Options::startZ,
                ByteBufCodecs.DOUBLE, Options::endX,
                ByteBufCodecs.DOUBLE, Options::endY,
                ByteBufCodecs.DOUBLE, Options::endZ,
                ByteBufCodecs.INT, Options::color,
                ByteBufCodecs.INT, Options::travelTicks,
                Options::new
        );

        @Override
        public ParticleType<Options> getType() {
            return ClinkerParticles.CHAIN_LIGHTNING_BOLT.get();
        }
    }
}
