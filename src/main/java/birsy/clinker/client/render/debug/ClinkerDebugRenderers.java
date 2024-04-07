package birsy.clinker.client.render.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.debug.DebugRenderer;

import java.util.ArrayList;
import java.util.List;

public class ClinkerDebugRenderers {
    public static boolean shouldRender = true;
    public static GnomadSquadDebugRenderer gnomadSquadDebugRenderer;
    public static List<DebugRenderer.SimpleDebugRenderer> renderers = new ArrayList<>();

    public static void initialize() {
        gnomadSquadDebugRenderer = new GnomadSquadDebugRenderer(Minecraft.getInstance());
        renderers.add(gnomadSquadDebugRenderer);
    }
}
