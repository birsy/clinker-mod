package birsy.clinker.common.world.physics.rigidbody;

import birsy.clinker.core.util.JomlConversions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;

public class Transform {
    public Vec3 position;
    public Quaterniond orientation;

    public Transform(Vec3 initialPosition, Quaterniond initialOrientation) {
        this.position = initialPosition;
        this.orientation = initialOrientation;
    }

    public Transform() {
        this.position = Vec3.ZERO;
        this.orientation = new Quaterniond();
    }

    public Vec3 getPosition() {
        return this.position;
    }
    public Transform setPosition(Vec3 pos) {
        this.position = pos;
        return this;
    }
    public Transform setPosition(double x, double y, double z) {
        this.setPosition(new Vec3(x, y, z));
        return this;
    }

    public Transform addPosition(Vec3 pos) {
        this.position = this.position.add(pos);
        return this;
    }
    public Transform addPosition(double x, double y, double z) {
        this.position = this.position.add(x, y, z);
        return this;
    }

    public Quaterniond getOrientation() {
        return this.orientation;
    }
    public Transform setOrientation(Quaterniond rot) {
        this.orientation = rot;
        return this;
    }

    public Transform rotate(Quaterniond rot) {
        this.orientation.mul(rot);
        return this;
    }
    public Transform addOrientation(Vec3 angularVel) {
        this.orientation = orientation.rotateXYZ(angularVel.x(), angularVel.y(), angularVel.z());
        this.orientation.normalize();
        return this;
    }
    public Transform addOrientation(Quaterniond rot) {
        this.orientation.mul(rot);
        this.orientation.normalize();
        return this;
    }

    public Vec3 toWorldSpace(Vec3 vec) {
        Vec3 v = this.rotate(vec);
        v = v.add(this.position);
        return v;
    }
    public Vec3 toLocalSpace(Vec3 vec) {
        Vec3 v = vec.subtract(this.position);
        return rotateInverse(v);
    }

    public Vec3 rotate(Vec3 vec) {
        return JomlConversions.toMoj(this.orientation.transform(JomlConversions.toJOML(vec)));
    }

    public Vec3 rotateInverse(Vec3 vec) {
        return JomlConversions.toMoj(this.orientation.transformInverse(JomlConversions.toJOML(vec)));
    }

    public Transform lerp(Transform t, double alpha) {
        Quaterniond orientation = this.orientation.slerp(t.getOrientation(), alpha);
        orientation.normalize();
        return new Transform(this.getPosition().lerp(t.getPosition(), alpha), orientation);
    }

    public Transform copy() {
        return new Transform(this.position.add(0, 0, 0), new Quaterniond(this.orientation.x, this.orientation.y, this.orientation.z, this.orientation.w));
    }
    public CompoundTag serialize() {
        return this.serialize(null);
    }
    public CompoundTag serialize(CompoundTag tag) {
        if (tag == null) tag = new CompoundTag();
        Vec3 pos = this.getPosition();
        Quaterniond rot = this.getOrientation();

        tag.putDouble("x", pos.x());
        tag.putDouble("y", pos.y());
        tag.putDouble("z", pos.z());

        tag.putDouble("i", rot.x());
        tag.putDouble("j", rot.y());
        tag.putDouble("k", rot.z());
        tag.putDouble("r", rot.w());

        return tag;
    }

    public void deserialize(CompoundTag tag) {
        double x = tag.getDouble("x");
        double y = tag.getDouble("y");
        double z = tag.getDouble("z");

        float i = (float) tag.getDouble("i");
        float j = (float) tag.getDouble("j");
        float k = (float) tag.getDouble("k");
        float r = (float) tag.getDouble("r");

        this.setPosition(x, y, z);
        this.setOrientation(new Quaterniond(i, j, k, r));
    }

    public static Transform fromNBT(CompoundTag tag) {
        double x = tag.getDouble("x");
        double y = tag.getDouble("y");
        double z = tag.getDouble("z");

        float i = (float) tag.getDouble("i");
        float j = (float) tag.getDouble("j");
        float k = (float) tag.getDouble("k");
        float r = (float) tag.getDouble("r");

        return new Transform(new Vec3(x, y, z), new Quaterniond(i, j, k, r));
    }
}
