package birsy.clinker.client.render;

import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;
import java.util.Objects;

public class ClinkerFonts {
    public static final ResourceLocation ALCHEMICAL = Clinker.resource("alchemical");
    public static final ResourceLocation SMALL = Clinker.resource("small");
    public static final ResourceLocation SERIF = Clinker.resource("serif");
    public static final ResourceLocation SERIF_SOFT = Clinker.resource("serif_soft");
}
