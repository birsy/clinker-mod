package birsy.clinker.core.util.rigidbody;

import birsy.clinker.core.util.MathUtils;
import com.mojang.math.Matrix3f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.world.phys.Vec3;

public class Transform {
    public Vec3 position;
    public Quaternion orientation;

    public Transform(Vec3 initialPosition, Quaternion initialOrientation) {
        this.position = initialPosition;
        this.orientation = initialOrientation;
    }

    public Transform() {
        this.position = Vec3.ZERO;
        this.orientation = Quaternion.ONE;
    }

    public Vec3 getPosition() {
        return this.position;
    }
    public void setPosition(Vec3 pos) {
        this.position = pos;
    }
    public void setPosition(float x, float y, float z) {
        this.setPosition(new Vec3(x, y, z));
    }
    public void addPosition(Vec3 pos) {
        this.position = this.position.add(pos);
    }
    public void addPosition(float x, float y, float z) {
        this.position = this.position.add(x, y, z);
    }
    
    public Quaternion getOrientation() {
        return this.orientation;
    }
    public void setOrientation(Quaternion rot) {
        this.orientation = rot;
    }

    public void rotate(Quaternion rot) {
        this.orientation.mul(rot);
    }
    public void addOrientation(Vec3 angularVel) {
        this.orientation = MathUtils.addVector(orientation, angularVel);
        this.orientation.normalize();
    }
    public void addOrientation(Quaternion rot) {
        this.orientation = MathUtils.add(orientation, rot);
        this.orientation.normalize();
    }
    public Vec3 toWorldSpace(Vec3 vec) {
        vec = MathUtils.transform(vec, this.orientation);
        vec = vec.add(this.position);
        return vec;
    }
    
    public Vec3 toLocalSpace(Vec3 vec) {
        vec = vec.subtract(this.position);
        vec = MathUtils.transform(vec, MathUtils.inverse(this.orientation));
        return vec;
    }

    public Transform copy() {
        return new Transform(this.position.add(0, 0, 0), this.orientation.copy());
    }
}
