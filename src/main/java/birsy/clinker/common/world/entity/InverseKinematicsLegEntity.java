package birsy.clinker.common.world.entity;

import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.networking.packet.ClientboundInverseKinematicsStepPacket;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.VectorUtils;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.ArrayList;
import java.util.List;

public class InverseKinematicsLegEntity extends LivingEntity {
    public Leg[] legs;
    @OnlyIn(Dist.CLIENT)
    public ClientLeg[] cLegs;

    private boolean canMove = true;
    public static int counter = 0;

    public InverseKinematicsLegEntity(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        this.legs = new Leg[4];
        this.cLegs = new ClientLeg[4];

        this.legs[0] = new Leg(new Vector3d(0.5F, 1.0F, 0.5F), new Vector3d(1.0F, 0.0F, 1.0F), 0);
        this.legs[1] = new Leg(new Vector3d(-0.5F, 1.0F, 0.5F), new Vector3d(-1.0F, 0.0F, 1.0F), 1);
        this.legs[2] = new Leg(new Vector3d(0.5F, 1.0F, -0.5F), new Vector3d(1.0F, 0.0F, -1.0F), 2);
        this.legs[3] = new Leg(new Vector3d(-0.5F, 1.0F, -0.5F), new Vector3d(-1.0F, 0.0F, -1.0F), 3);

        this.legs[0].adjacentLegs.add(this.legs[1]);
        this.legs[0].adjacentLegs.add(this.legs[2]);

        this.legs[1].adjacentLegs.add(this.legs[0]);
        this.legs[1].adjacentLegs.add(this.legs[3]);

        this.legs[2].adjacentLegs.add(this.legs[0]);
        this.legs[2].adjacentLegs.add(this.legs[3]);

        this.legs[3].adjacentLegs.add(this.legs[1]);
        this.legs[3].adjacentLegs.add(this.legs[2]);


        for (int i = 0; i < 4; i++) {

            //this.cLegs[i] = new ClientLeg(1.0F, 1.0F, 1.0F);
            //this.cLegs[i] = new ClientLeg(0.5F, 0.5F, 0.5F, 0.5F, 0.5F, 0.5F);
            //this.cLegs[i] = new ClientLeg(0.5F, 2.0F, 0.5F);
            this.cLegs[i] = new ClientLeg(0.5F, 0.25F, 0.5F, 2.0F);
            //this.cLegs[i] = new ClientLeg(pLevel.random.nextFloat() * 3.0F, pLevel.random.nextFloat() * 3.0F, pLevel.random.nextFloat() * 3.0F);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            for (int i = 0; i < cLegs.length; i++) {
                cLegs[i].update(legs[i]);
            }
        } else {
            this.canMove = true;
            for (int i = 0; i < legs.length; i++) {
                if (legs[i].refreshTimer > 0) legs[i].refreshTimer--;
                if (legs[i].stepping) this.canMove = false;
            }
        }
        for (int i = 0; i < legs.length; i++) {
            legs[i].update(this, this.level());
        }


