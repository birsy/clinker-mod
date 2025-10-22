package birsy.clinker.client.render;

import birsy.clinker.common.page.Page;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexSorting;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.framebuffer.FramebufferAttachmentDefinition;
import foundry.veil.api.client.render.texture.TextureFilter;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.system.NativeResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageAtlas implements NativeResource {
    public static final ResourceLocation LOCATION = Clinker.resource("page_atlas");
    final int width, height;
    final AdvancedFbo frameBuffer;

    final Page.PageLayout[] pageLayouts;
    final Map<Page.PageLayout, Integer> indexByLayout;
    final int[] framesSinceLayoutRendered;
    final int[] layoutPriority;
    final List<Integer> indicesToRender;

    public PageAtlas(int width, int height) {
        this.width = width;
        this.height = height;

        int size = width * height;
        this.pageLayouts = new Page.PageLayout[size];
        this.indexByLayout = new HashMap<>(size);
        this.framesSinceLayoutRendered = new int[size];
        this.layoutPriority = new int[size];
        this.indicesToRender = new ArrayList<>(size);

        this.frameBuffer = AdvancedFbo.withSize(256 * width, 256 * height)
                .setFormat(FramebufferAttachmentDefinition.Format.RGBA4)
                .setFilter(TextureFilter.CLAMP)
                .setDebugLabel("Page Atlas")
                .build(true);
        VeilRenderSystem.renderer().getFramebufferManager().setFramebuffer(LOCATION, this.frameBuffer);
    }

    public void tryReserveLayoutLocation(Page.PageLayout layout, int priority, int[] coordinatesOut) {
        if (indexByLayout.containsKey(layout)) {
            // if it's already included, return its location.
            int index = indexByLayout.get(layout);
            coordinatesOut[0] = Math.floorMod(index, width) * 256;
            coordinatesOut[1] = Math.floorDiv(index, height) * 256;
        } else {
            // otherwise, return the default location and add it to be rendered at the start of next frame.
            coordinatesOut[0] = 0;
            coordinatesOut[1] = 0;

            int index = findValidIndex(priority);
            // out of space!
            if (index == -1)
                return;
            // otherwise, add this to the layout;
            pageLayouts[index] = layout;
            indexByLayout.put(layout, index);
            framesSinceLayoutRendered[index] = 0;
            layoutPriority[index] = priority;
            indicesToRender.add(index);
        }
    }

    private int findValidIndex(int priority) {
        int lowestPriorityIndex = -1, lowestPriority = Integer.MAX_VALUE;
        for (int i = 1; i < pageLayouts.length; i++) {
            if (framesSinceLayoutRendered[i] > 2) {
                return i;
            }
            if (layoutPriority[i] <= lowestPriority) {
                lowestPriorityIndex = i;
                lowestPriority = layoutPriority[i];
            }
        }

        if (priority > lowestPriority) {
            return lowestPriorityIndex;
        }
        return -1;
    }

    public void update() {
        // render anything we just added to the atlas.
        PageRenderer.beginPageRenderBatch();
        for (int index : this.indicesToRender) {
            int x = Math.floorMod(index, width) * 256,
                y = Math.floorDiv(index, height) * 256;
            PageRenderer.renderPageToAtlas(pageLayouts[index], x, y);
        }
        this.indicesToRender.clear();
        PageRenderer.endPageRenderBatch();

        // update frame times
        for (int i = 0; i < this.framesSinceLayoutRendered.length; i++) {
            this.framesSinceLayoutRendered[i]++;
        }
    }

    @Override
    public void free() {
        frameBuffer.free();
    }
}
