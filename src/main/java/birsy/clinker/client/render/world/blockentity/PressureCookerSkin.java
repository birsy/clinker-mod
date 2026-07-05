package birsy.clinker.client.render.world.blockentity;

import foundry.veil.api.client.necromancer.render.Skin;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class PressureCookerSkin {
    protected static final Skin INSTANCE;
    static {
        Skin.Builder builder = Skin.builder(64, 32);
        builder.startBone("bone");
        builder.addCube(20F, 13F, 2F, -10F, -5.5F, -8.25F, 0F, 0F, 0F, 0F, 0F, false);
        builder.addCube(20F, 2F, 11F, -10F, 5.5F, -6.25F, 0F, 0F, 0F, 0F, 15F, false);
        
        INSTANCE = builder.build();
    }
}