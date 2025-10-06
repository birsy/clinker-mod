package birsy.clinker.core.registry;

import birsy.clinker.common.world.block.fluid.VitriolFluid;
import birsy.clinker.core.Clinker;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

@EventBusSubscriber(modid = Clinker.MOD_ID, value = Dist.CLIENT)
public class ClinkerFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, Clinker.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, Clinker.MOD_ID);

    public static final Supplier<FluidType> VITRIOL_TYPE = FLUID_TYPES.register("vitriol", () -> new FluidType(
            FluidType.Properties.create()
                    .fallDistanceModifier(0F)
                    .canConvertToSource(true)
                    .canExtinguish(true)
                    .supportsBoating(true)
                    .density(1200)
                    .viscosity(6000)
    ));
    public static final Supplier<FlowingFluid> VITRIOL = FLUIDS.register("vitriol", VitriolFluid.Source::new);
    public static final Supplier<FlowingFluid> FLOWING_VITRIOL = FLUIDS.register("flowing_vitriol", VitriolFluid.Flowing::new);

    @SubscribeEvent
    static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            private static final ResourceLocation STILL = Clinker.resource("block/vitriol_still"),
                    FLOW = Clinker.resource("block/vitriol_flow"),
                    OVERLAY = Clinker.resource("block/vitriol_overlay");

            @Override
            public ResourceLocation getStillTexture() {
                return STILL;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return FLOW;
            }

            @Override
            public ResourceLocation getOverlayTexture() {
                return OVERLAY;
            }

            @Override
            public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
                return OVERLAY;
            }
        }, VITRIOL_TYPE.get());
    }
}
