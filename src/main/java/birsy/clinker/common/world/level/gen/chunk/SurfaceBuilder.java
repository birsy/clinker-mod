package birsy.clinker.common.world.level.gen.chunk;

import birsy.clinker.common.world.level.gen.NoiseField;
import birsy.clinker.common.world.level.gen.NoiseSampler;
import birsy.clinker.common.world.level.gen.chunk.biome.SurfaceDecorators;
import birsy.clinker.common.world.level.gen.chunk.biome.surfacedecorator.SurfaceDecorator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import org.joml.Vector3f;

import java.util.concurrent.ExecutionException;

// constructs the surface layer for a chunk
public class SurfaceBuilder {
    protected final int maxElevationDifference;
    protected final int seaLevel;
    protected final BlockState defaultBlock;
    protected final NoiseSampler sampler;

    public SurfaceBuilder(int maxElevationDifference, int seaLevel, BlockState defaultBlock, NoiseSampler sampler) {
        this.maxElevationDifference = maxElevationDifference;
        this.seaLevel = seaLevel;
        this.defaultBlock = defaultBlock;
        this.sampler = sampler;
    }

    void applySurfaceDecorators(WorldGenLevel level, ChunkAccess chunk, NoiseField noiseField) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos neighborPos = new BlockPos.MutableBlockPos().set(pos);

        Vector3f derivative = new Vector3f();
        int offset = 0;
        try {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int startHeight = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x + offset, z + offset);
                    pos.set(x + chunk.getPos().getMinBlockX() + offset, startHeight, z + chunk.getPos().getMinBlockZ() + offset);

                    boolean visibleToSun = true;
                    while (pos.getY() > chunk.getMinBuildHeight() + 1) {
                        //when it encounters a new surface, check the biome and generate the corresponding surface.
                        if (level.getBlockState(pos) == this.defaultBlock) {
                            ResourceLocation biome = level.getBiome(pos).unwrapKey().get().location();
                            SurfaceDecorator decorator = SurfaceDecorators.getSurfaceDecorator(biome);

                            neighborPos.set(pos);
                            int depth = 1;
                            while (neighborPos.getY() > chunk.getMinBuildHeight() + 1) {
                                neighborPos.move(Direction.DOWN);
                                if (level.getBlockState(neighborPos) == this.defaultBlock) depth++;
                                else break;
                            }

                            int maxElevationIncrease = 0;
                            int maxElevationDecrease = 0;
                            if (decorator.shouldCalculateElevationChange(visibleToSun, pos.getY())) {
                                for (Direction direction : Direction.Plane.HORIZONTAL) {
                                    neighborPos.set(pos).move(direction);
                                    boolean movingUp = level.getBlockState(neighborPos).isCollisionShapeFullBlock(level, neighborPos);
                                    Direction moveDirection = movingUp ? Direction.UP : Direction.DOWN;

                                    for (int i = 0; i < maxElevationDifference + 1; i++) {
                                        neighborPos.move(moveDirection);

                                        if (movingUp) maxElevationIncrease = Math.max(maxElevationIncrease, i);
                                        else maxElevationDecrease = Math.max(maxElevationDecrease, i + 1);

                                        if (neighborPos.getY() < chunk.getMinBuildHeight() + 1) break;

                                        boolean isTop = movingUp != level.getBlockState(neighborPos).isCollisionShapeFullBlock(level, neighborPos);
                                        if (isTop) break;
                                    }
                                }
                            }

                            decorator.buildSurface(chunk, new BlockPos.MutableBlockPos().set(pos), seaLevel, visibleToSun, depth, maxElevationIncrease, maxElevationDecrease, (dx, dy, dz) -> noiseField.getGradient(dx, dy, dz, derivative), sampler);


                            visibleToSun = false;

                            // move down to the next air block
                            pos.move(Direction.DOWN, depth - 1);
                        }

                        pos.move(Direction.DOWN);
                    }
                }
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
