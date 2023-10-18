package birsy.clinker.core.util;

import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.util.Mth;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.NumberFormat;

/**
 * Quaternion of 4 single-precision floats which can represent rotation and uniform scaling.
 *
 * @author Richard Greenlees
 * @author Kai Burjack
 */
public class Quaternionf implements Externalizable, Cloneable {

    private static final long serialVersionUID = 1L;

    /**
     * The first component of the vector part.
     */
    public float x;
    /**
     * The second component of the vector part.
     */
    public float y;
    /**
     * The third component of the vector part.
     */
    public float z;
    /**
     * The real/scalar part of the quaternion.
     */
    public float w;

    /**
     * Create a new {@link Quaternionf} and initialize it with <code>(x=0, y=0, z=0, w=1)</code>,
     * where <code>(x, y, z)</code> is the vector part of the quaternion and <code>w</code> is the real/scalar part.
     */
    public Quaternionf() {
        this.w = 1.0F;
    }

    /**
     * Create a new {@link Quaternionf} and initialize its components to the given values.
     *
     * @param x
     *          the first component of the imaginary part
     * @param y
     *          the second component of the imaginary part
     * @param z
     *          the third component of the imaginary part
     * @param w
     *          the real part
     */
    public Quaternionf(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    /**
     * Create a new {@link Quaternionf} and initialize its components to the same values as the given {@link Quaternionf}.
     *
     * @param source
     *          the {@link Quaternionf} to take the component values from
     */
    public Quaternionf(Quaternionf source) {
        x = source.x();
        y = source.y();
        z = source.z();
        w = source.w();
    }

    /**
     * Create a new {@link Quaternionf} and initialize its components to the same values as the given {@link Quaternionf}.
     *
     * @param source
     *          the {@link Quaternionf} to take the component values from
     */
    public Quaternionf(Quaternion source) {
        x = source.i();
        y = source.j();
        z = source.k();
        w = source.r();
    }

    public Quaternionf(Vector3f axis, float angle) {
        float s = Mth.sin(angle * 0.5F);
        x = axis.x() * s;
        y = axis.y() * s;
        z = axis.z() * s;
        w = Mth.cos(angle * 0.5F);
    }

    public Quaternionf(MutableVec3 axis, float angle) {
        float s = Mth.sin(angle * 0.5F);
        x = axis.x() * s;
        y = axis.y() * s;
        z = axis.z() * s;
        w = Mth.cos(angle * 0.5F);
    }

    public Quaternionf(AxisAnglef aa) {
        float s = Mth.sin(aa.angle * 0.5F);
        x = aa.x() * s;
        y = aa.y() * s;
        z = aa.z() * s;
        w = Mth.cos(aa.angle * 0.5F);
    }

    /**
     * @return the first component of the vector part
     */
    public float x() {
        return this.x;
    }

    /**
     * @return the second component of the vector part
     */
    public float y() {
        return this.y;
    }

    /**
     * @return the third component of the vector part
     */
    public float z() {
        return this.z;
    }

    /**
     * @return the real/scalar part of the quaternion
     */
    public float w() {
        return this.w;
    }

    /**
     * Normalize this quaternion.
     *
     * @return this
     */
    public Quaternionf normalize() {
        float invNorm = 1 / Mth.sqrt(lengthSquared());
        x *= invNorm;
        y *= invNorm;
        z *= invNorm;
        w *= invNorm;
        return this;
    }

    public Quaternionf normalize(Quaternionf dest) {
        float invNorm = 1 / Mth.sqrt(lengthSquared());
        dest.x = x * invNorm;
        dest.y = y * invNorm;
        dest.z = z * invNorm;
        dest.w = w * invNorm;
        return dest;
    }

    /**
     * Add the quaternion <code>(x, y, z, w)</code> to this quaternion.
     *
     * @param x
     *          the x component of the vector part
     * @param y
     *          the y component of the vector part
     * @param z
     *          the z component of the vector part
     * @param w
     *          the real/scalar component
     * @return this
     */
    public Quaternionf add(float x, float y, float z, float w) {
        return add(x, y, z, w, this);
    }

    public Quaternionf add(float x, float y, float z, float w, Quaternionf dest) {
        dest.x = this.x + x;
        dest.y = this.y + y;
        dest.z = this.z + z;
        dest.w = this.w + w;
        return dest;
    }

    /**
     * Add <code>q2</code> to this quaternion.
     *
     * @param q2
     *          the quaternion to add to this
     * @return this
     */
    public Quaternionf add(Quaternionf q2) {
        x += q2.x();
        y += q2.y();
        z += q2.z();
        w += q2.w();
        return this;
    }

    public Quaternionf add(Quaternionf q2, Quaternionf dest) {
        dest.x = x + q2.x();
        dest.y = y + q2.y();
        dest.z = z + q2.z();
        dest.w = w + q2.w();
        return dest;
    }

    public float dot(Quaternionf otherQuat) {
        return this.x * otherQuat.x() + this.y * otherQuat.y() + this.z * otherQuat.z() + this.w * otherQuat.w();
    }

    public float angle() {
        return (float) (2.0F * Math.acos(w));
    }

    public AxisAnglef get() {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        float w = this.w;
        if (w > 1.0F) {
            float invNorm = 1 / Mth.sqrt(lengthSquared());
            x *= invNorm;
            y *= invNorm;
            z *= invNorm;
            w *= invNorm;
        }
        float s = Mth.sqrt(1.0F - w * w);
        if (s < 0.001F) {
            return new AxisAnglef(new Vector3f(x, y, z), (float) (2.0F * Math.acos(w)));
        } else {
            s = 1.0F / s;
            return new AxisAnglef(new Vector3f(x * s, y * s, z * s), (float) (2.0F * Math.acos(w)));
        }
    }

    /**
     * Set this quaternion to the new values.
     *
     * @param x
     *          the new value of x
     * @param y
     *          the new value of y
     * @param z
     *          the new value of z
     * @param w
     *          the new value of w
     * @return this
     */
    public Quaternionf set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }


    /**
     * Set this quaternion to be a copy of q.
     *
     * @param q
     *          the {@link Quaternionf} to copy
     * @return this
     */
    public Quaternionf set(Quaternionf q) {
        x = q.x();
        y = q.y();
        z = q.z();
        w = q.w();
        return this;
    }

    /**
     * Set this {@link Quaternionf} to be equivalent to the given
     * {@link AxisAnglef}.
     *
     * @param axisAngle
     *            the {@link AxisAnglef}
     * @return this
     */
    public Quaternionf set(AxisAnglef axisAngle) {
        return setAngleAxis(axisAngle.angle, axisAngle.x(), axisAngle.y(), axisAngle.z());
    }
    
    /**
     * Set this quaternion to a rotation equivalent to the supplied axis and
     * angle (in radians).
     * <p>
     * This method assumes that the given rotation axis <code>(x, y, z)</code> is already normalized
     *
     * @param angle
     *          the angle in radians
     * @param x
     *          the x-component of the normalized rotation axis
     * @param y
     *          the y-component of the normalized rotation axis
     * @param z
     *          the z-component of the normalized rotation axis
     * @return this
     */
    public Quaternionf setAngleAxis(float angle, float x, float y, float z) {
        float s = Mth.sin(angle * 0.5F);
        this.x = x * s;
        this.y = y * s;
        this.z = z * s;
        this.w = Mth.cos(angle * 0.5F);
        return this;
    }

    /**
     * Set this quaternion to be a representation of the supplied axis and
     * angle (in radians).
     *
     * @param angle
     *          the angle in radians
     * @param axis
     *          the rotation axis
     * @return this
     */
    public Quaternionf setAngleAxis(float angle, Vector3f axis) {
        return setAngleAxis(angle, axis.x(), axis.y(), axis.z());
    }

    public Quaternionf setFromUnnormalized(Matrix4f mat) {
        setFromUnnormalized(mat.m00, mat.m01, mat.m02, mat.m10, mat.m11, mat.m12, mat.m20, mat.m21, mat.m22);
        return this;
    }

    public void setFromUnnormalized(float m00, float m01, float m02, float m10, float m11, float m12, float m20, float m21, float m22) {
        float nm00 = m00, nm01 = m01, nm02 = m02;
        float nm10 = m10, nm11 = m11, nm12 = m12;
        float nm20 = m20, nm21 = m21, nm22 = m22;
        float lenX = 1 / Mth.sqrt(m00 * m00 + m01 * m01 + m02 * m02);
        float lenY = 1 / Mth.sqrt(m10 * m10 + m11 * m11 + m12 * m12);
        float lenZ = 1 / Mth.sqrt(m20 * m20 + m21 * m21 + m22 * m22);
        nm00 *= lenX; nm01 *= lenX; nm02 *= lenX;
        nm10 *= lenY; nm11 *= lenY; nm12 *= lenY;
        nm20 *= lenZ; nm21 *= lenZ; nm22 *= lenZ;
        setFromNormalized(nm00, nm01, nm02, nm10, nm11, nm12, nm20, nm21, nm22);
    }

    public Quaternionf setFromNormalized(float m00, float m01, float m02, float m10, float m11, float m12, float m20, float m21, float m22) {
        float t;
        float tr = m00 + m11 + m22;
        if (tr >= 0.0F) {
            t = Mth.sqrt(tr + 1.0F);
            w = t * 0.5F;
            t = 0.5F / t;
            x = (m12 - m21) * t;
            y = (m20 - m02) * t;
            z = (m01 - m10) * t;
        } else {
            if (m00 >= m11 && m00 >= m22) {
                t = Mth.sqrt(m00 - (m11 + m22) + 1.0F);
                x = t * 0.5F;
                t = 0.5F / t;
                y = (m10 + m01) * t;
                z = (m02 + m20) * t;
                w = (m12 - m21) * t;
            } else if (m11 > m22) {
                t = Mth.sqrt(m11 - (m22 + m00) + 1.0F);
                y = t * 0.5F;
                t = 0.5F / t;
                z = (m21 + m12) * t;
                x = (m10 + m01) * t;
                w = (m20 - m02) * t;
            } else {
                t = Mth.sqrt(m22 - (m00 + m11) + 1.0F);
                z = t * 0.5F;
                t = 0.5F / t;
                x = (m02 + m20) * t;
                y = (m21 + m12) * t;
                w = (m01 - m10) * t;
            }

        }
        return this;

    }
    
    /**
     * Multiply this quaternion by <code>q</code>.
     * <p>
     * If <code>T</code> is <code>this</code> and <code>Q</code> is the given
     * quaternion, then the resulting quaternion <code>R</code> is:
     * <p>
     * <code>R = T * Q</code>
     * <p>
     * So, this method uses post-multiplication like the matrix classes, resulting in a
     * vector to be transformed by <code>Q</code> first, and then by <code>T</code>.
     *
     * @param q
     *          the quaternion to multiply <code>this</code> by
     * @return this
     */
    public Quaternionf mul(Quaternionf q) {
        return mul(q, this);
    }

