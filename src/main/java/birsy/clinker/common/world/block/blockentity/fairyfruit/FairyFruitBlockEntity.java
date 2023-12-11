package birsy.clinker.common.world.block.blockentity.fairyfruit;

import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.networking.packet.ClientboundFairyFruitGrowPacket;
import birsy.clinker.common.networking.packet.ClientboundFairyFruitRemovalPacket;
import birsy.clinker.common.networking.packet.ClientboundFairyFruitSyncPacket;

import birsy.clinker.common.world.level.interactable.Interactable;
import birsy.clinker.common.world.level.interactable.InteractableParent;
import birsy.clinker.common.world.physics.particle.ParticleParent;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlockEntities;
import birsy.clinker.core.registry.ClinkerItems;
import birsy.clinker.common.world.physics.rigidbody.colliders.OBBCollisionShape;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FairyFruitBlockEntity extends BlockEntity implements InteractableParent, ParticleParent<FairyFruitJoint, FairyFruitSegment> {
    public static Vec3 ORIENTATION_FORWARD = new Vec3(0, -1, 0);
    public int ticksExisted;

    public List<FairyFruitJoint> joints;
    public List<FairyFruitSegment> segments;

    protected FairyFruitJoint root;
    protected FairyFruitJoint tip;
    protected final List<Interactable> interactables;

    public boolean canGrow = true;

    public FairyFruitBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ClinkerBlockEntities.FAIRY_FRUIT.get(), pPos, pBlockState);
        this.interactables = new ArrayList<>();
        this.joints = new ArrayList<>();
        this.segments = new ArrayList<>();

        // initializes the root
        this.root = new FairyFruitJoint(this, 0.25);
        this.root.isRoot = true;
        this.tip = this.root;
        for (int i = 0; i < 2; i++) {
            this.grow(false, false);
        }
    }

    public void serverTick() {
        this.updatePhysics(new Vec3(0, -10, 0), 1.0F / 20.0F, 16);
        this.root.position = this.getPosition();
        this.root.pPosition = this.getPosition();
        for (FairyFruitSegment segment : this.segments) {
            if (segment.interactable == null) {
                //FairyFruitInteractable interactable = new FairyFruitInteractable(this, segment, new OBBCollisionShape(0.25, 0.5, 0.25));
                //this.addChild(interactable);
                //InteractableManager.addServerInteractable(interactable, (ServerLevel) level);
            }

            //segment.interactable.setPosition(segment.getCenter(1.0F));
            //segment.interactable.setRotation(segment.getOrientation(1.0F, ORIENTATION_FORWARD));
        }

        ClinkerPacketHandler.sendToClientsTrackingChunk(level.getChunkAt(this.getBlockPos()), new ClientboundFairyFruitSyncPacket(this));
    }

    public void clientTick() {
        for (FairyFruitJoint joint : this.joints) {
            joint.clientTick();
        }
        //Clinker.LOGGER.info("end");
    }

    public void cleanup() {
        Iterator segmentIterator = segments.iterator();
        while (segmentIterator.hasNext()) {
            FairyFruitSegment segment = (FairyFruitSegment) segmentIterator.next();
            if (segment.shouldBeRemoved) {
                if (this.hasLevel() && !this.level.isClientSide) ClinkerPacketHandler.sendToClientsTrackingChunk(this.level.getChunkAt(this.getBlockPos()), new ClientboundFairyFruitRemovalPacket(segment));
                //if (segment.interactable != null) segment.interactable.markForRemoval();
                segmentIterator.remove();
            }
        }

        Iterator jointIterator = joints.iterator();
        while (jointIterator.hasNext()) {
            FairyFruitJoint joint = (FairyFruitJoint) jointIterator.next();
            if (joint.shouldBeRemoved) {
                if (this.hasLevel() && !this.level.isClientSide) ClinkerPacketHandler.sendToClientsTrackingChunk(this.level.getChunkAt(this.getBlockPos()), new ClientboundFairyFruitRemovalPacket(joint));
                jointIterator.remove();
            }
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FairyFruitBlockEntity entity) {
        entity.ticksExisted++;
        entity.cleanup();
        entity.tickParticles();
        if (level.isClientSide()) {
            entity.clientTick();
        } else {
            entity.serverTick();
        }

    }

    public void collideJoints(FairyFruitJoint particle1, FairyFruitJoint particle2) {
        if (particle1 == particle2 || !particle1.shouldCollide || !particle2.shouldCollide) return;
        double minimumDistance = 1;
        double distanceSqr = particle1.physicsNextPosition.distanceToSqr(particle2.physicsNextPosition);
        if (distanceSqr < minimumDistance * minimumDistance) {
            double correctionDistance = minimumDistance - Math.sqrt(distanceSqr);
            //point of collision will always be directly in the middle, in the case of spheres.
            Vec3 collisionVector = particle1.physicsNextPosition.subtract(particle2.physicsNextPosition).normalize().scale(correctionDistance * 0.5);

            particle1.physicsNextPosition = particle1.physicsNextPosition.add(collisionVector);
            particle2.physicsNextPosition = particle2.physicsNextPosition.subtract(collisionVector);
        }
    }

    public void useBoneMeal() {
        if (this.level == null) return;
        Clinker.LOGGER.info(canGrow);
        if (canGrow) {
            int growAmount = this.level.random.nextInt(1, 3);
            for (int i = 0; i < growAmount; i++) {
                this.grow(true, true);
            }
        }
    }

    public void grow(boolean sendPacket, boolean fromBoneMeal) {
        FairyFruitJoint newTip = new FairyFruitJoint(this, 0.25);
        Vec3 position = this.tip.position.add(0, -1, 0);
        newTip.position = position;
        newTip.physicsPrevPosition = position.add(0.001, 0, 0);
        newTip.pPosition = position;
        if (this.hasLevel() && this.level.isClientSide()) newTip.clientPos = position;

        FairyFruitSegment newSegment = new FairyFruitSegment(this, this.tip, newTip, 0);
        this.setTip(newTip);

        if (fromBoneMeal) newSegment.addBoneMealEffects();
        if (this.hasLevel()) this.canGrow = this.level.random.nextInt(this.segments.size()) < 4;
        if (this.hasLevel() && !this.level.isClientSide() && sendPacket) ClinkerPacketHandler.sendToClientsTrackingChunk(this.level.getChunkAt(this.worldPosition), new ClientboundFairyFruitGrowPacket(this, fromBoneMeal));
    }

    public void setTip(FairyFruitJoint newTip) {
        if (this.tip != null) this.tip.isTip = false;
        this.tip = newTip;
        this.tip.isTip = true;
    }

    public void breakAt(@Nullable Entity breaker, int index) {
        if (breaker instanceof Player p) if (!p.getAbilities().instabuild && this.hasLevel()) {
            if (!this.level.isClientSide() && this.tip.getFruitStage() == FairyFruitJoint.FruitStage.FRUIT) {
                ItemEntity item = new ItemEntity(this.level, this.tip.position.x(), this.tip.position.y(), this.tip.position.z(), new ItemStack(ClinkerItems.FAIRY_FRUIT.get(), 1));
                this.level.addFreshEntity(item);
            }
        }
        this.tip.fruitGrowth = 0;

        if (this.segments.size() == index + 1) return;
        if (this.segments.get(index + 1) == null || this.segments.get(index + 1).shouldBeRemoved) return;
        this.canGrow = true;

        this.setTip(this.segments.get(index).getBottomJoint());
        for (int i = index + 1; i < segments.size(); i++) {
            FairyFruitSegment segment = this.segments.get(i);
            segment.shouldBeRemoved = true;
            segment.getBottomJoint().shouldBeRemoved = true;
            segment.addDestroyEffects();
        }
    }

    public void syncFromNBT(CompoundTag nbt) {
        this.canGrow = nbt.getBoolean("canGrow");

        CompoundTag joints = nbt.getCompound("joints");
        for (int i = 0; i < joints.size(); i++) {
            CompoundTag jointTag = joints.getCompound("joint" + i);
            this.joints.get(i).deserialize(jointTag);
        }

        CompoundTag segments = nbt.getCompound("segments");
        for (int i = 0; i < segments.size(); i++) {
            CompoundTag segmentTag = segments.getCompound("segment" + i);
            this.segments.get(i).deserialize(segmentTag);
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        CompoundTag joints = new CompoundTag();

        for (int i = 0; i < this.joints.size(); i++) {
            joints.put("joint" + i, this.joints.get(i).serialize());
        }
        CompoundTag segments = new CompoundTag();
        for (int i = 0; i < this.segments.size(); i++) {
            segments.put("segment" + i, this.segments.get(i).serialize());
        }
        tag.put("joints", joints);
        tag.put("segments", segments);

        tag.putBoolean("canGrow", this.canGrow);

        return tag;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);

        CompoundTag joints = new CompoundTag();
        for (int i = 0; i < this.joints.size(); i++) {
            joints.put("joint" + i, this.joints.get(i).serialize());
        }
        CompoundTag segments = new CompoundTag();
        for (int i = 0; i < this.segments.size(); i++) {
            segments.put("segment" + i, this.segments.get(i).serialize());
        }
        pTag.put("joints", joints);
        pTag.put("segments", segments);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);

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

//    @Override
//    public List<Interactable> getChildInteractables() {
//        return interactables;
//    }
    @Nullable
    @Override
    public Vec3 getPosition() {
        return new Vec3(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1.0, this.worldPosition.getZ() + 0.5);
    }

    @Override
    public List<FairyFruitJoint> getParticles() {
        return joints;
    }
    @Override
    public List<FairyFruitSegment> getConstraints() {
        return segments;
    }



    /*public void serverTick() {
        this.updatePhysics();

        for (FairyFruitSegment segment : this.segments) {
            if (segment.interactable == null) {
                FairyFruitInteractable interactable = new FairyFruitInteractable(this, segment, new OBBCollisionShape(0.25, 0.5, 0.25), true);
                this.addChild(interactable);
                InteractableManager.addServerInteractable(interactable, (ServerLevel) level);
            }

            segment.interactable.setPosition(segment.getCenter(1.0F));
            segment.interactable.setRotation(segment.getOrientation(1.0F));

        }

        ClinkerPacketHandler.sendToClientsTrackingChunk(level.getChunkAt(this.getBlockPos()), new ClientboundFairyFruitSyncPacket(this));
    }

    public void clientTick() {
        this.updatePhysics();
        for (FairyFruitJoint joint : this.joints) {
            //Clinker.LOGGER.info(joint.id);
        }
        //Clinker.LOGGER.info("end");
    }


    public static void tick(Level level, BlockPos pos, BlockState state, FairyFruitBlockEntity entity) {
        entity.ticksExisted++;

        if (level.isClientSide()) {
            entity.clientTick();
        } else {
            entity.serverTick();
        }
    }

    protected static final Vec3 GRAVITY = new Vec3(0, -8, 0);
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

    public void grow(boolean sendPacket) {
        FairyFruitJoint newTip = new FairyFruitJoint(this, 0.25);
        newTip.position = this.tip.position.add(0, -1, 0);
        FairyFruitSegment newSegment = new FairyFruitSegment(this, this.tip, newTip, 1);
        this.setTip(newTip);

        if (this.hasLevel() && !this.level.isClientSide() && sendPacket) ClinkerPacketHandler.sendToClientsTrackingChunk(this.level.getChunkAt(this.worldPosition), new ClientboundFairyFruitGrowPacket(this, newSegment.index, newTip.index));
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

    public void breakAt(int index) {
        for (int i = this.segments.size(); i < index; i--) {
            this.segments.remove(i);
            this.segments.get(i).addDestroyEffects();
            this.segments.get(i).interactable.markForRemoval();
            this.interactables.remove(i);
            this.joints.remove(this.segments.get(i).bottomJoint.index);
        }
    }*/
}
