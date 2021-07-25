package birsy.clinker.common.block;

import birsy.clinker.common.tileentity.HeatedIronCauldronTileEntity;
import birsy.clinker.core.registry.ClinkerItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;

public class HeatedIronCauldronBlock extends ContainerBlock implements ILiquidContainer {
    private static final VoxelShape INSIDE = makeCuboidShape(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    protected static final VoxelShape SHAPE = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), VoxelShapes.or(makeCuboidShape(0.0D, 0.0D, 4.0D, 16.0D, 3.0D, 12.0D), makeCuboidShape(4.0D, 0.0D, 0.0D, 12.0D, 3.0D, 16.0D), makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D), INSIDE), IBooleanFunction.ONLY_FIRST);

    public HeatedIronCauldronBlock() {
        super(AbstractBlock.Properties.create(Material.IRON, MaterialColor.STONE).setRequiresTool().hardnessAndResistance(2.0F).notSolid());
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;

    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        boolean isItem = entityIn instanceof ItemEntity;
        int i = isItem ? 1 : 3;
        float f = (float)pos.getY() + (6.0F + (float)(3 * i)) / 16.0F;

        if (!worldIn.isRemote && entityIn.getPosY() <= (double) f) {
            HeatedIronCauldronTileEntity tileEntity = (HeatedIronCauldronTileEntity) worldIn.getTileEntity(pos);

            if (!entityIn.isImmuneToFire() && entityIn instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entityIn) && tileEntity.heat > 50) {
                entityIn.attackEntityFrom(DamageSource.HOT_FLOOR, 1.0F);
            } else if (isItem) {
                tileEntity.addItem((ItemEntity) entityIn);
            }
        }
        super.onEntityCollision(state, worldIn, pos, entityIn);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (player.getHeldItem(handIn).getItem() != ClinkerItems.LADLE.get()) {
            if (worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof HeatedIronCauldronTileEntity) {
                HeatedIronCauldronTileEntity tileEntity = (HeatedIronCauldronTileEntity) worldIn.getTileEntity(pos);
                tileEntity.stir();
            }
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        HeatedIronCauldronTileEntity tileEntity = (HeatedIronCauldronTileEntity) worldIn.getTileEntity(pos);
        tileEntity.dropItems(-1, player);

        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new HeatedIronCauldronTileEntity();
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new HeatedIronCauldronTileEntity();
    }

    @Override
    public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
        return true;
    }

    @Override
    public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
        if (worldIn.getTileEntity(pos) instanceof HeatedIronCauldronTileEntity) {
            HeatedIronCauldronTileEntity tileEntity = (HeatedIronCauldronTileEntity) worldIn.getTileEntity(pos);
            if (fluidStateIn.getFluid() == tileEntity.tank.getFluid().getFluid()) {
                tileEntity.tank.fill(new FluidStack(fluidStateIn.getFluid(), fluidStateIn.getLevel() * 125), IFluidHandler.FluidAction.EXECUTE);
                return true;
            }
        }
        return false;
    }
}
