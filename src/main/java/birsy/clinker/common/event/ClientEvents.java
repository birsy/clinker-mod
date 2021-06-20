package birsy.clinker.common.event;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {
    protected static final ResourceLocation LIGHT = new ResourceLocation(Clinker.MOD_ID, "textures/misc/spirit_glint.png");

    @SubscribeEvent
    public static void renderLiving(final RenderLivingEvent.Post<CreatureEntity, ?> event) {
        MatrixStack matrixStack = event.getMatrixStack();
        matrixStack.translate(0.0, 1.5, 0.0);
        matrixStack.rotate(Vector3f.ZP.rotation((float) Math.PI));
        matrixStack.rotate(Vector3f.YP.rotation((float) Math.toRadians(event.getEntity().getYaw(event.getPartialRenderTick()))));

        LivingEntity entity = event.getEntity();
        float ticks = entity.ticksExisted + event.getPartialRenderTick();

        Model model = event.getRenderer().getEntityModel();
        model.textureHeight = 16;
        model.textureWidth = 16;

        float glowRadius = 4;
        float resolution = 8;

        for (int i = 1; i < resolution; i++) {
            matrixStack.push();

            float distanceBetweenLayers = glowRadius / resolution;

            float size = 1.0F + ((i * distanceBetweenLayers) / (glowRadius * 2));
            size = size * MathUtils.minMaxSin(ticks * 0.02F, 1, 1.125F);
            matrixStack.scale(size, size, size);

            float wiggleX = MathHelper.sin(ticks * 0.06F) * 0.025F;
            float wiggleY = MathHelper.cos(ticks * 0.07F) * 0.025F;
            float wiggleZ = MathHelper.sin(ticks * 0.08F) * 0.025F;
            matrixStack.translate(wiggleX * (size - 1.0F), wiggleY * (size - 1.0F), wiggleZ * (size - 1.0F));

            float opacity = 1 / (resolution * 2.0F);
            event.getRenderer().getEntityModel().render(matrixStack, event.getBuffers().getBuffer(RenderType.getEnergySwirl(LIGHT, 1, 1)), event.getLight(), OverlayTexture.NO_OVERLAY, opacity, opacity, opacity, 1.0F);

            matrixStack.pop();
        }
    }
}
