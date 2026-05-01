package birsy.clinker.client.entity;

import birsy.clinker.common.world.entity.RedLightningBoltEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.dynamicbuffer.DynamicBufferType;
import foundry.veil.api.client.render.light.data.PointLightData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.Random;

public class RedLightningBoltRenderer extends EntityRenderer<RedLightningBoltEntity> {
    final Quaternionf q0 = new Quaternionf(), q1 = new Quaternionf();
    final Vector3f s0 = new Vector3f(), s1 = new Vector3f();

    public RedLightningBoltRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(RedLightningBoltEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.shape == null || entity.shape.isEmpty()) return;

        if (entity.light == null && !entity.clientRemoved) {
            entity.light = VeilRenderSystem.renderer().getLightRenderer()
                    .addLight(new PointLightData());
        }
        VeilRenderSystem.renderer().enableBuffers(Clinker.resource("lightning"), DynamicBufferType.ALBEDO, DynamicBufferType.NORMAL);
        Matrix4f pose = poseStack.last().pose();
        VertexConsumer vertices = buffer.getBuffer(RenderType.lightning());

        Vector3f averagePos = s1.set(0);
        float totalWeight = 0;

        final int glowLayers = 4;
        for (int layer = 0; layer < glowLayers; layer++) {
            float layerFactor = layer / (glowLayers - 1.0F);
            float layerWidth = Mth.lerp(layerFactor, 0.3F, 1.2F);

            List<List<RedLightningBoltEntity.LightningNode>> shape = entity.shape;
            for (int branchIndex = 0; branchIndex < shape.size(); branchIndex++) {
                List<RedLightningBoltEntity.LightningNode> branch = shape.get(branchIndex);
                if (branch.size() < 2) continue;
                q0.identity();
                q1.identity();

                float dX = 0, dY = -1, dZ = 0;
                for (int branchSegment = 0; branchSegment < branch.size() - 1; branchSegment++) {
                    RedLightningBoltEntity.LightningNode upper = branch.get(branchSegment + 0),
                                                         lower = branch.get(branchSegment + 1);
                    float nDX = lower.x() - upper.x(), nDY = lower.y() - upper.y(), nDZ = lower.z() - upper.z();
                    float length = (float) Mth.length(nDX, nDY, nDZ);
                    nDX /= length;
                    nDY /= length;
                    nDZ /= length;

                    q1.rotateTo(dX, dY, dZ, nDX, nDY, nDZ);
                    drawSegment(pose, vertices,
                            upper, q0, lower, q1,
                            layerWidth, layerFactor
                    );

                    dX = nDX;
                    dY = nDY;
                    dZ = nDZ;
                    q0.set(q1);

                    averagePos.add(lower.x() * lower.scale(), lower.y() * lower.scale(), lower.z() * lower.scale());
                    totalWeight += lower.scale();
                }
            }
        }
        averagePos.mul(1F / totalWeight);

        if (entity.light != null && entity.light.isValid()) {
            entity.light.getLightData().setPosition(averagePos.x + entity.getX(), averagePos.y + entity.getY(), averagePos.z + entity.getZ());
            entity.light.getLightData().setRadius(200.0F);
            entity.light.getLightData().setBrightness(10.0F);

            if (entity.level() instanceof ClientLevel clientLevel) {
                int skyFlashTime = clientLevel.getSkyFlashTime();


                float lightningFlicker = skyFlashTime - partialTicks * 0.5F;
                if (lightningFlicker > 1.0F) lightningFlicker = 1.0F;
                lightningFlicker = Mth.lerp(0.2F, lightningFlicker, 1.0F);
                float lessIntenseLightningFlicker = Mth.lerp(0.1F, lightningFlicker, 1.0F);

                float finalMult = skyFlashTime < 0 ? 0.5F : 1;

                entity.light.getLightData().setColor(
                        1.0F * lightningFlicker * finalMult,
                        0.2F * lessIntenseLightningFlicker * finalMult,
                        0.1F * lessIntenseLightningFlicker * finalMult
                );
            }

        }
    }

    private void drawSegment(
            Matrix4f pose, VertexConsumer vertices,
            RedLightningBoltEntity.LightningNode upper, Quaternionf upperRot,
            RedLightningBoltEntity.LightningNode lower, Quaternionf lowerRot,
            float widthMultiplier, float layerFactor) {

        float r = 1.0F,
              g = Mth.lerp(layerFactor, 1.0F, 0.0F),
              b = Mth.lerp(layerFactor, 1.0F, 0.0F),
              a = Mth.lerp(layerFactor, 0.5F, 0.2F);
        Vector3f vertexPos = s0;

        float lowerX = lower.x(), lowerY = lower.y(), lowerZ = lower.z(), lowerWidth = lower.scale() * widthMultiplier;
        float upperX = upper.x(), upperY = upper.y(), upperZ = upper.z(), upperWidth = upper.scale() * widthMultiplier;

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            float normalX = dir.getStepX(),
                  normalZ = dir.getStepZ();
            float tangentX = -normalZ,
                  tangentZ = normalX;

            vertexPos.set((normalX - tangentX) * lowerWidth, 0, (normalZ - tangentZ) * lowerWidth);
            lowerRot.transform(vertexPos);
            vertices.addVertex(pose, lowerX + vertexPos.x, lowerY + vertexPos.y, lowerZ + vertexPos.z).setColor(r, g, b, a);

            vertexPos.set((normalX - tangentX) * upperWidth, 0, (normalZ - tangentZ) * upperWidth);
            upperRot.transform(vertexPos);
            vertices.addVertex(pose, upperX + vertexPos.x, upperY + vertexPos.y, upperZ + vertexPos.z).setColor(r, g, b, a);

            vertexPos.set((normalX + tangentX) * upperWidth, 0, (normalZ + tangentZ) * upperWidth);
            upperRot.transform(vertexPos);
            vertices.addVertex(pose, upperX + vertexPos.x, upperY + vertexPos.y, upperZ + vertexPos.z).setColor(r, g, b, a);

            vertexPos.set((normalX + tangentX) * lowerWidth, 0, (normalZ + tangentZ) * lowerWidth);
            lowerRot.transform(vertexPos);
            vertices.addVertex(pose, lowerX + vertexPos.x, lowerY + vertexPos.y, lowerZ + vertexPos.z).setColor(r, g, b, a);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(RedLightningBoltEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
