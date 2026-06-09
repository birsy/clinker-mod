package birsy.clinker.client.ambience;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public class ExposureTracker {
    private final Minecraft minecraft;
    private float exposureFactor, prevExposureFactor = 1.0F;
    private float frameBonus;

    public ExposureTracker(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public void frame(boolean shouldUpdate) {
        if (!shouldUpdate) return;
        Camera camera = minecraft.gameRenderer.getMainCamera();

        frameBonus = 0;
        if (camera.getFluidInCamera() == FogType.LAVA) frameBonus -= 1.0F;
        if (camera.getFluidInCamera() == FogType.POWDER_SNOW) frameBonus -= 1.0F;
        if (camera.getFluidInCamera() == FogType.WATER) frameBonus -= 0.5F;
    }

    private static final int RAY_LENGTH = 12;
    public void tick(boolean shouldUpdate) {
        prevExposureFactor = exposureFactor;
        if (!shouldUpdate) return;

        ClientLevel level = minecraft.level;
        if (level == null) return;

        Camera camera = minecraft.gameRenderer.getMainCamera();
        Vec3 camPos = camera.getPosition();
        // cast a single ray in a random horizontal direction
        double angle = level.random.nextDouble() * Math.PI * 2;
        double dX = Math.cos(angle), dY = Math.abs(level.random.nextGaussian() * 0.5), dZ = Math.sin(angle);
        double length = Mth.length(dX, dY, dZ);
        dX /= length; dY /= length; dZ /= length;

        int rayLength = 24;
        BlockHitResult raycast = level.clip(
                new ClipContext(
                        camPos,
                        camPos.add(dX * RAY_LENGTH, dY * rayLength, dZ * rayLength),
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        CollisionContext.empty()
                )
        );
        float target = 3.0F;
        if (raycast.isInside()) {
            target = -1.0F;
        } else if (raycast.getType() == HitResult.Type.BLOCK) {
            target = Mth.clampedMap((float) raycast.getLocation().distanceTo(camPos), rayLength * 0.25F, rayLength, -1.0F, 3.0F);
        }
        boolean underBlock = camPos.y() < level.getHeight(Heightmap.Types.WORLD_SURFACE, (int) camPos.x(), (int) camPos.z());
        target += underBlock ? -0.5F : 0.5F;

        float mixFactor = 2.0F / 64.0F;
        exposureFactor = Mth.lerp(mixFactor, exposureFactor, target);
    }

    public float getExposureFactor(float partialTick) {
        return Mth.clamp(Mth.lerp(partialTick, prevExposureFactor, exposureFactor) + frameBonus, -1.0F, 1.0F);
    }
}
