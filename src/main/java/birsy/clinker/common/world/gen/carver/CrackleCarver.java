package birsy.clinker.common.world.gen.carver;

import birsy.clinker.core.registry.ClinkerBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;

public class CrackleCarver extends WorldCarver<ProbabilityConfig> {
    public CrackleCarver(Codec<ProbabilityConfig> codec, int maxHeight) {
        super(codec, maxHeight);
    }

    @Override
    public boolean carveRegion(IChunk chunk, Function<BlockPos, Biome> biomePos, Random rand, int seaLevel, int chunkXOffset, int chunkZOffset, int chunkXIn, int chunkZIn, BitSet carvingMask, ProbabilityConfig config) {
        BlockPos.Mutable pos = new BlockPos.Mutable();

        for (int y = 0; y < chunk.getHeight(); y++) {
            for (int x = 0; x < 15; x++) {
                for (int z = 0; z < 15; z++) {
                    pos.setPos(x, y, z);

                    float distance = 0;
                    for (int chunkX = -1; chunkX < 1; chunkX++) {
                        for (int chunkZ = -1; chunkZ < 1; chunkZ++) {
                            float comparingDistance = (float) pos.distanceSq(getRandomPointInChunk(chunkX + chunkXIn, chunkZ + chunkZIn, y));
                            if (distance < comparingDistance) {
                                distance = comparingDistance;
                            }
                        }
                    }

                    if (distance < 5) {
                        if (chunk.getBlockState(pos).isSolid()) {
                            chunk.setBlockState(pos, CAVE_AIR, false);
                        }
                    }
                }
            }
        }
        return false;
    }

    private BlockPos getRandomPointInChunk(int chunkX, int chunkY, int y) {
        Random rand = new Random((long) chunkX * chunkY);
        return new BlockPos(rand.nextInt(15), y, rand.nextInt(15));
    }

    @Override
    public boolean shouldCarve(Random rand, int chunkX, int chunkZ, ProbabilityConfig config) {
        return true;
    }

    @Override
    protected boolean func_222708_a(double p_222708_1_, double p_222708_3_, double p_222708_5_, int p_222708_7_) {
        return false;
    }
}
