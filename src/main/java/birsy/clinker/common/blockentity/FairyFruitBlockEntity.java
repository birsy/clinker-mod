package birsy.clinker.common.blockentity;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlockEntities;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class FairyFruitBlockEntity extends BlockEntity {
    public int ticksExisted;
    public int ropeHeight;
    public List<Vec3> locations;
    public List<Vec3> pLocations;
    private boolean small;

    public FairyFruitBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ClinkerBlockEntities.FAIRY_FRUIT.get(), pPos, pBlockState);
        this.ropeHeight = Integer.MIN_VALUE;
        this.locations = new ArrayList<>();
        this.pLocations = new ArrayList<>();
        this.small = false;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FairyFruitBlockEntity entity) {
        entity.ticksExisted++;
        entity.calculateRopeHeight(level);
        entity.updatePhysics();
    }

    public void updatePhysics() {
        List<Vec3> tempLocations = new ArrayList<>();
        // updates the positions of all the points
        for (int i = 0; i < locations.size(); i++) {
            Vec3 loc = this.locations.get(i);
            Vec3 pLoc = this.pLocations.get(i);

            // duplicate variable...
            Vec3 finalLocation = new Vec3(loc.x(), loc.y(), loc.z());

            // newton's first law
            Vec3 diff = loc.subtract(pLoc);
            finalLocation = finalLocation.add(diff);

            // if it's the attachment point, don't apply any forces to it.
            if (i != 0) {
                // gravity
                finalLocation = finalLocation.add(0, -0.1, 0);
                // "wind"
                float factor = (float)i / (float)(locations.size() - 1);
                float windSpeed = 0.1F;
                float windStrength = 0.002F * factor * factor * factor * factor;
                float unique = Mth.frac((float)this.getBlockPos().hashCode() * Mth.PI) * 256;
                finalLocation = finalLocation.add(Mth.sin((this.ticksExisted + unique) * windSpeed) * windStrength, 0, Mth.cos((this.ticksExisted + unique) * windSpeed * 1.05F) * windStrength);
            }

            tempLocations.add(i, finalLocation);
        }

        if (this.hasLevel()) this.updateCollisions(this.getLevel(), tempLocations);

        // iteration to correct rope lengths
        for (int iterations = 0; iterations < 256; iterations++) {
            for (int i = 0; i < locations.size() - 1; i++) {
                Vec3 segmentA = tempLocations.get(i);
                Vec3 segmentB = tempLocations.get(i + 1);

                float segmentLength = small ? 0.5F : 1.0F;
                Vec3 center = segmentA.add(segmentB).scale(0.5);
                Vec3 direction = segmentA.subtract(segmentB).normalize();

                if (i != 0) segmentA = center.add(direction.scale(segmentLength * 0.5));
                segmentB = center.subtract(direction.scale(segmentLength * 0.5));

                // constrain locations, so they don't venture too far out from block center...
                float clamping = 1.0F;
                segmentA = new Vec3(Mth.clamp(segmentA.x(), -clamping, clamping), segmentA.y(), Mth.clamp(segmentA.z(), -clamping, clamping));
                segmentB = new Vec3(Mth.clamp(segmentB.x(), -clamping, clamping), segmentB.y(), Mth.clamp(segmentB.z(), -clamping, clamping));

                tempLocations.set(i, segmentA);
                tempLocations.set(i + 1, segmentB);
            }
        }

        // set up variables for next time...
        for (int i = 0; i < locations.size(); i++) {
            Vec3 loc = this.locations.get(i);
            this.pLocations.set(i, new Vec3(loc.x(), loc.y(), loc.z()));
            this.locations.set(i, tempLocations.get(i));
        }
    }

    private void updateCollisions(Level level, List<Vec3> points) {
        BlockPos origin = this.getBlockPos();
        AABB boundingBox = new AABB(origin, origin.offset(1, this.ropeHeight, 1));
        List<Entity> entities = level.getEntities(null, boundingBox);

        for (int i = 0; i < points.size(); i++) {
            Vec3 loc = points.get(i);
            Vec3 worldSpaceLoc = this.toWorldSpace(loc);
            for (Entity entity : entities) {
               // if (!entity.canBeCollidedWith()) break;

                AABB bb = entity.getBoundingBox();
                if (bb.contains(worldSpaceLoc)) {
                    Vec3 correctedLoc = closestPointOnSurface(bb, worldSpaceLoc);
                    // reorient it back into local space
                    Vec3 dif = correctedLoc.subtract(worldSpaceLoc);
                    // multiplying by a factor smears out the collision a bit, so it's less jittery
                    points.set(i, loc.add(dif.scale(1.0)));
                }
            }
        }
    }

    // unbelievably scuffed
    private Vec3 closestPointOnSurface(AABB box, Vec3 point) {
        Vec3 center = box.getCenter();
        Vec3 newPos = point.subtract(center);

        Vec2 horizontal = new Vec2((float) newPos.x(), (float) newPos.z());
        float radius = (float) (box.getZsize() * 0.5);
        if (horizontal.length() < radius) {
            Vec2 hCorrected = horizontal.normalized().scale(radius);
            return new Vec3(hCorrected.x, newPos.y(), hCorrected.y).add(center);
        }

        return point;
    }

    public Vec3 toWorldSpace(Vec3 point) {
        return point.add(0.5, 0.5, 0.5).add(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ());
    }

    private void calculateRopeHeight(Level level) {
        if (this.hasLevel()) {
            BlockPos.MutableBlockPos mutable = this.getBlockPos().mutable();
            for (int length = 0; length < level.getMaxBuildHeight() - mutable.getY(); length++) {
                mutable.move(Direction.UP);
                if (!isFairyFruitBlock(level.getBlockState(mutable))) {
                    updateRopeHeight(length);
                    return;
                }
            }
            updateRopeHeight(level.getMaxBuildHeight() - mutable.getY());
        } else {
            updateRopeHeight(2);
            return;
        }
    }

    private void updateRopeHeight(int value) {
        if (value != this.ropeHeight) {
            this.ropeHeight = value;
            generateRopePoints();
        }
    }

    private void generateRopePoints() {
        this.locations = new ArrayList<>();
        this.pLocations = new ArrayList<>();

        for (float i = this.ropeHeight + 0.5F; i > 0; i--) {
            this.addPoint(new Vec3(0, i, 0));
        }

        if (this.locations.size() <= 1) {
            this.locations = new ArrayList<>();
            this.pLocations = new ArrayList<>();

            this.addPoint(new Vec3(0, 0.5, 0));
            this.addPoint(new Vec3(0, 0, 0));

            this.small = true;
        }
    }

    private void addPoint(Vec3 vec) {
        this.locations.add(vec);
        this.pLocations.add(vec);
    }

    private static boolean isFairyFruitBlock(BlockState state) {
        return state.is(ClinkerBlocks.FAIRY_FRUIT_BLOCK.get()) || state.is(ClinkerBlocks.FAIRY_FRUIT_VINE.get());
    }
}
