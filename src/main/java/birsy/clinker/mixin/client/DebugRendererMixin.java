package birsy.clinker.mixin.client;

import birsy.clinker.client.render.debug.ClinkerDebugRenderers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.SharedConstants;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugRenderer.class)
public abstract class DebugRendererMixin {
    @Shadow @Final public DebugRenderer.SimpleDebugRenderer chunkBorderRenderer;

    @Shadow @Final public DebugRenderer.SimpleDebugRenderer waterDebugRenderer;

    @Shadow @Final public DebugRenderer.SimpleDebugRenderer heightMapRenderer;

    @Shadow @Final public DebugRenderer.SimpleDebugRenderer collisionBoxRenderer;

    @Shadow @Final public DebugRenderer.SimpleDebugRenderer supportBlockRenderer;

    @Shadow @Final public DebugRenderer.SimpleDebugRenderer neighborsUpdateRenderer;

    @Shadow @Final public StructureRenderer structureRenderer;

    @Shadow @Final public DebugRenderer.SimpleDebugRenderer lightDebugRenderer;

    @Shadow @Final public DebugRenderer.SimpleDebugRenderer worldGenAttemptRenderer;

    @Shadow @Final public DebugRenderer.SimpleDebugRenderer solidFaceRenderer;

    @Shadow @Final public DebugRenderer.SimpleDebugRenderer chunkRenderer;

    @Shadow @Final public BrainDebugRenderer brainDebugRenderer;

    @Shadow @Final public VillageSectionsDebugRenderer villageSectionsDebugRenderer;

    @Shadow @Final public BeeDebugRenderer beeDebugRenderer;

    @Shadow @Final public RaidDebugRenderer raidDebugRenderer;

    @Shadow @Final public GoalSelectorDebugRenderer goalSelectorRenderer;

    @Shadow @Final public GameTestDebugRenderer gameTestDebugRenderer;

    @Shadow @Final public GameEventListenerRenderer gameEventListenerRenderer;

    @Shadow @Final public LightSectionDebugRenderer skyLightSectionDebugRenderer;

    @Shadow @Final public BreezeDebugRenderer breezeDebugRenderer;

    @Shadow @Final public PathfindingRenderer pathfindingRenderer;

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;DDD)V", at = @At("TAIL"))
    private void clinker$render(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, double a, double b, double c, CallbackInfo ci) {
        for (DebugRenderer.SimpleDebugRenderer renderer : ClinkerDebugRenderers.renderers) {
            if (SharedConstants.IS_RUNNING_IN_IDE && ClinkerDebugRenderers.shouldRender) renderer.render(poseStack, bufferSource, a, b, c);
        }

        this.pathfindingRenderer.render(poseStack, bufferSource, a, b, c);
//        this.waterDebugRenderer.render(poseStack, bufferSource, a, b, c);
//        this.chunkBorderRenderer.render(poseStack, bufferSource, a, b, c);
//        this.heightMapRenderer.render(poseStack, bufferSource, a, b, c);
//        this.collisionBoxRenderer.render(poseStack, bufferSource, a, b, c);
//        this.supportBlockRenderer.render(poseStack, bufferSource, a, b, c);
//        this.neighborsUpdateRenderer.render(poseStack, bufferSource, a, b, c);
//        this.structureRenderer.render(poseStack, bufferSource, a, b, c);
//        this.lightDebugRenderer.render(poseStack, bufferSource, a, b, c);
//        this.worldGenAttemptRenderer.render(poseStack, bufferSource, a, b, c);
//        this.solidFaceRenderer.render(poseStack, bufferSource, a, b, c);
//        this.chunkRenderer.render(poseStack, bufferSource, a, b, c);
        this.brainDebugRenderer.render(poseStack, bufferSource, a, b, c);
//        this.villageSectionsDebugRenderer.render(poseStack, bufferSource, a, b, c);
//        this.beeDebugRenderer.render(poseStack, bufferSource, a, b, c);
//        this.raidDebugRenderer.render(poseStack, bufferSource, a, b, c);
//        this.goalSelectorRenderer.render(poseStack, bufferSource, a, b, c);
//        this.gameTestDebugRenderer.render(poseStack, bufferSource, a, b, c);
//        this.gameEventListenerRenderer.render(poseStack, bufferSource, a, b, c);
//        this.skyLightSectionDebugRenderer.render(poseStack, bufferSource, a, b, c);
//        this.breezeDebugRenderer.render(poseStack, bufferSource, a, b, c);
    }
}