    public Quaternionf mul(Quaternionf q, Quaternionf dest) {
        return mul(q.x(), q.y(), q.z(), q.w(), dest);
    }

    /**
     * Multiply this quaternion by the quaternion represented via <code>(qx, qy, qz, qw)</code>.
     * <p>
     * If <code>T</code> is <code>this</code> and <code>Q</code> is the given
     * quaternion, then the resulting quaternion <code>R</code> is:
     * <p>
     * <code>R = T * Q</code>
     * <p>
     * So, this method uses post-multiplication like the matrix classes, resulting in a
     * vector to be transformed by <code>Q</code> first, and then by <code>T</code>.
     *
     * @param qx
     *          the x component of the quaternion to multiply <code>this</code> by
     * @param qy
     *          the y component of the quaternion to multiply <code>this</code> by
     * @param qz
     *          the z component of the quaternion to multiply <code>this</code> by
     * @param qw
     *          the w component of the quaternion to multiply <code>this</code> by
     * @return this
     */
    public Quaternionf mul(float qx, float qy, float qz, float qw) {
        return mul(qx, qy, qz, qw, this);
    }

    public Quaternionf mul(float qx, float qy, float qz, float qw, Quaternionf dest) {
        return dest.set(Math.fma(w, qx, Math.fma(x, qw, Math.fma(y, qz, -z * qy))),
                Math.fma(w, qy, Math.fma(-x, qz, Math.fma(y, qw, z * qx))),
                Math.fma(w, qz, Math.fma(x, qy, Math.fma(-y, qx, z * qw))),
                Math.fma(w, qw, Math.fma(-x, qx, Math.fma(-y, qy, -z * qz))));
    }

    /**
     * Multiply this quaternion by the given scalar.
     * <p>
     * This method multiplies all of the four components by the specified scalar.
     *
     * @param f
     *          the factor to multiply all components by
     * @return this
     */
    public Quaternionf mul(float f) {
        return mul(f, this);
    }

    public Quaternionf mul(float f, Quaternionf dest) {
        dest.x = x * f;
        dest.y = y * f;
        dest.z = z * f;
        dest.w = w * f;
        return dest;
    }

    /**
     * Pre-multiply this quaternion by <code>q</code>.
     * <p>
     * If <code>T</code> is <code>this</code> and <code>Q</code> is the given quaternion, then the resulting quaternion <code>R</code> is:
     * <p>
     * <code>R = Q * T</code>
     * <p>
     * So, this method uses pre-multiplication, resulting in a vector to be transformed by <code>T</code> first, and then by <code>Q</code>.
     *
     * @param q
     *            the quaternion to pre-multiply <code>this</code> by
     * @return this
     */
    public Quaternionf premul(Quaternionf q) {
        return premul(q, this);
    }

    public Quaternionf premul(Quaternionf q, Quaternionf dest) {
        return premul(q.x(), q.y(), q.z(), q.w(), dest);
    }

    /**
     * Pre-multiply this quaternion by the quaternion represented via <code>(qx, qy, qz, qw)</code>.
     * <p>
     * If <code>T</code> is <code>this</code> and <code>Q</code> is the given quaternion, then the resulting quaternion <code>R</code> is:
     * <p>
     * <code>R = Q * T</code>
     * <p>
     * So, this method uses pre-multiplication, resulting in a vector to be transformed by <code>T</code> first, and then by <code>Q</code>.
     *
     * @param qx
     *          the x component of the quaternion to multiply <code>this</code> by
     * @param qy
     *          the y component of the quaternion to multiply <code>this</code> by
     * @param qz
     *          the z component of the quaternion to multiply <code>this</code> by
     * @param qw
     *          the w component of the quaternion to multiply <code>this</code> by
     * @return this
     */
    public Quaternionf premul(float qx, float qy, float qz, float qw) {
        return premul(qx, qy, qz, qw, this);
    }

    public Quaternionf premul(float qx, float qy, float qz, float qw, Quaternionf dest) {
        return dest.set(Math.fma(qw, x, Math.fma(qx, w, Math.fma(qy, z, -qz * y))),
                Math.fma(qw, y, Math.fma(-qx, z, Math.fma(qy, w, qz * x))),
                Math.fma(qw, z, Math.fma(qx, y, Math.fma(-qy, x, qz * w))),
                Math.fma(qw, w, Math.fma(-qx, x, Math.fma(-qy, y, -qz * z))));
    }

    
    public Vector3f transformPositiveX() {
        float ww = w * w;
        float xx = x * x;
        float yy = y * y;
        float zz = z * z;
        float zw = z * w;
        float xy = x * y;
        float xz = x * z;
        float yw = y * w;
        return new Vector3f(ww + xx - zz - yy, xy + zw + zw + xy, xz - yw + xz - yw);
    }

    public Vector3f transformUnitPositiveX() {
        float yy = y * y;
        float zz = z * z;
        float xy = x * y;
        float xz = x * z;
        float yw = y * w;
        float zw = z * w;
        return new Vector3f(1.0F - yy - yy - zz - zz, xy + zw + xy + zw, xz - yw + xz - yw);
    }
    
    public Vector3f transformPositiveY() {
        float ww = w * w;
        float xx = x * x;
        float yy = y * y;
        float zz = z * z;
        float zw = z * w;
        float xy = x * y;
        float yz = y * z;
        float xw = x * w;
        return new Vector3f(-zw + xy - zw + xy, yy - zz + ww - xx, yz + yz + xw + xw);
    }
    
    public Vector3f transformUnitPositiveY() {
        float xx = x * x;
        float zz = z * z;
        float xy = x * y;
        float yz = y * z;
        float xw = x * w;
        float zw = z * w;
        float nx = xy - zw + xy - zw;
        float ny = 1.0F - xx - xx - zz - zz;
        float nz = yz + yz + xw + xw;
        return new Vector3f(nx, ny, nz);
    }

    public Vector3f transformPositiveZ() {
        float ww = w * w;
        float xx = x * x;
        float yy = y * y;
        float zz = z * z;
        float xz = x * z;
        float yw = y * w;
        float yz = y * z;
        float xw = x * w;
        float nx = yw + xz + xz + yw;
        float ny = yz + yz - xw - xw;
        float nz = zz - yy - xx + ww;
        return new Vector3f(nx, ny, nz);
    }

    public Vector3f transformUnitPositiveZ() {
        float xx = x * x;
        float yy = y * y;
        float xz = x * z;
        float yz = y * z;
        float xw = x * w;
        float yw = y * w;
        float nx = xz + yw + xz + yw;
        float ny = yz + yz - xw - xw;
        float nz = 1.0F - xx - xx - yy - yy;
        return new Vector3f(nx, ny, nz);
    }
    
    public Vector3f transform(Vector3f vec) {
        return transform(vec.x(), vec.y(), vec.z());
    }

    public MutableVec3 transform(MutableVec3 vec) {
        float x = vec.num[0], y = vec.num[1], z = vec.num[2];
        float xx = this.x * this.x, yy = this.y * this.y, zz = this.z * this.z, ww = this.w * this.w;
        float xy = this.x * this.y, xz = this.x * this.z, yz = this.y * this.z, xw = this.x * this.w;
        float zw = this.z * this.w, yw = this.y * this.w, k = 1 / (xx + yy + zz + ww);
        return vec.set(Math.fma((xx - yy - zz + ww) * k, x, Math.fma(2 * (xy - zw) * k, y, (2 * (xz + yw) * k) * z)),
                Math.fma(2 * (xy + zw) * k, x, Math.fma((yy - xx - zz + ww) * k, y, (2 * (yz - xw) * k) * z)),
                Math.fma(2 * (xz - yw) * k, x, Math.fma(2 * (yz + xw) * k, y, ((zz - xx - yy + ww) * k) * z)));
    }

    public Vector3f transformInverse(Vector3f vec) {
        return transformInverse(vec.x(), vec.y(), vec.z());
    }

    public Vector3f transform(float x, float y, float z) {
        float xx = this.x * this.x, yy = this.y * this.y, zz = this.z * this.z, ww = this.w * this.w;
        float xy = this.x * this.y, xz = this.x * this.z, yz = this.y * this.z, xw = this.x * this.w;
        float zw = this.z * this.w, yw = this.y * this.w, k = 1 / (xx + yy + zz + ww);
        return new Vector3f(Math.fma((xx - yy - zz + ww) * k, x, Math.fma(2 * (xy - zw) * k, y, (2 * (xz + yw) * k) * z)),
                Math.fma(2 * (xy + zw) * k, x, Math.fma((yy - xx - zz + ww) * k, y, (2 * (yz - xw) * k) * z)),
                Math.fma(2 * (xz - yw) * k, x, Math.fma(2 * (yz + xw) * k, y, ((zz - xx - yy + ww) * k) * z)));
    }

    public Vector3f transformInverse(float x, float y, float z) {
        float n = 1.0F / Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
        float qx = this.x * n, qy = this.y * n, qz = this.z * n, qw = this.w * n;
        float xx = qx * qx, yy = qy * qy, zz = qz * qz, ww = qw * qw;
        float xy = qx * qy, xz = qx * qz, yz = qy * qz, xw = qx * qw;
        float zw = qz * qw, yw = qy * qw, k = 1 / (xx + yy + zz + ww);
        return new Vector3f(Math.fma((xx - yy - zz + ww) * k, x, Math.fma(2 * (xy + zw) * k, y, (2 * (xz - yw) * k) * z)),
                Math.fma(2 * (xy - zw) * k, x, Math.fma((yy - xx - zz + ww) * k, y, (2 * (yz + xw) * k) * z)),
                Math.fma(2 * (xz + yw) * k, x, Math.fma(2 * (yz - xw) * k, y, ((zz - xx - yy + ww) * k) * z)));
    }

    public Vector3f transformUnit(Vector3f vec) {
        return transformUnit(vec.x(), vec.y(), vec.z());
    }

    public Vector3f transformInverseUnit(Vector3f vec) {
        return transformInverseUnit(vec.x(), vec.y(), vec.z());
    }

