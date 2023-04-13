package birsy.clinker.core.util.rigidbody.colliders;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.rigidbody.Transform;
import birsy.clinker.core.util.rigidbody.colliders.ICollisionShape;
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

        calculateVertices();
    }

    private void calculateVertices() {
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
        Vec3 fromT = from;
        Vec3 toT = to;
        AABB aabb = new AABB(this.size.scale(-1), this.size);
        if (aabb.contains(fromT)) return Optional.of(fromT);
        Optional<Vec3> position = aabb.clip(fromT, toT);
        position.ifPresent((vec3 -> vec3 = this.transform.toWorldSpace(vec3)));
        return position;
    }

    public Optional<Vec3> raycast(Vec3 from, Vec3 to, double inflate) {
        Vec3 fromT = from;
        Vec3 toT = to;
        AABB aabb = new AABB(this.size.scale(-1), this.size).inflate(inflate);
        if (aabb.contains(fromT)) return Optional.of(fromT);
        Optional<Vec3> position = aabb.clip(fromT, toT);
        position.ifPresent((vec3 -> vec3 = this.transform.toWorldSpace(vec3)));
        return position;
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
        this.calculateVertices();
        return this;
    }
}
