package birsy.clinker.client;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.world.ClinkerBiomes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SheetMossTintHandler implements BlockColor, ItemColor {
    public static final SheetMossTintHandler INSTANCE = new SheetMossTintHandler();

    private static final Map<Block, Integer> colorRegistry = new HashMap<>();
    private static final Map<Biome, Optional<ResourceKey<Biome>>> biomeHashMap = new ConcurrentHashMap<>();

    private static final int WHITE = FastColor.ARGB32.colorFromFloat(1, 1, 1, 1);
    private static final int DEFAULT_COLOR = 0xFF6e6550;

    public static void register(Block biome, int color) { colorRegistry.put(biome, color); }

    static {
        int calcColor = 0xFF6b6969;
        register(ClinkerBlocks.CALC.get(), calcColor);
        register(ClinkerBlocks.CALC_SLAB.get(), calcColor);
        register(ClinkerBlocks.CALC_STAIRS.get(), calcColor);
        register(ClinkerBlocks.POLISHED_CALC.get(), calcColor);
        register(ClinkerBlocks.POLISHED_CALC_SLAB.get(), calcColor);
        register(ClinkerBlocks.POLISHED_CALC_STAIRS.get(), calcColor);
        register(ClinkerBlocks.CALC_BRICKS.get(), calcColor);
        register(ClinkerBlocks.CALC_BRICK_SLAB.get(), calcColor);
        register(ClinkerBlocks.CALC_BRICK_STAIRS.get(), calcColor);
        register(ClinkerBlocks.SALTMOSS.get(), calcColor);
    }

    private SheetMossTintHandler() {}

    @Override
    public int getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
        if (tintIndex != 0)
            return WHITE;

        if (level == null || pos == null)
            return DEFAULT_COLOR;

        BlockPos offsetPos;
        if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
            offsetPos = pos.above(state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER ? 1 : 2);
        } else {
            offsetPos = pos.above();
        }

        if (offsetPos.getY() > level.getMaxBuildHeight())
            return DEFAULT_COLOR;
        return colorRegistry.getOrDefault(level.getBlockState(offsetPos).getBlock(), DEFAULT_COLOR);
    }

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        return DEFAULT_COLOR;
    }
}
