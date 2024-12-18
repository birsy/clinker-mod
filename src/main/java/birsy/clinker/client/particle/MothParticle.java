package birsy.clinker.client.particle;

import birsy.clinker.core.util.MathUtil;
import birsy.clinker.core.util.noise.FastNoiseLite;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

import org.joml.Matrix4f;


public class MothParticle extends Particle {
    protected TextureAtlasSprite sprite;
    private static final FastNoiseLite noise;
    static { noise = new FastNoiseLite(); noise.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic); }
    private float flap;
    private float pFlap;

    private float seed;

    private Vec3 heading;
    private float currentLightLevel;
    private Vec3 maxLightDirection;
    private Vec3 lightHeading = Vec3.ZERO;
    private boolean flying;
    private float takeoffTimer;
    private Direction attachmentDirection;


    BlockPos currentBlockPos, previousBlockPos;

    protected MothParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ);
        this.sprite = pSprites.get(pLevel.random);

        this.flap = 0;
        this.pFlap = 0;

        this.seed = pLevel.random.nextFloat();

        this.heading = Vec3.ZERO;

        this.flying = true;
        this.takeoffTimer = 0;
        this.attachmentDirection = Direction.DOWN;

        this.currentBlockPos = BlockPos.containing(this.getPos().add(0.5, 0.5, 0.5));

        this.setLifetime(1000);
    }

    @Override
    public void tick() {
        super.tick();
        float flapSpeed = 60;


        if (this.flying) {
            tickFlying();
        } else {
            flapSpeed = 5;
            tickLanded();
        }

        this.pFlap = this.flap;
        this.flap = (float) this.noise.GetNoise(this.age * flapSpeed, this.seed * 100);
    }

    private void updateBlockPosition(BlockPos newPos) {
        float maxLight = 0;
        Vec3 lightPosOffset = Vec3.ZERO;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    float light = this.level.getBrightness(LightLayer.BLOCK, newPos.offset(x , y , z));

                    if (light > maxLight) {
                        maxLight = light;
                        lightPosOffset = new Vec3(x, y, z);
                    }
                }
            }
        }

        this.currentLightLevel = this.level.getBrightness(LightLayer.BLOCK, newPos);
        this.maxLightDirection = lightPosOffset.normalize();
    }

    private Vec3 getLightHeading() {
        // begin to move in circles around lights if you're close by!
        float lerpFactor = this.currentLightLevel;
        if (lerpFactor > 10.0F) {
            lerpFactor = 1.0F;
        } else {
            lerpFactor = 0.0F;
        }

        this.lightHeading = this.lightHeading.lerp(this.maxLightDirection.lerp(this.maxLightDirection.cross(new Vec3(0, 1, 0)), lerpFactor), 0.1).normalize();
        if (this.maxLightDirection == Vec3.ZERO) return Vec3.ZERO;
        return this.lightHeading;
    }

    private void tickFlying() {
        this.takeoffTimer = (float) Mth.clamp(this.takeoffTimer - 0.1, 0, 1);

        this.heading = this.getRandomNoiseHeading();

        this.currentBlockPos = BlockPos.containing(this.getPos().add(0.5, 0.5, 0.5));
        if (this.previousBlockPos != this.currentBlockPos) {
            this.previousBlockPos = this.currentBlockPos;
            this.updateBlockPosition(this.currentBlockPos);
        }

        Vec3 lightHeading = this.getLightHeading();
        //Clinker.LOGGER.info(this.lightHeading);
        if (lightHeading != Vec3.ZERO) this.heading = this.heading.lerp(lightHeading, 0.3F);

        this.heading = this.heading.lerp(this.getTakeoffHeading(), this.takeoffTimer);
        this.heading.normalize();

        Vec3 velocity = heading.scale(0.135 + (this.seed * 0.1));
        this.move(velocity.x(), velocity.y(), velocity.z());
    }

    private Vec3 getRandomNoiseHeading() {
        float hSpeed = 2;
        float vSpeed = 5;
        float noiseY = this.seed * 256;
        return new Vec3(this.noise.GetNoise(this.age * hSpeed, noiseY + 128), this.noise.GetNoise(this.age * vSpeed, noiseY), this.noise.GetNoise(this.age * hSpeed,noiseY + 512));
    }

    private Vec3 getTakeoffHeading() {
        return new Vec3(attachmentDirection.getNormal().getX(), attachmentDirection.getNormal().getY(), attachmentDirection.getNormal().getZ());
    }

    private void tickLanded() {
        Vec3 pos = new Vec3(this.x, this.y, this.z);
        if (this.random.nextInt(512) == 0) { this.takeoff(); return; }

        Vec3 antiNormal = new Vec3(-this.attachmentDirection.getNormal().getX(), -this.attachmentDirection.getNormal().getY(), -this.attachmentDirection.getNormal().getY());
        BlockHitResult raycast = this.level.clip(new ClipContext(pos, pos.add(antiNormal.scale(0.05)), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, CollisionContext.empty()));
        if (raycast.getType() == HitResult.Type.MISS) { this.takeoff(); return; }

        for (Entity entity : level.getEntities(null, this.getBoundingBox().inflate(16))) {
            if (entity.getPosition(1.0F).distanceToSqr(pos) < (16 + (this.seed * 2.0) - 1.0)) { this.takeoff(); return; }
        }
    }

    public void takeoff() {
        this.flying = true;
        this.takeoffTimer = 1;
    }

    public void move(double pX, double pY, double pZ) {
        Vec3 pos = new Vec3(this.x, this.y, this.z);
        Vec3 nextPos = pos.add(pX, pY, pZ);
        BlockHitResult raycast = this.level.clip(new ClipContext(pos, nextPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, CollisionContext.empty()));

        this.setBoundingBox(this.getBoundingBox().move(raycast.getLocation().subtract(pos)));
        this.setLocationFromBoundingbox();

        if (raycast.getType() == HitResult.Type.BLOCK && this.takeoffTimer < 0.8) {
            this.flying = false;
            this.attachmentDirection = raycast.getDirection();
        }
    }

    private float getFlap(float partialTick) {
        return Mth.lerp(partialTick, pFlap, flap);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    protected int getLightColor(float pPartialTick) {
        BlockPos blockpos = BlockPos.containing(x, y, z);
        if (!this.flying) {
            blockpos = blockpos.offset((int) (this.attachmentDirection.getNormal().getX() * 0.5), (int) (this.attachmentDirection.getNormal().getY() * 0.5), (int) (this.attachmentDirection.getNormal().getZ() * 0.5));
        }

        return this.level.hasChunkAt(blockpos) ? LevelRenderer.getLightColor(this.level, blockpos) : 0;
    }

    public void render(VertexConsumer pBuffer, Camera camera, float pPartialTicks) {
        Vec3 vec3 = camera.getPosition();
        float x = (float)(Mth.lerp(pPartialTicks, this.xo, this.x) - vec3.x());
        float y = (float)(Mth.lerp(pPartialTicks, this.yo, this.y) - vec3.y());
        float z = (float)(Mth.lerp(pPartialTicks, this.zo, this.z) - vec3.z());

        PoseStack posestack = new PoseStack();
        posestack.translate(x, y, z);

        float flap = this.getFlap(pPartialTicks) * 2.0F;

        if (this.flying) {
            posestack.mulPose(Axis.YP.rotation((float) Mth.atan2(heading.x(), heading.z()) + Mth.PI));
            posestack.mulPose(Axis.XP.rotation((float) heading.y() * 2));
        } else {
            posestack.mulPose(this.attachmentDirection.getRotation());
            flap *= flap;
            flap += 0.15;
            flap = -flap;
            float tilt = ((seed * 2.0f) - 1.0f) * 0.2F;
            if (this.attachmentDirection == Direction.UP || this.attachmentDirection == Direction.DOWN) tilt = seed * 2 * Mth.PI;
            posestack.mulPose(Axis.YP.rotation(tilt));
        }
        float totalLife = (this.age + pPartialTicks) / this.lifetime;
        float scale = Mth.clamp(MathUtil.mapRange(0.95F, 1.0F, 1, 0, totalLife), 0, 1);
        scale *= Mth.sqrt(scale);
        posestack.scale(scale, scale, scale);
        int packedLight = getLightColor(pPartialTicks);
        Matrix4f matrix;
        float mothRadius = 0.25F;
        float halfU = (sprite.getU0() + sprite.getU1()) * 0.5F;

        posestack.pushPose();
        posestack.mulPose(Axis.ZP.rotation(-flap));
        matrix = posestack.last().pose();
        drawDoubleSidedQuad(pBuffer, matrix, mothRadius, mothRadius, 0, -mothRadius, sprite.getU0(), sprite.getV0(), halfU, sprite.getV1(), packedLight);
        posestack.popPose();
        posestack.pushPose();
        posestack.mulPose(Axis.ZP.rotation(flap));
        matrix = posestack.last().pose();
        drawDoubleSidedQuad(pBuffer, matrix,0, mothRadius, -mothRadius, -mothRadius, halfU, sprite.getV0(), sprite.getU1(), sprite.getV1(), packedLight);
        posestack.popPose();
    }

    private void drawDoubleSidedQuad(VertexConsumer pBuffer, Matrix4f matrix, float maxX, float maxZ, float minX, float minZ, float maxU, float maxV, float minU, float minV, int packedLight) {
        pBuffer.addVertex(matrix, maxX, 0, maxZ).setUv(maxU, minV).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(packedLight);
        pBuffer.addVertex(matrix, maxX, 0, minZ).setUv(maxU, maxV).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(packedLight);
        pBuffer.addVertex(matrix, minX, 0, minZ).setUv(minU, maxV).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(packedLight);
        pBuffer.addVertex(matrix, minX, 0, maxZ).setUv(minU, minV).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(packedLight);

        pBuffer.addVertex(matrix, minX, 0, maxZ).setUv(minU, minV).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(packedLight);
        pBuffer.addVertex(matrix, minX, 0, minZ).setUv(minU, maxV).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(packedLight);
        pBuffer.addVertex(matrix, maxX, 0, minZ).setUv(maxU, maxV).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(packedLight);
        pBuffer.addVertex(matrix, maxX, 0, maxZ).setUv(maxU, minV).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(packedLight);
    }

    
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new MothParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites);
        }
    }
}
