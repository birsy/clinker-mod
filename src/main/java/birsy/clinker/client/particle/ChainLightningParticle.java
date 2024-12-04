package birsy.clinker.client.particle;

import birsy.clinker.client.render.ClinkerRenderTypes;
import birsy.clinker.client.render.RenderUtils;
import birsy.clinker.common.world.alchemy.effects.ChainLightningHandler;
import birsy.clinker.core.registry.ClinkerParticles;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;

import org.checkerframework.checker.units.qual.A;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;


public class ChainLightningParticle extends Particle {
    protected TextureAtlasSprite sprite;
    private final RenderType renderType = ClinkerRenderTypes.chainLightning();

    Vec3 startPos, endPos;
    private List<Vec3> boltPositions;
    private List<Float> boltDistances;
    private float totalDistance;

    protected ChainLightningParticle(ClientLevel pLevel, Vec3 startPos, Vec3 endPos, SpriteSet pSprites) {
        super(pLevel, startPos.x(), startPos.y(), startPos.z());
        this.startPos = startPos;
        this.endPos = endPos;
        this.sprite = pSprites.get(pLevel.random);
        this.boltPositions = new ArrayList<>();
        this.boltDistances = new ArrayList<>();
        this.boltPositions.add(startPos);
        this.boltPositions.add(endPos);

        // randomly split the line segment in two and jitter points
        // should look vaguely lightning-y...
        float distance = 4.0f; //startPos.distanceTo(endPos);
        int splits = pLevel.random.nextInt((int) (distance * 1.0), (int) (distance * 4.0));
        for (int i = 0; i < splits; i++) {
            int splitIndex = pLevel.random.nextInt(this.boltPositions.size() - 1);
            Vec3 newPoint = this.boltPositions.get(splitIndex).add(this.boltPositions.get(splitIndex + 1)).scale(0.5);

            float averageOffset = (float) newPoint.distanceTo(this.boltPositions.get(splitIndex)) * 0.2f;
            newPoint = newPoint.add(pLevel.random.nextGaussian() * averageOffset, pLevel.random.nextGaussian() * averageOffset, pLevel.random.nextGaussian() * averageOffset);

            this.boltPositions.add(splitIndex + 1, newPoint);
        }

        this.totalDistance = 0.0f;
        boltDistances.add(0.0f);
        for (int i = 1; i < boltPositions.size(); i++) {
            Vec3 pos1 = boltPositions.get(i - 1);
            Vec3 pos2 = boltPositions.get(i);
            this.totalDistance += pos1.distanceTo(pos2);
            boltDistances.add(this.totalDistance);
        }

        this.setLifetime(ChainLightningHandler.BOLT_TRAVEL_TIME);
    }

    @Override
    public AABB getBoundingBox() {
        return new AABB(startPos, endPos).inflate(0.1F);
    }

    @Override
    public void tick() {
        super.tick();
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public void render(VertexConsumer pBuffer, Camera camera, float pPartialTicks) {
        float time = (float)this.age / (float)this.lifetime;

        Vec3 camPos = camera.getPosition();
        PoseStack posestack = new PoseStack();
        posestack.translate(-camPos.x, -camPos.y, -camPos.z);

        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vertexconsumer = bufferSource.getBuffer(this.renderType);

        for (int i = 0; i < boltPositions.size() - 1; i++) {
            Vec3 pos1 = boltPositions.get(i);
            Vec3 pos2 = boltPositions.get(i + 1);

            Vector3f tangent1 = pos1.subtract(pos2).toVector3f().normalize();
            Vector3f tangent2 = tangent1;
            if (i < boltPositions.size() - 2) { tangent2 = pos2.subtract(boltPositions.get(i + 2)).toVector3f().normalize(); }

            Vector3f biTangent1 = camPos.subtract(pos1).toVector3f().normalize();
            Vector3f biTangent2 = camPos.subtract(pos2).toVector3f().normalize();

            Vector3f normal1 = new Vector3f(tangent1).cross(biTangent1);
            Vector3f normal2 = new Vector3f(tangent2).cross(biTangent2);

            float d1 = boltDistances.get(i) / totalDistance;
            float d2 = boltDistances.get(i + 1) / totalDistance;
            float v1 = Mth.lerp(1.0f - d1, sprite.getV0(), sprite.getV1());
            float v2 = Mth.lerp(1.0f - d2, sprite.getV0(), sprite.getV1());

            RenderUtils.drawFaceBetweenPoints(vertexconsumer, posestack, 0.05f, pos1, tangent1, biTangent1, normal1, sprite.getU0(), v1, 1, 1, 1, time,
                                                                                       pos2, tangent2, biTangent2, normal2, sprite.getU1(), v2, 1, 1, 1, time);
        }

        bufferSource.endBatch();
    }

    
    public static class Provider implements ParticleProvider<ChainLightningParticleOptions>, ParticleEngine.SpriteParticleRegistration<ChainLightningParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(ChainLightningParticleOptions pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new ChainLightningParticle(pLevel, new Vec3(pType.startX, pType.startY, pType.startZ), new Vec3(pType.endX, pType.endY, pType.endZ), this.sprites);
        }

        @Override
        public ParticleProvider<ChainLightningParticleOptions> create(SpriteSet pSprites) {
            return new Provider(pSprites);
        }
    }

    public static class ChainLightningParticleOptions implements ParticleOptions {
        public static final MapCodec<ChainLightningParticleOptions> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                Codec.DOUBLE.fieldOf("startX").forGetter(particleOptions -> particleOptions.startX),
                                Codec.DOUBLE.fieldOf("startY").forGetter(particleOptions -> particleOptions.startY),
                                Codec.DOUBLE.fieldOf("startZ").forGetter(particleOptions -> particleOptions.startZ),
                                Codec.DOUBLE.fieldOf("endX").forGetter(particleOptions -> particleOptions.endX),
                                Codec.DOUBLE.fieldOf("endY").forGetter(particleOptions -> particleOptions.endY),
                                Codec.DOUBLE.fieldOf("endZ").forGetter(particleOptions -> particleOptions.endZ)
                        ).apply(builder, ChainLightningParticleOptions::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, ChainLightningParticleOptions> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.DOUBLE, particleOptions -> particleOptions.startX,
                ByteBufCodecs.DOUBLE, particleOptions -> particleOptions.startY,
                ByteBufCodecs.DOUBLE, particleOptions -> particleOptions.startZ,
                ByteBufCodecs.DOUBLE, particleOptions -> particleOptions.endX,
                ByteBufCodecs.DOUBLE, particleOptions -> particleOptions.endY,
                ByteBufCodecs.DOUBLE, particleOptions -> particleOptions.endZ,
                ChainLightningParticleOptions::new
        );

        protected final double startX, startY, startZ;
        protected final double endX, endY, endZ;

        public ChainLightningParticleOptions(double startX, double startY, double startZ, double endX, double endY, double endZ) {
            this.startX = startX;
            this.startY = startY;
            this.startZ = startZ;
            this.endX = endX;
            this.endY = endY;
            this.endZ = endZ;
        }

        public ChainLightningParticleOptions(Vec3 start, Vec3 end) {
            this.startX = start.x();
            this.startY = start.y();
            this.startZ = start.z();
            this.endX = end.x();
            this.endY = end.y();
            this.endZ = end.z();
        }

        @Override
        public ParticleType<?> getType() {
            return ClinkerParticles.CHAIN_LIGHTNING.get();
        }
    }
}
