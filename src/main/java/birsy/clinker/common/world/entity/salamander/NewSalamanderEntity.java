package birsy.clinker.common.world.entity.salamander;

import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.networking.packet.ClientboundSalamanderSyncPacket;
import birsy.clinker.common.world.level.interactable.Interactable;
import birsy.clinker.common.world.level.interactable.InteractableManager;
import birsy.clinker.common.world.level.interactable.InteractableParent;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.Quaterniond;
import birsy.clinker.core.util.rigidbody.colliders.OBBCollisionShape;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// todo:
//  slipperiness
//  legs~!
//  reimplement ai
//  graphics...
public class NewSalamanderEntity extends LivingEntity implements InteractableParent {
    private static final Vec3 gravity = new Vec3(0, -8, 0);

    public SalamanderJoint headJoint;
    public SalamanderSegment headSegment;
    protected SalamanderJoint lastHitJoint;

    public List<SalamanderJoint> joints;
    public List<SalamanderSegment> segments;

    public List<Interactable> childInteractables;

    public NewSalamanderEntity(EntityType<? extends NewSalamanderEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.childInteractables = new ArrayList<>();
        this.joints = new ArrayList<>();
        this.segments = new ArrayList<>();

        this.noPhysics = true;
        this.setNoGravity(true);

        SalamanderJoint previousJoint = new SalamanderJoint(this, 0.5);
        this.headJoint = previousJoint;
        previousJoint.isHead = true;
        addInteractableToJoint(previousJoint);
        for (int i = 1; i < 10; i++) {
            SalamanderJoint joint = new SalamanderJoint(this, 0.5);
            joint.position = previousJoint.position.add(0, 0, 1.0);
            joint.pPosition = joint.position.add(0, 0, 0);
            joint.physicsPrevPosition = joint.position.add(0, 0, 0);
            SalamanderSegment segment = new SalamanderSegment(this, previousJoint, joint, 1.0);
            if (i <= 1) this.headSegment = segment;
            previousJoint = joint;
            addInteractableToJoint(joint);
        }
    }

    public void addInteractableToJoint(SalamanderJoint joint) {
        if (!this.level.isClientSide()) {
            SalamanderBodyInteractable interactable = new SalamanderBodyInteractable(this, joint, new OBBCollisionShape(0.5, 0.5, 0.5));
            interactable.setPosition(joint.position);

            InteractableManager.addServerInteractable(interactable, (ServerLevel) this.level);
        }
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

        if (!this.level.isClientSide()) {
            for (Interactable interactable : this.childInteractables) {
                if (interactable instanceof SalamanderBodyInteractable bodyInteractable) {
                    bodyInteractable.setPosition(bodyInteractable.jointParent.getSmoothedPosition(1.0).add(0, 0.5, 0));
                    bodyInteractable.setRotation(bodyInteractable.jointParent.getOrientation(1.0).toMojangQuaternion());
                }
            }
            ClinkerPacketHandler.sendToClientsInChunk((LevelChunk) this.level.getChunk(this.blockPosition()), new ClientboundSalamanderSyncPacket(this));
        }

        this.setControllerPosition(this.headJoint.position);
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
        BlockPos jointPosDown = new BlockPos(joint.position);
        BlockPos jointPosUp = new BlockPos(joint.position.add(0, joint.radius, 0));

        double volumeInDown = downPos.y - Math.ceil(downPos.y);
        FluidState stateDown = this.level.getFluidState(jointPosDown);
        double densityDown = stateDown.isEmpty() ? airDensity : stateDown.getFluidType().getDensity() * 0.001D;

        double volumeInUp = 1 - volumeInDown;
        FluidState stateUp = this.level.getFluidState(jointPosUp);
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
        net.minecraftforge.event.entity.living.LivingKnockBackEvent event = net.minecraftforge.common.ForgeHooks.onLivingKnockBack(this, (float) pStrength, pX, pZ);
        if(event.isCanceled()) return;
        pStrength = event.getStrength();
        pX = event.getRatioX();
        pZ = event.getRatioZ();
        pStrength *= 1.0D - this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);

        if (!(pStrength <= 0.0D)) {
            this.hasImpulse = true;
            Vec3 velocity = lastHitJoint.getDeltaMovement();
            Vec3 knockBack = (new Vec3(pX, 0.0D, pZ)).normalize().scale(pStrength);
            lastHitJoint.push(velocity.x / 2.0D - knockBack.x, this.onGround ? Math.min(0.4D, velocity.y / 2.0D + pStrength) : velocity.y, velocity.z / 2.0D - knockBack.z);
        }
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
    public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack) {

    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public List<Interactable> getChildInteractables() {
        return childInteractables;
    }

    @Override
    public void clearChildren() {
        if (childInteractables != null) childInteractables.clear();
    }

    @Override
    public void remove(RemovalReason pReason) {
        super.remove(pReason);
        Clinker.LOGGER.info("removed salamander");
        this.remove();
    }

    public class SalamanderJoint {
        public Vec3 physicsPrevPosition;
        public Vec3 pPosition;
        public Vec3 position;
        public Vec3 physicsNextPosition;
        public double roll;

        private Vec3 pushes;
        private Vec3 acceleration;
        public final double radius;
        final List<SalamanderSegment> frontAttachments;
        final List<SalamanderSegment> backAttachments;
        final NewSalamanderEntity entity;
        public boolean isHead;

        public SalamanderJoint(NewSalamanderEntity entity, double radius) {
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
            Vec3 velocity = this.getDeltaMovement();

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
            this.physicsNextPosition = this.position.add(constrainVelocity(this.physicsNextPosition.subtract(position)));
        }

        // ripped from entity code. not entirely sure how it works!
        private Vec3 constrainVelocity(Vec3 velocity) {
            Level level = entity.level;
            AABB aabb = new AABB(this.position.add(radius, 2 * radius, radius), this.position.subtract(radius, 0, radius));

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
            Quaterniond roll = new Quaterniond(new Vec3(0, 0, 1), this.roll);

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

            return Quaterniond.lookAt(frontCenter, backCenter, new Vec3(0, 0, 1)).normalize().mul(roll);
        }
    }

    public class SalamanderSegment {
        public final SalamanderJoint joint1;
        public final SalamanderJoint joint2;
        public final double length;
        public final NewSalamanderEntity entity;

        public SalamanderSegment(NewSalamanderEntity entity, SalamanderJoint joint1, SalamanderJoint joint2, double length) {
            this.joint1 = joint1;
            this.joint1.backAttachments.add(this);
            this.joint2 = joint2;
            this.joint2.frontAttachments.add(this);
            this.length = length;
            this.entity = entity;
            entity.segments.add(this);
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
            return Quaterniond.lookAt(joint1.getPosition(partialTick), joint2.getPosition(partialTick), new Vec3(0, 0, 1)).normalize();
        }

        protected void applyConstraint() {
            Vec3 pos1 = this.joint1.physicsNextPosition;
            Vec3 pos2 = this.joint2.physicsNextPosition;

            Vec3 center = pos1.add(pos2).scale(0.5);
            Vec3 direction = pos1.subtract(pos2).normalize();

            this.joint1.physicsNextPosition = center.add(direction.scale(this.length * 0.5));
            this.joint2.physicsNextPosition = center.subtract(direction.scale(this.length * 0.5));
        }
    }
}
