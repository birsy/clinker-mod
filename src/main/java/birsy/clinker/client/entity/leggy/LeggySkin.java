package birsy.clinker.client.entity.leggy;

import foundry.veil.api.client.necromancer.render.Skin;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class LeggySkin {
    protected static final Skin INSTANCE;
    static {
        Skin.Builder builder = Skin.builder(64, 128);
        builder.startBone("upperLeg2");
        builder.addCube(8F, 24F, 8F, -4F, -24F, -4.5F, 0F, 0F, 0F, 0F, 42F, false);
        builder.addCube(8F, 5F, 4F, -4F, -28F, -5.5F, 0F, 0F, 0F, 32F, 42F, false);
        
        builder.startBone("lowerLeg2");
        builder.addCube(6F, 26F, 6F, -3F, -24F, -3F, 0F, 0F, 0F, 0F, 74F, false);
        
        builder.startBone("upperLeg1");
        builder.addCube(8F, 24F, 8F, -4F, -24F, -4.5F, 0F, 0F, 0F, 0F, 42F, false);
        builder.addCube(8F, 5F, 4F, -4F, -28F, -5.5F, 0F, 0F, 0F, 32F, 42F, false);
        
        builder.startBone("lowerLeg1");
        builder.addCube(6F, 26F, 6F, -3F, -24F, -3F, 0F, 0F, 0F, 0F, 74F, false);
        
        builder.startBone("upperLeg0");
        builder.addCube(8F, 24F, 8F, -4F, -24F, -4.5F, 0F, 0F, 0F, 0F, 42F, false);
        builder.addCube(8F, 5F, 4F, -4F, -28F, -5.5F, 0F, 0F, 0F, 32F, 42F, false);
        
        builder.startBone("lowerLeg0");
        builder.addCube(6F, 26F, 6F, -3F, -24F, -3F, 0F, 0F, 0F, 0F, 74F, false);
        
        builder.startBone("upperLeg4");
        builder.addCube(8F, 24F, 8F, -4F, -24F, -4.5F, 0F, 0F, 0F, 0F, 42F, false);
        builder.addCube(8F, 5F, 4F, -4F, -28F, -5.5F, 0F, 0F, 0F, 32F, 42F, false);
        
        builder.startBone("lowerLeg4");
        builder.addCube(6F, 26F, 6F, -3F, -24F, -3F, 0F, 0F, 0F, 0F, 74F, false);
        
        builder.startBone("upperLeg3");
        builder.addCube(8F, 24F, 8F, -4F, -24F, -4.5F, 0F, 0F, 0F, 0F, 42F, false);
        builder.addCube(8F, 5F, 4F, -4F, -28F, -5.5F, 0F, 0F, 0F, 32F, 42F, false);
        
        builder.startBone("lowerLeg3");
        builder.addCube(6F, 26F, 6F, -3F, -24F, -3F, 0F, 0F, 0F, 0F, 74F, false);
        
        builder.startBone("head");
        builder.addCube(12F, 2F, 12F, -6F, -2F, -6F, 0F, 0F, 0F, 0F, 28F, false);
        builder.addCube(16F, 12F, 16F, -8F, 0F, -8F, 0F, 0F, 0F, 0F, 0F, false);
        
        builder.startBone("root");
        
        INSTANCE = builder.build();
    }
}