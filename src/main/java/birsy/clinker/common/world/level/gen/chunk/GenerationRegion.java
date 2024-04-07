package birsy.clinker.common.world.level.gen.chunk;

import birsy.clinker.core.Clinker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec2;

public class GenerationRegion {
    public static final float REGION_SIZE = 512;
    public long worldSeed;
    public Vec2 position;
    public RandomSource random;
    private ChunkAccess chunk;

    public GenerationRegion(Vec2 position, long worldSeed, ChunkAccess chunk) {
        this.worldSeed = worldSeed;
        this.position = position;
        this.chunk = chunk;
    }

    //dummy chunk, only used for world height checks etc... probably a better way to do this
    public ChunkAccess getChunkAccess() {
        return this.chunk;
    }
}
