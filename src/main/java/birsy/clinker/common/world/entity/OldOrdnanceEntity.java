package birsy.clinker.common.world.entity;

import birsy.clinker.client.render.particle.OrdnanceExplosionParticle;
import birsy.clinker.client.render.particle.OrdnanceTrailParticle;
import birsy.clinker.client.sound.OrdnanceSoundInstance;
import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.networking.packet.ClientboundOrdnanceExplosionPacket;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.entity.ClinkerEntities;
import birsy.clinker.core.registry.ClinkerParticles;
import birsy.clinker.core.registry.ClinkerSounds;
import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.VectorUtils;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.PointLight;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.awt.*;
import java.util.Optional;

@Deprecated
public class OldOrdnanceEntity extends Projectile {
    private static final Vec3[] PARTICLE_POINTS = MathUtils.generateSpherePoints(500);
    private static final EntityDataAccessor<Integer> DATA_FUSE_TIME = SynchedEntityData.defineId(OldOrdnanceEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_MAX_FUSE_TIME = SynchedEntityData.defineId(OldOrdnanceEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_DEFLECTION = SynchedEntityData.defineId(OldOrdnanceEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> DATA_STUCK = SynchedEntityData.defineId(OldOrdnanceEntity.class, EntityDataSerializers.BOOLEAN);
    Optional<Entity> stuckEntity = Optional.empty();
    private Vec3 stuckEntityOffset = Vec3.ZERO;

    @OnlyIn(Dist.CLIENT)
    private OrdnanceSoundInstance sound;
    @OnlyIn(Dist.CLIENT)
    private Vec3 previousVelocity = Vec3.ZERO;
    @OnlyIn(Dist.CLIENT)
    private float spin, pSpin;
    @OnlyIn(Dist.CLIENT)
    public PointLight light;

    int deflectionTime;

    float damageMultiplier = 1.0F;
    float elasticity = 0.5F;

    public OldOrdnanceEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static OldOrdnanceEntity create(Level pLevel, double x, double y, double z) {
        OldOrdnanceEntity entity = new OldOrdnanceEntity(ClinkerEntities.ORDNANCE.get(), pLevel);
        entity.setPos(x, y, z);
        pLevel.addFreshEntity(entity);
        return entity;
    }

    public static OldOrdnanceEntity toss(Level pLevel, LivingEntity thrower) {
        OldOrdnanceEntity entity = new OldOrdnanceEntity(ClinkerEntities.ORDNANCE.get(), pLevel);
        entity.setOwner(thrower);
        entity.shootFromRotation(thrower, thrower.getXRot(), thrower.getYRot(), 0.0F, 0.8F, 0.0F);
        entity.setPos(thrower.getEyePosition().add(entity.getDeltaMovement().normalize()));
        return entity;
    }

    public static OldOrdnanceEntity fireAtPosition(Level pLevel, Vec3 currentPosition, Vec3 targetPosition, int timeInTicks) {
        OldOrdnanceEntity entity = OldOrdnanceEntity.create(pLevel, currentPosition.x, currentPosition.y, currentPosition.z);
        double timeSquared = timeInTicks * timeInTicks;
        Vec3 delta = targetPosition.subtract(currentPosition);
        Vec3 acceleration = new Vec3(0, -0.024, 0);

        double velocityX = (delta.x - (0.5 * acceleration.x * timeSquared)) / (double)timeInTicks;
        double velocityY = (delta.y - (0.5 * acceleration.y * timeSquared)) / (double)timeInTicks;
        double velocityZ = (delta.z - (0.5 * acceleration.z * timeSquared)) / (double)timeInTicks;
        entity.setDeltaMovement(velocityX, velocityY, velocityZ);

        return entity;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (this.level().isClientSide) {
            if (VeilRenderSystem.renderer().getDeferredRenderer().isEnabled()) {
                this.light = new PointLight();
                this.light.setRadius(1.0F);
                this.light.setColor(0, 0, 0);
                this.light.setPosition(this.getX(), this.getY(), this.getZ());
                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.light);
            }
        }
    }

    @Override
    public void onClientRemoval() {
        super.onClientRemoval();
        if (this.level().isClientSide) {
            if (VeilRenderSystem.renderer().getDeferredRenderer().isEnabled()) {
                if (this.light != null) VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.light);
            }
        }
    }

    @Override
    public void remove(RemovalReason p_146834_) {
        super.remove(p_146834_);
        if (this.level().isClientSide) {
            if (VeilRenderSystem.renderer().getDeferredRenderer().isEnabled()) {
                if (this.light != null) VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.light);
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_FUSE_TIME, 0);
        this.entityData.define(DATA_MAX_FUSE_TIME, 6 * 20);
        this.entityData.define(DATA_DEFLECTION, false);
        this.entityData.define(DATA_STUCK, false);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Fuse Time", this.getFuseTime());
        pCompound.putInt("Max Fuse Time", this.getMaxFuseTime());
   }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setFuseTime(pCompound.getInt("Fuse Time"));
        this.setMaxFuseTime(pCompound.getInt("Max Fuse Time"));
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.noPhysics) {
            Vec3 position = this.position();
            Vec3 velocity = this.getDeltaMovement();

            FluidState state = this.level().getFluidState(this.blockPosition());
            float drag = 0.99F;
            float gravity = -0.024F;
            if (!state.isEmpty()) {
                drag = 0.9F;
                gravity *= -0.1;
            }

            velocity = velocity.scale(drag);
            velocity = velocity.add(0, gravity, 0);

            Vec3 endPosition = position.add(velocity);

            BlockHitResult blockHitResult = this.level().clip(new ClipContext(position, endPosition, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(this.level(), this, position, endPosition, this.getBoundingBox().expandTowards(velocity).inflate(1.0D), this::canHitEntity);

            float bounceStrength = elasticity;
            if (blockHitResult.getType() == HitResult.Type.BLOCK) {
                Vec3 normal = new Vec3(blockHitResult.getDirection().getStepX(), blockHitResult.getDirection().getStepY(), blockHitResult.getDirection().getStepZ());
                if (normal.distanceTo(new Vec3(0, 1, 0)) < 0.01) {
                    //extra little force to prevent goofy bouncing
                    velocity = velocity.subtract(0, gravity, 0);
                }
                velocity = VectorUtils.reflect(normal, velocity).scale(bounceStrength);
                this.level().gameEvent(GameEvent.PROJECTILE_LAND, blockHitResult.getBlockPos(), GameEvent.Context.of(this, this.level().getBlockState(blockHitResult.getBlockPos())));
                this.onHitBlock(blockHitResult);
            } else if (entityHitResult != null) {
                if (entityHitResult.getType() == HitResult.Type.ENTITY) {
                    entityHitResult.getEntity().hurt(this.damageSources().mobProjectile(this, (LivingEntity) this.getOwner()), 3.0F);
                    velocity = velocity.scale(-bounceStrength * 0.2F);
                    this.level().gameEvent(GameEvent.PROJECTILE_LAND, entityHitResult.getLocation(), GameEvent.Context.of(this, null));
                    this.onHitEntity(entityHitResult);
                }
            }

            this.previousVelocity = this.getDeltaMovement();
            if (this.isStuck()) {
                velocity = Vec3.ZERO;
                if (this.stuckEntity.isPresent()) {
                    this.setPos(this.stuckEntity.get().position().add(this.stuckEntityOffset));
                }
            }

            this.setDeltaMovement(velocity);

            Vec3 finalPos = position.add(velocity);
            this.setPos(finalPos);
        }

        this.incrementFuseTime();
        if (this.getFuseTime() > this.getMaxFuseTime()) {
            this.detonate();
        }
        if (this.isOnFire()) {
            this.detonate();
        }

        if (this.level().isClientSide) {
            this.updateSpin();
            // wait a few ticks, so the particles dont get in your face.
            // todo : make this independent of fuse time
            if (this.getFuseTime() > 5) {
                Vector3f baseColor = new Vector3f(1.0f, 1.0f, 0.5f);
                Vector3f smokeColor =  new Vector3f(0.8f, 0.8f, 0.8f);

                float horizontalSpeed = 0.01F;
                float spaceBetweenParticles = 0.1F;
                Vec3 direction = this.getPosition(0).subtract(this.getPosition(1));
                Vec3 offset = direction.normalize().scale(spaceBetweenParticles);
                Vec3 particlePosition = this.getPosition(1);
                for (float distance = 0; distance < direction.length(); distance+=spaceBetweenParticles) {
                    particlePosition = particlePosition.add(offset);
                    this.level().addParticle(new OrdnanceTrailParticle.Options(baseColor, smokeColor, Mth.abs((float) random.nextGaussian()) * 1.5F), particlePosition.x(), particlePosition.y(), particlePosition.z(),
                            random.nextGaussian() * horizontalSpeed, Mth.abs((float) random.nextGaussian()) * horizontalSpeed * 2, random.nextGaussian() * horizontalSpeed);
                }
            }

            if (this.sound == null) {
                this.sound = new OrdnanceSoundInstance(this);
                Minecraft.getInstance().getSoundManager().play(this.sound);
            }
        }

        this.setDeflected(deflectionTime > 0);
        this.deflectionTime--;
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        this.level().gameEvent(GameEvent.PROJECTILE_LAND, pResult.getLocation(), GameEvent.Context.of(this, null));
        this.playCollisionSound(1, 0.4F);
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        this.level().gameEvent(GameEvent.PROJECTILE_LAND, pResult.getLocation(), GameEvent.Context.of(this, null));
        this.playCollisionSound(1, 0.6F);
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

    public float getSpin(float partialTicks) {
        return Mth.lerp(partialTicks, this.pSpin, this.spin);
    }

    @Override
    protected boolean canHitEntity(Entity pTarget) {
        // if we're currently stuck on something, we can't hit any entities.
        if (this.isStuck()) return false;
        return true;
    }
    public boolean isPickable() {
        return true;
    }
    public float getPickRadius() {
        return 1.0F;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.isInvulnerableTo(pSource)) {
            return false;
        } else {
            Entity entity = pSource.getDirectEntity();
            boolean isDeflection = false;
            if (entity != null) {
                this.setStuck(false);

                if (entity instanceof OldOrdnanceEntity) return false;

                if (!this.level().isClientSide) {
                    Vec3 vec3 = entity.getLookAngle();
                    isDeflection = this.getDeltaMovement().length() > 0.1 && entity instanceof LivingEntity;
                    if (isDeflection) Clinker.LOGGER.info("PARRY");
                    this.setDeltaMovement(this.getDeltaMovement().add(vec3.scale(isDeflection ? 0.8 : 0.5)));
                    this.setOwner(entity);
                    this.level().playSound(null, this.position().x(), this.position().y(), this.position().z(), SoundEvents.TRIDENT_HIT, SoundSource.BLOCKS, isDeflection ? 1.5F : 0.5F, isDeflection ? 0.5F : 1.0F);
                    this.setDeflected(isDeflection);
                    this.deflectionTime = 2;
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

    @Override
    public boolean mayInteract(Level pLevel, BlockPos pPos) {
        return true;
    }

    public void detonate() {
        createOrdnanceExplosion(this.position().add(0, this.getBbHeight() * 0.5, 0), this.level(), this.getOwner(), this);
        this.discard();
    }

    public static void createOrdnanceExplosion(Vec3 location, Level level, @Nullable Entity thrower, @Nullable OldOrdnanceEntity ordnance) {
        float radius = 5F;

        if (ordnance != null) {
            if (level.isClientSide) {
                if (VeilRenderSystem.renderer().getDeferredRenderer().isEnabled()) {
                    if (ordnance.light != null) VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(ordnance.light);
                }
            }
        }

        if (level.isClientSide) {
            for (Vec3 particlePoint : PARTICLE_POINTS) {
                Vec3 velocity = particlePoint.normalize();
                velocity = velocity.scale(radius + (level.random.nextGaussian() * 0.1F));

                Vector3f baseColor = new Vector3f(1.0f, 1.0f, 0.5f);
                Vector3f smokeColor =  new Vector3f(0.8f, 0.8f, 0.8f);
                Color color = Color.getHSBColor(level.random.nextFloat(), 0.5F, 1.0F);

                level.addParticle(new OrdnanceExplosionParticle.Options(baseColor, smokeColor, Mth.abs((float)  level.random.nextGaussian()) * 3.0F), location.x(), location.y(), location.z(),
                        velocity.x(), velocity.y, velocity.z);
            }

            level.addParticle(ParticleTypes.FLASH, true, location.x(), location.y(), location.z(), 0, 0, 0);
            level.addParticle(ClinkerParticles.EXPLOSION_LIGHT.get(), true, location.x(), location.y(), location.z(), 0, 0, 0);

            level.playLocalSound(location.x(), location.y(), location.z(), ClinkerSounds.ORDNANCE_EXPLODE.get(), SoundSource.BLOCKS, 3F, Mth.lerp(level.random.nextFloat(), 0.4F, 0.6F), false);
            level.playLocalSound(location.x(), location.y(), location.z(), SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, SoundSource.BLOCKS, 4F, Mth.lerp(level.random.nextFloat(), 0.7F, 0.9F), false);
            level.playLocalSound(location.x(), location.y(), location.z(), SoundEvents.FIREWORK_ROCKET_TWINKLE_FAR, SoundSource.BLOCKS, 0.1F, Mth.lerp(level.random.nextFloat(), 0.7F, 0.9F), false);
        } else {
            ClinkerPacketHandler.sendToClientsTrackingChunk(level.getChunkAt(BlockPos.containing(location)), new ClientboundOrdnanceExplosionPacket(location));
        }

        for (Entity entity : level.getEntities(ordnance, new AABB(location.subtract(radius, radius, radius), location.add(radius, radius, radius)))) {
            // if you're outside of its radius you're ok.
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

    public void setFuseTime(int time) {
        this.entityData.set(DATA_FUSE_TIME, time);
    }

    public int getFuseTime() {
        return this.entityData.get(DATA_FUSE_TIME);
    }

    public void incrementFuseTime() {
        this.setFuseTime(this.getFuseTime() + 1);
    }

    public void setMaxFuseTime(int time) {
        this.entityData.set(DATA_MAX_FUSE_TIME, time);
    }

    public int getMaxFuseTime() {
        return this.entityData.get(DATA_MAX_FUSE_TIME);
    }

    public boolean isDeflected() {
        return this.entityData.get(DATA_DEFLECTION);
    }

    public void setDeflected(boolean deflected) {
        this.entityData.set(DATA_DEFLECTION, deflected);
    }

    public boolean isStuck() {
        return this.entityData.get(DATA_STUCK);
    }

    public void setStuck(boolean stuck) {
        this.entityData.set(DATA_STUCK, stuck);
    }

    public void stickToEntity(Entity entity) {
        this.stuckEntity = Optional.of(entity);
        this.stuckEntityOffset = this.position().subtract(entity.position());
        this.setStuck(true);
    }

    public void setElasticity(float elasticity) {

    }
}
