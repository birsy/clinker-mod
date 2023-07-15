package birsy.clinker.client;

import birsy.clinker.core.Clinker;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.HumanoidArm;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;

public class ClinkerCursor {
    private static long[] CURSORS = new long[4];
    private static long[] LEFT_CURSORS = new long[4];
    private static boolean loadedCursors = false;
    private static int currentCursor = -1;

    private static final ResourceLocation IDLE = new ResourceLocation(Clinker.MOD_ID, "textures/gui/cursor/cursor_idle.png");
    private static final ResourceLocation GRAB = new ResourceLocation(Clinker.MOD_ID, "textures/gui/cursor/cursor_grab.png");
    private static final ResourceLocation HOVER = new ResourceLocation(Clinker.MOD_ID, "textures/gui/cursor/cursor_hover.png");
    private static final ResourceLocation BIRD = new ResourceLocation(Clinker.MOD_ID, "textures/gui/cursor/cursor_bird.png");
    private static final ResourceLocation[] CURSOR_LOCATIONS = new ResourceLocation[]{IDLE, HOVER, GRAB, BIRD};
    private static final ResourceLocation LEFT_IDLE = new ResourceLocation(Clinker.MOD_ID, "textures/gui/cursor/cursor_left_idle.png");
    private static final ResourceLocation LEFT_GRAB = new ResourceLocation(Clinker.MOD_ID, "textures/gui/cursor/cursor_left_grab.png");
    private static final ResourceLocation LEFT_HOVER = new ResourceLocation(Clinker.MOD_ID, "textures/gui/cursor/cursor_left_hover.png");
    private static final ResourceLocation LEFT_BIRD = new ResourceLocation(Clinker.MOD_ID, "textures/gui/cursor/cursor_left_bird.png");
    private static final ResourceLocation[] LEFT_CURSOR_LOCATIONS = new ResourceLocation[]{LEFT_IDLE, LEFT_HOVER, LEFT_GRAB, LEFT_BIRD};

    public static void init() throws IOException {
        try {
            if (loadedCursors) {
                for (long cursor : CURSORS) {
                    //GLFW.glfwDestroyCursor(cursor);
                }
                for (long cursor : LEFT_CURSORS) {
                    //GLFW.glfwDestroyCursor(cursor);
                }
            }

            ResourceManager resourcemanager = Minecraft.getInstance().getResourceManager();

            for (int i = 0; i < CURSOR_LOCATIONS.length; i++) {
                ResourceLocation cursorLocation = CURSOR_LOCATIONS[i];
                Optional<Resource> resource = resourcemanager.getResource(cursorLocation);

                ByteBuffer bytes = ByteBuffer.wrap(resource.get().open().readAllBytes());
                GLFWImage image = new GLFWImage(bytes);
                //CURSORS[i] = GLFW.glfwCreateCursor(image, 0, 0);
            }

            for (int i = 0; i < LEFT_CURSOR_LOCATIONS.length; i++) {
                ResourceLocation cursorLocation = LEFT_CURSOR_LOCATIONS[i];
                ByteBuffer bytes = ByteBuffer.wrap(resourcemanager.getResource(cursorLocation).get().open().readAllBytes());
                GLFWImage image = new GLFWImage(bytes);
                //LEFT_CURSORS[i] = GLFW.glfwCreateCursor(image, 0, 0);
            }

            loadedCursors = true;
        } catch (IOException e) {
            Clinker.LOGGER.warn("Failed to load custom cursors!");
            loadedCursors = false;
        }
    }

    public static long getCursor(CursorState state) {
        if (state == CursorState.NONE) return MemoryUtil.NULL;
        return Minecraft.getInstance().options.mainHand().get() == HumanoidArm.LEFT ? LEFT_CURSORS[state.ordinal()] : CURSORS[state.ordinal()];
    }

    public static void setCursor(CursorState state) {
        long cursor;
        switch (state) {
            case IDLE -> cursor = GLFW.GLFW_HAND_CURSOR;
            case HOVER -> cursor = GLFW.GLFW_IBEAM_CURSOR;
            case GRAB -> cursor = GLFW.GLFW_CROSSHAIR_CURSOR;
            default -> cursor = 0L;
        }

        GLFW.glfwSetCursor(Minecraft.getInstance().getWindow().getWindow(), cursor);
    }

    public enum CursorState {
        IDLE, HOVER, GRAB, BIRD, NONE;
    }
}
