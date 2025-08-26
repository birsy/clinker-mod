package birsy.clinker.common.world.level.gen.noise;

import net.minecraft.world.level.chunk.ChunkAccess;

public record NoiseComputerContext(NoiseComputerExecutor noiseComputerExecutor, NoiseHolder noiseHolder, ChunkAccess chunk) {}
