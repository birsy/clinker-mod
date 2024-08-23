package birsy.clinker.common.world.entity.proceduralanimation;

import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.networking.packet.ClientboundInverseKinematicsStepPacket;
import birsy.clinker.core.util.VectorUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.ArrayList;
import java.util.List;

public class IKLeg<E extends LivingEntity & IKLocomotionEntity> {
    private final Vector3d initialBaseOffset = new Vector3d();
    private final Vector3d base = new Vector3d();
    private final Vector3d foot = new Vector3d();

    private final Vector3d restOffset = new Vector3d();
    private final Vector3d restPos = new Vector3d();
    private final Vector3d attachmentDir = new Vector3d(0, -1, 0);

    private final Quaterniond bodyRotation = new Quaterniond();
    private final Vector3d previousBodyPosition = new Vector3d();
    private final Vector3d bodyPosition = new Vector3d();

    private final Vector3d previousStepPosition = new Vector3d();
    private final Vector3d nextStepPosition = new Vector3d();
    private float stepProgress;
    public boolean stepping;
    protected int refreshTimer = 0;

    private float stepSize = 0.75F;
    private float stillStepSize = 0.2F;

    private float gait = 1F / (20F * 0.35F);

    final List<IKLeg> adjacentLegs = new ArrayList<>(3);

    private final int legIndex;

    protected IKLeg(int legIndex) {
        this.legIndex = legIndex;
    }

    public IKLeg setBaseOffset(double x, double y, double z) {
        this.initialBaseOffset.set(x, y, z);
        return this;
    }
    public IKLeg setRestPosOffset(double x, double y, double z) {
        this.restOffset.set(x, y, z);
        return this;
    }
    public IKLeg setAttachmentDirection(double x, double y, double z) {
        this.attachmentDir.set(x, y, z);
        return this;
    }
    public IKLeg setAdjacentLegs(IKLeg... legs) {
        adjacentLegs.clear();
        adjacentLegs.addAll(List.of(legs));
        return this;
    }
    public IKLeg addAdjacentLegs(IKLeg... legs) {
        adjacentLegs.addAll(List.of(legs));
        return this;
    }
    public IKLeg setGait(float gait) {
        this.gait = gait;
        return this;
    }
    public IKLeg setStepSize(float stepSize) {
        this.stepSize = stepSize;
        return this;
    }
    public IKLeg setStillStepSize(float stepSize) {
        this.stillStepSize = stepSize;
        return this;
    }
    public Vector3dc getTargetPos() {
        return this.foot;
    }

    protected Quaterniond updateBodyRotation(E entity) {
        return bodyRotation.rotationY(Mth.DEG_TO_RAD * -entity.yBodyRot);
    }

    protected Vector3d updateBodyPosition(E entity) {
        this.previousBodyPosition.set(this.bodyPosition);
        return bodyPosition.set(entity.getX(), entity.getY(), entity.getZ());
    }

    public void update(E entity, Level level) {
        // general process:
        // foot placement logic is handled server-side
        // server tells the clientside legs when and where to step
        // rotation of the joints is handled entirely client-side.
        this.updateBasePosition(this.updateBodyPosition(entity), this.updateBodyRotation(entity));

        if (level.isClientSide()) {
            if (this.stepping) {
                this.updateStepProgress(entity, true);
            }
            return;
        }

        this.updateRestPosition(entity, level, this.bodyRotation);

        if (this.stepping) {
            this.updateStepProgress(entity, false);
        } else {
            this.attemptStep(entity);
        }
    }

    private final Vector3d temp = new Vector3d();
    private final Vector3d temp2 = new Vector3d();
    private void updateRestPosition(E entity, BlockGetter level, Quaterniond bodyRotation) {
        this.restPos.set(this.restOffset);
        this.restPos.rotate(bodyRotation);
        this.restPos.add(this.attachmentDir);
        // try to predict where we will need to be by the next step, preventing legs from getting left behind
        // breaks down at unusually high speeds...
        // todo: clamping
        this.restPos.add((this.bodyPosition.x() - this.previousBodyPosition.x()) * (1.0 / gait), 0, (this.bodyPosition.z() - this.previousBodyPosition.z()) * (1.0 / gait));
        this.restPos.add(this.base);

        BlockHitResult raycast = level.clip(new ClipContext(
                VectorUtils.toMoj(this.restPos.add(this.attachmentDir.mul(-1.5, temp), temp)),
                VectorUtils.toMoj(this.restPos.add(this.attachmentDir.mul(1.5, temp), temp)),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                CollisionContext.empty()
        ));

        if (raycast.getType() == HitResult.Type.BLOCK) {
            VectorUtils.toJOML(raycast.getLocation(), this.restPos);
        } else {
            this.searchNeighboringBlocksForValidRestPos(entity, level);
        }
    }

