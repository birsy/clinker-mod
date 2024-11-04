package birsy.clinker.common.world.entity;

import birsy.clinker.common.world.entity.ai.GroundMoveControl;
import birsy.clinker.common.world.entity.gnomad.OldGnomadAxemanEntity;
import birsy.clinker.core.Clinker;
import com.mojang.math.Constants;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.Vec3i;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class GroundLocomoteEntity extends PathfinderMob {
    public final Vector3f walk = new Vector3f(), previousWalk = new Vector3f();
    @OnlyIn(Dist.CLIENT)
    Vector3f smoothedWalk = new Vector3f();

    private static final EntityDataAccessor<Vector3f> DATA_WALK_ID = SynchedEntityData.defineId(GroundLocomoteEntity.class, EntityDataSerializers.VECTOR3);

    float cumulativeWalk = 0;

    protected GroundLocomoteEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new GroundMoveControl(this);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_WALK_ID, new Vector3f());
    }

    @Override
    public void tick() {
        this.previousWalk.set(walk);
        super.tick();
        if (this.level().isClientSide()) {
            this.walk.set(this.entityData.get(DATA_WALK_ID));
            this.smoothedWalk.lerp(this.walk,0.1F);
        }
        if (this.onGround()) cumulativeWalk += this.walk.length();
    }

    @Override
    protected void customServerAiStep() {
        this.debugMove();
        super.customServerAiStep();
        this.move(MoverType.SELF, new Vec3(walk.x, walk.y, walk.z));
        this.entityData.set(DATA_WALK_ID, this.walk);
    }

    public void walk(float x, float y, float z) {
        this.walk.set(x, y, z);
    }

    public void walk(Vector3fc vector3fc) {
        this.walk.set(vector3fc);
    }

    private final Vector3f returnWalkVector = new Vector3f();
    public Vector3fc getWalkVector(float partialTicks) {
        return this.smoothedWalk;
    }

    private final Vector3f returnFacingVector = new Vector3f();
    public Vector3fc getBodyFacingDirection(float partialTicks) {
        float angle = - Mth.lerp(partialTicks, this.yBodyRotO, this.yBodyRot) * (float) (Math.PI / 180.0);
        return returnFacingVector.set(Mth.sin(angle), 0, Mth.cos(angle));
    }

    public float getWalkAmount(float partialTick) { return this.getWalkVector(partialTick).dot(this.getBodyFacingDirection(partialTick)); }

    private final Vector3f strafeDir = new Vector3f(1, 0, 0);
    public float getStrafeAmount(float partialTick) { return this.getWalkVector(partialTick).dot(this.getBodyFacingDirection(partialTick).cross(0, 1, 0, strafeDir)); }

    public float getCumulativeWalk() {
        return cumulativeWalk;
    }

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
            this.getMoveControl().strafe(0.0F, Mth.clamp(Mth.sin(this.tickCount * 0.008F) * 10, -1, 1) * 1.5F);
        } else {
            this.moveTowardsPosition(this.getX(), this.getY(), this.getZ(), maxSpeed, 1.0);
        }

    }
    private void moveTowardsPosition(double x, double y, double z, double maxSpeed, double completionRadius) {
        this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(x, y, z));
        this.lookControl.setLookAt(x, y, z);
        if (this.position().distanceToSqr(x, y, z) > completionRadius*completionRadius) {
            this.getMoveControl().setWantedPosition(x, y, z, maxSpeed);
        } else {
            this.getMoveControl().setWantedPosition(this.getX(), this.getY(), this.getZ(), 0);
        }
    }
}
