package birsy.clinker.mixin.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.renderer.block.LiquidBlockRenderer.shouldRenderFace;

@Mixin(LiquidBlockRenderer.class)
public abstract class LiquidBlockRendererMixin {
    private static int[][] LIGHT_SAMPLE_OFFSETS = {
            { 0, 0, 0},
            {-1, 0, 0},
            { 0,-1, 0},
            {-1,-1, 0},
            { 0, 0,-1},
            {-1, 0,-1},
            { 0,-1,-1},
            {-1,-1,-1},
    };
    @Shadow protected abstract float getHeight(BlockAndTintGetter p_203157_, Fluid p_203158_, BlockPos p_203159_);

    @Shadow protected abstract float getHeight(BlockAndTintGetter p_203161_, Fluid p_203162_, BlockPos p_203163_, BlockState p_203164_, FluidState pState);

    @Shadow
    protected static boolean isNeighborSameFluid(FluidState pFirstState, FluidState pSecondState) {return false;}

    @Shadow
    protected static boolean isFaceOccludedByNeighbor(BlockGetter pLevel, BlockPos pPos, Direction pSide, float pHeight, BlockState pBlockState) {
        return false;
    }

    @Shadow protected abstract float calculateAverageHeight(BlockAndTintGetter p_203150_, Fluid p_203151_, float p_203152_, float p_203153_, float p_203154_, BlockPos p_203155_);

    private void vertex(VertexConsumer pConsumer, double pX, double pY, double pZ, float pRed, float pGreen, float pBlue, float alpha, float pU, float pV, float nX, float nY, float nZ, int pPackedLight) {
        pConsumer.vertex(pX, pY, pZ).color(pRed, pGreen, pBlue, alpha).uv(pU, pV).uv2(pPackedLight).normal(nX, nY, nZ).endVertex();
    }

    private int getLightLevel(int baseLight, BlockPos pos, BlockAndTintGetter level) {
        if (!Minecraft.getInstance().useAmbientOcclusion()) return baseLight;

        int maxBLight = 0;
        int maxSLight = 0;
        for (int[] offset : LIGHT_SAMPLE_OFFSETS) {
            BlockPos pos2 = pos.offset(offset[0],offset[1],offset[2]);
            maxBLight = maxBLight + level.getBrightness(LightLayer.BLOCK, pos2);
            maxSLight = maxSLight + level.getBrightness(LightLayer.SKY, pos2);
        }
        return LightTexture.pack(maxBLight / 8, maxSLight / 8);
    }


    @Shadow protected abstract int getLightColor(BlockAndTintGetter pLevel, BlockPos pPos);

    @Shadow private TextureAtlasSprite waterOverlay;

    @Inject(method = "tesselate(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V", at = @At("HEAD"), cancellable = true)
    private void tesselate(BlockAndTintGetter pLevel, BlockPos pPos, VertexConsumer pVertexConsumer, BlockState pBlockState, FluidState pFluidState, CallbackInfo ci) {
        ci.cancel();
        this.tes(pLevel, pPos, pVertexConsumer, pBlockState, pFluidState);
    }

