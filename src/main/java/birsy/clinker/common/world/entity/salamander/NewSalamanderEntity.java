package birsy.clinker.common.world.entity.salamander;

import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.networking.packet.ClientboundSalamanderSyncPacket;
import birsy.clinker.common.world.level.interactable.Interactable;
import birsy.clinker.common.world.level.interactable.InteractableManager;
import birsy.clinker.common.world.level.interactable.InteractableParent;
import birsy.clinker.common.world.level.interactable.InteractionContext;
import birsy.clinker.core.util.Quaterniond;
import birsy.clinker.core.util.rigidbody.colliders.OBBCollisionShape;
import com.mojang.math.Matrix3f;
import com.mojang.math.Quaternion;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// todo:
//  entity - entity collisions
//  slipperiness
//  legs~!
//  reimplement ai
//  graphics...

public class NewSalamanderEntity extends LivingEntity implements InteractableParent {
    public SalamanderJoint headJoint;
    public SalamanderSegment headSegment;

    public List<SalamanderJoint> joints;
    public List<SalamanderSegment> segments;

    public List<Interactable> childInteractables;

    public NewSalamanderEntity(EntityType<? extends NewSalamanderEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.childInteractables = new ArrayList<>();
        this.joints = new ArrayList<>();
        this.segments = new ArrayList<>();

        SalamanderJoint previousJoint = new SalamanderJoint(this, 0.5);
        this.headJoint = previousJoint;
        previousJoint.isHead = true;

        this.noPhysics = true;
        this.setNoGravity(true);

        for (int i = 1; i < 10; i++) {
            SalamanderJoint joint = new SalamanderJoint(this, 0.5);
            joint.position = previousJoint.position.add(0, 0, 1.0);
            joint.pPosition = joint.position.add(0, 0, 0);
            joint.physicsPrevPosition = joint.position.add(0, 0, 0);
            SalamanderSegment segment = new SalamanderSegment(this, previousJoint, joint, 1.0);
            if (i >= 1) this.headSegment = segment;
            previousJoint = joint;

            if (!pLevel.isClientSide()) {
                SalamanderBodyInteractable interactable = new SalamanderBodyInteractable(this, segment, new OBBCollisionShape(0.5, 0.5, 0.5));
                interactable.setPosition(segment.getCenter(1.0F));

                InteractableManager.addServerInteractable(interactable, (ServerLevel) pLevel);
                this.childInteractables.add(interactable);
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.ATTACK_DAMAGE).add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.MOVEMENT_SPEED, 0.15D).add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    public void tick() {
        this.baseTick();

        Vec3 gravity = new Vec3(0, -9.81, 0);
        for (SalamanderJoint joint : this.joints) {
            joint.accelerate(gravity);
            joint.beginTick(1.0f / 20.0f);
        }

        for (int i = 0; i < 16; i++) {
            for (SalamanderSegment segment : segments) {
                segment.applyConstraint();
            }
            for (SalamanderJoint joint1 : this.joints) {
                joint1.applyCollisionConstraints();
                for (SalamanderJoint joint2 : this.joints) {
                    if (joint1 == joint2) break;
                    double minimumDistance = joint1.radius + joint2.radius;
                    double distanceSqr = joint1.physicsNextPosition.distanceToSqr(joint2.physicsNextPosition);
                    if (distanceSqr < minimumDistance * minimumDistance) {
                        double correctionDistance = minimumDistance - Mth.sqrt((float) distanceSqr);
                        //point of collision will always be directly in the middle, in the case of spheres.
                        Vec3 collisionVector = joint1.physicsNextPosition.subtract(joint2.physicsNextPosition).normalize().scale(correctionDistance * 0.5);

                        joint1.physicsNextPosition = joint1.physicsNextPosition.add(collisionVector);
                        joint2.physicsNextPosition = joint2.physicsNextPosition.subtract(collisionVector);
                    }
                }
            }
        }

        for (SalamanderJoint joint : this.joints) {
            joint.finalizeTick();
        }

        if (!this.level.isClientSide()) {
            for (Interactable interactable : this.childInteractables) {
                if (interactable instanceof SalamanderBodyInteractable bodyInteractable) {
                    bodyInteractable.setPosition(bodyInteractable.segmentParent.getCenter(1.0).add(0, 0.5, 0));
                    bodyInteractable.setRotation(bodyInteractable.segmentParent.getOrientation(1.0).toMojangQuaternion());
                }
            }
            ClinkerPacketHandler.sendToClientsInChunk((LevelChunk) this.level.getChunk(this.blockPosition()), new ClientboundSalamanderSyncPacket(this));
        }
        this.setPosRaw(this.headJoint.position.x(), this.headJoint.position.y(), this.headJoint.position.z());
        this.setBoundingBox(this.makeBoundingBox());
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
        this.remove();
    }

    public class SalamanderJoint {
        public Vec3 physicsPrevPosition;
        public Vec3 pPosition;
        public Vec3 position;
        public Vec3 physicsNextPosition;
        private Vec3 acceleration;
        public final double radius;
        final List<SalamanderSegment> attachments;
        final NewSalamanderEntity entity;
        public boolean isHead;

        public SalamanderJoint(NewSalamanderEntity entity, double radius) {
            this.radius = radius;
            this.attachments = new ArrayList<>();
            this.entity = entity;
            this.pPosition = Vec3.ZERO;
            this.position = Vec3.ZERO;
            this.physicsPrevPosition = Vec3.ZERO;
            this.physicsNextPosition = Vec3.ZERO;
            this.acceleration = Vec3.ZERO;
            entity.joints.add(this);
        }

        public Vec3 getPosition(double partialTick) {
            return pPosition.lerp(position, partialTick);
        }

        protected void beginTick(float deltaTime) {
            this.pPosition = this.position;
            Vec3 velocity = this.position.subtract(this.physicsPrevPosition);
            this.physicsPrevPosition = this.position;
            this.physicsNextPosition = this.position.add(velocity).add(this.acceleration.scale(deltaTime * deltaTime));

            this.acceleration = Vec3.ZERO;
        }

        protected void finalizeTick() {
            applyCollisionConstraints();
            this.position = this.physicsNextPosition;
        }

        protected void applyCollisionConstraints() {
            Vec3 pNPos = this.physicsNextPosition.scale(1.0);
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
    }

    public class SalamanderSegment {
        public final SalamanderJoint joint1;
        public final SalamanderJoint joint2;
        public final double length;
        public final NewSalamanderEntity entity;

        public SalamanderSegment(NewSalamanderEntity entity, SalamanderJoint joint1, SalamanderJoint joint2, double length) {
            this.joint1 = joint1;
            this.joint1.attachments.add(this);
            this.joint2 = joint2;
            this.joint2.attachments.add(this);
            this.length = length;
            this.entity = entity;
            entity.segments.add(this);
        }

        public void split() {
            this.joint1.attachments.remove(this);
            this.joint2.attachments.remove(this);
            this.entity.segments.remove(this);
        }

        public boolean isHead() {
            return joint1.isHead || joint2.isHead;
        }

        public Vec3 getCenter(double partialTick) {
            return joint1.getPosition(partialTick).add(joint2.getPosition(partialTick)).scale(0.5);
        }

        // extracts rotation from angle between heading vector and up vector.
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

    public class SalamanderBodyInteractable extends Interactable {
        final NewSalamanderEntity entityParent;
        final SalamanderSegment segmentParent;

        public SalamanderBodyInteractable(NewSalamanderEntity entityParent, SalamanderSegment segmentParent, OBBCollisionShape shape) {
            super(shape);
            this.entityParent = entityParent;
            this.segmentParent = segmentParent;
        }

        @Override
        public boolean onInteract(InteractionContext interactionContext, @Nullable Entity entity) {
            return false;
        }

        @Override
        public boolean onHit(InteractionContext interactionContext, @Nullable Entity entity) {
            return false;
        }

        @Override
        public boolean onPick(InteractionContext interactionContext, @Nullable Entity entity) {
            // todo : entity picking.
            //        copy code from Minecraft#pickBlock()
            return false;
        }

        @Override
        public boolean onTouch(Entity touchingEntity) {
            return false;
        }
    }
}
