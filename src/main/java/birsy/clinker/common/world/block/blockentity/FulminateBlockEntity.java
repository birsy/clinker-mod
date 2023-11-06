package birsy.clinker.common.world.block.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;

//TODO: make fulminate listen to lightning
public class FulminateBlockEntity extends BlockEntity {
    public FulminateBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
    }

    public void activate() {

    }



    public class LightningListener implements GameEventListener {
        protected final PositionSource listenerSource;
        protected final int listenerRange;

        public LightningListener(PositionSource positionSource, int range) {
            this.listenerSource = positionSource;
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
        public boolean handleGameEvent(ServerLevel pLevel, GameEvent pGameEvent, GameEvent.Context pContext, Vec3 pPos) {
            if (pGameEvent == GameEvent.LIGHTNING_STRIKE) {
                BlockEntity fuliminate = pLevel.getBlockEntity(BlockPos.containing(this.listenerSource.getPosition(pLevel).get()));
                if (fuliminate instanceof FulminateBlockEntity) {
                    ((FulminateBlockEntity) fuliminate).activate();
                    return true;
                }
            }

            return false;
        }
    }
}
