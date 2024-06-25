package birsy.clinker.common.world.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.navigation.ExtendedNavigator;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class SmoothFlyingNavigation extends FlyingPathNavigation implements ExtendedNavigator {
    public SmoothFlyingNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    public Mob getMob() {
        return this.mob;
    }

    @Nullable
    @Override
    public Path getPath() {
        return super.getPath();
    }

    /**
     * Patch {@link Path#getEntityPosAtNode} to use a properly rounding check
     */
    @Override
    protected PathFinder createPathFinder(int maxVisitedNodes) {
        this.nodeEvaluator = new WalkNodeEvaluator();
        this.nodeEvaluator.setCanPassDoors(true);

        return new PathFinder(this.nodeEvaluator, maxVisitedNodes) {
            @Nullable
            @Override
            public Path findPath(PathNavigationRegion navigationRegion, Mob mob, Set<BlockPos> targetPositions, float maxRange, int accuracy, float searchDepthMultiplier) {
                final Path path = super.findPath(navigationRegion, mob, targetPositions, maxRange, accuracy, searchDepthMultiplier);

                return path == null ? null : new Path(path.nodes, path.getTarget(), path.canReach()) {
                    @Override
                    public Vec3 getEntityPosAtNode(Entity entity, int nodeIndex) {
                        return SmoothFlyingNavigation.this.getEntityPosAtNode(nodeIndex);
                    }
                };
            }
        };
    }

    @Override
    protected void followThePath() {
        final Vec3 safeSurfacePos = getTempMobPos();
        final int shortcutNode = getClosestVerticalTraversal(Mth.floor(safeSurfacePos.y));
        this.maxDistanceToWaypoint = this.mob.getBbWidth() > 0.75f ? this.mob.getBbWidth() / 2f : 0.75f - this.mob.getBbWidth() / 2f;

        if (!attemptShortcut(shortcutNode, safeSurfacePos)) {
            if (isCloseToNextNode(0.5f) || isAboutToTraverseVertically() && isCloseToNextNode(getMaxDistanceToWaypoint()))
                this.path.advance();
        }

        doStuckDetection(safeSurfacePos);
    }


    protected int getClosestVerticalTraversal(int safeSurfaceHeight) {
        final int nodesLength = this.path.getNodeCount();

        for (int nodeIndex = this.path.getNextNodeIndex(); nodeIndex < nodesLength; nodeIndex++) {
            if (this.path.getNode(nodeIndex).y != safeSurfaceHeight)
                return nodeIndex;
        }

        return nodesLength;
    }
}
