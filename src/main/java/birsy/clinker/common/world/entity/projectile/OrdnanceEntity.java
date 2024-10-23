package birsy.clinker.common.world.entity.projectile;

import birsy.clinker.client.particle.OrdnanceExplosionParticle;
import birsy.clinker.client.particle.OrdnanceTrailParticle;
import birsy.clinker.client.sound.OrdnanceSoundInstance;
import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.networking.packet.ClientboundOrdnanceExplosionPacket;
import birsy.clinker.core.registry.entity.ClinkerEntities;
import birsy.clinker.core.registry.ClinkerParticles;
import birsy.clinker.core.registry.ClinkerSounds;
import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.VectorUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class OrdnanceEntity extends Projectile implements IEntityWithComplexSpawn {
    private static final Vec3[] PARTICLE_POINTS = MathUtils.generateSpherePoints(500);

    private static final EntityDataAccessor<Integer> DATA_FUSE_TIME = SynchedEntityData.defineId(OrdnanceEntity.class, EntityDataSerializers.INT);

    protected OrdnanceEffects effects = OrdnanceEffects.DEFAULT_EFFECT_PARAMS;

    boolean stuck = false;
    Entity stuckEntity;
    float stuckEntityAngleOffset, stuckEntityHeightOffset;

    @OnlyIn(Dist.CLIENT)
    private OrdnanceSoundInstance sound;
    @OnlyIn(Dist.CLIENT)
    private float spin, pSpin;

    public OrdnanceEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        if (!pLevel.isClientSide()) {
            this.effects = new OrdnanceEffects (
                OrdnanceEffects.DetonationType.NORMAL,
                OrdnanceEffects.TouchType.NORMAL,
                Potions.EMPTY,
                false,
                false,
                pLevel.random.nextInt(0x000000, 0xFFFFFF),
                120
            );
        }
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
        entity.shootFromRotation(thrower, thrower.getXRot(), thrower.getYRot(), 0.0F, 0.8F, 0.0F);
        entity.setPos(thrower.getEyePosition().add(entity.getDeltaMovement().normalize()));
        return entity;
    }

    public static OrdnanceEntity fireAtPosition(Level pLevel, Vec3 currentPosition, Vec3 targetPosition, int timeInTicks) {
        OrdnanceEntity entity = OrdnanceEntity.create(pLevel, currentPosition.x, currentPosition.y, currentPosition.z);
        double timeSquared = timeInTicks * timeInTicks;
        Vec3 delta = targetPosition.subtract(currentPosition);
        Vec3 acceleration = new Vec3(0, -0.024, 0);

        double velocityX = (delta.x - (0.5 * acceleration.x * timeSquared)) / (double)timeInTicks;
        double velocityY = (delta.y - (0.5 * acceleration.y * timeSquared)) / (double)timeInTicks;
        double velocityZ = (delta.z - (0.5 * acceleration.z * timeSquared)) / (double)timeInTicks;
        entity.setDeltaMovement(velocityX, velocityY, velocityZ);

        return entity;
    }

    // networking & serialization
    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeNbt(this.effects.serialize(new CompoundTag()));
    }
    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
        this.effects = OrdnanceEffects.deserialize(buffer.readNbt());
    }
    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_FUSE_TIME, 0);
    }
    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Fuse Time", this.getFuseTime());
        effects.serialize(pCompound);
   }
    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setFuseTime(pCompound.getInt("Fuse Time"));
        this.effects = OrdnanceEffects.deserialize(pCompound);
    }

    // behavior
    @Override
    public void tick() {
        super.tick();

        if (!this.noPhysics) this.tickPhysics();
        if (this.level().isClientSide) tickClient();

        this.incrementFuseTime();
        if (this.getFuseTime() > this.getMaxFuseTime() || this.isOnFire()) this.detonate();
    }

    public void tickPhysics() {
        Vec3 position = this.position();
        Vec3 velocity = this.getDeltaMovement();

        float drag = this.getDrag();
        float gravity = this.getGravity();

        if (this.stuck) {
            this.setDeltaMovement(0, 0, 0);
            if (this.stuckEntity != null) {
                position = stuckEntity.position();
                position = position.add(0, this.stuckEntityHeightOffset, 0);
                position = position.add(Mth.sin(stuckEntity.getYRot()))
                this.setPos();
            } else {

            }
            return;
        }

        velocity = velocity.scale(drag);
        velocity = velocity.add(0, gravity, 0);
        this.setDeltaMovement(velocity);

        Vec3 endPosition = position.add(velocity);

        BlockHitResult blockHitResult = this.level().clip(new ClipContext(position, endPosition, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(this.level(), this, position, endPosition, this.getBoundingBox().expandTowards(velocity).inflate(1.0D), this::canHitEntity);

        // only register whichever hit is closer...
        boolean hitBlock = blockHitResult.getType() == HitResult.Type.BLOCK;
        boolean hitEntity = entityHitResult != null;
        if (hitBlock && hitEntity) {
            boolean isBlockCloser = blockHitResult.getLocation().distanceTo(position) < entityHitResult.getLocation().distanceTo(position);
            hitBlock = isBlockCloser;
            hitEntity = !isBlockCloser;
        }

        if (hitBlock) {
            this.onHitBlock(blockHitResult);
        } else if (hitEntity) {
            this.onHitEntity(entityHitResult);
        }

        Vec3 finalPos = position.add(this.getDeltaMovement());
        this.setPos(finalPos);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);

        if (!this.stuck) pResult.getEntity().hurt(this.damageSources().mobProjectile(this, (LivingEntity) this.getOwner()), 3.0F);
        this.level().gameEvent(GameEvent.PROJECTILE_LAND, pResult.getLocation(), GameEvent.Context.of(this, null));
        this.playCollisionSound(1, 0.4F);

        switch (effects.touchType()) {
            case NORMAL:
                this.setDeltaMovement(this.getDeltaMovement().scale(-0.1F));
            case STICK:
                this.setDeltaMovement(Vec3.ZERO);
                this.stickToEntity(pResult.getEntity());
                break;
            case BOUNCE:
                this.setDeltaMovement(this.getDeltaMovement().scale(-0.95F));
                break;
            case DETONATE:
                this.detonate();
                break;
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);

        this.level().gameEvent(GameEvent.PROJECTILE_LAND, pResult.getLocation(), GameEvent.Context.of(this, null));

        Vec3 velocity = this.getDeltaMovement();

        Vec3 normal = new Vec3(pResult.getDirection().getStepX(), pResult.getDirection().getStepY(), pResult.getDirection().getStepZ());
        //extra little force to prevent goofy bouncing
        if (normal.distanceTo(new Vec3(0, 1, 0)) < 0.01) velocity = velocity.subtract(0, this.getGravity(), 0);
        velocity = VectorUtils.reflect(normal, velocity);

        switch (effects.touchType()) {
            case NORMAL:
                velocity = velocity.scale(0.5F);
                break;
            case STICK:
                this.stuck = true;
                velocity = velocity.scale(0.0F);
                break;
            case BOUNCE:
                velocity = velocity.scale(0.95F);
                break;
            case DETONATE:
                this.detonate();
                break;
        }

        if (Math.abs(velocity.y) > 0.01) this.playCollisionSound(1, 0.6F);

        this.setDeltaMovement(velocity);
    }

    protected void stickToEntity(Entity entity) {
        if (this.hurtMarked) return;

        this.stuck = true;
        this.stuckEntity = entity;
        Vec3 posOffset = entity.position().subtract(this.position());
        this.stuckEntityHeightOffset = (float) posOffset.y;

        posOffset = posOffset.normalize();
        float angleToEntity = (float) Mth.atan2(posOffset.z(), posOffset.x()) * Mth.RAD_TO_DEG;
        float entityAngle = entity.getYRot();

        this.stuckEntityAngleOffset = Mth.degreesDifference(angleToEntity, entityAngle);
    }

    protected float getGravity() {
        float gravity = -0.024F;
        if (this.isUnderWater()) gravity *= -0.1F;
        return gravity;
    }
    protected float getDrag() {
        float drag = 0.99F;
        if (this.isUnderWater()) drag = 0.9F;
        return drag;
    }

    @OnlyIn(Dist.CLIENT)
    protected void tickClient() {
        this.updateSpin();

        // wait a few ticks, so the particles don't get in your face.
        if (this.tickCount > 5) {
            Vector3f baseColor = Vec3.fromRGB24(effects.color()).toVector3f();
            Vector3f smokeColor =  new Vector3f(0.8f, 0.8f, 0.8f);

            float speed = 0.01F;
            float spaceBetweenParticles = 0.1F;
            Vec3 direction = this.getPosition(0).subtract(this.getPosition(1));
            Vec3 offset = direction.normalize().scale(spaceBetweenParticles);
            Vec3 particlePosition = this.getPosition(1);
            for (float distance = 0; distance < direction.length(); distance+=spaceBetweenParticles) {
                particlePosition = particlePosition.add(offset);
                this.level().addParticle(
                        new OrdnanceTrailParticle.Options(baseColor, smokeColor, 2.0F + (float) (random.nextGaussian() * 0.3F)),
                        particlePosition.x(), particlePosition.y(), particlePosition.z(),
                        random.nextGaussian() * speed, Math.abs(random.nextGaussian()) * speed * 2, random.nextGaussian() * speed
                );
            }
        }

        if (this.sound == null) {
            this.sound = new OrdnanceSoundInstance(this, this.getMaxFuseTime(), () -> (float)this.getFuseTime());
            Minecraft.getInstance().getSoundManager().play(this.sound);
        }
    }

    public void playCollisionSound(float volumeMultiplier, float pitchMultiplier) {
        Vec3 pos = this.position().add(0, this.getBbHeight() * 0.5, 0);
        float speed = (float) this.getDeltaMovement().length();
        float pitch = (random.nextFloat() + 0.5F) * (speed * 4 + 0.8F) * 0.6F;
        float volume = 0.5F;
        if (speed < 0.03) return;
        this.level().playSound(null, pos.x(), pos.y(), pos.z(), ClinkerSounds.ORDNANCE_BOUNCE.get(), SoundSource.BLOCKS, volume * volumeMultiplier, pitch * pitchMultiplier);
    }

    @OnlyIn(Dist.CLIENT)
    public void updateSpin() {
        this.pSpin = this.spin;
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 camPosition = camera.getPosition();
        Vec3 directionToCamera = this.position().subtract(camPosition).normalize();
        //spin less when you're facing it, so the rotation looks more natural.
        Vec3 deltaMovement = this.getPosition(0).subtract(this.getPosition(1));
        Vec3 axisOfRotation = deltaMovement.normalize().cross(new Vec3(0, 1, 0));
        float angleBasedSpinMultiplier = (float) axisOfRotation.dot(directionToCamera);

        //the circumference divided by the amount moved, so it appears like it's rolling.
        float spinAmount = (float) (deltaMovement.length() / (Mth.TWO_PI * 6.0F));
        float spinInRadians = spinAmount * Mth.TWO_PI * 3;

        this.spin += spinInRadians * angleBasedSpinMultiplier;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.isInvulnerableTo(pSource)) {
            return false;
        } else {
            Entity entity = pSource.getDirectEntity();
            boolean isDeflection = false;
            if (entity != null) {
                if (entity instanceof OrdnanceEntity) return false;

                if (!this.level().isClientSide) {
                    Vec3 vec3 = entity.getLookAngle();
                    isDeflection = this.getDeltaMovement().length() > 0.1 && entity instanceof LivingEntity;
                    this.stuck = false;
                    this.setDeltaMovement(this.getDeltaMovement().add(vec3.scale(isDeflection ? 0.8 : 0.5)));
                    this.setOwner(entity);
                    this.level().playSound(null, this.position().x(), this.position().y(), this.position().z(), SoundEvents.TRIDENT_HIT, SoundSource.BLOCKS, isDeflection ? 1.5F : 0.5F, isDeflection ? 0.5F : 1.0F);
                }

                if (!isDeflection) {
                    this.markHurt();
                }
                return true;
            } else {
                return false;
            }
        }
    }

    public void detonate() {
        createOrdnanceExplosion(this.position().add(0, this.getBbHeight() * 0.5, 0), this.level(), this.getOwner(), this, this.effects);
        this.discard();
    }

    public static void createOrdnanceExplosion(Vec3 location, Level level, @Nullable Entity thrower, @Nullable OrdnanceEntity ordnance, OrdnanceEffects effects) {
        float radius = 5F;

        if (level.isClientSide) {
            for (Vec3 particlePoint : PARTICLE_POINTS) {
                Vec3 velocity = particlePoint.normalize();
                velocity = velocity.scale(radius + (level.random.nextGaussian() * 0.1F));

                Vector3f baseColor = Vec3.fromRGB24(effects.color()).toVector3f();
                Vector3f smokeColor =  new Vector3f(0.8f, 0.8f, 0.8f);
                level.addParticle(new OrdnanceExplosionParticle.Options(baseColor, smokeColor, Mth.abs((float)  level.random.nextGaussian()) * 3.0F), location.x(), location.y(), location.z(),
                        velocity.x(), velocity.y, velocity.z);
            }

            level.addParticle(ParticleTypes.FLASH, true, location.x(), location.y(), location.z(), 0, 0, 0);
            level.addParticle(ClinkerParticles.EXPLOSION_LIGHT.get(), true, location.x(), location.y(), location.z(), 0, 0, 0);

            level.playLocalSound(location.x(), location.y(), location.z(), ClinkerSounds.ORDNANCE_EXPLODE.get(), SoundSource.BLOCKS, 3F, Mth.lerp(level.random.nextFloat(), 0.4F, 0.6F), false);
            level.playLocalSound(location.x(), location.y(), location.z(), SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, SoundSource.BLOCKS, 4F, Mth.lerp(level.random.nextFloat(), 0.7F, 0.9F), false);
            level.playLocalSound(location.x(), location.y(), location.z(), SoundEvents.FIREWORK_ROCKET_TWINKLE_FAR, SoundSource.BLOCKS, 0.1F, Mth.lerp(level.random.nextFloat(), 0.7F, 0.9F), false);
        } else {
            ClinkerPacketHandler.sendToClientsTrackingChunk(level.getChunkAt(BlockPos.containing(location)), new ClientboundOrdnanceExplosionPacket(location, effects));
        }

        for (Entity entity : level.getEntities(ordnance, new AABB(location.subtract(radius, radius, radius), location.add(radius, radius, radius)))) {
            // if you're outside its radius you're ok.
            if (entity.distanceToSqr(location) > radius * radius) {
                continue;
            }
            float scaledDistance = (float) (1 - (entity.position().distanceTo(location) / radius));
            float damage = Mth.lerp(Mth.sqrt(scaledDistance), 15, 32);
            Vec3 knockback = entity.position().add(0, entity.getBbHeight() * 0.5, 0).subtract(location).normalize().scale(Mth.sqrt(scaledDistance));
            DamageSource source = entity.damageSources().explosion(ordnance, thrower);//
            entity.hurt(source, damage);
            if (entity instanceof LivingEntity l) {
                if (l.isDamageSourceBlocked(source)) {
                    knockback = knockback.scale(8); //3
                }
            }
            entity.setOnGround(false);
            entity.setDeltaMovement(entity.getDeltaMovement().add(knockback));
        }
    }

    public float getSpin(float partialTicks) {
        return Mth.lerp(partialTicks, this.pSpin, this.spin);
    }

    public void setFuseTime(int time) {
        this.entityData.set(DATA_FUSE_TIME, time);
    }

    public int getFuseTime() {
        return this.entityData.get(DATA_FUSE_TIME);
    }

    public void incrementFuseTime() {
        this.setFuseTime(this.getFuseTime() + 1);
    }

    public int getMaxFuseTime() {
        return effects.maxFuseTime();
    }

    // misc stuff idk
    @Override
    public boolean isPickable() {
        return true;
    }
    @Override
    public float getPickRadius() {
        return 1.0F;
    }
    @Override
    public boolean mayInteract(Level pLevel, BlockPos pPos) {
        return true;
    }
}
