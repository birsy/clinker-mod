package birsy.clinker.client.entity.gnomad.basic;

import foundry.veil.api.client.necromancer.render.Skin;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class GnomadSkin {
    protected static final Skin INSTANCE;
    static {
        Skin.Builder builder = Skin.builder(64, 64);
        builder.startBone("face");
        builder.addCube(6F, 2F, 1F, -3F, -5F, -1F, 0F, 0F, 0F, 37F, 18F, false);
        builder.addCube(8F, 6F, 1F, -4F, -3F, -1F, 0F, 0F, 0F, 36F, 12F, false);
        builder.addCube(6F, 1F, 1F, -3F, 3F, -1F, 0F, 0F, 0F, 37F, 11F, false);
        
        builder.startBone("nose");
        builder.setTransform(new Matrix4f()
            .translate(0F, 0F, 0F)
            .rotate(new Quaternionf().rotationZYX(0F, 0F, 0.5235987753F))
            .translate(0F, 0F, 0F)
        );
        builder.addCube(2F, 2F, 2F, -1F, -2F, 0F, 0F, 0F, 0F, 56F, 0F, false);
        builder.setTransform(new Matrix4f());
        
        builder.startBone("head");
        builder.addCube(6F, 6F, 5F, -3F, -1.5F, -5.25F, 0F, 0F, 0F, 36F, 0F, false);
        
        builder.startBone("torso");
        builder.addCube(10F, 14F, 8F, -5F, 0F, -4F, 0.25F, 0.25F, 0.25F, 0F, 22F, false);
        builder.addCube(10F, 14F, 8F, -5F, 0F, -4F, 0F, 0F, 0F, 0F, 0F, false);
        
        builder.startBone("bag");
        builder.setTransform(new Matrix4f()
            .translate(0F, 0F, 0F)
            .rotate(new Quaternionf().rotationZYX(0F, 0F, 0.043633231274999996F))
            .translate(0F, 0F, 0F)
        );
        builder.addCube(9F, 13F, 5F, -4.5F, -13F, 0F, 0F, 0F, 0F, 36F, 43F, false);
        builder.setTransform(new Matrix4f());
        
        builder.startBone("hat");
        builder.addCube(4F, 3F, 4F, -2F, 0F, -2F, 0F, 0F, 0F, 20F, 57F, false);
        
        builder.startBone("neck");
        builder.setTransform(new Matrix4f()
            .translate(0F, 0F, 0F)
            .rotate(new Quaternionf().rotationZYX(0F, 0F, -1.5707963259F))
            .translate(0F, 0F, 0F)
        );
        builder.addCube(3F, 6F, 3F, -1.5F, 0.5F, -1.5F, 0F, 0F, 0F, 36F, 21F, false);
        builder.setTransform(new Matrix4f());
        
        builder.startBone("headJoint");
        
        builder.startBone("rightArm");
        builder.addCube(2F, 12F, 2F, -1F, -10F, -1F, 0F, 0F, 0F, 56F, 25F, false);
        
        builder.startBone("leftArm");
        builder.addCube(2F, 12F, 2F, -1F, -10F, -1F, 0F, 0F, 0F, 56F, 11F, false);
        
        builder.startBone("leftLeg");
        builder.addCube(2F, 11F, 2F, -1F, -9F, -1F, 0F, 0F, 0F, 44F, 30F, false);
        
        builder.startBone("rightLeg");
        builder.addCube(2F, 11F, 2F, -1F, -9F, -1F, 0F, 0F, 0F, 36F, 30F, false);
        
        builder.startBone("skirt");
        builder.addCube(10F, 5F, 8F, -5F, -5.25F, -8.25F, 0.24F, 0.24F, 0.24F, 0F, 44F, false);
        
        builder.startBone("root");
        
        INSTANCE = builder.build();
    }
}