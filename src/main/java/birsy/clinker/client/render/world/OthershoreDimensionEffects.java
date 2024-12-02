package birsy.clinker.client.render.world;

import birsy.clinker.core.util.MathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;



public class OthershoreDimensionEffects extends DimensionSpecialEffects {
    private final Minecraft mc = Minecraft.getInstance();

    OthershoreSkyRenderer skyRenderer;
    OthershoreCloudRenderer cloudRenderer;

    public OthershoreDimensionEffects() {
        super(256.0F, true, SkyType.NORMAL, false, false);
        this.skyRenderer = new OthershoreSkyRenderer(RandomSource.create(1337));
        this.cloudRenderer = new OthershoreCloudRenderer();
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 pFogColor, float pBrightness) {
        return pFogColor.multiply(pBrightness * 0.94F + 0.06F, pBrightness * 0.94F + 0.06F, pBrightness * 0.91F + 0.09F);
    }

    @Override
    public boolean isFoggyAt(int x, int z) {
        return false;
    }

    private Vector3f skyColor = new Vector3f();
    public Vector3fc getSkyColor(ClientLevel level, Vec3 pPos, float pPartialTick) {
        Vec3 pos = pPos.subtract(2.0, 2.0, 2.0).scale(0.25);
        BiomeManager biomemanager = level.getBiomeManager();
        Vec3 interpolatedSkyColor = CubicSampler.gaussianSampleVec3(
                pos, (x, y, z) -> Vec3.fromRGB24(biomemanager.getNoiseBiomeAtQuart(x, y, z).value().getSkyColor())
        );
        float r = (float)interpolatedSkyColor.x;
        float g = (float)interpolatedSkyColor.y;
        float b = (float)interpolatedSkyColor.z;

        int i = level.getSkyFlashTime();
        if (i > 0) {
            float lightningFlicker = i - pPartialTick;
            if (lightningFlicker > 1.0F) lightningFlicker = 1.0F;

            lightningFlicker *= 0.45F;
            r = r * (1.0F - lightningFlicker) + 0.8F * lightningFlicker;
            g = g * (1.0F - lightningFlicker) + 0.8F * lightningFlicker;
            b = b * (1.0F - lightningFlicker) + lightningFlicker;
        }

        return skyColor.set(r, g, b);
    }

    @Override
    public boolean renderClouds(ClientLevel level, int ticks, float partialTick, PoseStack pPoseStack, double camX, double camY, double camZ, Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
        Minecraft.getInstance().getProfiler().push("clinker.drawClouds");
        this.cloudRenderer.render(level, ticks, partialTick, pPoseStack, camX, camY, camZ, projectionMatrix, this.getSkyColor(level, new Vec3(camX, camY, camZ), partialTick));
        Minecraft.getInstance().getProfiler().pop();
        return true;
    }

    @Override
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, Matrix4f modelViewMatrix, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        Minecraft.getInstance().getProfiler().push("clinker.drawSky");
        setupFog.run();
        this.skyRenderer.render(level, ticks, partialTick, new PoseStack(), camera, projectionMatrix, this.getSkyColor(level, camera.getPosition(), partialTick));
        Minecraft.getInstance().getProfiler().pop();
        return true;
    }

    @Override
    public void adjustLightmapColors(ClientLevel level, float partialTicks, float skyDarken, float skyLight, float blockLight, int pixelX, int pixelY, Vector3f colors) {
        Minecraft.getInstance().getProfiler().push("clinker.updateLightmap");
        // skylight, between 1 and 0 so it's easier to work with.
        float skyL = (float) pixelY / 15.0F;
        // ditto
        float blockL = (float) pixelX / 15.0F;

        //colors.set(Math.max(skyL, blockL), Math.max(skyL, blockL), Math.max(skyL, blockL));


        // Start off with an orange base
        Vector3f blockLightColor = new Vector3f(255.0F / 255.0F, 96.0F / 255.0F, 0.0F / 255.0F);
        // Tone down the orange a tad.
        blockLightColor.lerp(new Vector3f(1, 1, 1), 0.1F);
        // Fade it to black...
        float b1 = blockL;
        b1 = (float) Math.pow(b1, 1.8F);
        b1 *= 0.6;
        blockLightColor.mul(b1);
        // Add some white to it.
        float b2 = blockL;
        b2 = (float) Math.pow(b2, 15.0F);
        // torch flicker
        float flicker = 1.0F + MathUtil.awfulRandom(((level.getGameTime() + partialTicks) % 20000.0F)) * 0.1F;
        b2 *= flicker;
        b2 *= 8.0;
        blockLightColor.add(b2, b2, b2 * 0.5F);

        // The othershore's default skylight color. Quite dark! (it has no sun, so makes sense.)
        Vector3f skyLightColor = new Vector3f(75.0F / 255.0F, 65.0F / 255.0F, 59.0F / 255.0F);
        // some math to make it all a little smoother and fancy looking. just kept adding functions until it looked good.
        skyLightColor.lerp(new Vector3f(0.0f, 0.0f, 1.0f), (float) Math.pow((1 - skyL), 1.1));
        skyL = ((1 / ((1.5F * skyL * skyL * skyL) - 2)) + 2) / 1.5F;
        skyLightColor.lerp(new Vector3f(), skyL);
        skyLightColor.mul(skyDarken);

        colors.set(blockLightColor.x() + skyLightColor.x(), blockLightColor.y() + skyLightColor.y(), blockLightColor.z() + skyLightColor.z());
        float ambientBrightness = 0.02F;
        if (mc.player.hasEffect(MobEffects.NIGHT_VISION)) {
            float nvScale = GameRenderer.getNightVisionScale(mc.player, partialTicks);
            colors.add(ambientBrightness * nvScale, ambientBrightness * nvScale, ambientBrightness);
        } else {
            colors.add(ambientBrightness * 0.8F, ambientBrightness * 0.8F, ambientBrightness);
        }
        Minecraft.getInstance().getProfiler().pop();
    }

    @Override
    public boolean renderSnowAndRain(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ) {
        return true;
    }

    @Override
    public boolean tickRain(ClientLevel level, int ticks, Camera camera) {
        return false;
    }
}
