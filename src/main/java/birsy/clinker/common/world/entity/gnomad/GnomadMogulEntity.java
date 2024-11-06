package birsy.clinker.common.world.entity.gnomad;

import birsy.clinker.client.model.base.InterpolatedSkeletonParent;
import birsy.clinker.common.world.entity.GroundLocomoteEntity;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.SoulSandBlock;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class GnomadMogulEntity extends GnomadEntity implements InterpolatedSkeletonParent {
    private static final int[] ROBE_COLORS = new int[]{0x4d423c, 0x513337, 0x4a4751, 0x505049, 0x4f4c4b};
    private static final EntityDataAccessor<Integer> DATA_ROBE_COLOR = SynchedEntityData.defineId(GnomadMogulEntity.class, EntityDataSerializers.INT);

    public GnomadMogulEntity(EntityType<? extends GnomadEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setMaxUpStep(1.0F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ROBE_COLOR, 0x4d423c);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("RobeColor", this.getRobeColor());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("RobeColor")) {
            this.setRobeColor(pCompound.getInt("RobeColor"));
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            this.computeHeightOffset();
        }
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        // we're a little silly. choose a random robe
        this.setRobeColor(ROBE_COLORS[this.random.nextInt(ROBE_COLORS.length)]);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    public int getRobeColor() {
        return this.entityData.get(DATA_ROBE_COLOR);
    }

    public void setRobeColor(double r, double g, double b) {
        int ir = (int) (r * 255), ig = (int) (g * 255), ib = (int) (b * 255);
        this.setRobeColor(ir<<16 + ig<<8 + ib);
    }

    public void setRobeColor(int robeColor) {
        this.entityData.set(DATA_ROBE_COLOR, robeColor);
    }

    @OnlyIn(Dist.CLIENT)
    protected double prevSmoothedHeight = 0;
    @OnlyIn(Dist.CLIENT)
    protected double smoothedHeight = 0;
    @OnlyIn(Dist.CLIENT)
    protected void computeHeightOffset() {
        this.prevSmoothedHeight = this.smoothedHeight;

        Vec3 samplePosition = this.getPosition(1.0F).add(0, 0.1F, 0);
        Vec3 to = samplePosition.add(0, -this.getStepHeight() - 0.1F, 0);
        HitResult result = this.level().clip(new ClipContext(samplePosition, to, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, this));
        double height = (result.getLocation().y + this.getY()) / 2.0F;

        this.smoothedHeight = Mth.lerp(0.3F, this.smoothedHeight, height);
        if (this.smoothedHeight > this.getY()) this.smoothedHeight = this.getY();
        this.smoothedHeight = MathUtils.clampDifference(this.smoothedHeight, this.getY(), this.getStepHeight());
    }
    @OnlyIn(Dist.CLIENT)
    public float getHeightOffset(float partialTick) {
        return (float) (Mth.lerp(partialTick, prevSmoothedHeight, smoothedHeight) - this.getPosition(partialTick).y);
    }
}
