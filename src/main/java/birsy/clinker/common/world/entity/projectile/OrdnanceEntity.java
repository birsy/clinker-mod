package birsy.clinker.common.world.entity.projectile;

import birsy.clinker.client.particle.OrdnanceTrailParticle;
import birsy.clinker.client.sound.OrdnanceFuseSoundInstance;
import birsy.clinker.common.world.components.FuseTimer;
import birsy.clinker.common.world.ordnance.OrdnanceGradient;
import birsy.clinker.common.world.ordnance.OrdnanceHelper;
import birsy.clinker.common.world.ordnance.OrdnanceModifierSet;
import birsy.clinker.common.world.ordnance.OrdnanceModifierType;
import birsy.clinker.common.world.ordnance.modifiers.FuseTimeModifier;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.*;
import birsy.clinker.core.registry.entity.ClinkerEntities;
import com.google.common.base.Predicates;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import org.joml.Vector3d;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.*;

public class OrdnanceEntity extends Projectile implements IEntityWithComplexSpawn {
    private static final EntityDataAccessor<Integer> DATA_FUSE_TIME = SynchedEntityData.defineId(OrdnanceEntity.class, EntityDataSerializers.INT);
    // sticky state stuff
    BlockState cachedStickyBlockState = null;
    private static final EntityDataAccessor<Boolean> DATA_STICKY_ATTACHED = SynchedEntityData.defineId(OrdnanceEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<OptionalInt> DATA_STICKY_ENTITY_ID = SynchedEntityData.defineId(OrdnanceEntity.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
    private static final EntityDataAccessor<Optional<BlockPos>> DATA_STICKY_BLOCK = SynchedEntityData.defineId(OrdnanceEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Vector3f> DATA_STICKY_OFFSET = SynchedEntityData.defineId(OrdnanceEntity.class, EntityDataSerializers.VECTOR3);

    protected OrdnanceModifierSet modifiers = OrdnanceModifierSet.NONE;
    protected OrdnanceModifierCache modifierCache = new OrdnanceModifierCache(this.modifiers);
    protected OrdnanceGradient gradient = modifiers.gradient();

    protected final Set<Entity> lastEntityCollisions = new HashSet<>(4);

    private OrdnanceFuseSoundInstance fuseSound;

    boolean xCollision, zCollision;
    float spin, pSpin;

    public OrdnanceEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }
    public static OrdnanceEntity create(Level pLevel, double x, double y, double z) {
        OrdnanceEntity entity = new OrdnanceEntity(ClinkerEntities.ORDNANCE.get(), pLevel);
        entity.setPos(x, y, z);
        pLevel.addFreshEntity(entity);
        return entity;
    }
    public static OrdnanceEntity toss(Level pLevel, LivingEntity thrower) {
        OrdnanceEntity entity = new OrdnanceEntity(ClinkerEntities.ORDNANCE.get(), pLevel);
        entity.setOwner(thrower);
        entity.shootFromRotation(thrower, thrower.getXRot(), thrower.getYRot(), 0.0F, 1.5F, 0.0F);
        entity.setPos(thrower.getEyePosition().add(entity.getDeltaMovement().normalize()));
        return entity;
    }
    public static OrdnanceEntity fireAtPosition(Level pLevel, Vec3 currentPosition, Vec3 targetPosition, int timeInTicks) {
        OrdnanceEntity entity = OrdnanceEntity.create(pLevel, currentPosition.x, currentPosition.y, currentPosition.z);
        double timeSquared = timeInTicks * timeInTicks;
        Vec3 delta = targetPosition.subtract(currentPosition);
        Vec3 acceleration = new Vec3(0, -0.024, 0);

        double velocityX = (delta.x - (0.5 * acceleration.x * timeSquared)) / (double) timeInTicks;
        double velocityY = (delta.y - (0.5 * acceleration.y * timeSquared)) / (double) timeInTicks;
        double velocityZ = (delta.z - (0.5 * acceleration.z * timeSquared)) / (double) timeInTicks;
        entity.setDeltaMovement(velocityX, velocityY, velocityZ);

        return entity;
    }
    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        super.shoot(x, y, z, velocity * 0.6F, inaccuracy);
    }

    // networking & serialization
    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeNbt(this.modifiers.serialize(buffer.registryAccess()));
    }
    @Override
    public void readSpawnData(RegistryFriendlyByteBuf buffer) {
        this.setModifiers(OrdnanceModifierSet.deserialize(buffer.readNbt(), buffer.registryAccess()));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_FUSE_TIME, 0);
        builder.define(DATA_STICKY_ATTACHED, false);
        builder.define(DATA_STICKY_ENTITY_ID, OptionalInt.empty());
        builder.define(DATA_STICKY_BLOCK, Optional.empty());
        builder.define(DATA_STICKY_OFFSET, new Vector3f(0));
    }
    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("FuseTime", getFuseTime());
        pCompound.put("Modifiers", modifiers.serialize(this.registryAccess()));
        this.serializeStickyAttachment(pCompound);
    }
    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        setFuseTime(pCompound.getInt("FuseTime"));
        setModifiers(OrdnanceModifierSet.deserialize(pCompound.get("Modifiers"), this.registryAccess()));
        this.deserializeStickyAttachment(pCompound);
    }

    // behavior
    @Override
    public void tick() {
        this.hurtMarked = false;
        super.tick();
        this.updateStickyAttachment();
        this.tickPhysics();

        if (this.level().isClientSide())
            this.updateSpin();

        this.updateFuse();
    }

    public float getSpin(float partialTicks) { return Mth.lerp(partialTicks, this.pSpin, this.spin); }
    public void updateSpin() {
        this.pSpin = this.spin;
        if (this.isStickyAttached()) return;

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 camPosition = camera.getPosition();
        Vec3 directionToCamera = this.position().subtract(camPosition).normalize();

        //spin less when you're facing it, so the rotation looks more natural.
        Vec3 deltaMovement = this.getPosition(0).subtract(this.getPosition(1));
        Vec3 axisOfRotation = deltaMovement.normalize().cross(new Vec3(0, 1, 0));
        float angleBasedSpinMultiplier = (float) axisOfRotation.dot(directionToCamera);

        //the circumference divided by the amount moved, so it appears like it's rolling.
        float spinAmount = (float) (deltaMovement.length() / (Mth.TWO_PI * this.radius()));
        if (!this.onGround()) spinAmount *= 0.5F;
        float spinInRadians = spinAmount * Mth.TWO_PI;

        this.spin += spinInRadians * angleBasedSpinMultiplier;
    }

    // physics
    private final Vector3d scratchVelocity = new Vector3d();
    void tickPhysics() {
        Vec3 deltaMovement = this.getDeltaMovement();
        scratchVelocity.set(deltaMovement.x(), deltaMovement.y(), deltaMovement.z());
        updateGravity(scratchVelocity);
        updateFriction(scratchVelocity);
        updateVelocityFromFluid(scratchVelocity);
        updateEntityCollisions(scratchVelocity);
        this.setDeltaMovement(scratchVelocity.x, scratchVelocity.y, scratchVelocity.z);
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    @Override
    public void move(MoverType type, Vec3 vector) {
        if (this.noPhysics) {
            this.setPos(this.getX() + vector.x, this.getY() + vector.y, this.getZ() + vector.z);
        } else {
            if (type == MoverType.PISTON) {
                vector = this.limitPistonMovement(vector);
                if (vector.equals(Vec3.ZERO)) return;
            }

            this.level().getProfiler().push("move");
            Vec3 collisionConstrainedVector = this.collide(vector);
            this.setPos(
                    this.getX() + collisionConstrainedVector.x,
                    this.getY() + collisionConstrainedVector.y,
                    this.getZ() + collisionConstrainedVector.z
            );
            this.level().getProfiler().pop();

            this.level().getProfiler().push("rest");
            this.xCollision = !Mth.equal(vector.x, collisionConstrainedVector.x);
            this.zCollision = !Mth.equal(vector.z, collisionConstrainedVector.z);
            this.horizontalCollision = xCollision || zCollision;
            this.verticalCollision = !Mth.equal(vector.y, collisionConstrainedVector.y);
            this.verticalCollisionBelow = this.verticalCollision && vector.y < 0.0;

            this.setOnGroundWithMovement(this.verticalCollisionBelow, collisionConstrainedVector);

            if (!this.isRemoved()) {
                BlockPos legacyOnPos = this.getOnPosLegacy();
                BlockState legacyOnState = this.level().getBlockState(legacyOnPos);
                Block legacyOnBlock = legacyOnState.getBlock();

                Vec3 deltaMovement = this.getDeltaMovement();
                scratchVelocity.set(deltaMovement.x(), deltaMovement.y(), deltaMovement.z());

                // handle block collisions
                // this is all sorts of Slightly Incorrect
                // but like whatever it works in most cases
                Direction collisionNormal = Direction.UP;
                double collisionVelocity = 0;
                double collisionVelocityX = Math.abs(this.scratchVelocity.x);
                if (xCollision && collisionVelocityX > collisionVelocity) {
                    collisionNormal = vector.x < 0.0 ? Direction.EAST : Direction.WEST;
                    collisionVelocity = collisionVelocityX;
                }
                double collisionVelocityZ = Math.abs(this.scratchVelocity.z);
                if (zCollision && collisionVelocityZ > collisionVelocity) {
                    collisionNormal = vector.z < 0.0 ? Direction.SOUTH : Direction.NORTH;
                    collisionVelocity = collisionVelocityZ;
                }
                double collisionVelocityY = Math.abs(this.scratchVelocity.y);
                if (verticalCollision && collisionVelocityY > collisionVelocity) {
                    collisionNormal = vector.y < 0.0 ? Direction.UP : Direction.DOWN;
                    collisionVelocity = collisionVelocityY;
                }
                if (xCollision || zCollision || verticalCollision) {
                    this.playBlockCollisionSound((float) collisionVelocity);
                    if (collisionVelocity > this.blockHitSpeedThreshold()) {
                        double nudge = -0.02;
                        AABB probeAABB = this.getBoundingBox().expandTowards(
                                collisionNormal.getStepX() * nudge,
                                collisionNormal.getStepY() * nudge,
                                collisionNormal.getStepZ() * nudge
                        );
                        BlockPos collidedBlockPos = findMostOverlappingBlock(probeAABB);

                        if (collidedBlockPos != null) {
                            Vec3 testPoint = new Vec3(
                                    this.getX() + (xCollision ? Math.signum(vector.x) * this.getBbWidth() * 0.5 : 0),
                                    this.getY() + this.getBbHeight() * 0.5 + (verticalCollision ? Math.signum(vector.y) * this.getBbHeight() * 0.5 : 0),
                                    this.getZ() + (zCollision ? Math.signum(vector.z) * this.getBbWidth() * 0.5 : 0));
                            Optional<Vec3> collisionPos = this.level()
                                    .getBlockState(collidedBlockPos)
                                    .getCollisionShape(level(), collidedBlockPos, CollisionContext.of(this))
                                    .closestPointTo(testPoint);
                            this.onHitBlock(new BlockHitResult(collisionPos.orElse(testPoint), collisionNormal, collidedBlockPos, false));
                        }
                    }
                }

                // adjust velocity
                double elasticCoefficient = this.elasticCoefficient();
                if (xCollision) scratchVelocity.x = -scratchVelocity.x * elasticCoefficient;
                if (verticalCollision) scratchVelocity.y = -scratchVelocity.y * elasticCoefficient;
                if (zCollision) scratchVelocity.z = -scratchVelocity.z * elasticCoefficient;

                this.tryCheckInsideBlocks();
                float blockSpeedFactor = this.getBlockSpeedFactor();
                this.setDeltaMovement(scratchVelocity.x * blockSpeedFactor, scratchVelocity.y, scratchVelocity.z * blockSpeedFactor);

                if (this.onGround()) legacyOnBlock.stepOn(this.level(), legacyOnPos, legacyOnState, this);
            }
            this.level().getProfiler().pop();
        }
    }

    @Nullable
    private BlockPos findMostOverlappingBlock(AABB probe) {
        BlockPos.MutableBlockPos bestPos = null;
        double bestOverlap = -1;
        double[] bestOverlapArray = new double[]{bestOverlap};
        for (BlockPos pos : BlockPos.betweenClosed(
                (int) Math.floor(probe.minX), (int) Math.floor(probe.minY), (int) Math.floor(probe.minZ),
                (int) Math.floor(probe.maxX), (int) Math.floor(probe.maxY), (int) Math.floor(probe.maxZ)
        )) {
            double overlap = evaluateOverlapVolumeWithBlock(probe, pos, bestOverlapArray);
            if (overlap > bestOverlap) {
                bestOverlap = overlap;
                if (bestPos == null) bestPos = new BlockPos.MutableBlockPos();
                bestPos.set(pos);
            }
        }
        return bestPos == null ? null : bestPos.immutable();
    }
    private double evaluateOverlapVolumeWithBlock(AABB probe, BlockPos pos, double[] bestOverlapArray) {
        if (!level().isLoaded(pos)) return -1;

        BlockState state = level().getBlockState(pos);
        if (state.isCollisionShapeFullBlock(level(), pos))
            return evaluateOverlapVolume(probe,
                    pos.getX() + 0, pos.getY() + 0, pos.getZ() + 0,
                    pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1
            );

        double bestOverlap = -1;

        VoxelShape shape = state.getCollisionShape(level(), pos, CollisionContext.of(this));
        if (shape.isEmpty()) return bestOverlap;

        bestOverlapArray[0] = bestOverlap;
        shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> {
            double overlap = evaluateOverlapVolume(probe,
                    x1 + pos.getX(), y1 + pos.getY(), z1 + pos.getZ(),
                    x2 + pos.getX(), y2 + pos.getY(), z2 + pos.getZ()
            );
            if (overlap > bestOverlapArray[0]) bestOverlapArray[0] = overlap;
        });
        return bestOverlapArray[0];
    }
    private double evaluateOverlapVolume(AABB probe, double x1, double y1, double z1, double x2, double y2, double z2) {
        if (!probe.intersects(x1, y1, z1, x2, y2, z2)) return -1;
        double overlapX = Math.min(probe.maxX, x2) - Math.max(probe.minX, x1),
               overlapY = Math.min(probe.maxY, y2) - Math.max(probe.minY, y1),
               overlapZ = Math.min(probe.maxZ, z2) - Math.max(probe.minZ, z1);
        return overlapX * overlapY * overlapZ;
    }

    void updateGravity(Vector3d velocity) {
        velocity.add(0, -getGravity(), 0);
    }

    void updateFriction(Vector3d velocity) {
        if (!this.onGround()) return;
        // hack in a little less friction
        float friction = Mth.lerp(0.5F, this.getBlockStateOn().getFriction(this.level(), this.getOnPos(), this), 1.0F);
        velocity.mul(friction, 1.0F, friction);
    }

    void updateVelocityFromFluid(Vector3d velocity) {
        if (!this.isInFluidType()) return;

        FluidType fluid = this.getMaxHeightFluidType();
        double fluidHeight = this.getFluidTypeHeight(fluid);
        if (fluid.isAir()) return;

        // the amount that we are inside this fluid
        double displacedVolume = this.getBbWidth() * this.getBbWidth() * Math.clamp(fluidHeight, 0, this.getBbHeight());
        double buoyantForce = displacedVolume * fluid.getDensity() * getGravity();
        // really just used to scale the buoyant force
        double approximateMass = this.getBbWidth() * this.getBbWidth() * this.getBbHeight() * 900;

        velocity.add(0, buoyantForce / approximateMass, 0);

        // not exactly correct but close enough
        double fluidDrag = Mth.map(fluid.getViscosity(), 0, 1000, 1.0, 0.95);
        velocity.mul(fluidDrag, fluidDrag, fluidDrag);
    }

    void updateEntityCollisions(Vector3d velocity) {
        if (this.isStickyAttached()) return;
        Set<Entity> nextEntityCollisions = new HashSet<>(4);
        EntityRetrievalUtil.getEntities(this.level(), this.getBoundingBox(), Predicates.alwaysTrue()).stream()
                .sorted(Comparator.comparingDouble(this::distanceToSqr))
                .forEachOrdered(entity -> this.collideWithEntity(entity, velocity, nextEntityCollisions));
        lastEntityCollisions.clear();
        lastEntityCollisions.addAll(nextEntityCollisions);
    }

    private final Vector3d scratchRelativeVelocity = new Vector3d();
    void collideWithEntity(Entity entity, Vector3d velocity, Set<Entity> collisions) {
        // treat entities as capsules and ourselves a sphere
        double entityHeight = entity.getBbHeight();
        double entityRadius = Math.min(entity.getBbWidth() * 0.5, entityHeight * 0.5);
        double upperSphereColliderHeight = entity.getY() + entityHeight - entityRadius,
               lowerSphereColliderHeight = entity.getY() + entityRadius;

        double radius = this.radius();
        double centerY = this.getY() + radius;

        double combinedRadius = radius + entityRadius;

        boolean hit = false;
        double normalX = 0, normalY = 0, normalZ = 0;
        if (centerY > upperSphereColliderHeight || centerY < lowerSphereColliderHeight) {
            // cap sphere
            double sphereHeight = centerY > upperSphereColliderHeight ? upperSphereColliderHeight : lowerSphereColliderHeight;
            double diffX = this.getX() - entity.getX(),
                   diffY = centerY - sphereHeight,
                   diffZ = this.getZ() - entity.getZ();
            double length = Mth.length(diffX, diffY, diffZ);

            if (length <= combinedRadius && length != 0) {
                normalX = diffX / length;
                normalY = diffY / length;
                normalZ = diffZ / length;
                hit = true;
            }
        } else {
            // cylinder
            double diffX = this.getX() - entity.getX(),
                   diffZ = this.getZ() - entity.getZ();
            double length = Mth.length(diffX, diffZ);

            if (length <= combinedRadius && length != 0) {
                normalX = diffX / length;
                normalZ = diffZ / length;
                hit = true;
            }
        }

        if (hit) {
            // don't keep hitting the same entity over and over
            if (!lastEntityCollisions.contains(entity)) {
                // get the relative velocity
                Vec3 entityVelocity = entity.getDeltaMovement();
                scratchRelativeVelocity.set(velocity).sub(entityVelocity.x(), entityVelocity.y(), entityVelocity.z());

                double velocityTowardsEntity = Math.abs(scratchRelativeVelocity.dot(normalX, normalY, normalZ));
                playEntityCollisionSound((float) velocityTowardsEntity);
                velocity.reflect(normalX, normalY, normalZ);
                velocity.mul(this.elasticCoefficient());

                this.onHitEntity(new EntityHitResult(entity, this.position().add(normalX * -radius, normalY * -radius, normalZ * -radius)));
            }
            collisions.add(entity);
        }
    }

    double elasticCoefficient() {
        if (this.hasModifier(ClinkerOrdnanceModifierTypes.BOUNCY.get())) return 0.995;
        if (this.hasModifier(ClinkerOrdnanceModifierTypes.STICKY.get())) return 0.0;
        return 0.5;
    }
    double radius() { return this.getBbHeight() * 0.5; }
    @Override protected double getDefaultGravity() { return 0.06; }
    @Override public float maxUpStep() { return 0.2F; }
    @Override public boolean canBeCollidedWith() { return false; }
    @Override public boolean isPushable() { return !this.isStickyAttached(); }
    @Override
    public void push(Entity entity) {
        if (!this.isPassengerOfSameVehicle(entity)) {
            if (!entity.noPhysics && !this.noPhysics) {
                double d0 = entity.getX() - this.getX();
                double d1 = entity.getZ() - this.getZ();
                double d2 = Mth.absMax(d0, d1);
                if (d2 >= 0.01F) {
                    d2 = Math.sqrt(d2);
                    d0 /= d2;
                    d1 /= d2;
                    double d3 = 1.0 / d2;
                    if (d3 > 1.0) {
                        d3 = 1.0;
                    }

                    d0 *= d3;
                    d1 *= d3;
                    d0 *= 0.05F;
                    d1 *= 0.05F;
                    if (!this.isVehicle() && this.isPushable()) {
                        this.push(-d0 * 2, 0.0, -d1 * 2);
                    }
                }
            }
        }
    }

    // hit reactions
    double blockHitSpeedThreshold() {
        if (this.hasModifier(ClinkerOrdnanceModifierTypes.BOUNCY.get())) return 0.08;
        return 0.06;
    }
    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (canStickyAttach() && !this.isStickyAttached()) {
            this.stickToBlock(result.getBlockPos());
        }
        if (this.hasModifier(ClinkerOrdnanceModifierTypes.UNSTABLE.get()))
            this.detonate();
    }
    void playBlockCollisionSound(float speed) {
        double speedThreshold = blockHitSpeedThreshold();
        if (speed < speedThreshold) return;

        float volume = 0.5F * (float) Mth.clampedMap(speed, speedThreshold, speedThreshold * 2, 0, 1);
        float pitch = 0.6F * (random.nextFloat() + 0.5F);

        this.level().playSound(null, this.getX(), this.getY() + this.radius(), this.getZ(), getBlockCollisionSound(), this.getSoundSource(), volume, pitch);
    }
    SoundEvent getBlockCollisionSound() {
        if (this.getModifiers().hasModifier(ClinkerOrdnanceModifierTypes.STICKY.get()))
            return ClinkerSounds.ORDNANCE_BOUNCE_STICKY.get();
        if (this.getModifiers().hasModifier(ClinkerOrdnanceModifierTypes.BOUNCY.get()))
            return ClinkerSounds.ORDNANCE_BOUNCE_BOUNCY.get();
        return ClinkerSounds.ORDNANCE_BOUNCE.get();
    }

    double entityHitSpeedThreshold() { return 0.3; }
    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Vec3 relativeVelocity = this.getDeltaMovement().subtract(result.getEntity().getDeltaMovement());
        double velocityTowardsEntity = relativeVelocity.length();
        if (velocityTowardsEntity > entityHitSpeedThreshold()) {
            result.getEntity().hurt(
                    this.damageSources().source(DamageTypes.MOB_ATTACK, this, this.getOwner()),
                    (float) Mth.clampedMap(velocityTowardsEntity, 0, 0.8, 0, 3)
            );
        }

        // disable attaching to entities for now
        // it's just a little too janky
        // todo: this!
        if (true) return;
        if (canStickyAttach() && (!this.isStickyAttached() || this.getStickyAttachedEntity() == null)) {
            this.unstick();
            this.stickToEntity(result.getEntity());
        }
    }
    void playEntityCollisionSound(float speed) {
        double hitSpeedThreshold = entityHitSpeedThreshold(),
               baseSpeedThreshold = hitSpeedThreshold * 0.5;
        if (speed < baseSpeedThreshold) return;

        float volume = 0.2F;
        float pitch = 0.6F * (random.nextFloat() + 0.5F);

        if (this.hasModifier(ClinkerOrdnanceModifierTypes.THORNED.get()))
            this.level().playSound(null, this.getX(), this.getY() + this.radius(), this.getZ(), SoundEvents.THORNS_HIT, this.getSoundSource(), volume, pitch);
        if (speed > baseSpeedThreshold)
            this.level().playSound(null, this.getX(), this.getY() + this.radius(), this.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, this.getSoundSource(), volume, pitch);
        if (speed > hitSpeedThreshold)
            this.level().playSound(null, this.getX(), this.getY() + this.radius(), this.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, this.getSoundSource(),  volume, pitch);
    }

    // interaction
    @Override
    public @org.jetbrains.annotations.Nullable ItemStack getPickResult() {
        ItemStack itemStack = ClinkerItems.ORDNANCE.toStack();
        itemStack.set(ClinkerDataComponents.ORDNANCE_MODIFIERS.get(), this.modifiers);
        if (this.hasFuse()) {
            itemStack.set(ClinkerDataComponents.FUSE_TIMER.get(), new FuseTimer(this.getFuseTime(), this.level()));
        }
        return itemStack;
    }
    @Override
    public float getPickRadius() { return (float) this.radius(); }
    @Override
    public boolean isPickable() { return true; }
    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!this.level().isClientSide()) {
            ItemStack asItem = this.getPickResult();
            ItemHandlerHelper.giveItemToPlayer(player, asItem, player.getInventory().selected);
            this.remove(RemovalReason.DISCARDED);
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide());
    }
    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.isInvulnerableTo(pSource)) {
            return false;
        } else {
            Entity entity = pSource.getDirectEntity();
            if (entity != null) {
                if (entity instanceof OrdnanceEntity) return false;
                if (!this.level().isClientSide) {
                    Vec3 velocityAddend = entity.getDeltaMovement().add(entity.getLookAngle().scale(0.2));
                    this.addDeltaMovement(velocityAddend);
                    this.setOwner(entity);
                    this.level().playSound(null, this.position().x(), this.position().y(), this.position().z(), SoundEvents.TRIDENT_HIT, this.getSoundSource(), 0.5F, 1.0F);
                    this.unstick();
                }
                this.markHurt();
                return true;
            } else {
                return false;
            }
        }
    }

    // fuse stuff
    public boolean canDetonate() {
        return this.hasModifier(ClinkerTags.OrdnanceModifiers.DETONATES) &&
                this.hasModifier(ClinkerTags.OrdnanceModifiers.CAUSES_DETONATION);
    }
    public void detonate() {
        OrdnanceHelper.detonate(this.modifiers, this.getX(), this.getY() + this.radius(), this.getZ(), this.level(), this, this.getOwner());
    }

    void updateFuse() {
        FuseTimeModifier modifier = this.modifiers.getModifier(ClinkerOrdnanceModifierTypes.FUSE_TIME.get());
        if (modifier == null) return;

        if (this.level().isClientSide()) {
            this.createFuseParticles();
            // todo: 'fuse pulse'
            if (this.getFuseTime() < modifier.getFuseTicks()) {
                // create a fuse sound if it should be playing
                if (this.fuseSound == null) {
                    this.fuseSound = new OrdnanceFuseSoundInstance(this, this.getMaxFuseTime(), () -> (float)this.getFuseTime());
                    Minecraft.getInstance().getSoundManager().play(this.fuseSound);
                }
            } else {
                // and remove it if it shouldn't...
                if (this.fuseSound != null) {
                    this.fuseSound.stopPlaying();
                    this.fuseSound = null;
                }
            }
            return;
        }

        int newFuseTime = this.getFuseTime() + 1;
        this.setFuseTime(newFuseTime);
        // we should probably detonate now.
        if (newFuseTime == modifier.getFuseTicks()) {
            if (this.canDetonate()) {
                detonate();
            } else {
                // dud...
                this.level().playSound(null,
                        this.getX(), this.getY(), this.getZ(),
                        SoundEvents.SQUID_SQUIRT, this.getSoundSource(),
                        0.5F, 1.8F
                );
            }
        }
    }
    public boolean hasFuse() {
        FuseTimeModifier modifier = this.modifiers.getModifier(ClinkerOrdnanceModifierTypes.FUSE_TIME.get());
        if (modifier == null) return false;

        int fuseTime = this.getFuseTime();
        int maxFuseTime = modifier.getFuseTicks();
        return fuseTime < maxFuseTime;
    }
    public int getMaxFuseTime() {
        FuseTimeModifier modifier = this.modifiers.getModifier(ClinkerOrdnanceModifierTypes.FUSE_TIME.get());
        if (modifier == null) return Integer.MAX_VALUE;
        return modifier.getFuseTicks();
    }
    public void setFuseTime(int time) { this.entityData.set(DATA_FUSE_TIME, time); }
    public int getFuseTime() { return this.entityData.get(DATA_FUSE_TIME); }
    private final Vector3d fuseParticlePos = new Vector3d(), lastFuseParticlePos = new Vector3d();
    public void createFuseParticles() {
        if (!this.hasFuse()) return;

        lastFuseParticlePos.set(fuseParticlePos);
        // get the fuse position
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();

        double radius = this.radius();
        fuseParticlePos.set(camera.up);
        fuseParticlePos.rotateAxis(this.spin, camera.forwards.x(), camera.forwards.y(), camera.forwards.z());
        fuseParticlePos.add(camera.forwards.x() * 0.25, camera.forwards.y() * 0.25, camera.forwards.z() * 0.25);
        fuseParticlePos.mul(radius).add(this.getX(), this.getY() + radius, this.getZ());

        // dont spawn particles frame one
        if (this.tickCount <= 1) return;

        // spawn particles, accounting for distance travelled between frames...
        double distance = lastFuseParticlePos.distance(fuseParticlePos);
        double distanceBetweenParticles = 0.1;
        int count = Math.clamp((int) Math.ceil(distance / distanceBetweenParticles), 1, 10);
        for (int i = 0; i < count; i++) {
            float factor = (float) i / count;
            double x = Mth.lerp(factor, lastFuseParticlePos.x, fuseParticlePos.x),
                    y = Mth.lerp(factor, lastFuseParticlePos.y, fuseParticlePos.y),
                    z = Mth.lerp(factor, lastFuseParticlePos.z, fuseParticlePos.z);
            this.level().addParticle(
                    new OrdnanceTrailParticle.Options(this.gradient, 2.0F + (float) (random.nextGaussian() * 0.3F)),
                    x, y, z,
                    random.nextGaussian() * 0.01F, Math.abs(random.nextGaussian()) * 0.01F * 2, random.nextGaussian() * 0.01F
            );
        }
    }

    // sticky stuff
    public boolean canStickyAttach() {
        return this.getModifiers().hasModifier(ClinkerOrdnanceModifierTypes.STICKY.get());
    }
    void serializeStickyAttachment(CompoundTag tag) {
        CompoundTag stickyAttachmentInfo = new CompoundTag();

        boolean attached = this.isStickyAttached();
        stickyAttachmentInfo.putBoolean("StickyAttached", attached);

        // we're not attached, no need to save extra data
        if (!attached) {
            tag.put("StickyAttachmentInfo", stickyAttachmentInfo);
            return;
        }

        Vector3f stickyAttachmentOffset = entityData.get(DATA_STICKY_OFFSET);
        stickyAttachmentInfo.putFloat("StickyAttachmentOffsetX", stickyAttachmentOffset.x);
        stickyAttachmentInfo.putFloat("StickyAttachmentOffsetY", stickyAttachmentOffset.y);
        stickyAttachmentInfo.putFloat("StickyAttachmentOffsetZ", stickyAttachmentOffset.z);

        Entity stickyAttachedEntity = this.getStickyAttachedEntity();
        if (this.getStickyAttachedEntity() != null) {
            stickyAttachmentInfo.putUUID("StickyAttachedEntityUUID", stickyAttachedEntity.getUUID());
        } else if (entityData.get(DATA_STICKY_BLOCK).isPresent()) {
            BlockPos attachedPos = entityData.get(DATA_STICKY_BLOCK).get();
            CompoundTag stickyAttachedBlockPos = new CompoundTag();
            stickyAttachedBlockPos.putInt("X", attachedPos.getX());
            stickyAttachedBlockPos.putInt("Y", attachedPos.getY());
            stickyAttachedBlockPos.putInt("Z", attachedPos.getZ());
            stickyAttachmentInfo.put("StickyAttachedBlockPos", stickyAttachedBlockPos);
        } else {
            // invalid state
            return;
        }

        tag.put("StickyAttachmentInfo", stickyAttachmentInfo);
    }
    void deserializeStickyAttachment(CompoundTag tag) {
        if (!tag.contains("StickyAttachmentInfo")) {
            unstick();
            return;
        }

        CompoundTag stickyAttachmentInfo = tag.getCompound("StickyAttachmentInfo");
        boolean attached = stickyAttachmentInfo.getBoolean("StickyAttached");
        entityData.set(DATA_STICKY_ATTACHED, attached);

        // we're not attached, no need to read extra data
        if (!attached) {
            unstick();
            return;
        }

        float attachedX = stickyAttachmentInfo.getFloat("StickyAttachmentOffsetX"),
              attachedY = stickyAttachmentInfo.getFloat("StickyAttachmentOffsetY"),
              attachedZ = stickyAttachmentInfo.getFloat("StickyAttachmentOffsetZ");
        entityData.set(DATA_STICKY_OFFSET, new Vector3f(attachedX, attachedY, attachedZ));

        if (stickyAttachmentInfo.contains("StickyAttachedEntityUUID")) {
            if (this.level() instanceof ServerLevel serverLevel) {
                Entity stickyAttachedEntity = serverLevel.getEntity(stickyAttachmentInfo.getUUID("StickyAttachedEntityUUID"));
                if (stickyAttachedEntity != null)
                    entityData.set(DATA_STICKY_ENTITY_ID, OptionalInt.of(stickyAttachedEntity.getId()));
            }
        } else if (stickyAttachmentInfo.contains("StickyAttachedBlockPos")) {
            CompoundTag stickyAttachedBlockPos = stickyAttachmentInfo.getCompound("StickyAttachedBlockPos");
            entityData.set(DATA_STICKY_BLOCK, Optional.of(
                    new BlockPos(
                            stickyAttachedBlockPos.getInt("X"),
                            stickyAttachedBlockPos.getInt("Y"),
                            stickyAttachedBlockPos.getInt("Z")
                    )
            ));
        } else {
            // invalid state
            unstick();
        }
    }
    void updateStickyAttachment() {
        if (!isStickyAttached()) return;

        // shared behavior: stuck stuff shouldn't move!
        this.setDeltaMovement(0, 0, 0);
        Vector3f offset = this.entityData.get(DATA_STICKY_OFFSET);
        this.noPhysics = true;

        if (this.entityData.get(DATA_STICKY_ENTITY_ID).isPresent()) {
            // check entity
            Entity entity = this.getStickyAttachedEntity();
            if (entity == null || invalidStickTarget(entity)) {
                unstick();
                return;
            }
            this.setDeltaMovement(entity.getDeltaMovement());
            this.setPos(entity.position().add(offset.x, offset.y, offset.z));
        } else if (this.entityData.get(DATA_STICKY_BLOCK).isPresent()) {
            // check block
            BlockPos attachedPos = this.entityData.get(DATA_STICKY_BLOCK).get();
            this.setPos(attachedPos.getX() + offset.x, attachedPos.getY() + offset.y, attachedPos.getZ() + offset.z);

            if (!this.level().isLoaded(attachedPos)) return;

            BlockState attachedBlockState = this.level().getBlockState(attachedPos);
            // recheck every hundred ticks, or when the block state changes!
            if (this.cachedStickyBlockState != attachedBlockState || this.tickCount % 100 == 0) {
                this.cachedStickyBlockState = attachedBlockState;
                boolean intersects = false;
                AABB box = this.getBoundingBox()
                        .move(-attachedPos.getX(), -attachedPos.getY(), -attachedPos.getZ())
                        .inflate(2.0 / 16.0);
                if (attachedBlockState.isCollisionShapeFullBlock(level(), attachedPos)) {
                    intersects = box.intersects(0, 0, 0, 1, 1, 1);
                } else {
                    VoxelShape shape = attachedBlockState.getCollisionShape(level(), attachedPos, CollisionContext.of(this));
                    if (!shape.isEmpty()) {
                        // big allocation but whatever
                        List<AABB> aabbs = attachedBlockState.getCollisionShape(level(), attachedPos, CollisionContext.of(this)).toAabbs();
                        for (AABB aabb : aabbs) {
                            if (box.intersects(aabb)) {
                                intersects = true; break;
                            }
                        }
                    }
                }
                if (!intersects)
                    unstick();
            }
        } else {
            // invalid state: we're "sticky" but not attached to anything.
            unstick();
        }
    }
    public boolean isStickyAttached() {
        return this.getEntityData().get(DATA_STICKY_ATTACHED);
    }
    public boolean invalidStickTarget(Entity entity) {
        return entity == null ||
               entity.level() != this.level() ||
               entity.isRemoved() ||
               entity.isSpectator() ||
               (entity instanceof LivingEntity living && living.isDeadOrDying());
    }
    @Nullable
    public Entity getStickyAttachedEntity() {
        if (this.entityData.get(DATA_STICKY_ENTITY_ID).isEmpty()) return null;
        return this.level().getEntity(this.entityData.get(DATA_STICKY_ENTITY_ID).getAsInt());
    }
    public void stickToEntity(Entity entity) {
        if (invalidStickTarget(entity)) return;
        Clinker.LOGGER.info("Stuck to entity! {}", entity.getName());

        this.getEntityData().set(DATA_STICKY_ATTACHED, true);
        this.getEntityData().set(DATA_STICKY_ENTITY_ID, OptionalInt.of(entity.getId()));
        this.getEntityData().set(DATA_STICKY_OFFSET, new Vector3f((float) (this.getX() - entity.getX()), (float) (this.getY() - entity.getY()), (float) (this.getZ() - entity.getZ())));
    }
    public void stickToBlock(BlockPos pos) {
        if (pos == null) return;
        Clinker.LOGGER.info("Stuck to block! {}", pos);

        this.getEntityData().set(DATA_STICKY_ATTACHED, true);
        this.getEntityData().set(DATA_STICKY_BLOCK, Optional.of(pos));
        this.getEntityData().set(DATA_STICKY_OFFSET, new Vector3f((float) (this.getX() - pos.getX()), (float) (this.getY() - pos.getY()), (float) (this.getZ() - pos.getZ())));
    }
    public void unstick() {
        this.getEntityData().set(DATA_STICKY_ATTACHED, false);
        if (this.getEntityData().get(DATA_STICKY_ENTITY_ID).isPresent()) this.getEntityData().set(DATA_STICKY_ENTITY_ID, OptionalInt.empty());
        if (this.getEntityData().get(DATA_STICKY_BLOCK).isPresent()) this.getEntityData().set(DATA_STICKY_BLOCK, Optional.empty());
        this.getEntityData().set(DATA_STICKY_OFFSET, new Vector3f(0));
        this.cachedStickyBlockState = null;
        this.noPhysics = false;
    }


    public OrdnanceModifierSet getModifiers() { return modifiers; }
    public void setModifiers(OrdnanceModifierSet modifiers) {
        this.modifiers = modifiers;
        this.modifierCache = new OrdnanceModifierCache(modifiers);
        this.gradient = modifiers.gradient();
    }
    public boolean hasModifier(OrdnanceModifierType<?> type) { return modifierCache.hasType(type); }
    public boolean hasModifier(TagKey<OrdnanceModifierType<?>> type) { return modifierCache.hasType(type); }

    // classes
    // cache for quickly evaluating if we contain a type or tag
    protected static class OrdnanceModifierCache {
        final OrdnanceModifierSet set;
        final Object2BooleanMap<OrdnanceModifierType<?>> hasTypeMap = new Object2BooleanOpenHashMap<>();
        final Object2BooleanMap<TagKey<OrdnanceModifierType<?>>> hasKeyMap = new Object2BooleanOpenHashMap<>();

        private OrdnanceModifierCache(OrdnanceModifierSet set) { this.set = set; }

        public boolean hasType(OrdnanceModifierType<?> type) {
            return hasTypeMap.computeIfAbsent(type, val -> set.hasModifier(type));
        }
        public boolean hasType(TagKey<OrdnanceModifierType<?>> type) {
            return hasKeyMap.computeIfAbsent(type, val -> set.hasModifier(type));
        }
    }
}
