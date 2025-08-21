package birsy.clinker.client;

import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.TintedGlassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WeirdLiquidRendererRemake {


    public static void tessellate(BlockAndTintGetter level, BlockPos pos, VertexConsumer buffer, BlockState blockState, FluidState fluidState) {
        TextureAtlasSprite[] sprites = net.neoforged.neoforge.client.textures.FluidSpriteCache.getFluidSprites(level, pos, fluidState);
        int fluidColor = net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions.of(fluidState).getTintColor(fluidState, level, pos);
        float alpha = (float)(fluidColor >> 24 & 255) / 255.0F,
                red = (float)(fluidColor >> 16 & 0xFF) / 255.0F,
                green = (float)(fluidColor >> 8 & 0xFF) / 255.0F,
                blue = (float)(fluidColor & 0xFF) / 255.0F;

        BlockState block = level.getBlockState(pos.relative(Direction.DOWN)),
                upBlock = level.getBlockState(pos.relative(Direction.UP)),
                northBlock = level.getBlockState(pos.relative(Direction.NORTH)),
                southBlock = level.getBlockState(pos.relative(Direction.SOUTH)),
                westBlock = level.getBlockState(pos.relative(Direction.WEST)),
                eastBlock = level.getBlockState(pos.relative(Direction.EAST));
        FluidState fluid = block.getFluidState(),
                upFluid = upBlock.getFluidState(),
                northFluid = northBlock.getFluidState(),
                southFluid = southBlock.getFluidState(),
                westFluid = westBlock.getFluidState(),
                eastFluid = eastBlock.getFluidState();

        boolean drawSurface = !isNeighborStateHidingOverlay(fluidState, upBlock, Direction.DOWN),
                drawDownFace = shouldRenderFace(level, pos, fluidState, blockState, Direction.DOWN, block)
                    && !isFaceOccludedByNeighbor(level, pos, Direction.DOWN, 0.8888889F, block),
                drawNorthFace = shouldRenderFace(level, pos, fluidState, blockState, Direction.NORTH, northBlock),
                drawSouthFace = shouldRenderFace(level, pos, fluidState, blockState, Direction.SOUTH, southBlock),
                drawWestFace = shouldRenderFace(level, pos, fluidState, blockState, Direction.WEST, westBlock),
                drawEastFace = shouldRenderFace(level, pos, fluidState, blockState, Direction.EAST, eastBlock);

        Fluid fluidType = fluidState.getType();
        float height = getHeight(level, fluidType, pos, blockState, fluidState);
        float northEastHeight = 1.0F, northWestHeight = 1.0F, southEastHeight = 1.0F, southWestHeight = 1.0F;
        if (height < 1.0F) {
            float heightN = getHeight(level, fluidType, pos.north(), northBlock, northFluid),
                  heightS = getHeight(level, fluidType, pos.south(), southBlock, southFluid),
                  heightE = getHeight(level, fluidType, pos.east(), eastBlock, eastFluid),
                  heightW = getHeight(level, fluidType, pos.west(), westBlock, westFluid);
            northEastHeight = calculateAverageHeight(level, fluidType, height, heightN, heightE, pos.relative(Direction.NORTH).relative(Direction.EAST));
            northWestHeight = calculateAverageHeight(level, fluidType, height, heightN, heightW, pos.relative(Direction.NORTH).relative(Direction.WEST));
            southEastHeight = calculateAverageHeight(level, fluidType, height, heightS, heightE, pos.relative(Direction.SOUTH).relative(Direction.EAST));
            southWestHeight = calculateAverageHeight(level, fluidType, height, heightS, heightW, pos.relative(Direction.SOUTH).relative(Direction.WEST));
        }



        float blockX = (float)(pos.getX() & 15);
        float blockY = (float)(pos.getY() & 15);
        float blockZ = (float)(pos.getZ() & 15);

        TextureAtlasSprite stillSprite = sprites[0];
        TextureAtlasSprite flowingSprite = sprites[1];

        int[] blockLight = new int[9], skyLight = new int[9];
        float[] occlusion = new float[9];

        if (drawSurface) {
            fillFaceShadingArrays(level, Direction.UP, pos, blockLight, skyLight, occlusion);

            int packedLight0 = packedLightFromShadingArrays(0, blockLight, skyLight, occlusion);
            float ao0 = ambientOcclusionFromShadingArray(0, occlusion);
            int packedLight1 = packedLightFromShadingArrays(1, blockLight, skyLight, occlusion);
            float ao1 = ambientOcclusionFromShadingArray(1, occlusion);
            int packedLight2 = packedLightFromShadingArrays(2, blockLight, skyLight, occlusion);
            float ao2 = ambientOcclusionFromShadingArray(2, occlusion);
            int packedLight3 = packedLightFromShadingArrays(3, blockLight, skyLight, occlusion);
            float ao3 = ambientOcclusionFromShadingArray(3, occlusion);

            vertex(buffer, blockX + 1.0F, blockY + southEastHeight, blockZ + 1.0F, red * ao3, green * ao3, blue * ao3, alpha, stillSprite.getU(1.0F), stillSprite.getV(1.0F), packedLight3);
            vertex(buffer, blockX + 1.0F, blockY + northEastHeight, blockZ + 0.0F, red * ao2, green * ao2, blue * ao2, alpha, stillSprite.getU(1.0F), stillSprite.getV(0.0F), packedLight2);
            vertex(buffer, blockX + 0.0F, blockY + northWestHeight, blockZ + 0.0F, red * ao1, green * ao1, blue * ao1, alpha, stillSprite.getU(0.0F), stillSprite.getV(0.0F), packedLight1);
            vertex(buffer, blockX + 0.0F, blockY + southWestHeight, blockZ + 1.0F, red * ao0, green * ao0, blue * ao0, alpha, stillSprite.getU(0.0F), stillSprite.getV(1.0F), packedLight0);

            if (fluidState.shouldRenderBackwardUpFace(level, pos.above())) {
                vertex(buffer, blockX + 0.0F, blockY + southWestHeight, blockZ + 1.0F, red * ao0, green * ao0, blue * ao0, alpha, stillSprite.getU(0.0F), stillSprite.getV(1.0F), packedLight0);
                vertex(buffer, blockX + 0.0F, blockY + northWestHeight, blockZ + 0.0F, red * ao1, green * ao1, blue * ao1, alpha, stillSprite.getU(0.0F), stillSprite.getV(0.0F), packedLight1);
                vertex(buffer, blockX + 1.0F, blockY + northEastHeight, blockZ + 0.0F, red * ao2, green * ao2, blue * ao2, alpha, stillSprite.getU(1.0F), stillSprite.getV(0.0F), packedLight2);
                vertex(buffer, blockX + 1.0F, blockY + southEastHeight, blockZ + 1.0F, red * ao3, green * ao3, blue * ao3, alpha, stillSprite.getU(1.0F), stillSprite.getV(1.0F), packedLight3);
            }
        }

        if (drawEastFace || drawWestFace || drawNorthFace || drawSouthFace) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                float heightLeft = 0, heightRight = 0;
                float x1 = 0, z1 = 0, x2 = 0, z2 = 0;
                boolean shouldDraw = false;
                switch (direction) {
                    case NORTH -> {
                        heightLeft = northWestHeight;
                        heightRight = northEastHeight;
                        x1 = blockX;
                        x2 = blockX + 1.0F;
                        z1 = blockZ;
                        z2 = blockZ;
                        shouldDraw = drawNorthFace;
                    }
                    case SOUTH -> {
                        heightLeft = southEastHeight;
                        heightRight = southWestHeight;
                        x1 = blockX + 1.0F;
                        x2 = blockX;
                        z1 = blockZ + 1.0F;
                        z2 = blockZ + 1.0F;
                        shouldDraw = drawSouthFace;
                    }
                    case WEST -> {
                        heightLeft = southWestHeight;
                        heightRight = northWestHeight;
                        x1 = blockX;
                        x2 = blockX;
                        z1 = blockZ + 1.0F;
                        z2 = blockZ;
                        shouldDraw = drawWestFace;
                    }
                    case EAST -> {
                        heightLeft = northEastHeight;
                        heightRight = southEastHeight;
                        x1 = blockX + 1.0F;
                        x2 = blockX + 1.0F;
                        z1 = blockZ;
                        z2 = blockZ + 1.0F;
                        shouldDraw = drawEastFace;
                    }
                    case null, default -> {}
                }

                if (shouldDraw) {
                    fillFaceShadingArrays(level, direction, pos.relative(direction), blockLight, skyLight, occlusion);

                    int packedLight0 = packedLightFromShadingArrays(0, blockLight, skyLight, occlusion);
                    float ao0 = ambientOcclusionFromShadingArray(0, occlusion);
                    int packedLight1 = packedLightFromShadingArrays(1, blockLight, skyLight, occlusion);
                    float ao1 = ambientOcclusionFromShadingArray(1, occlusion);
                    int packedLight2 = packedLightFromShadingArrays(2, blockLight, skyLight, occlusion);
                    float ao2 = ambientOcclusionFromShadingArray(2, occlusion);
                    int packedLight3 = packedLightFromShadingArrays(3, blockLight, skyLight, occlusion);
                    float ao3 = ambientOcclusionFromShadingArray(3, occlusion);

                    vertex(buffer, x1, blockY, z1, red * ao3, green * ao3, blue * ao3, alpha, stillSprite.getU(1.0F), stillSprite.getV(1.0F), packedLight3);
                    vertex(buffer, x1, blockY + heightLeft, z1, red * ao2, green * ao2, blue * ao2, alpha, stillSprite.getU(1.0F), stillSprite.getV(0.0F), packedLight2);
                    vertex(buffer, x2, blockY + heightRight, z2, red * ao1, green * ao1, blue * ao1, alpha, stillSprite.getU(0.0F), stillSprite.getV(0.0F), packedLight1);
                    vertex(buffer, x2, blockY, z2, red * ao0, green * ao0, blue * ao0, alpha, stillSprite.getU(0.0F), stillSprite.getV(1.0F), packedLight0);

                    if (!isFaceOccludedByNeighbor(level, pos, direction, Math.max(heightLeft, heightRight), level.getBlockState(pos.relative(direction)))) {
                        vertex(buffer, x2, blockY, z2, red * ao0, green * ao0, blue * ao0, alpha, stillSprite.getU(0.0F), stillSprite.getV(1.0F), packedLight0);
                        vertex(buffer, x2, blockY + heightRight, z2, red * ao1, green * ao1, blue * ao1, alpha, stillSprite.getU(0.0F), stillSprite.getV(0.0F), packedLight1);
                        vertex(buffer, x1, blockY + heightLeft, z1, red * ao2, green * ao2, blue * ao2, alpha, stillSprite.getU(1.0F), stillSprite.getV(0.0F), packedLight2);
                        vertex(buffer, x1, blockY, z1, red * ao3, green * ao3, blue * ao3, alpha, stillSprite.getU(1.0F), stillSprite.getV(1.0F), packedLight3);
                    }
                }
            }
        }

