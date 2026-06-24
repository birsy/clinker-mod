package birsy.clinker.client.render.world.cloud;

import birsy.clinker.client.render.world.OthershoreDimensionEffects;
import birsy.clinker.common.world.level.weather.OthershoreWeatherSystem;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.block.DynamicShaderBlock;
import foundry.veil.api.client.render.shader.block.ShaderBlock;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = Clinker.MOD_ID, value = Dist.CLIENT)
public class CloudHoleTracker {
    // static stuff
    private static CloudHoleTracker HOLE_TRACKER;
    @Nullable public static CloudHoleTracker getInstance() { return HOLE_TRACKER; }
    @SubscribeEvent
    static void onEnterLevel(LevelEvent.Load event) {
        if (event.getLevel() instanceof ClientLevel level && level.effects() instanceof OthershoreDimensionEffects) {
            HOLE_TRACKER = new CloudHoleTracker();
        }
    }
    @SubscribeEvent
    static void onLeaveLevel(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ClientLevel) {
            if (HOLE_TRACKER != null) HOLE_TRACKER.free();
            HOLE_TRACKER = null;
        }
    }
    @SubscribeEvent
    public static void tickHoleTracker(LevelTickEvent.Post event) {
        if (!event.getLevel().isClientSide()) return;
        CloudHoleTracker tracker = getInstance();
        if (tracker != null) tracker.tick();
    }

    // instance stuff
    public static final int MAX_CLOUD_HOLES = 64;
    private final CloudHole[] holesArray = new CloudHole[MAX_CLOUD_HOLES];
    private final List<CloudHoleHandle> handles = new ArrayList<>();

    private DynamicShaderBlock<CloudHoleTracker> cloudHolesShaderBlock;
    private int cloudHolesCount;

    boolean dirty = true;

    public CloudHoleHandle createHandle(CloudHoleType type, double x, double z, float radius) {
        CloudHoleHandle handle = new CloudHoleHandle(this, new CloudHole(type, x, z, radius));
        handle.setPosition(x, z);
        handles.add(handle);
        dirty = true;
        return handle;
    }

    void tick() {
        for (CloudHoleHandle handle : handles) {
            if (!handle.freed) handle.tick();
            else dirty = true;
        }
        handles.removeIf(handle -> handle.freed);
    }

    public void updateFrame(float partialTicks) {
        if (!dirty) {
            for (CloudHoleHandle handle : handles) {
                if (handle.isDirty()) {
                    dirty = true;
                    break;
                }
            }
        }
        if (!dirty) return;

        cloudHolesCount = Math.min(handles.size(), MAX_CLOUD_HOLES);
        for (int i = 0; i < cloudHolesCount; i++) {
            CloudHoleHandle handle = handles.get(i);
            handle.updateData(partialTicks);
            holesArray[i] = handle.hole;
        }

        if (cloudHolesShaderBlock == null) {
            int size = Integer.BYTES + CloudHole.SIZE * MAX_CLOUD_HOLES;
            cloudHolesShaderBlock = ShaderBlock.dynamic(
                    ShaderBlock.BufferBinding.SHADER_STORAGE, size,
                    (tracker, buf) -> {
                        // count first
                        buf.putInt(tracker.cloudHolesCount);
                        // all the cloud holes
                        for (int i = 0; i < tracker.cloudHolesCount; i++) tracker.holesArray[i].upload(buf);
                        // remaining padding
                        int remaining = MAX_CLOUD_HOLES - tracker.cloudHolesCount;
                        buf.position(buf.position() + remaining * CloudHole.SIZE);
                    }
            );
        }
        cloudHolesShaderBlock.set(this);
        dirty = false;
    }

    public void bind() {
        if (cloudHolesShaderBlock != null) VeilRenderSystem.bind("CloudHoles", cloudHolesShaderBlock);
    }

    public void free() {
        if (cloudHolesShaderBlock != null) {
            cloudHolesShaderBlock.close();
            cloudHolesShaderBlock = null;
        }
        handles.clear();
    }

    public static class CloudHoleHandle {
        final CloudHoleTracker tracker;
        final CloudHole hole;

        double x, z, pX, pZ;
        float startRadius, endRadius;
        MathUtils.EasingType easeType = MathUtils.EasingType.linear;
        float easeSpeed, easeProgress, pEaseProgress;

        boolean shouldFreeOnCompletion = false, freed = false;

        CloudHoleHandle(CloudHoleTracker tracker, CloudHole hole) {
            this.tracker = tracker;
            this.hole = hole;
        }

        public void tick() {
            pX = x; pZ = z;
            pEaseProgress = easeProgress;
            if (easeProgress < 1.0F) easeProgress = Math.clamp(easeProgress + easeSpeed, 0.0F, 1.0F);
            else if (easeProgress >= 1.0F && shouldFreeOnCompletion) this.free();
        }

        boolean isDirty() {
            return pX != x || pZ != z || pEaseProgress != easeProgress;
        }

        void updateData(float partialTicks) {
            hole.setPos(getX(partialTicks), getZ(partialTicks));
            hole.radius = getRadius(partialTicks);
        }

        public void freeOnCompletion() {
            this.shouldFreeOnCompletion = true;
        }
        public void setRadius(float desiredRadius, MathUtils.EasingType easeType, int transitionTimeInTicks) {
            tracker.dirty = true;
            if (transitionTimeInTicks == 0) {
                this.startRadius = desiredRadius;
                this.endRadius = desiredRadius;
                this.pEaseProgress = 1.0F;
                this.easeProgress = 1.0F;
                this.easeSpeed = 1.0F;
                return;
            }

            this.startRadius = getRadius(1.0F);
            this.endRadius = desiredRadius;
            this.easeProgress = 0;
            this.easeType = easeType;
            this.easeSpeed = 1.0F / transitionTimeInTicks;
        }
        public float getRadius(float partialTicks) { return Mth.lerp(MathUtils.ease(Mth.lerp(partialTicks, pEaseProgress, easeProgress), easeType), startRadius, endRadius); }

        public void setPosition(double x, double z) { this.x = x; this.z = z; }
        public double getX(float partialTicks) { return Mth.lerp(partialTicks, pX, x); }
        public double getZ(float partialTicks) { return Mth.lerp(partialTicks, pZ, z); }
        public void free() { this.freed = true; }
    }

    public enum CloudHoleType {
        UNINITIALIZED(0), NORMAL(1), BEACON(2);
        final int index;
        CloudHoleType(int index) { this.index = index; }
    }

    static class CloudHole {
        static final int SIZE = Integer.BYTES * 3 + Float.BYTES;
        final CloudHoleType type;
        int x, z; // fixed point! 26 bit integer component, 6 bit fractional component
        float radius;

        public CloudHole(CloudHoleType type, double x, double z, float radius) {
            this.type = type;
            this.setPos(x, z);
            this.radius = radius;
        }

        public void upload(ByteBuffer buffer) {
            buffer.putInt(type.index);
            buffer.putInt(x);
            buffer.putInt(z);
            buffer.putFloat(radius);
        }

        public void setPos(double x, double z) {
            this.x = (int) Math.round(x * 64.0);
            this.z = (int) Math.round(z * 64.0);
        }
    }
}
