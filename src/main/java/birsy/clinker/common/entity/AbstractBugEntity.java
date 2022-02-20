package birsy.clinker.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public abstract class AbstractBugEntity extends Monster {
    public AbstractBugEntity(EntityType<? extends AbstractBugEntity> entity, Level level) {
        super(entity, level);
    }

    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }
}