    public Vector3f transformUnit(float x, float y, float z) {
        float xx = this.x * this.x, xy = this.x * this.y, xz = this.x * this.z;
        float xw = this.x * this.w, yy = this.y * this.y, yz = this.y * this.z;
        float yw = this.y * this.w, zz = this.z * this.z, zw = this.z * this.w;
        return new Vector3f(Math.fma(Math.fma(-2, yy + zz, 1), x, Math.fma(2 * (xy - zw), y, (2 * (xz + yw)) * z)),
                Math.fma(2 * (xy + zw), x, Math.fma(Math.fma(-2, xx + zz, 1), y, (2 * (yz - xw)) * z)),
                Math.fma(2 * (xz - yw), x, Math.fma(2 * (yz + xw), y, Math.fma(-2, xx + yy, 1) * z)));
    }

    public Vector3f transformInverseUnit(float x, float y, float z) {
        float xx = this.x * this.x, xy = this.x * this.y, xz = this.x * this.z;
        float xw = this.x * this.w, yy = this.y * this.y, yz = this.y * this.z;
        float yw = this.y * this.w, zz = this.z * this.z, zw = this.z * this.w;
        return new Vector3f(Math.fma(Math.fma(-2, yy + zz, 1), x, Math.fma(2 * (xy + zw), y, (2 * (xz - yw)) * z)),
                Math.fma(2 * (xy - zw), x, Math.fma(Math.fma(-2, xx + zz, 1), y, (2 * (yz + xw)) * z)),
                Math.fma(2 * (xz + yw), x, Math.fma(2 * (yz - xw), y, Math.fma(-2, xx + yy, 1) * z)));
    }
    
    public Quaternionf invert(Quaternionf dest) {
        float invNorm = 1.0F / lengthSquared();
        dest.x = -x * invNorm;
        dest.y = -y * invNorm;
        dest.z = -z * invNorm;
        dest.w = w * invNorm;
        return dest;
    }

    /**
     * Invert this quaternion and {@link #normalize() normalize} it.
     * <p>
     * If this quaternion is already normalized, then {@link #conjugate()} should be used instead.
     *
     * @see #conjugate()
     *
     * @return this
     */
    public Quaternionf invert() {
        return invert(this);
    }

    public Quaternionf div(Quaternionf b, Quaternionf dest) {
        float invNorm = 1.0F / Math.fma(b.x(), b.x(), Math.fma(b.y(), b.y(), Math.fma(b.z(), b.z(), b.w() * b.w())));
        float x = -b.x() * invNorm;
        float y = -b.y() * invNorm;
        float z = -b.z() * invNorm;
        float w = b.w() * invNorm;
        return dest.set(Math.fma(this.w, x, Math.fma(this.x, w, Math.fma(this.y, z, -this.z * y))),
                Math.fma(this.w, y, Math.fma(-this.x, z, Math.fma(this.y, w, this.z * x))),
                Math.fma(this.w, z, Math.fma(this.x, y, Math.fma(-this.y, x, this.z * w))),
                Math.fma(this.w, w, Math.fma(-this.x, x, Math.fma(-this.y, y, -this.z * z))));
    }

    /**
     * Divide <code>this</code> quaternion by <code>b</code>.
     * <p>
     * The division expressed using the inverse is performed in the following way:
     * <p>
     * <code>this = this * b^-1</code>, where <code>b^-1</code> is the inverse of <code>b</code>.
     *
     * @param b
     *          the {@link Quaternionf} to divide this by
     * @return this
     */
    public Quaternionf div(Quaternionf b) {
        return div(b, this);
    }

    /**
     * Conjugate this quaternion.
     *
     * @return this
     */
    public Quaternionf conjugate() {
        x = -x;
        y = -y;
        z = -z;
        return this;
    }

    public Quaternionf conjugate(Quaternionf dest) {
        dest.x = -x;
        dest.y = -y;
        dest.z = -z;
        dest.w = w;
        return dest;
    }

    /**
     * Set this quaternion to the identity.
     *
     * @return this
     */
    public Quaternionf identity() {
        x = 0.0F;
        y = 0.0F;
        z = 0.0F;
        w = 1.0F;
        return this;
    }

    public float lengthSquared() {
        return Math.fma(x, x, Math.fma(y, y, Math.fma(z, z, w * w)));
    }

    /**
     * Set this quaternion from the supplied euler angles (in radians) with rotation order XYZ.
     * <p>
     * This method is equivalent to calling: <code>rotationX(angleX).rotateY(angleY).rotateZ(angleZ)</code>
     * <p>
     * Reference: <a href="http://gamedev.stackexchange.com/questions/13436/glm-euler-angles-to-quaternion#answer-13446">this stackexchange answer</a>
     *
     * @param angleX
     *          the angle in radians to rotate about x
     * @param angleY
     *          the angle in radians to rotate about y
     * @param angleZ
     *          the angle in radians to rotate about z
     * @return this
     */
    public Quaternionf rotationXYZ(float angleX, float angleY, float angleZ) {
        float sx = Mth.sin(angleX * 0.5F);
        float cx = Mth.cos(angleX * 0.5F);
        float sy = Mth.sin(angleY * 0.5F);
        float cy = Mth.cos(angleY * 0.5F);
        float sz = Mth.sin(angleZ * 0.5F);
        float cz = Mth.cos(angleZ * 0.5F);

        float cycz = cy * cz;
        float sysz = sy * sz;
        float sycz = sy * cz;
        float cysz = cy * sz;
        w = cx*cycz - sx*sysz;
        x = sx*cycz + cx*sysz;
        y = cx*sycz - sx*cysz;
        z = cx*cysz + sx*sycz;

        return this;
    }

    /**
     * Set this quaternion from the supplied euler angles (in radians) with rotation order ZYX.
     * <p>
     * This method is equivalent to calling: <code>rotationZ(angleZ).rotateY(angleY).rotateX(angleX)</code>
     * <p>
     * Reference: <a href="http://gamedev.stackexchange.com/questions/13436/glm-euler-angles-to-quaternion#answer-13446">this stackexchange answer</a>
     *
     * @param angleX
     *          the angle in radians to rotate about x
     * @param angleY
     *          the angle in radians to rotate about y
     * @param angleZ
     *          the angle in radians to rotate about z
     * @return this
     */
    public Quaternionf rotationZYX(float angleZ, float angleY, float angleX) {
        float sx = Mth.sin(angleX * 0.5F);
        float cx = Mth.cos(angleX * 0.5F);
        float sy = Mth.sin(angleY * 0.5F);
        float cy = Mth.cos(angleY * 0.5F);
        float sz = Mth.sin(angleZ * 0.5F);
        float cz = Mth.cos(angleZ * 0.5F);

        float cycz = cy * cz;
        float sysz = sy * sz;
        float sycz = sy * cz;
        float cysz = cy * sz;
        w = cx*cycz + sx*sysz;
        x = sx*cycz - cx*sysz;
        y = cx*sycz + sx*cysz;
        z = cx*cysz - sx*sycz;

        return this;
    }

    /**
     * Set this quaternion from the supplied euler angles (in radians) with rotation order YXZ.
     * <p>
     * This method is equivalent to calling: <code>rotationY(angleY).rotateX(angleX).rotateZ(angleZ)</code>
     * <p>
     * Reference: <a href="https://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles">https://en.wikipedia.org</a>
     *
     * @param angleY
     *          the angle in radians to rotate about y
     * @param angleX
     *          the angle in radians to rotate about x
     * @param angleZ
     *          the angle in radians to rotate about z
     * @return this
     */
    public Quaternionf rotationYXZ(float angleY, float angleX, float angleZ) {
        float sx = Mth.sin(angleX * 0.5F);
        float cx = Mth.cos(angleX * 0.5F);
        float sy = Mth.sin(angleY * 0.5F);
        float cy = Mth.cos(angleY * 0.5F);
        float sz = Mth.sin(angleZ * 0.5F);
        float cz = Mth.cos(angleZ * 0.5F);

        float x = cy * sx;
        float y = sy * cx;
        float z = sy * sx;
        float w = cy * cx;
        this.x = x * cz + y * sz;
        this.y = y * cz - x * sz;
        this.z = w * sz - z * cz;
        this.w = w * cz + z * sz;

        return this;
    }

    /**
     * Interpolate between <code>this</code> {@link #normalize() unit} quaternion and the specified
     * <code>target</code> {@link #normalize() unit} quaternion using spherical linear interpolation using the specified interpolation factor <code>alpha</code>.
     * <p>
     * This method resorts to non-spherical linear interpolation when the absolute dot product between <code>this</code> and <code>target</code> is
     * below <code>1E-6</code>.
     *
     * @param target
     *          the target of the interpolation, which should be reached with <code>alpha = 1.0F</code>
     * @param alpha
     *          the interpolation factor, within <code>[0..1]</code>
     * @return this
     */
    public Quaternionf slerp(Quaternionf target, float alpha) {
        return slerp(target, alpha, this);
    }

    public Quaternionf slerp(Quaternionf target, float alpha, Quaternionf dest) {
        float cosom = Math.fma(x, target.x(), Math.fma(y, target.y(), Math.fma(z, target.z(), w * target.w())));
        float absCosom = Math.abs(cosom);
        float scale0, scale1;
        if (1.0F - absCosom > 1E-6) {
            float sinSqr = 1.0F - absCosom * absCosom;
            float sinom = 1 / Mth.sqrt(sinSqr);
            float omega = (float) Math.atan2(sinSqr * sinom, absCosom);
            scale0 = Mth.sin((1.0F - alpha) * omega) * sinom;
            scale1 = Mth.sin(alpha * omega) * sinom;
        } else {
            scale0 = 1.0F - alpha;
            scale1 = alpha;
        }
        scale1 = cosom >= 0.0F ? scale1 : -scale1;
        dest.x = Math.fma(scale0, x, scale1 * target.x());
        dest.y = Math.fma(scale0, y, scale1 * target.y());
        dest.z = Math.fma(scale0, z, scale1 * target.z());
        dest.w = Math.fma(scale0, w, scale1 * target.w());
        return dest;
    }

    /**
     * Interpolate between all of the quaternions given in <code>qs</code> via spherical linear interpolation using the specified interpolation factors <code>weights</code>,
     * and store the result in <code>dest</code>.
     * <p>
     * This method will interpolate between each two successive quaternions via {@link #slerp(Quaternionf, float)} using their relative interpolation weights.
     * <p>
     * This method resorts to non-spherical linear interpolation when the absolute dot product of any two interpolated quaternions is below <code>1E-6f</code>.
     * <p>
     * Reference: <a href="http://gamedev.stackexchange.com/questions/62354/method-for-interpolation-between-3-quaternions#answer-62356">http://gamedev.stackexchange.com/</a>
     *
     * @param qs
     *          the quaternions to interpolate over
     * @param weights
     *          the weights of each individual quaternion in <code>qs</code>
     * @param dest
     *          will hold the result
     * @return dest
     */
    public static Quaternionf slerp(Quaternionf[] qs, float[] weights, Quaternionf dest) {
        dest.set(qs[0]);
        float w = weights[0];
        for (int i = 1; i < qs.length; i++) {
            float w0 = w;
            float w1 = weights[i];
            float rw1 = w1 / (w0 + w1);
            w += w1;
            dest.slerp(qs[i], rw1);
        }
        return dest;
    }

