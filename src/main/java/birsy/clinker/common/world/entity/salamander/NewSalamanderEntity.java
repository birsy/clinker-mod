package birsy.clinker.common.world.entity.salamander;

import birsy.clinker.core.util.Quaterniond;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class NewSalamanderEntity extends Entity {
    public SalamanderJoint headJoint;
    public SalamanderSegment headSegment;

    public List<SalamanderJoint> joints;
    public List<SalamanderSegment> segments;

    public NewSalamanderEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.joints = new ArrayList<>();
        this.segments = new ArrayList<>();

        SalamanderJoint previousJoint = new SalamanderJoint(this, 0.5);
        this.headJoint = previousJoint;
        previousJoint.isHead = true;

        for (int i = 1; i < 10; i++) {
            SalamanderJoint joint = new SalamanderJoint(this, 0.5);
            joint.position = previousJoint.position.add(0, 0, 1.0);
            SalamanderSegment segment = new SalamanderSegment(this, previousJoint, joint, 1.0);
            if (i >= 1) this.headSegment = segment;
            previousJoint = joint;
        }
    }

    @Override
    public void setPos(double x, double y, double z) {
        if (this.joints == null) { super.setPos(x, y, z); return; }
        // gets the position of each segment relative to the core.
        Vec3[] relativePositions = new Vec3[this.joints.size()];
        for (int i = 0; i < relativePositions.length; i++) {
            relativePositions[i] = this.joints.get(i).position.subtract(this.position());
        }
        // repositions the core.
        super.setPos(x, y, z);
        // repositions all the segments relative to the core again.
        for (int i = 0; i < joints.size(); i++) {
            this.joints.get(i).position = relativePositions[i].add(this.position());
        }
    }

    @Override
    public void moveTo(double x, double y, double z, float pYRot, float pXRot) {
        if (this.joints == null) { super.setPos(x, y, z); return; }
        // gets the position of each segment relative to the core.
        Vec3[] relativePositions = new Vec3[this.joints.size()];
        for (int i = 0; i < relativePositions.length; i++) {
            relativePositions[i] = this.joints.get(i).position.subtract(this.position());
        }
        // repositions the core.
        super.moveTo(x, y, z, pYRot, pXRot);
        // repositions all the segments relative to the core again.
        for (int i = 0; i < joints.size(); i++) {
            this.joints.get(i).position = relativePositions[i].add(this.position());
        }
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    public class SalamanderJoint {
        public Vec3 pPosition;
        public Vec3 position;
        public final double radius;
        final List<SalamanderSegment> attachments;
        final NewSalamanderEntity entity;
        public boolean isHead;

        public SalamanderJoint(NewSalamanderEntity entity, double radius) {
            this.radius = radius;
            this.attachments = new ArrayList<>();
            this.entity = entity;
            this.position = Vec3.ZERO;
            this.pPosition = Vec3.ZERO;
            entity.joints.add(this);
        }

        public Vec3 getPosition(double partialTick) {
            return pPosition.lerp(position, partialTick);
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
            Vec3 direction = joint2.getPosition(partialTick).subtract(joint1.getPosition(partialTick)).normalize();
            Vec3 upVec = new Vec3(0, 1, 0);

            Vec3 axis = direction.cross(upVec);
            double angle = Math.acos(direction.dot(upVec));
            return new Quaterniond(axis, angle);
        }
    }
}
