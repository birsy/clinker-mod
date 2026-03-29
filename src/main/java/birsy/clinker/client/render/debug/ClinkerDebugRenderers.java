package birsy.clinker.client.render.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.debug.DebugRenderer;

import java.util.ArrayList;
import java.util.List;

public class ClinkerDebugRenderers {
    public static boolean shouldRender = true;
    public static SquadDebugRenderer squadDebugRenderer;
    public static List<DebugRenderer.SimpleDebugRenderer> renderers = new ArrayList<>();

    public static void initialize() {
        squadDebugRenderer = new SquadDebugRenderer(Minecraft.getInstance());
        renderers.add(squadDebugRenderer);
    }
}
