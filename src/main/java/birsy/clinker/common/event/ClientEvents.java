package birsy.clinker.common.event;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import ResourceLocation;

@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {
    protected static final ResourceLocation LIGHT = new ResourceLocation(Clinker.MOD_ID, "textures/misc/spirit_glint.png");

    //@SubscribeEvent
    public static void renderLiving(final RenderLivingEvent.Post<PathfinderMob, ?> event) {
        PoseStack matrixStack = event.getMatrixStack();
        matrixStack.translate(0.0, 1.5, 0.0);
        matrixStack.mulPose(Vector3f.ZP.rotation((float) Math.PI));
        matrixStack.mulPose(Vector3f.YP.rotation((float) Math.toRadians(event.getEntity().getViewYRot(event.getPartialRenderTick()))));

        LivingEntity entity = event.getEntity();
        float ticks = entity.tickCount + event.getPartialRenderTick();

        Model model = event.getRenderer().getModel();
        model.texHeight = 16;
        model.texWidth = 16;

        float glowRadius = 4;
        float resolution = 8;

        for (int i = 1; i < resolution; i++) {
            matrixStack.pushPose();

            float distanceBetweenLayers = glowRadius / resolution;

            float size = 1.0F + ((i * distanceBetweenLayers) / (glowRadius * 2));
            size = size * MathUtils.minMaxSin(ticks * 0.02F, 1, 1.125F);
            matrixStack.scale(size, size, size);

            float wiggleX = Mth.sin(ticks * 0.06F) * 0.025F;
            float wiggleY = Mth.cos(ticks * 0.07F) * 0.025F;
            float wiggleZ = Mth.sin(ticks * 0.08F) * 0.025F;
            matrixStack.translate(wiggleX * (size - 1.0F), wiggleY * (size - 1.0F), wiggleZ * (size - 1.0F));

            float opacity = 1 / (resolution * 2.0F);
            event.getRenderer().getModel().renderToBuffer(matrixStack, event.getBuffers().getBuffer(RenderType.energySwirl(LIGHT, 1, 1)), event.getLight(), OverlayTexture.NO_OVERLAY, opacity, opacity, opacity, 1.0F);

            matrixStack.popPose();
        }
    }
}
