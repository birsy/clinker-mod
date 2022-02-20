package birsy.clinker.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class GravitationalEntity extends Mob {
    private static final double GRAVITATIONAL_CONSTANT = 0;
    private static final double TIME_STEP = 1.0;

    private static final EntityDataAccessor<Float> MASS = SynchedEntityData.defineId(GravitationalEntity.class, EntityDataSerializers.FLOAT);

    public Vec3 initialVelocity = new Vec3(2, 0, 2);

    protected GravitationalEntity(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.setDeltaMovement(initialVelocity);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 100.0D).add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MASS, 1.0F);
    }

    @Override
    public void tick() {
        this.baseTick();

        for (Entity entity : this.level.getEntities(this, this.getBoundingBox().inflate(50))) {
            if (entity instanceof GravitationalEntity && entity != this) {
                double otherMass = ((GravitationalEntity) entity).getMass();

                double dist = this.position().distanceTo(entity.position());
                Vec3 forceDirection = (this.position().subtract(entity.position())).normalize();
                Vec3 force = forceDirection.multiply(GRAVITATIONAL_CONSTANT, GRAVITATIONAL_CONSTANT, GRAVITATIONAL_CONSTANT)
                        .multiply(this.getMass(), this.getMass(), this.getMass())
                        .multiply(otherMass, otherMass, otherMass)
                        .multiply(1 / dist, 1 / dist, 1 / dist);
                Vec3 acceleration = force.multiply(1 /this.getMass(), 1 /this.getMass(), 1 /this.getMass());
                this.setDeltaMovement(acceleration.multiply(TIME_STEP, TIME_STEP, TIME_STEP));
            }
        }

        this.setPos(this.getPosition(1.0F).add(this.getDeltaMovement()));
    }

    public float getMass() {
        return this.getEntityData().get(MASS);
    }

    public void setMass(float mass) {
        this.getEntityData().set(MASS, mass);
    }
}
