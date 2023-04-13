package birsy.clinker.common.world.entity.salamander;

import birsy.clinker.core.registry.ClinkerEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class SalamanderHeadEntity extends AbstractSalamanderPartEntity {
    private static final EntityDataAccessor<Boolean> IS_DECAPITATED_STUMP_ID = SynchedEntityData.defineId(SalamanderHeadEntity.class, EntityDataSerializers.BOOLEAN);

    public SalamanderHeadEntity(EntityType<? extends AbstractSalamanderPartEntity> entity, Level level) {
        super(entity, level);
        this.setSegmentID(0);
    }

    public void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_DECAPITATED_STUMP_ID, false);
    }

    public boolean isDecaptiatedStump() {
        return this.entityData.get(IS_DECAPITATED_STUMP_ID);
    }
    public void setDecaptiatedStump(boolean isStump) {
        this.entityData.set(IS_DECAPITATED_STUMP_ID, isStump);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.MOVEMENT_SPEED, 0.15D).add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        CompoundTag behindSegmentsTag = new CompoundTag();
        if (behindSegments != null) {
            for (int i = 0; i < behindSegments.size(); i++) {
                CompoundTag segmentTag = new CompoundTag();
                SalamanderBodyEntity segment = behindSegments.get(i);
                if (segment != null) {
                    segment.saveWithoutId(segmentTag);
                    behindSegmentsTag.put("Segment " + i, segmentTag);
                }
            }
        }

        pCompound.put("Body Segments", behindSegmentsTag);
        pCompound.putBoolean("Decapitated Stump", this.isDecaptiatedStump());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        CompoundTag behindSegmentsTag = pCompound.getCompound("Body Segments");
        int length = behindSegmentsTag.size();
        this.behindSegments = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            SalamanderBodyEntity segment = ClinkerEntities.SALAMANDER_BODY.get().create(this.level);
            segment.load(behindSegmentsTag.getCompound("Segment " + i));
            segment.head = this;
            this.level.addFreshEntity(segment);

            getSegmentFromIndex(i - 1).behind = segment;
            segment.ahead = getSegmentFromIndex(i - 1);

            this.behindSegments.add(segment);
        }

        this.setDecaptiatedStump(pCompound.getBoolean("Decapitated Stump"));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isDeadOrDying()) {
            this.applyRopeConstraints(16);
        }
        /*if (this.tickCount % 35 == 0 || this.random.nextInt(90) == 0) {
            Vec3 viewDirection = this.getViewVector(1.0F); //new Vec3(Mth.cos(this.getBodyRotY(1.0F)) * Mth.cos(this.getBodyRotX(1.0F)), Mth.sin(this.getBodyRotY(1.0F)) * Mth.cos(this.getBodyRotX(1.0F)), Mth.sin(this.getBodyRotX(1.0F)));
            Vec3 randomVector = new Vec3(random.nextDouble(), random.nextDouble(), random.nextDouble()).subtract(0.5, 0.5, 0.5).scale(2).normalize();
            Vec3 particleLocation = this.position().add(0, 0.5, 0).add(viewDirection.scale(0.9F)).add(0.0F, 0.0F, 0.0F);
            Vec3 particleColor = new Vec3(100.0F, 160.0F, 190.0F).scale(1.0F / 255.0F);
            this.level.addParticle(ClinkerParticles.SNOOZE.get(), particleLocation.x(), particleLocation.y(), particleLocation.z(), particleColor.x(), particleColor.y(), particleColor.z());
        }*/
        if (!this.isDecaptiatedStump()) {
            //this.travel(new Vec3(Mth.sin(tickCount * 0.07F) * 0.25, 0, 1).normalize().scale(6.5F));
        }
    }


    public void split(int segmentIndex) {
        if (!this.level.isClientSide()) {
            SalamanderBodyEntity frontSegment = (SalamanderBodyEntity) this.getSegmentFromIndex(segmentIndex + 1);
            if (frontSegment != null) {
                SalamanderHeadEntity newHead = frontSegment.turnToHead();
                this.level.addFreshEntity(newHead);

                newHead.behindSegments = this.behindSegments.subList(segmentIndex + 2, this.behindSegments.size());
                int newIndex = 0;
                int newLength = this.behindSegments.size() - (segmentIndex + 2);
                for (SalamanderBodyEntity segment : newHead.behindSegments) {
                    segment.setSegmentID(newIndex + 1);
                    segment.setBodyLength(newLength + 1);
                    segment.head = newHead;
                    newIndex++;
                }

                this.behindSegments = this.behindSegments.subList(0, segmentIndex);
                this.behindSegments.get(this.behindSegments.size() - 1).behind = null;
            }
        }
    }

    @Override
    public void checkDespawn() {
        super.checkDespawn();
    }

    @Override
    public boolean onClimbable() {
        return this.isInWater() || super.onClimbable();
    }

    @Override
    public void setPortalCooldown() {
        super.setPortalCooldown();
        for (SalamanderBodyEntity segment : behindSegments) {
            segment.setPortalCooldownBody();
        }
    }

    @Nullable
    @Override
    public Entity changeDimension(ServerLevel level, ITeleporter teleporter) {
        for (SalamanderBodyEntity segment : behindSegments) {
            segment.changeDimensionBody(level, teleporter);
        }
        return super.changeDimension(level, teleporter);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        float difficulty = pDifficulty.getEffectiveDifficulty() / 3.0F;
        int salamanderLength = 6;
        while (salamanderLength < 24) {
            if (pLevel.getRandom().nextFloat() < difficulty) {
                salamanderLength++;
            } else {
                break;
            }
        }
        this.setOriginalSegmentID(0);
        this.setSegmentID(0);
        this.setBodyLength(salamanderLength + 1);
        this.setOriginalBodyLength(salamanderLength + 1);

        double minCircleRadius = (salamanderLength + 1) / (Math.PI * 2);

        int tailLength = 1 + pLevel.getRandom().nextInt(2);
        boolean hasLegs = true;
        for (int i = 0; i < salamanderLength; i++) {
            SalamanderBodyEntity segment = ClinkerEntities.SALAMANDER_BODY.get().create(this.level);

            segment.head = this;

            segment.setLegs(hasLegs);
            hasLegs = !hasLegs;

            segment.setSegmentID(i + 1);
            segment.setOriginalSegmentID(i + 1);
            segment.setBodyLength(salamanderLength + 1);
            segment.setOriginalBodyLength(salamanderLength + 1);
            float posAlongRadius = (float) (((i + 1.0F) / (salamanderLength + 1.0F)) * (2.0F * Math.PI));
            segment.setPos(this.position().add(Mth.cos(posAlongRadius) * minCircleRadius, 0.0, Mth.sin(posAlongRadius) * minCircleRadius));//.add(/*Mth.sin(i * 0.25F) * 2*/ 0, 0, -(i + 1) * (segment.segmentLength)));

            pLevel.addFreshEntityWithPassengers(segment);

            getSegmentFromIndex(i - 1).behind = segment;
            segment.ahead = getSegmentFromIndex(i - 1);

            segment.setTailAmount(Mth.clamp(i - (salamanderLength - 3), 0, 2));

            this.behindSegments.add(segment);
        }
        this.setPos(this.position().add(1 * minCircleRadius, 0.0, 0 * minCircleRadius));
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    public boolean hurtFromBody(SalamanderBodyEntity part, int segmentID, DamageSource pSource, float pAmount) {
        if (!pSource.isFall()) {
            return super.hurt(DamageSource.STARVE, pAmount);
        } else {
            return false;
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (behindSegments != null) {
            for (SalamanderBodyEntity bodySegment : behindSegments) {
                bodySegment.hurtFromHead(pSource, 0.0F);
            }
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        super.travel(pTravelVector);
        if (behindSegments != null) {
            for (int segmentIndex = 0; segmentIndex < behindSegments.size(); segmentIndex++) {
                AbstractSalamanderPartEntity segment = this.getSegmentFromIndex(segmentIndex);
                AbstractSalamanderPartEntity aheadSegment = this.getSegmentFromIndex(segmentIndex - 1);

                segment.travel(aheadSegment.position().subtract(segment.position()).normalize().scale(pTravelVector.length()));
            }
        }
    }



    @Override
    protected void playStepSound(BlockPos pPos, BlockState pBlock) {}
}
