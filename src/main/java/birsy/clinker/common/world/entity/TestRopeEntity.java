package birsy.clinker.common.world.entity;

import birsy.clinker.common.world.entity.rope.RopeEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class TestRopeEntity extends RopeEntity<RopeEntity.RopeEntitySegment> {
    public TestRopeEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        if (!pLevel.isClientSide) {
            for (int i = 0; i < 18; i++) {
                this.addSegmentToEnd(1, false);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        float z = 0;
        for (RopeEntitySegment segment : this.segments) {
            segment.setInitialPosition(this.getX(), this.getY(), this.getZ() + z);
            z += segment.length;
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    protected RopeEntitySegment createSegment(int segmentType) {
        if (segmentType == 0) return new RopeEntitySegment(this, segmentType, 1, 1);
        return new RopeEntitySegment(this, segmentType, 0.5F, 1.0F);
    }
}
