package birsy.clinker.common.world.entity.mold;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.*;

@Deprecated
public class BiohazardEntity extends LivingEntity {
    public int maxDepth;
    int maxEnergy;
    BiohazardEntityPart root;
    public List<BiohazardEntityPart> parts;
    Set<BlockPos> occupiedSpaces;
    public BiohazardEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
        this.parts = new ArrayList<>();
        this.occupiedSpaces = new HashSet<>();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createLivingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        this.setPos(Math.floor(this.getX()) + 0.5F, Math.floor(this.getY()), Math.floor(this.getZ()) + 0.5F);
        this.maxEnergy = 16;
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.maxEnergy);
        this.root = new BiohazardEntityPart(this, Direction.DOWN, this.maxEnergy);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.root.energy <= 0) {
            this.remove(RemovalReason.KILLED);
        }
        this.root.tick();
    }

    @Override
    public boolean hurt(DamageSource p_21016_, float p_21017_) {
        return super.hurt(p_21016_, p_21017_);

//        if (this.isInvulnerableTo(p_21016_)) {
//            return false;
//        } else if (this.level().isClientSide) {
//            return false;
//        } else if (this.isDeadOrDying()) {
//            return false;
//        } else if (p_21016_.is(DamageTypeTags.IS_FIRE) && this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
//            return false;
//        }
//        this.root.hurt(p_21016_, p_21017_);
//        return true;
    }

    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        return false;
    }

    // can't be moved around.
    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }
    @Override
    public void push(Entity p_21294_) {}
    @Override
    public boolean isNoGravity() {
        return true;
    }
    @Override
    public void knockback(double p_147241_, double p_147242_, double p_147243_) {}

    // all of these are required for living entities for whatever reason
    @Override
    public Iterable<ItemStack> getArmorSlots() {return Collections.singleton(ItemStack.EMPTY);}
    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {return ItemStack.EMPTY;}
    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack item) {}
    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    public class BiohazardEntityPart {
        private static final int MAX_CHILDREN = 4;

        final BiohazardEntity entity;
        final Direction attachedFace;
        final Optional<BiohazardEntityPart> parent;
        public final List<BiohazardEntityPart> children;

        DamageSource latestDamageSource;
        public final Vec3 position;
        float energy;
        float damage;
        int timeUntilNextGrowth = 60;
        boolean canGrowAgain = true;

        public int depth;

        public BiohazardEntityPart(BiohazardEntity entity, Direction attachedFace, int initialEnergy) {
            this.entity = entity;
            this.entity.root = this;
            this.entity.parts.add(this);

            this.depth = 0;

            this.attachedFace = attachedFace;
            this.position = new Vec3(0, 0.5, 0);
            this.entity.occupiedSpaces.add(BlockPos.containing(this.position));
            this.energy = initialEnergy;
            this.damage = 0;
            this.parent = Optional.empty();
            this.children = new ArrayList<>();
        }

        public BiohazardEntityPart(BiohazardEntityPart parent, Direction attachedFace, Vec3 position) {
            this.entity = parent.entity;
            this.entity.parts.add(this);

            this.attachedFace = attachedFace;

            this.parent = Optional.of(parent);
            parent.children.add(this);
            this.depth = parent.depth + 1;
            this.entity.maxDepth = Math.max(this.entity.maxDepth, this.depth);
            this.position = position;
            this.entity.occupiedSpaces.add(BlockPos.containing(this.position));
            this.children = new ArrayList<>();
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

        public float getRadius() {
            if (this.getCurrentEnergy() == this.entity.maxEnergy) return 1.0F;
            float t = this.getCurrentEnergy() / this.entity.maxEnergy;
            return (t * t * (3.0F - 2.0F * t)) * 0.666F;
        }

        private Vec3 getPosition() {
            return this.position.add(this.entity.position());
        }

        protected void tick() {
            if (this.canGrow()) {
                if (this.grow()) {
                    // the first part can always grow the max number of times.
                    if (this.parent.isPresent()) this.canGrowAgain = random.nextFloat() < 0.1F;
                    if (this.children.size() > MAX_CHILDREN - 1) this.canGrowAgain = false;
                    this.timeUntilNextGrowth = (int) ((this.entity.random.nextGaussian() * 30) + 60);
                }
            }

            if (this.getCurrentEnergy() <= 0) {
                this.die(false);
            }

            for (BiohazardEntityPart child : this.children) {
                child.tick();
            }
        }

        private boolean canGrow() {
            return this.canGrowAgain && this.getCurrentEnergy() > 1; //&& this.timeUntilNextGrowth-- < 0;
        }

        // makes sure that it doesnt grow on top of itself.
        // kind of jank but supports non-axially aligned growth directions.
        private Optional<Direction> getValidGrowthDirection() {
            if (this.parent.isEmpty()) {
                return Optional.of(getRandomPerpendicularDirection(this.attachedFace, this.entity.getRandom()));
            }
            for (int i = 0; i < 10; i++) {
                Direction direction = getRandomPerpendicularDirection(this.attachedFace, this.entity.getRandom());
                Vec3 vector = new Vec3(direction.getStepX(), direction.getStepY(), direction.getStepZ());
                if (this.entity.occupiedSpaces.contains(BlockPos.containing(this.position.add(vector)))) {
                    break;
                }
                return Optional.of(direction);
            }

            return Optional.empty();
        }

        private static final Direction[] X_DIRS = new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH};
        private static final Direction[] Y_DIRS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        private static final Direction[] Z_DIRS = new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST};
        private Direction getRandomPerpendicularDirection(Direction normal, RandomSource randomSource) {
            Direction.Axis axis = normal.getAxis();

            switch (axis) {
                case X:
                    return X_DIRS[randomSource.nextInt(4)];
                default:
                    return Y_DIRS[randomSource.nextInt(4)];
                case Z:
                    return Z_DIRS[randomSource.nextInt(4)];
            }
        }

        private boolean grow() {
            Optional<Direction> validDirection = this.getValidGrowthDirection();
            if (validDirection.isEmpty()) return false;
            Direction growthDirection = validDirection.get();
            Vec3 nextPosition = this.getPosition();
            ClipContext clipContext = new ClipContext(nextPosition, nextPosition = nextPosition.add(growthDirection.getStepX(), growthDirection.getStepY(), growthDirection.getStepZ()), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
            BlockHitResult raycastOut = this.entity.level().clip(clipContext);
            if (raycastOut.getType() == HitResult.Type.BLOCK) {
                return createChild(raycastOut.getLocation(), raycastOut.getDirection());
            }

            clipContext = new ClipContext(nextPosition, nextPosition = nextPosition.add(0, -1, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
            BlockHitResult raycastDown = this.entity.level().clip(clipContext);
            if (raycastDown.getType() == HitResult.Type.BLOCK) {
                return createChild(raycastDown.getLocation(), raycastDown.getDirection());
            }

            clipContext = new ClipContext(nextPosition, nextPosition.subtract(growthDirection.getStepX(), growthDirection.getStepY(), growthDirection.getStepZ()), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
            BlockHitResult raycastIn = this.entity.level().clip(clipContext);
            if (raycastIn.getType() == HitResult.Type.BLOCK) {
                return createChild(raycastIn.getLocation(), raycastIn.getDirection());
            }

            return false;
        }

        private boolean createChild(Vec3 hitPosition, Direction facing) {
            Vec3 position = hitPosition.add(facing.getStepX() * 0.5, facing.getStepY() * 0.5, facing.getStepZ() * 0.5);
            BiohazardEntityPart child = new BiohazardEntityPart(this, facing, position.subtract(this.entity.position()));
            return true;
        }

        protected void hurt(DamageSource source, float amount) {
            this.latestDamageSource = source;
            this.damage += amount;
        }

        private void die(boolean removedByParent) {
            this.entity.parts.remove(this);
            if (this.parent.isPresent() && !removedByParent) {
                this.parent.get().children.remove(this);
            } else {
                this.entity.die(this.latestDamageSource);
            }

            for (BiohazardEntityPart child : this.children) {
                child.die(true);
            }
            this.children.clear();
        }
    }

    public enum AttachmentPoint {
        UP_FACE(Direction.UP),
        NORTH_FACE(Direction.NORTH),
        EAST_FACE(Direction.EAST),
        SOUTH_FACE(Direction.SOUTH),
        WEST_FACE(Direction.WEST),
        DOWN_FACE(Direction.DOWN),
        UP_NORTH_EDGE(Direction.UP, Direction.NORTH),
        UP_EAST_EDGE(Direction.UP, Direction.EAST),
        UP_SOUTH_EDGE(Direction.UP, Direction.SOUTH),
        UP_WEST_EDGE(Direction.UP, Direction.WEST),
        DOWN_NORTH_EDGE(Direction.DOWN, Direction.NORTH),
        DOWN_EAST_EDGE(Direction.DOWN, Direction.EAST),
        DOWN_SOUTH_EDGE(Direction.DOWN, Direction.SOUTH),
        DOWN_WEST_EDGE(Direction.DOWN, Direction.WEST),
        NORTH_EAST_EDGE(Direction.NORTH, Direction.EAST),
        SOUTH_EAST_EDGE(Direction.SOUTH, Direction.EAST),
        SOUTH_WEST_EDGE(Direction.SOUTH, Direction.WEST),
        NORTH_WEST_EDGE(Direction.NORTH, Direction.WEST);

        final Vec3 vector;
        final Direction[] directions;

        AttachmentPoint(Direction... directions) {
            Vec3 vector = Vec3.ZERO;
            for (Direction direction : directions) {
                vector = vector.add(direction.getStepX(), direction.getStepY(), direction.getStepZ());
            }
            this.vector = vector.normalize();
            this.directions = directions;
        }
    }
}
