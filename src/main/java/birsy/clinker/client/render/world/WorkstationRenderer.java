package birsy.clinker.client.render.world;

import birsy.clinker.client.render.DebugRenderUtil;
import birsy.clinker.common.world.alchemy.workstation.Workstation;
import birsy.clinker.common.world.alchemy.workstation.WorkstationManager;
import birsy.clinker.common.world.alchemy.workstation.WorkstationPhysicsObject;
import birsy.clinker.common.world.alchemy.workstation.camera.CameraPath;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class WorkstationRenderer {
    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            event.getPoseStack().pushPose();
            Vec3 camPos = event.getCamera().getPosition();
            event.getPoseStack().translate(-camPos.x(), -camPos.y(), -camPos.z());
            render(event.getPoseStack(), event.getLevelRenderer().renderBuffers.bufferSource(), event.getFrustum(), event.getPartialTick());
            event.getPoseStack().popPose();
        }
    }

    public static void render(PoseStack poseStack, MultiBufferSource pBuffer, Frustum camera, float pPartialTicks) {
        List<Workstation> renderableWorkstations = new ArrayList<>();
        WorkstationManager manager = WorkstationManager.clientWorkstationManager;
        if (manager == null) return;
        for (Workstation workstation : manager.workstationStorage.values()) {
            for (long keyLong : workstation.containedBlocks.bitsetBySection.keySet()) {
                SectionPos pos = SectionPos.of(keyLong);
                AABB aabb = new AABB(pos.minBlockX(), pos.minBlockY(), pos.minBlockZ(), pos.maxBlockX(), pos.maxBlockY(), pos.maxBlockZ());
                if (camera.isVisible(aabb.inflate(8))) {
                    renderableWorkstations.add(workstation);
                    break;
                }
            }
        }

        for (Workstation workstation : renderableWorkstations) {
            //renderWorkstation(poseStack, pBuffer, workstation, pPartialTicks);
        }
    }

    private static void renderWorkstation(PoseStack pPoseStack, MultiBufferSource pBufferSource, Workstation station, float pPartialTicks) {
        for (BlockPos containedBlock : station.containedBlocks) {
            pPoseStack.pushPose();
            Vec3 translation = new Vec3(containedBlock.getX(), containedBlock.getY(), containedBlock.getZ());
            pPoseStack.translate(translation.x, translation.y, translation.z);
            DebugRenderUtil.renderCube(pPoseStack, pBufferSource.getBuffer(RenderType.LINES), 1.0F, 1.0F, 1.0F, 0.2F);
            pPoseStack.popPose();
        }

        for (CameraPath.CameraPathNode node : station.path.cameraPathNodes) {
            pPoseStack.pushPose();
            Vec3 translation = node.position;
            pPoseStack.translate(translation.x, translation.y + node.offset, translation.z);

            DebugRenderUtil.renderSphere(pPoseStack, pBufferSource.getBuffer(RenderType.LINES), 16, 0.2F, 0, 0, 0, 1, 0, 0, 1);
            float length = 0.5F;
            DebugRenderUtil.renderLine(pPoseStack, pBufferSource.getBuffer(RenderType.LINES), 0, 0, 0, node.direction.x * length, node.direction.y * length, node.direction.z * length, 0, 1, 0, 1);
            pPoseStack.popPose();
        }

        for (WorkstationPhysicsObject object : station.environment.objects) {
            WorkstationPhysicsObject.SphereBoxCollider collider = object.collider;
            for (WorkstationPhysicsObject.SphereCollider sphere : collider.spheres) {
                pPoseStack.pushPose();
                Vec3 translation = sphere.position;
                pPoseStack.translate(translation.x, translation.y, translation.z);

                DebugRenderUtil.renderSphere(pPoseStack, pBufferSource.getBuffer(RenderType.LINES), 16, (float)sphere.radius, 0, 0, 0, 1, 0, 0, 1);
                pPoseStack.popPose();
            }
            DebugRenderUtil.renderBox(pPoseStack, pBufferSource.getBuffer(RenderType.LINES), collider.boundingBox, 1.0F, 1.0F, 1.0F, 0.2F);
        }

        for(List<AABB> shapes : station.environment.blockShapeCache.values()) {
            for (AABB box : shapes) {
                DebugRenderUtil.renderBox(pPoseStack, pBufferSource.getBuffer(RenderType.LINES), box, 1, 0.5F, 1, 1);
            }
        }

        for (CameraPath.CameraPathLine line : station.path.cameraPathLines) {
            Vec3 pos1 = line.node1().position.add(0, line.node1().offset, 0);
            Vec3 pos2 = line.node2().position.add(0, line.node1().offset, 0);
            DebugRenderUtil.renderLine(pPoseStack, pBufferSource.getBuffer(RenderType.LINES), pos1.x(), pos1.y(), pos1.z(), pos2.x(), pos2.y(), pos2.z(), 0, 0, 1, 1, 0, 0, 0, 1);
        }

//        AABB aabb = new AABB(((BlockPos) station.containedBlocks.blocks.toArray()[0]).above());
//        Vec3 playerPos = Minecraft.getInstance().cameraEntity.getPosition(pPartialTicks);
//        Vec3 closestPoint = MathUtils.closestPointOnAABB(playerPos, aabb);
//        double distance = playerPos.distanceTo(closestPoint);
//
//        DebugRenderUtil.renderBox(pPoseStack, pBufferSource.getBuffer(RenderType.LINES), aabb, 1.0F, 1.0F, 1.0F, 1.0F);
//
//        pPoseStack.pushPose();
//        pPoseStack.translate(playerPos.x, playerPos.y, playerPos.z);
//        DebugRenderUtil.renderSphere(pPoseStack, pBufferSource.getBuffer(RenderType.LINES), 32, (float)distance, 0, 0, 0, 1, 0, 0, 1);
//        pPoseStack.popPose();
//
//        pPoseStack.pushPose();
//        pPoseStack.translate(closestPoint.x, closestPoint.y, closestPoint.z);
//        DebugRenderUtil.renderSphere(pPoseStack, pBufferSource.getBuffer(RenderType.LINES), 32, 2.0F / 16.0F, 0, 0, 0, 1, 0, 0, 1);
//        pPoseStack.popPose();
    }
}
