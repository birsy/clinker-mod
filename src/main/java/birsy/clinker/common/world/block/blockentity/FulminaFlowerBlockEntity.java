package birsy.clinker.common.world.block.blockentity;

import birsy.clinker.common.world.block.plant.FulminaFlowerBlock;
import birsy.clinker.core.registry.entity.ClinkerBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;

public class FulminaFlowerBlockEntity extends BlockEntity implements GameEventListener.Provider<FulminaFlowerBlockEntity.LightningListener> {
    LightningListener lightningListener;
    int ticksUntilExtinguish = 0;

    public FulminaFlowerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ClinkerBlockEntities.FULMINA_FLOWER.get(), pWorldPosition, pBlockState);
        this.lightningListener = new LightningListener(pWorldPosition, 32);
    }

    public void activate() {
        this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(FulminaFlowerBlock.LIT, true), 3);
        this.ticksUntilExtinguish = 180;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, FulminaFlowerBlockEntity entity) {
        if (entity.ticksUntilExtinguish > 0) entity.ticksUntilExtinguish--;
        if (entity.ticksUntilExtinguish <= 0) {
            level.setBlock(blockPos, blockState.setValue(FulminaFlowerBlock.LIT, false), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("TicksUntilExtinguish", this.ticksUntilExtinguish);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.ticksUntilExtinguish = tag.getInt("TicksUntilExtinguish");
    }

    @Override
    public LightningListener getListener() {
        return this.lightningListener;
    }

    public class LightningListener implements GameEventListener {
        protected final PositionSource listenerSource;
        protected final int listenerRange;

        public LightningListener(BlockPos pos, int range) {
            this.listenerSource = new BlockPositionSource(pos);
            this.listenerRange = range;
        }

        @Override
        public PositionSource getListenerSource() {
            return this.listenerSource;
        }

        @Override
        public int getListenerRadius() {
            return this.listenerRange;
        }

        @Override
        public boolean handleGameEvent(ServerLevel level, Holder<GameEvent> gameEvent, GameEvent.Context context, Vec3 pos) {
            if (gameEvent.is(GameEvent.LIGHTNING_STRIKE)) {
                BlockEntity flower = level.getBlockEntity(BlockPos.containing(this.listenerSource.getPosition(level).get()));
                if (flower instanceof FulminaFlowerBlockEntity) {
                    ((FulminaFlowerBlockEntity) flower).activate();
                    return true;
                }
            }
            return false;
        }
    }
}
