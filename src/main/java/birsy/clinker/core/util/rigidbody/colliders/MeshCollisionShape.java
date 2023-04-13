package birsy.clinker.core.util.rigidbody.colliders;

import birsy.clinker.core.util.rigidbody.Transform;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MeshCollisionShape implements ICollisionShape {
    public Vec3[] vertices;
    public Transform transform;
    public double radius;

    public MeshCollisionShape(Vec3... vertices) {
        this.vertices = vertices;
        this.transform = new Transform();
        double radius = 0;
        for (Vec3 vertex : vertices) {
            radius = Math.max(vertex.length(), radius);
        }
        this.radius = radius;
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
        for(Vec3 raw : vertices){
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
        // average all vertex positions
        Vec3 sum = new Vec3(0, 0, 0);

        for(Vec3 v : vertices){
            sum = sum.add(applyTransform(v));
        }

        return sum.scale(1.0 / vertices.length);
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
        return radius;
    }

    @Override
    public CompoundTag serialize(CompoundTag tag) {
        CompoundTag transformTag = new CompoundTag();
        this.transform.serialize(transformTag);
        tag.put("transform", transformTag);

        int vertexId = 0;
        CompoundTag verticesTag = new CompoundTag();

        for (Vec3 vertex : this.vertices) {
            vertexId++;
            CompoundTag vertexTag = new CompoundTag();
            vertexTag.putDouble("x", vertex.x());
            vertexTag.putDouble("y", vertex.y());
            vertexTag.putDouble("z", vertex.z());

            tag.put("vertex" + vertexId, verticesTag);
        }
        tag.put("vertices", verticesTag);

        return tag;
    }

    @Override
    public ICollisionShape deserialize(CompoundTag tag) {
        CompoundTag transformTag = tag.getCompound("transform");
        this.setTransform(Transform.fromNBT(transformTag));

        CompoundTag verticesTag = tag.getCompound("vertices");
        this.vertices = new Vec3[verticesTag.size()];
        for (int vID = 0; vID < verticesTag.size(); vID++) {
            CompoundTag vertexTag = tag.getCompound("vertex" + vID);
            this.vertices[vID] = new Vec3(vertexTag.getDouble("x"), vertexTag.getDouble("y"), vertexTag.getDouble("z"));
        }
        return this;
    }

    public static MeshCollisionShape fromAABB(AABB bb) {
        return new MeshCollisionShape(new Vec3(bb.minX, bb.minY, bb.minZ),
                new Vec3(bb.minX, bb.minY, bb.maxZ),
                new Vec3(bb.minX, bb.maxY, bb.minZ),
                new Vec3(bb.minX, bb.maxY, bb.maxZ),
                new Vec3(bb.maxX, bb.minY, bb.minZ),
                new Vec3(bb.maxX, bb.minY, bb.maxZ),
                new Vec3(bb.maxX, bb.maxY, bb.minZ),
                new Vec3(bb.maxX, bb.maxY, bb.maxZ));
    }
    
    public static MeshCollisionShape Box(Vec3 start, Vec3 end) {
        return new MeshCollisionShape(new Vec3(start.x, start.y, start.z),
                                      new Vec3(start.x, start.y, end.z),
                                      new Vec3(start.x, end.y, start.z),
                                      new Vec3(start.x, end.y, end.z),
                                      new Vec3(end.x, start.y, start.z),
                                      new Vec3(end.x, start.y, end.z),
                                      new Vec3(end.x, end.y, start.z),
                                      new Vec3(end.x, end.y, end.z));
    }
}
