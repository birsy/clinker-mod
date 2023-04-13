package birsy.clinker.client.render.entity.model.gnomad.armor;

import birsy.clinker.client.render.entity.model.base.DynamicModelPart;
import net.minecraft.resources.ResourceLocation;

public class GnomadHelmetSoldierVisorModel extends GnomadHelmetSoldierModel {
    public DynamicModelPart gnomadVisor;

    public GnomadHelmetSoldierVisorModel(ResourceLocation location) {
        super(location);
        this.gnomadVisor = new DynamicModelPart(this.skeleton, 0, 19);
        this.gnomadVisor.setInitialPosition(0.0F, -1.0F, -4.0F);
        this.gnomadVisor.addCube(-4.5F, -1.5F, -3.5F, 9.0F, 5.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.gnomadVisor.setInitialRotation( 0.40000000466912183F, 0.0F, 0.0F);

        this.gnomadHelmet.addChild(this.gnomadVisor);
    }
}
