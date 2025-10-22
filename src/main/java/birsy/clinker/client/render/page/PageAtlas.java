package birsy.clinker.client.render.page;

import birsy.clinker.common.page.Page;
import birsy.clinker.core.Clinker;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.framebuffer.FramebufferAttachmentDefinition;
import foundry.veil.api.client.render.texture.TextureFilter;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.system.NativeResource;

import java.util.*;

public class PageAtlas implements NativeResource {
    public static final PageAtlas INSTANCE = new PageAtlas(8, 8);

    public static final ResourceLocation LOCATION = Clinker.resource("page_atlas");
    final int width, height;
    AdvancedFbo frameBuffer;

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
        this.framesSinceLayoutRendered = new int[size]; Arrays.fill(this.framesSinceLayoutRendered, -1);
        this.layoutPriority = new int[size]; Arrays.fill(this.layoutPriority, Integer.MIN_VALUE);
        this.indicesToRender = new ArrayList<>(size);

        // add the fallback page
        pageLayouts[0] = Page.FALLBACK_LAYOUT;
        indexByLayout.put(Page.FALLBACK_LAYOUT, 0);
        framesSinceLayoutRendered[0] = 0;
        layoutPriority[0] = Integer.MAX_VALUE;
        indicesToRender.add(0);
    }

    private void initFrameBuffer() {
        if (frameBuffer == null || frameBuffer.getWidth() != this.width || frameBuffer.getHeight() != this.height) {
            free();
            this.frameBuffer = AdvancedFbo.withSize(256 * this.width, 256 * this.height)
                    .setFormat(FramebufferAttachmentDefinition.Format.RGBA4)
                    .setFilter(TextureFilter.CLAMP)
                    .setDebugLabel("Page Atlas")
                    .addColorTextureBuffer()
                    .build(true);
        }
    }

    public void tryReserveLayoutLocation(Page.PageLayout layout, int priority, int[] coordinatesOut) {
        if (indexByLayout.containsKey(layout)) {
            // if it's already included, return its location.
            int index = indexByLayout.get(layout);
            coordinatesOut[0] = Math.floorMod(index, width) * 256;
            coordinatesOut[1] = Math.floorDiv(index, height) * 256;
            framesSinceLayoutRendered[index] = 0;
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
            if (framesSinceLayoutRendered[i] > 2 || framesSinceLayoutRendered[i] < 0) {
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
        this.initFrameBuffer();
        VeilRenderSystem.renderer().getFramebufferManager().setFramebuffer(LOCATION, this.frameBuffer);
        PageRenderer.beginPageRenderBatch(this.frameBuffer);
        for (int index = 0; index < this.pageLayouts.length; index++) {
            if (pageLayouts[index] == null) continue;
            int x = Math.floorMod(index, width) * 256,
                y = Math.floorDiv(index, height) * 256;
            PageRenderer.renderPageToAtlas(pageLayouts[index], x, y);
        }
//        for (int index : this.indicesToRender) {
//            int x = Math.floorMod(index, width) * 256,
//                y = Math.floorDiv(index, height) * 256;
//            PageRenderer.renderPageToAtlas(pageLayouts[index], x, y);
//        }
        this.indicesToRender.clear();
        PageRenderer.endPageRenderBatch();

        // update frame times
        for (int i = 0; i < this.framesSinceLayoutRendered.length; i++) {
            // only update frame times of set page layouts
            if (this.framesSinceLayoutRendered[i] >= 0)
                this.framesSinceLayoutRendered[i]++;
        }
    }

    @Override
    public void free() {
        if (this.frameBuffer != null)
            frameBuffer.free();
    }
}
