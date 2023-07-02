package birsy.clinker.client.animation;

import birsy.clinker.client.render.Mesh;

public class DynamicallyMeshedAnimationBone extends AnimationBone {
    public final Mesh mesh;

    public DynamicallyMeshedAnimationBone(String identifier, AnimationSkeleton skeleton, Mesh mesh) {
        super(identifier, skeleton);
        this.mesh = mesh;
    }
}
