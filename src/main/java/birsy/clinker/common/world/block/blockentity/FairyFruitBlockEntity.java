package birsy.clinker.common.world.block.blockentity;

import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.networking.packet.ClientboundFairyFruitBreakPacket;
import birsy.clinker.common.networking.packet.ClientboundFairyFruitGrowPacket;
import birsy.clinker.common.networking.packet.ClientboundFairyFruitSyncPacket;
import birsy.clinker.common.world.level.interactable.*;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlockEntities;
import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.Quaterniond;
import birsy.clinker.core.util.rigidbody.colliders.OBBCollisionShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FairyFruitBlockEntity extends BlockEntity implements InteractableParent {
    int jID = 0;
    int sID = 0;
    public int ticksExisted;
    public final List<FairyFruitJoint> joints;
    public final Map<Integer, FairyFruitJoint> jointByID = new HashMap<>();
    public final List<FairyFruitSegment> segments;
    public final Map<Integer, FairyFruitSegment> segmentByID = new HashMap<>();
    public FairyFruitJoint root;
    private FairyFruitJoint tip;
    private final List<Interactable> interactables;

    private static final Vec3 GRAVITY = new Vec3(0, -8, 0);
    private static final float JOINT_RADIUS = 0.5F;
    private static final float SEGMENT_LENGTH = 1.0F;
    private static final float GROWTH_RATE = 0.1F;
    private static final float FRUIT_GROWTH_RATE = 0.02F;

    public FairyFruitBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ClinkerBlockEntities.FAIRY_FRUIT.get(), pPos, pBlockState);
        this.interactables = new ArrayList<>();
        this.joints = new ArrayList<>();
        this.segments = new ArrayList<>();
        this.root = new FairyFruitJoint(this, 0.25, 0);
        this.root.isRoot = true;
        this.tip = this.root;
        this.joints.add(this.root);
        for (int i = 0; i < 8; i++) {
            this.grow(true, false);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FairyFruitBlockEntity entity) {
        entity.ticksExisted++;
        entity.updatePhysics();
        //entity.updateGrowth();

        if (!level.isClientSide()) {
            for (FairyFruitSegment segment : entity.segments) {
                if (segment.interactable == null) {
                    FairyFruitInteractable interactable = new FairyFruitInteractable(entity, segment, new OBBCollisionShape(0.25, 0.5, 0.25), true);
                    entity.addChild(interactable);
                    InteractableManager.addServerInteractable(interactable, (ServerLevel) level);
                } else {
                    segment.interactable.setPosition(segment.getCenter(1.0F));
                    segment.interactable.setRotation(segment.getOrientation(1.0F));
                }
            }

            if (level.random.nextInt(100) == 0) {
                //entity.grow(true, true);
            }

            ClinkerPacketHandler.sendToClientsInChunk(level.getChunkAt(pos), new ClientboundFairyFruitSyncPacket(entity));
        }


        for (FairyFruitSegment segment : entity.segments) {
            segment.tick();
        }

        if (entity.isRemoved()) {
            entity.remove();
        }
    }

    public void updatePhysics() {
        for (FairyFruitJoint joint : this.joints) {
            joint.accelerate(GRAVITY);
            joint.accelerate(new Vec3(1, 1, 1).scale(1.0 / (double)ticksExisted));
            applyBouyancyAndDragForce(joint);

            joint.beginTick(1.0f / 20.0f);
        }

        for (int i = 0; i < 16; i++) {
            for (FairyFruitSegment segment : segments) {
                segment.applyConstraint();
            }
            for (FairyFruitJoint joint1 : this.joints) {
                joint1.applyCollisionConstraints();
                for (FairyFruitJoint joint2 : this.joints) {
                    collideJoints(joint1, joint2);
                }
            }
        }

        for (FairyFruitJoint joint : this.joints) {
            joint.finalizeTick();
        }

        Vec3 rootPosition = new Vec3(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1.0, this.worldPosition.getZ() + 0.5);
        this.root.position = rootPosition;
        this.root.pPosition = rootPosition;
        this.root.physicsPrevPosition = rootPosition;
        this.root.physicsNextPosition = rootPosition;
    }
    private void applyBouyancyAndDragForce(FairyFruitJoint joint) {
        if (!this.hasLevel()) return;

        double mass = 0.9;
        double inverseMass = 1.0 / mass;
        double airDensity = 0.1;
        // calculates the bouyant force in a really jank way
        // takes into account partial submersion... kinda
        Vec3 downPos = joint.position.subtract(0, joint.radius, 0);
        BlockPos jointPosDown = new BlockPos(joint.position);
        BlockPos jointPosUp = new BlockPos(joint.position.add(0, joint.radius, 0));

        double volumeInDown = downPos.y - Math.ceil(downPos.y);
        FluidState stateDown = this.level.getFluidState(jointPosDown);
        double densityDown = stateDown.isEmpty() ? airDensity : stateDown.getFluidType().getDensity() * 0.001D;

        double volumeInUp = 1 - volumeInDown;
        FluidState stateUp = this.level.getFluidState(jointPosUp);
        double densityUp = stateUp.isEmpty() ? airDensity : stateUp.getFluidType().getDensity() * 0.001D;

        double averageDensity = (densityDown * volumeInDown + densityUp * volumeInUp);
        joint.accelerate(GRAVITY.scale(-1 * averageDensity).scale(inverseMass));

        Vec3 velocity = joint.getDeltaMovement();
        double dragCoefficient = 0.05;
        Vec3 drag = velocity.scale(Math.exp(-averageDensity * dragCoefficient));
        joint.physicsPrevPosition = joint.position.subtract(drag);
    }
    private void collideJoints(FairyFruitJoint joint1, FairyFruitJoint joint2) {
        if (!joint1.shouldCollide || !joint2.shouldCollide) return;
        if (joint1 == joint2) return;
        double minimumDistance = joint1.radius + joint2.radius;
        double distanceSqr = joint1.physicsNextPosition.distanceToSqr(joint2.physicsNextPosition);
        if (distanceSqr < minimumDistance * minimumDistance) {
            double correctionDistance = minimumDistance - Math.sqrt(distanceSqr);
            //point of collision will always be directly in the middle, in the case of spheres.
            Vec3 collisionVector = joint1.physicsNextPosition.subtract(joint2.physicsNextPosition).normalize().scale(correctionDistance * 0.5);

            joint1.physicsNextPosition = joint1.physicsNextPosition.add(collisionVector);
            joint2.physicsNextPosition = joint2.physicsNextPosition.subtract(collisionVector);
        }
    }

    public void updateGrowth() {
        final float epsilon = GROWTH_RATE;
        for (FairyFruitJoint joint : this.joints) {
            if (joint.radius <= JOINT_RADIUS && !joint.isTip) {
                joint.radius = Math.min(JOINT_RADIUS, joint.radius + (JOINT_RADIUS * GROWTH_RATE));
                if (joint.radius - JOINT_RADIUS < epsilon) joint.shouldCollide = true;
            }

            if (joint.isTip) {
                joint.fruitGrowth = Math.min(1.0F, joint.fruitGrowth + (FRUIT_GROWTH_RATE));
            } else {
                joint.fruitGrowth = 0;
            }
        }
        for (FairyFruitSegment segment : this.segments) {
            if (segment.length <= SEGMENT_LENGTH && !segment.isTip()) segment.length = Math.min(SEGMENT_LENGTH, segment.length + (SEGMENT_LENGTH * GROWTH_RATE));
        }
    }

    public void grow(boolean beginGrown, boolean sendPacket) {
        FairyFruitJoint newTip = new FairyFruitJoint(this, beginGrown ? 0.25 : 0);
        newTip.position = this.tip.position.add(0, beginGrown ? -1 : 0, 0);
        newTip.shouldCollide = beginGrown;
        FairyFruitSegment newSegment = new FairyFruitSegment(this, this.tip, newTip, beginGrown ? 1 : 0);
        this.setTip(newTip);
        if (sendPacket) {
            if (this.hasLevel()) if (!this.level.isClientSide()) ClinkerPacketHandler.sendToClientsInChunk(this.level.getChunkAt(this.worldPosition), new ClientboundFairyFruitGrowPacket(this, beginGrown, newSegment.id, newTip.id));
        }
    }

    public void grow(boolean beginGrown, int jointID, int segmentID) {
        this.sID = Math.min(this.sID, segmentID);
        this.jID = Math.min(this.jID, jointID);

        FairyFruitJoint newTip = new FairyFruitJoint(this, beginGrown ? 0.25 : 0, jointID);
        newTip.position = this.tip.position.add(0, beginGrown ? -1 : 0, 0);
        newTip.shouldCollide = beginGrown;
        FairyFruitSegment newSegment = new FairyFruitSegment(this, this.tip, newTip, beginGrown ? 1 : 0, segmentID);
        this.setTip(newTip);
    }
    
    public void setTip(FairyFruitJoint newTip) {
        if (this.tip != null) this.tip.isTip = false;
        this.tip = newTip;
        this.tip.isTip = true;
    }

    public void syncFromNBT(CompoundTag nbt) {
        CompoundTag joints = nbt.getCompound("joints");
        for (int i = 0; i < joints.size(); i++) {
            CompoundTag jointTag = joints.getCompound("joint" + i);
            FairyFruitJoint.deserialize(this, jointTag);
        }

        CompoundTag segments = nbt.getCompound("segments");
        for (int i = 0; i < segments.size(); i++) {
            CompoundTag segmentTag = segments.getCompound("segment" + i);
            FairyFruitSegment.deserialize(this, segmentTag);
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        CompoundTag joints = new CompoundTag();
        int i = 0;
        for (FairyFruitJoint joint : this.joints) {
            joints.put("joint" + i++, joint.serialize());
        }

        i = 0;
        CompoundTag segments = new CompoundTag();
        for (FairyFruitSegment segment : this.segments) {
            segments.put("segment" + i++, segment.serialize());
        }

        tag.put("joints", joints);
        tag.put("segments", segments);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);

        this.joints.clear();
        CompoundTag joints = nbt.getCompound("joints");
        for (int i = 0; i < joints.size(); i++) {
            CompoundTag jointTag = joints.getCompound("joint" + i);
            FairyFruitJoint.deserializeNew(this, jointTag);
        }

        this.segments.clear();
        CompoundTag segments = nbt.getCompound("segments");
        for (int i = 0; i < segments.size(); i++) {
            CompoundTag segmentTag = segments.getCompound("segment" + i);
            FairyFruitSegment.deserializeNew(this, segmentTag);
        }
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        this.remove();
    }

    @Override
    public List<Interactable> getChildInteractables() {
        return this.interactables;
    }

    @Override
    public void clearChildren() {
        this.interactables.clear();
    }

    public static class FairyFruitJoint {
        public final int id;

        public Vec3 physicsPrevPosition;
        public Vec3 pPosition;
        public Vec3 position;
        public Vec3 physicsNextPosition;
        public double roll;

        private Vec3 pushes;
        private Vec3 acceleration;
        public double radius;
        public FairyFruitSegment topAttachment;
        public FairyFruitSegment bottomAttachment;
        final FairyFruitBlockEntity parent;
        float fruitGrowth = 0;
        public boolean isTip;
        public boolean isRoot;
        public boolean shouldCollide = false;

        public FairyFruitJoint(FairyFruitBlockEntity parent, double radius, int id) {
            this.id = id;
            this.radius = radius;
            this.pushes = Vec3.ZERO;
            this.parent = parent;
            Vec3 position = parent.tip == null ? new Vec3(parent.worldPosition.getX() + 0.5, parent.worldPosition.getY() + 1.0, parent.worldPosition.getZ() + 0.5) : parent.tip.position.add(0, 2 * radius, 0);
            this.pPosition = position;
            this.position = position;
            this.physicsPrevPosition = position;
            this.physicsNextPosition = position;
            this.acceleration = Vec3.ZERO;
            this.roll = 0.0;
            parent.joints.add(this);
            parent.jointByID.remove(this.id);
            parent.jointByID.put(this.id, this);
        }

        public FairyFruitJoint(FairyFruitBlockEntity parent, double radius) {
            this(parent, radius, parent.jID++);
        }

        public Vec3 getPosition(double partialTick) {
            return pPosition.lerp(position, partialTick);
        }
        
        protected void beginTick(float deltaTime) {
            this.pPosition = this.position;
            if (this.isTip) this.shouldCollide = false;
            if (this.isRoot) return;

            Vec3 velocity = this.getDeltaMovement();

            velocity = velocity.add(pushes);
            this.pushes = Vec3.ZERO;

            this.physicsPrevPosition = this.position;
            this.physicsNextPosition = this.position.add(velocity).add(this.acceleration.scale(deltaTime * deltaTime));

            this.acceleration = Vec3.ZERO;
        }

        protected void finalizeTick() {
            applyCollisionConstraints();
            this.position = this.physicsNextPosition;
        }

        protected void applyCollisionConstraints() {
            this.physicsNextPosition = this.position.add(constrainVelocity(this.physicsNextPosition.subtract(position)));
        }

        // ripped from entity code. not entirely sure how it works!
        private Vec3 constrainVelocity(Vec3 velocity) {
            Level level = parent.level;
            float smallerRadius = 0.25F;
            AABB aabb = new AABB(this.position.add(smallerRadius, 2 * smallerRadius, smallerRadius), this.position.subtract(smallerRadius, 0, smallerRadius));

            List<VoxelShape> list = level.getEntityCollisions(null, aabb.expandTowards(velocity));

            return Entity.collideBoundingBox(null, velocity, aabb, level, list);
        }

        protected void accelerate(Vec3 amount) {
            if (this.isRoot) return;
            this.acceleration = this.acceleration.add(amount);
        }

        protected void push(Vec3 amount) {
            pushes = pushes.add(amount);
        }

        protected void push(double x, double y, double z) {
            pushes = pushes.add(x, y, z);
        }

        protected Vec3 getDeltaMovement() {
            return this.position.subtract(this.physicsPrevPosition);
        }

        public Quaterniond getOrientation(double partialTick) {
            Quaterniond roll = new Quaterniond(new Vec3(0, -1, 0), this.roll);

            if (this.topAttachment == null || this.isRoot) return roll;
            Vec3 topCenter = this.topAttachment.getCenter(partialTick);
            Vec3 bottomCenter = this.bottomAttachment == null ? this.getPosition(partialTick) : this.bottomAttachment.getCenter(partialTick);

            return Quaterniond.lookAt(topCenter, bottomCenter, new Vec3(0, -1, 0)).normalize().mul(roll);
        }

        public void destroy() {
            this.parent.joints.remove(this);
            this.parent.jointByID.remove(this.id);
            if (this.bottomAttachment != null) this.bottomAttachment.destroy();
        }

        public CompoundTag serialize() {
            CompoundTag jointTag = new CompoundTag();

            jointTag.putDouble("x", this.position.x);
            jointTag.putDouble("y", this.position.y);
            jointTag.putDouble("z", this.position.z);

            jointTag.putDouble("dX", this.physicsPrevPosition.x);
            jointTag.putDouble("dY", this.physicsPrevPosition.y);
            jointTag.putDouble("dZ", this.physicsPrevPosition.z);

            jointTag.putDouble("roll", this.roll);
            jointTag.putDouble("radius", this.radius);
            jointTag.putBoolean("root", this.isRoot);
            jointTag.putBoolean("tip", this.isTip);
            jointTag.putFloat("fruit", this.fruitGrowth);
            jointTag.putBoolean("collision", this.shouldCollide);

            jointTag.putInt("id", this.id);

            return jointTag;
        }

        public FairyFruitJoint deserialize(CompoundTag jointTag) {
            Vec3 position = new Vec3(jointTag.getDouble("x"), jointTag.getDouble("y"), jointTag.getDouble("z"));
            Vec3 physicsPrevPosition = new Vec3(jointTag.getDouble("dX"), jointTag.getDouble("dY"), jointTag.getDouble("dZ"));
            double roll = jointTag.getDouble("roll");
            double radius = jointTag.getDouble("radius");
            boolean tip = jointTag.getBoolean("tip");
            boolean root = jointTag.getBoolean("root");
            float fruitGrowth = jointTag.getFloat("fruit");
            boolean hasCollision = jointTag.getBoolean("collision");

            this.radius = radius;
            this.position = position;
            this.pPosition = position;
            this.physicsPrevPosition = physicsPrevPosition;
            this.roll = roll;
            this.fruitGrowth = fruitGrowth;
            this.shouldCollide = hasCollision;
            this.isTip = tip;
            if (tip) parent.tip = this;
            this.isRoot = tip;
            if (root) parent.root = this;

            return this;
        }

        public static FairyFruitJoint deserialize(FairyFruitBlockEntity parent, CompoundTag jointTag) {
            return parent.jointByID.get(jointTag.getInt("id")).deserialize(jointTag);
        }

        public static FairyFruitJoint deserializeNew(FairyFruitBlockEntity parent, CompoundTag jointTag) {
            int id = jointTag.getInt("id");
            FairyFruitJoint joint = new FairyFruitJoint(parent, jointTag.getDouble("radius"), jointTag.getInt("id"));
            parent.jID = Math.min(parent.jID, id);
            return joint.deserialize(jointTag);
        }
    }

    public static class FairyFruitSegment {
        public final int id;

        public final FairyFruitJoint topJoint;
        public final FairyFruitJoint bottomJoint;
        private double pLength;
        public double length;
        public final FairyFruitBlockEntity parent;
        private FairyFruitInteractable interactable;

        public FairyFruitSegment(FairyFruitBlockEntity parent, FairyFruitJoint topJoint, FairyFruitJoint bottomJoint, double length, int id) {
            this.id = id;
            this.topJoint = topJoint;
            this.topJoint.bottomAttachment = this;
            this.bottomJoint = bottomJoint;
            this.bottomJoint.topAttachment = this;
            this.length = length;
            this.pLength = length;
            this.parent = parent;
            parent.segments.add(this);
            parent.segmentByID.remove(id);
            parent.segmentByID.put(id, this);
        }

        public FairyFruitSegment(FairyFruitBlockEntity parent, FairyFruitJoint topJoint, FairyFruitJoint bottomJoint, double length) {
            this(parent, topJoint, bottomJoint, length, parent.sID++);
        }

        public void tick() {
            this.pLength = length;
            if (this.isTip()) {
                this.length = 0.4375 * this.fruitGrowth();
            }

            if (this.interactable != null && this.length != this.pLength) {
                OBBCollisionShape shape = new OBBCollisionShape(0.375 * 0.5, this.length * 0.5, 0.375 * 0.5);
                shape.setTransform(this.interactable.getTransform());
                this.interactable.shape = shape;
                this.interactable.updateShape();
            }
        }

        public void split() {
            this.topJoint.bottomAttachment = null;
            this.bottomJoint.topAttachment = null;
            this.parent.segments.remove(this);
            this.parent.segmentByID.remove(this.id);
            if (interactable != null) {
                this.parent.interactables.remove(interactable);
                interactable.markForRemoval();
            }
        }

        public void destroy() {
            this.split();
            Clinker.LOGGER.info("destroyed!");
            this.parent.setTip(this.topJoint);
            if (!bottomJoint.isTip) this.bottomJoint.destroy();

            float boundX = 0.25F, boundY= 0.5F, boundZ = 0.25F;
            if (this.interactable != null) {
                Vec3 size = this.interactable.shape.size;
                boundX = (float) size.x;
                boundY = (float) size.y;
                boundZ = (float) size.z;
            }
            if (this.parent.hasLevel()) {
                Vec3 segmentPosition = this.getCenter(1.0);
                float particleDensity = 0.1F;
                for (float x = -boundX; x < boundX; x += particleDensity) {
                    for (float y = -boundY; y < boundY; y += particleDensity) {
                        for (float z = -boundZ; z < boundZ; z += particleDensity) {
                            Vec3 particlePos = new Vec3(x, y, z);
                            particlePos = this.getOrientation(1.0).transform(particlePos);
                            particlePos = particlePos.add(segmentPosition);

                            parent.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, parent.getBlockState()).setPos(parent.getBlockPos()),
                                    particlePos.x(), particlePos.y(), particlePos.z(),
                                    0, 0, 0);
                        }
                    }
                }
            }
        }

        public boolean isTip() {
            return bottomJoint.isTip;
        }

        public float fruitGrowth() {
            return bottomJoint.fruitGrowth;
        }

        public Vec3 getCenter(double partialTick) {
            return topJoint.getPosition(partialTick).add(bottomJoint.getPosition(partialTick)).scale(0.5);
        }

        public Quaterniond getOrientation(double partialTick) {
            Quaterniond roll = new Quaterniond(new Vec3(0, -1, 0), (topJoint.roll + bottomJoint.roll) * 0.5);
            return Quaterniond.lookAt(topJoint.getPosition(partialTick), bottomJoint.getPosition(partialTick), new Vec3(0, -1, 0)).normalize().mul(roll);
        }

        protected void applyConstraint() {
            Vec3 pos1 = this.topJoint.physicsNextPosition;
            Vec3 pos2 = this.bottomJoint.physicsNextPosition;

            Vec3 center = pos1.add(pos2).scale(0.5);
            Vec3 direction = pos1.subtract(pos2).normalize();

            if (!this.topJoint.isRoot) this.topJoint.physicsNextPosition = center.add(direction.scale(this.length * 0.5));
            this.bottomJoint.physicsNextPosition = center.subtract(direction.scale(this.length * 0.5));
        }

        protected void push(Vec3 push) {
            this.topJoint.push(push);
            this.bottomJoint.push(push);
        }

        public CompoundTag serialize() {
            CompoundTag segmentTag = new CompoundTag();

            segmentTag.putInt("topId", this.topJoint.id);
            segmentTag.putInt("bottomId", this.bottomJoint.id);
            segmentTag.putDouble("length", this.length);
            segmentTag.putInt("id", this.id);

            return segmentTag;
        }

        public FairyFruitSegment deserialize(CompoundTag segmentTag) {
            double length = segmentTag.getDouble("length");
            this.length = length;
            return this;
        }

        public static FairyFruitSegment deserialize(FairyFruitBlockEntity parent, CompoundTag segmentTag) {
            return parent.segmentByID.get(segmentTag.getInt("id")).deserialize(segmentTag);
        }

        public static FairyFruitSegment deserializeNew(FairyFruitBlockEntity parent, CompoundTag segmentTag) {
            int topIndex = segmentTag.getInt("topId");
            int bottomIndex = segmentTag.getInt("bottomId");
            double length = segmentTag.getDouble("length");
            int id = segmentTag.getInt("id");

            FairyFruitSegment segment = new FairyFruitSegment(parent, parent.jointByID.get(topIndex), parent.jointByID.get(bottomIndex), length, id);
            parent.sID = Math.min(parent.sID, id);

            return segment;
        }
    }

    public static class FairyFruitInteractable extends CollidableInteractable {
        final FairyFruitBlockEntity parent;
        final FairyFruitSegment segmentParent;

        public FairyFruitInteractable(FairyFruitBlockEntity parent, FairyFruitSegment segmentParent, OBBCollisionShape shape, boolean reacts) {
            super(shape, reacts, 0.0, 0.01,true);
            parent.addChild(this);
            this.parent = parent;
            segmentParent.interactable = this;
            this.segmentParent = segmentParent;
        }

        public FairyFruitInteractable() {
            super();
            this.parent = null;
            this.segmentParent = null;
        }

        @Override
        protected void tick() {
            super.tick();
            if (parent.isRemoved()) parent.remove();
        }

        @Override
        public boolean onHit(InteractionContext interactionContext, @Nullable Entity entity, boolean clientSide) {
            boolean result = super.onHit(interactionContext, entity, clientSide);
            if (clientSide) return result;

            if (entity instanceof Player player) {
                if (player.getAbilities().mayBuild &&
                    player.getItemInHand(interactionContext.hand()).getItem().canAttackBlock(this.parent.getBlockState(), this.parent.level, MathUtils.blockPosFromVec3(this.getPosition()), player)) {
                    this.segmentParent.destroy();
                    ClinkerPacketHandler.sendToClientsInChunk(this.parent.level.getChunkAt(this.parent.worldPosition), new ClientboundFairyFruitBreakPacket(this.parent, this.segmentParent.id));
                }
                Clinker.LOGGER.info("break!");
            }
            return true;
        }


        @Override
        public boolean onTouch(InteractionContext context, Entity touchingEntity, boolean clientSide) {
            return true;
        }

        @Override
        public void push(Vec3 movement) {
            segmentParent.push(movement);
            super.push(movement);
        }

        @Override
        public CompoundTag serialize(@Nullable CompoundTag tag) {
            if (tag == null) tag = new CompoundTag();
            tag.putUUID("uuid", this.uuid);
            tag.putDouble("mass", this.mass);
            tag.putBoolean("reacts", this.reacts);
            tag.putDouble("reactionStrength", this.reactionStrength);
            tag.putBoolean("outline", this.hasOutline);
            tag.putString("name", this.getClass().getName());
            this.shape.serialize(tag);

            return tag;
        }

        @Override
        public <I extends Interactable> I reconstructOnClient(CompoundTag tag) {
            return (I) new CollidableInteractable((OBBCollisionShape) new OBBCollisionShape(0, 0, 0).deserialize(tag), tag.getBoolean("reacts"), tag.getDouble("mass"), tag.getDouble("reactionStrength"), tag.getUUID("uuid"), tag.getBoolean("outline"));
        }
    }
}
