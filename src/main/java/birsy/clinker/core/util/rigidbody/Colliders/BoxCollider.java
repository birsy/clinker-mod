package birsy.clinker.core.util.rigidbody.Colliders;

import birsy.clinker.core.util.MathUtils;
import com.mojang.math.Quaternion;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class BoxCollider extends Collider {
    public final Vec3 dimensions;
    private List<Vec3> nonTransformedVertices;
    private List<Vec3> nonTransformedNormals;

    public BoxCollider(Vec3 position, Vec3 dimensions) {
        this(position, dimensions, Quaternion.ONE);
    }
    public BoxCollider(Vec3 position, Vec3 dimensions, Quaternion orientation) {
        super(position, orientation);
        this.dimensions = dimensions;
        this.nonTransformedVertices = List.of(new Vec3(-1, -1, -1),
                new Vec3(-1, -1, 1),
                new Vec3(-1, 1, -1),
                new Vec3(-1, 1, 1),
                new Vec3(1, -1, -1),
                new Vec3(1, -1, 1),
                new Vec3(1, 1, -1),
                new Vec3(1, 1, 1));
        for (int i = 0; i < this.nonTransformedVertices.size(); i++) {
            this.nonTransformedVertices.set(i, this.nonTransformedVertices.get(i).multiply(this.dimensions.scale(0.5)));
        }
        this.nonTransformedNormals = List.of(new Vec3( 1, 0, 0),
                new Vec3(-1, 0, 0),
                new Vec3(0,  1, 0),
                new Vec3(0, -1, 0),
                new Vec3(0, 0,  1),
                new Vec3(0, 0, -1));
    }

    public List<Vec3> getVertices() {
        ArrayList<Vec3> list = new ArrayList<>();
        for (int i = 0; i < this.nonTransformedVertices.size(); i++) {
            list.add(i, MathUtils.rotate(this.orientation, this.nonTransformedVertices.get(i).add(this.position)));
        }
        return list;
    }

    public AABB getAABB() {
        List<Vec3> verticies = this.getVertices();
        Vec3 min = Vec3.ZERO;
        Vec3 max = Vec3.ZERO;
        for (Vec3 vertex : verticies) {
            vertex = vertex.subtract(this.position);
            if (vertex.x() < min.x()) {min = new Vec3(vertex.x(), min.y(), min.z()); }
            if (vertex.y() < min.y()) {min = new Vec3(min.x(), vertex.y(), min.z()); }
            if (vertex.z() < min.z()) {min = new Vec3(min.x(), min.y(), vertex.z()); }

            if (vertex.x() > max.x()) {max = new Vec3(vertex.x(), max.y(), max.z()); }
            if (vertex.y() > max.y()) {max = new Vec3(max.x(), vertex.y(), max.z()); }
            if (vertex.z() > max.z()) {max = new Vec3(max.x(), max.y(), vertex.z()); }
        }

        return new AABB(min.add(this.position), max.add(this.position));
    }

    public List<Vec3> getNormals() {
        ArrayList<Vec3> list = new ArrayList<>();
        for (int i = 0; i < this.nonTransformedNormals.size(); i++) {
            list.set(i, MathUtils.rotate(this.orientation, this.nonTransformedNormals.get(i)));
        }
        return list;
    }

    public boolean intersects(BoxCollider collider) {
        return Collisions.BoxBoxIntersection(this, collider);
    }

    public boolean intersects(SphereCollider collider) {
        return Collisions.BoxSphereIntersection(this, collider);
    }
}
