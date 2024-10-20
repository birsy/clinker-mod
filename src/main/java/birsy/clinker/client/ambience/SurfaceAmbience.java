package birsy.clinker.client.ambience;

import birsy.clinker.core.Clinker;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.joml.Vector3f;

public class SurfaceAmbience {
    private final Minecraft minecraft;
    private float aboveGroundFactor = 1.0F;

    public SurfaceAmbience(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    private final BlockPos.MutableBlockPos samplePos = new BlockPos.MutableBlockPos();
    private final BlockPos.MutableBlockPos iteratorPos = new BlockPos.MutableBlockPos();
    public void tick() {
        ClientLevel level = minecraft.level;
        Camera camera = minecraft.gameRenderer.getMainCamera();
        BlockPos cameraPos = camera.getBlockPosition();
        Vector3f cameraLook = camera.getLookVector();
        boolean aboveGround = false;

        BlockHitResult cast = level.clip(
                new ClipContext(camera.getPosition(), camera.getPosition().add(cameraLook.x * 16, cameraLook.y * 16, cameraLook.z * 16), ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, CollisionContext.empty())
        );

        // our sample positions are randomly set.
        // making sure that it's not inside a solid block.
        for (int i = 0; i < 3; i++) {
            // offset in look direction
            samplePos.set(cast.getBlockPos());

            // some random offset
            int x = (int) (level.random.nextGaussian() * 8);
            int z = (int) (level.random.nextGaussian() * 8);

            samplePos.move(x, 2, z);
            if (!isSolidBlock(level, samplePos)) {
                aboveGround = true;
                break;
            }
        }

        if (aboveGround) {
            int heightY = Math.max(level.getHeight(Heightmap.Types.WORLD_SURFACE, samplePos.getX(), samplePos.getZ()), level.getSeaLevel());
            // if we're above the height map and sea level, we're always considered outside.
            boolean aboveHeightMap = samplePos.getY() > heightY;
            if (!aboveHeightMap) {
                // count the number of fully solid blocks above us.
                int solidBlocksEncountered = 0;
                iteratorPos.set(samplePos);
                while (iteratorPos.getY() < 256) {
                    BlockState state = level.getBlockState(iteratorPos);
                    if (state.isCollisionShapeFullBlock(level, iteratorPos) && state.blocksMotion() && state.canOcclude()) {
                        solidBlocksEncountered++;
                    }

                    if (solidBlocksEncountered >= 12) {
                        aboveGround = false;
                        break;
                    }
                    iteratorPos.move(Direction.UP);
                }
            }
        }

        float mixFactor = 1.0F / 32.0F;
        aboveGroundFactor = aboveGroundFactor * (1.0F - mixFactor) + (aboveGround ? 1.0F : 0.0F) * mixFactor;
    }

    public void render() {

    }

    private static boolean isSolidBlock(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isCollisionShapeFullBlock(level, pos) && state.blocksMotion() && state.canOcclude();
    }
}
