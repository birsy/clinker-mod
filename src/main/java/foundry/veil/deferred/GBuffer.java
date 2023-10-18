package foundry.veil.deferred;

import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class GBuffer extends MainTarget {
    private final List<BufferTexture> textures = Util.make(() -> {
        ArrayList<BufferTexture> list = new ArrayList<>();
        list.add(new BufferTexture("albedo") {
            @Override
            protected void setup(int width, int height, int index) {
                glBindTexture(GL_TEXTURE_2D, this.textureID);
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, width, height, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
                int attachmentType = GL_COLOR_ATTACHMENT0 + index;
                glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

                glFramebufferTexture2D(GL_FRAMEBUFFER, attachmentType, GL_TEXTURE_2D, this.textureID, 0);
            }
        });
        list.add(new BufferTexture("light") {
            @Override
            protected void setup(int width, int height, int index) {
                glBindTexture(GL_TEXTURE_2D, this.textureID);
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, width, height, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
                int attachmentType = GL_COLOR_ATTACHMENT0 + index;
                glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

                glFramebufferTexture2D(GL_FRAMEBUFFER, attachmentType, GL_TEXTURE_2D, this.textureID, 0);
            }
        });
        list.add(new BufferTexture("light_uv") {
            @Override
            protected void setup(int width, int height, int index) {
                glBindTexture(GL_TEXTURE_2D, this.textureID);
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RG16F, width, height, 0, GL_RG, GL_FLOAT, (ByteBuffer) null);
                int attachmentType = GL_COLOR_ATTACHMENT0 + index;
                glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

                glFramebufferTexture2D(GL_FRAMEBUFFER, attachmentType, GL_TEXTURE_2D, this.textureID, 0);
            }
        });
        list.add(new BufferTexture("normal") {
            @Override
            protected void setup(int width, int height, int index) {
                glBindTexture(GL_TEXTURE_2D, this.textureID);
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, width, height, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
                int attachmentType = GL_COLOR_ATTACHMENT0 + index;
                glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

                glFramebufferTexture2D(GL_FRAMEBUFFER, attachmentType, GL_TEXTURE_2D, this.textureID, 0);
            }
        });
        list.add(new BufferTexture("position") {
            @Override
            protected void setup(int width, int height, int index) {
                glBindTexture(GL_TEXTURE_2D, this.textureID);
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, width, height, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
                int attachmentType = GL_COLOR_ATTACHMENT0 + index;
                glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

                glFramebufferTexture2D(GL_FRAMEBUFFER, attachmentType, GL_TEXTURE_2D, this.textureID, 0);
            }
        });
        return list;
    });

    private int gBufferId;

    public GBuffer(int pWidth, int pHeight) {
        super(pWidth, pHeight);
        RenderSystem.assertOnRenderThreadOrInit();

        gBufferId = glGenFramebuffers();
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, gBufferId);

        int[] textureIds = new int[textures.size()];
        glGenTextures(textureIds);
        for (int i = 0; i < textureIds.length; i++) {
            textures.get(i).textureID = textureIds[i];
        }

        for (int i = 0; i < textures.size(); i++) {
            textures.get(i).setup(this.width, this.height, i);
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer intBuff = stack.mallocInt(textures.size());
            for (int i = 0; i < textures.size(); i++) {
                intBuff.put(i, GL_COLOR_ATTACHMENT0 + i);
            }
            glDrawBuffers(intBuff);
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }


    @Override
    public void createBuffers(int pWidth, int pHeight, boolean pClearError) {
        super.createBuffers(pWidth, pHeight, pClearError);
        gBufferId = glGenFramebuffers();
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, gBufferId);

        int[] textureIds = new int[textures.size()];
        glGenTextures(textureIds);
        for (int i = 0; i < textureIds.length; i++) {
            textures.get(i).textureID = textureIds[i];
        }

        for (int i = 0; i < textures.size(); i++) {
            textures.get(i).setup(this.width, this.height, i);
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer intBuff = stack.mallocInt(textures.size());
            for (int i = 0; i < textures.size(); i++) {
                intBuff.put(i, GL_COLOR_ATTACHMENT0 + i);
            }
            glDrawBuffers(intBuff);
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public void destroyBuffers() {
        super.destroyBuffers();
        for (int i = 0; i < textures.size(); i++) {
            glDeleteTextures(textures.get(i).textureID);
            textures.get(i).textureID = -1;
        }
        glDeleteFramebuffers(gBufferId);
        gBufferId = -1;
    }

    public static abstract class BufferTexture {
        int textureID;
        final String name;

        protected BufferTexture(String name) {
            this.name = name;
        }

        protected abstract void setup(int width, int height, int index);
    }
}
