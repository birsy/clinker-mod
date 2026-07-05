package birsy.clinker.client.render.world.blockentity;

import birsy.clinker.client.entity.SingleBoneSkeleton;
import birsy.clinker.common.world.block.PressureCookerBlock;
import birsy.clinker.common.world.block.blockentity.PressureCookerBlockEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import foundry.veil.api.client.necromancer.Skeleton;
import foundry.veil.api.client.necromancer.render.NecromancerRenderer;
import foundry.veil.api.client.necromancer.render.Skin;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;

public class PressureCookerRenderer implements BlockEntityRenderer<PressureCookerBlockEntity> {
    public static final ResourceLocation RENDERTYPE = Clinker.resource("entity/clinker_entity");
    private static final ResourceLocation TEXTURE_LOCATION = Clinker.resource("textures/entity/pressure_cooker.png");
    private static final Skeleton SKELETON = new SingleBoneSkeleton();
    private static final Skin LID_SKIN = Skin.builder(64, 32)
            .startBone("Root")
            .addCube(20F, 13F, 2F, -10F, -6.5F, -7F, 0F, 0F, 0F, 0F, 0F, false)
            .addCube(20F, 2F, 12F, -10F, 4.5F, -5F, 0F, 0F, 0F, 0F, 15F, false)
            .build();

    public PressureCookerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(PressureCookerBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
//
//        Vector3f pos = poseStack.last().pose().transformPosition(new Vector3f());
//        Clinker.LOGGER.info("{} {} {}", pos.x, pos.y, pos.z);

        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.scale(1F / 16F, 1F / 16F, 1F / 16F);
        Direction facing = blockEntity.getBlockState().getValue(PressureCookerBlock.FACING);
        boolean mirrored = blockEntity.getBlockState().getValue(PressureCookerBlock.MIRRORED);
        float rotation = facing.toYRot();
        if (facing.getAxis() == Direction.Axis.Z) rotation += 180;
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        poseStack.translate(mirrored ? 11 : -11, 1.5, -1);
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.map(Mth.sin(((Minecraft.getInstance().level.getGameTime() % 1000) + partialTick) * 0.1F), -1F, 1F, 0, 100)));

        NecromancerRenderer necromancerRenderer = VeilRenderSystem.getNecromancerRenderer();
        necromancerRenderer.reset();
        necromancerRenderer.setTransform(poseStack.last().pose());
        necromancerRenderer.setLight(packedLight);
        necromancerRenderer.setColor(1F, 1F, 1F, 1F);
        necromancerRenderer.draw(VeilRenderType.get(RENDERTYPE, TEXTURE_LOCATION), SKELETON, LID_SKIN, partialTick);

        poseStack.popPose();
    }

    @Override
    public AABB getRenderBoundingBox(PressureCookerBlockEntity blockEntity) {
        return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity);
    }
}
