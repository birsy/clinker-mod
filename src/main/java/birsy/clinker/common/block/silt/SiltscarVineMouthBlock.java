package birsy.clinker.common.block.silt;

import birsy.clinker.common.tileentity.SiltscarTileEntity;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.JukeboxTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class SiltscarVineMouthBlock extends AbstractTopPlantBlock implements ITileEntityProvider {
    public static final VoxelShape SHAPE = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

    public static final BooleanProperty MOUTH_CLOSED = BooleanProperty.create("mouth_closed");
    public static final BooleanProperty HOLDING_ITEM = BooleanProperty.create("holding_item");

    public SiltscarVineMouthBlock(Properties properties) {
        super(properties, Direction.UP, SHAPE, true, 0.1D);
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HOLDING_ITEM).add(MOUTH_CLOSED);
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        CompoundNBT compoundnbt = stack.getOrCreateTag();
        if (compoundnbt.contains("BlockEntityTag")) {
            CompoundNBT compoundnbt1 = compoundnbt.getCompound("BlockEntityTag");
            if (compoundnbt1.contains("HeldItem")) {
                worldIn.setBlockState(pos, state.with(HOLDING_ITEM, Boolean.valueOf(true)), 2);
            }
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        super.onEntityCollision(state, worldIn, pos, entityIn);

        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof SiltscarTileEntity && !isHoldingOn(state, worldIn, pos)) {
            chomp(state, worldIn, pos, entityIn);
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (isHoldingOn(state, worldIn, pos)) {
            pullOpen(state, worldIn, pos);
        }

        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    public void chomp(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof SiltscarTileEntity) {
            if (((SiltscarTileEntity) tileentity).attackDelay <= 0) {
                if (entityIn instanceof ItemEntity && !isHoldingOn(state, worldIn, pos)) {
                    ((SiltscarTileEntity) tileentity).setHeldItem(((ItemEntity) entityIn).getItem());
                    worldIn.setBlockState(pos, state.with(MOUTH_CLOSED, true).with(HOLDING_ITEM, true));
                    entityIn.remove();
                } else if (!isHoldingOn(state, worldIn, pos)) {
                    worldIn.setBlockState(pos, state.with(MOUTH_CLOSED, true));
                    if (entityIn != ((SiltscarTileEntity) tileentity).getHeldEntity()) {
                        ((SiltscarTileEntity) tileentity).setHeldEntity(entityIn);
                        entityIn.attackEntityFrom(new DamageSource("chomped").setDifficultyScaled(), 1.0f);
                    }
                    entityIn.setMotion(entityIn.getMotion().mul(0.125, 0.125, 0.125));
                }
            }
        }
    }

    public void pullOpen(BlockState state, World worldIn, BlockPos pos) {
        if (isHoldingOn(state, worldIn, pos)) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof SiltscarTileEntity) {
                ItemStack itemstack = ((SiltscarTileEntity) tileentity).getHeldItem();

                ((SiltscarTileEntity) tileentity).open(true, true);
                worldIn.setBlockState(pos, state.with(MOUTH_CLOSED, false).with(HOLDING_ITEM, false));

                double d0 = (double)(worldIn.rand.nextFloat() * 0.7F) + (double)0.15F;
                double d1 = (double)(worldIn.rand.nextFloat() * 0.7F) + (double)0.060000002F + 0.6D;
                double d2 = (double)(worldIn.rand.nextFloat() * 0.7F) + (double)0.15F;
                ItemStack itemstack1 = itemstack.copy();
                ItemEntity itementity = new ItemEntity(worldIn, (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, itemstack1);
                itementity.setDefaultPickupDelay();
                worldIn.addEntity(itementity);
            }
        }
    }

    private boolean isHoldingOn(BlockState state, World worldIn, BlockPos pos) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        boolean flag = false;
        if (tileentity instanceof SiltscarTileEntity) {
            flag = ((SiltscarTileEntity) tileentity).getHeldEntity() == null && ((SiltscarTileEntity) tileentity).getHeldItem() == ItemStack.EMPTY;
        }

        return state.get(MOUTH_CLOSED) || state.get(HOLDING_ITEM) || flag;
    }

    @Override
    protected int getGrowthAmount(Random rand) {
        return PlantBlockHelper.getGrowthAmount(rand);
    }

    @Override
    protected boolean canGrowIn(BlockState state) {
        return PlantBlockHelper.isAir(state);
    }

    @Override
    protected Block getBodyPlantBlock() {
        //return ClinkerBlocks.SILTSCAR_VINE.get();
        return null;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new SiltscarTileEntity();
    }
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}
