package birsy.clinker.common.world.level.gen.system.biome.placement.resolver;

public class ProtoBiomeNeighborhood {
    public static final int[] POSITIVE_NEIGHBORS = {1, 2, 5}, POSITIVE_DIRECT_NEIGHBORS = {1, 2};
    public static final int[] NEIGHBOR_INDICES = {0, 1, 2, 3, 5, 6, 7, 8}, DIRECT_NEIGHBOR_INDICES = {1, 3, 5, 7};
    public static int neighborAt(int[] n, int dx, int dz) {
        return n[(dz + 1) * 3 + (dx + 1)];
    }
}
