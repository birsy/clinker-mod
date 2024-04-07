package birsy.clinker.common.world.entity.salamander;

import birsy.clinker.client.model.base.InterpolatedSkeleton;
import birsy.clinker.client.model.base.InterpolatedSkeletonParent;
import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.networking.packet.ClientboundSalamanderSyncPacket;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.JomlConversions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4d;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// todo:
//  slipperiness
//  reimplement ai
public class NewSalamanderEntity extends LivingEntity implements InterpolatedSkeletonParent {
    private static final Vec3 gravity = new Vec3(0, -8, 0);

    public SalamanderJoint headJoint;
    public SalamanderSegment headSegment;
    protected SalamanderJoint lastHitJoint;

    public List<SalamanderJoint> joints;
    public List<SalamanderSegment> segments;

    //public List<Interactable> childInteractables;

    // ANIMATION DEBUG
    //private SalamanderJoint clientJoint1, clientJoint2;
    //private SalamanderSegment clientSegment;

    public List<SalamanderPartEntity> partEntities = new ArrayList<>();

    public static float[] SEGMENT_WIDTHS = {11.0F / 16.0F, (11.0F / 16.0F) * 0.85F, 8.0F / 16.0F, 5.0F / 16.0F};

    public NewSalamanderEntity(EntityType<? extends NewSalamanderEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.joints = new ArrayList<>();
        this.segments = new ArrayList<>();

        this.noPhysics = true;
        this.setNoGravity(true);

        int id = 0;

        SalamanderJoint previousJoint = new SalamanderJoint(this, SEGMENT_WIDTHS[0] * 0.5F, id++);
        this.headJoint = previousJoint;
        previousJoint.isHead = true;
        int length = 10;
        for (int i = 1; i < length; i++) {
            byte girth = 0;
            if (i - (length - 4) > 0) girth = (byte) (i - (length - 4));
            boolean hasLegs = i % 2 == 0;
            if (girth != 0) hasLegs = false;

            SalamanderJoint joint = new SalamanderJoint(this, SEGMENT_WIDTHS[girth] * 0.5F, id++);
            joint.position = previousJoint.position.add(0, 0, 1.0);
            joint.pPosition = joint.position.add(0, 0, 0);
            joint.physicsPrevPosition = joint.position.add(0, 0, 0);
            SalamanderSegment segment = new SalamanderSegment(this, previousJoint, joint, 1.0, hasLegs);
            segment.girth = girth;
            if (i <= 1) this.headSegment = segment;
            previousJoint = joint;
        }

        for (SalamanderSegment segment : this.segments) {
            addEntityToSegment(segment);
        }

        // increment the id counter by all my part segments, and myself.
        this.setId(ENTITY_COUNTER.getAndAdd(this.partEntities.size() + 1) + 1);
    }

    public void addEntityToSegment(SalamanderSegment segment) {
        SalamanderPartEntity segmentEntity = new SalamanderPartEntity(this, segment);
        segmentEntity.setPos(segment.getCenter(1.0F));
        this.partEntities.add(segmentEntity);
    }

