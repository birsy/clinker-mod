package birsy.clinker.common.world.entity.gnomad;

import birsy.clinker.client.model.base.InterpolatedSkeletonParent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class GnomadMogulEntity extends GnomadEntity implements InterpolatedSkeletonParent {
    public GnomadMogulEntity(EntityType<? extends GnomadEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


}
