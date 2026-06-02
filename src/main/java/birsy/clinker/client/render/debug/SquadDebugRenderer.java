package birsy.clinker.client.render.debug;

import birsy.clinker.common.world.entity.system.squad.SquadDebugDataDump;
import com.mojang.blaze3d.vertex.PoseStack;
import foundry.veil.api.client.util.DebugRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.minecraft.client.renderer.debug.DebugRenderer.*;

public class SquadDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
    // enemy position state colors
    private static final int COLOR_KNOWN = 0xFFFF4444,
                         COLOR_UNCERTAIN = 0xFFFFAA00,
                           COLOR_MISSING = 0xFF888888;
    // member colors
    private static final int COLOR_LEADER = 0xFFFFD700,
                             COLOR_MEMBER = 0xFF4488FF;
    // task status colors
    private static final int COLOR_UNASSIGNED = 0xFFAAAAAA,
                            COLOR_IN_PROGRESS = 0xFF44FF44,
                              COLOR_SUCCEEDED = 0xFF00FFFF,
                                 COLOR_FAILED = 0xFFFF4444;

    private final Minecraft minecraft;
    private final Map<UUID, SquadDebugDataDump> squads = new HashMap<>();

    public SquadDebugRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public void handlePacket(List<SquadDebugDataDump> payload) {
        this.clear();
        for (SquadDebugDataDump squad : payload) squads.put(squad.id(), squad);
    }

    @Override
    public void clear() {
        squads.clear();
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, double camX, double camY, double camZ) {
        if (squads.isEmpty()) return;
        if (!minecraft.getEntityRenderDispatcher().shouldRenderHitBoxes()) return;

        Level level = minecraft.level;
        if (level == null) return;

        for (SquadDebugDataDump squad : squads.values()) {
            renderSquad(poseStack, bufferSource, squad, level, camX, camY, camZ);
        }
    }

    private void renderSquad(PoseStack poseStack, MultiBufferSource bufferSource,
                             SquadDebugDataDump squad, Level level,
                             double camX, double camY, double camZ) {
        if (squad.members().isEmpty()) return;
        double cx = squad.centerX(),
               cy = squad.centerY(),
               cz = squad.centerZ();

        String shortId = squad.id().toString().substring(0, 8);
        String centerLabel = "squad [" + shortId + "], sized " + squad.members().size();
        renderFloatingText(poseStack, bufferSource, centerLabel, cx, cy + 2.6, cz, 0xFFFFFFFF);

        for (SquadDebugDataDump.MemberData member : squad.members()) {
            double mx = member.x(),
                   my = member.y(),
                   mz = member.z();
            int memberColor = member.isLeader() ? COLOR_LEADER : COLOR_MEMBER;

            LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()),
                    mx - 0.3, my, mz - 0.3,
                    mx + 0.3, my + 1.9, mz + 0.3,
                    colorR(memberColor), colorG(memberColor), colorB(memberColor), 1.0f);

            String label = member.isLeader() ? "LEADER" : "MEMBER";
            renderFloatingText(poseStack, bufferSource,
                    label,
                    mx, my + 5, mz,
                    memberColor
            );
            renderFloatingText(poseStack, bufferSource,
                    member.name(),
                    mx, my + 4.85, mz,
                    0xFFFFFFFF
            );

            DebugRenderHelper.renderLine(
                    poseStack, bufferSource.getBuffer(RenderType.lines()),
                    mx - camX, my - camY + 1.0, mz - camZ,
                    cx - camX, cy - camY + 1.0, cz - camZ,
                    colorR(memberColor), colorG(memberColor), colorB(memberColor), 0.4F
            );
        }

        double taskY = cy + 3.0;
        for (SquadDebugDataDump.TaskData task : squad.tasks()) {
            int taskColor = switch (task.status()) {
                case "UNASSIGNED" -> COLOR_UNASSIGNED;
                case "IN_PROGRESS" -> COLOR_IN_PROGRESS;
                case "SUCCEEDED" -> COLOR_SUCCEEDED;
                case "FAILED" -> COLOR_FAILED;
                default -> 0xFFFFFFFF;
            };
            String taskLine = String.format("%d %s  %s  (%d/%d-%d)  t=%d",
                    task.priority(),
                    task.className(),
                    task.status(),
                    task.assigneeCount(), task.minAssignees(), task.maxAssignees(),
                    task.ticksExisted());
            renderFloatingText(poseStack, bufferSource, taskLine, cx, taskY, cz, taskColor);
            taskY += 0.25;
        }

        for (SquadDebugDataDump.EnemyPositionData enemy : squad.enemyPositions()) {
            int color = switch (enemy.state()) {
                case "known" -> COLOR_KNOWN;
                case "uncertain" -> COLOR_UNCERTAIN;
                case "missing" -> COLOR_MISSING;
                default -> 0xFFFFFFFF;
            };

            double ex = enemy.x() + 0.5,
                   ey = enemy.y(),
                   ez = enemy.z() + 0.5 ;

            DebugRenderHelper.renderLine(poseStack, bufferSource.getBuffer(RenderType.lines()),
                    ex - 0.5 - camX, ey + 0.1 - camY, ez - camZ,
                    ex + 0.5 - camX, ey + 0.1 - camY, ez - camZ,
                    colorR(color), colorG(color), colorB(color), 0.5f);
            DebugRenderHelper.renderLine(poseStack, bufferSource.getBuffer(RenderType.lines()),
                    ex - camX, ey + 0.1 - camY, ez - 0.5 - camZ,
                    ex - camX, ey + 0.1 - camY, ez + 0.5 - camZ,
                    colorR(color), colorG(color), colorB(color), 0.5f);

            renderFloatingText(poseStack, bufferSource, enemy.name(), ex, ey + 1.4, ez, color);
            renderFloatingText(poseStack, bufferSource, enemy.state(), ex, ey + 1.2, ez, color);
        }
    }

    private static float colorR(int color) { return ((color >> 16) & 0xFF) / 255f; }
    private static float colorG(int color) { return ((color >> 8)  & 0xFF) / 255f; }
    private static float colorB(int color) { return  (color        & 0xFF) / 255f; }
}