    @Override
    public void setId(int id) {
        super.setId(id);
        for (int i = 0; i < this.partEntities.size(); i++) this.partEntities.get(i).setId(id + i + 1);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.ATTACK_DAMAGE).add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.MOVEMENT_SPEED, 0.15D).add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    public void tick() {
        this.baseTick();

        Vec3 gravity = new Vec3(0, -8, 0);
        for (SalamanderJoint joint : this.joints) {
            joint.accelerate(gravity);
            applyBouyancyAndDragForce(joint);

            joint.beginTick(1.0f / 20.0f);
        }

        for (int i = 0; i < 16; i++) {
            for (SalamanderSegment segment : segments) {
                segment.applyConstraint();
            }
            for (SalamanderJoint joint1 : this.joints) {
                joint1.applyCollisionConstraints();
                for (SalamanderJoint joint2 : this.joints) {
                    collideJoints(joint1, joint2);
                }
            }
        }

        for (SalamanderJoint joint : this.joints) {
            joint.finalizeTick();
        }

        for (SalamanderPartEntity partEntity : this.partEntities) {
            partEntity.setPos(partEntity.segmentParent.getCenter(1.0F));
        }

        if (!this.level().isClientSide()) {
            ClinkerPacketHandler.sendToClientsTrackingChunk((LevelChunk) this.level().getChunk(this.blockPosition()), new ClientboundSalamanderSyncPacket(this));
        }

        this.setControllerPosition(this.headJoint.position.add(0, -0.5, 0));

        for (SalamanderSegment segment : this.segments) {
            segment.updateLegs();
        }
    }

    private void setControllerPosition(Vec3 position) {
        this.setPosRaw(position.x(), position.y(), position.z());
        this.setBoundingBox(this.makeBoundingBox());
    }



    private void applyBouyancyAndDragForce(SalamanderJoint joint) {
        double mass = 0.9;
        double inverseMass = 1.0 / mass;
        double airDensity = 0.1;
        // calculates the bouyant force in a really jank way
        // takes into account partial submersion... kinda
        Vec3 downPos = joint.position.subtract(0, joint.radius, 0);
        BlockPos jointPosDown = BlockPos.containing(joint.position);
        BlockPos jointPosUp = BlockPos.containing(joint.position.add(0, joint.radius, 0));

        double volumeInDown = downPos.y - Math.ceil(downPos.y);
        FluidState stateDown = this.level().getFluidState(jointPosDown);
        double densityDown = stateDown.isEmpty() ? airDensity : stateDown.getFluidType().getDensity() * 0.001D;

        double volumeInUp = 1 - volumeInDown;
        FluidState stateUp = this.level().getFluidState(jointPosUp);
        double densityUp = stateUp.isEmpty() ? airDensity : stateUp.getFluidType().getDensity() * 0.001D;

        double averageDensity = (densityDown * volumeInDown + densityUp * volumeInUp);
        joint.accelerate(gravity.scale(-1 * averageDensity).scale(inverseMass));

        Vec3 velocity = joint.getDeltaMovement();
        double dragCoefficient = 0.05;
        Vec3 drag = velocity.scale(Math.exp(-averageDensity * dragCoefficient));
        joint.physicsPrevPosition = joint.position.subtract(drag);
    }

    private void collideJoints(SalamanderJoint joint1, SalamanderJoint joint2) {
        if (joint1 == joint2) return;
        double minimumDistance = joint1.radius + joint2.radius;
        double distanceSqr = joint1.physicsNextPosition.distanceToSqr(joint2.physicsNextPosition);
        if (distanceSqr < minimumDistance * minimumDistance) {
            double correctionDistance = minimumDistance - Math.sqrt(distanceSqr);
            //point of collision will always be directly in the middle, in the case of spheres.
            Vec3 collisionVector = joint1.physicsNextPosition.subtract(joint2.physicsNextPosition).normalize().scale(correctionDistance * 0.5);

            joint1.physicsNextPosition = joint1.physicsNextPosition.add(collisionVector);
            joint2.physicsNextPosition = joint2.physicsNextPosition.subtract(collisionVector);
        }
    }

    @Override
    public void setPos(double x, double y, double z) {
        if (this.joints == null) { super.setPos(x, y, z); return; }
        // gets the position of each segment relative to the core.
        Vec3[] relativePositions = new Vec3[this.joints.size()];
        Vec3[] relativePrevPositions = new Vec3[this.joints.size()];
        for (int i = 0; i < relativePositions.length; i++) {
            relativePositions[i] = this.joints.get(i).position.subtract(this.position());
            relativePrevPositions[i] = this.joints.get(i).physicsPrevPosition.subtract(this.position());
        }
        // repositions the core.
        super.setPos(x, y, z);
        // repositions all the segments relative to the core again.
        for (int i = 0; i < joints.size(); i++) {
            this.joints.get(i).position = relativePositions[i].add(this.position());
            this.joints.get(i).physicsPrevPosition = relativePrevPositions[i].add(this.position());
        }
    }

    @Override
    public void moveTo(double x, double y, double z, float pYRot, float pXRot) {
        if (this.joints == null) { super.setPos(x, y, z); return; }
        // gets the position of each segment relative to the core.
        Vec3[] relativePositions = new Vec3[this.joints.size()];
        Vec3[] relativePrevPositions = new Vec3[this.joints.size()];
        for (int i = 0; i < relativePositions.length; i++) {
            relativePositions[i] = this.joints.get(i).position.subtract(this.position());
            relativePrevPositions[i] = this.joints.get(i).physicsPrevPosition.subtract(this.position());
        }
        // repositions the core.
        super.moveTo(x, y, z, pYRot, pXRot);
        // repositions all the segments relative to the core again.
        for (int i = 0; i < joints.size(); i++) {
            this.joints.get(i).position = relativePositions[i].add(this.position());
            this.joints.get(i).physicsPrevPosition = relativePrevPositions[i].add(this.position());
        }
    }

    @Override
    public void knockback(double pStrength, double pX, double pZ) {
        if (lastHitJoint == null) return;
        net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent event = net.neoforged.neoforge.common.CommonHooks.onLivingKnockBack(this, (float) pStrength, pX, pZ);
        if(event.isCanceled()) return;
        pStrength = event.getStrength();
        pX = event.getRatioX();
        pZ = event.getRatioZ();
        pStrength *= 1.0D - this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);

        if (!(pStrength <= 0.0D)) {
            this.hasImpulse = true;
            Vec3 velocity = lastHitJoint.getDeltaMovement();
            Vec3 knockBack = (new Vec3(pX, 0.0D, pZ)).normalize().scale(pStrength);
            lastHitJoint.push(velocity.x / 2.0D - knockBack.x, this.onGround() ? Math.min(0.4D, velocity.y / 2.0D + pStrength) : velocity.y, velocity.z / 2.0D - knockBack.z);
        }
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        double minX = joints.get(0).position.x, minY = joints.get(0).position.y, minZ = joints.get(0).position.z, maxX = joints.get(0).position.x, maxY = joints.get(0).position.y, maxZ = joints.get(0).position.z;
        for (SalamanderJoint joint : this.joints) {
            if (joint.position.x > maxX) maxX = joint.position.x;
            if (joint.position.y > maxY) maxY = joint.position.y;
            if (joint.position.z > maxZ) maxZ = joint.position.z;
            if (joint.position.x < minX) minX = joint.position.x;
            if (joint.position.y < minY) minY = joint.position.y;
            if (joint.position.z < minZ) minZ = joint.position.z;
        }

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ).inflate(1.2F);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
//        CompoundTag jointsTag = new CompoundTag();
//        for (SalamanderJoint joint : this.joints) {
//            CompoundTag jointTag = new CompoundTag();
//            jointTag.putDouble("x", joint.position.x);
//            jointTag.putDouble("y", joint.position.y);
//            jointTag.putDouble("z", joint.position.z);
//            jointTag.putDouble("pX", joint.physicsPrevPosition.x);
//            jointTag.putDouble("pY", joint.physicsPrevPosition.y);
//            jointTag.putDouble("pZ", joint.physicsPrevPosition.z);
//            jointTag.putDouble("roll", joint.roll);
//
//            jointTag.putDouble("radius", joint.radius);
//            jointTag.putBoolean("head", joint.isHead);
//            jointTag.putInt("id", joint.id);
//
//            jointsTag.put("joint" + joint.id, jointTag);
//        }
//        pCompound.put("joints", jointsTag);
//
//        CompoundTag segmentsTag = new CompoundTag();
//        int j = 0;
//        for (SalamanderSegment segment : this.segments) {
//            CompoundTag segmentTag = new CompoundTag();
//            segmentTag.putDouble("length", segment.length);
//            segmentTag.putByte("girth", segment.girth);
//            segmentTag.putBoolean("legs", segment.hasLegs);
//            segmentTag.putInt("joint1", segment.joint1.id);
//            segmentTag.putInt("joint2", segment.joint2.id);
//            segmentTag.putBoolean("head", segment.joint1.isHead || segment.joint2.isHead );
//
//            segmentsTag.put("segment" + j++, segmentTag);
//        }
//        pCompound.put("segments", segmentsTag);
//
//        Clinker.LOGGER.info("addAdditionalSaveData: " + pCompound.toString());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
//        this.clearChildren();
//        this.joints.clear();
//        this.segments.clear();
//
//        CompoundTag jointsTag = pCompound.getCompound("joints");
//        for (int i = 0; i < jointsTag.size(); i++) {
//            CompoundTag jointTag = jointsTag.getCompound("joint" + i);
//
//            double x = jointTag.getDouble("x");
//            double y = jointTag.getDouble("y");
//            double z = jointTag.getDouble("z");
//            double px = jointTag.getDouble("pX");
//            double py = jointTag.getDouble("pY");
//            double pz = jointTag.getDouble("pZ");
//            double roll = jointTag.getDouble("roll");
//
//            double radius = jointsTag.getDouble("radius");
//            boolean head = jointsTag.getBoolean("head");
//            int id = jointsTag.getInt("id");
//
//            SalamanderJoint joint = new SalamanderJoint(this, radius, id);
//            joint.position = new Vec3(x, y, z);
//            joint.physicsNextPosition = new Vec3(px, py, pz);
//            joint.roll = roll;
//            joint.isHead = head;
//            if (head) this.headJoint = joint;
//
//            addEntityToJoint(joint);
//        }
//
//        CompoundTag segmentsTag = pCompound.getCompound("segments");
//        for (int i = 0; i < segmentsTag.size(); i++) {
//            CompoundTag segmentTag = segmentsTag.getCompound("segment" + i);
//
//            double length = segmentTag.getDouble("length");
//            byte girth = segmentTag.getByte("girth");
//            boolean hasLegs = segmentTag.getBoolean("legs");
//            int joint1 = segmentTag.getInt("joint1");
//            int joint2 = segmentTag.getInt("joint2");
//            boolean head = segmentTag.getBoolean("head");
//
//            SalamanderSegment segment = new SalamanderSegment(this, this.joints.get(joint1), this.joints.get(joint2), length, hasLegs);
//            segment.girth = girth;
//            if (head) this.headSegment = segment;
//        }
//
//        Clinker.LOGGER.info("readAdditionalSaveData: " + pCompound.toString());

    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return Collections.emptyList();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot pSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack) {}

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }


    @Override
    public @Nullable PartEntity<?>[] getParts() {
        return (PartEntity<?>[]) this.partEntities.toArray();
    }

    @Override
    public void remove(RemovalReason pReason) {
        super.remove(pReason);
        Clinker.LOGGER.info("removed salamander");
    }

    InterpolatedSkeleton skeleton;
    @Override
    public void setSkeleton(InterpolatedSkeleton skeleton) {
        this.skeleton = skeleton;
    }
    @Override
    public InterpolatedSkeleton getSkeleton() {
        return skeleton;
    }

    public class SalamanderJoint {
        public Vec3 physicsPrevPosition;
        public Vec3 pPosition;
        public Vec3 position;
        public Vec3 physicsNextPosition;
        public double roll;
        int id;

        private Vec3 pushes;
        private Vec3 acceleration;
        public final double radius;
        final List<SalamanderSegment> frontAttachments;
        final List<SalamanderSegment> backAttachments;
        final NewSalamanderEntity entity;
        public boolean isHead;

        public SalamanderJoint(NewSalamanderEntity entity, double radius, int id) {
            this.radius = radius;
            this.frontAttachments = new ArrayList<>();
            this.backAttachments = new ArrayList<>();
            this.pushes = Vec3.ZERO;
            this.entity = entity;
            this.pPosition = Vec3.ZERO;
            this.position = Vec3.ZERO;
            this.physicsPrevPosition = Vec3.ZERO;
            this.physicsNextPosition = Vec3.ZERO;
            this.acceleration = Vec3.ZERO;
            this.roll = 0.0;
            entity.joints.add(this);
            this.id = id;
        }

        public Vec3 getPosition(double partialTick) {
            return pPosition.lerp(position, partialTick);
        }

        public Vec3 getSmoothedPosition(double partialTick) {
            if (frontAttachments.isEmpty() || backAttachments.isEmpty()) return this.pPosition.lerp(this.position, partialTick);

            Vec3 center = Vec3.ZERO;
            for (SalamanderSegment frontAttachment : frontAttachments) {
                center = center.add(frontAttachment.getCenter(partialTick));
            }
            for (SalamanderSegment backAttachment : backAttachments) {
                center = center.add(backAttachment.getCenter(partialTick));
            }
            center = center.scale(1.0D / (double)(backAttachments.size() + frontAttachments.size()));

            return center;
        }

        protected void beginTick(float deltaTime) {
            this.pPosition = this.position;
            Vec3 velocity = this.getDeltaMovement().scale(0.95F);

            velocity = velocity.add(pushes);
            this.pushes = Vec3.ZERO;

            this.physicsPrevPosition = this.position;
            this.physicsNextPosition = this.position.add(velocity).add(this.acceleration.scale(deltaTime * deltaTime));

            this.acceleration = Vec3.ZERO;
        }

        protected void finalizeTick() {
            applyCollisionConstraints();
            this.position = this.physicsNextPosition;
        }

        protected void applyCollisionConstraints() {
            Vec3 collisionForce = constrainVelocity(this.physicsNextPosition.subtract(position));
            this.physicsNextPosition = this.position.add(collisionForce);
        }

        // ripped from entity code. not entirely sure how it works!
        private Vec3 constrainVelocity(Vec3 velocity) {
            Level level = entity.level();
            AABB aabb = new AABB(this.position.add(radius, radius, radius), this.position.subtract(radius, radius, radius));

            List<VoxelShape> list = level.getEntityCollisions(entity, aabb.expandTowards(velocity));

            return collideBoundingBox(entity, velocity, aabb, level, list);
        }

        protected void accelerate(Vec3 amount) {
            this.acceleration = this.acceleration.add(amount);
        }

        protected void push(Vec3 amount) {
            pushes = pushes.add(amount);
        }

        protected void push(double x, double y, double z) {
            pushes = pushes.add(x, y, z);
        }

        protected Vec3 getDeltaMovement() {
            return this.position.subtract(this.physicsPrevPosition);
        }

        public Quaterniond getOrientation(double partialTick) {
            Quaterniond roll = new Quaterniond(new AxisAngle4d(0, 0, 1, this.roll));

            Vec3 frontCenter = Vec3.ZERO;
            if (frontAttachments.isEmpty()) { frontCenter = this.getPosition(partialTick); } else {
                for (SalamanderSegment frontAttachment : frontAttachments) {
                    frontCenter = frontCenter.add(frontAttachment.getCenter(partialTick));
                }
                frontCenter = frontCenter.scale(1.0D / (double) frontAttachments.size());
            }

            Vec3 backCenter = Vec3.ZERO;
            if (backAttachments.isEmpty()) { backCenter = this.getPosition(partialTick); } else {
                for (SalamanderSegment backAttachment : backAttachments) {
                    backCenter = backCenter.add(backAttachment.getCenter(partialTick));
                }
                backCenter = backCenter.scale(1.0D / (double)backAttachments.size());
            }

            Vec3 direction = frontCenter.subtract(backCenter);
            return new Quaterniond().lookAlong(new Vector3d(direction.x, direction.y, direction.z), new Vector3d(0, 1, 0)).normalize().mul(roll);
        }
    }

    public class SalamanderSegment {
        public final SalamanderJoint joint1;
        public final SalamanderJoint joint2;
        public final double length;
        public byte girth = 0;
        public final NewSalamanderEntity entity;
        public int distanceFromHead = 0;

        public final boolean hasLegs;
        public SalamanderLeg leftLeg, rightLeg;

        public SalamanderSegment(NewSalamanderEntity entity, SalamanderJoint joint1, SalamanderJoint joint2, double length, boolean hasLegs) {
            this.joint1 = joint1;
            this.hasLegs = hasLegs;
            this.joint1.backAttachments.add(this);
            this.joint2 = joint2;
            this.joint2.frontAttachments.add(this);
            this.length = length;
            this.entity = entity;
            entity.segments.add(this);

            if (hasLegs) {
                this.rightLeg = new SalamanderLeg(new Vec3(-0.34375, 0, 0), new Vec3(-1, -1, 0));
                this.leftLeg = new SalamanderLeg(new Vec3(0.34375, 0, 0), new Vec3(1, -1, 0));
                rightLeg.oppositeLeg = leftLeg;
                leftLeg.oppositeLeg = rightLeg;
            }
        }

        public void updateLegs() {
            if (!this.hasLegs) return;
            this.leftLeg.tick(this);
            this.rightLeg.tick(this);

            float legLength = 1.0F;
            Vec3 adjustment = Vec3.ZERO;
            if (this.rightLeg.attached) {
                Vec3 adjustedHipPosition = this.rightLeg.footNormal.scale(legLength).add(this.rightLeg.footPos);
                adjustment = adjustment.add(this.rightLeg.hipPos.subtract(adjustedHipPosition));
            }
            if (this.leftLeg.attached) {
                Vec3 adjustedHipPosition = this.leftLeg.footNormal.scale(legLength).add(this.leftLeg.footPos);
                adjustment = adjustment.add(this.leftLeg.hipPos.subtract(adjustedHipPosition));
            }
            adjustment.scale(0.5);
            //this.joint1.accelerate(adjustment);
            //this.joint2.accelerate(adjustment);
        }

        public void split() {
            this.joint1.backAttachments.remove(this);
            this.joint2.frontAttachments.remove(this);
            this.entity.segments.remove(this);
        }

        public boolean isHead() {
            return joint1.isHead || joint2.isHead;
        }

        public Vec3 getCenter(double partialTick) {
            return joint1.getPosition(partialTick).add(joint2.getPosition(partialTick)).scale(0.5);
        }

        public Quaterniond getOrientation(double partialTick) {
            Vec3 direction = joint2.getPosition(partialTick).subtract(joint1.getPosition(partialTick)).normalize();
            double yRot = Mth.atan2(direction.x(), direction.z());
            double xRot = -Mth.atan2(direction.y(), Math.sqrt(direction.x() * direction.x() + direction.z() * direction.z()));
            return new Quaterniond().rotateYXZ(yRot, xRot, (joint1.roll + joint2.roll) * 0.5);
        }

        protected void applyConstraint() {
            Vec3 pos1 = this.joint1.physicsNextPosition;
            Vec3 pos2 = this.joint2.physicsNextPosition;

            Vec3 center = pos1.add(pos2).scale(0.5);
            Vec3 direction = pos1.subtract(pos2).normalize();

            this.joint1.physicsNextPosition = center.add(direction.scale(this.length * 0.5));
            this.joint2.physicsNextPosition = center.subtract(direction.scale(this.length * 0.5));
        }

        public void push(double x, double y, double z) {
            this.joint1.push(x, y, z);
            this.joint2.push(x, y, z);
        }

        public static class SalamanderLeg {
            boolean attached;
            boolean isMidAir;
            static final float legLength = 1.25F;
            final Vec3 baseHipPos, baseTargetPos;
            public Vec3 hipPos, targetPos;

            boolean moving;
            float moveSpeed = 1.0F / 20.0F;
            float moveTime = 0;
            public Vec3 footPos, footNormal;
            public Vec3 nextFootPos, nextFootNormal;

            SalamanderLeg oppositeLeg;

            public SalamanderLeg(Vec3 baseHipPos, Vec3 baseTargetPos) {
                this.baseHipPos = baseHipPos;
                this.baseTargetPos = baseTargetPos;
                this.footPos = baseTargetPos;
                this.footNormal = new Vec3(0, 1, 0);
                this.nextFootPos = baseTargetPos;
                this.nextFootNormal = new Vec3(0, 1, 0);
                this.targetPos = baseTargetPos;
                this.hipPos = baseHipPos;
            }

            public void tick(SalamanderSegment parent) {
                float stepTimeSeconds = 0.5F;
                float stepTimeTicks = stepTimeSeconds * 20;
                moveSpeed = 1 / stepTimeTicks;

                Vec3 pos = parent.getCenter(1.0F);
                Quaterniond orientation = parent.getOrientation(1.0F);

                Vec3 horizontalMovement = parent.joint1.getDeltaMovement().add(parent.joint2.getDeltaMovement()).multiply(0.5F, 0, 0.5F);
                float speed = (float) horizontalMovement.length();
                float strideLength = legLength;
                if (speed < 0.01F) strideLength *= 0.7F;

                this.hipPos = JomlConversions.toMojang(orientation.transform(JomlConversions.toJOML(baseHipPos))).add(pos);
                this.targetPos = JomlConversions.toMojang(orientation.transform(JomlConversions.toJOML(baseTargetPos))).add(pos).add(new Vec3(Math.min(horizontalMovement.x() * stepTimeTicks, strideLength * 0.9), 0, Math.min(horizontalMovement.z() * stepTimeTicks, strideLength * 0.9)));

                boolean isSolidGround = true;
                Vec3 direction = this.footPos.subtract(this.hipPos);
                BlockHitResult ray = parent.entity.level().clip(new ClipContext(this.hipPos, this.footPos.add(direction.scale(0.05)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, parent.entity));
                isSolidGround = ray.getType() != HitResult.Type.MISS;

                //check for new foot placement!
                if ((footPos.distanceTo(hipPos) > strideLength && !this.moving && !this.oppositeLeg.moving) || this.isMidAir || (!isSolidGround && !this.moving)) {
                    BlockHitResult raycast = parent.entity.level().clip(new ClipContext(hipPos, targetPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, parent.entity));
                    this.attached = false;

                    if (raycast.getType() != HitResult.Type.MISS) {
                        this.nextFootPos = raycast.getLocation();
                        this.nextFootNormal = new Vec3(raycast.getDirection().step());
                        this.isMidAir = false;
                        this.moveTime = 0;
                        this.moving = true;
                    } else {
                        this.isMidAir = true;
                        this.footPos = this.footPos.lerp(this.targetPos, 0.05);
                    }
                }

                if (this.moving && !this.isMidAir) {
                    this.moveTime += moveSpeed;
                    if (this.moveTime >= 1.0F) {
                        this.moving = false;
                        this.attached = true;
                        this.moveTime = 0;
                        this.footPos = this.nextFootPos;
                        this.footNormal = this.nextFootNormal;
                    }
                } else if (this.isMidAir) {
                    this.moveTime = Mth.clamp(moveSpeed - moveSpeed * 0.5F, 0, 1);
                }
            }

            public Vec3 getFootPosition() {
                Vec3 normal = this.footNormal.lerp(this.nextFootNormal, 0.5F);
                Vec3 pos = this.footPos.lerp(this.nextFootPos, this.moveTime).add(normal.scale(Mth.sin(Mth.PI * this.moveTime) * 0.2));
                return pos;
            }

            public Vec3 getFootNormal() {
                return this.footNormal.lerp(this.nextFootNormal, this.moveTime);
            }
        }
    }
}
