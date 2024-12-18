package birsy.clinker.common.world.block.blockentity;

import birsy.clinker.client.sound.StoveSoundInstance;
import birsy.clinker.common.world.block.AbstractStoveBlock;
import birsy.clinker.core.registry.entity.ClinkerBlockEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.neoforged.api.distmarker.Dist;


public class StoveBlockEntity extends BlockEntity {
    public ChestType type;
    public Direction facingDirection;
    private float pHeat;
    private float heat;

    
    private boolean hasSound = false;

    public StoveBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ClinkerBlockEntities.STOVE.get(), pWorldPosition, pBlockState);
        this.type = pBlockState.getValue(AbstractStoveBlock.TYPE);
        this.facingDirection = pBlockState.getValue(AbstractStoveBlock.FACING);
    }

    public void updateState(BlockState pBlockState) {
        this.type = pBlockState.getValue(AbstractStoveBlock.TYPE);
        this.facingDirection = pBlockState.getValue(AbstractStoveBlock.FACING);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, StoveBlockEntity entity) {
        if (level.isClientSide()) {
            entity.clientTick();
        }
    }

    
    public void clientTick() {
        if (!this.hasSound) {
            StoveSoundInstance sound = new StoveSoundInstance(this);
            Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
            this.hasSound = true;
        }
    }
}
