package birsy.clinker.common.world.physics.rigidbody.colliders;

import birsy.clinker.core.util.MathUtils;
import birsy.clinker.common.world.physics.rigidbody.Transform;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class OBBCollisionShape implements ICollisionShape {
    public Transform transform;
    //measured from the center out
    public Vec3 size;
    private Vec3[] vertices;
    
    public OBBCollisionShape(double sizeX, double sizeY, double sizeZ) {
        this.size = new Vec3(sizeX, sizeY, sizeZ);
        this.transform = new Transform();

        recalculateVertices();
    }

    public void recalculateVertices() {
        this.vertices = new Vec3[8];
        this.vertices[0] = new Vec3(-size.x, -size.y, -size.z);
        this.vertices[1] = new Vec3( size.x, -size.y, -size.z);
        this.vertices[2] = new Vec3(-size.x,  size.y, -size.z);
        this.vertices[3] = new Vec3( size.x,  size.y, -size.z);
        this.vertices[4] = new Vec3(-size.x, -size.y,  size.z);
        this.vertices[5] = new Vec3( size.x, -size.y,  size.z);
        this.vertices[6] = new Vec3(-size.x,  size.y,  size.z);
        this.vertices[7] = new Vec3( size.x,  size.y,  size.z);
    }

    @Override
    public AABB getBounds() {
        // Find the min and max values for each axis
        double minX = Float.MAX_VALUE;
        double minY = Float.MAX_VALUE;
        double minZ = Float.MAX_VALUE;
        double maxX = -Float.MAX_VALUE;
        double maxY = -Float.MAX_VALUE;
        double maxZ = -Float.MAX_VALUE;

        for(Vec3 unProcessed : vertices){
            Vec3 v = applyTransform(unProcessed);
            if(v.x < minX) minX = v.x;
            if(v.y < minY) minY = v.y;
            if(v.z < minZ) minZ = v.z;
            if(v.x > maxX) maxX = v.x;
            if(v.y > maxY) maxY = v.y;
            if(v.z > maxZ) maxZ = v.z;
        }

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public Vec3 support(Vec3 direction) {
        // find the vertex with the largest dot product with the direction
        double maxDot = -Float.MAX_VALUE;

        Vec3 maxVertex = null;
        for(Vec3 raw : this.vertices){
            Vec3 v = applyTransform(raw);
            double dot = v.subtract(getCenter()).dot(direction);
            if(dot > maxDot) {
                maxDot = dot;
                maxVertex =  v;
            }
        }

        return maxVertex;
    }

    @Override
    public Vec3 getCenter() {
        return this.transform.position;
    }

    @Override
    public void setTransform(Transform transform) {
        this.transform = transform.copy();
    }

    @Override
    public Transform getTransform() {
        return this.transform;
    }

    public Vec3 applyTransform(Vec3 vec) {
        return this.transform.toWorldSpace(vec);
    }

    @Override
    public double getRadius() {
        return Math.max(size.x(), Math.max(size.y(), size.z()));
    }

    public Optional<Vec3> raycast(Vec3 from, Vec3 to) {
        Vec3 fromT = this.transform.toLocalSpace(from);
        Vec3 toT = this.transform.toLocalSpace(to);

        double length = toT.subtract(fromT).length();
        Vec3 direction = toT.subtract(fromT).normalize();
        AABB aabb = new AABB(this.size.scale(-1), this.size);
        Optional<Vec3> vec = aabb.clip(fromT, toT);
        if (vec.isPresent()) return Optional.of(this.transform.toWorldSpace(vec.get()));
        return Optional.empty();
        /*// god knows why i have to scale it.
        double distance = intersectBox(this.size.scale(-1), this.size, from, direction);

        if (Double.isInfinite(distance)) return Optional.empty();
        if (distance > length) return Optional.empty();
        Vec3 position = direction.scale(distance).add(fromT);
        position = this.transform.toWorldSpace(position);

        return Optional.of(position);*/
    }

    public Optional<Vec3> raycast(Vec3 from, Vec3 to, double inflate) {
        Vec3 fromT = this.transform.toLocalSpace(from);
        Vec3 toT = this.transform.toLocalSpace(to);

        double length = toT.subtract(fromT).length();
        Vec3 direction = toT.subtract(fromT).normalize();
        AABB aabb = new AABB(this.size.scale(-1), this.size).inflate(inflate);
        Optional<Vec3> vec = aabb.clip(fromT, toT);
        if (vec.isPresent()) return Optional.of(this.transform.toWorldSpace(vec.get()));
        return Optional.empty();

        /*if (Double.isInfinite(distance)) return Optional.empty();
        if (distance > length) return Optional.empty();
        Vec3 position = direction.scale(distance).add(fromT);
        position = this.transform.toWorldSpace(position);

        return Optional.of(position);*/
    }


    //returns infinity if no intersection is possible.
    private static double intersectBox(Vec3 boundsMin, Vec3 boundsMax, Vec3 rayOrigin, Vec3 rayDirection) {
        Vec3 t0 = boundsMin.subtract(rayOrigin).multiply(1.0 / rayDirection.x(), 1.0 / rayDirection.y(), 1.0 / rayDirection.z());
        Vec3 t1 = boundsMax.subtract(rayOrigin).multiply(1.0 / rayDirection.x(), 1.0 / rayDirection.y(), 1.0 / rayDirection.z());
        Vec3 tMin = MathUtils.min(t0, t1);
        Vec3 tMax = MathUtils.max(t0, t1);

        double distA = Math.max(Math.max(tMin.x, tMin.y), tMin.z);
        double distB = Math.min(tMax.x, Math.min(tMax.y, tMax.z));

        double distanceToBox = Math.max(0, distA);
        double distanceInsideBox = Math.max(0, distB - distanceToBox);
        boolean missesBox = distanceInsideBox == 0;
        boolean insideBox = distA < 0 && 0 < distB;
        return missesBox ? Double.POSITIVE_INFINITY : insideBox ? 0 : distanceToBox;
    }

    @Override
    public CompoundTag serialize(CompoundTag tag) {
        CompoundTag transformTag = new CompoundTag();
        this.transform.serialize(transformTag);
        tag.put("transform", transformTag);

        CompoundTag sizeTag = new CompoundTag();
        sizeTag.putDouble("x", this.size.x());
        sizeTag.putDouble("y", this.size.y());
        sizeTag.putDouble("z", this.size.z());
        tag.put("size", sizeTag);

        return tag;
    }

    @Override
    public ICollisionShape deserialize(CompoundTag tag) {
        CompoundTag transformTag = tag.getCompound("transform");
        this.setTransform(Transform.fromNBT(transformTag));

        CompoundTag sizeTag = tag.getCompound("size");
        this.size = new Vec3(sizeTag.getDouble("x"), sizeTag.getDouble("y"), sizeTag.getDouble("z"));
        this.recalculateVertices();
        return this;
    }
}