    /**
     * Apply scaling to this quaternion, which results in any vector transformed by this quaternion to change
     * its length by the given <code>factor</code>.
     *
     * @param factor
     *          the scaling factor
     * @return this
     */
    public Quaternionf scale(float factor) {
        return scale(factor, this);
    }

    public Quaternionf scale(float factor, Quaternionf dest) {
        float sqrt = Mth.sqrt(factor);
        dest.x = sqrt * x;
        dest.y = sqrt * y;
        dest.z = sqrt * z;
        dest.w = sqrt * w;
        return dest;
    }

    /**
     * Set this quaternion to represent scaling, which results in a transformed vector to change
     * its length by the given <code>factor</code>.
     *
     * @param factor
     *          the scaling factor
     * @return this
     */
    public Quaternionf scaling(float factor) {
        float sqrt = Mth.sqrt(factor);
        this.x = 0.0F;
        this.y = 0.0F;
        this.z = 0.0F;
        this.w = sqrt;
        return this;
    }

    /**
     * Integrate the rotation given by the angular velocity <code>(vx, vy, vz)</code> around the x, y and z axis, respectively,
     * with respect to the given elapsed time delta <code>dt</code> and add the differentiate rotation to the rotation represented by this quaternion.
     * <p>
     * This method pre-multiplies the rotation given by <code>dt</code> and <code>(vx, vy, vz)</code> by <code>this</code>, so
     * the angular velocities are always relative to the local coordinate system of the rotation represented by <code>this</code> quaternion.
     * <p>
     * This method is equivalent to calling: <code>rotateLocal(dt * vx, dt * vy, dt * vz)</code>
     * <p>
     * Reference: <a href="http://physicsforgames.blogspot.de/2010/02/quaternions.html">http://physicsforgames.blogspot.de/</a>
     *
     * @param dt
     *          the delta time
     * @param vx
     *          the angular velocity around the x axis
     * @param vy
     *          the angular velocity around the y axis
     * @param vz
     *          the angular velocity around the z axis
     * @return this
     */
    public Quaternionf integrate(float dt, float vx, float vy, float vz) {
        return integrate(dt, vx, vy, vz, this);
    }

    public Quaternionf integrate(float dt, float vx, float vy, float vz, Quaternionf dest) {
        float thetaX = dt * vx * 0.5F;
        float thetaY = dt * vy * 0.5F;
        float thetaZ = dt * vz * 0.5F;
        float thetaMagSq = thetaX * thetaX + thetaY * thetaY + thetaZ * thetaZ;
        float s;
        float dqX, dqY, dqZ, dqW;
        if (thetaMagSq * thetaMagSq / 24.0F < 1E-8) {
            dqW = 1.0F - thetaMagSq * 0.5F;
            s = 1.0F - thetaMagSq / 6.0F;
        } else {
            float thetaMag = Mth.sqrt(thetaMagSq);
            float sin = Mth.sin(thetaMag);
            s = sin / thetaMag;
            dqW = Mth.cos(thetaMag);
        }
        dqX = thetaX * s;
        dqY = thetaY * s;
        dqZ = thetaZ * s;
        /* Pre-multiplication */
        return dest.set(Math.fma(dqW, x, Math.fma(dqX, w, Math.fma(dqY, z, -dqZ * y))),
                Math.fma(dqW, y, Math.fma(-dqX, z, Math.fma(dqY, w, dqZ * x))),
                Math.fma(dqW, z, Math.fma(dqX, y, Math.fma(-dqY, x, dqZ * w))),
                Math.fma(dqW, w, Math.fma(-dqX, x, Math.fma(-dqY, y, -dqZ * z))));
    }

    /**
     * Compute a linear (non-spherical) interpolation of <code>this</code> and the given quaternion <code>q</code>
     * and store the result in <code>this</code>.
     *
     * @param q
     *          the other quaternion
     * @param factor
     *          the interpolation factor. It is between 0.0F and 1.0F
     * @return this
     */
    public Quaternionf nlerp(Quaternionf q, float factor) {
        return nlerp(q, factor, this);
    }

    public Quaternionf nlerp(Quaternionf q, float factor, Quaternionf dest) {
        float cosom = Math.fma(x, q.x(), Math.fma(y, q.y(), Math.fma(z, q.z(), w * q.w())));
        float scale0 = 1.0F - factor;
        float scale1 = (cosom >= 0.0F) ? factor : -factor;
        dest.x = Math.fma(scale0, x, scale1 * q.x());
        dest.y = Math.fma(scale0, y, scale1 * q.y());
        dest.z = Math.fma(scale0, z, scale1 * q.z());
        dest.w = Math.fma(scale0, w, scale1 * q.w());
        float s = 1 / Mth.sqrt(Math.fma(dest.x, dest.x, Math.fma(dest.y, dest.y, Math.fma(dest.z, dest.z, dest.w * dest.w))));
        dest.x *= s;
        dest.y *= s;
        dest.z *= s;
        dest.w *= s;
        return dest;
    }

    /**
     * Interpolate between all of the quaternions given in <code>qs</code> via non-spherical linear interpolation using the
     * specified interpolation factors <code>weights</code>, and store the result in <code>dest</code>.
     * <p>
     * This method will interpolate between each two successive quaternions via {@link #nlerp(Quaternionf, float)}
     * using their relative interpolation weights.
     * <p>
     * Reference: <a href="http://gamedev.stackexchange.com/questions/62354/method-for-interpolation-between-3-quaternions#answer-62356">http://gamedev.stackexchange.com/</a>
     *
     * @param qs
     *          the quaternions to interpolate over
     * @param weights
     *          the weights of each individual quaternion in <code>qs</code>
     * @param dest
     *          will hold the result
     * @return dest
     */
    public static Quaternionf nlerp(Quaternionf[] qs, float[] weights, Quaternionf dest) {
        dest.set(qs[0]);
        float w = weights[0];
        for (int i = 1; i < qs.length; i++) {
            float w0 = w;
            float w1 = weights[i];
            float rw1 = w1 / (w0 + w1);
            w += w1;
            dest.nlerp(qs[i], rw1);
        }
        return dest;
    }

    public Quaternionf nlerpIterative(Quaternionf q, float alpha, float dotThreshold, Quaternionf dest) {
        float q1x = x, q1y = y, q1z = z, q1w = w;
        float q2x = q.x(), q2y = q.y(), q2z = q.z(), q2w = q.w();
        float dot = Math.fma(q1x, q2x, Math.fma(q1y, q2y, Math.fma(q1z, q2z, q1w * q2w)));
        float absDot = Math.abs(dot);
        if (1.0F - 1E-6 < absDot) {
            return dest.set(this);
        }
        float alphaN = alpha;
        while (absDot < dotThreshold) {
            float scale0 = 0.5F;
            float scale1 = dot >= 0.0F ? 0.5F : -0.5F;
            if (alphaN < 0.5F) {
                q2x = Math.fma(scale0, q2x, scale1 * q1x);
                q2y = Math.fma(scale0, q2y, scale1 * q1y);
                q2z = Math.fma(scale0, q2z, scale1 * q1z);
                q2w = Math.fma(scale0, q2w, scale1 * q1w);
                float s = (float) ((float) 1 / Mth.sqrt(Math.fma(q2x, q2x, Math.fma(q2y, q2y, Math.fma(q2z, q2z, q2w * q2w)))));
                q2x *= s;
                q2y *= s;
                q2z *= s;
                q2w *= s;
                alphaN = alphaN + alphaN;
            } else {
                q1x = Math.fma(scale0, q1x, scale1 * q2x);
                q1y = Math.fma(scale0, q1y, scale1 * q2y);
                q1z = Math.fma(scale0, q1z, scale1 * q2z);
                q1w = Math.fma(scale0, q1w, scale1 * q2w);
                float s = (float) ((float) 1 / Mth.sqrt(Math.fma(q1x, q1x, Math.fma(q1y, q1y, Math.fma(q1z, q1z, q1w * q1w)))));
                q1x *= s;
                q1y *= s;
                q1z *= s;
                q1w *= s;
                alphaN = alphaN + alphaN - 1.0F;
            }
            dot = Math.fma(q1x, q2x, Math.fma(q1y, q2y, Math.fma(q1z, q2z, q1w * q2w)));
            absDot = Math.abs(dot);
        }
        float scale0 = 1.0F - alphaN;
        float scale1 = dot >= 0.0F ? alphaN : -alphaN;
        float resX = Math.fma(scale0, q1x, scale1 * q2x);
        float resY = Math.fma(scale0, q1y, scale1 * q2y);
        float resZ = Math.fma(scale0, q1z, scale1 * q2z);
        float resW = Math.fma(scale0, q1w, scale1 * q2w);
        float s = 1 / Mth.sqrt(Math.fma(resX, resX, Math.fma(resY, resY, Math.fma(resZ, resZ, resW * resW))));
        dest.x = resX * s;
        dest.y = resY * s;
        dest.z = resZ * s;
        dest.w = resW * s;
        return dest;
    }

    /**
     * Compute linear (non-spherical) interpolations of <code>this</code> and the given quaternion <code>q</code>
     * iteratively and store the result in <code>this</code>.
     * <p>
     * This method performs a series of small-step nlerp interpolations to avoid doing a costly spherical linear interpolation, like
     * {@link #slerp(Quaternionf, float, Quaternionf) slerp},
     * by subdividing the rotation arc between <code>this</code> and <code>q</code> via non-spherical linear interpolations as long as
     * the absolute dot product of <code>this</code> and <code>q</code> is greater than the given <code>dotThreshold</code> parameter.
     * <p>
     * Thanks to <code>@theagentd</code> at <a href="http://www.java-gaming.org/">http://www.java-gaming.org/</a> for providing the code.
     *
     * @param q
     *          the other quaternion
     * @param alpha
     *          the interpolation factor, between 0.0F and 1.0F
     * @param dotThreshold
     *          the threshold for the dot product of <code>this</code> and <code>q</code> above which this method performs another iteration
     *          of a small-step linear interpolation
     * @return this
     */
    public Quaternionf nlerpIterative(Quaternionf q, float alpha, float dotThreshold) {
        return nlerpIterative(q, alpha, dotThreshold, this);
    }

