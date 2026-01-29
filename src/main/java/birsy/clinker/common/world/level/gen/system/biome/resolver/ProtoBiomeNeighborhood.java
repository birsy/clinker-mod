package birsy.clinker.common.world.level.gen.system.biome.resolver;

public class ProtoBiomeNeighborhood {
    public static final int[] NEIGHBOR_INDICES = {0, 1, 2, 3, 5, 6, 7, 8};
    public static final int[] DIRECT_NEIGHBOR_INDICES = {1, 3, 4, 5, 7};

    protected final ProtoBiome[] array;

    public ProtoBiomeNeighborhood() {
        this.array = new ProtoBiome[3 * 3];
    }

    public ProtoBiome fromOffset(int x, int z) {
        int localX = x + 1,
            localZ = z + 1;
        return array[localZ * 3 + localX];
    }

    public ProtoBiome fromIndex(int index) {
        return array[index];
    }
}
