package birsy.clinker.common.block;

import birsy.clinker.common.tileentity.HeaterTileEntity;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.block.*;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.Random;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class HeaterBlock extends BaseEntityBlock {
    public static final IntegerProperty PROPERTY_HEAT = IntegerProperty.create("heat", 0, 15);

    public HeaterBlock() {
        super(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.TERRACOTTA_BROWN)
                .strength(2.75F, 75.0F)
                .sound(SoundType.BASALT)
                .harvestLevel(1)
                .harvestTool(ToolType.PICKAXE)
                .lightLevel((state) -> state.getValue(PROPERTY_HEAT)));
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (worldIn.getBlockEntity(pos) != null && worldIn.getBlockEntity(pos) instanceof HeaterTileEntity) {
            HeaterTileEntity tileEntity = (HeaterTileEntity) worldIn.getBlockEntity(pos);
            tileEntity.isCooking = !tileEntity.isCooking;
            return InteractionResult.SUCCESS;
        }

        return super.use(state, worldIn, pos, player, handIn, hit);
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
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PROPERTY_HEAT);
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return new HeaterTileEntity();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockGetter worldIn) {
        return new HeaterTileEntity();
    }
}
