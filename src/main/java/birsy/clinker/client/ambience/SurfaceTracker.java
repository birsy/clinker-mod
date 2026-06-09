package birsy.clinker.client.ambience;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import org.joml.Vector3f;

public class SurfaceTracker {
    private final Minecraft minecraft;
    private float aboveGroundFactor, prevAboveGroundFactor = 1.0F;

    public SurfaceTracker(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    private final BlockPos.MutableBlockPos samplePos = new BlockPos.MutableBlockPos();
    private final BlockPos.MutableBlockPos iteratorPos = new BlockPos.MutableBlockPos();
    public void tick(boolean shouldUpdate) {
        prevAboveGroundFactor = aboveGroundFactor;
        if (!shouldUpdate) return;

        ClientLevel level = minecraft.level;
        Camera camera = minecraft.gameRenderer.getMainCamera();
        Vector3f cameraLook = camera.getLookVector();
        boolean aboveGround = true;

        // our sample positions are randomly set.
        // making sure that it's not inside a solid block.
        for (int i = 0; i < 3; i++) {
            // offset in look direction
            samplePos.set(camera.getBlockPosition());

            // some random offset
            int x = (int) (level.random.nextGaussian() * 5 + cameraLook.x() * 2);
            int z = (int) (level.random.nextGaussian() * 5 + cameraLook.z() * 2);
            samplePos.move(x, 0, z);
            if (!isSolidBlock(level, samplePos)) break;
        }

        int heightY = level.getHeight(Heightmap.Types.WORLD_SURFACE, samplePos.getX(), samplePos.getZ());
        // if we're above the heightmap, we're always considered outside.
        boolean aboveHeightMap = camera.getPosition().y > heightY;
        if (!aboveHeightMap) {
            // count the number of fully solid blocks above us.
            int solidBlocksEncountered = 0;
            int consecutiveSolidBlocks = 0, maxConsecutiveSolidBlocks = 0;
            iteratorPos.set(samplePos);
            while (iteratorPos.getY() < heightY) {
                if (isSolidBlock(level, iteratorPos)) {
                    solidBlocksEncountered++;
                    consecutiveSolidBlocks++;
                    maxConsecutiveSolidBlocks = Math.max(consecutiveSolidBlocks, maxConsecutiveSolidBlocks);
                } else {
                    // reset consecutive count
                    consecutiveSolidBlocks = 0;
                }

                if (solidBlocksEncountered >= 12 || consecutiveSolidBlocks >= 5) {
                    aboveGround = false;
                    break;
                }
                iteratorPos.move(Direction.UP);
            }
        }
        float target = aboveGround ? 1.0F : 0.0F;

        float mixFactor = 4.0F / 64.0F;
        aboveGroundFactor = Mth.clamp(Mth.lerp(mixFactor, aboveGroundFactor, target), 0, 1);
    }

    public float getAboveGroundFactor(double partialTick) {
        return (float) Mth.lerp(partialTick, prevAboveGroundFactor, aboveGroundFactor);
    }

    private static boolean isSolidBlock(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isCollisionShapeFullBlock(level, pos) && state.blocksMotion() && state.canOcclude();
    }
}
