package birsy.clinker.client.entity.gnomad.runt;

import foundry.veil.api.client.necromancer.render.Skin;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class GnomadRuntSkin {
    protected static final Skin INSTANCE;
    static {
        Skin.Builder builder = Skin.builder(64, 32);
        builder.startBone("rightArm");
        builder.addCube(1F, 6F, 1F, 0F, -5.5F, -0.5F, 0F, 0F, 0F, 0F, 21F, false);
        
        builder.startBone("leftLeg");
        builder.addCube(1F, 7F, 1F, -1F, -6.25F, -0.5F, 0F, 0F, 0F, 12F, 21F, false);
        
        builder.startBone("rightLeg");
        builder.addCube(1F, 7F, 1F, 0F, -6.25F, -0.5F, 0F, 0F, 0F, 8F, 21F, false);
        
        builder.startBone("leftArm");
        builder.addCube(1F, 6F, 1F, -1F, -5.5F, -0.5F, 0F, 0F, 0F, 4F, 21F, false);
        
        builder.startBone("torso");
        builder.addCube(7F, 8F, 5F, -3.5F, 0F, -2.5F, 0F, 0F, 0F, 0F, 8F, false);
        
        builder.startBone("head");
        builder.addCube(6F, 6F, 5F, -3F, 0F, -3F, 0F, 0F, 0F, 19F, 0F, false);
        
        builder.startBone("face");
        builder.addCube(6F, 7F, 1F, -3F, -4F, -1F, 0F, 0F, 0F, 0F, 0F, false);
        builder.setTransform(new Matrix4f()
            .translate(0F, 2F, -1F)
            .rotate(new Quaternionf().rotationZYX(0F, 0F, 0.5235987753F))
            .translate(0F, -2F, 1F)
        );
        builder.addCube(1F, 2F, 2F, -0.5F, 0F, -1F, 0F, 0F, 0F, 14F, 0F, false);
        builder.setTransform(new Matrix4f());
        
        builder.startBone("hat");
        builder.addCube(3F, 2F, 3F, -1.5F, 0F, -1.5F, 0F, 0F, 0F, 24F, 11F, false);
        
        builder.startBone("root");

        builder.startBone("deliveryGrasp");

        INSTANCE = builder.build();
    }
}