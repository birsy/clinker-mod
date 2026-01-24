package birsy.clinker.common.world.level.gen.system.biome.resolver;

public class ProtoBiomeNeighborhood {
    protected final ProtoBiome[] array;

    public ProtoBiomeNeighborhood() {
        this.array = new ProtoBiome[3 * 3];
    }

    public ProtoBiome fromOffset(int x, int z) {
        int localX = x + 1,
            localZ = z + 1;
        return array[localZ * 3 + localX];
    }
}
