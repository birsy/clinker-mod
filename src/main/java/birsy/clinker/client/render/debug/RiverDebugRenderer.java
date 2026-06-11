package birsy.clinker.client.render.debug;

import birsy.clinker.core.Clinker;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import foundry.veil.api.client.util.DebugRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.client.renderer.debug.DebugRenderer.renderFloatingText;

public class RiverDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
    List<RiverDebugInfo> rivers = new ArrayList<>();

    public void handlePacket(List<RiverDebugPoint> points) {
        if (points.isEmpty()) return;
        int color = FastColor.ARGB32.colorFromFloat(1.0F, (float) Math.random(), (float) Math.random(), (float) Math.random());
        rivers.add(new RiverDebugInfo(color, ImmutableList.copyOf(points)));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, double camX, double camY, double camZ) {
        if (!Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
            rivers.clear();
            return;
        }
        for (RiverDebugInfo river : rivers) {
            List<RiverDebugPoint> points = river.points;
            int color = river.color;
            float r = FastColor.ARGB32.red(color) / 255.0F,
                  g = FastColor.ARGB32.green(color) / 255.0F,
                  b = FastColor.ARGB32.blue(color) / 255.0F;
            for (int i = 0; i < points.size(); i++) {
                RiverDebugPoint point = points.get(i);

                double dist = Mth.lengthSquared(point.x() - camX, point.y() - camY, point.z() - camZ);
                if (dist < 30 * 30) {
                    renderFloatingText(poseStack, bufferSource,
                            "" + i,
                            point.x(), point.y() + 1, point.z(),
                            0xFFFFFFFF
                    );
                }

                DebugRenderHelper.renderSphere(
                        poseStack, bufferSource.getBuffer(RenderType.lines()),
                        16, (float) point.radius(),
                        point.x() - camX, point.y() - camY, point.z() - camZ,
                        r, g, b, 0.1F
                );
                if (i == points.size() - 1) continue;
                RiverDebugPoint nextPoint = points.get(i + 1);

                if (Math.abs(nextPoint.y() - point.y()) > 1) {
                    DebugRenderHelper.renderLine(
                            poseStack, bufferSource.getBuffer(RenderType.lines()),
                            point.x() - camX, point.y() - camY, point.z() - camZ,
                            point.x() - camX, nextPoint.y() - camY, point.z() - camZ,
                            r, g, b, 0.8F
                    );
                    DebugRenderHelper.renderSphere(
                            poseStack, bufferSource.getBuffer(RenderType.lines()),
                            16, (float) point.radius(),
                            point.x() - camX, nextPoint.y() - camY, point.z() - camZ,
                            r, g, b, 0.1F
                    );
                }

                DebugRenderHelper.renderLine(
                        poseStack, bufferSource.getBuffer(RenderType.lines()),
                        point.x() - camX, nextPoint.y() - camY, point.z() - camZ,
                        nextPoint.x() - camX, nextPoint.y() - camY, nextPoint.z() - camZ,
                        r, g, b, 0.8F
                );
            }
        }
    }

    public record RiverDebugInfo(int color, List<RiverDebugPoint> points) {}
    public record RiverDebugPoint(int x, int y, int z, double radius, double depth) {
        public static final StreamCodec<FriendlyByteBuf, RiverDebugPoint> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, RiverDebugPoint::x,
                ByteBufCodecs.INT, RiverDebugPoint::y,
                ByteBufCodecs.INT, RiverDebugPoint::z,
                ByteBufCodecs.DOUBLE, RiverDebugPoint::radius,
                ByteBufCodecs.DOUBLE, RiverDebugPoint::depth,
                RiverDebugPoint::new
        );
    }
}
