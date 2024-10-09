package birsy.clinker.client.render.world;

import birsy.clinker.client.render.ClinkerShaders;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.TickEvent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


// fucking terrible old ass code.
// todo: redo everything
@OnlyIn(Dist.CLIENT)
public class OthershoreDimensionEffects extends DimensionSpecialEffects {
    private final Minecraft mc = Minecraft.getInstance();
    private static final ResourceLocation STAR_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/environment/star.png");
    private static final ResourceLocation NOISE_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/environment/noise.png");
    private static final ResourceLocation CLOUD_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/environment/cloud_map_a.png");
    private static final ResourceLocation FOG_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/environment/fog.png");

    OthershoreSkyRenderer skyRenderer;

    private List<Star> starInfo = null;
    public OthershoreDimensionEffects() {
        super(256.0F, true, SkyType.NORMAL, false, false);
        this.skyRenderer = new OthershoreSkyRenderer(RandomSource.create(1337));
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 pFogColor, float pBrightness) {
        return pFogColor.multiply(pBrightness * 0.94F + 0.06F, pBrightness * 0.94F + 0.06F, pBrightness * 0.91F + 0.09F);
    }

    @Override
    public boolean isFoggyAt(int x, int z) {
        return false;
    }

    @Override
    public boolean renderClouds(ClientLevel level, int ticks, float partialTick, PoseStack pPoseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix) {
        return true;
    }


    @Override
    public void adjustLightmapColors(ClientLevel level, float partialTicks, float skyDarken, float skyLight, float blockLight, int pixelX, int pixelY, Vector3f colors) {
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
        float flicker = 1.0F + MathUtils.awfulRandom(((level.getGameTime() + partialTicks) % 20000.0F)) * 0.1F;
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
    }


    @Override
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        setupFog.run();
        this.skyRenderer.render(level, ticks, partialTick, poseStack, camera, projectionMatrix);
        return true;
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
