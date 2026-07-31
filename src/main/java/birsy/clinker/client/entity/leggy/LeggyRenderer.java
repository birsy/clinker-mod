package birsy.clinker.client.entity.leggy;

import birsy.clinker.client.render.utilities.DebugRenderUtil;
import birsy.clinker.common.entity.GiantLeggyCritterEntity;
import birsy.clinker.common.entity.LegManager;
import birsy.clinker.core.Clinker;
import foundry.veil.api.client.necromancer.animation.Animator;
import foundry.veil.api.client.necromancer.render.NecromancerEntityRenderer;
import foundry.veil.api.client.necromancer.render.NecromancerRenderer;
import foundry.veil.api.client.necromancer.render.NecromancerSkinEntityRenderLayer;
import foundry.veil.api.client.necromancer.render.Skin;
import foundry.veil.api.client.render.MatrixStack;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class LeggyRenderer extends NecromancerEntityRenderer<GiantLeggyCritterEntity, LeggySkeleton> {
    public static final ResourceLocation RENDERTYPE = Clinker.resource("entity/clinker_entity");
    private static final ResourceLocation TEXTURE_LOCATION = Clinker.resource("textures/entity/slabcrab.png");

    private final BlockRenderDispatcher dispatcher;

    public LeggyRenderer(EntityRendererProvider.Context context) {
        super(context, 1.0F);
        dispatcher = context.getBlockRenderDispatcher();
        this.addLayer(new NecromancerSkinEntityRenderLayer<>(this) {
            @Override
            public RenderType getRenderType(GiantLeggyCritterEntity entity) {
                return VeilRenderType.get(RENDERTYPE, TEXTURE_LOCATION);
            }
            @Override
            public Skin getSkin(GiantLeggyCritterEntity parent) {
                return LeggySkin.INSTANCE;
            }
            @Override
            protected void renderSkin(GiantLeggyCritterEntity parent, LeggySkeleton skeleton, Skin skin, RenderType renderType, NecromancerRenderer renderer, MatrixStack matrixStack, int packedLight, float partialTicks) {
                if (parent.hurtTime > 0 || !parent.isAlive()) renderer.setOverlay(OverlayTexture.RED_OVERLAY_V);
                super.renderSkin(parent, skeleton, skin, renderType, renderer, matrixStack, packedLight, partialTicks);
            }
        });
    }

    @Override
    public LeggySkeleton createSkeleton(GiantLeggyCritterEntity parent) {
        return new LeggySkeleton();
    }

    @Override
    public Animator<GiantLeggyCritterEntity, LeggySkeleton> createAnimator(GiantLeggyCritterEntity parent, LeggySkeleton skeleton) {
        return new LeggyAnimator(parent, skeleton);
    }

    @Override
    public void render(GiantLeggyCritterEntity parent, NecromancerRenderer renderer, MatrixStack matrixStack, int packedLight, float partialTicks) {
        super.render(parent, renderer, matrixStack, packedLight, partialTicks);
        //if (true) return;
        matrixStack.matrixPush();
        // eliminate partial tick stuffs
        Vec3 previousEntityPos = parent.getPosition(partialTicks);

        double entityX = parent.getX(), entityY = parent.getY(), entityZ = parent.getZ();
        matrixStack.translate(
                entityX - previousEntityPos.x(),
                entityY - previousEntityPos.y(),
                entityZ - previousEntityPos.z()
        );

        Vector3d scratch = new Vector3d();

        LegManager legManager = parent.legManager;
        for (int i = 0; i < legManager.legCount(); i++) {
            LegManager.Leg leg = legManager.getLeg(i);

            legManager.fromParentSpace(leg.relativeSocketPos, scratch);
            double socketX = scratch.x - entityX, socketY = scratch.y - entityY, socketZ = scratch.z - entityZ;
            legManager.fromParentSpace(leg.getRelativeFootPos(), scratch);
            double footX = scratch.x - entityX, footY = scratch.y - entityY, footZ = scratch.z - entityZ;

            DebugRenderUtil.renderLine(
                    matrixStack.toPoseStack(),
                    renderer.getBuffer(RenderType.lines()),
                    socketX, socketY, socketZ,
                    footX, footY, footZ,
                    1F, 1F, 1F, 0.25F
            );

            matrixStack.matrixPush();
            matrixStack.translate(footX, footY, footZ);
            matrixStack.applyScale(0.2F, 0.2F, 0.2F);
            this.dispatcher.renderSingleBlock(Blocks.COBBLESTONE.defaultBlockState(), matrixStack.toPoseStack(), renderer, packedLight, OverlayTexture.NO_OVERLAY);
            matrixStack.matrixPop();

            legManager.fromParentSpace(leg.relativeIdealFootPos, scratch);
            double idealX = scratch.x - entityX, idealY = scratch.y - entityY, idealZ = scratch.z - entityZ;
            matrixStack.matrixPush();
            matrixStack.translate(idealX, idealY, idealZ);
            matrixStack.applyScale(0.15F, 0.15F, 0.15F);
            this.dispatcher.renderSingleBlock(Blocks.REDSTONE_BLOCK.defaultBlockState(), matrixStack.toPoseStack(), renderer, packedLight, OverlayTexture.NO_OVERLAY);
            matrixStack.matrixPop();

            legManager.fromParentSpacePredicted(leg.relativeIdealFootPos, legManager.ticksUntilNextTurn(leg.stepGroup), scratch);
            double predictedIdealX = scratch.x - entityX, predictedIdealY = scratch.y - entityY, predictedIdealZ = scratch.z - entityZ;
            matrixStack.matrixPush();
            matrixStack.translate(predictedIdealX, predictedIdealY, predictedIdealZ);
            matrixStack.applyScale(0.1F, 0.1F, 0.1F);
            this.dispatcher.renderSingleBlock(Blocks.LAPIS_BLOCK.defaultBlockState(), matrixStack.toPoseStack(), renderer, packedLight, OverlayTexture.NO_OVERLAY);
            matrixStack.matrixPop();

            Vector3dc targetPos = leg.getNextTargetPosition();
            if (targetPos != null) {
                scratch.set(targetPos);
                double nextBestTargetX = scratch.x - entityX, nextBestTargetY = scratch.y - entityY, nextBestTargetZ = scratch.z - entityZ;
                matrixStack.matrixPush();
                matrixStack.translate(nextBestTargetX, nextBestTargetY, nextBestTargetZ);
                matrixStack.applyScale(0.2F, 0.2F, 0.2F);
                this.dispatcher.renderSingleBlock(Blocks.GOLD_BLOCK.defaultBlockState(), matrixStack.toPoseStack(), renderer, packedLight, OverlayTexture.NO_OVERLAY);
                matrixStack.matrixPop();
            }
        }
        matrixStack.matrixPop();
    }
}
