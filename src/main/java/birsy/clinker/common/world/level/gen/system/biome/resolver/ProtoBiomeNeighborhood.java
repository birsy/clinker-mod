package birsy.clinker.common.world.level.gen.system.biome.resolver;

public class ProtoBiomeNeighborhood {
    public static final int[] NEIGHBOR_INDICES = {0, 1, 2, 3, 5, 6, 7, 8};
    public static final int[] DIRECT_NEIGHBOR_INDICES = {1, 3, 5, 7};
    public static int neighborAt(int[] n, int dx, int dz) {
        return n[(dz + 1) * 3 + (dx + 1)];
    }
}
