package birsy.clinker.client.gui.debug;

import birsy.clinker.common.world.level.gen.OthershoreBiomeSource;
import birsy.clinker.common.world.level.gen.system.biome.resolver.LayeredBiomeResolver;
import birsy.clinker.common.world.level.gen.system.biome.resolver.ProtoBiome;
import birsy.clinker.common.world.level.gen.system.noise.SeededNoiseHolder;
import birsy.clinker.common.world.level.gen.system.noise.UncachedNoiseContext;
import birsy.clinker.core.registry.ClinkerRegistries;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.QuartPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BiomeLayerDebugViewScreen extends Screen {
    public static final int MAP_SIZE = 256, HALF_MAP_SIZE = MAP_SIZE / 2;

    private LayeredBiomeResolver resolver;

    private int centerX, centerZ;
    private int newCenterX, newCenterZ;

    private int blocksPerPixel = 2;
    private int viewingLayer = 0;

    private ResourceLocation textureLocation;
    private DynamicTexture texture;
    private boolean dirty = true;

    private boolean dragging;
    private int lastMouseX, lastMouseY;
    private int dragDeltaX, dragDeltaY;

    // threading
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "BiomeDebugBuilder");
        t.setDaemon(true);
        return t;
    });
    private Future<?> currentTask;
    private volatile NativeImage pendingImage;
    private final Object taskLock = new Object();

    public BiomeLayerDebugViewScreen() {
        super(Component.literal("Biome Layer Debug View"));
        createBiomeResolver();
        this.viewingLayer = this.resolver.layerCount - 1;
    }

    public void createBiomeResolver() {
        long seed = 0L;
        PositionalRandomFactory randomFactory = RandomSource.create(seed).forkPositional();
        SeededNoiseHolder holder = new SeededNoiseHolder(randomFactory);
        UncachedNoiseContext noiseContext = new UncachedNoiseContext(holder);
        this.resolver = OthershoreBiomeSource.createSurfaceBiomeResolver((name) -> randomFactory.fromHashOf(name).forkPositional(), noiseContext);
        if (this.viewingLayer >= this.resolver.layerCount) this.viewingLayer = this.resolver.layerCount - 1;
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        if (resolver == null) return;
        if (dirty) rebuildMap();
        if (pendingImage != null) applyBuiltMap();

        renderBackground(gfx, mouseX, mouseY, partialTick);
        gfx.fillGradient(0, 0, width, height, 0x80FFFFFF, 0x80000000);

        int offsetX = this.width / 2 - HALF_MAP_SIZE, offsetY = this.height / 2 - HALF_MAP_SIZE;
        int borderSize = 1;
        gfx.fill(offsetX - borderSize, offsetY - borderSize, offsetX + MAP_SIZE + borderSize, offsetY + MAP_SIZE + borderSize, 0xFF000000);
        // offset uvs by drag delta to make movement smoother
        if (texture != null && textureLocation != null) {
            gfx.blit(textureLocation, offsetX, offsetY, -dragDeltaX, -dragDeltaY, MAP_SIZE, MAP_SIZE, MAP_SIZE, MAP_SIZE);
        }

        gfx.drawCenteredString(font, "epic biomes preview", this.width/2, offsetY - (font.lineHeight + 2) * 2, 0xFFFFFF);
        gfx.drawString(font, "press L to change layers. press R to regenerate.", offsetX, offsetY - (font.lineHeight + 2), 0xFFFFFF);

        int y = offsetY + MAP_SIZE + 2;
        gfx.drawString(font, "center: " + (centerX - dragDeltaX * blocksPerPixel) + ", " + (centerZ - dragDeltaY * blocksPerPixel), offsetX, y, 0xFFFFFF);
        y += font.lineHeight + 2;
        gfx.drawString(font, "zoom: " + blocksPerPixel + " blocks / pixel", offsetX, y, 0xFFFFFF);
        y += font.lineHeight + 2;
        gfx.drawString(font, "layer: " + viewingLayer, offsetX, y, 0xFFFFFF);

        int blockX = centerX + (mouseX - offsetX - HALF_MAP_SIZE) * blocksPerPixel;
        int blockZ = centerZ + (mouseY - offsetY - HALF_MAP_SIZE) * blocksPerPixel;
        gfx.drawString(
                font, blockX + ", " + blockZ,
                mouseX + 10, mouseY + 10,
                0xFFFFFF
        );
        ProtoBiome protoBiome = resolver.getProtoBiome(QuartPos.fromBlock(blockX), QuartPos.fromBlock(blockZ), viewingLayer);
        gfx.drawString(
                font,
                ClinkerRegistries.PROTO_BIOME_REGISTRY.getKey(protoBiome).getPath(),
                mouseX + 10, mouseY + 20,
                0xFFFFFF
        );
    }

    private void rebuildMap() {
        dirty = false;
        synchronized (taskLock) {
            if (currentTask != null && !currentTask.isDone()) {
                currentTask.cancel(true);
            }
            newCenterX = centerX - (dragDeltaX * blocksPerPixel);
            newCenterZ = centerZ - (dragDeltaY * blocksPerPixel);
            currentTask = EXECUTOR.submit(() -> {
                int minBlockX = newCenterX - HALF_MAP_SIZE * blocksPerPixel;
                int maxBlockX = newCenterX + (HALF_MAP_SIZE - 1) * blocksPerPixel;
                int minBlockZ = newCenterZ - HALF_MAP_SIZE * blocksPerPixel;
                int maxBlockZ = newCenterZ + (HALF_MAP_SIZE - 1) * blocksPerPixel;

                int minQX = QuartPos.fromBlock(minBlockX), maxQX = QuartPos.fromBlock(maxBlockX) + 1;
                int minQZ = QuartPos.fromBlock(minBlockZ), maxQZ = QuartPos.fromBlock(maxBlockZ) + 1;
                int areaSizeX = maxQX - minQX;

                if (Thread.currentThread().isInterrupted()) return null;
                ProtoBiome[] area = resolver.getProtoBiomeArea(minQX, minQZ, maxQX, maxQZ, viewingLayer);

                NativeImage image = new NativeImage(MAP_SIZE, MAP_SIZE, false);
                for (int z = 0; z < MAP_SIZE; z++) {
                    if (Thread.currentThread().isInterrupted()) return null;

                    int blockZ = newCenterZ + (z - HALF_MAP_SIZE) * blocksPerPixel;
                    int qZ = QuartPos.fromBlock(blockZ) - minQZ;
                    for (int x = 0; x < MAP_SIZE; x++) {
                        int blockX = newCenterX + (x - HALF_MAP_SIZE) * blocksPerPixel;
                        int qX = QuartPos.fromBlock(blockX) - minQX;

                        ProtoBiome proto = area[qX + qZ * areaSizeX];
                        int color = protoBiomeColor(proto);
                        image.setPixelRGBA(x, z, color);
                    }
                }
                pendingImage = image;
                return null;
            });
        }
    }

    private void applyBuiltMap() {
        if (texture != null) texture.close();
        texture = new DynamicTexture(pendingImage);
        textureLocation = Minecraft.getInstance()
                .getTextureManager()
                .register("biome_debug", texture);
        pendingImage.close();
        pendingImage = null;

        centerX = newCenterX;
        centerZ = newCenterZ;
        dragDeltaX = 0;
        dragDeltaY = 0;
    }

    @Override
    public void onClose() {
        super.onClose();
        synchronized (taskLock) {
            if (currentTask != null) {
                currentTask.cancel(true);
            }
        }
        if (texture != null) {
            texture.close();
            texture = null;
        }
        if (textureLocation != null) {
            Minecraft.getInstance().getTextureManager().release(textureLocation);
            textureLocation = null;
        }
    }

    // input stuff
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            dragging = true;
            lastMouseX = (int) mouseX;
            lastMouseY = (int) mouseY;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        if (!dragging) return false;
        int dX = (int) mouseX - lastMouseX, dY = (int) mouseY - lastMouseY;
        dragDeltaX += dX;
        dragDeltaY += dY;
        lastMouseX = (int) mouseX;
        lastMouseY = (int) mouseY;
        if (dX != 0 && dY != 0) dirty = true;
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int oldZoom = blocksPerPixel;
        if (scrollY > 0) blocksPerPixel = Math.max(1, blocksPerPixel / 2);
        else blocksPerPixel = Math.min(64, blocksPerPixel * 2);
        if (oldZoom != blocksPerPixel) dirty = true;
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_R) {
            createBiomeResolver();
            this.dirty = true;
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_L) {
            viewingLayer = (viewingLayer + 1) % resolver.layerCount;
            this.dirty = true;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private static int protoBiomeColor(ProtoBiome protoBiome) {
        int hash = protoBiome.id * 0x9E3779B9;
        int r = (hash >> 16) & 0xFF;
        int g = (hash >> 8) & 0xFF;
        int b = hash & 0xFF;
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
