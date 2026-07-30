package birsy.clinker.common.world.block;

import birsy.clinker.common.world.block.blockentity.MortarBlockEntity;
import birsy.clinker.common.world.item.PestleItem;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.entity.ClinkerBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MortarBlock extends BaseEntityBlock {
    private static final VoxelShape OUTSIDE = box(3.0, 0.0, 3.0, 13.0, 5.0, 13.0),
                                    INSIDE = box(4.0, 1.0, 4.0, 12.0, 5.0, 12.0);
    public static final VoxelShape SHAPE = Shapes.join(OUTSIDE, INSIDE, BooleanOp.ONLY_FIRST);
    public static final MapCodec<MortarBlock> CODEC = simpleCodec(MortarBlock::new);

    public MortarBlock(Properties properties) {
        super(properties);
    }

    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return SHAPE; }
    @Override protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) { return INSIDE; }

    @Override
    public MapCodec<MortarBlock> codec() {
        return CODEC;
    }

    // block entity stuff
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof MortarBlockEntity mortarBlockEntity) {
            if (stack.getItem() instanceof PestleItem) {
                mortarBlockEntity.grind();
                return ItemInteractionResult.CONSUME_PARTIAL;
            }

            if (!stack.isEmpty() && mortarBlockEntity.addItem(player, stack)) {
                level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.PLAYERS, 0.2F, Mth.lerp(player.getRandom().nextFloat(), 1.3F, 1.6F));
                level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BASALT_HIT, SoundSource.PLAYERS, 0.2F, Mth.lerp(player.getRandom().nextFloat(), 1.5F, 1.6F));
                return ItemInteractionResult.SUCCESS;
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof MortarBlockEntity mortarBlockEntity) {
            ItemStack stack = mortarBlockEntity.removeItemStack();
            if (stack.isEmpty())
                return InteractionResult.PASS;
            if (!player.addItem(stack) && !level.isClientSide())
                spawnItem(stack, pos, level);

            level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, Mth.lerp(player.getRandom().nextFloat(), 1.3F, 1.6F));
            level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BASALT_HIT, SoundSource.PLAYERS, 0.2F, Mth.lerp(player.getRandom().nextFloat(), 2.0F, 2.2F));
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (level.getBlockEntity(pos) instanceof MortarBlockEntity mortarBlockEntity) {
            for (ItemStack ingredient : mortarBlockEntity.ingredients) {
                while (!ingredient.isEmpty())
                    spawnItem(ingredient.split(level.random.nextInt(21) + 10), pos, level);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    private static void spawnItem(ItemStack stack, Vec3i pos, Level level) {
        Vec3 itemPos = Vec3.atBottomCenterOf(pos).add(0, 3.0 / 16.0, 0);
        ItemEntity itemEntity = new ItemEntity(level, itemPos.x, itemPos.y, itemPos.z, stack);
        itemEntity.setDeltaMovement(
                level.random.triangle(0.0F, 0.1F),
                level.random.triangle(0.2F, 0.1F),
                level.random.triangle(0.0F, 0.1F)
        );
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);
    }

    @Override
    protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        super.triggerEvent(state, level, pos, id, param);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(id, param);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MortarBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ?
                createTickerHelper(blockEntityType, ClinkerBlockEntities.MORTAR.get(), MortarBlockEntity::tickClient) :
                createTickerHelper(blockEntityType, ClinkerBlockEntities.MORTAR.get(), MortarBlockEntity::tickServer);
    }
}
