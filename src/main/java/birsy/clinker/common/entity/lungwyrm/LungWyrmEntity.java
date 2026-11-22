package birsy.clinker.common.entity.lungwyrm;

import birsy.clinker.client.render.world.cloud.UpperLayerCloudRenderer;
import birsy.clinker.common.entity.SegmentedEntity;
import birsy.clinker.common.entity.ai.ClinkerBrainUtils;
import birsy.clinker.common.entity.ai.SmoothFlyingNavigation;
import birsy.clinker.common.entity.ai.UnclampedLookAngleControl;
import birsy.clinker.common.entity.lungwyrm.behaviors.CircleNest;
import birsy.clinker.common.entity.lungwyrm.behaviors.FireChargeVolley;
import birsy.clinker.common.entity.lungwyrm.behaviors.FloatDirectlyTowardsWalkTarget;
import birsy.clinker.common.entity.lungwyrm.behaviors.SetRandomFloatTarget;
import birsy.clinker.common.entity.lungwyrm.sensors.LungWyrmTargetsSensor;
import birsy.clinker.common.networking.packet.ClientboundLungwyrmAirborneHitPacket;
import birsy.clinker.core.util.CompressionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.network.PacketDistributor;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

public class LungWyrmEntity extends SegmentedEntity<LungWyrmEntity, LungWyrmEntity.LungWyrmJoint> implements Enemy, SmartBrainOwner<LungWyrmEntity> {
    float grounding = 0.0F;

    // initialization