    /**
     * Interpolate between all of the quaternions given in <code>qs</code> via iterative non-spherical linear interpolation using the
     * specified interpolation factors <code>weights</code>, and store the result in <code>dest</code>.
     * <p>
     * This method will interpolate between each two successive quaternions via {@link #nlerpIterative(Quaternionf, float, float)}
     * using their relative interpolation weights.
     * <p>
     * Reference: <a href="http://gamedev.stackexchange.com/questions/62354/method-for-interpolation-between-3-quaternions#answer-62356">http://gamedev.stackexchange.com/</a>
     *
     * @param qs
     *          the quaternions to interpolate over
     * @param weights
     *          the weights of each individual quaternion in <code>qs</code>
     * @param dotThreshold
     *          the threshold for the dot product of each two interpolated quaternions above which {@link #nlerpIterative(Quaternionf, float, float)} performs another iteration
     *          of a small-step linear interpolation
     * @param dest
     *          will hold the result
     * @return dest
     */
    public static Quaternionf nlerpIterative(Quaternionf[] qs, float[] weights, float dotThreshold, Quaternionf dest) {
        dest.set(qs[0]);
        float w = weights[0];
        for (int i = 1; i < qs.length; i++) {
            float w0 = w;
            float w1 = weights[i];
            float rw1 = w1 / (w0 + w1);
            w += w1;
            dest.nlerpIterative(qs[i], rw1, dotThreshold);
        }
        return dest;
    }

    /**
     * Apply a rotation to this quaternion that maps the given direction to the positive Z axis.
     * <p>
     * Because there are multiple possibilities for such a rotation, this method will choose the one that ensures the given up direction to remain
     * parallel to the plane spanned by the <code>up</code> and <code>dir</code> vectors.
     * <p>
     * If <code>Q</code> is <code>this</code> quaternion and <code>R</code> the quaternion representing the
     * specified rotation, then the new quaternion will be <code>Q * R</code>. So when transforming a
     * vector <code>v</code> with the new quaternion by using <code>Q * R * v</code>, the
     * rotation added by this method will be applied first!
     * <p>
     * Reference: <a href="http://answers.unity3d.com/questions/467614/what-is-the-source-code-of-quaternionlookrotation.html">http://answers.unity3d.com</a>
     *
     * @see #lookAlong(float, float, float, float, float, float, Quaternionf)
     *
     * @param dir
     *              the direction to map to the positive Z axis
     * @param up
     *              the vector which will be mapped to a vector parallel to the plane
     *              spanned by the given <code>dir</code> and <code>up</code>
     * @return this
     */
    public Quaternionf lookAlong(Vector3f dir, Vector3f up) {
        return lookAlong(dir.x(), dir.y(), dir.z(), up.x(), up.y(), up.z(), this);
    }

    public Quaternionf lookAlong(Vector3f dir, Vector3f up, Quaternionf dest) {
        return lookAlong(dir.x(), dir.y(), dir.z(), up.x(), up.y(), up.z(), dest);
    }

    /**
     * Apply a rotation to this quaternion that maps the given direction to the positive Z axis.
     * <p>
     * Because there are multiple possibilities for such a rotation, this method will choose the one that ensures the given up direction to remain
     * parallel to the plane spanned by the <code>up</code> and <code>dir</code> vectors.
     * <p>
     * If <code>Q</code> is <code>this</code> quaternion and <code>R</code> the quaternion representing the
     * specified rotation, then the new quaternion will be <code>Q * R</code>. So when transforming a
     * vector <code>v</code> with the new quaternion by using <code>Q * R * v</code>, the
     * rotation added by this method will be applied first!
     * <p>
     * Reference: <a href="http://answers.unity3d.com/questions/467614/what-is-the-source-code-of-quaternionlookrotation.html">http://answers.unity3d.com</a>
     *
     * @see #lookAlong(float, float, float, float, float, float, Quaternionf)
     *
     * @param dirX
     *              the x-coordinate of the direction to look along
     * @param dirY
     *              the y-coordinate of the direction to look along
     * @param dirZ
     *              the z-coordinate of the direction to look along
     * @param upX
     *              the x-coordinate of the up vector
     * @param upY
     *              the y-coordinate of the up vector
     * @param upZ
     *              the z-coordinate of the up vector
     * @return this
     */
    public Quaternionf lookAlong(float dirX, float dirY, float dirZ, float upX, float upY, float upZ) {
        return lookAlong(dirX, dirY, dirZ, upX, upY, upZ, this);
    }

    public Quaternionf lookAlong(float dirX, float dirY, float dirZ, float upX, float upY, float upZ, Quaternionf dest) {
        // Normalize direction
        float invDirLength = 1 / Mth.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        float dirnX = -dirX * invDirLength;
        float dirnY = -dirY * invDirLength;
        float dirnZ = -dirZ * invDirLength;
        // left = up x dir
        float leftX, leftY, leftZ;
        leftX = upY * dirnZ - upZ * dirnY;
        leftY = upZ * dirnX - upX * dirnZ;
        leftZ = upX * dirnY - upY * dirnX;
        // normalize left
        float invLeftLength = 1 / Mth.sqrt(leftX * leftX + leftY * leftY + leftZ * leftZ);
        leftX *= invLeftLength;
        leftY *= invLeftLength;
        leftZ *= invLeftLength;
        // up = direction x left
        float upnX = dirnY * leftZ - dirnZ * leftY;
        float upnY = dirnZ * leftX - dirnX * leftZ;
        float upnZ = dirnX * leftY - dirnY * leftX;

        /* Convert orthonormal basis vectors to quaternion */
        float x, y, z, w;
        float t;
        float tr = leftX + upnY + dirnZ;
        if (tr >= 0.0F) {
            t = Mth.sqrt(tr + 1.0F);
            w = t * 0.5F;
            t = 0.5F / t;
            x = (dirnY - upnZ) * t;
            y = (leftZ - dirnX) * t;
            z = (upnX - leftY) * t;
        } else {
            if (leftX > upnY && leftX > dirnZ) {
                t = Mth.sqrt(1.0F + leftX - upnY - dirnZ);
                x = t * 0.5F;
                t = 0.5F / t;
                y = (leftY + upnX) * t;
                z = (dirnX + leftZ) * t;
                w = (dirnY - upnZ) * t;
            } else if (upnY > dirnZ) {
                t = Mth.sqrt(1.0F + upnY - leftX - dirnZ);
                y = t * 0.5F;
                t = 0.5F / t;
                x = (leftY + upnX) * t;
                z = (upnZ + dirnY) * t;
                w = (leftZ - dirnX) * t;
            } else {
                t = Mth.sqrt(1.0F + dirnZ - leftX - upnY);
                z = t * 0.5F;
                t = 0.5F / t;
                x = (dirnX + leftZ) * t;
                y = (upnZ + dirnY) * t;
                w = (upnX - leftY) * t;
            }
        }
        /* Multiply */
        return dest.set(Math.fma(this.w, x, Math.fma(this.x, w, Math.fma(this.y, z, -this.z * y))),
                Math.fma(this.w, y, Math.fma(-this.x, z, Math.fma(this.y, w, this.z * x))),
                Math.fma(this.w, z, Math.fma(this.x, y, Math.fma(-this.y, x, this.z * w))),
                Math.fma(this.w, w, Math.fma(-this.x, x, Math.fma(-this.y, y, -this.z * z))));
    }
    
