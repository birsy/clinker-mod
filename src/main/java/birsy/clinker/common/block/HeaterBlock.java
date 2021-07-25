package birsy.clinker.common.block;

import birsy.clinker.common.tileentity.HeaterTileEntity;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.Random;

public class HeaterBlock extends ContainerBlock {
    public static final IntegerProperty PROPERTY_HEAT = IntegerProperty.create("heat", 0, 15);

    public HeaterBlock() {
        super(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.BROWN_TERRACOTTA)
                .hardnessAndResistance(2.75F, 75.0F)
                .sound(SoundType.BASALT)
                .harvestLevel(1)
                .harvestTool(ToolType.PICKAXE)
                .setLightLevel((state) -> state.get(PROPERTY_HEAT)));
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof HeaterTileEntity) {
            HeaterTileEntity tileEntity = (HeaterTileEntity) worldIn.getTileEntity(pos);
            tileEntity.isCooking = !tileEntity.isCooking;
            return ActionResultType.SUCCESS;
        }

        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    /**
    //@OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (stateIn.get(PROPERTY_HEAT) >= 7) {
            double x = pos.getX() + 0.5D;
            double y = pos.getY();
            double z = pos.getZ() + 0.5D;

            if (rand.nextDouble() < 0.1D) {
                worldIn.playSound(x, y, z, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }

            for (Direction direction : Direction.Plane.HORIZONTAL) {
                for (int i = 0; i < 3; i++) {
                    Direction.Axis direction$axis = direction.getAxis();
                    double pi = Math.PI;

                    //double horizontalOffset = MathUtils.mapRange(0, 1, 5.5F / 16, 10.5F / 16, (float) ((MathHelper.sin((float) ((rand.nextFloat() * 2 * pi) + (1.5 * pi))) + 1) * 0.5));
                    double horizontalOffset = MathUtils.getRandomFloatBetween(rand, 5.5F, 10.5F) / 16;
                    double particleX = direction$axis == Direction.Axis.X ? direction.getXOffset() * 0.52D : horizontalOffset;
                    double particleY = MathUtils.bias(MathUtils.getRandomFloatBetween(rand, 1, 10) / 16.0F, 0.5F);
                    double particleZ = direction$axis == Direction.Axis.Z ? direction.getZOffset() * 0.52D : horizontalOffset;

                    worldIn.addParticle(ParticleTypes.SMOKE, x + particleX, y + particleY, z + particleZ, 0.0D, 0.0D, 0.0D);
                    worldIn.addParticle(ParticleTypes.FLAME, x + particleX, y + particleY, z + particleZ, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }
     */

    @Override
    public void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(PROPERTY_HEAT);
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new HeaterTileEntity();
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new HeaterTileEntity();
    }
}