    public LungWyrmEntity(EntityType<? extends SegmentedEntity<LungWyrmEntity, LungWyrmJoint>> entityType, Level level) {
        super(entityType, level, 32);
        this.moveControl = new LungWyrmMoveControl(this);
        this.lookControl = new UnclampedLookAngleControl(this, 80F, 0.05F);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 200.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, -5.0)
                .add(Attributes.FOLLOW_RANGE, 230.0);
    }

    @Override
    protected LungWyrmJoint createJoint(int index, int length) {
        float scale = Mth.clampedMap((float) index / length, 0, 1, 1, 0.3F);
        return new LungWyrmJoint(this, this.parts[index], index, scale);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data) {
        data = super.finalizeSpawn(level, difficulty, reason, data);
        this.joints.clear();
        int count = level.getRandom().nextInt((int) (maxJointCount * 0.75), maxJointCount);
        Vector3d pos = new Vector3d(this.getX(), this.getY(), this.getZ()), offset = new Vector3d();
        for (int i = 0; i < count; i++) {
            LungWyrmJoint joint = createJoint(i, count);
            float length = joint.length;
            float fac = Mth.clampedMap((float) i / count, 0.0F, 0.1F, 0.0F, 1.0F);

            joint.x = pos.x();
            joint.y = pos.y();
            joint.z = pos.z();
            this.addJoint(joint);

            offset.set(0, 0, -0.5)
                  .add(Math.sin(i * 0.5) * fac, Math.cos(i * 0.5) * fac, 0)
                  .normalize(length);
            pos.add(offset);
        }

        if (!BrainUtils.hasMemory(this, MemoryModuleType.HOME))
            BrainUtils.setMemory(this, MemoryModuleType.HOME,
                    new GlobalPos(level.getLevel().dimension(), BlockPos.containing(this.getEyePosition()))
            );

        return data;
    }

    // serialization

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        CompoundTag memories = new CompoundTag();
        ClinkerBrainUtils.serializeMemory(this, memories, MemoryModuleType.HOME);
        tag.put("Memories", memories);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        CompoundTag memories = tag.getCompound("Memories");
        ClinkerBrainUtils.deserializeMemory(this, memories, MemoryModuleType.HOME);
    }

    // ai

    @Override
    public LungWyrmMoveControl getMoveControl() {
        return (LungWyrmMoveControl) super.getMoveControl();
    }
    @Override
    public UnclampedLookAngleControl getLookControl() {
        return (UnclampedLookAngleControl) super.getLookControl();
    }
    @Override
    protected PathNavigation createNavigation(Level level) {
        return new SmoothFlyingNavigation(this, level);
    }

    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    public List<? extends ExtendedSensor<? extends LungWyrmEntity>> getSensors() {
        return List.of(
                new LungWyrmTargetsSensor()
        );
    }

    @Override
    public BrainActivityGroup<? extends LungWyrmEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),
                new FloatDirectlyTowardsWalkTarget()
        );
    }

    @Override
    public BrainActivityGroup<? extends LungWyrmEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<>(
                        new CircleNest(),
                        new SetRandomFloatTarget()
                ),
                new FireChargeVolley()
                        .cooldownForBetween(80, 200)
        );
    }

    @Override
    protected void customServerAiStep() {
        if (this.joints.isEmpty()) return;
        tickBrain(this);
        super.customServerAiStep();
    }

    private void updateDebugMovement() {
        Player player = EntityRetrievalUtil.getNearestPlayer(this, 200F);
        if (player == null) return;
        if (!player.getItemInHand(InteractionHand.MAIN_HAND).is(Items.CARROT_ON_A_STICK)) return;
        this.moveControl.setWantedPosition(player.getX(), player.getY(), player.getZ(), 0.2F);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return super.hurt(source, amount);
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ELDER_GUARDIAN_HURT;
    }
    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.ELDER_GUARDIAN_DEATH;
    }
    @Override
    public void playSound(SoundEvent sound, float volume, float pitch) {
        super.playSound(sound, volume * 5.0F, pitch);
    }

    public void sendAirborneHitEffectPacket(LungWyrmJoint joint, DamageSource arrowSource) {
        Vec3 hit = arrowSource.getSourcePosition();
        if (hit == null) return;

        Vector3d segmentDir = new Vector3d();
        Quaterniond segmentRot = new Quaterniond();
        joint.getSegmentTransform(null, segmentDir, segmentRot, 1.0F);

        Vector3d relative = new Vector3d(hit.x() - joint.x, hit.y() - (joint.y + joint.height * 0.5), hit.z() - joint.z);
        segmentRot.transformInverse(relative);

        double offset = Math.clamp(-relative.z() / joint.length, -1, 1);
        short compressedOffset = CompressionUtils.writeCompressed(offset, CompressionUtils.COMPRESSION_SCALE_MAX_1);
        double angle = Math.atan2(relative.y(), relative.x());
        short compressedAngle = CompressionUtils.writeCompressed(angle / Math.PI, CompressionUtils.COMPRESSION_SCALE_MAX_1);

        PacketDistributor.sendToPlayersTrackingEntity(this, new ClientboundLungwyrmAirborneHitPacket(this.getId(), (byte) joint.index, compressedOffset, compressedAngle));
    }

    public void receiveAirborneHitEffectPacket(int jointId, short compressedOffset, short compressedAngle) {
        if (jointId < 0 || jointId >= this.joints.size() || this.joints.get(jointId) == null) return;
        LungWyrmJoint joint = this.joints.get(jointId);
        float offset = (float) CompressionUtils.readCompressed(compressedOffset, CompressionUtils.COMPRESSION_SCALE_MAX_1) * joint.length;
        float angle = (float) CompressionUtils.readCompressed(compressedAngle, CompressionUtils.COMPRESSION_SCALE_MAX_1) * Mth.PI;
        joint.hitVectors.add(new Vector3f(Mth.cos(angle), Mth.sin(angle), offset));
    }

    public static class LungWyrmJoint extends Joint<LungWyrmEntity, LungWyrmEntity.LungWyrmJoint> {
        public boolean disabled, inflated;
        public double groundY = Double.NEGATIVE_INFINITY, ceilingY = Double.POSITIVE_INFINITY;
        float inflationAmount, pInflationAmount;

        final Vector3d segDir = new Vector3d(), hitPos = new Vector3d(), hitDir = new Vector3d();
        final Quaterniond segRot = new Quaterniond();
        List<Vector3fc> hitVectors = new ArrayList<>();

        protected LungWyrmJoint(LungWyrmEntity parent, SegmentPartEntity part, int index, float scale) {
            super(parent, part, index, 4F * scale, 4F * scale, 4F);
        }

        @Override
        public void tick() {
            pInflationAmount = inflationAmount;
            super.tick();
            inflationAmount = Mth.lerp(0.1F, inflationAmount, disabled ? -0.25F : (inflated ? 0.25F : 0));

            // periodically refresh ground height!
            if ((this.parent.tickCount + this.parent.getId() + this.index) % 5 == 0)
                refreshGroundHeight();

            if (this.parent.level().isClientSide()) {
                this.getSegmentTransform(null, segDir, segRot, 1.0F);
                for (Vector3fc hitVector : this.hitVectors) {
                    segDir.mul(hitVector.z(), hitPos).add(x, y + height * 0.5, z);
                    hitDir.set(hitVector.x(), hitVector.y(), 0);
                    segRot.transform(hitDir);
                    this.parent.level().addParticle(
                            ParticleTypes.POOF,
                            hitPos.x(), hitPos.y(), hitPos.z(),
                            hitDir.x(), hitDir.y(), hitDir.z()
                    );
                }
            }
        }

        @Override
        public boolean hurt(DamageSource source, float amount) {
            boolean actuallyHurt = super.hurt(source, amount);
            if (actuallyHurt) {
                this.disabled = true;
                this.parent.sendAirborneHitEffectPacket(this, source);
            }
            return actuallyHurt;
        }

        @Override
        public CompoundTag serialize(CompoundTag tag) {
            super.serialize(tag);
            tag.putBoolean("disabled", this.disabled);
            return tag;
        }

        @Override
        public void deserialize(CompoundTag tag) {
            super.deserialize(tag);
            this.disabled = tag.getBoolean("disabled");
        }

        @Override
        public void writeToBuffer(RegistryFriendlyByteBuf buf) {
            super.writeToBuffer(buf);
            buf.writeBoolean(inflated);
            buf.writeBoolean(disabled);
        }
        @Override
        public void readFromBuffer(RegistryFriendlyByteBuf buf) {
            super.readFromBuffer(buf);
            inflated = buf.readBoolean();
            disabled = buf.readBoolean();
        }

        void refreshGroundHeight() {
            this.groundY = parent.level().getHeight(Heightmap.Types.MOTION_BLOCKING, Mth.floor(x), Mth.floor(z));
            this.ceilingY = UpperLayerCloudRenderer.UPPER_CLOUD_HEIGHT;
            if (this.y < this.groundY) {
                Vec3 pos = new Vec3(this.x, this.y, this.z);
                this.groundY = parent.level().clip(
                        new ClipContext(
                                pos, pos.subtract(0, 48, 0),
                                ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY,
                                CollisionContext.of(this.parent)
                        )
                ).getLocation().y;
                this.ceilingY = parent.level().clip(
                        new ClipContext(
                                pos, pos.add(0, 48, 0),
                                ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY,
                                CollisionContext.of(this.parent)
                        )
                ).getLocation().y;
            }
        }

        public float getInflationAmount(float partialTick) {
            return Mth.lerp(partialTick, pInflationAmount, inflationAmount);
        }
    }
}
