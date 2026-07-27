package birsy.clinker.common.world.block;

import birsy.clinker.common.world.block.blockentity.CounterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class CounterBlock extends Block implements EntityBlock {
    public CounterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (hitResult.getDirection() != Direction.UP) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (level.getBlockEntity(pos) instanceof CounterBlockEntity counter) {
            Vec3 hitPos = hitResult.getLocation();
            int slotX = CounterBlockEntity.getSlot(hitPos.x), slotZ = CounterBlockEntity.getSlot(hitPos.z);
            if (counter.addItem(player, stack, slotX, slotZ)) {
                level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.PLAYERS, 0.2F, Mth.lerp(player.getRandom().nextFloat(), 1.3F, 1.6F));
                return ItemInteractionResult.SUCCESS;
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (hitResult.getDirection() != Direction.UP) return InteractionResult.PASS;
        if (level.getBlockEntity(pos) instanceof CounterBlockEntity counter) {
            Vec3 hitPos = hitResult.getLocation();
            int slotX = CounterBlockEntity.getSlot(hitPos.x), slotZ = CounterBlockEntity.getSlot(hitPos.z);

            ItemStack item = counter.removeItem(slotX, slotZ);
            if (item.isEmpty())
                return InteractionResult.PASS;
            if (!player.addItem(item) && !level.isClientSide()){
                counter.spawnItem(slotX, slotZ, item);
            }

            level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, Mth.lerp(player.getRandom().nextFloat(), 1.3F, 1.6F));
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (level.getBlockEntity(pos) instanceof CounterBlockEntity counter) {
            for (int x = 0; x < CounterBlockEntity.SLOT_SIDE_LENGTH; x++) {
                for (int z = 0; z < CounterBlockEntity.SLOT_SIDE_LENGTH; z++) {
                    int itemIndex = CounterBlockEntity.getIndex(x, z);
                    ItemStack item = counter.items.get(itemIndex);
                    while (!item.isEmpty()) counter.spawnItem(x, z, item.split(level.random.nextIntBetweenInclusive(10, 32) + 10));
                }
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.UP && level.getBlockEntity(pos) instanceof CounterBlockEntity counter)
            counter.updateSlotsFromObstruction();
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        super.triggerEvent(state, level, pos, id, param);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(id, param);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CounterBlockEntity(pos, state);
    }
}
