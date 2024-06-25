package birsy.clinker.common.world.entity.homunculoids;

import birsy.clinker.common.world.entity.ai.SmoothFlyingNavigation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.level.Level;

public abstract class FlyingHomunculoidEntity extends HomunculoidEntity {
    protected FlyingHomunculoidEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        SmoothFlyingNavigation flyingpathnavigation = new SmoothFlyingNavigation(this, pLevel);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }
}
