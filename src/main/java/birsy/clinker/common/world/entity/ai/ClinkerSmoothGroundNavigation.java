package birsy.clinker.common.world.entity.ai;

import birsy.clinker.core.Clinker;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.navigation.SmoothGroundNavigation;

public class ClinkerSmoothGroundNavigation extends SmoothGroundNavigation {
    protected final float minDistanceToNode;

    public ClinkerSmoothGroundNavigation(Mob mob, Level level, float minDistanceToNode) {
        super(mob, level);
        this.minDistanceToNode = minDistanceToNode;
        this.maxDistanceToWaypoint = 0.5F;
    }

    public float getNodeRadius() {
        return 0.5F;
    }
    
    @Override
    protected void followThePath() {
        final Vec3 safeSurfacePos = getTempMobPos();
        final int shortcutNode = getClosestVerticalTraversal(Mth.floor(safeSurfacePos.y));
        this.maxDistanceToWaypoint = this.mob.getBbWidth() * 0.5F;
        if (!attemptShortcut(shortcutNode, safeSurfacePos)) {
            if (isCloseToNextNode(0.5f) || isCloseToNextNode(getMaxDistanceToWaypoint())) {
                this.path.advance();
                Clinker.LOGGER.info("AAAA{}", this.mob.getRandom().nextFloat());
            }

        }

        doStuckDetection(safeSurfacePos);
    }
    
    @Override
    public boolean isAboutToTraverseVertically() {
        final Path path = getPath();
        final int fromNode = path.getNextNodeIndex();
        final int fromNodeHeight = path.getNode(fromNode).y;
        final int toNode = Math.min(path.getNodeCount(), fromNode + Mth.ceil(this.getNodeRadius() * 0.5d) + 1);

        for (int i = fromNode + 1; i < toNode; i++) {
            if (path.getNode(i).y != fromNodeHeight)
                return true;
        }

        return false;
    }
    
    @Override
    public boolean attemptShortcut(int targetNode, Vec3 safeSurfacePos) {
        final Mob mob = getMob();
        final Path path = getPath();
        final Vec3 position = mob.position();
        final Vec3 minBounds = safeSurfacePos.add(-this.getNodeRadius() * 0.5d, 0, -this.getNodeRadius() * 0.5d);
        final Vec3 maxBounds = minBounds.add(this.getNodeRadius(), mob.getBbHeight(), this.getNodeRadius());

        for (int nodeIndex = targetNode - 1; nodeIndex > path.getNextNodeIndex(); nodeIndex--) {
            final Vec3 nodeDelta = getEntityPosAtNode(nodeIndex).subtract(position);

            if (isCollisionFreeTraversal(nodeDelta, minBounds, maxBounds)) {
                path.setNextNodeIndex(nodeIndex);

                return true;
            }
        }

        return false;
    }
    
    @Override
    public Vec3 getEntityPosAtNode(int nodeIndex) {
        final Mob mob = getMob();
        final Path path = getPath();
        final double lateralOffset = Mth.floor(this.getNodeRadius() + 1d) / 2d;

        return Vec3.atLowerCornerOf(path.getNodePos(nodeIndex)).add(lateralOffset, 0, lateralOffset);
    }

    @Override
    public boolean isCloseToNextNode(float distance) {
        final Mob mob = getMob();
        final Path path = getPath();
        final Vec3 nextNodePos = getEntityPosAtNode(path.getNextNodeIndex());

        return Math.abs(mob.getX() - nextNodePos.x) < distance &&
               Math.abs(mob.getZ() - nextNodePos.z) < distance &&
               Math.abs(mob.getY() - nextNodePos.y) < distance + mob.maxUpStep();
    }
}
