package birsy.clinker.client.animation;

import birsy.clinker.client.render.Mesh;

public class DynamicMeshModelBone extends ModelBone {
    public final Mesh mesh;

    public DynamicMeshModelBone(String identifier, ModelSkeleton skeleton, Mesh mesh) {
        super(identifier, skeleton);
        this.mesh = mesh;
    }
}