    private final Vector3d xOffsetDirection = new Vector3d();
    private final Vector3d zOffsetDirection = new Vector3d();
    private void searchNeighboringBlocksForValidRestPos(E entity, BlockGetter level) {
        // strategy: try some offsets until something works
        this.zOffsetDirection.set(entity.getLookAngle().x, entity.getLookAngle().y, entity.getLookAngle().z);

        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int zOffset = -1; zOffset <= 1; zOffset++) {
                if (xOffset == 0 && zOffset == 0) continue;
                BlockHitResult raycast = level.clip(new ClipContext(
                        VectorUtils.toMoj(this.restPos.add(this.attachmentDir.mul(-1.5, temp), temp).add(xOffsetDirection.mul(xOffset, temp2)).add(zOffsetDirection.mul(zOffset, temp2))),
                        VectorUtils.toMoj(this.restPos.add(this.attachmentDir.mul(1.5, temp), temp).add(xOffsetDirection.mul(xOffset, temp2)).add(zOffsetDirection.mul(zOffset, temp2))),
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        CollisionContext.empty()
                ));

                if (raycast.getType() == HitResult.Type.BLOCK) {
                    VectorUtils.toJOML(raycast.getLocation(), this.restPos);
                    return;
                }
            }
        }
    }

    private void updateBasePosition(Vector3d bodyPosition, Quaterniond bodyRotation) {
        this.base.set(this.initialBaseOffset);
        this.base.rotate(bodyRotation);
        this.base.add(bodyPosition);
    }

    private void updateStepProgress(E entity, boolean client) {
        this.stepProgress += gait;

        this.previousStepPosition.lerp(this.nextStepPosition, this.stepProgress, this.foot).add(0, (-1 * this.stepProgress * this.stepProgress + this.stepProgress) * 2, 0);

        if (this.stepProgress >= 1.0F) {
            this.foot.set(this.nextStepPosition);

            if (client && this.stepping) {
                entity.level().playLocalSound(this.foot.x, this.foot.y, this.foot.z, SoundEvents.DROWNED_STEP, SoundSource.NEUTRAL, 0.1F, Mth.lerp(entity.getRandom().nextFloat(), 0.5F, 1.1F), false);
                this.stepping = false;
                return;
            }

            this.stepping = false;

            // if our adjacent legs need to step, wait for them to do so.
            for (IKLeg adjacentLeg : this.adjacentLegs) {
                if (adjacentLeg.wantsToStep(entity) && !adjacentLeg.stepping && adjacentLeg.refreshTimer <= 0) {
                    this.refreshTimer = Mth.ceil(1.0 / gait) + 1;
                }
            }
        }
    }

    private void attemptStep(E entity) {
        for (IKLeg adjacentLeg : adjacentLegs) {
            if (adjacentLeg.stepping) {
                return;
            }
        }
        if (this.refreshTimer > 0) {
            return;
        }

        if (this.wantsToStep(entity)) {
            this.step(false, entity, this.restPos);
        }
    }

    private boolean wantsToStep(E entity) {
        return this.foot.distance(this.restPos) > this.getStepDistance(entity);
    }

    public void step(boolean client, E entity, Vector3d newFootPosition) {
        this.step(client, entity, newFootPosition.x, newFootPosition.y, newFootPosition.z);
    }

    public void step(boolean client, E entity, double x, double y, double z) {
        this.stepping = true;
        this.stepProgress = 0.0F;

        this.nextStepPosition.set(x, y, z);
        this.previousStepPosition.set(this.foot);
        if (!client) ClinkerPacketHandler.sendToClientsTrackingEntity(entity, new ClientboundInverseKinematicsStepPacket(entity, this.legIndex, this.nextStepPosition));
    }

    private float getStepDistance(E entity) {
        return entity.getDeltaMovement().multiply(1, 0, 1).length() > 0.01 ? stepSize : stillStepSize;
    }
}
