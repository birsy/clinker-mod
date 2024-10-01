package birsy.clinker.common.world.level.gen.chunk;

import birsy.clinker.common.world.level.gen.ChunkFluidField;
import birsy.clinker.common.world.level.gen.ClinkerWorldGenerator;
import birsy.clinker.common.world.level.gen.NoiseFieldWithOffset;
import birsy.clinker.common.world.level.gen.chunk.biome.SurfaceDecorator;
import birsy.clinker.common.world.level.gen.chunk.biome.SurfaceDecorators;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import org.joml.Vector3f;

public interface ClinkerChunkGenerator {
    int MAX_ELEVATION_DIFFERENCE = 4;

    ClinkerWorldGenerator getWorldGenerator();

    NoiseFieldWithOffset getNoiseField();
    ChunkFluidField getFluidField();
    BlockState getDefaultBlock();
    int getSeaLevel();

    void populateNoiseField(ChunkAccess chunk, StructureManager structureManager, RandomState random);

    default void constructTerrainBase(ChunkAccess chunk) {
        ChunkPos chunkPos = chunk.getPos();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        NoiseFieldWithOffset noiseField = this.getNoiseField();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = chunk.getMaxBuildHeight(); y >= chunk.getMinBuildHeight(); y--) {
                    pos.set(x + chunk.getPos().getMinBlockX(), y, z + chunk.getPos().getMinBlockZ());
                    float noiseVal = noiseField.getValue(x + chunk.getPos().getMinBlockX(), y, z + chunk.getPos().getMinBlockZ());
                    if (noiseVal > 0) {
                        chunk.setBlockState(pos, this.getDefaultBlock(), false);
                    } else {
                        chunk.setBlockState(pos, Blocks.AIR.defaultBlockState(), false);
                    }
                }
            }
        }

        ChunkFluidField fluidField = this.getFluidField();
        fluidField.fillBlockStates(chunk,
                this.getWorldGenerator().getFluidField(chunkPos.x, chunkPos.z - 1),
                this.getWorldGenerator().getFluidField(chunkPos.x, chunkPos.z + 1),
                this.getWorldGenerator().getFluidField(chunkPos.x - 1, chunkPos.z),
                this.getWorldGenerator().getFluidField(chunkPos.x + 1, chunkPos.z));

        this.updateHeightmaps(chunk);
    }

    default void updateHeightmaps(ChunkAccess chunk) {
        Heightmap[] heightmaps = {chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG), chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG)};
        boolean[] lastResult = {false, false};
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = chunk.getMaxBuildHeight(); y >= chunk.getMinBuildHeight(); y--) {
                    pos.set(x + chunk.getPos().getMinBlockX(), y, z + chunk.getPos().getMinBlockZ());
                    BlockState blockState = chunk.getBlockState(pos);

                    boolean shouldContinue = false;
                    for (int i = 0; i < heightmaps.length; i++) {
                        if (!lastResult[i]) {
                            lastResult[i] = heightmaps[i].update(x, y, z, blockState);
                            shouldContinue = shouldContinue || lastResult[i];
                        }
                    }

                    if (!shouldContinue) {
                        break;
                    }
                }
            }
        }
    }

    default void applySurfaceDecorators(WorldGenLevel level, ChunkAccess chunk) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos neighborPos = new BlockPos.MutableBlockPos().set(pos);

        Vector3f derivative = new Vector3f();
        int offset = 0;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int startHeight = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x + offset, z + offset);
                pos.set(x + chunk.getPos().getMinBlockX() + offset, startHeight, z + chunk.getPos().getMinBlockZ() + offset);

                boolean visibleToSun = true;
                while (pos.getY() > chunk.getMinBuildHeight() + 1) {
                    //when it encounters a new surface, check the biome and generate the corresponding surface.
                    if (level.getBlockState(pos) == this.getDefaultBlock()) {
                        ResourceLocation biome = level.getBiome(pos).unwrapKey().get().location();
                        SurfaceDecorator decorator = SurfaceDecorators.getSurfaceDecorator(biome);

                        neighborPos.set(pos);
                        int depth = 1;
                        while (neighborPos.getY() > chunk.getMinBuildHeight() + 1) {
                            neighborPos.move(Direction.DOWN);
                            if (level.getBlockState(neighborPos) == this.getDefaultBlock()) depth++;
                            else break;
                        }

                        int maxElevationIncrease = 0;
                        int maxElevationDecrease = 0;
                        if (decorator.shouldCalculateElevationChange(visibleToSun, pos.getY())) {
                            for (Direction direction : Direction.Plane.HORIZONTAL) {
                                neighborPos.set(pos).move(direction);
                                boolean movingUp = level.getBlockState(neighborPos).isCollisionShapeFullBlock(level, neighborPos);
                                Direction moveDirection = movingUp ? Direction.UP : Direction.DOWN;

                                for (int i = 0; i < MAX_ELEVATION_DIFFERENCE + 1; i++) {
                                    neighborPos.move(moveDirection);

                                    if (movingUp) maxElevationIncrease = Math.max(maxElevationIncrease, i);
                                    else maxElevationDecrease = Math.max(maxElevationDecrease, i + 1);

                                    if (neighborPos.getY() < chunk.getMinBuildHeight() + 1) break;

                                    boolean isTop = movingUp != level.getBlockState(neighborPos).isCollisionShapeFullBlock(level, neighborPos);
                                    if (isTop) break;
                                }
                            }
                        }

                        decorator.buildSurface(chunk, new BlockPos.MutableBlockPos().set(pos), this.getSeaLevel(), visibleToSun, depth, maxElevationIncrease, maxElevationDecrease, (dx, dy, dz) -> this.getNoiseField().getGradient(dx, dy, dz, derivative));
                        visibleToSun = false;

                        // move down to the next air block
                        pos.move(Direction.DOWN, depth - 1);
                    }

                    pos.move(Direction.DOWN);
                }
            }
        }
    }
}
