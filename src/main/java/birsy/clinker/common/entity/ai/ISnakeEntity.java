package birsy.clinker.common.entity.ai;

import net.minecraft.world.phys.Vec3;

public interface ISnakeEntity {
    Vec3 getFacingDirection(float partialTick);
    void setFacingDirection(Vec3 direction);
}
