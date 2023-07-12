package birsy.clinker.client.animation;

import birsy.clinker.client.animation.simulation.Ligament;
import birsy.clinker.client.render.Mesh;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;

/**
 * Base used for creating new models
 * Referenced for mesh data for non-dynamic bones.
 */
@OnlyIn(Dist.CLIENT)
public class BaseModel {

    public HashMap<String, Mesh> bones;

    public Vector3f[] getInitialLigamentPositions(String identifier) {
        return new Vector3f[0];
    }

    public Vector3f getInitialLigamentOffset(String identifier) {
        return null;
    }

    public Vector3f getRelativeLigamentPosition(ModelBone bone, Ligament ligament) {
        return null;
    }

    public void resetToInitialTransform(ModelBone bone) {

    }
}
