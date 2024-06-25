package birsy.clinker.common.world.entity.homunculoids;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;

import java.util.List;

public class MotherHomunculoid extends HomunculoidEntity implements SmartBrainOwner<MotherHomunculoid> {
    public MotherHomunculoid(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public List<? extends ExtendedSensor<? extends MotherHomunculoid>> getSensors() {
        return null;
    }
}
