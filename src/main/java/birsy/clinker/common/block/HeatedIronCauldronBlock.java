package birsy.clinker.common.block;

import birsy.clinker.common.tileentity.HeatedIronCauldronTileEntity;
import birsy.clinker.core.registry.ClinkerItems;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;

public class HeatedIronCauldronBlock extends BaseEntityBlock implements LiquidBlockContainer {
    private static final VoxelShape INSIDE = box(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    protected static final VoxelShape SHAPE = Shapes.join(Shapes.block(), Shapes.or(box(0.0D, 0.0D, 4.0D, 16.0D, 3.0D, 12.0D), box(4.0D, 0.0D, 0.0D, 12.0D, 3.0D, 16.0D), box(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D), INSIDE), BooleanOp.ONLY_FIRST);

    public HeatedIronCauldronBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE).requiresCorrectToolForDrops().strength(2.0F).noOcclusion());
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;

    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        boolean isItem = entityIn instanceof ItemEntity;
        int i = isItem ? 1 : 3;
        float f = (float)pos.getY() + (6.0F + (float)(3 * i)) / 16.0F;

        if (!worldIn.isClientSide && entityIn.getY() <= (double) f) {
            HeatedIronCauldronTileEntity tileEntity = (HeatedIronCauldronTileEntity) worldIn.getBlockEntity(pos);

            if (!entityIn.fireImmune() && entityIn instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entityIn) && tileEntity.heat > 50) {
                entityIn.hurt(DamageSource.HOT_FLOOR, 1.0F);
            } else if (isItem) {
                tileEntity.addItem((ItemEntity) entityIn);
            }
        }
        super.entityInside(state, worldIn, pos, entityIn);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (player.getItemInHand(handIn).getItem() != ClinkerItems.LADLE.get()) {
            if (worldIn.getBlockEntity(pos) != null && worldIn.getBlockEntity(pos) instanceof HeatedIronCauldronTileEntity) {
                HeatedIronCauldronTileEntity tileEntity = (HeatedIronCauldronTileEntity) worldIn.getBlockEntity(pos);
                tileEntity.stir();
            }
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        HeatedIronCauldronTileEntity tileEntity = (HeatedIronCauldronTileEntity) worldIn.getBlockEntity(pos);
        tileEntity.dropItems(-1, player);

        super.playerWillDestroy(worldIn, pos, state, player);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return new HeatedIronCauldronTileEntity();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockGetter worldIn) {
        return new HeatedIronCauldronTileEntity();
    }

    @Override
    public boolean canPlaceLiquid(BlockGetter worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
        return true;
    }

    @Override
    public boolean placeLiquid(LevelAccessor worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
        if (worldIn.getBlockEntity(pos) instanceof HeatedIronCauldronTileEntity) {
            HeatedIronCauldronTileEntity tileEntity = (HeatedIronCauldronTileEntity) worldIn.getBlockEntity(pos);
            if (fluidStateIn.getType() == tileEntity.tank.getFluid().getFluid()) {
                tileEntity.tank.fill(new FluidStack(fluidStateIn.getType(), fluidStateIn.getAmount() * 125), IFluidHandler.FluidAction.EXECUTE);
                return true;
            }
        }
        return false;
    }
}
