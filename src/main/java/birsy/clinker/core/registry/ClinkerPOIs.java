package birsy.clinker.core.registry;

import birsy.clinker.common.page.PageElementType;
import birsy.clinker.common.page.elements.ImagePageElement;
import birsy.clinker.core.Clinker;
import net.minecraft.Util;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class ClinkerPOIs {
    public static final DeferredRegister<PoiType> POINT_OF_INTEREST_TYPES =
            DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, Clinker.MOD_ID);

    public static final DeferredHolder<PoiType, PoiType> CONTAINER =
            POINT_OF_INTEREST_TYPES.register("container", () -> new PoiType(
                    Util.make(new HashSet<>(), set -> {
                        set.addAll(Blocks.CHEST.getStateDefinition().getPossibleStates());
                        set.addAll(Blocks.TRAPPED_CHEST.getStateDefinition().getPossibleStates());
                        set.addAll(Blocks.BARREL.getStateDefinition().getPossibleStates());
                    }), 1, 1)
            );

    public static final DeferredHolder<PoiType, PoiType> RELAXATION_POINT =
            POINT_OF_INTEREST_TYPES.register("relaxation_point", () -> new PoiType(
                    Set.of(
                            Blocks.CAMPFIRE.defaultBlockState(),
                            Blocks.SOUL_CAMPFIRE.defaultBlockState(),
                            Blocks.BELL.defaultBlockState()
                    ), 5, 6)
            );
}
