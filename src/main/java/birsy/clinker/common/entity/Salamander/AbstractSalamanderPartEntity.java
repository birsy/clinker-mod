package birsy.clinker.common.entity.Salamander;

import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSalamanderPartEntity extends Monster {
    public float segmentLength = 1.0F;
    public List<SalamanderBodyEntity> behindSegments;
    public AbstractSalamanderPartEntity ahead;
    public SalamanderBodyEntity behind;
    int airTime;
    private static final EntityDataAccessor<Integer> SEGMENT_ID_ID = SynchedEntityData.defineId(AbstractSalamanderPartEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ORIGINAL_SEGMENT_ID_ID = SynchedEntityData.defineId(AbstractSalamanderPartEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> BODY_LENGTH_ID = SynchedEntityData.defineId(AbstractSalamanderPartEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ORIGINAL_BODY_LENGTH_ID = SynchedEntityData.defineId(AbstractSalamanderPartEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> HAS_LEGS_ID = SynchedEntityData.defineId(AbstractSalamanderPartEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Float> X_BODY_ROT_ID = SynchedEntityData.defineId(AbstractSalamanderPartEntity.class, EntityDataSerializers.FLOAT);
    private float pXBodyRot;
    private static final EntityDataAccessor<Float> Y_BODY_ROT_ID = SynchedEntityData.defineId(AbstractSalamanderPartEntity.class, EntityDataSerializers.FLOAT);
    private float pYBodyRot;

    protected AbstractSalamanderPartEntity(EntityType<? extends AbstractSalamanderPartEntity> entity, Level level) {
        super(entity, level);
        this.maxUpStep = 1.0F;
        this.behindSegments = new ArrayList<>();
        airTime = 0;
    }

    public void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(X_BODY_ROT_ID, 0.0F);
        this.entityData.define(Y_BODY_ROT_ID, 0.0F);
        this.entityData.define(SEGMENT_ID_ID, 0);
        this.entityData.define(ORIGINAL_SEGMENT_ID_ID, 0);
        this.entityData.define(BODY_LENGTH_ID, 0);
        this.entityData.define(ORIGINAL_BODY_LENGTH_ID, 0);
        this.entityData.define(HAS_LEGS_ID, true);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Segment ID", this.getSegmentID());
        pCompound.putInt("Original Segment ID", this.getOriginalSegmentID());
        pCompound.putInt("Body Length", this.getBodyLength());
        pCompound.putInt("Original Body Length", this.getOriginalBodyLength());
        pCompound.putBoolean("Legs", this.hasLegs());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setSegmentID(pCompound.getInt("Segment ID"));
        this.setOriginalSegmentID(pCompound.getInt("Original Segment ID"));
        this.setBodyLength(pCompound.getInt("Body Length"));
        this.setOriginalBodyLength(pCompound.getInt("Original Body Length"));
        this.setLegs(pCompound.getBoolean("Legs"));
    }



    public void setSegmentID(int id) {
        this.entityData.set(SEGMENT_ID_ID, id);
    }
    public void setOriginalSegmentID(int id) {
        this.entityData.set(ORIGINAL_SEGMENT_ID_ID, id);
    }
    public int getSegmentID() {
        return this.entityData.get(SEGMENT_ID_ID);
    }
    public int getOriginalSegmentID() {
        return this.entityData.get(ORIGINAL_SEGMENT_ID_ID);
    }

    public void setBodyLength(int bodyLength) {
        this.entityData.set(BODY_LENGTH_ID, bodyLength);
    }
    public void setOriginalBodyLength(int bodyLength) {
        this.entityData.set(ORIGINAL_BODY_LENGTH_ID, bodyLength);
    }
    public int getBodyLength() {
        return this.entityData.get(BODY_LENGTH_ID);
    }
    public int getOriginalBodyLength() {
        return this.entityData.get(ORIGINAL_BODY_LENGTH_ID);
    }

    public void setLegs(boolean hasLegs) {
        this.entityData.set(HAS_LEGS_ID, hasLegs);
    }
    public boolean hasLegs() {
        return this.entityData.get(HAS_LEGS_ID);
    }

    public float getBodyRotX(float partialTick) {
        return Mth.rotLerp(partialTick, pXBodyRot, this.entityData.get(X_BODY_ROT_ID));
    }

    public void setBodyRotX(float rotation) {
        this.entityData.set(X_BODY_ROT_ID, rotation);
    }

    public float getBodyRotY(float partialTick) {
        return Mth.rotLerp(partialTick, pYBodyRot, this.entityData.get(Y_BODY_ROT_ID));
    }

    public void setBodyRotY(float rotation) {
        this.entityData.set(Y_BODY_ROT_ID, rotation);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        this.pXBodyRot = this.entityData.get(X_BODY_ROT_ID);
        this.pYBodyRot = this.entityData.get(Y_BODY_ROT_ID);
        if (!this.isOnGround() && !this.onClimbable()) {
            airTime++;
        } else {
            airTime = 0;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level.isClientSide()) {
            Vec3 dir1 = Vec3.ZERO;
            float count = 0.0F;
            if (ahead != null) {
                dir1 = this.position().subtract(ahead.position()).reverse();
                count++;
            }
            Vec3 dir2 = Vec3.ZERO;
            if (behind != null) {
                dir2 = this.position().subtract(behind.position());
                count++;
            }
            Vec3 average = dir1.add(dir2).scale(1.0F / Math.max(count, 1));
            this.face(this.position().add(average));
        }

        //this.setYBodyRot(0.0F);
    }

    //Iterative solver - adjusts segment positions to ensure that they keep their distance
    public void applyRopeConstraints(int iterations) {
        if (behindSegments != null) {
            Vec3[] desiredPositions = Util.make(new Vec3[behindSegments.size() + 1], (array) -> {
                for (int segmentIndex = -1; segmentIndex < behindSegments.size(); segmentIndex++) {
                    array[segmentIndex + 1] = this.getSegmentFromIndex(segmentIndex).position();
                }
            });

            for (int iteration = 0; iteration < iterations; iteration++) {
                for (int posIndex = 0; posIndex < desiredPositions.length - 1; posIndex++) {
                    AbstractSalamanderPartEntity segment = this.getSegmentFromIndex(posIndex - 1);
                    Vec3 segmentAPos = desiredPositions[posIndex];
                    Vec3 segmentBPos = desiredPositions[posIndex + 1];

                    Vec3 center = segmentAPos.add(segmentBPos).multiply(0.5, 0.5, 0.5);
                    Vec3 direction = segmentAPos.subtract(segmentBPos).normalize();
                    desiredPositions[posIndex] = center.add(direction.scale(segment.segmentLength * 0.5F));
                    desiredPositions[posIndex + 1] = center.add(direction.reverse().scale(segment.segmentLength * 0.5F));
                }
            }

            for (int segmentIndex = -1; segmentIndex < behindSegments.size(); segmentIndex++) {
                AbstractSalamanderPartEntity segment = this.getSegmentFromIndex(segmentIndex);
                segment.moveWithoutClamping(MoverType.SELF, desiredPositions[segmentIndex + 1].subtract(segment.position()));
            }
        }
    }

    public AbstractSalamanderPartEntity getSegmentFromIndex(int segmentIndex) {
        if (segmentIndex < -1 || segmentIndex > behindSegments.size() - 1) {
            return null;
        }

        return segmentIndex == -1 ? this : behindSegments.get(segmentIndex);
    }


    public void face(Vec3 position) {
        if (!this.level.isClientSide()) {
            double xDirection = position.x() - this.getX();
            double yDirection = position.y() - this.getY();
            double zDirection = position.z() - this.getZ();

            double magnitude = Math.sqrt(xDirection * xDirection + zDirection * zDirection);
            double yaw = (Mth.atan2(zDirection, xDirection) * (180F / Math.PI)) - 90.0F;
            double pitch = -(Mth.atan2(yDirection, magnitude) * (180F / Math.PI));

            this.setBodyRotX((float) pitch);
            this.setXRot((float) pitch);
            this.setYBodyRot((float) yaw);
            this.setBodyRotY((float) yaw);
        }
    }

    @Override
    public boolean canCollideWith(Entity pEntity) {
        if (pEntity instanceof AbstractSalamanderPartEntity) {
            return false;
        } else {
            return super.canCollideWith(pEntity);
        }
    }

    @Override
    public void push(Entity pEntity) {
        if (!(pEntity instanceof AbstractSalamanderPartEntity)) {
            super.push(pEntity);
        } else {
            if (this.position().distanceToSqr(pEntity.position()) < 0.5) {
                super.push(pEntity);
            }
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        super.travel(pTravelVector);
        //Uses "(airTime < 10)" rather than this.isOnGround() to ensure that they don't slow down when climbing up terrain.
        if (this.isEffectiveAi() && ((airTime < 10) || this.isInWater() || this.isNoGravity())) {
            this.moveRelative(0.08F, pTravelVector);
        }
    }

    @Override
    public void move(MoverType pType, Vec3 pPos) {
        if (!this.isDeadOrDying()) {
            double segLengthSqr = (this.segmentLength * this.segmentLength);
            Vec3 resultingPos = this.position().add(0.0, pPos.y(), 0.0);
            Vec3 adjustedPos = pPos;
            if (behind != null) {
                double distToPrevSegmentSqr = resultingPos.distanceToSqr(behind.position());
                if (distToPrevSegmentSqr > segLengthSqr) {
                    double distToPrevSegment = Math.sqrt(distToPrevSegmentSqr);
                    double difference = distToPrevSegment - segmentLength;
                    adjustedPos = pPos.normalize().scale(pPos.length() - difference);
                }
            } else if (ahead != null) {
                Vec3 falseBehind = this.position().add(this.position().subtract(ahead.position()).reverse());
                double distToNextSegmentSqr = resultingPos.distanceToSqr(falseBehind);
                if (distToNextSegmentSqr > segLengthSqr) {
                    double distToNextSegment = Math.sqrt(distToNextSegmentSqr);
                    double difference = distToNextSegment - segmentLength;
                    adjustedPos = pPos.normalize().scale(pPos.length() - difference);
                }
            }

            super.move(pType, new Vec3(pPos.x(), adjustedPos.y(), pPos.z()));
        } else {
            super.move(pType, pPos);
        }
    }

    public void moveWithoutClamping(MoverType pType, Vec3 pPos) {
        super.move(pType, pPos);
    }

    @Override
    public float getEyeHeight(Pose pPose) {
        return this.getDimensions(pPose).height * 0.5F;
    }
}
