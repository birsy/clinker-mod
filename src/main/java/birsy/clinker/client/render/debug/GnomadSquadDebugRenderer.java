package birsy.clinker.client.render.debug;

import birsy.clinker.client.render.DebugRenderUtil;
import birsy.clinker.common.networking.packet.debug.GnomadSquadDebugPacket;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.world.entity.Entity;

import java.util.Iterator;

public class GnomadSquadDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
    private final IntObjectMap<GnomadSquadDebugPacket> squadDumps = new IntObjectHashMap<>();
    private final Minecraft minecraft;

    public GnomadSquadDebugRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, double x, double y, double z) {
        for (GnomadSquadDebugPacket squadDump : squadDumps.values()) {
            renderSquad(squadDump, poseStack, bufferSource, x, y, z);
        }
    }

    @Override
    public void clear() {
        this.squadDumps.clear();
    }

    public void renderSquad(GnomadSquadDebugPacket squadDump, PoseStack poseStack, MultiBufferSource bufferSource, double x, double y, double z) {
        ClientLevel level = minecraft.level;

        poseStack.pushPose();
        poseStack.translate(-x, -y, -z);
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.LINES);
        for (int memberId : squadDump.squadMemberIDs) {
            Entity member = level.getEntity(memberId);
            if (member == null) continue;
            DebugRenderUtil.renderLine(poseStack, consumer, squadDump.squadCenter, member.getEyePosition(), 0, 0, 0, 0.5F);
        }
        poseStack.popPose();

        for (int i = 0; i < squadDump.taskNames.size(); i++) {
            String task = squadDump.taskNames.get(i);
            float yOffset = (i + 1) * 0.25f;
            DebugRenderer.renderFloatingText(poseStack, bufferSource, task, squadDump.squadCenter.x(), squadDump.squadCenter.y() + yOffset, squadDump.squadCenter.z(), -16711936);
        }
    }

    public void dumpSquad(GnomadSquadDebugPacket debugPacket) {
        squadDumps.put(debugPacket.squadID, debugPacket);
    }
    public void removeSquad(int squadID) {
        squadDumps.remove(squadID);
    }
}
