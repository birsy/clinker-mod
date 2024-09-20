package birsy.clinker.common.world.entity.ai;

import birsy.clinker.core.Clinker;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.navigation.SmoothGroundNavigation;

public class ClinkerSmoothGroundNavigation extends SmoothGroundNavigation {
    protected final float minDistanceToNode;

    public ClinkerSmoothGroundNavigation(Mob mob, Level level, float minDistanceToNode) {
        super(mob, level);
        this.minDistanceToNode = minDistanceToNode;
    }

    @Override
    protected void followThePath() {
        final Vec3 safeSurfacePos = getTempMobPos();
        final int shortcutNode = getClosestVerticalTraversal(Mth.floor(safeSurfacePos.y));
        this.maxDistanceToWaypoint = this.mob.getBbWidth() > 0.75f ? this.mob.getBbWidth() / 2f : 0.75f - this.mob.getBbWidth() / 2f;

        if (!attemptShortcut(shortcutNode, safeSurfacePos)) {
            if (isCloseToNextNode(minDistanceToNode)) {
                this.path.advance();
                Clinker.LOGGER.info("REACHED NODE");
            }
        }

        doStuckDetection(safeSurfacePos);
    }
}
