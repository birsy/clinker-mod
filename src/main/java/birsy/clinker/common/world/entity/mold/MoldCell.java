package birsy.clinker.common.world.entity.mold;

import birsy.clinker.common.networking.packet.ClientboundMoldGrowthPacket;
import birsy.clinker.core.util.MathUtil;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class MoldCell {

    private static final int MAX_CHILDREN = 4;

    public final MoldEntity entity;
    public final Optional<MoldCell> parent;
    public final Direction parentDirection;
    public final List<MoldCell> children;

    public final BlockPos pos;
    public final AttachmentPoint attachmentPoint;

    DamageSource latestDamageSource;
    float energy;
    float damage = 0;
    int timeUntilNextGrowth = 60;
    boolean canGrowAgain = true;

    public int depth;

    public float previousGrowthAmount = 0.0F;
    public float growthAmount = 0.0F;

    public float previousRadius = 0.0F;
    public float radius = 0.0F;

    public int id;

    public int maxBranchDepth = 0;

    public MoldCell(MoldEntity entity, AttachmentPoint attachmentPoint, int initialEnergy) {
        this.entity = entity;
        this.entity.root = this;
        this.id = this.entity.currentId.getAndIncrement();

        this.depth = 0;

        this.attachmentPoint = attachmentPoint;

        this.pos = entity.blockPosition();
        this.entity.addMoldCell(this);
        this.entity.occupiedSpaces.add(this.pos);

        this.energy = initialEnergy;
        this.parent = Optional.empty();
        this.children = new ArrayList<>();

        this.parentDirection = attachmentPoint.directions[0];
        this.setMaxBranchDepth(this.depth);
    }

    public MoldCell(MoldCell parent, AttachmentPoint attachmentPoint, BlockPos position, Direction parentDirection) {
        this.entity = parent.entity;
        this.id = this.entity.currentId.getAndIncrement();

        this.attachmentPoint = attachmentPoint;

        this.pos = position;
        this.entity.addMoldCell(this);
        this.entity.occupiedSpaces.add(this.pos);

        this.parent = Optional.of(parent);
        parent.children.add(this);
        this.depth = parent.depth + 1;
        this.entity.maxDepth = Math.max(this.entity.maxDepth, this.depth);
        this.children = new ArrayList<>();

        this.energy = parent.getPassedAlongEnergy();

        this.parentDirection = parentDirection;
        this.setMaxBranchDepth(this.depth);
    }

    public MoldCell(MoldCell parent, AttachmentPoint attachmentPoint, BlockPos position, Direction parentDirection, int id) {
        this.entity = parent.entity;
        this.id = id;

        this.attachmentPoint = attachmentPoint;

        this.pos = position;
        this.entity.addMoldCell(this);
        this.entity.occupiedSpaces.add(this.pos);

        this.parent = Optional.of(parent);
        parent.children.add(this);
        this.depth = parent.depth + 1;
        this.entity.maxDepth = Math.max(this.entity.maxDepth, this.depth);
        this.children = new ArrayList<>();

        this.energy = parent.energy - 1;

        this.parentDirection = parentDirection;
        this.setMaxBranchDepth(this.depth);
    }

    public MoldCell(MoldEntity entity, CompoundTag tag) {
        this.entity = entity;
        this.id = tag.getInt("ID");
        if (tag.contains("ParentID")) {
            this.parent = Optional.ofNullable(this.entity.parts.get(tag.getInt("ParentID")));
        } else {
            this.parent = Optional.empty();
        }
        this.parentDirection = Direction.from3DDataValue(tag.getInt("Direction"));
        this.pos = new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"));
        this.attachmentPoint = AttachmentPoint.values()[tag.getInt("Attach")];
        this.energy = tag.getFloat("Energy");
        this.damage = tag.getFloat("Damage");
        this.timeUntilNextGrowth = tag.getInt("TimeUntilNextGrowth");
        this.canGrowAgain = tag.getBoolean("CanGrowAgain");
        this.depth = tag.getInt("Depth");
        this.growthAmount = tag.getFloat("GrowthAmount");
        this.previousGrowthAmount = this.growthAmount;
        this.children = new ArrayList<>();
        this.entity.addMoldCell(this);

        this.setMaxBranchDepth(this.depth);
    }

    public void serialize(CompoundTag tag, int depth) {
        CompoundTag cellTag = new CompoundTag();

        cellTag.putInt("ID", this.id);
        if (this.parent.isPresent()) cellTag.putInt("ParentID", this.parent.get().id);
        cellTag.putInt("Direction", this.parentDirection.get3DDataValue());
        cellTag.putInt("X", this.pos.getX());
        cellTag.putInt("Y", this.pos.getY());
        cellTag.putInt("Z", this.pos.getZ());
        cellTag.putInt("Attach", this.attachmentPoint.ordinal());
        cellTag.putFloat("Energy", this.energy);
        cellTag.putFloat("Damage", this.damage);
        cellTag.putInt("TimeUntilNextGrowth", this.timeUntilNextGrowth);
        cellTag.putBoolean("CanGrowAgain", this.canGrowAgain);
        cellTag.putInt("Depth", this.depth);
        cellTag.putFloat("GrowthAmount", this.growthAmount);
        tag.put("Cell" + depth, cellTag);

        for (MoldCell child : this.children) {
            child.serialize(tag, depth++);
        }
    }

    public void setMaxBranchDepth(int depth) {
        this.maxBranchDepth = Math.max(this.maxBranchDepth, depth);
        if (this.parent.isPresent()) this.parent.get().setMaxBranchDepth(this.maxBranchDepth);
    }

    // accumulate damages across branches of the fungus.
    public float getCurrentEnergy() {
        if (this.parent.isPresent()) {
            return parent.get().getPassedAlongEnergy() - damage;
        }
        return energy - damage;
    }

    private float getPassedAlongEnergy() {
        return this.getCurrentEnergy() - 1;
    }

    public Vec3 getOffsetPosition(float partialTicks) {
        float maxOffset = 0.5F;
        Vec3 attachmentPoint = new Vec3(Mth.clamp(this.attachmentPoint.vector.x() * 30, -maxOffset, maxOffset),
                Mth.clamp(this.attachmentPoint.vector.y() * 30, -maxOffset, maxOffset),
                Mth.clamp(this.attachmentPoint.vector.z() * 30, -maxOffset, maxOffset));
        float radius = this.getRadius(partialTicks) * 0.5F;
        Vec3 radiusOffset = new Vec3(Mth.clamp(this.attachmentPoint.vector.x() * 30, -radius, radius),
                Mth.clamp(this.attachmentPoint.vector.y() * 30, -radius, radius),
                Mth.clamp(this.attachmentPoint.vector.z() * 30, -radius, radius));
        return this.pos.getCenter().add(attachmentPoint).subtract(radiusOffset);
    }

    public Vec3 position(float partialTicks) {
        if (this.parent.isEmpty()) return this.getOffsetPosition(partialTicks);
        Vec3 parentPos = this.parent.get().getOffsetPosition(partialTicks);
        Vec3 thisPos = this.getOffsetPosition(partialTicks);
        return parentPos.lerp(thisPos, this.getGrowthAmount(partialTicks));
    }

    public float getRadius(float partialTicks) {
//        if (this.getCurrentEnergy() == this.entity.startingEnergy) return 1.0F;
//        float t = this.getCurrentEnergy() / (this.entity.startingEnergy + 10.0F);
//        return t * this.getGrowthAmount(partialTicks);//(t * t * (3.0F - 2.0F * t)) * 0.666F * this.getGrowthAmount(partialTicks);
        return Mth.lerp(partialTicks, this.previousRadius, this.radius);
    }

    public float getGrowthAmount(float partialTicks) {
        return MathUtil.ease(Mth.clamp(Mth.lerp(partialTicks, this.previousGrowthAmount, this.growthAmount), 0, 1), MathUtil.EasingType.easeInOutQuad);
    }

    protected void tick() {
        if (!this.entity.level().isClientSide()) {
            if (this.canGrow()) {
                if (this.grow()) {
                    // the first part can always grow the max number of times.
                    if (this.parent.isPresent()) this.canGrowAgain = this.entity.getRandom().nextFloat() < 0.15F;
                    if (this.children.size() > MAX_CHILDREN - 1) this.canGrowAgain = false;
                    this.timeUntilNextGrowth = (int) ((this.entity.getRandom().nextGaussian() * 30) + 60);
                }
            }
        }

        if (this.getCurrentEnergy() <= 0) {
            this.die(false);
        } else {
            this.updateGrowthAmount();
            this.updateRadius();
        }

        for (MoldCell child : this.children) {
            child.tick();
        }
    }

    private void updateGrowthAmount() {
        this.previousGrowthAmount = this.growthAmount;
        this.growthAmount += (0.05) / 1.0F;
    }

    private void updateRadius() {
        this.previousRadius = this.radius;
        if (this.depth == 0) {
            this.radius = 1.0F;
            return;
        }
        float desiredRadius = (float) (Math.pow((1 - (((float)this.depth) / ((float)this.maxBranchDepth))) * Mth.clamp(this.growthAmount, 0, 1), 0.6F) * 0.66F);
        desiredRadius = Math.max(desiredRadius, 0.2F);
        //float damageFactor = this.parent.isPresent() ? this.getCurrentEnergy() / this.energy : this.getCurrentEnergy() / this.entity.startingEnergy;
        this.radius = Mth.lerp(0.05F, this.radius, desiredRadius);// * damageFactor;
    }

    private boolean matured() {
        return this.growthAmount > 1;
    }

    protected void hurt(DamageSource source, float amount) {
        this.latestDamageSource = source;
        this.damage += amount;
    }

    private void die(boolean removedByParent) {
        this.entity.parts.remove(this.id);
        if (this.parent.isPresent() && !removedByParent) {
            this.parent.get().children.remove(this);
        } else {
            this.entity.die(this.latestDamageSource);
        }

        for (MoldCell child : this.children) {
            child.die(true);
        }
        this.children.clear();
    }

    private boolean canGrow() {
        return this.matured() && this.canGrowAgain && this.getCurrentEnergy() > 1;// && this.timeUntilNextGrowth-- <= 0;
    }

    private Direction getValidGrowthDirection() {
        Direction[] a = ATTACHMENT_POINT_TO_VALID_DIRECTIONS[this.attachmentPoint.ordinal()];
        for (int i = 0; i < 10; i++) {
            Direction direction = a[this.entity.getRandom().nextInt(a.length)];
            if (direction != parentDirection) {
                return direction;
            }
        }

        return null;
    }

    private boolean grow() {
        Direction growthDirection = this.getValidGrowthDirection();
        if (growthDirection == null) return false;

        BlockPos growthPos = this.pos.relative(growthDirection);

        boolean occupied = this.entity.occupiedSpaces.contains(growthPos) || this.entity.level().getBlockState(growthPos).isFaceSturdy(this.entity.level(), growthPos, growthDirection);
        if (occupied) return false;

        AttachmentPoint supportDirection = getChildAttachmentPoint(growthPos, growthDirection);
        if (supportDirection == null) return false;

        MoldCell child = new MoldCell(this, supportDirection, growthPos, growthDirection.getOpposite());
        PacketDistributor.sendToPlayersTrackingEntity(this.entity, new ClientboundMoldGrowthPacket(child));
        return true;
    }

    private AttachmentPoint getChildAttachmentPoint(BlockPos growthPos, Direction growthDirection) {
        // check if this is a face or edge.
        if (!this.attachmentPoint.isEdge) {
            // if it's a face,
            Direction attachmentDirection = this.attachmentPoint.directions[0];
            return this.faceTest(growthPos, growthDirection, attachmentDirection);
        } // if not, check if the edge is inner or outer.
        else if (this.attachmentPoint.isInner) {
            // determine the attachment direction.
            // this will be the only direction of our attachment point that ISN'T the growth direction's opposite.
            Direction attachmentDirection =  this.attachmentPoint.directions[0];
            for (Direction direction : this.attachmentPoint.directions) {
                if (direction != growthDirection.getOpposite()) attachmentDirection = direction;
            }

            return this.faceTest(growthPos, growthDirection, attachmentDirection);
        } else {
            // determine the attachment direction.
            // this will be the only direction of our attachment point that ISN'T the growth direction.
            Direction attachmentDirection =  this.attachmentPoint.directions[0];
            for (Direction direction : this.attachmentPoint.directions) {
                if (direction != growthDirection) attachmentDirection = direction;
            }

            // if we're on an outer edge, then there has to be a face in our growth direction, so we can safely grow forward.
            // BUT check to make sure we're not going to immediately turn forward into an inner edge!
            // test to see if there's a face in the way.
            BlockPos testPos = growthPos.relative(growthDirection);
            if (this.entity.level().getBlockState(testPos).isFaceSturdy(this.entity.level(), testPos, growthDirection.getOpposite())) {
                // if so, then this is an inner corner attachment.
                return AttachmentPoint.EDGE_DATA_TO_ATTACHMENT_POINT.get(new EdgeData(true, attachmentDirection, growthDirection));
            } else {
                // otherwise this is just normal.
                return AttachmentPoint.EDGE_DATA_TO_ATTACHMENT_POINT.get(new EdgeData(false, attachmentDirection, attachmentDirection));
            }
        }
    }

    private AttachmentPoint faceTest(BlockPos growthPos, Direction growthDirection, Direction attachmentFace) {
        // test if the face below me exists
        BlockPos testPos = growthPos.relative(attachmentFace);
        if (this.entity.level().getBlockState(testPos).isFaceSturdy(this.entity.level(), testPos, attachmentFace.getOpposite())) {
            // if so, test if the face in front of me exists
            testPos = growthPos.relative(growthDirection);
            if (this.entity.level().getBlockState(testPos).isFaceSturdy(this.entity.level(), testPos, growthDirection.getOpposite())) {
                // if so, then this is an inner corner attachment.
                return AttachmentPoint.EDGE_DATA_TO_ATTACHMENT_POINT.get(new EdgeData(true, attachmentFace, growthDirection));
            } else {
                // if not, then this is attached to the ground.
                return AttachmentPoint.EDGE_DATA_TO_ATTACHMENT_POINT.get(new EdgeData(true, attachmentFace, attachmentFace));
            }
        } else {
            // if not, then check the face below and behind me to see if i can attach
            testPos = growthPos.relative(growthDirection.getOpposite()).relative(attachmentFace);
            if (this.entity.level().getBlockState(testPos).isFaceSturdy(this.entity.level(), testPos, growthDirection)) {
                // if so, this is an outer corner attachment.
                return AttachmentPoint.EDGE_DATA_TO_ATTACHMENT_POINT.get(new EdgeData(false, attachmentFace, growthDirection.getOpposite()));
            } else {
                return null;
            }
        }
    }

    public static AttachmentPoint getSupportDirection(BlockPos pos, Direction growthDirection, Level level) {
        AttachmentPoint[] supportList = AttachmentPoint.getSortedSupportList(growthDirection);
        BlockPos.MutableBlockPos mPos = pos.mutable();

        for (AttachmentPoint attachmentPoint : supportList) {
            boolean isValid = true;
            if (attachmentPoint.isInner) {
                // if it's an "inner" edge check each direction for each block.
                // (O = attach point)
                //                             |▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
                //                    |        |▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
                //                ----+----->  |▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
                //                    |        |▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
                //                    V        |▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
                //            _________________O▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
                //            ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
                //            ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓

                for (Direction direction : attachmentPoint.directions) {
                    mPos.setWithOffset(pos, direction);
                    if (!level.getBlockState(mPos).isFaceSturdy(level, mPos, direction.getOpposite(), SupportType.FULL)) {
                        isValid = false;
                        break;
                    }
                }

                if (isValid) return attachmentPoint;
            } else {
                // if it's an "outer" edge check each direction on the same block..
                // (O = attach point)
                //                        |
                //                        |
                //                        V
                //              O__________________
                //              |▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
                //              |▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
                //   -------->  |▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
                //              |▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
                //              |▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
                mPos.set(pos);
                for (Direction direction : attachmentPoint.directions) {
                    mPos.move(direction);
                }

                for (Direction direction : attachmentPoint.directions) {
                    if (direction == growthDirection) break;
                    if (!level.getBlockState(mPos).isFaceSturdy(level, mPos, direction.getOpposite(), SupportType.FULL)) {
                        isValid = false;
                        break;
                    }
                }

                if (isValid) return attachmentPoint;
            }
        }

        return null;
    }

    // really fucking awful
    private static Direction[][] PERPENDICULAR_DIRECTION = Util.make(() -> {
        Direction[][] array = new Direction[Direction.Axis.values().length][];
        array[Direction.Axis.X.ordinal()] = new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH};
        array[Direction.Axis.Y.ordinal()] = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        array[Direction.Axis.Z.ordinal()] = new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST};
        return array;
    });
    // ok this is gonna be really fucked
    // have to determine the valid growth directions based off an attachment point
    // cells attached to faces can potentially grow in all directions.
    // cells attached to edges can only grow along the edge. inner corners are the reverse of this.
    private static Direction[][] ATTACHMENT_POINT_TO_VALID_DIRECTIONS = Util.make(() -> {
        Direction[][] array = new Direction[AttachmentPoint.values().length][];
        AttachmentPoint[] attachmentPoints = AttachmentPoint.values();
        for (int i = 0; i < 6; i++) {
            array[i] = PERPENDICULAR_DIRECTION[attachmentPoints[i].directions[0].getAxis().ordinal()];
        }
        for (int i = 6; i < attachmentPoints.length; i++) {
            if (attachmentPoints[i].isInner) {
                Direction[] dirs = new Direction[attachmentPoints[i].directions.length];
                for (int j = 0; j < dirs.length; j++) {
                    dirs[j] = attachmentPoints[i].directions[j].getOpposite();
                }
                array[i] = dirs;
            } else {
                array[i] = attachmentPoints[i].directions;
            }
        }

        return array;
    });




    public enum AttachmentPoint {
        UP_FACE(Direction.UP),
        NORTH_FACE(Direction.NORTH),
        EAST_FACE(Direction.EAST),
        SOUTH_FACE(Direction.SOUTH),
        WEST_FACE(Direction.WEST),
        DOWN_FACE(Direction.DOWN),
        UP_NORTH_EDGE_OUTER(Direction.UP, Direction.NORTH),
        UP_EAST_EDGE_OUTER(Direction.UP, Direction.EAST),
        UP_SOUTH_EDGE_OUTER(Direction.UP, Direction.SOUTH),
        UP_WEST_EDGE_OUTER(Direction.UP, Direction.WEST),
        DOWN_NORTH_EDGE_OUTER(Direction.DOWN, Direction.NORTH),
        DOWN_EAST_EDGE_OUTER(Direction.DOWN, Direction.EAST),
        DOWN_SOUTH_EDGE_OUTER(Direction.DOWN, Direction.SOUTH),
        DOWN_WEST_EDGE_OUTER(Direction.DOWN, Direction.WEST),
        NORTH_EAST_EDGE_OUTER(Direction.NORTH, Direction.EAST),
        SOUTH_EAST_EDGE_OUTER(Direction.SOUTH, Direction.EAST),
        SOUTH_WEST_EDGE_OUTER(Direction.SOUTH, Direction.WEST),
        NORTH_WEST_EDGE_OUTER(Direction.NORTH, Direction.WEST),
        UP_NORTH_EDGE_INNER(true, Direction.UP, Direction.NORTH),
        UP_EAST_EDGE_INNER(true, Direction.UP, Direction.EAST),
        UP_SOUTH_EDGE_INNER(true, Direction.UP, Direction.SOUTH),
        UP_WEST_EDGE_INNER(true, Direction.UP, Direction.WEST),
        DOWN_NORTH_EDGE_INNER(true, Direction.DOWN, Direction.NORTH),
        DOWN_EAST_EDGE_INNER(true, Direction.DOWN, Direction.EAST),
        DOWN_SOUTH_EDGE_INNER(true, Direction.DOWN, Direction.SOUTH),
        DOWN_WEST_EDGE_INNER(true, Direction.DOWN, Direction.WEST),
        NORTH_EAST_EDGE_INNER(true, Direction.NORTH, Direction.EAST),
        SOUTH_EAST_EDGE_INNER(true, Direction.SOUTH, Direction.EAST),
        SOUTH_WEST_EDGE_INNER(true, Direction.SOUTH, Direction.WEST),
        NORTH_WEST_EDGE_INNER(true, Direction.NORTH, Direction.WEST);

        private static final AttachmentPoint[] fromOrdinal = AttachmentPoint.values();
        public final Vec3 vector;
        public final Direction[] directions;
        public final boolean isEdge;
        public final boolean isInner;

        AttachmentPoint(Direction... directions) {
            this(false, directions);
        }
        AttachmentPoint(boolean isInner, Direction... directions) {
            Vec3 vector = Vec3.ZERO;
            for (Direction direction : directions) {
                vector = vector.add(direction.getStepX(), direction.getStepY(), direction.getStepZ());
            }
            this.vector = vector.normalize();
            this.directions = directions;
            this.isEdge = this.directions.length > 1;
            this.isInner = isInner;
        }


        public static final HashMap<EdgeData, AttachmentPoint> EDGE_DATA_TO_ATTACHMENT_POINT = Util.make(() -> {
            HashMap<EdgeData, AttachmentPoint> map = new HashMap<>();
            for (AttachmentPoint value : AttachmentPoint.values()) {
                if (!value.isEdge) {
                    map.put(new EdgeData(false, value.directions[0], value.directions[0]), value);
                    map.put(new EdgeData(true, value.directions[0], value.directions[0]), value);
                } else {
                    map.put(new EdgeData(value.isInner, value.directions[0], value.directions[1]), value);
                    map.put(new EdgeData(value.isInner, value.directions[1], value.directions[0]), value);
                }
            }
            return map;
        });


        // even more really fucking awful
        private static AttachmentPoint[][] SORTED_SUPPORT_LISTS = Util.make(() -> {
            AttachmentPoint[][] array = new AttachmentPoint[Direction.values().length][];
            array[Direction.UP.ordinal()] = new AttachmentPoint[]{
                    AttachmentPoint.DOWN_NORTH_EDGE_INNER,
                    AttachmentPoint.DOWN_EAST_EDGE_INNER,
                    AttachmentPoint.DOWN_SOUTH_EDGE_INNER,
                    AttachmentPoint.DOWN_WEST_EDGE_INNER,
                    AttachmentPoint.NORTH_FACE,
                    AttachmentPoint.EAST_FACE,
                    AttachmentPoint.SOUTH_FACE,
                    AttachmentPoint.WEST_FACE,
                    AttachmentPoint.DOWN_FACE,
                    AttachmentPoint.UP_NORTH_EDGE_OUTER,
                    AttachmentPoint.UP_EAST_EDGE_OUTER,
                    AttachmentPoint.UP_SOUTH_EDGE_OUTER,
                    AttachmentPoint.UP_WEST_EDGE_OUTER,
                    AttachmentPoint.NORTH_EAST_EDGE_OUTER,
                    AttachmentPoint.SOUTH_EAST_EDGE_OUTER,
                    AttachmentPoint.SOUTH_WEST_EDGE_OUTER,
                    AttachmentPoint.NORTH_WEST_EDGE_OUTER,
                    AttachmentPoint.DOWN_NORTH_EDGE_OUTER,
                    AttachmentPoint.DOWN_EAST_EDGE_OUTER,
                    AttachmentPoint.DOWN_SOUTH_EDGE_OUTER,
                    AttachmentPoint.DOWN_WEST_EDGE_OUTER};
            array[Direction.DOWN.ordinal()] = new AttachmentPoint[]{
                    AttachmentPoint.UP_NORTH_EDGE_INNER,
                    AttachmentPoint.UP_EAST_EDGE_INNER,
                    AttachmentPoint.UP_SOUTH_EDGE_INNER,
                    AttachmentPoint.UP_WEST_EDGE_INNER,
                    AttachmentPoint.NORTH_FACE,
                    AttachmentPoint.EAST_FACE,
                    AttachmentPoint.SOUTH_FACE,
                    AttachmentPoint.WEST_FACE,
                    AttachmentPoint.UP_FACE,
                    AttachmentPoint.DOWN_NORTH_EDGE_OUTER,
                    AttachmentPoint.DOWN_EAST_EDGE_OUTER,
                    AttachmentPoint.DOWN_SOUTH_EDGE_OUTER,
                    AttachmentPoint.DOWN_WEST_EDGE_OUTER,
                    AttachmentPoint.NORTH_EAST_EDGE_OUTER,
                    AttachmentPoint.SOUTH_EAST_EDGE_OUTER,
                    AttachmentPoint.SOUTH_WEST_EDGE_OUTER,
                    AttachmentPoint.NORTH_WEST_EDGE_OUTER,
                    AttachmentPoint.UP_NORTH_EDGE_OUTER,
                    AttachmentPoint.UP_EAST_EDGE_OUTER,
                    AttachmentPoint.UP_SOUTH_EDGE_OUTER,
                    AttachmentPoint.UP_WEST_EDGE_OUTER};
            array[Direction.NORTH.ordinal()] = new AttachmentPoint[]{
                    AttachmentPoint.DOWN_SOUTH_EDGE_INNER,
                    AttachmentPoint.SOUTH_EAST_EDGE_INNER,
                    AttachmentPoint.SOUTH_WEST_EDGE_INNER,
                    AttachmentPoint.UP_SOUTH_EDGE_INNER,
                    AttachmentPoint.DOWN_FACE,
                    AttachmentPoint.EAST_FACE,
                    AttachmentPoint.WEST_FACE,
                    AttachmentPoint.SOUTH_FACE,
                    AttachmentPoint.UP_FACE,
                    AttachmentPoint.DOWN_NORTH_EDGE_OUTER,
                    AttachmentPoint.DOWN_EAST_EDGE_OUTER,
                    AttachmentPoint.DOWN_WEST_EDGE_OUTER,
                    AttachmentPoint.DOWN_SOUTH_EDGE_OUTER,
                    AttachmentPoint.NORTH_EAST_EDGE_OUTER,
                    AttachmentPoint.NORTH_WEST_EDGE_OUTER,
                    AttachmentPoint.SOUTH_EAST_EDGE_OUTER,
                    AttachmentPoint.SOUTH_WEST_EDGE_OUTER,
                    AttachmentPoint.UP_NORTH_EDGE_OUTER,
                    AttachmentPoint.UP_EAST_EDGE_OUTER,
                    AttachmentPoint.UP_WEST_EDGE_OUTER,
                    AttachmentPoint.UP_SOUTH_EDGE_OUTER};
            array[Direction.SOUTH.ordinal()] = new AttachmentPoint[]{
                    AttachmentPoint.DOWN_NORTH_EDGE_INNER,
                    AttachmentPoint.NORTH_EAST_EDGE_INNER,
                    AttachmentPoint.NORTH_WEST_EDGE_INNER,
                    AttachmentPoint.UP_NORTH_EDGE_INNER,
                    AttachmentPoint.DOWN_FACE,
                    AttachmentPoint.EAST_FACE,
                    AttachmentPoint.WEST_FACE,
                    AttachmentPoint.NORTH_FACE,
                    AttachmentPoint.UP_FACE,
                    AttachmentPoint.DOWN_SOUTH_EDGE_OUTER,
                    AttachmentPoint.DOWN_EAST_EDGE_OUTER,
                    AttachmentPoint.DOWN_WEST_EDGE_OUTER,
                    AttachmentPoint.DOWN_NORTH_EDGE_OUTER,
                    AttachmentPoint.SOUTH_EAST_EDGE_OUTER,
                    AttachmentPoint.SOUTH_WEST_EDGE_OUTER,
                    AttachmentPoint.NORTH_EAST_EDGE_OUTER,
                    AttachmentPoint.NORTH_WEST_EDGE_OUTER,
                    AttachmentPoint.UP_SOUTH_EDGE_OUTER,
                    AttachmentPoint.UP_EAST_EDGE_OUTER,
                    AttachmentPoint.UP_WEST_EDGE_OUTER,
                    AttachmentPoint.UP_NORTH_EDGE_OUTER};
            array[Direction.EAST.ordinal()] = new AttachmentPoint[]{
                    AttachmentPoint.DOWN_WEST_EDGE_INNER,
                    AttachmentPoint.SOUTH_WEST_EDGE_INNER,
                    AttachmentPoint.NORTH_WEST_EDGE_INNER,
                    AttachmentPoint.UP_WEST_EDGE_INNER,
                    AttachmentPoint.DOWN_FACE,
                    AttachmentPoint.NORTH_FACE,
                    AttachmentPoint.SOUTH_FACE,
                    AttachmentPoint.WEST_FACE,
                    AttachmentPoint.UP_FACE,
                    AttachmentPoint.DOWN_EAST_EDGE_OUTER,
                    AttachmentPoint.DOWN_NORTH_EDGE_OUTER,
                    AttachmentPoint.DOWN_SOUTH_EDGE_OUTER,
                    AttachmentPoint.DOWN_WEST_EDGE_OUTER,
                    AttachmentPoint.NORTH_EAST_EDGE_OUTER,
                    AttachmentPoint.SOUTH_EAST_EDGE_OUTER,
                    AttachmentPoint.SOUTH_WEST_EDGE_OUTER,
                    AttachmentPoint.NORTH_WEST_EDGE_OUTER,
                    AttachmentPoint.UP_EAST_EDGE_OUTER,
                    AttachmentPoint.UP_NORTH_EDGE_OUTER,
                    AttachmentPoint.UP_SOUTH_EDGE_OUTER,
                    AttachmentPoint.UP_WEST_EDGE_OUTER};
            array[Direction.WEST.ordinal()] = new AttachmentPoint[]{
                    AttachmentPoint.DOWN_EAST_EDGE_INNER,
                    AttachmentPoint.NORTH_EAST_EDGE_INNER,
                    AttachmentPoint.SOUTH_EAST_EDGE_INNER,
                    AttachmentPoint.UP_EAST_EDGE_INNER,
                    AttachmentPoint.DOWN_FACE,
                    AttachmentPoint.NORTH_FACE,
                    AttachmentPoint.SOUTH_FACE,
                    AttachmentPoint.EAST_FACE,
                    AttachmentPoint.UP_FACE,
                    AttachmentPoint.DOWN_WEST_EDGE_OUTER,
                    AttachmentPoint.DOWN_NORTH_EDGE_OUTER,
                    AttachmentPoint.DOWN_SOUTH_EDGE_OUTER,
                    AttachmentPoint.DOWN_EAST_EDGE_OUTER,
                    AttachmentPoint.SOUTH_WEST_EDGE_OUTER,
                    AttachmentPoint.NORTH_WEST_EDGE_OUTER,
                    AttachmentPoint.NORTH_EAST_EDGE_OUTER,
                    AttachmentPoint.SOUTH_EAST_EDGE_OUTER,
                    AttachmentPoint.UP_WEST_EDGE_OUTER,
                    AttachmentPoint.UP_NORTH_EDGE_OUTER,
                    AttachmentPoint.UP_SOUTH_EDGE_OUTER,
                    AttachmentPoint.UP_EAST_EDGE_OUTER};
            return array;
        });
        private static AttachmentPoint[] getSortedSupportList(Direction direction) {
            return SORTED_SUPPORT_LISTS[direction.ordinal()];
        }

        public static AttachmentPoint fromOrdinal(int ordinal) {
            return fromOrdinal[ordinal];
        }
    }

    private record EdgeData(boolean inner, Direction a, Direction b) {}
}
