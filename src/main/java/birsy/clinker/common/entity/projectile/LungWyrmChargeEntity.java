package birsy.clinker.common.entity.projectile;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.entity.ClinkerEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class LungWyrmChargeEntity extends Projectile implements ItemSupplier {
    // this is synced so client prediction works better
    private static final EntityDataAccessor<BlockPos> DATA_TARGET_POS = SynchedEntityData.defineId(LungWyrmChargeEntity.class, EntityDataSerializers.BLOCK_POS);

    public LungWyrmChargeEntity(EntityType<? extends LungWyrmChargeEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    public static LungWyrmChargeEntity create(Level level) {
        return new LungWyrmChargeEntity(ClinkerEntities.LUNGWYRM_CHARGE.get(), level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_TARGET_POS, new BlockPos(0,0,0));
    }
    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        BlockPos targetPos = getTargetPos();
        compound.putInt("TargetX", targetPos.getX());
        compound.putInt("TargetY", targetPos.getY());
        compound.putInt("TargetZ", targetPos.getZ());
    }
    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setTargetPos(
                new BlockPos(compound.getInt("TargetX"), compound.getInt("TargetY"), compound.getInt("TargetZ"))
        );
    }
    public BlockPos getTargetPos() { return this.entityData.get(DATA_TARGET_POS); }
    public void setTargetPos(BlockPos pos) { this.entityData.set(DATA_TARGET_POS, pos); }

    @Override
    public void tick() {
        super.tick();
        Vec3 targetPos = this.getTargetPos().getBottomCenter();

        // if we're below the target pos, just keep going in the same direction
        if (this.getY() > targetPos.y()) {
            Vec3 desiredVelocity = targetPos.subtract(this.position());
            desiredVelocity = desiredVelocity.normalize().scale(2);

            Vec3 velocity = this.getDeltaMovement();
            float acceleration = 0.03F;
            this.setDeltaMovement(
                    Mth.approach((float) velocity.x(), (float) desiredVelocity.x(), acceleration),
                    Mth.approach((float) velocity.y(), (float) desiredVelocity.y(), acceleration),
                    Mth.approach((float) velocity.z(), (float) desiredVelocity.z(), acceleration)
            );
        }

        this.level().addParticle(
                ParticleTypes.POOF,
                this.getX(), this.getY(), this.getZ(),
                0, 0, 0
        );

        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        this.move(MoverType.SELF, this.getDeltaMovement());
        if (hitresult.getType() != HitResult.Type.MISS) onHit(hitresult);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        // todo: detonate
        this.discard();
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.DRAGON_FIREBALL_EXPLODE, this.getSoundSource(), 3.0F, 1.0F);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.FLASH,
                    this.getX(), this.getY(), this.getZ(),
                    1, 0, 0, 0, 0
            );
        }
    }

    @Override
    public ItemStack getItem() {
        return Items.FIRE_CHARGE.getDefaultInstance();
    }
}
