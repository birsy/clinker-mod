package birsy.clinker.common.alchemy.chemicals;

import birsy.clinker.common.alchemy.anatomy.EntityBody;

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
