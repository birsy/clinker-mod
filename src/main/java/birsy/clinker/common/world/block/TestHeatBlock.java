package birsy.clinker.common.world.block;

import birsy.clinker.common.world.block.blockentity.TestHeatBlockEntity;
import birsy.clinker.common.world.level.heatnetwork.HeatNode;
import birsy.clinker.core.registry.entity.ClinkerBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class TestHeatBlock extends BaseEntityBlock {
    //public static final MapCodec<TestHeatBlock> CODEC = simpleCodec(props -> new TestHeatBlock(props, ClinkerBlockEntities.STOVE, (blockEntity) -> new HeatNode(8)));
    protected final Function<TestHeatBlockEntity, HeatNode> heatNodeSupplier;
    private final BlockEntityType<? extends TestHeatBlockEntity> blockEntityType;

    public TestHeatBlock(Properties pProperties, BlockEntityType<? extends TestHeatBlockEntity> type, Function<TestHeatBlockEntity, HeatNode> heatNodeSupplier) {
        super(pProperties);
        this.heatNodeSupplier = heatNodeSupplier;
        this.blockEntityType = type;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new TestHeatBlockEntity(blockEntityType, pPos, pState, heatNodeSupplier);
    }
}
