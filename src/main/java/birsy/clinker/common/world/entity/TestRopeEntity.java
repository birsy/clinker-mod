package birsy.clinker.common.world.entity;

import birsy.clinker.common.networking.packet.debug.ClientboundPathfindingDebugPacket;
import birsy.clinker.common.world.entity.ai.ClinkerSmoothGroundNavigation;
import birsy.clinker.common.world.entity.ai.behaviors.SetRandomWalkTargetCloseEnough;
import birsy.clinker.common.world.entity.rope.RopeEntity;
import birsy.clinker.common.world.entity.rope.RopeEntitySegment;
import birsy.clinker.core.registry.ClinkerParticles;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.network.PacketDistributor;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3d;

import java.util.List;

public class TestRopeEntity extends RopeEntity<RopeEntitySegment> implements SmartBrainOwner<TestRopeEntity> {

    public TestRopeEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        if (!pLevel.isClientSide) {
            int segmentCount = 18;
            this.addSegmentToEnd(SegmentType.HEAD.index(), false);
            for (int i = 0; i < segmentCount - 4; i++) {
                if (i % 3 == 0) {
                    this.addSegmentToEnd(SegmentType.BODY_LEGS.index(), false);
                } else {
                    this.addSegmentToEnd(SegmentType.BODY.index(), false);
                }
            }
            this.addSegmentToEnd(SegmentType.TAIL_START.index(), false);
            this.addSegmentToEnd(SegmentType.TAIL_MIDDLE.index(), false);
            this.addSegmentToEnd(SegmentType.TAIL_END.index(), false);
        }

    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new ClinkerSmoothGroundNavigation(this, pLevel, 1.5F);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            //spawnFireParticles(8);
        } else {
            this.debugMove();
        }
    }

    private final Quaternionf rotation = new Quaternionf();
    private final Vector3d position = new Vector3d(), velocity = new Vector3d();
    private void spawnFireParticles(int count) {
        rotation.set(0, 0, 0, 1).rotateY(this.getViewYRot(1.0F) * -Mth.DEG_TO_RAD).rotateX(this.getViewXRot(1.0F) * Mth.DEG_TO_RAD);

        for (int i = 0; i < count; i++) {
            float angle = this.random.nextFloat() * 2 * Mth.PI;
            float radius = this.random.nextFloat();
            radius = (float) Math.pow(radius, 3);
            float speed = (1 - radius)*0.05F + 0.1F;//MathUtils.map(0.1F, 0.2F, this.random.nextFloat());
            speed *= 3;
            float x = Mth.sin(angle), y = Mth.cos(angle);
            position.set(x, y, 0).normalize().mul(radius * 0.1F);
            velocity.set(x * radius*2, y * radius*2, -3).normalize().mul(speed);

            position.set(rotation.transform(position));
            velocity.set(rotation.transform(velocity));

            this.level().addParticle(ClinkerParticles.FIRE_SPEW.get(),
                    position.x + this.getX(), position.y + this.getY() + this.getEyeHeight(), position.z + this.getZ(),
                    velocity.x, velocity.y, velocity.z);
        }
    }