//        if (drawSurface || drawDownFace || drawEastFace || drawWestFace || drawNorthFace || drawSouthFace) {
//            float directionalShadingDown = level.getShade(Direction.DOWN, true),
//                  directionalShadingUp = level.getShade(Direction.UP, true),
//                  directionalShadingNorth = level.getShade(Direction.NORTH, true),
//                  directionalShadingWest = level.getShade(Direction.WEST, true);
//
//            Fluid fluidType = fluidState.getType();
//            float height = getHeight(level, fluidType, pos, blockState, fluidState);
//            float northEastHeight = 1.0F, northWestHeight = 1.0F, southEastHeight = 1.0F, southWestHeight = 1.0F;
//            if (height < 1.0F) {
//                float heightN = getHeight(level, fluidType, pos.north(), northBlock, northFluid),
//                      heightS = getHeight(level, fluidType, pos.south(), southBlock, southFluid),
//                      heightE = getHeight(level, fluidType, pos.east(), eastBlock, eastFluid),
//                      heightW = getHeight(level, fluidType, pos.west(), westBlock, westFluid);
//                northEastHeight = calculateAverageHeight(level, fluidType, height, heightN, heightE, pos.relative(Direction.NORTH).relative(Direction.EAST));
//                northWestHeight = calculateAverageHeight(level, fluidType, height, heightN, heightW, pos.relative(Direction.NORTH).relative(Direction.WEST));
//                southEastHeight = calculateAverageHeight(level, fluidType, height, heightS, heightE, pos.relative(Direction.SOUTH).relative(Direction.EAST));
//                southWestHeight = calculateAverageHeight(level, fluidType, height, heightS, heightW, pos.relative(Direction.SOUTH).relative(Direction.WEST));
//            }
//
//            float chunkX = (float)(pos.getX() & 15);
//            float chunkY = (float)(pos.getY() & 15);
//            float chunkZ = (float)(pos.getZ() & 15);
//            float f16 = drawDownFace ? 0.001F : 0.0F;
//            if (drawSurface && !isFaceOccludedByNeighbor(level, pos, Direction.UP, Math.min(Math.min(northWestHeight, southWestHeight), Math.min(southEastHeight, northEastHeight)), upBlock)) {
//                northWestHeight -= 0.001F;
//                southWestHeight -= 0.001F;
//                southEastHeight -= 0.001F;
//                northEastHeight -= 0.001F;
//                Vec3 vec3 = fluidState.getFlow(level, pos);
//                float f17;
//                float f18;
//                float f19;
//                float f20;
//                float f21;
//                float f22;
//                float f23;
//                float f24;
//                if (vec3.x == 0.0 && vec3.z == 0.0) {
//                    TextureAtlasSprite textureatlassprite1 = sprites[0];
//                    f17 = textureatlassprite1.getU(0.0F);
//                    f21 = textureatlassprite1.getV(0.0F);
//                    f18 = f17;
//                    f22 = textureatlassprite1.getV(1.0F);
//                    f19 = textureatlassprite1.getU(1.0F);
//                    f23 = f22;
//                    f20 = f19;
//                    f24 = f21;
//                } else {
//                    TextureAtlasSprite textureatlassprite = sprites[1];
//                    float f25 = (float) Mth.atan2(vec3.z, vec3.x) - (float) (Math.PI / 2);
//                    float f26 = Mth.sin(f25) * 0.25F;
//                    float f27 = Mth.cos(f25) * 0.25F;
//                    float f28 = 0.5F;
//                    f17 = textureatlassprite.getU(0.5F + (-f27 - f26));
//                    f21 = textureatlassprite.getV(0.5F + -f27 + f26);
//                    f18 = textureatlassprite.getU(0.5F + -f27 + f26);
//                    f22 = textureatlassprite.getV(0.5F + f27 + f26);
//                    f19 = textureatlassprite.getU(0.5F + f27 + f26);
//                    f23 = textureatlassprite.getV(0.5F + (f27 - f26));
//                    f20 = textureatlassprite.getU(0.5F + (f27 - f26));
//                    f24 = textureatlassprite.getV(0.5F + (-f27 - f26));
//                }
//
//                float f53 = (f17 + f18 + f19 + f20) / 4.0F;
//                float f54 = (f21 + f22 + f23 + f24) / 4.0F;
//                float f55 = sprites[0].uvShrinkRatio();
//                f17 = Mth.lerp(f55, f17, f53);
//                f18 = Mth.lerp(f55, f18, f53);
//                f19 = Mth.lerp(f55, f19, f53);
//                f20 = Mth.lerp(f55, f20, f53);
//                f21 = Mth.lerp(f55, f21, f54);
//                f22 = Mth.lerp(f55, f22, f54);
//                f23 = Mth.lerp(f55, f23, f54);
//                f24 = Mth.lerp(f55, f24, f54);
//                int l = getLightColor(level, pos);
//                float f57 = directionalShadingUp * red;
//                float f29 = directionalShadingUp * green;
//                float f30 = directionalShadingUp * blue;
//                vertex(buffer, chunkX + 0.0F, chunkY + northWestHeight, chunkZ + 0.0F, f57, f29, f30, alpha, f17, f21, l);
//                vertex(buffer, chunkX + 0.0F, chunkY + southWestHeight, chunkZ + 1.0F, f57, f29, f30, alpha, f18, f22, l);
//                vertex(buffer, chunkX + 1.0F, chunkY + southEastHeight, chunkZ + 1.0F, f57, f29, f30, alpha, f19, f23, l);
//                vertex(buffer, chunkX + 1.0F, chunkY + northEastHeight, chunkZ + 0.0F, f57, f29, f30, alpha, f20, f24, l);
//                if (fluidState.shouldRenderBackwardUpFace(level, pos.above())) {
//                    vertex(buffer, chunkX + 0.0F, chunkY + northWestHeight + 199, chunkZ + 0.0F, f57, f29, f30, alpha, f17, f21, l);
//                    vertex(buffer, chunkX + 1.0F, chunkY + northEastHeight, chunkZ + 0.0F, f57, f29, f30, alpha, f20, f24, l);
//                    vertex(buffer, chunkX + 1.0F, chunkY + southEastHeight, chunkZ + 1.0F, f57, f29, f30, alpha, f19, f23, l);
//                    vertex(buffer, chunkX + 0.0F, chunkY + southWestHeight, chunkZ + 1.0F, f57, f29, f30, alpha, f18, f22, l);
//                }
//            }
//
//            if (drawDownFace) {
//                float f40 = sprites[0].getU0();
//                float f41 = sprites[0].getU1();
//                float f42 = sprites[0].getV0();
//                float f43 = sprites[0].getV1();
//                int k = getLightColor(level, pos.below());
//                float f46 = directionalShadingDown * red;
//                float f48 = directionalShadingDown * green;
//                float f50 = directionalShadingDown * blue;
//                vertex(buffer, chunkX, chunkY + f16, chunkZ + 1.0F, f46, f48, f50, alpha, f40, f43, k);
//                vertex(buffer, chunkX, chunkY + f16, chunkZ, f46, f48, f50, alpha, f40, f42, k);
//                vertex(buffer, chunkX + 1.0F, chunkY + f16, chunkZ, f46, f48, f50, alpha, f41, f42, k);
//                vertex(buffer, chunkX + 1.0F, chunkY + f16, chunkZ + 1.0F, f46, f48, f50, alpha, f41, f43, k);
//            }
//
//            int packedLight = getLightColor(level, pos);
//
//            for (Direction direction : Direction.Plane.HORIZONTAL) {
//                float heightLeft;
//                float heightRight;
//                float x1;
//                float z1;
//                float x2;
//                float z2;
//                boolean shouldDraw;
//                switch (direction) {
//                    case NORTH:
//                        heightLeft = northWestHeight;
//                        heightRight = northEastHeight;
//                        x1 = chunkX;
//                        x2 = chunkX + 1.0F;
//                        z1 = chunkZ + 0.001F;
//                        z2 = chunkZ + 0.001F;
//                        shouldDraw = drawNorthFace;
//                        break;
//                    case SOUTH:
//                        heightLeft = southEastHeight;
//                        heightRight = southWestHeight;
//                        x1 = chunkX + 1.0F;
//                        x2 = chunkX;
//                        z1 = chunkZ + 1.0F - 0.001F;
//                        z2 = chunkZ + 1.0F - 0.001F;
//                        shouldDraw = drawSouthFace;
//                        break;
//                    case WEST:
//                        heightLeft = southWestHeight;
//                        heightRight = northWestHeight;
//                        x1 = chunkX + 0.001F;
//                        x2 = chunkX + 0.001F;
//                        z1 = chunkZ + 1.0F;
//                        z2 = chunkZ;
//                        shouldDraw = drawWestFace;
//                        break;
//                    default:
//                        heightLeft = northEastHeight;
//                        heightRight = southEastHeight;
//                        x1 = chunkX + 1.0F - 0.001F;
//                        x2 = chunkX + 1.0F - 0.001F;
//                        z1 = chunkZ;
//                        z2 = chunkZ + 1.0F;
//                        shouldDraw = drawEastFace;
//                }
//
//                if (shouldDraw
//                        && !isFaceOccludedByNeighbor(level, pos, direction, Math.max(heightLeft, heightRight), level.getBlockState(pos.relative(direction)))) {
//                    BlockPos blockpos = pos.relative(direction);
//                    TextureAtlasSprite textureatlassprite2 = sprites[1];
//                    if (sprites[2] != null) {
//                        if (level.getBlockState(blockpos).shouldDisplayFluidOverlay(level, blockpos, fluidState)) {
//                            textureatlassprite2 = sprites[2];
//                        }
//                    }
//
//                    float f56 = textureatlassprite2.getU(0.0F);
//                    float f58 = textureatlassprite2.getU(0.5F);
//                    float f59 = textureatlassprite2.getV((1.0F - heightLeft) * 0.5F);
//                    float f60 = textureatlassprite2.getV((1.0F - heightRight) * 0.5F);
//                    float f31 = textureatlassprite2.getV(0.5F);
//                    float directionalShading = direction.getAxis() == Direction.Axis.Z ? directionalShadingNorth : directionalShadingWest;
//                    float finalR = directionalShadingUp * directionalShading * red;
//                    float finalG = directionalShadingUp * directionalShading * green;
//                    float finalB = directionalShadingUp * directionalShading * blue;
//                    vertex(buffer, x1, chunkY + heightLeft, z1, finalR, finalG, finalB, alpha, f56, f59, packedLight);
//                    vertex(buffer, x2, chunkY + heightRight, z2, finalR, finalG, finalB, alpha, f58, f60, packedLight);
//                    vertex(buffer, x2, chunkY + f16, z2, finalR, finalG, finalB, alpha, f58, f31, packedLight);
//                    vertex(buffer, x1, chunkY + f16, z1, finalR, finalG, finalB, alpha, f56, f31, packedLight);
//                    if (textureatlassprite2 != sprites[2]) { // Neo: use custom fluid's overlay texture
//                        vertex(buffer, x1, chunkY + f16, z1, finalR, finalG, finalB, alpha, f56, f31, packedLight);
//                        vertex(buffer, x2, chunkY + f16, z2, finalR, finalG, finalB, alpha, f58, f31, packedLight);
//                        vertex(buffer, x2, chunkY + heightRight, z2, finalR, finalG, finalB, alpha, f58, f60, packedLight);
//                        vertex(buffer, x1, chunkY + heightLeft, z1, finalR, finalG, finalB, alpha, f56, f59, packedLight);
//                    }
//                }
//            }
//        }
    }

    private static void fillFaceShadingArrays(BlockAndTintGetter level, Direction faceDirection, BlockPos pos, int[] blockLight, int[] skyLight, float[] occlusion) {
        Direction xRelative = Direction.DOWN, yRelative = Direction.DOWN;
        switch (faceDirection) {
            case UP    -> { xRelative = Direction.EAST;  yRelative = Direction.NORTH; }
            case DOWN  -> { xRelative = Direction.EAST;  yRelative = Direction.SOUTH; }
            case NORTH -> { xRelative = Direction.WEST;  yRelative = Direction.UP; }
            case EAST  -> { xRelative = Direction.NORTH; yRelative = Direction.UP;}
            case SOUTH -> { xRelative = Direction.EAST;  yRelative = Direction.UP; }
            case WEST  -> { xRelative = Direction.SOUTH; yRelative = Direction.UP; }
        }

        BlockPos.MutableBlockPos mPos = pos.mutable();
        int index = 0;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                mPos = mPos.set(pos).move(xRelative, x).move(yRelative, y);
                blockLight[index] = level.getBrightness(LightLayer.BLOCK, mPos);
                skyLight[index] = level.getBrightness(LightLayer.SKY, mPos);
                occlusion[index] = 1 - ((float) level.getBlockState(mPos).getLightBlock(level, mPos) / (float) level.getMaxLightLevel());
                index++;
            }
        }
    }

    private static int[] neighborsByVertexIndex = new int[]{
            0, 1, 3, 4,
            1, 2, 4, 5,
            4, 5, 7, 8,
            3, 4, 6, 7
    };
    private static int packedLightFromShadingArrays(int vertexIndex, int[] blockLight, int[] skyLight, float[] occlusion) {
        float totalBlockLight = 0;
        float totalSkyLight = 0;
        float lightCount = 0;

        int indexOffset = vertexIndex * 4;
        for (int i = 0; i < 4; i++) {
            int index = neighborsByVertexIndex[indexOffset + i];
            if (occlusion[index] > 0) {
                totalBlockLight += blockLight[index] * occlusion[index];
                totalSkyLight += skyLight[index] * occlusion[index];
                lightCount += occlusion[index];
            }
        }
        if (lightCount > 0) {
            totalBlockLight /= lightCount;
            totalSkyLight /= lightCount;
        }

        return LightTexture.pack(Math.round(totalBlockLight), Math.round(totalSkyLight));
    }

    private static float ambientOcclusionFromShadingArray(int vertexIndex, float[] occlusion) {
        float lightCount = 0;
        int indexOffset = vertexIndex * 4;
        for (int i = 0; i < 4; i++) {
            int index = neighborsByVertexIndex[indexOffset + i];
            lightCount += occlusion[index];
        }

        return lightCount / 4.0F;
    }

    private static float getHeight(BlockAndTintGetter level, Fluid fluid, BlockPos pos, BlockState blockState, FluidState fluidState) {
        if (fluid.isSame(fluidState.getType())) {
            BlockState blockstate = level.getBlockState(pos.above());
            return fluid.isSame(blockstate.getFluidState().getType()) ? 1.0F : fluidState.getOwnHeight();
        } else {
            return !blockState.isSolid() ? 0.0F : -1.0F;
        }
    }

    private static float getHeight(BlockAndTintGetter level, Fluid fluid, BlockPos pos) {
        BlockState blockstate = level.getBlockState(pos);
        return getHeight(level, fluid, pos, blockstate, blockstate.getFluidState());
    }

    private static float calculateAverageHeight(BlockAndTintGetter level, Fluid fluid, float currentHeight, float height1, float height2, BlockPos pos) {
        if (!(height2 >= 1.0F) && !(height1 >= 1.0F)) {
            float[] afloat = new float[2];
            if (height2 > 0.0F || height1 > 0.0F) {
                float f = getHeight(level, fluid, pos);
                if (f >= 1.0F) {
                    return 1.0F;
                }

                addWeightedHeight(afloat, f);
            }

            addWeightedHeight(afloat, currentHeight);
            addWeightedHeight(afloat, height2);
            addWeightedHeight(afloat, height1);
            return afloat[0] / afloat[1];
        } else {
            return 1.0F;
        }
    }

    private static void addWeightedHeight(float[] output, float height) {
        if (height >= 0.8F) {
            output[0] += height * 10.0F;
            output[1] += 10.0F;
        } else if (height >= 0.0F) {
            output[0] += height;
            output[1]++;
        }
    }

    public static boolean shouldRenderFace(
            BlockAndTintGetter level, BlockPos pos, FluidState fluidState, BlockState blockState, Direction side, FluidState neighborFluid
    ) {
        return !isFaceOccludedBySelf(level, pos, blockState, side) && !isNeighborSameFluid(fluidState, neighborFluid);
    }

    public static boolean shouldRenderFace(
            BlockAndTintGetter level, BlockPos pos, FluidState fluidState, BlockState selfState, Direction direction, BlockState otherState
    ) {
        return !isFaceOccludedBySelf(level, pos, selfState, direction) && !isNeighborStateHidingOverlay(fluidState, otherState, direction.getOpposite());
    }

    private static boolean isNeighborSameFluid(FluidState firstState, FluidState secondState) {
        return secondState.getType().isSame(firstState.getType());
    }

    private static boolean isNeighborStateHidingOverlay(FluidState selfState, BlockState otherState, Direction neighborFace) {
        return otherState.shouldHideAdjacentFluidFace(neighborFace, selfState);
    }

    private static boolean isFaceOccludedByState(BlockGetter level, Direction face, float height, BlockPos pos, BlockState state) {
        if (state.canOcclude()) {
            VoxelShape voxelshape = Shapes.box(0.0, 0.0, 0.0, 1.0, (double)height, 1.0);
            VoxelShape voxelshape1 = state.getOcclusionShape(level, pos);
            return Shapes.blockOccudes(voxelshape, voxelshape1, face);
        } else {
            return false;
        }
    }

    private static boolean isFaceOccludedByNeighbor(BlockGetter level, BlockPos pos, Direction side, float height, BlockState blockState) {
        return isFaceOccludedByState(level, side, height, pos.relative(side), blockState);
    }

    private static boolean isFaceOccludedBySelf(BlockGetter level, BlockPos pos, BlockState state, Direction face) {
        return isFaceOccludedByState(level, face.getOpposite(), 1.0F, pos, state);
    }

    private static int getLightColor(BlockAndTintGetter level, BlockPos pos) {
        int i = LevelRenderer.getLightColor(level, pos);
        int j = LevelRenderer.getLightColor(level, pos.above());
        int k = i & 0xFF;
        int l = j & 0xFF;
        int i1 = i >> 16 & 0xFF;
        int j1 = j >> 16 & 0xFF;
        return (Math.max(k, l)) | (Math.max(i1, j1)) << 16;
    }

    private static void vertex(
            VertexConsumer vertexConsumer,
            float x,
            float y,
            float z,
            float r,
            float g,
            float b,
            float alpha,
            float u,
            float v,
            int packedLight
    ) {
        vertexConsumer.addVertex(x, y, z)
                .setColor(r, g, b, alpha)
                .setUv(u, v)
                .setLight(packedLight)
                .setNormal(0.0F, 1.0F, 0.0F);
    }
}
