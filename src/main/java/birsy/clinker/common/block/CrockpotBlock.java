package birsy.clinker.common.block;

import birsy.clinker.common.tileentity.CrockpotTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class CrockpotBlock extends ContainerBlock {
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 11.0D, 16.0D);
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    public CrockpotBlock() {
        super(AbstractBlock.Properties.create(Material.IRON, MaterialColor.LIGHT_GRAY)
                .hardnessAndResistance(2.75F, 75.0F)
                .sound(SoundType.NETHERITE)
                .harvestLevel(1)
                .notSolid()
                .harvestTool(ToolType.PICKAXE));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof CrockpotTileEntity) {
            CrockpotTileEntity tileEntity = (CrockpotTileEntity) worldIn.getTileEntity(pos);
            boolean flag = tileEntity.switchOpenState();
            return flag ? ActionResultType.SUCCESS : ActionResultType.FAIL;
        }

        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        CrockpotTileEntity tileEntity = new CrockpotTileEntity();
        return tileEntity;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        CrockpotTileEntity tileEntity = new CrockpotTileEntity();
        return tileEntity;
    }
}
