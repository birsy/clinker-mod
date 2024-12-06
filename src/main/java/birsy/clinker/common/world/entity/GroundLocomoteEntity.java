package birsy.clinker.common.world.entity;

import birsy.clinker.common.networking.packet.ClientboundGroundLocomotorSyncPacket;
import birsy.clinker.common.world.entity.ai.*;
import birsy.clinker.core.Clinker;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.api.distmarker.Dist;

import net.neoforged.neoforge.network.PacketDistributor;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class GroundLocomoteEntity extends PathfinderMob {
    public final Vector3f walk = new Vector3f(), previousWalk = new Vector3f();
    
    Vector3f smoothedWalk = new Vector3f();

    private static final EntityDataAccessor<Boolean> DATA_WATCHING_ENTITY = SynchedEntityData.defineId(GroundLocomoteEntity.class, EntityDataSerializers.BOOLEAN);

    float cumulativeWalkGoal = 0;
    float cumulativeWalk = 0;
    protected final Scheduler scheduler = new Scheduler();

    protected GroundLocomoteEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new GroundMoveControl(this);
        this.lookControl = new GroundLookControl(this);
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new ClinkerSmoothGroundNavigation(this, pLevel, 0.5F);
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new GroundBodyRotationControl(this);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_WATCHING_ENTITY, false);
    }

    @Override
    public void tick() {
        this.previousWalk.set(walk);
        this.scheduler.tick();
        super.tick();
        if (this.level().isClientSide()) {
            this.smoothedWalk.lerp(this.walk,0.1F);
            this.cumulativeWalk = Mth.lerp(0.5F, this.cumulativeWalk, this.cumulativeWalkGoal);
        }
    }

    @Override
    protected void customServerAiStep() {
        //this.debugMove();
        Vec3 positionPriorToWalking = this.position();
        this.move(MoverType.SELF, new Vec3(walk.x, walk.y, walk.z));
        Vec3 walkedVector = this.position().subtract(positionPriorToWalking);

        float distancedActuallyWalked = (float) walkedVector.length();
        this.cumulativeWalk += distancedActuallyWalked;
        PacketDistributor.sendToPlayersTrackingEntity(this, new ClientboundGroundLocomotorSyncPacket(this.getId(), walkedVector.toVector3f(), this.cumulativeWalk));
    }

    public void walk(float x, float y, float z) {
        this.walk.set(x, y, z);
    }

    public void walk(Vector3fc vector3fc) {
        this.walk.set(vector3fc);
    }

    private final Vector3f returnWalkVector = new Vector3f();
    public Vector3fc getWalkVector(float partialTicks) {
        returnWalkVector.set(smoothedWalk);
        return returnWalkVector;
    }

    private final Vector3f returnFacingVector = new Vector3f();
    public Vector3fc getBodyFacingDirection(float partialTicks) {
        float angle = - Mth.lerp(partialTicks, this.yBodyRotO, this.yBodyRot) * (float) (Math.PI / 180.0);
        return returnFacingVector.set(Mth.sin(angle), 0, Mth.cos(angle));
    }

    public float getWalkAmount(float partialTick) {
        return this.getWalkVector(partialTick).dot(this.getBodyFacingDirection(partialTick));
    }

    private final Vector3f strafeDir = new Vector3f(1, 0, 0);
    public float getStrafeAmount(float partialTick) {
        return this.getWalkVector(partialTick).dot(this.getBodyFacingDirection(partialTick).cross(0, 1, 0, strafeDir));
    }

    public void setCumulativeWalk(float amount) {
        this.cumulativeWalkGoal = amount;
    }

    public float getCumulativeWalk() {
        return cumulativeWalk;
    }

    public void setWatchingEntity(boolean watchingEntity) {
        entityData.set(DATA_WATCHING_ENTITY, watchingEntity);
    }

    public boolean isWatchingEntity() {
        return entityData.get(DATA_WATCHING_ENTITY);
    }

    private void debugMove() {
        float maxSpeed = 1.0F;
        Player target = EntityRetrievalUtil.getNearestEntity(this, 40.0F, (entity -> entity instanceof Player));

        if (target == null) {
            this.moveTowardsPosition(this.getX(), this.getY(), this.getZ(), maxSpeed, 1.0);
            return;
        }

        // approach player
        if (target.getMainHandItem().is(Items.CARROT_ON_A_STICK)) {
            this.moveTowardsPosition(target.getX(), this.getY(), target.getZ(), maxSpeed, this.getBbWidth() * 1.8F);
            this.getLookControl().setLookAt(target);
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
                this.moveTowardsPosition(result.getLocation().x() + normal.getX(), result.getLocation().y() + normal.getY(), result.getLocation().z() + normal.getZ(), maxSpeed, 1.5);
                return;
            }
        }
        // strafe
        if (target.getMainHandItem().is(Items.STRING)) {
            Vec3 targetPos = target.getEyePosition();
            this.lookAt(EntityAnchorArgument.Anchor.EYES, targetPos);
            this.getLookControl().setLookAt(target);

            float desiredDistance = this.getBbWidth() * 2.2F;
            float tolerance = this.getBbWidth() * 0.25F;
            float difference = (float)this.position().subtract(targetPos).horizontalDistance() - desiredDistance;
            float forwardbackwardAmount = 0;
            if (Math.abs(difference) > tolerance) forwardbackwardAmount = Mth.clamp(Mth.abs(difference / tolerance), 0, 3) * Mth.sign(difference);

            float sideToSideAmount = Mth.clamp(Mth.sin(this.tickCount * 0.008F) * 10, -1, 1) * 1.5F;

            this.getMoveControl().strafe(forwardbackwardAmount, sideToSideAmount);
        } else {
            this.moveTowardsPosition(this.getX(), this.getY(), this.getZ(), maxSpeed, 1.0);
        }

    }
    private void moveTowardsPosition(double x, double y, double z, double maxSpeed, double completionRadius) {
        this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(x, y, z));
        this.lookControl.setLookAt(x, y, z);
        if (this.position().distanceToSqr(x, y, z) > completionRadius*completionRadius) {
            //this.getMoveControl().setWantedPosition(x, y, z, maxSpeed);
            Path path = this.getNavigation().createPath(new BlockPos((int) x, (int) y, (int) z), 0);
            if (path != null) this.getNavigation().moveTo(path, maxSpeed);
        } else {
            //this.getMoveControl().setWantedPosition(this.getX(), this.getY(), this.getZ(), 0);
           // this.getNavigation().stop();
        }
    }
}
