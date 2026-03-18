package birsy.clinker.client.entity.gnomad;

import foundry.veil.api.client.necromancer.Bone;
import foundry.veil.api.client.render.MatrixStack;

public interface SuppliesDelivererSkeleton {
    Bone suppliesParentBone();
    void suppliesOffset(MatrixStack matrixStack);
}
