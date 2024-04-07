package birsy.clinker.common.world.entity.ordnance.modifer;

import birsy.clinker.common.world.entity.ordnance.OrdnanceEntity;

public class FulminateModifier extends OrdnanceModifier {

    public FulminateModifier(OrdnanceEntity entity) {
        super(entity);
    }


    @Override
    public boolean createParticles() {
        return true;
    }
}
