package birsy.clinker.core.registry;

import birsy.clinker.core.Clinker;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Supplier;

public class ClinkerFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Clinker.MOD_ID);

    public static final RegistryObject<FlowingFluid> BRINE_SOURCE = FLUIDS.register("brine_source", () -> new ForgeFlowingFluid.Source(ClinkerFluids.BRINE_PROPERTIES));
    public static final RegistryObject<FlowingFluid> BRINE_FLOWING = FLUIDS.register("brine_flowing", () -> new ForgeFlowingFluid.Flowing(ClinkerFluids.BRINE_PROPERTIES));

    public static final ForgeFlowingFluid.Properties BRINE_PROPERTIES = new ForgeFlowingFluid.Properties(BRINE_SOURCE, BRINE_FLOWING,
            FluidAttributes.builder(new ResourceLocation(Clinker.MOD_ID, "blocks/brine_still"),
                                    new ResourceLocation(Clinker.MOD_ID, "blocks/brine_flow")))
                                    .bucket(ClinkerItems.BRINE_BUCKET)
                                    .block(ClinkerBlocks.BRINE)
                                    .canMultiply();
}
