package birsy.clinker.client.necromancer.animate;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Matrix4x3f;
import org.joml.Matrix4x3fc;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class Bone {
    Bone parent;
    Collection<Bone> children;
    Matrix4x3f transformMatrix;
    Matrix4x3f poseSpaceMatrix;

    public Bone(float x, float y, float z) {
        this.children = new ArrayList<>();
        this.transformMatrix = new Matrix4x3f().translate(x, y, z);
        this.poseSpaceMatrix = new Matrix4x3f();
    }

    public Bone addChild(Bone child) {
        this.children.add(child);
        child.parent = this;

        return this;
    }

    public void precalculatePoseSpaceMatricies() {
        if (this.parent == null) {
            this.poseSpaceMatrix.set(this.transformMatrix);
        } else {
            this.transformMatrix.mul(this.parent.poseSpaceMatrix, this.poseSpaceMatrix);
        }

        for (Bone child : this.children) {
            child.precalculatePoseSpaceMatricies();
        }
    }

    public Matrix4x3fc poseSpaceMatrix() {
        return this.poseSpaceMatrix;
    }

    public void visit(Consumer<Bone> consumer) {
        consumer.accept(this);
        for (Bone child : this.children) {
            child.visit(consumer);
        }
    }
}
