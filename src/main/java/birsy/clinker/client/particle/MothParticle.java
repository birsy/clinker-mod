package birsy.clinker.client.particle;

import birsy.clinker.core.util.noise.Basic1dNoise;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;

public class MothParticle extends Particle {
    protected static final Basic1dNoise NOISE = new Basic1dNoise(RandomSource.create(9999), 128);
    protected static final PoseStack SHARED_STACK = new PoseStack();
    protected static final Vector3f SCRATCH_VEC = new Vector3f();
    protected static final Quaternionf SCRATCH_QUATERNION_A = new Quaternionf(), SCRATCH_QUATERNION_B = new Quaternionf();

    protected TextureAtlasSprite sprite;
    protected final int seed;
    protected final Vector3f heading = new Vector3f(), pHeading = new Vector3f();
    protected final Vector3f lightDir = new Vector3f(), meanderDir = new Vector3f(), surfacePushDir = new Vector3f(), nearestEntityDir = new Vector3f();
    protected double desiredY;

    protected boolean landed;
    protected Direction landedDirection = Direction.UP;
    protected float landFactor = 0F, pLandFactor = 1F;

    protected float wingRot, pWingRot;

    protected MothParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet pSprites) {
        super(level, x, y, z);
        this.sprite = pSprites.get(level.random);
        this.desiredY = y;
        this.xd = xSpeed; this.yd = ySpeed; this.zd = zSpeed;
        this.gravity = 1F;

        this.setLifetime(level.random.nextInt(400, 600));
        this.setSize(0.1F, 0.1F);

        this.seed = level.random.nextInt(512);
    }

    @Override
    public void tick() {
        this.pWingRot = wingRot;
        this.pLandFactor = landFactor;
        this.pHeading.set(heading);

        // this is kind of expensive, so only do it periodically.
        if (this.age % 5 == 0) updateNearestEntityDir();

        if (this.landed) {
            this.xd = 0; this.yd = 0; this.zd = 0;
            this.desiredY = y;
            this.gravity = 0F;

            this.landFactor = Mth.lerp(0.3F, landFactor, 1F);

            boolean detached = this.random.nextInt(60) == 0;
            if (!detached && nearestEntityDir.lengthSquared() > 0.001)
                detached = true;
            if (!detached && this.age % 3 == 0) {
                // check that we're still attached
                double skinWidth = 0.05, epsilon = 0.01;
                Direction attachmentDirection = landedDirection.getOpposite();
                double xa = attachmentDirection.getStepX(), ya = attachmentDirection.getStepY(), za = attachmentDirection.getStepZ();
                Vec3 collidedVector = Entity.collideBoundingBox(null, new Vec3(xa * skinWidth, ya * skinWidth, za * skinWidth), this.getBoundingBox(), this.level, List.of());

                if (attachmentDirection.getAxis() == Direction.Axis.X && Math.abs(collidedVector.x()) >= skinWidth - epsilon) detached = true;
                else if (attachmentDirection.getAxis() == Direction.Axis.Y && Math.abs(collidedVector.y()) >= skinWidth - epsilon) detached = true;
                else if (attachmentDirection.getAxis() == Direction.Axis.Z && Math.abs(collidedVector.z()) >= skinWidth - epsilon) detached = true;
            }

            if (detached) {
                this.landed = false;
                // boost in velocity away from the wall
                this.flap(landedDirection, 1);
                this.surfacePushDir.set(
                        landedDirection.getStepX(),
                        landedDirection.getStepY(),
                        landedDirection.getStepZ()
                ).mul(4.0F);
            }
        } else {
            this.landFactor = Mth.lerp(0.1F, landFactor, 0F);

            updateMeanderVector();
            updateSurfacePushVector();
            this.heading.set(0)
                    .add(meanderDir)
                    .add(lightDir)
                    .add(surfacePushDir)
                    .add(nearestEntityDir)
                    .normalize(0.1F);

            this.desiredY = Math.clamp(this.desiredY + this.heading.y, this.y - 0.3F, this.y + 0.3F);

            // flap!
            if (this.y < this.desiredY - 0.2)
                this.flap(Direction.UP, random.triangle(1, 1));
            float acceleration = 0.02F;
            this.xd = Mth.approach((float) this.xd, this.heading.x, acceleration);
            this.zd = Mth.approach((float) this.zd, this.heading.z, acceleration);
            this.gravity = Mth.lerp(0.01F, this.gravity, 1F);
        }

        super.tick();
    }

    @Override
    public void move(double deltaX, double deltaY, double deltaZ) {
        if (deltaX == 0.0 && deltaY == 0.0 && deltaZ == 0.0) return;

        Vec3 actualDelta = Entity.collideBoundingBox(null, new Vec3(deltaX, deltaY, deltaZ), this.getBoundingBox(), this.level, List.of());

        if (actualDelta.x != 0.0 || actualDelta.y != 0.0 || actualDelta.z != 0.0) {
            this.setBoundingBox(this.getBoundingBox().move(actualDelta.x(), actualDelta.y(), actualDelta.z()));
            this.setLocationFromBoundingbox();

            // block pos changed
            if (Math.floor(this.xo) != Math.floor(this.x) ||
                Math.floor(this.yo) != Math.floor(this.y) ||
                Math.floor(this.zo) != Math.floor(this.z)) {
                this.updateLightVector(BlockPos.containing(this.x, this.y + this.bbHeight * 0.5F, this.z));
            }
        }

        if (landed) return;
        if (deltaY != actualDelta.y && deltaY != 0) {
            landed = true;
            landedDirection = deltaY < 0 ? Direction.UP : Direction.DOWN;
        } else if (deltaX != actualDelta.x && deltaX != 0) {
            landed = true;
            landedDirection = deltaX < 0 ? Direction.EAST : Direction.WEST;
        } else if (deltaZ != actualDelta.z && deltaZ != 0) {
            landed = true;
            landedDirection = deltaZ < 0 ? Direction.SOUTH : Direction.NORTH;
        }
    }

    // wander randomly
    protected void updateMeanderVector() {
        float time = this.age / 20F;
        this.meanderDir.set(
                NOISE.sample(seed + time),
                NOISE.sample(seed + time + 30) * 0.3F,
                NOISE.sample(seed + time - 60)
        );
        if (meanderDir.lengthSquared() > 0.001)
            meanderDir.normalize();
    }

    // circle lights
    protected void updateLightVector(BlockPos pos) {
        int currentLightLevel = level.getBrightness(LightLayer.BLOCK, pos);
        int xLightDiff = level.getBrightness(LightLayer.BLOCK, pos.east())  - level.getBrightness(LightLayer.BLOCK, pos.west());
        int yLightDiff = level.getBrightness(LightLayer.BLOCK, pos.above()) - level.getBrightness(LightLayer.BLOCK, pos.below());
        int zLightDiff = level.getBrightness(LightLayer.BLOCK, pos.south()) - level.getBrightness(LightLayer.BLOCK, pos.north());
        lightDir.set(xLightDiff, yLightDiff, zLightDiff);
        if (lightDir.lengthSquared() > 0.001)
            lightDir.normalize();
        if (lightDir.x() != 0 || lightDir.z != 0)
            lightDir.cross(0, 1 * (this.seed % 2 == 0 ? -1 : 1), 0);
        float strength = Mth.map(currentLightLevel, 8, 15, 0, 4);
        strength = Math.max(0, strength);
        lightDir.mul(strength);
    }

    // flee from nearby entities
    protected void updateNearestEntityDir() {
        Optional<LivingEntity> maybeNearby = EntityRetrievalUtil.getNearestEntity(this.level, new Vec3(x, y, z), 5, LivingEntity.class);
        if (maybeNearby.isPresent()) {
            LivingEntity nearby = maybeNearby.get();
            this.nearestEntityDir.set(x - nearby.getX(), y - (nearby.getY() + nearby.getBbHeight() * 0.5F), z - nearby.getZ());
            float distance = this.nearestEntityDir.length();
            if (distance != 0)
                this.nearestEntityDir.normalize(Mth.map(distance, 5, 0, 0, 5));
        } else {
            this.nearestEntityDir.set(0);
        }
    }

    // move away from any surfaces we just landed on
    protected void updateSurfacePushVector() {
        this.surfacePushDir.set(
                Mth.approach(this.surfacePushDir.x, 0, 0.2F),
                Mth.approach(this.surfacePushDir.y, 0, 0.2F),
                Mth.approach(this.surfacePushDir.z, 0, 0.2F)
        );
    }

    protected void flap(Direction direction, double power) {
        double acceleration = Mth.lerp(power, 0, 0.05);
        this.xd += direction.getStepX() * acceleration;
        this.yd += direction.getStepY() * acceleration;
        this.zd += direction.getStepZ() * acceleration;
    }

    @Override
    protected int getLightColor(float partialTick) {
        BlockPos blockpos = BlockPos.containing(this.x, this.y + this.bbHeight * 0.5F, this.z);
        return this.level.hasChunkAt(blockpos) ? LevelRenderer.getLightColor(this.level, blockpos) : 0;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        SHARED_STACK.pushPose();
        Vec3 camPos = camera.getPosition();
        float rX = (float) (Mth.lerp(partialTicks, this.xo, this.x) - camPos.x()),
              rY = (float) (Mth.lerp(partialTicks, this.yo, this.y) - camPos.y()),
              rZ = (float) (Mth.lerp(partialTicks, this.zo, this.z) - camPos.z());
        SHARED_STACK.translate(rX, rY + this.bbHeight * 0.5F, rZ);

        float size = Mth.clampedMap(this.age + partialTicks, 0, 10, 0, 1) *
                Mth.clampedMap(this.age + partialTicks, this.lifetime - 60, this.lifetime, 1, 0);
        size *= 0.3F;
        SHARED_STACK.scale(size, size, size);

        float interpLandFactor = Mth.lerp(partialTicks, pLandFactor, landFactor);
        Vector3f interpHeading = SCRATCH_VEC.set(this.pHeading).lerp(this.heading, partialTicks).normalize();

        float wingRotation = NOISE.sample((this.seed + this.age + partialTicks) * 0.5F) * 60;

        float pitch = (float) Math.asin(interpHeading.y()), yaw = (float) Mth.atan2(-interpHeading.x(), -interpHeading.z());
        Quaternionf rot = SCRATCH_QUATERNION_B.identity().rotateY(yaw).rotateX(pitch);

        if (interpLandFactor > 0) {
            wingRotation = Mth.lerp(interpLandFactor,
                    wingRotation, (NOISE.sample((this.seed + this.age + partialTicks) * 0.1F) * 0.5F + 0.5F) * 60F
            );

            Quaternionf landedRot = SCRATCH_QUATERNION_A.identity();
            if (this.landedDirection.getAxis() == Direction.Axis.Y) {
                landedRot.rotateY(yaw);
                landedRot.premul(this.landedDirection.getRotation());
            } else {
                // not technically correct but i want some variation in their vertical landing angles...
                landedRot.rotateAxis(
                        yaw * 0.2F,
                        this.landedDirection.getStepX(),
                        this.landedDirection.getStepY(),
                        this.landedDirection.getStepZ()
                );
                landedRot.mul(this.landedDirection.getRotation());
            }
            rot.slerp(landedRot, interpLandFactor);
        }

        SHARED_STACK.mulPose(rot);

        int lightColor = getLightColor(partialTicks);
        renderWing(buffer, SHARED_STACK, true , wingRotation, lightColor);
        renderWing(buffer, SHARED_STACK, false, wingRotation, lightColor);
        SHARED_STACK.popPose();
    }

    protected void renderWing(VertexConsumer buffer, PoseStack stack, boolean right, float rotation, int light) {
        float sidedness = (right ? -1 : 1);
        stack.pushPose();
        stack.mulPose(Axis.ZP.rotationDegrees(rotation * sidedness));
        PoseStack.Pose pose = stack.last();

        // double sided
        buffer.addVertex(pose, sidedness * 0F, 0F, -1F).setUv(sprite.getU(.5F), sprite.getV(0F)).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);
        buffer.addVertex(pose, sidedness * 0F, 0F,  1F).setUv(sprite.getU(.5F), sprite.getV(1F)).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);
        buffer.addVertex(pose, sidedness * 1F, 0F,  1F).setUv(sprite.getU(.5F + sidedness * .5F), sprite.getV(1F)).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);
        buffer.addVertex(pose, sidedness * 1F, 0F, -1F).setUv(sprite.getU(.5F + sidedness * .5F), sprite.getV(0F)).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);

        buffer.addVertex(pose, sidedness * 1F, 0F, -1F).setUv(sprite.getU(.5F + sidedness * .5F), sprite.getV(0F)).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);
        buffer.addVertex(pose, sidedness * 1F, 0F,  1F).setUv(sprite.getU(.5F + sidedness * .5F), sprite.getV(1F)).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);
        buffer.addVertex(pose, sidedness * 0F, 0F,  1F).setUv(sprite.getU(.5F), sprite.getV(1F)).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);
        buffer.addVertex(pose, sidedness * 0F, 0F, -1F).setUv(sprite.getU(.5F), sprite.getV(0F)).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);

        stack.popPose();
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
