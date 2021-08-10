package birsy.clinker.common.block.silt;

import birsy.clinker.common.tileentity.SiltscarTileEntity;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.block.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.JukeboxTileEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.NetherVines;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class SiltscarVineMouthBlock extends GrowingPlantHeadBlock implements EntityBlock {
    public static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

    public static final BooleanProperty MOUTH_CLOSED = BooleanProperty.create("mouth_closed");
    public static final BooleanProperty HOLDING_ITEM = BooleanProperty.create("holding_item");

    public SiltscarVineMouthBlock(Properties properties) {
        super(properties, Direction.UP, SHAPE, true, 0.1D);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HOLDING_ITEM).add(MOUTH_CLOSED);
    }

    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        CompoundTag compoundnbt = stack.getOrCreateTag();
        if (compoundnbt.contains("BlockEntityTag")) {
            CompoundTag compoundnbt1 = compoundnbt.getCompound("BlockEntityTag");
            if (compoundnbt1.contains("HeldItem")) {
                worldIn.setBlock(pos, state.setValue(HOLDING_ITEM, Boolean.valueOf(true)), 2);
            }
        }
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        super.entityInside(state, worldIn, pos, entityIn);

        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof SiltscarTileEntity && !isHoldingOn(state, worldIn, pos)) {
            chomp(state, worldIn, pos, entityIn);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (isHoldingOn(state, worldIn, pos)) {
            pullOpen(state, worldIn, pos);
        }

        return super.use(state, worldIn, pos, player, handIn, hit);
    }

    public void chomp(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof SiltscarTileEntity) {
            if (((SiltscarTileEntity) tileentity).attackDelay <= 0) {
                if (entityIn instanceof ItemEntity && !isHoldingOn(state, worldIn, pos)) {
                    ((SiltscarTileEntity) tileentity).setHeldItem(((ItemEntity) entityIn).getItem());
                    worldIn.setBlockAndUpdate(pos, state.setValue(MOUTH_CLOSED, true).setValue(HOLDING_ITEM, true));
                    entityIn.remove();
                } else if (!isHoldingOn(state, worldIn, pos)) {
                    worldIn.setBlockAndUpdate(pos, state.setValue(MOUTH_CLOSED, true));
                    if (entityIn != ((SiltscarTileEntity) tileentity).getHeldEntity()) {
                        ((SiltscarTileEntity) tileentity).setHeldEntity(entityIn);
                        entityIn.hurt(new DamageSource("chomped").setScalesWithDifficulty(), 1.0f);
                    }
                    entityIn.setDeltaMovement(entityIn.getDeltaMovement().multiply(0.125, 0.125, 0.125));
                }
            }
        }
    }

    public void pullOpen(BlockState state, Level worldIn, BlockPos pos) {
        if (isHoldingOn(state, worldIn, pos)) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof SiltscarTileEntity) {
                ItemStack itemstack = ((SiltscarTileEntity) tileentity).getHeldItem();

                ((SiltscarTileEntity) tileentity).open(true, true);
                worldIn.setBlockAndUpdate(pos, state.setValue(MOUTH_CLOSED, false).setValue(HOLDING_ITEM, false));

                double d0 = (double)(worldIn.random.nextFloat() * 0.7F) + (double)0.15F;
                double d1 = (double)(worldIn.random.nextFloat() * 0.7F) + (double)0.060000002F + 0.6D;
                double d2 = (double)(worldIn.random.nextFloat() * 0.7F) + (double)0.15F;
                ItemStack itemstack1 = itemstack.copy();
                ItemEntity itementity = new ItemEntity(worldIn, (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, itemstack1);
                itementity.setDefaultPickUpDelay();
                worldIn.addFreshEntity(itementity);
            }
        }
    }

    private boolean isHoldingOn(BlockState state, Level worldIn, BlockPos pos) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        boolean flag = false;
        if (tileentity instanceof SiltscarTileEntity) {
            flag = ((SiltscarTileEntity) tileentity).getHeldEntity() == null && ((SiltscarTileEntity) tileentity).getHeldItem() == ItemStack.EMPTY;
        }

        return state.getValue(MOUTH_CLOSED) || state.getValue(HOLDING_ITEM) || flag;
    }

    @Override
    protected int getBlocksToGrowWhenBonemealed(Random rand) {
        return NetherVines.getBlocksToGrowWhenBonemealed(rand);
    }

    @Override
    protected boolean canGrowInto(BlockState state) {
        return NetherVines.isValidGrowthState(state);
    }

    @Override
    protected Block getBodyBlock() {
        //return ClinkerBlocks.SILTSCAR_VINE.get();
        return null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockGetter worldIn) {
        return new SiltscarTileEntity();
    }
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
}
