package birsy.clinker.common.world.entity;

import birsy.clinker.core.util.MathUtils;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LegManager {
    final Entity entity;
    final float stepSpeed;
    final List<Leg> legs = new ArrayList<>();

    int currentStepGroup = 0;
    int totalStepGroups = 1;
    int stepGroupTimer = 0;

    static final int STEP_GROUP_TIMEOUT = 20;

    public LegManager(Entity owner, float stepSpeed) {
        this.entity = owner;
        this.stepSpeed = stepSpeed;
    }

    public Leg addLeg(int stepGroup,
                      double socketX, double socketY, double socketZ,
                      double idealX,  double idealY,  double idealZ,
                      double length) {
        Leg leg = new Leg(this, stepGroup,
                socketX, socketY, socketZ,
                idealX,  idealY,  idealZ,
                length);
        legs.add(leg);
        totalStepGroups = Math.max(totalStepGroups, stepGroup + 1);
        return leg;
    }

    public int legCount() {
        return this.legs.size();
    }

    public Leg getLeg(int index) {
        return this.legs.get(index);
    }

    public void tick() {
        for (Leg leg : legs) leg.tick();
        tickStepGroup();
    }

    void tickStepGroup() {
        stepGroupTimer++;
        boolean hasGroupFinished = stepGroupTimer >= STEP_GROUP_TIMEOUT;
        if (!hasGroupFinished) {
            hasGroupFinished = true;
            for (Leg leg : legs) {
                if (leg.stepGroup == currentStepGroup && leg.state == Leg.State.STEPPING) {
                    hasGroupFinished = false; break;
                }
            }
        }
        if (hasGroupFinished) {
            currentStepGroup = (currentStepGroup + 1) % totalStepGroups;
            stepGroupTimer = 0;
            for (Leg leg : legs) if (leg.stepGroup == currentStepGroup) leg.groupAllowedToStep = true;
        }
    }

    boolean resting() {
        return Mth.lengthSquared(entity.getX() - entity.xo, entity.getZ() - entity.zo) < 0.01F;
    }

    public int ticksUntilNextTurn(int stepGroup) {
        int groupsAway = Math.floorMod(stepGroup - currentStepGroup, totalStepGroups);
        if (groupsAway == 0) groupsAway = totalStepGroups;
        return Math.round(groupsAway / stepSpeed) - stepGroupTimer;
    }

    public double getParentRotation() {
        if (entity instanceof GroundLocomotionEntity gle)
            return -gle.getSyncedBodyRotation() * Mth.DEG_TO_RAD;
        return entity.getYRot() * Mth.DEG_TO_RAD;
    }

    public Vector3d fromParentSpace(Vector3dc relative, Vector3d result) {
        result.set(relative);
        result.rotateY(getParentRotation());
        result.add(entity.getX(), entity.getY(), entity.getZ());
        return result;
    }
    public Vector3d toParentSpace(Vector3dc world, Vector3d result) {
        result.set(world);
        result.sub(entity.getX(), entity.getY(), entity.getZ());
        result.rotateY(-getParentRotation());
        return result;
    }

    public Vector3d fromParentSpacePredicted(Vector3dc relative, int ticks, Vector3d result) {
        // super basic dead reckoning
        double dX = (entity.getX() - entity.xo) * ticks,
               dY = (entity.getY() - entity.yo) * ticks,
               dZ = (entity.getZ() - entity.zo) * ticks;
        double length = Mth.length(dX, dY, dZ);
        double maxLength = legs.getFirst().length * 0.9;
        if (length > maxLength) {
            double scale = maxLength / length;
            dX *= scale; dY *= scale; dZ *= scale;
        }
        return fromParentSpace(relative, result)
                .add(dX, dY, dZ);
    }

    public static class Leg {
        public final LegManager parent;

        public final int stepGroup; // see https://factorio.com/blog/post/fff-425
        public final Vector3dc relativeSocketPos; // relative to parent
        public final Vector3dc relativeIdealFootPos; // relative to parent
        public final double length;

        final FootPlacementSearcher searcher = new FootPlacementSearcher(this);

        final Vector3d relativeFootPos = new Vector3d(); // relative to parent
        final Vector3d targetFootPos = new Vector3d(), // both in world space
                       previousTargetFootPos = new Vector3d();

        float stepProgress = 0;
        boolean groupAllowedToStep = true;

        enum State { FLOATING, STEPPING, ATTACHED }
        State state = State.FLOATING, lastState = State.FLOATING;

        static final double STEP_THRESHOLD = 0.6, RESTING_STEP_THRESHOLD = 0.1;
        static final double INVALID_THRESHOLD = 1.0;

        // scratch vectors
        final Vector3d s0 = new Vector3d(), s1 = new Vector3d(), s2 = new Vector3d();

        public Leg(LegManager parent, int stepGroup,
                   double socketX, double socketY, double socketZ,
                   double idealX, double idealY, double idealZ,
                   double length) {
            this.parent = parent;
            this.stepGroup = stepGroup;
            this.relativeSocketPos = new Vector3d(socketX, socketY, socketZ);
            this.relativeIdealFootPos = new Vector3d(idealX,  idealY,  idealZ);
            this.length = length;

            this.relativeFootPos.set(this.relativeIdealFootPos);
        }

        void tick() {
            searcher.tick();
            switch (state) {
                case STEPPING -> tickStepping();
                case FLOATING -> tickFloating();
                case ATTACHED -> tickAttached();
            }
        }

        void tickFloating() {
            relativeFootPos.lerp(relativeIdealFootPos, 0.1F);
            Vector3dc nextTargetPos = getNextTargetPosition();
            // dont check for step groups or whatever here.
            // if we're floating then always find purchase as soon as possible
            if (nextTargetPos != null) beginStep(nextTargetPos);
        }

        void tickStepping() {
            stepProgress = Mth.approach(stepProgress, 1.0F, parent.stepSpeed);
            float smoothedStepProgress = MathUtils.ease(stepProgress, MathUtils.EasingType.easeInOutQuad);
            Vector3d footPos = previousTargetFootPos.lerp(targetFootPos, smoothedStepProgress, s0);
            if (lastState != State.FLOATING) {
                double stepHeight = Math.sin(stepProgress * Math.PI);
                double stepDistance = previousTargetFootPos.distance(targetFootPos);
                stepHeight *= Mth.clampedMap(stepDistance, length * RESTING_STEP_THRESHOLD, length * STEP_THRESHOLD * 0.5, 0, length * 0.1);
                footPos.add(0, stepHeight, 0);
            }
            parent.toParentSpace(footPos, relativeFootPos);

            if (stepProgress >= 0.99) finishStep();
        }

        void tickAttached() {
            parent.toParentSpace(targetFootPos, relativeFootPos);
            boolean mustStep = stepPosInvalid();
            if (shouldTryStepping() || mustStep) {
                Vector3dc nextTargetPos = getNextTargetPosition();
                if (nextTargetPos != null) beginStep(nextTargetPos);
                else if (mustStep) detach();
            }
        }

        boolean shouldTryStepping() {
            if (parent.currentStepGroup != this.stepGroup) return false;
            if (!groupAllowedToStep) return false; // each group should only step Once during its time.

            // make sure we're not too far from our ideal leg location
            int lookaheadTicks = parent.ticksUntilNextTurn(stepGroup);
            Vector3d predictedIdeal = parent.fromParentSpacePredicted(relativeIdealFootPos, lookaheadTicks, s0);
            double threshold = length * (parent.resting() ? RESTING_STEP_THRESHOLD : STEP_THRESHOLD * 0.5);
            if (predictedIdeal.distanceSquared(targetFootPos) > threshold * threshold)
                return true;

            // make sure we're not too close to our body
            double minDistanceFromSocket = length * 0.3;
            Vector3d predictedSocket = parent.fromParentSpacePredicted(relativeSocketPos, lookaheadTicks, s1);
            if (predictedSocket.distanceSquared(targetFootPos) < minDistanceFromSocket * minDistanceFromSocket)
                return true;

            return false;
        }

        // returns true if we MUST step this tick, or else start floating
        boolean stepPosInvalid() {
            // make sure its still in reach of the socket
            parent.fromParentSpace(relativeSocketPos, s1);
            double reachLimit = length * INVALID_THRESHOLD;
            if (s1.distanceSquared(targetFootPos) > reachLimit * reachLimit) return true;

            // make sure it touches some geometry
            boolean touchesGeometry = false;
            double footRadius = 0.25;
            AABB footAABB = new AABB(
                    targetFootPos.x() - footRadius, targetFootPos.y() - footRadius, targetFootPos.z() - footRadius,
                    targetFootPos.x() + footRadius, targetFootPos.y() + footRadius, targetFootPos.z() + footRadius
            );
            Iterable<VoxelShape> intersectingShapes = this.parent.entity.level().getBlockCollisions(this.parent.entity, footAABB);
            for (VoxelShape intersectingShape : intersectingShapes) {
                for (AABB aabb : intersectingShape.toAabbs())
                    if (footAABB.intersects(aabb)) { touchesGeometry = true; break; }
            }
            if (!touchesGeometry) return true;

            return false;
        }

        void beginStep(Vector3dc targetPos) {
            if (targetPos.distanceSquared(targetFootPos) < 0.1 * 0.1) return;

            changeState(State.STEPPING);
            previousTargetFootPos.set(targetFootPos);
            targetFootPos.set(targetPos);
            stepProgress = 0;
            groupAllowedToStep = false;
            searcher.reset();
        }

        void finishStep() {
            changeState(State.ATTACHED);
            parent.toParentSpace(targetFootPos, relativeFootPos);
            this.parent.entity.playSound(SoundEvents.COW_STEP, 0.1F, 1.0F);
        }

        void detach() {
            changeState(State.FLOATING);
        }

        public @Nullable Vector3dc getNextTargetPosition() {
            if (true) return searcher.getBestCandidate();

            int lookaheadTicks = parent.ticksUntilNextTurn(stepGroup);

            Vector3d socketPos = parent.fromParentSpace(relativeSocketPos, s1);
            Vector3d predictedIdeal = parent.fromParentSpacePredicted(relativeIdealFootPos, lookaheadTicks, s2);

            Vec3 rayFrom = new Vec3(socketPos.x(), socketPos.y(), socketPos.z());
            Vec3 rayTo = new Vec3(predictedIdeal.x(), predictedIdeal.y(), predictedIdeal.z());
            BlockHitResult hit = parent.entity.level().clip(new ClipContext(
                    rayFrom, rayTo,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    parent.entity
            ));

            if (hit.getType() != HitResult.Type.BLOCK) return null;

            Vec3 loc = hit.getLocation();
            return s0.set(loc.x(), loc.y(), loc.z());
        }

        void changeState(State nextState) {
            lastState = state;
            state = nextState;
        }

        public Vector3dc getRelativeFootPos() {
            return this.relativeFootPos;
        }
    }

    static class FootPlacementSearcher {
        final Leg leg;
        final LongSet searchedBlockPositions = new LongOpenHashSet();
        final LongArrayFIFOQueue frontier = new LongArrayFIFOQueue();
        final Vector3d predictedIdealFootPos = new Vector3d(), predictedSocketPos = new Vector3d();
        final Vector3d bestCandidate = new Vector3d();
        double bestCandidateScore = Double.NEGATIVE_INFINITY;

        static final int BLOCKS_PER_TICK = 6;
        static final double SEARCH_RADIUS_FACTOR = 1.5;

        final Vector3d s0 = new Vector3d();

        FootPlacementSearcher(Leg leg) {
            this.leg = leg;
        }

        void tick() {
            int ticksUntilNextTurn = leg.parent.ticksUntilNextTurn(leg.stepGroup);
            Vector3d newPredicted = leg.parent.fromParentSpacePredicted(leg.relativeIdealFootPos, ticksUntilNextTurn, s0);
            double resetThreshold = leg.length * 0.3;
            if (predictedIdealFootPos.distanceSquared(newPredicted) > resetThreshold * resetThreshold)
                reset();

            Level level = leg.parent.entity.level();
            int processed = 0;
            while (!frontier.isEmpty() && processed < BLOCKS_PER_TICK) {
                evaluateAndExpand(BlockPos.of(frontier.dequeueLong()), level);
                processed++;
            }
        }

        void evaluateAndExpand(BlockPos pos, Level level) {
            BlockState state = level.getBlockState(pos);

            VoxelShape collider = state.getCollisionShape(level, pos);
            if (!collider.isEmpty()) {
                Vec3 localQuery = new Vec3(
                        predictedIdealFootPos.x - pos.getX(),
                        predictedIdealFootPos.y - pos.getY(),
                        predictedIdealFootPos.z - pos.getZ()
                );
                Optional<Vec3> nearestPoint = collider.closestPointTo(localQuery);
                if (nearestPoint.isPresent()) {
                    Vec3 localPoint = nearestPoint.get();
                    s0.set(pos.getX() + localPoint.x, pos.getY() + localPoint.y, pos.getZ() + localPoint.z);

                    // terrible normal estimate
                    Direction normal = Direction.getNearest(localPoint);

                    // space on the foot side must be passable, and it must be close enough
                    if (!level.getBlockState(pos.relative(normal)).isSolid() &&
                        s0.distanceSquared(predictedSocketPos) < leg.length * leg.length) {
                        double score = score(s0, normal, level);
                        if (score > bestCandidateScore) {
                            bestCandidateScore = score;
                            bestCandidate.set(s0);
                        }
                    }
                }
            }

            double searchRadius = leg.length * SEARCH_RADIUS_FACTOR;
            double searchRadiusSq = searchRadius * searchRadius;
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = pos.relative(dir);
                long packed = neighbor.asLong();
                if (searchedBlockPositions.contains(packed)) continue;

                double dx = neighbor.getX() + 0.5 - predictedIdealFootPos.x();
                double dy = neighbor.getY() + 0.5 - predictedIdealFootPos.y();
                double dz = neighbor.getZ() + 0.5 - predictedIdealFootPos.z();
                if (dx*dx + dy*dy + dz*dz > searchRadiusSq) continue;

                searchedBlockPositions.add(packed);
                frontier.enqueue(packed);
            }
        }

        void reset() {
            bestCandidateScore = Double.NEGATIVE_INFINITY;
            searchedBlockPositions.clear();
            frontier.clear();

            int ticksUntilNextTurn = leg.parent.ticksUntilNextTurn(leg.stepGroup);
            leg.parent.fromParentSpacePredicted(leg.relativeIdealFootPos, ticksUntilNextTurn, predictedIdealFootPos);
            leg.parent.fromParentSpacePredicted(leg.relativeSocketPos,    ticksUntilNextTurn, predictedSocketPos);

            BlockPos seed = BlockPos.containing(predictedIdealFootPos.x, predictedIdealFootPos.y, predictedIdealFootPos.z);
            long packed = seed.asLong();
            searchedBlockPositions.add(packed);
            frontier.enqueue(packed);
        }

        @Nullable Vector3dc getBestCandidate() {
            if (bestCandidateScore <= Double.NEGATIVE_INFINITY) return null;
            return bestCandidate;
        }

        double score(Vector3d position, Direction footNormal, Level level) {
            double score = 0;

            double maxDist = leg.length * SEARCH_RADIUS_FACTOR;
            double dist = position.distance(predictedIdealFootPos);
            score += (1.0 - Math.min(dist / maxDist, 1.0)) * 10.0;

            score += footNormal.getStepY() * 2.0;

            BlockPos footBlock = BlockPos.containing(
                    position.x + footNormal.getStepX() * 0.1,
                    position.y + footNormal.getStepY() * 0.1,
                    position.z + footNormal.getStepZ() * 0.1
            );
            if (!level.getFluidState(footBlock).isEmpty()) score -= 5.0;

            return score;
        }
    }
}
