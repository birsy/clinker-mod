package birsy.clinker.common.world.entity.gnomad;

import net.minecraft.world.phys.Vec3;

public interface GnomadSupplyDepot {
    boolean canSupply();
    Vec3 getDepotLocation();
}
