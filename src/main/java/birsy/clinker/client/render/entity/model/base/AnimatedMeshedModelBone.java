package birsy.clinker.client.render.entity.model.base;

import org.jetbrains.annotations.Nullable;

public class AnimatedMeshedModelBone extends AnimatedModelBone {
    public final Mesh mesh;

    public AnimatedMeshedModelBone(String identifier, Mesh mesh) {
        super(identifier);
        this.mesh = mesh;
    }
}
