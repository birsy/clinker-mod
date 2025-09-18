package birsy.clinker.client;

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
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SheetMossTintHandler implements ColorResolver, BlockColor, ItemColor {
    public static final SheetMossTintHandler INSTANCE = new SheetMossTintHandler();

    private static final Map<ResourceKey<Biome>, Integer> colorRegistry = new HashMap<>();
    private static final Map<Biome, Optional<ResourceKey<Biome>>> biomeHashMap = new ConcurrentHashMap<>();

    private static final int WHITE = FastColor.ARGB32.colorFromFloat(1, 1, 1, 1);
    private static final int DEFAULT_COLOR = 0xFF776f55;

    public static void register(ResourceKey<Biome> biome, int color) { colorRegistry.put(biome, color); }

    static {
        register(ClinkerBiomes.BRINE_SWAMP, 0xFF6b6969);
    }

    private SheetMossTintHandler() {}

    @Override
    public int getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
        if (tintIndex != 0)
            return WHITE;

        if (level == null || pos == null)
            return DEFAULT_COLOR;

        if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF))
            return BiomeColors.getAverageColor(level, state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos, INSTANCE);

        return BiomeColors.getAverageColor(level, pos, INSTANCE);
    }

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        return DEFAULT_COLOR;
    }

    @Override
    public int getColor(Biome biome, double x, double z) {
        // i despise this
        Optional<ResourceKey<Biome>> keyOptional = biomeHashMap.computeIfAbsent(biome, (actualBiome) -> {
            ClientPacketListener connection = Minecraft.getInstance().getConnection();
            if (connection == null)
                return Optional.empty();

            Optional<HolderLookup.RegistryLookup<Biome>> registryOptional = connection.registryAccess().lookup(Registries.BIOME);
            if (registryOptional.isEmpty())
                return Optional.empty();

            for (ResourceKey<Biome> biomeResourceKey : colorRegistry.keySet()) {
                Optional<Holder.Reference<Biome>> biomeOptional = registryOptional.get().get(biomeResourceKey);
                if (biomeOptional.isEmpty())
                    continue;
                if (actualBiome.equals(biomeOptional.get().value()))
                    return Optional.of(biomeResourceKey);
            }

            return Optional.empty();
        });

        if (keyOptional.isEmpty())
            return DEFAULT_COLOR;

        return colorRegistry.getOrDefault(keyOptional.get(), DEFAULT_COLOR);
    }
}
