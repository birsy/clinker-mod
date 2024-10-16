package birsy.clinker.client.necromancer.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface Skin {
    void draw(PoseStack poseStack);
}
