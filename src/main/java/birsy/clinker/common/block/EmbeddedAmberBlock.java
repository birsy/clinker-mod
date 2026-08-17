package birsy.clinker.common.block;

import birsy.clinker.common.block.blockentity.EmbeddedAmberBlockEntity;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class EmbeddedAmberBlock extends AmberBlock implements EntityBlock {
    public EmbeddedAmberBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.getAbilities().instabuild) {
            if (level instanceof ServerLevel serverLevel) {
                if (level.getBlockEntity(pos) instanceof EmbeddedAmberBlockEntity blockEntity) {
                    if (stack.getItem() instanceof SpawnEggItem egg) {
                        stack.shrink(1);
                        blockEntity.setEmbeddedEntity(egg.getType(stack).create(serverLevel));
                    } else {
                        blockEntity.setEmbeddedItem(stack.copyAndClear());
                    }
                }
            }
            return ItemInteractionResult.CONSUME;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        // make sure that it's actually a new block!
        if (!newState.is(this) || movedByPiston) {
            if (level instanceof ServerLevel serverLevel && serverLevel.getBlockEntity(pos) instanceof EmbeddedAmberBlockEntity blockEntity) {
                blockEntity.dropEmbeddedObjects(serverLevel);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EmbeddedAmberBlockEntity(pos, state);
    }

    @Override
    public Item asItem() {
        return ClinkerBlocks.AMBER_BLOCK.asItem();
    }
}
