package birsy.clinker.common.world.entity.system.lightningspawner;

import birsy.clinker.core.registry.entity.ClinkerAttributes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;

import java.util.Collection;

public class LightningSpawner {
    final ServerLevel level;
    double x, z;
    double dX, dZ;

    LightningSpawner(ServerLevel level, double startX, double startZ) {
        this.level = level;
        this.x = startX; this.z = startZ;
    }

    void tick() {
        x += dX; z += dZ;
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) x, (int) z);

        Collection<LivingEntity> potentialAttractors = EntityRetrievalUtil.getEntities(
                this.level, new Vec3(x, y, z),
                10, 24, 10,
                LivingEntity.class, entity -> level.canSeeSky(entity.blockPosition())
        );
        double nDx = 0, nDz = 0;
        for (LivingEntity potentialAttractor : potentialAttractors) {

        }
    }
}
