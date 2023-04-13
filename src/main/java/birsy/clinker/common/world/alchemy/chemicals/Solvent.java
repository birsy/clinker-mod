package birsy.clinker.common.world.alchemy.chemicals;

import birsy.clinker.common.world.alchemy.anatomy.EntityBody;

public class Solvent extends Chemical {
    public final float capacity;
    public Solvent(float capacity, Chemical.Properties properties) {
        super(properties.solvent(true));
        this.capacity = capacity;
    }

    @Override
    public void affectOrgan(EntityBody.EntityOrgan organ, float dillution) {

    }
}