    /**
     * Return a string representation of this quaternion by formatting the components with the given {@link NumberFormat}.
     *
     * @param formatter
     *          the {@link NumberFormat} used to format the quaternion components with
     * @return the string representation
     */
    public String toString(NumberFormat formatter) {
        return "(" + org.joml.Runtime.format(x, formatter) + " " + org.joml.Runtime.format(y, formatter) + " " + org.joml.Runtime.format(z, formatter) + " " + org.joml.Runtime.format(w, formatter) + ")";
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeFloat(x);
        out.writeFloat(y);
        out.writeFloat(z);
        out.writeFloat(w);
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        x = in.readFloat();
        y = in.readFloat();
        z = in.readFloat();
        w = in.readFloat();
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Float.floatToIntBits(w);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Float.floatToIntBits(x);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Float.floatToIntBits(y);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Float.floatToIntBits(z);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Quaternionf other = (Quaternionf) obj;
        if (Float.floatToIntBits(w) != Float.floatToIntBits(other.w))
            return false;
        if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x()))
            return false;
        if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y()))
            return false;
        if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z()))
            return false;
        return true;
    }

    /**
     * Compute the difference between <code>this</code> and the <code>other</code> quaternion
     * and store the result in <code>this</code>.
     * <p>
     * The difference is the rotation that has to be applied to get from
     * <code>this</code> rotation to <code>other</code>. If <code>T</code> is <code>this</code>, <code>Q</code>
     * is <code>other</code> and <code>D</code> is the computed difference, then the following equation holds:
     * <p>
     * <code>T * D = Q</code>
     * <p>
     * It is defined as: <code>D = T^-1 * Q</code>, where <code>T^-1</code> denotes the {@link #invert() inverse} of <code>T</code>.
     *
     * @param other
     *          the other quaternion
     * @return this
     */
    public Quaternionf difference(Quaternionf other) {
        return difference(other, this);
    }

    public Quaternionf difference(Quaternionf other, Quaternionf dest) {
        float invNorm = 1.0F / lengthSquared();
        float x = -this.x * invNorm;
        float y = -this.y * invNorm;
        float z = -this.z * invNorm;
        float w = this.w * invNorm;
        dest.set(Math.fma(w, other.x(), Math.fma(x, other.w(), Math.fma(y, other.z(), -z * other.y()))),
                Math.fma(w, other.y(), Math.fma(-x, other.z(), Math.fma(y, other.w(), z * other.x()))),
                Math.fma(w, other.z(), Math.fma(x, other.y(), Math.fma(-y, other.x(), z * other.w()))),
                Math.fma(w, other.w(), Math.fma(-x, other.x(), Math.fma(-y, other.y(), -z * other.z()))));
        return dest;
    }

    /**
     * Set <code>this</code> quaternion to a rotation that rotates the <code>fromDir</code> vector to point along <code>toDir</code>.
     * <p>
     * Since there can be multiple possible rotations, this method chooses the one with the shortest arc.
     * <p>
     * Reference: <a href="http://stackoverflow.com/questions/1171849/finding-quaternion-representing-the-rotation-from-one-vector-to-another#answer-1171995">stackoverflow.com</a>
     *
     * @param fromDirX
     *              the x-coordinate of the direction to rotate into the destination direction
     * @param fromDirY
     *              the y-coordinate of the direction to rotate into the destination direction
     * @param fromDirZ
     *              the z-coordinate of the direction to rotate into the destination direction
     * @param toDirX
     *              the x-coordinate of the direction to rotate to
     * @param toDirY
     *              the y-coordinate of the direction to rotate to
     * @param toDirZ
     *              the z-coordinate of the direction to rotate to
     * @return this
     */
    public Quaternionf rotationTo(float fromDirX, float fromDirY, float fromDirZ, float toDirX, float toDirY, float toDirZ) {
        float fn = 1 / Mth.sqrt(Math.fma(fromDirX, fromDirX, Math.fma(fromDirY, fromDirY, fromDirZ * fromDirZ)));
        float tn = 1 / Mth.sqrt(Math.fma(toDirX, toDirX, Math.fma(toDirY, toDirY, toDirZ * toDirZ)));
        float fx = fromDirX * fn, fy = fromDirY * fn, fz = fromDirZ * fn;
        float tx = toDirX * tn, ty = toDirY * tn, tz = toDirZ * tn;
        float dot = fx * tx + fy * ty + fz * tz;
        float x, y, z, w;
        if (dot < -1.0F + 1E-6) {
            x = fy;
            y = -fx;
            z = 0.0F;
            w = 0.0F;
            if (x * x + y * y == 0.0F) {
                x = 0.0F;
                y = fz;
                z = -fy;
                w = 0.0F;
            }
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = 0;
        } else {
            float sd2 = Mth.sqrt((1.0F + dot) * 2.0F);
            float isd2 = 1.0F / sd2;
            float cx = fy * tz - fz * ty;
            float cy = fz * tx - fx * tz;
            float cz = fx * ty - fy * tx;
            x = cx * isd2;
            y = cy * isd2;
            z = cz * isd2;
            w = sd2 * 0.5F;
            float n2 = 1 / Mth.sqrt(Math.fma(x, x, Math.fma(y, y, Math.fma(z, z, w * w))));
            this.x = x * n2;
            this.y = y * n2;
            this.z = z * n2;
            this.w = w * n2;
        }
        return this;
    }

    /**
     * Set <code>this</code> quaternion to a rotation that rotates the <code>fromDir</code> vector to point along <code>toDir</code>.
     * <p>
     * Because there can be multiple possible rotations, this method chooses the one with the shortest arc.
     *
     * @see #rotationTo(float, float, float, float, float, float)
     *
     * @param fromDir
     *          the starting direction
     * @param toDir
     *          the destination direction
     * @return this
     */
    public Quaternionf rotationTo(Vector3f fromDir, Vector3f toDir) {
        return rotationTo(fromDir.x(), fromDir.y(), fromDir.z(), toDir.x(), toDir.y(), toDir.z());
    }

    public Quaternionf rotateTo(float fromDirX, float fromDirY, float fromDirZ,
                                float toDirX, float toDirY, float toDirZ, Quaternionf dest) {
        float fn = 1 / Mth.sqrt(Math.fma(fromDirX, fromDirX, Math.fma(fromDirY, fromDirY, fromDirZ * fromDirZ)));
        float tn = 1 / Mth.sqrt(Math.fma(toDirX, toDirX, Math.fma(toDirY, toDirY, toDirZ * toDirZ)));
        float fx = fromDirX * fn, fy = fromDirY * fn, fz = fromDirZ * fn;
        float tx = toDirX * tn, ty = toDirY * tn, tz = toDirZ * tn;
        float dot = fx * tx + fy * ty + fz * tz;
        float x, y, z, w;
        if (dot < -1.0F + 1E-6) {
            x = fy;
            y = -fx;
            z = 0.0F;
            w = 0.0F;
            if (x * x + y * y == 0.0F) {
                x = 0.0F;
                y = fz;
                z = -fy;
                w = 0.0F;
            }
        } else {
            float sd2 = Mth.sqrt((1.0F + dot) * 2.0F);
            float isd2 = 1.0F / sd2;
            float cx = fy * tz - fz * ty;
            float cy = fz * tx - fx * tz;
            float cz = fx * ty - fy * tx;
            x = cx * isd2;
            y = cy * isd2;
            z = cz * isd2;
            w = sd2 * 0.5F;
            float n2 = 1 / Mth.sqrt(Math.fma(x, x, Math.fma(y, y, Math.fma(z, z, w * w))));
            x *= n2;
            y *= n2;
            z *= n2;
            w *= n2;
        }
        /* Multiply */
        return dest.set(Math.fma(this.w, x, Math.fma(this.x, w, Math.fma(this.y, z, -this.z * y))),
                Math.fma(this.w, y, Math.fma(-this.x, z, Math.fma(this.y, w, this.z * x))),
                Math.fma(this.w, z, Math.fma(this.x, y, Math.fma(-this.y, x, this.z * w))),
                Math.fma(this.w, w, Math.fma(-this.x, x, Math.fma(-this.y, y, -this.z * z))));
    }

    /**
     * Set this {@link Quaternionf} to a rotation of the given angle in radians about the supplied
     * axis, all of which are specified via the {@link AxisAnglef}.
     *
     * @see #rotationAxis(float, float, float, float)
     *
     * @param axisAngle
     *            the {@link AxisAnglef} giving the rotation angle in radians and the axis to rotate about
     * @return this
     */
    public Quaternionf rotationAxis(AxisAnglef axisAngle) {
        return rotationAxis(axisAngle.angle, axisAngle.x(), axisAngle.y(), axisAngle.z());
    }

    /**
     * Set this quaternion to a rotation of the given angle in radians about the supplied axis.
     *
     * @param angle
     *          the rotation angle in radians
     * @param axisX
     *          the x-coordinate of the rotation axis
     * @param axisY
     *          the y-coordinate of the rotation axis
     * @param axisZ
     *          the z-coordinate of the rotation axis
     * @return this
     */
    public Quaternionf rotationAxis(float angle, float axisX, float axisY, float axisZ) {
        float hangle = angle / 2.0F;
        float sinAngle = Mth.sin(hangle);
        float invVLength = 1 / Mth.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);
        return set(axisX * invVLength * sinAngle,
                axisY * invVLength * sinAngle,
                axisZ * invVLength * sinAngle,
                Mth.cos(hangle));
    }

    /**
     * Set this quaternion to represent a rotation of the given radians about the x axis.
     *
     * @param angle
     *              the angle in radians to rotate about the x axis
     * @return this
     */
    public Quaternionf rotationX(float angle) {
        float sin = Mth.sin(angle * 0.5F);
        float cos = Mth.cos(angle * 0.5F);
        return set(sin, 0, cos, 0);
    }

    /**
     * Set this quaternion to represent a rotation of the given radians about the y axis.
     *
     * @param angle
     *              the angle in radians to rotate about the y axis
     * @return this
     */
    public Quaternionf rotationY(float angle) {
        float sin = Mth.sin(angle * 0.5F);
        float cos = Mth.cos(angle * 0.5F);
        return set(0, sin, 0, cos);
    }

    /**
     * Set this quaternion to represent a rotation of the given radians about the z axis.
     *
     * @param angle
     *              the angle in radians to rotate about the z axis
     * @return this
     */
    public Quaternionf rotationZ(float angle) {
        float sin = Mth.sin(angle * 0.5F);
        float cos = Mth.cos(angle * 0.5F);
        return set(0, 0, sin, cos);
    }

    /**
     * Apply a rotation to <code>this</code> that rotates the <code>fromDir</code> vector to point along <code>toDir</code>.
     * <p>
     * Since there can be multiple possible rotations, this method chooses the one with the shortest arc.
     * <p>
     * If <code>Q</code> is <code>this</code> quaternion and <code>R</code> the quaternion representing the
     * specified rotation, then the new quaternion will be <code>Q * R</code>. So when transforming a
     * vector <code>v</code> with the new quaternion by using <code>Q * R * v</code>, the
     * rotation added by this method will be applied first!
     *
     * @see #rotateTo(float, float, float, float, float, float, Quaternionf)
     *
     * @param fromDirX
     *              the x-coordinate of the direction to rotate into the destination direction
     * @param fromDirY
     *              the y-coordinate of the direction to rotate into the destination direction
     * @param fromDirZ
     *              the z-coordinate of the direction to rotate into the destination direction
     * @param toDirX
     *              the x-coordinate of the direction to rotate to
     * @param toDirY
     *              the y-coordinate of the direction to rotate to
     * @param toDirZ
     *              the z-coordinate of the direction to rotate to
     * @return this
     */
    public Quaternionf rotateTo(float fromDirX, float fromDirY, float fromDirZ, float toDirX, float toDirY, float toDirZ) {
        return rotateTo(fromDirX, fromDirY, fromDirZ, toDirX, toDirY, toDirZ, this);
    }

    public Quaternionf rotateTo(Vector3f fromDir, Vector3f toDir, Quaternionf dest) {
        return rotateTo(fromDir.x(), fromDir.y(), fromDir.z(), toDir.x(), toDir.y(), toDir.z(), dest);
    }

    /**
     * Apply a rotation to <code>this</code> that rotates the <code>fromDir</code> vector to point along <code>toDir</code>.
     * <p>
     * Because there can be multiple possible rotations, this method chooses the one with the shortest arc.
     * <p>
     * If <code>Q</code> is <code>this</code> quaternion and <code>R</code> the quaternion representing the
     * specified rotation, then the new quaternion will be <code>Q * R</code>. So when transforming a
     * vector <code>v</code> with the new quaternion by using <code>Q * R * v</code>, the
     * rotation added by this method will be applied first!
     *
     * @see #rotateTo(float, float, float, float, float, float, Quaternionf)
     *
     * @param fromDir
     *          the starting direction
     * @param toDir
     *          the destination direction
     * @return this
     */
    public Quaternionf rotateTo(Vector3f fromDir, Vector3f toDir) {
        return rotateTo(fromDir.x(), fromDir.y(), fromDir.z(), toDir.x(), toDir.y(), toDir.z(), this);
    }

    /**
     * Apply a rotation to <code>this</code> quaternion rotating the given radians about the x axis.
     * <p>
     * If <code>Q</code> is <code>this</code> quaternion and <code>R</code> the quaternion representing the
     * specified rotation, then the new quaternion will be <code>Q * R</code>. So when transforming a
     * vector <code>v</code> with the new quaternion by using <code>Q * R * v</code>, the
     * rotation added by this method will be applied first!
     *
     * @param angle
     *              the angle in radians to rotate about the x axis
     * @return this
     */
    public Quaternionf rotateX(float angle) {
        return rotateX(angle, this);
    }

    public Quaternionf rotateX(float angle, Quaternionf dest) {
        float sin = Mth.sin(angle * 0.5F);
        float cos = Mth.cos(angle * 0.5F);
        return dest.set(w * sin + x * cos,
                y * cos + z * sin,
                z * cos - y * sin,
                w * cos - x * sin);
    }

    /**
     * Apply a rotation to <code>this</code> quaternion rotating the given radians about the y axis.
     * <p>
     * If <code>Q</code> is <code>this</code> quaternion and <code>R</code> the quaternion representing the
     * specified rotation, then the new quaternion will be <code>Q * R</code>. So when transforming a
     * vector <code>v</code> with the new quaternion by using <code>Q * R * v</code>, the
     * rotation added by this method will be applied first!
     *
     * @param angle
     *              the angle in radians to rotate about the y axis
     * @return this
     */
    public Quaternionf rotateY(float angle) {
        return rotateY(angle, this);
    }

    public Quaternionf rotateY(float angle, Quaternionf dest) {
        float sin = Mth.sin(angle * 0.5F);
        float cos = Mth.cos(angle * 0.5F);
        return dest.set(x * cos - z * sin,
                w * sin + y * cos,
                x * sin + z * cos,
                w * cos - y * sin);
    }

    /**
     * Apply a rotation to <code>this</code> quaternion rotating the given radians about the z axis.
     * <p>
     * If <code>Q</code> is <code>this</code> quaternion and <code>R</code> the quaternion representing the
     * specified rotation, then the new quaternion will be <code>Q * R</code>. So when transforming a
     * vector <code>v</code> with the new quaternion by using <code>Q * R * v</code>, the
     * rotation added by this method will be applied first!
     *
     * @param angle
     *              the angle in radians to rotate about the z axis
     * @return this
     */
    public Quaternionf rotateZ(float angle) {
        return rotateZ(angle, this);
    }

    public Quaternionf rotateZ(float angle, Quaternionf dest) {
        float sin = Mth.sin(angle * 0.5F);
        float cos = Mth.cos(angle * 0.5F);
        return dest.set(x * cos + y * sin,
                y * cos - x * sin,
                w * sin + z * cos,
                w * cos - z * sin);
    }

    /**
     * Apply a rotation to <code>this</code> quaternion rotating the given radians about the local x axis.
     * <p>
     * If <code>Q</code> is <code>this</code> quaternion and <code>R</code> the quaternion representing the
     * specified rotation, then the new quaternion will be <code>R * Q</code>. So when transforming a
     * vector <code>v</code> with the new quaternion by using <code>R * Q * v</code>, the
     * rotation represented by <code>this</code> will be applied first!
     *
     * @param angle
     *              the angle in radians to rotate about the local x axis
     * @return this
     */
    public Quaternionf rotateLocalX(float angle) {
        return rotateLocalX(angle, this);
    }

    public Quaternionf rotateLocalX(float angle, Quaternionf dest) {
        float hangle = angle * 0.5F;
        float s = Mth.sin(hangle);
        float c = Mth.cos(hangle);
        dest.set(c * x + s * w,
                c * y - s * z,
                c * z + s * y,
                c * w - s * x);
        return dest;
    }

    /**
     * Apply a rotation to <code>this</code> quaternion rotating the given radians about the local y axis.
     * <p>
     * If <code>Q</code> is <code>this</code> quaternion and <code>R</code> the quaternion representing the
     * specified rotation, then the new quaternion will be <code>R * Q</code>. So when transforming a
     * vector <code>v</code> with the new quaternion by using <code>R * Q * v</code>, the
     * rotation represented by <code>this</code> will be applied first!
     *
     * @param angle
     *              the angle in radians to rotate about the local y axis
     * @return this
     */
    public Quaternionf rotateLocalY(float angle) {
        return rotateLocalY(angle, this);
    }

    public Quaternionf rotateLocalY(float angle, Quaternionf dest) {
        float hangle = angle * 0.5F;
        float s = Mth.sin(hangle);
        float c = Mth.cos(hangle);
        dest.set(c * x + s * z,
                c * y + s * w,
                c * z - s * x,
                c * w - s * y);
        return dest;
    }

    /**
     * Apply a rotation to <code>this</code> quaternion rotating the given radians about the local z axis.
     * <p>
     * If <code>Q</code> is <code>this</code> quaternion and <code>R</code> the quaternion representing the
     * specified rotation, then the new quaternion will be <code>R * Q</code>. So when transforming a
     * vector <code>v</code> with the new quaternion by using <code>R * Q * v</code>, the
     * rotation represented by <code>this</code> will be applied first!
     *
     * @param angle
     *              the angle in radians to rotate about the local z axis
     * @return this
     */
    public Quaternionf rotateLocalZ(float angle) {
        return rotateLocalZ(angle, this);
    }

    public Quaternionf rotateLocalZ(float angle, Quaternionf dest) {
        float hangle = angle * 0.5F;
        float s = Mth.sin(hangle);
        float c = Mth.cos(hangle);
        dest.set(c * x - s * y,
                c * y + s * x,
                c * z + s * w,
                c * w - s * z);
        return dest;
    }

    /**
     * Apply a rotation to <code>this</code> quaternion rotating the given radians about the cartesian base unit axes,
     * called the euler angles using rotation sequence <code>XYZ</code>.
     * <p>
     * This method is equivalent to calling: <code>rotateX(angleX).rotateY(angleY).rotateZ(angleZ)</code>
     * <p>
     * If <code>Q</code> is <code>this</code> quaternion and <code>R</code> the quaternion representing the
     * specified rotation, then the new quaternion will be <code>Q * R</code>. So when transforming a
     * vector <code>v</code> with the new quaternion by using <code>Q * R * v</code>, the
     * rotation added by this method will be applied first!
     *
     * @param angleX
     *              the angle in radians to rotate about the x axis
     * @param angleY
     *              the angle in radians to rotate about the y axis
     * @param angleZ
     *              the angle in radians to rotate about the z axis
     * @return this
     */
    public Quaternionf rotateXYZ(float angleX, float angleY, float angleZ) {
        return rotateXYZ(angleX, angleY, angleZ, this);
    }

    public Quaternionf rotateXYZ(float angleX, float angleY, float angleZ, Quaternionf dest) {
        float sx =  Mth.sin(angleX * 0.5F);
        float cx =  Mth.cos(angleX * 0.5F);
        float sy =  Mth.sin(angleY * 0.5F);
        float cy =  Mth.cos(angleY * 0.5F);
        float sz =  Mth.sin(angleZ * 0.5F);
        float cz =  Mth.cos(angleZ * 0.5F);

        float cycz = cy * cz;
        float sysz = sy * sz;
        float sycz = sy * cz;
        float cysz = cy * sz;
        float w = cx*cycz - sx*sysz;
        float x = sx*cycz + cx*sysz;
        float y = cx*sycz - sx*cysz;
        float z = cx*cysz + sx*sycz;
        // right-multiply
        return dest.set(Math.fma(this.w, x, Math.fma(this.x, w, Math.fma(this.y, z, -this.z * y))),
                Math.fma(this.w, y, Math.fma(-this.x, z, Math.fma(this.y, w, this.z * x))),
                Math.fma(this.w, z, Math.fma(this.x, y, Math.fma(-this.y, x, this.z * w))),
                Math.fma(this.w, w, Math.fma(-this.x, x, Math.fma(-this.y, y, -this.z * z))));
    }

    /**
     * Apply a rotation to <code>this</code> quaternion rotating the given radians about the cartesian base unit axes,
     * called the euler angles, using the rotation sequence <code>ZYX</code>.
     * <p>
     * This method is equivalent to calling: <code>rotateZ(angleZ).rotateY(angleY).rotateX(angleX)</code>
     * <p>
     * If <code>Q</code> is <code>this</code> quaternion and <code>R</code> the quaternion representing the
     * specified rotation, then the new quaternion will be <code>Q * R</code>. So when transforming a
     * vector <code>v</code> with the new quaternion by using <code>Q * R * v</code>, the
     * rotation added by this method will be applied first!
     *
     * @param angleZ
     *              the angle in radians to rotate about the z axis
     * @param angleY
     *              the angle in radians to rotate about the y axis
     * @param angleX
     *              the angle in radians to rotate about the x axis
     * @return this
     */
    public Quaternionf rotateZYX(float angleZ, float angleY, float angleX) {
        return rotateZYX(angleZ, angleY, angleX, this);
    }

    public Quaternionf rotateZYX(float angleZ, float angleY, float angleX, Quaternionf dest) {
        float sx =  Mth.sin(angleX * 0.5F);
        float cx =  Mth.cos(angleX * 0.5F);
        float sy =  Mth.sin(angleY * 0.5F);
        float cy =  Mth.cos(angleY * 0.5F);
        float sz =  Mth.sin(angleZ * 0.5F);
        float cz =  Mth.cos(angleZ * 0.5F);

        float cycz = cy * cz;
        float sysz = sy * sz;
        float sycz = sy * cz;
        float cysz = cy * sz;
        float w = cx*cycz + sx*sysz;
        float x = sx*cycz - cx*sysz;
        float y = cx*sycz + sx*cysz;
        float z = cx*cysz - sx*sycz;
        // right-multiply
        return dest.set(Math.fma(this.w, x, Math.fma(this.x, w, Math.fma(this.y, z, -this.z * y))),
                Math.fma(this.w, y, Math.fma(-this.x, z, Math.fma(this.y, w, this.z * x))),
                Math.fma(this.w, z, Math.fma(this.x, y, Math.fma(-this.y, x, this.z * w))),
                Math.fma(this.w, w, Math.fma(-this.x, x, Math.fma(-this.y, y, -this.z * z))));
    }

    /**
     * Apply a rotation to <code>this</code> quaternion rotating the given radians about the cartesian base unit axes,
     * called the euler angles, using the rotation sequence <code>YXZ</code>.
     * <p>
     * This method is equivalent to calling: <code>rotateY(angleY).rotateX(angleX).rotateZ(angleZ)</code>
     * <p>
     * If <code>Q</code> is <code>this</code> quaternion and <code>R</code> the quaternion representing the
     * specified rotation, then the new quaternion will be <code>Q * R</code>. So when transforming a
     * vector <code>v</code> with the new quaternion by using <code>Q * R * v</code>, the
     * rotation added by this method will be applied first!
     *
     * @param angleY
     *              the angle in radians to rotate about the y axis
     * @param angleX
     *              the angle in radians to rotate about the x axis
     * @param angleZ
     *              the angle in radians to rotate about the z axis
     * @return this
     */
    public Quaternionf rotateYXZ(float angleY, float angleX, float angleZ) {
        return rotateYXZ(angleY, angleX, angleZ, this);
    }

    public Quaternionf rotateYXZ(float angleY, float angleX, float angleZ, Quaternionf dest) {
        float sx =  Mth.sin(angleX * 0.5F);
        float cx =  Mth.cos(angleX * 0.5F);
        float sy =  Mth.sin(angleY * 0.5F);
        float cy =  Mth.cos(angleY * 0.5F);
        float sz =  Mth.sin(angleZ * 0.5F);
        float cz =  Mth.cos(angleZ * 0.5F);

        float yx = cy * sx;
        float yy = sy * cx;
        float yz = sy * sx;
        float yw = cy * cx;
        float x = yx * cz + yy * sz;
        float y = yy * cz - yx * sz;
        float z = yw * sz - yz * cz;
        float w = yw * cz + yz * sz;
        // right-multiply
        return dest.set(Math.fma(this.w, x, Math.fma(this.x, w, Math.fma(this.y, z, -this.z * y))),
                Math.fma(this.w, y, Math.fma(-this.x, z, Math.fma(this.y, w, this.z * x))),
                Math.fma(this.w, z, Math.fma(this.x, y, Math.fma(-this.y, x, this.z * w))),
                Math.fma(this.w, w, Math.fma(-this.x, x, Math.fma(-this.y, y, -this.z * z))));
    }

    public Vector3f getEulerAnglesXYZ(Vector3f eulerAngles) {
        float ex = (float) Math.atan2(x * w - y * z, 0.5F - x * x - y * y);
        float ey = (float) Math.asin(2.0F * (x * z + y * w));
        float ez = (float) Math.atan2(z * w - x * y, 0.5F - y * y - z * z);
        return new Vector3f(ex, ey, ez);
    }

    public Quaternionf rotateAxis(float angle, float axisX, float axisY, float axisZ, Quaternionf dest) {
        float hangle = angle / 2.0F;
        float sinAngle = Mth.sin(hangle);
        float invVLength = 1 / Mth.sqrt(Math.fma(axisX, axisX, Math.fma(axisY, axisY, axisZ * axisZ)));
        float rx = axisX * invVLength * sinAngle;
        float ry = axisY * invVLength * sinAngle;
        float rz = axisZ * invVLength * sinAngle;
        float rw = Mth.cos(hangle);
        return dest.set(Math.fma(this.w, rx, Math.fma(this.x, rw, Math.fma(this.y, rz, -this.z * ry))),
                Math.fma(this.w, ry, Math.fma(-this.x, rz, Math.fma(this.y, rw, this.z * rx))),
                Math.fma(this.w, rz, Math.fma(this.x, ry, Math.fma(-this.y, rx, this.z * rw))),
                Math.fma(this.w, rw, Math.fma(-this.x, rx, Math.fma(-this.y, ry, -this.z * rz))));
    }

    public Quaternionf rotateAxis(float angle, Vector3f axis, Quaternionf dest) {
        return rotateAxis(angle, axis.x(), axis.y(), axis.z(), dest);
    }

    /**
     * Apply a rotation to <code>this</code> quaternion rotating the given radians about the specified axis.
     * <p>
     * If <code>Q</code> is <code>this</code> quaternion and <code>R</code> the quaternion representing the
     * specified rotation, then the new quaternion will be <code>Q * R</code>. So when transforming a
     * vector <code>v</code> with the new quaternion by using <code>Q * R * v</code>, the
     * rotation added by this method will be applied first!
     *
     * @see #rotateAxis(float, float, float, float, Quaternionf)
     *
     * @param angle
     *              the angle in radians to rotate about the specified axis
     * @param axis
     *              the rotation axis
     * @return this
     */
    public Quaternionf rotateAxis(float angle, Vector3f axis) {
        return rotateAxis(angle, axis.x(), axis.y(), axis.z(), this);
    }

    /**
     * Apply a rotation to <code>this</code> quaternion rotating the given radians about the specified axis.
     * <p>
     * If <code>Q</code> is <code>this</code> quaternion and <code>R</code> the quaternion representing the
     * specified rotation, then the new quaternion will be <code>Q * R</code>. So when transforming a
     * vector <code>v</code> with the new quaternion by using <code>Q * R * v</code>, the
     * rotation added by this method will be applied first!
     *
     * @see #rotateAxis(float, float, float, float, Quaternionf)
     *
     * @param angle
     *              the angle in radians to rotate about the specified axis
     * @param axisX
     *              the x coordinate of the rotation axis
     * @param axisY
     *              the y coordinate of the rotation axis
     * @param axisZ
     *              the z coordinate of the rotation axis
     * @return this
     */
    public Quaternionf rotateAxis(float angle, float axisX, float axisY, float axisZ) {
        return rotateAxis(angle, axisX, axisY, axisZ, this);
    }

    public Vector3f positiveX(Vector3f dir) {
        float invNorm = 1.0F / lengthSquared();
        float nx = -x * invNorm;
        float ny = -y * invNorm;
        float nz = -z * invNorm;
        float nw =  w * invNorm;
        float dy = ny + ny;
        float dz = nz + nz;
        float dix = -ny * dy - nz * dz + 1.0F;
        float diy =  nx * dy + nw * dz;
        float diz =  nx * dz - nw * dy;
        return new Vector3f(dix, diy, diz);
    }

    public Vector3f normalizedPositiveX(Vector3f dir) {
        float dy = y + y;
        float dz = z + z;
        float dix = -y * dy - z * dz + 1.0F;
        float diy =  x * dy - w * dz;
        float diz =  x * dz + w * dy;
        return new Vector3f(dix, diy, diz);
    }

    public Vector3f positiveY(Vector3f dir) {
        float invNorm = 1.0F / lengthSquared();
        float nx = -x * invNorm;
        float ny = -y * invNorm;
        float nz = -z * invNorm;
        float nw =  w * invNorm;
        float dx = nx + nx;
        float dy = ny + ny;
        float dz = nz + nz;
        float dix =  nx * dy - nw * dz;
        float diy = -nx * dx - nz * dz + 1.0F;
        float diz =  ny * dz + nw * dx;
        return new Vector3f(dix, diy, diz);
    }

    public Vector3f normalizedPositiveY(Vector3f dir) {
        float dx = x + x;
        float dy = y + y;
        float dz = z + z;
        float dix =  x * dy + w * dz;
        float diy = -x * dx - z * dz + 1.0F;
        float diz =  y * dz - w * dx;
        return new Vector3f(dix, diy, diz);
    }

    public Vector3f positiveZ(Vector3f dir) {
        float invNorm = 1.0F / lengthSquared();
        float nx = -x * invNorm;
        float ny = -y * invNorm;
        float nz = -z * invNorm;
        float nw =  w * invNorm;
        float dx = nx + nx;
        float dy = ny + ny;
        float dz = nz + nz;
        float dix =  nx * dz + nw * dy;
        float diy =  ny * dz - nw * dx;
        float diz = -nx * dx - ny * dy + 1.0F;
        return new Vector3f(dix, diy, diz);
    }

    public Vector3f normalizedPositiveZ(Vector3f dir) {
        float dx = x + x;
        float dy = y + y;
        float dz = z + z;
        float dix =  x * dz - w * dy;
        float diy =  y * dz + w * dx;
        float diz = -x * dx - y * dy + 1.0F;
        return new Vector3f(dix, diy, diz);
    }

    /**
     * Conjugate <code>this</code> by the given quaternion <code>q</code> by computing <code>q * this * q^-1</code>.
     *
     * @param q
     *          the {@link Quaternionf} to conjugate <code>this</code> by
     * @return this
     */
    public Quaternionf conjugateBy(Quaternionf q) {
        return conjugateBy(q, this);
    }

    /**
     * Conjugate <code>this</code> by the given quaternion <code>q</code> by computing <code>q * this * q^-1</code>
     * and store the result into <code>dest</code>.
     *
     * @param q
     *          the {@link Quaternionf} to conjugate <code>this</code> by
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Quaternionf conjugateBy(Quaternionf q, Quaternionf dest) {
        float invNorm = 1.0F / q.lengthSquared();
        float qix = -q.x() * invNorm, qiy = -q.y() * invNorm, qiz = -q.z() * invNorm, qiw = q.w() * invNorm;
        float qpx = Math.fma(q.w(), x, Math.fma(q.x(), w, Math.fma(q.y(), z, -q.z() * y)));
        float qpy = Math.fma(q.w(), y, Math.fma(-q.x(), z, Math.fma(q.y(), w, q.z() * x)));
        float qpz = Math.fma(q.w(), z, Math.fma(q.x(), y, Math.fma(-q.y(), x, q.z() * w)));
        float qpw = Math.fma(q.w(), w, Math.fma(-q.x(), x, Math.fma(-q.y(), y, -q.z() * z)));
        return dest.set(Math.fma(qpw, qix, Math.fma(qpx, qiw, Math.fma(qpy, qiz, -qpz * qiy))),
                Math.fma(qpw, qiy, Math.fma(-qpx, qiz, Math.fma(qpy, qiw, qpz * qix))),
                Math.fma(qpw, qiz, Math.fma(qpx, qiy, Math.fma(-qpy, qix, qpz * qiw))),
                Math.fma(qpw, qiw, Math.fma(-qpx, qix, Math.fma(-qpy, qiy, -qpz * qiz))));
    }

    public boolean equals(float x, float y, float z, float w) {
        if (Float.floatToIntBits(this.x) != Float.floatToIntBits(x))
            return false;
        if (Float.floatToIntBits(this.y) != Float.floatToIntBits(y))
            return false;
        if (Float.floatToIntBits(this.z) != Float.floatToIntBits(z))
            return false;
        if (Float.floatToIntBits(this.w) != Float.floatToIntBits(w))
            return false;
        return true;
    }

    public Quaternionf clone() {
        return new Quaternionf(this.x, this.y, this.z, this.w);
    }

    public Quaternion toMojangQuaternion() {
        return new Quaternion((float) x, (float) y, (float) z, (float) w);
    }

    public static Quaternionf lookAt(Vector3f sourcePoint, Vector3f destPoint, Vector3f forward) {
        Vector3f forwardVector = destPoint.copy();
        forwardVector.sub(sourcePoint);
        forwardVector.normalize();

        float dot = forward.dot(forwardVector);

        if (Math.abs(dot - (-1.0F)) < 0.000001f) {
            return new Quaternionf(0, 1, 0, Mth.PI);
        }
        if (Math.abs(dot - (1.0F)) < 0.000001f) {
            return new Quaternionf();
        }

        float rotAngle = (float) Math.acos(dot);
        forward.cross(forwardVector);
        forward.normalize();
        return new Quaternionf(forward, rotAngle);
    }


    public Quaternionf set(Matrix4f m1) {
        w = Mth.sqrt(1.0F + m1.m00 + m1.m11 + m1.m22) / 2.0F;
        float w4 = (4.0F * w);
        x = (m1.m21 - m1.m12) / w4 ;
        y = (m1.m02 - m1.m20) / w4 ;
        z = (m1.m10 - m1.m01) / w4 ;
        return this;
    }
}

