package birsy.clinker.client.render.debug;

import birsy.clinker.client.render.DebugRenderUtil;
import birsy.clinker.common.networking.packet.debug.GnomadSquadDebugPacket;
import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
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
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class GnomadSquadDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
    private final HashMap<UUID, GnomadSquadDebugPacket> squadDumps = new HashMap<>();
    private final Minecraft minecraft;

    public GnomadSquadDebugRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, double x, double y, double z) {
        for (GnomadSquadDebugPacket squadDump : squadDumps.values()) renderSquad(squadDump, poseStack, bufferSource, x, y, z);
        squadDumps.values().removeIf(packet -> packet.squadMemberIDs.isEmpty());
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

        double squadY = squadDump.squadCenter.y() + 8.5F;
        double yOffset = 0;
        DebugRenderer.renderFloatingText(poseStack, bufferSource, "Squad " + squadDump.squadID.toString(), squadDump.squadCenter.x(), squadY - yOffset, squadDump.squadCenter.z(), -16711936);
        yOffset += 0.3;

        DebugRenderer.renderFloatingText(poseStack, bufferSource, "Members:", squadDump.squadCenter.x(), squadY - yOffset, squadDump.squadCenter.z(), -16711681);
        for (int i = 0; i < squadDump.squadMemberIDs.size(); i++) {
            int id = squadDump.squadMemberIDs.get(i);
            Entity entity = level.getEntity(id);
            if (entity == null) continue;
            String name  = DebugEntityNameGenerator.getEntityName(entity);
            yOffset += 0.25;
            DebugRenderer.renderFloatingText(poseStack, bufferSource, name, squadDump.squadCenter.x(), squadY - yOffset, squadDump.squadCenter.z(), -3355444);
        }

        yOffset += 0.4;
        DebugRenderer.renderFloatingText(poseStack, bufferSource, "Tasks:", squadDump.squadCenter.x(), squadY - yOffset, squadDump.squadCenter.z(), -98404);
        for (int i = 0; i < squadDump.taskNames.size(); i++) {
            String task = squadDump.taskNames.get(i);
            yOffset = drawSquadTask(poseStack, bufferSource, squadDump.squadCenter.x(), squadY, squadDump.squadCenter.z(), yOffset, task);
        }
    }

    public double drawSquadTask(PoseStack poseStack, MultiBufferSource bufferSource, double x, double y, double z, double yOffset, String task) {
        String[] splitTasks = task.split("\n");
        for (String splitTask : splitTasks) {
            yOffset += 0.25;
            DebugRenderer.renderFloatingText(poseStack, bufferSource, splitTask, x, y - yOffset, z, -3355444);
        }
        yOffset += 0.25;
        return yOffset;
    }

    public void dumpSquad(GnomadSquadDebugPacket debugPacket) {
        squadDumps.put(debugPacket.squadID, debugPacket);
    }
    public void removeSquad(UUID squadID) {
        squadDumps.remove(squadID);
    }
}