//    @Override
//    public void aiStep() {
//        super.aiStep();
//        this.debugMove();
//    }

    private void debugMove() {
        float maxSpeed = 2.0F;
        Player target = EntityRetrievalUtil.getNearestEntity(this, 40.0F, (entity -> entity instanceof Player));

        if (target == null) {
            this.moveTowardsPosition(this.getX(), this.getY(), this.getZ(), maxSpeed, 1.0);
            return;
        }

        // approach player
        if (target.getMainHandItem().is(Items.CARROT_ON_A_STICK)) {
            this.moveTowardsPosition(target.getX(), target.getY() + target.getEyeHeight(), target.getZ(), maxSpeed, 4);
            return;
        }
        // approach point
        if (target.getMainHandItem().is(Items.ARROW)) {
            BlockHitResult result = this.level().clip(new ClipContext(
                    target.getEyePosition(),
                    target.getEyePosition().add(target.getLookAngle().scale(40.0F)),
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    CollisionContext.empty()
            ));
            if (result.getType() == HitResult.Type.BLOCK) {
                Vec3i normal = result.getDirection().getNormal();
                float radius = this.segments.get(0).radius;
                this.moveTowardsPosition(result.getLocation().x() + normal.getX()*radius, result.getLocation().y() + normal.getY()*radius, result.getLocation().z() + normal.getZ()*radius, maxSpeed, 1.5);
                return;
            }
        }
        // strafe
        if (target.getMainHandItem().is(Items.STRING)) {
            Vec3 targetPos = target.getEyePosition();
            this.lookAt(EntityAnchorArgument.Anchor.EYES, targetPos);
            //Mth.cos(this.tickCount * 0.08F)
            this.getMoveControl().strafe(1.2F, Mth.clamp(Mth.sin(this.tickCount * 0.008F) * 10, -1, 1) * 1.5F);
        } else {
            this.moveTowardsPosition(this.getX(), this.getY(), this.getZ(), maxSpeed, 1.0);
        }

    }
    private void moveTowardsPosition(double x, double y, double z, double maxSpeed, double completionRadius) {
        this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(x, y, z));
        if (this.position().distanceToSqr(x, y, z) > completionRadius*completionRadius) {
            //this.getNavigation().moveTo(x, y, z, maxSpeed);
            this.getMoveControl().setWantedPosition(x, y, z, maxSpeed);
        } else {
            //this.getNavigation().stop();
            this.getMoveControl().setWantedPosition(this.getX(), this.getY(), this.getZ(), 0);
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
        float z = 0;
        for (RopeEntitySegment segment : this.segments) {
            segment.setInitialPosition(this.getX(), this.getY() + segment.radius, this.getZ() + z);
            z += segment.length;
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
    }

    @Override
    protected RopeEntitySegment createSegment(int segmentType, int index) {
        SegmentType type = SegmentType.fromIndex(segmentType);
        return switch (type) {
            case HEAD, BODY, BODY_LEGS -> new RopeEntitySegment(this, segmentType, 0.5F, 1.0F, index);
            case TAIL_START -> new RopeEntitySegment(this, segmentType, 0.5F * 0.85F, 1.0F, index);
            case TAIL_MIDDLE -> new RopeEntitySegment(this, segmentType, 4.0F / 16.0F, 1.0F, index);
            case TAIL_END -> new RopeEntitySegment(this, segmentType, 2.5F / 16.0F, 1.0F, index);
        };
    }

    public enum SegmentType {
        HEAD(0), BODY(1), BODY_LEGS(2), TAIL_START(3), TAIL_MIDDLE(4), TAIL_END(5);
        private final int index;
        private static final SegmentType[] fromIndex = SegmentType.values();
        SegmentType(int index) { this.index = index; }
        public int index() { return this.index; }
        public static SegmentType fromIndex(int index) { return fromIndex[index]; }
    }

    /* AI STUFF */
    @Override
    protected void customServerAiStep() {
        tickBrain(this);
        if (this.navigation.getPath() != null) {
            PacketDistributor.sendToAllPlayers(new ClientboundPathfindingDebugPacket(this, this.navigation.getPath()));
        }
    }

    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    public List<? extends ExtendedSensor<? extends TestRopeEntity>> getSensors() {
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<>(),
                new HurtBySensor<>()
        );
    }

    @Override
    public BrainActivityGroup<TestRopeEntity> getCoreTasks() { // These are the tasks that run all the time (usually)
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),                      // Have the entity turn to face and look at its current look target
                new MoveToWalkTarget<>());                 // Walk towards the current walk target
    }

    @Override
    public BrainActivityGroup<TestRopeEntity> getIdleTasks() { // These are the tasks that run when the mob isn't doing anything else (usually)
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<TestRopeEntity>(      // Run only one of the below behaviours, trying each one in order. Include the generic type because JavaC is silly
                        new TargetOrRetaliate<>(),            // Set the attack target and walk target based on nearby entities
                        new SetPlayerLookTarget<>(),//,          // Set the look target for the nearest player
                        new SetRandomLookTarget<>()),         // Set a random look target
                new OneRandomBehaviour<>(                 // Run a random task from the below options
                        new SetRandomWalkTargetCloseEnough<>().closeEnoughDist(3), // Set a random walk target to a nearby position
                        new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 200)) // Do nothing for 1.5->3 seconds
                )
        );
    }

    @Override
    public BrainActivityGroup<TestRopeEntity> getFightTasks() { // These are the tasks that handle fighting
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(), // Cancel fighting if the target is no longer valid
                new SetWalkTargetToAttackTarget<>(),      // Set the walk target to the attack target
                new AnimatableMeleeAttack<>(0)); // Melee attack the target if close enough
    }
}