        this.debugMove();
    }

    private void debugMove() {
        float maxSpeed = 1.0F;
        float acceleration = 0.05F;
        Player target = EntityRetrievalUtil.getNearestEntity(this, 16.0F, (entity -> entity instanceof Player));

        if (target != null) {
            if (target.getMainHandItem().is(Items.CARROT_ON_A_STICK)) {
                this.moveTowardsPosition(target.getX(), target.getY(), target.getZ(), acceleration, maxSpeed, 5.0, !this.level().isClientSide());
                return;
            }
            if (target.getMainHandItem().is(Items.ARROW)) {
                BlockHitResult result = this.level().clip(new ClipContext(
                        target.getEyePosition(),
                        target.getEyePosition().add(target.getLookAngle().scale(16.0F)),
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        CollisionContext.empty()
                ));
                if (result.getType() == HitResult.Type.BLOCK) {
                    this.moveTowardsPosition(result.getLocation().x(), result.getLocation().y(), result.getLocation().z(), acceleration, maxSpeed, 1.0, !this.level().isClientSide());
                    return;
                }
            }
        }

        this.moveTowardsPosition(this.getX(), this.getY(), this.getZ(), 0.01F, maxSpeed, 1.0, false);
    }
    private void moveTowardsPosition(double x, double y, double z, double acceleration, double maxSpeed, double completionRadius, boolean setLook) {
        double desiredXVelocity = 0;
        double desiredZVelocity = 0;
        double acc = 0.0;
        if (this.position().distanceToSqr(x, this.position().y, z) > completionRadius*completionRadius) {
            desiredXVelocity = (x - this.position().x);
            desiredZVelocity = (z - this.position().z);

            double invMagnitude = 1.0 / Math.sqrt(desiredXVelocity*desiredXVelocity + desiredZVelocity*desiredZVelocity);

            desiredXVelocity *= invMagnitude * maxSpeed;
            desiredZVelocity *= invMagnitude * maxSpeed;
            acc = acceleration;
        }

        Vec3 deltaMovement = this.getDeltaMovement();
        this.setDeltaMovement(MathUtils.approach(deltaMovement.x, desiredXVelocity, acc), deltaMovement.y, MathUtils.approach(deltaMovement.z, desiredZVelocity, acc));
        if (setLook) this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(x, y, z));
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() { return List.of(ItemStack.EMPTY); }
    @Override
    public ItemStack getItemBySlot(EquipmentSlot pSlot) { return ItemStack.EMPTY; }
    @Override
    public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack) { return; }
    @Override
    public HumanoidArm getMainArm() { return HumanoidArm.RIGHT; }

    public static class Leg {
        private final Vector3d initialBaseOffset;
        private final Vector3d base = new Vector3d();
        private final Vector3d foot = new Vector3d();

        final Vector3d restOffset;
        private final Vector3d restPos = new Vector3d();
        final Vector3d attachmentDir = new Vector3d(0, -1, 0);

        private final Vector3d previousStepPosition = new Vector3d();
        private final Vector3d nextStepPosition = new Vector3d();
        private float stepProgress;
        private boolean stepping;
        private int refreshTimer = 0;

        private static float STEP_DISTANCE = 0.75F;
        private static float STILL_STEP_DISTANCE = 0.2F;

        private static float STEP_SPEED = 1F / (20F * 0.35F);

        final List<Leg> adjacentLegs = new ArrayList<>(3);

        private final int legIndex;

        protected Leg(Vector3d initialBaseOffset, Vector3d restOffset, int legIndex) {
            this.initialBaseOffset = initialBaseOffset;
            this.restOffset = restOffset;
            this.legIndex = legIndex;
        }

        private final Vector3d temp = new Vector3d();
        private final Vector3d temp2 = new Vector3d();
        private void updateRestPosition(InverseKinematicsLegEntity entity, BlockGetter level, Quaterniond bodyRotation) {
            Vec3 previousPosition = entity.getPosition(0.0F);
            Vec3 position = entity.getPosition(1.0F);
            this.restPos.set(this.restOffset);
            this.restPos.rotate(bodyRotation);
            this.restPos.add(this.attachmentDir);
            // try to predict where we will need to be by the next step, preventing legs from getting left behind
            // breaks down at unusually high speeds...
            // todo: clamping
            this.restPos.add((position.x() - previousPosition.x()) * (1.0/STEP_SPEED), 0, (position.z() - previousPosition.z()) * (1.0/STEP_SPEED));
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
        private void searchNeighboringBlocksForValidRestPos(InverseKinematicsLegEntity entity, BlockGetter level) {
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

        private void updateBasePosition(double bodyX, double bodyY, double bodyZ, Quaterniond bodyRotation) {
            this.base.set(this.initialBaseOffset);
            this.base.rotate(bodyRotation);
            this.base.add(bodyX, bodyY, bodyZ);
        }

        private final Quaterniond bodyRotation = new Quaterniond();
        public void update(InverseKinematicsLegEntity entity, Level level) {

            // general process:
            // foot placement logic is handled server-side
            // server tells the clientside legs when and where to step
            // rotation of the joints is handled entirely client-side.

            bodyRotation.rotationY(Mth.DEG_TO_RAD * -entity.yHeadRot);
            this.updateBasePosition(entity.getX(), entity.getY(), entity.getZ(), this.bodyRotation);

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

        private void updateStepProgress(InverseKinematicsLegEntity entity, boolean client) {
            this.stepProgress += STEP_SPEED;

            this.previousStepPosition.lerp(this.nextStepPosition, this.stepProgress, this.foot).add(0, (-1 * this.stepProgress * this.stepProgress + this.stepProgress) * 2, 0);

            if (this.stepProgress >= 1.0F) {
                this.foot.set(this.nextStepPosition);

                if (client && this.stepping) {
                    entity.level().playLocalSound(this.foot.x, this.foot.y, this.foot.z, SoundEvents.NETHERITE_BLOCK_STEP, SoundSource.NEUTRAL, 0.2F, 1.0F, false);
                    this.stepping = false;
                    return;
                }

                this.stepping = false;

                // if our adjacent legs need to step, wait for them to do so.
                for (Leg adjacentLeg : this.adjacentLegs) {
                    if (adjacentLeg.wantsToStep(entity) && !adjacentLeg.stepping && adjacentLeg.refreshTimer <= 0) {
                        this.refreshTimer = Mth.ceil(1.0 / STEP_SPEED) + 1;
                    }
                }
            }
        }

        private void attemptStep(InverseKinematicsLegEntity entity) {
            for (Leg adjacentLeg : adjacentLegs) {
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

        private boolean wantsToStep(InverseKinematicsLegEntity entity) {
            return this.foot.distance(this.restPos) > this.getStepDistance(entity);
        }

        public void step(boolean client, InverseKinematicsLegEntity entity, Vector3d newFootPosition) {
            this.step(client, entity, newFootPosition.x, newFootPosition.y, newFootPosition.z);
        }

        public void step(boolean client, InverseKinematicsLegEntity entity, double x, double y, double z) {
            this.stepping = true;
            this.stepProgress = 0.0F;

            this.nextStepPosition.set(x, y, z);
            this.previousStepPosition.set(this.foot);
            if (!client) ClinkerPacketHandler.sendToClientsTrackingEntity(entity, new ClientboundInverseKinematicsStepPacket(entity, this.legIndex, this.nextStepPosition));
        }

        public float getStepDistance(InverseKinematicsLegEntity entity) {
            return entity.getDeltaMovement().multiply(1, 0, 1).length() > 0.01 ? STEP_DISTANCE : STILL_STEP_DISTANCE;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class ClientLeg {
        private Vector3d[] previousSegments;
        private Vector3d[] segments;

        private Vector3d[] interpolatedSegments;

        public float[] segmentLengths;

        protected ClientLeg(float... segmentLengths) {
            this.segments = new Vector3d[segmentLengths.length + 1];

            float totalLength = 0;
            for (int i = 0; i < segmentLengths.length; i++) {
                this.segments[i] = new Vector3d(0, totalLength, 0);
                totalLength += segmentLengths[i];
            }
            this.segments[segmentLengths.length] = new Vector3d(0, totalLength, 0);

            this.previousSegments = new Vector3d[this.segments.length];
            this.interpolatedSegments = new Vector3d[this.segments.length];
            for (int i = 0; i < this.segments.length; i++) {
                this.previousSegments[i] = new Vector3d(this.segments[i]);
                this.interpolatedSegments[i] = new Vector3d(this.segments[i]);
            }

            this.segmentLengths = segmentLengths;
        }

        protected ClientLeg(int segmentCount, float segmentLength) {
            this.segments = new Vector3d[segmentCount];
            this.segmentLengths = new float[segmentCount];

            for (int i = 0; i < segmentCount; i++) {
                this.segmentLengths[i] = segmentLength;
                this.segments[i] = new Vector3d(0, segmentLength * i, 0);
            }
            for (int i = 0; i < segmentCount + 1; i++) {
                this.segments[i] = new Vector3d(0, segmentLength * i, 0);
            }
        }

        void update(Leg leg) {
            for (int i = 0; i < this.segments.length; i++) { this.previousSegments[i].set(this.segments[i]); }
            this.constrain(leg.base, leg.foot, 16, 0.05F);
        }

        void constrain(Vector3d base, Vector3d foot, int maxIterations, float closeEnoughDistance) {
            resetAndAlignToEndPoint(base, foot);

            // runs the FABRIK algorithm
            Vector3d dir = new Vector3d();
            for (int iteration = 0; iteration < maxIterations * 2; iteration ++) {
                boolean reversed = iteration % 2 == 0;

                segments[reversed ? segments.length - 1 : 0].set(reversed ? foot : base);

                correctLegLength(dir, reversed);

                if (!reversed && foot.distance(segments[segments.length - 1]) < closeEnoughDistance) {
                    return;
                }
            }
        }

        // todo: pole target
        private void resetAndAlignToEndPoint(Vector3d base, Vector3d foot) {
            Vector3d direction = foot.sub(base, new Vector3d()).mul(1, 0, 1).add(0, 1, 0).normalize();

            segments[0].set(base);

            float totalLength = 0;
            for (int i = 0; i < this.segmentLengths.length; i++) {
                totalLength += segmentLengths[i];
                direction.mul(totalLength, segments[i + 1]);
                segments[i + 1].add(base);
            }
        }

        private void correctLegLength(Vector3d dir, boolean reversed) {
            if (reversed) {
                for (int i = segments.length - 1; i >= 1; i--) {
                    Vector3d segment = segments[i];
                    Vector3d nextSegment = segments[i - 1];

                    dir.set(nextSegment);
                    dir.sub(segment);
                    dir.normalize();
                    dir.mul(this.segmentLengths[i - 1]);

                    nextSegment.set(segment.x() + dir.x(), segment.y() + dir.y(), segment.z() + dir.z());
                }
            } else {
                for (int i = 1; i < segments.length; i++) {
                    Vector3d segment = segments[i];
                    Vector3d previousSegment = segments[i - 1];

                    dir.set(segment);
                    dir.sub(previousSegment);
                    dir.normalize();
                    dir.mul(this.segmentLengths[i - 1]);

                    segment.set(previousSegment.x() + dir.x(), previousSegment.y() + dir.y(), previousSegment.z() + dir.z());
                }
            }
        }

        public int getPointCount() {
            return this.segments.length;
        }

        public Vector3dc getPointPosition(int index, float partialTicks) {
            return this.previousSegments[index].lerp(this.segments[index], partialTicks, this.interpolatedSegments[index]);
        }
    }
}
