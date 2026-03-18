package birsy.clinker.common.world.entity;

import birsy.clinker.client.entity.slabcrab.SlabCrabAnimator;
import birsy.clinker.client.entity.slabcrab.SlabCrabSkeleton;
import birsy.clinker.common.world.entity.ai.LookTargetController;
import birsy.clinker.common.world.entity.ai.behaviors.LocomotorLookAtTarget;
import birsy.clinker.core.Clinker;
import foundry.veil.api.client.necromancer.SkeletonParent;
import foundry.veil.api.client.necromancer.animation.Animator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;

import java.util.List;

public class SlabCrabEntity extends GroundLocomotionEntity implements SmartBrainOwner<SlabCrabEntity>, SkeletonParent<SlabCrabEntity, SlabCrabSkeleton> {

    public SlabCrabEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.1F);
    }

    // ai
    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    protected void customServerAiStep() {
        tickBrain(this);
        super.customServerAiStep();
    }

    @Override
    protected void updateBaseBodyRotation() {
        super.updateBaseBodyRotation();

        BlockPos targetPos = this.getNavigation().getTargetPos();
        if (targetPos != null && Mth.length(this.locomotionVector.x, this.locomotionVector.z) > 0.05F) {
            double dX = (targetPos.getX() + 0.5) - this.getX(),
                   dZ = (targetPos.getZ() + 0.5) - this.getZ();
            double lateralDistanceToTarget = Mth.length(dX, dZ);

            float desiredAngle = this.getSyncedBodyRotation();
            if (lateralDistanceToTarget > 0.1F) {
                dX /= lateralDistanceToTarget; dZ /= lateralDistanceToTarget; // normalize

                float angleToTarget = (float) Mth.atan2(-dX, dZ) * Mth.RAD_TO_DEG;
                float angleDifference = Mth.degreesDifference(this.getSyncedBodyRotation(), angleToTarget + 90);
                if (Math.abs(angleDifference) < 90) {
                    desiredAngle = angleToTarget + 90;
                } else {
                    desiredAngle = angleToTarget - 90;
                }
            }
            baseBodyRotationHandle.face(0, Mth.rotLerp(0.25F, this.getSyncedBodyRotation(), desiredAngle));
            return;
        }

        // stop interpolating direction if there's no target or we're not fast enough.
        baseBodyRotationHandle.face(0.0F, this.getSyncedBodyRotation());
    }

    @Override
    public List<ExtendedSensor<SlabCrabEntity>> getSensors() {
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<>(),
                new NearbyPlayersSensor<>(),
                new HurtBySensor<>()
        );
    }

    @Override
    public BrainActivityGroup<SlabCrabEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LocomotorLookAtTarget<>(),
                new MoveToWalkTarget<>()
        );
    }

    @Override
    public BrainActivityGroup<SlabCrabEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<AiTestEntity>(
                        new SetPlayerLookTarget<>(),
                        new SetRandomLookTarget<>()
                ),
                new OneRandomBehaviour<>(
                        new SetRandomWalkTarget<>(),
                        new Idle<>().runFor(mob -> mob.getRandom().nextInt(30, 60))
                )
        );
    }

    // make them act like a solid
    @Override public boolean canBeCollidedWith() {
        return this.isAlive();
    }
    @Override public void push(Entity entity) {}

    SlabCrabSkeleton skeleton;
    SlabCrabAnimator animator;
    @Override public void setSkeleton(SlabCrabSkeleton skeleton) { this.skeleton = skeleton; }
    @Override public SlabCrabSkeleton getSkeleton() { return this.skeleton; }
    @Override public void setAnimator(Animator<SlabCrabEntity, SlabCrabSkeleton> animator) { this.animator = (SlabCrabAnimator) animator; }
    @Override public SlabCrabAnimator getAnimator() { return animator; }
}
