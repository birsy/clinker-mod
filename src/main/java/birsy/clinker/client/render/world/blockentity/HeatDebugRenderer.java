package birsy.clinker.client.render.world.blockentity;

import birsy.clinker.client.render.DebugRenderUtil;
import birsy.clinker.common.world.block.blockentity.TestHeatBlockEntity;
import birsy.clinker.common.world.level.heatnetwork.HeatSocket;
import birsy.clinker.common.world.level.heatnetwork.HeatSourceNode;
import birsy.clinker.common.world.level.heatnetwork.TileHeatNode;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class HeatDebugRenderer<T extends TestHeatBlockEntity> implements BlockEntityRenderer<T> {
    public HeatDebugRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(T pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        DebugRenderer.renderFloatingText(pPoseStack, pBufferSource,
                "HEAT: " + pBlockEntity.heatNode.getTotalHeat(),
                0,0,0, pBlockEntity.heatNode instanceof HeatSourceNode ? 0xFFFF0000 : 0xFFFFFFFF);
        VertexConsumer lines = pBufferSource.getBuffer(RenderType.lines());

        DebugRenderUtil.renderCube(pPoseStack, lines,  1.0F, 0.5F, 0.0F, 0.25F);

        Vec3 pos = new Vec3(pBlockEntity.getBlockPos().getX(), pBlockEntity.getBlockPos().getY(), pBlockEntity.getBlockPos().getZ());
        for (HeatSocket socket : pBlockEntity.heatNode.sockets) {
            if (socket.connected()) {
                if (socket.connectedSocket().node instanceof TileHeatNode tile) {
                    BlockEntity tileEntity = tile.entity;
                    Vec3 otherPos = new Vec3(tileEntity.getBlockPos().getX(), tileEntity.getBlockPos().getY(), tileEntity.getBlockPos().getZ());
                    DebugRenderUtil.renderLine(pPoseStack, lines,
                            Vec3.ZERO, otherPos.subtract(pos), 1.0F, 0.0F, 0.0F, 0.25F);
                }
            }
        }

    }
}
