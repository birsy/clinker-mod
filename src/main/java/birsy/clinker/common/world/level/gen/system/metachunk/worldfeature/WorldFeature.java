package birsy.clinker.common.world.level.gen.system.metachunk.worldfeature;

import java.util.Set;
import java.util.function.Consumer;

public abstract class WorldFeature {
    public void collectChildFeatures(int childDepth, Consumer<WorldFeature> collector) {}

    public abstract int getCenterX();
    public abstract int getCenterZ();

    public abstract boolean within(int minX, int minZ, int maxX, int maxZ);

    public abstract WorldFeatureType<? extends WorldFeature> type();
}
