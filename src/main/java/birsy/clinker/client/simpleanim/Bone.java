package birsy.clinker.client.simpleanim;

import org.joml.Matrix4f;

public class Bone {

    public float x, y, z;
    public float xRot, yRot, zRot;
    public float xScl, yScl, zScl;

    Matrix4f transform(Matrix4f input) {
        input.translate(x, y, z);
        input.rotateYXZ(xRot, yRot, zRot);
        input.scale(xScl, yScl, zScl);
        return input;
    }
}
