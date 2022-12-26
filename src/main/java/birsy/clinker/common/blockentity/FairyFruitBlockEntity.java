package birsy.clinker.common.blockentity;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlockEntities;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class FairyFruitBlockEntity extends BlockEntity {
    public int ticksExisted;

    // location of the base, relative to the fruit block's center
    public Vec3 baseLocation;
    // location of the fruit, relative to the fruit block's center
    public Vec3 fruitLocation;
    public Vec3 pFruitLocation;

    public FairyFruitBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ClinkerBlockEntities.FAIRY_FRUIT.get(), pPos, pBlockState);
        this.baseLocation = Vec3.ZERO;
        this.fruitLocation = Vec3.ZERO;
        this.pFruitLocation = Vec3.ZERO;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FairyFruitBlockEntity entity) {
        entity.ticksExisted++;
        entity.calculateBaseLocation(level);
        entity.updatePhysics();
    }

    public void updatePhysics() {
        Vec3 finalLocation = this.fruitLocation.add(0, 0, 0);

        // newton's second (?) law
        Vec3 diff = this.pFruitLocation.subtract(this.fruitLocation);
        finalLocation = finalLocation.add(diff);

        // fucked up gravity
        finalLocation = finalLocation.add(0, -0.5, 0);
        // "wind"
        float windSpeed = 0.1F;
        float windStrength = 0.025F;
        finalLocation = finalLocation.add(Mth.sin(this.ticksExisted * windSpeed) * windStrength, 0, Mth.cos(this.ticksExisted * windSpeed * 1.05F) * windStrength);

        // constrain it to be a particular distance from the base location - rope physics!
        double ropeLength = baseLocation.y;
        Vec3 direction = finalLocation.subtract(baseLocation).normalize();
        finalLocation = direction.scale(ropeLength).add(baseLocation);
        // constrain location so it never leaves the block
        //finalLocation = new Vec3(Mth.clamp(finalLocation.x(), -0.5, 0.5), Mth.clamp(finalLocation.y(), -0.5, 0.5), Mth.clamp(finalLocation.z(), -0.5, 0.5));

        // set up variables for next time...
        this.pFruitLocation = this.fruitLocation;
        this.fruitLocation = finalLocation;
    }

    public void calculateBaseLocation(Level level) {
        BlockPos.MutableBlockPos mutable = this.getBlockPos().mutable();
        for (int length = 0; length < level.getMaxBuildHeight() - mutable.getY(); length++) {
            mutable.move(Direction.UP);
            if (!isFairyFruitBlock(level.getBlockState(mutable))) {
                this.baseLocation = new Vec3(0, length, 0);
                return;
            }
        }
        this.baseLocation = new Vec3(0, 256, 0);
    }

    public static boolean isFairyFruitBlock(BlockState state) {
        return state.is(ClinkerBlocks.FAIRY_FRUIT_BLOCK.get()) || state.is(ClinkerBlocks.FAIRY_FRUIT_VINE.get());
    }
}