    private void tes(BlockAndTintGetter pLevel, BlockPos pPos, VertexConsumer pVertexConsumer, BlockState pBlockState, FluidState pFluidState) {
        float glow = (float)(pBlockState.getLightEmission()) / 15.0F;
        TextureAtlasSprite[] fluidAtlas = net.minecraftforge.client.ForgeHooksClient.getFluidSprites(pLevel, pPos, pFluidState);
        int tintColor = net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions.of(pFluidState).getTintColor(pFluidState, pLevel, pPos);
        float alpha = (float) (tintColor >> 24 & 255) / 255.0F;
        float red = (float) (tintColor >> 16 & 255) / 255.0F;
        float green = (float) (tintColor >> 8 & 255) / 255.0F;
        float blue = (float) (tintColor & 255) / 255.0F;
        BlockState downState = pLevel.getBlockState(pPos.relative(Direction.DOWN));
        FluidState downFluidState = downState.getFluidState();
        BlockState upState = pLevel.getBlockState(pPos.relative(Direction.UP));
        FluidState upFluidState = upState.getFluidState();
        BlockState northState = pLevel.getBlockState(pPos.relative(Direction.NORTH));
        FluidState northFluidState = northState.getFluidState();
        BlockState southState = pLevel.getBlockState(pPos.relative(Direction.SOUTH));
        FluidState southFluidState = southState.getFluidState();
        BlockState westState = pLevel.getBlockState(pPos.relative(Direction.WEST));
        FluidState westFluidState = westState.getFluidState();
        BlockState eastState = pLevel.getBlockState(pPos.relative(Direction.EAST));
        FluidState eastFluidState = eastState.getFluidState();

        boolean renderAbove = !isNeighborSameFluid(pFluidState, upFluidState);
        boolean renderBelow = shouldRenderFace(pLevel, pPos, pFluidState, pBlockState, Direction.DOWN, downFluidState) && !isFaceOccludedByNeighbor(pLevel, pPos, Direction.DOWN, 0.8888889F, downState);
        boolean renderNorth = shouldRenderFace(pLevel, pPos, pFluidState, pBlockState, Direction.NORTH, northFluidState);
        boolean renderSouth = shouldRenderFace(pLevel, pPos, pFluidState, pBlockState, Direction.SOUTH, southFluidState);
        boolean renderWest = shouldRenderFace(pLevel, pPos, pFluidState, pBlockState, Direction.WEST, westFluidState);
        boolean renderEast = shouldRenderFace(pLevel, pPos, pFluidState, pBlockState, Direction.EAST, eastFluidState);

        int thisLight = this.getLightColor(pLevel, pPos);
//        int downNorthWestLight = thisLight;
//        int downNorthEastLight = getLightLevel(thisLight, pPos.offset(1, 0, 0), pLevel);
//        int downSouthWestLight = getLightLevel(thisLight, pPos.offset(0, 0, 1), pLevel);
//        int downSouthEastLight = getLightLevel(thisLight, pPos.offset(1, 0, 1), pLevel);
        int upNorthWestLight = getLightLevel(thisLight, pPos.offset(0, 1, 0), pLevel);
        int upNorthEastLight = getLightLevel(thisLight, pPos.offset(1, 1, 0), pLevel);
        int upSouthWestLight = getLightLevel(thisLight, pPos.offset(0, 1, 1), pLevel);
        int upSouthEastLight = getLightLevel(thisLight, pPos.offset(1, 1, 1), pLevel);

        if (renderAbove || renderBelow || renderEast || renderWest || renderNorth || renderSouth) {
            float downShading = Mth.lerp(glow, pLevel.getShade(Direction.DOWN, true), 1);
            float upShading = Mth.lerp(glow, pLevel.getShade(Direction.UP, true), 1);
            float northShading = Mth.lerp(glow, pLevel.getShade(Direction.NORTH, true), 1);
            float westShading = Mth.lerp(glow, pLevel.getShade(Direction.WEST, true), 1);
            Fluid fluid = pFluidState.getType();
            float height = this.getHeight(pLevel, fluid, pPos, pBlockState, pFluidState);
            float northEastHeight;
            float northWestHeight;
            float southEastHeight;
            float southWestHeight;
            if (height >= 1.0F) {
                northEastHeight = 1.0F;
                northWestHeight = 1.0F;
                southEastHeight = 1.0F;
                southWestHeight = 1.0F;
            } else {
                float northHeight = this.getHeight(pLevel, fluid, pPos.north(), northState, northFluidState);
                float southHeight = this.getHeight(pLevel, fluid, pPos.south(), southState, southFluidState);
                float eastHeight = this.getHeight(pLevel, fluid, pPos.east(), eastState, eastFluidState);
                float westHeight = this.getHeight(pLevel, fluid, pPos.west(), westState, westFluidState);
                northEastHeight = this.calculateAverageHeight(pLevel, fluid, height, northHeight, eastHeight, pPos.relative(Direction.NORTH).relative(Direction.EAST));
                northWestHeight = this.calculateAverageHeight(pLevel, fluid, height, northHeight, westHeight, pPos.relative(Direction.NORTH).relative(Direction.WEST));
                southEastHeight = this.calculateAverageHeight(pLevel, fluid, height, southHeight, eastHeight, pPos.relative(Direction.SOUTH).relative(Direction.EAST));
                southWestHeight = this.calculateAverageHeight(pLevel, fluid, height, southHeight, westHeight, pPos.relative(Direction.SOUTH).relative(Direction.WEST));
            }

            double localX = pPos.getX() & 15;
            double localY = pPos.getY() & 15;
            double localZ = pPos.getZ() & 15;
            float epsilon = 0.001F;
            float yOffset = renderBelow ? epsilon : 0.0F;
            if (renderAbove && !isFaceOccludedByNeighbor(pLevel, pPos, Direction.UP, Math.min(Math.min(northWestHeight, southWestHeight), Math.min(southEastHeight, northEastHeight)), upState)) {
                northWestHeight -= 0.001F;
                southWestHeight -= 0.001F;
                southEastHeight -= 0.001F;
                northEastHeight -= 0.001F;
                Vec3 vec3 = pFluidState.getFlow(pLevel, pPos);
                float uv1;
                float uv2;
                float uv3;
                float uv4;
                float uv5;
                float uv6;
                float uv7;
                float uv8;
                if (vec3.x == 0.0D && vec3.z == 0.0D) {
                    TextureAtlasSprite sprite = fluidAtlas[0];
                    uv1 = sprite.getU(0.0D);
                    uv5 = sprite.getV(0.0D);
                    uv2 = uv1;
                    uv6 = sprite.getV(16.0D);
                    uv3 = sprite.getU(16.0D);
                    uv7 = uv6;
                    uv4 = uv3;
                    uv8 = uv5;
                } else {
                    TextureAtlasSprite textureatlassprite = fluidAtlas[1];
                    float spriteLength = (float) Mth.atan2(vec3.z, vec3.x) - ((float) Math.PI / 2F);
                    float f27 = Mth.sin(spriteLength) * 0.25F;
                    float f28 = Mth.cos(spriteLength) * 0.25F;
                    float f29 = 8.0F;
                    uv1 = textureatlassprite.getU(f29 + (-f28 - f27) * 16.0F);
                    uv5 = textureatlassprite.getV(f29 + (-f28 + f27) * 16.0F);
                    uv2 = textureatlassprite.getU(f29 + (-f28 + f27) * 16.0F);
                    uv6 = textureatlassprite.getV(f29 + (f28 + f27) * 16.0F);
                    uv3 = textureatlassprite.getU(f29 + (f28 + f27) * 16.0F);
                    uv7 = textureatlassprite.getV(f29 + (f28 - f27) * 16.0F);
                    uv4 = textureatlassprite.getU(f29 + (f28 - f27) * 16.0F);
                    uv8 = textureatlassprite.getV(f29 + (-f28 - f27) * 16.0F);
                }

                float f49 = (uv1 + uv2 + uv3 + uv4) / 4.0F;
                float f50 = (uv5 + uv6 + uv7 + uv8) / 4.0F;
                float f51 = (float) fluidAtlas[0].getWidth() / (fluidAtlas[0].getU1() - fluidAtlas[0].getU0());
                float f52 = (float) fluidAtlas[0].getHeight() / (fluidAtlas[0].getV1() - fluidAtlas[0].getV0());
                float f53 = 4.0F / Math.max(f52, f51);
                uv1 = Mth.lerp(f53, uv1, f49);
                uv2 = Mth.lerp(f53, uv2, f49);
                uv3 = Mth.lerp(f53, uv3, f49);
                uv4 = Mth.lerp(f53, uv4, f49);
                uv5 = Mth.lerp(f53, uv5, f50);
                uv6 = Mth.lerp(f53, uv6, f50);
                uv7 = Mth.lerp(f53, uv7, f50);
                uv8 = Mth.lerp(f53, uv8, f50);

                float fluidR = upShading * red;
                float fluidG = upShading * green;
                float fluidB = upShading * blue;

                float normalX = 0;
                float normalY = 1;
                float normalZ = 0;

                this.vertex(pVertexConsumer, localX + 0.0D, localY + (double) northWestHeight, localZ + 0.0D, fluidR, fluidG, fluidB, alpha, uv1, uv5, normalX, normalY, normalZ, upNorthWestLight);
                this.vertex(pVertexConsumer, localX + 0.0D, localY + (double) southWestHeight, localZ + 1.0D, fluidR, fluidG, fluidB, alpha, uv2, uv6, normalX, normalY, normalZ, upSouthWestLight);
                this.vertex(pVertexConsumer, localX + 1.0D, localY + (double) southEastHeight, localZ + 1.0D, fluidR, fluidG, fluidB, alpha, uv3, uv7, normalX, normalY, normalZ, upSouthEastLight);
                this.vertex(pVertexConsumer, localX + 1.0D, localY + (double) northEastHeight, localZ + 0.0D, fluidR, fluidG, fluidB, alpha, uv4, uv8, normalX, normalY, normalZ, upNorthEastLight);
                if (pFluidState.shouldRenderBackwardUpFace(pLevel, pPos.above())) {
                    this.vertex(pVertexConsumer, localX + 0.0D, localY + (double) northWestHeight, localZ + 0.0D, fluidR, fluidG, fluidB, alpha, uv1, uv5, normalX, -normalY, normalZ, upNorthWestLight);
                    this.vertex(pVertexConsumer, localX + 1.0D, localY + (double) northEastHeight, localZ + 0.0D, fluidR, fluidG, fluidB, alpha, uv4, uv8, normalX, -normalY, normalZ, upNorthEastLight);
                    this.vertex(pVertexConsumer, localX + 1.0D, localY + (double) southEastHeight, localZ + 1.0D, fluidR, fluidG, fluidB, alpha, uv3, uv7, normalX, -normalY, normalZ, upSouthEastLight);
                    this.vertex(pVertexConsumer, localX + 0.0D, localY + (double) southWestHeight, localZ + 1.0D, fluidR, fluidG, fluidB, alpha, uv2, uv6, normalX, -normalY, normalZ, upSouthWestLight);
                }
            }

            if (renderBelow) {
                float f40 = fluidAtlas[0].getU0();
                float f41 = fluidAtlas[0].getU1();
                float f42 = fluidAtlas[0].getV0();
                float f43 = fluidAtlas[0].getV1();
                float fluidR = downShading * red;
                float fluidG = downShading * green;
                float fluidB = downShading * blue;
                float normalX = 0;
                float normalY = -1;
                float normalZ = 0;

                this.vertex(pVertexConsumer, localX, localY + yOffset, localZ + 1.0D, fluidR, fluidG, fluidB, alpha, f40, f43, normalX, normalY, normalZ, thisLight);
                this.vertex(pVertexConsumer, localX, localY + yOffset, localZ, fluidR, fluidG, fluidB, alpha, f40, f42, normalX, normalY, normalZ, thisLight);
                this.vertex(pVertexConsumer, localX + 1.0D, localY + yOffset, localZ, fluidR, fluidG, fluidB, alpha, f41, f42, normalX, normalY, normalZ, thisLight);
                this.vertex(pVertexConsumer, localX + 1.0D, localY + yOffset, localZ + 1.0D, fluidR, fluidG, fluidB, alpha, f41, f43, normalX, normalY, normalZ, thisLight);
            }

            for (Direction direction : Direction.Plane.HORIZONTAL) {
                float upRight;
                float upLeft;
                double d3;
                double d4;
                double d5;
                double d6;
                boolean shouldRender;

                switch (direction) {
                    case NORTH:
                        upRight = northWestHeight;
                        upLeft = northEastHeight;
                        d3 = localX;
                        d5 = localX + 1.0D;
                        d4 = localZ + epsilon;
                        d6 = localZ + epsilon;
                        shouldRender = renderNorth;

                        break;
                    case SOUTH:
                        upRight = southEastHeight;
                        upLeft = southWestHeight;
                        d3 = localX + 1.0D;
                        d5 = localX;
                        d4 = localZ + 1.0D - epsilon;
                        d6 = localZ + 1.0D - epsilon;
                        shouldRender = renderSouth;

                        break;
                    case WEST:
                        upRight = southWestHeight;
                        upLeft = northWestHeight;
                        d3 = localX + epsilon;
                        d5 = localX + epsilon;
                        d4 = localZ + 1.0D;
                        d6 = localZ;
                        shouldRender = renderWest;

                        break;
                    default:
                        upRight = northEastHeight;
                        upLeft = southEastHeight;
                        d3 = localX + 1.0D - epsilon;
                        d5 = localX + 1.0D - epsilon;
                        d4 = localZ;
                        d6 = localZ + 1.0D;
                        shouldRender = renderEast;

                }

                if (shouldRender && !isFaceOccludedByNeighbor(pLevel, pPos, direction, Math.max(upRight, upLeft), pLevel.getBlockState(pPos.relative(direction)))) {
                    BlockPos blockpos = pPos.relative(direction);
                    TextureAtlasSprite textureatlassprite2 = fluidAtlas[1];
                    if (fluidAtlas[2] != null) {
                        if (pLevel.getBlockState(blockpos).shouldDisplayFluidOverlay(pLevel, blockpos, pFluidState)) {
                            textureatlassprite2 = fluidAtlas[2];
                        }
                    }

                    float f54 = textureatlassprite2.getU(0.0D);
                    float f55 = textureatlassprite2.getU(8.0D);
                    float f33 = textureatlassprite2.getV((1.0F - upRight) * 16.0F * 0.5F);
                    float f34 = textureatlassprite2.getV((1.0F - upLeft) * 16.0F * 0.5F);
                    float f35 = textureatlassprite2.getV(8.0D);
                    float f36 = direction.getAxis() == Direction.Axis.Z ? northShading : westShading;
                    float r = upShading * f36 * red;
                    float g = upShading * f36 * green;
                    float b = upShading * f36 * blue;

                    Vec3i normal = direction.getNormal();
                    float normalX = normal.getX();
                    float normalY = normal.getY();
                    float normalZ = normal.getZ();

                    this.vertex(pVertexConsumer, d3, localY + upRight, d4, r, g, b, alpha, f54, f33, normalX, normalY, normalZ, thisLight);
                    this.vertex(pVertexConsumer, d5, localY + upLeft, d6, r, g, b, alpha, f55, f34, normalX, normalY, normalZ, thisLight);
                    this.vertex(pVertexConsumer, d5, localY + yOffset, d6, r, g, b, alpha, f55, f35, normalX, normalY, normalZ, thisLight);
                    this.vertex(pVertexConsumer, d3, localY + yOffset, d4, r, g, b, alpha, f54, f35, normalX, normalY, normalZ, thisLight);
                    if (textureatlassprite2 != this.waterOverlay) {
                        this.vertex(pVertexConsumer, d3, localY + yOffset, d4, r, g, b, alpha, f54, f35, normalX * -1, normalY * -1, normalZ * -1, thisLight);
                        this.vertex(pVertexConsumer, d5, localY + yOffset, d6, r, g, b, alpha, f55, f35, normalX * -1, normalY * -1, normalZ * -1, thisLight);
                        this.vertex(pVertexConsumer, d5, localY + upLeft,  d6, r, g, b, alpha, f55, f34, normalX * -1, normalY * -1, normalZ * -1, thisLight);
                        this.vertex(pVertexConsumer, d3, localY + upRight, d4, r, g, b, alpha, f54, f33, normalX * -1, normalY * -1, normalZ * -1, thisLight);
                    }
                }
            }
        }
    }
}
