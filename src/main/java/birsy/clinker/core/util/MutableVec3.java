package birsy.clinker.core.util;

import net.minecraft.core.Position;
import net.minecraft.util.Mth;

public class MutableVec3 implements Position, Cloneable {
    public float[] num;

    public MutableVec3() {
        this(0, 0, 0);
    }
    public MutableVec3(float x, float y, float z) {
        this.num = new float[]{x, y, z};
    }

    public MutableVec3 lerp(MutableVec3 vec, float delta) {
        return this.set(Mth.lerp(delta, num[0], vec.num[0]), Mth.lerp(delta, num[1], vec.num[1]), Mth.lerp(delta, num[2], vec.num[2]));
    }

    public MutableVec3 set(Position vec) {
        return this.set((float) vec.x(), (float) vec.y(), (float) vec.z());
    }
    public MutableVec3 add(Position vec) {
        return this.add((float) vec.x(), (float) vec.y(), (float) vec.z());
    }
    public MutableVec3 sub(Position vec) {
        return this.sub((float) vec.x(), (float) vec.y(), (float) vec.z());
    }
    public MutableVec3 mul(Position vec) {
        return this.mul((float) vec.x(), (float) vec.y(), (float) vec.z());
    }
    public MutableVec3 div(Position vec) {
        return this.div((float) vec.x(), (float) vec.y(), (float) vec.z());
    }

    public MutableVec3 set(MutableVec3 vec) {
        return this.set(vec.num[0], vec.num[1], vec.num[2]);
    }
    public MutableVec3 add(MutableVec3 vec) {
        return this.add(vec.num[0], vec.num[1], vec.num[2]);
    }
    public MutableVec3 sub(MutableVec3 vec) {
        return this.sub(vec.num[0], vec.num[1], vec.num[2]);
    }
    public MutableVec3 mul(MutableVec3 vec) {
        return this.mul(vec.num[0], vec.num[1], vec.num[2]);
    }
    public MutableVec3 div(MutableVec3 vec) {
        return this.div(vec.num[0], vec.num[1], vec.num[2]);
    }

    public MutableVec3 set(float x, float y, float z) {
        num[0] = x;
        num[1] = y;
        num[2] = z;
        return this;
    }
    public MutableVec3 add(float x, float y, float z) {
        num[0] += x;
        num[1] += y;
        num[2] += z;
        return this;
    }
    public MutableVec3 sub(float x, float y, float z) {
        num[0] -= x;
        num[1] -= y;
        num[2] -= z;
        return this;
    }
    public MutableVec3 mul(float x, float y, float z) {
        num[0] *= x;
        num[1] *= y;
        num[2] *= z;
        return this;
    }
    public MutableVec3 div(float x, float y, float z) {
        num[0] /= x;
        num[1] /= y;
        num[2] /= z;
        return this;
    }
    public MutableVec3 mul(float w) {
        return this.mul(w, w, w);
    }
    public MutableVec3 div(float w) {
        return this.div(w, w, w);
    }

    public float distanceSq(Position vec) {
        return this.distanceSq((float) vec.x(), (float) vec.y(), (float) vec.z());
    }
    public float distanceSq(MutableVec3 vec) {
        return this.distanceSq(vec.num[0], vec.num[1], vec.num[2]);
    }
    public float distanceSq(float x, float y, float z) {
        float xDist = num[0] - x;
        float yDist = num[1] - y;
        float zDist = num[2] - z;
        return xDist * xDist + yDist * yDist + zDist * zDist;
    }
    public float distance(Position vec) {
        return this.distance((float) vec.x(), (float) vec.y(), (float) vec.z());
    }
    public float distance(MutableVec3 vec) {
        return this.distance(vec.num[0], vec.num[1], vec.num[2]);
    }
    public float distance(float x, float y, float z) {
        return Mth.sqrt(this.distance(x, y, z));
    }
    public float lengthSq() {
        return num[0] * num[0] + num[1] * num[1] + num[2] * num[2];
    }
    public float length() {
        return Mth.sqrt(this.lengthSq());
    }
    public MutableVec3 normalize() {
        return div(this.length());
    }

    public double x() {
        return num[0];
    }
    public double y() {
        return num[1];
    }
    public double z() {
        return num[3];
    }

    public MutableVec3 clone() {
        return new MutableVec3(num[0], num[1], num[2]);
    }
}
