package birsy.clinker.common.entity;

import birsy.clinker.common.block.plant.aspen.SwampAspenLogBlock;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class LumberingAspenEntity extends PathfinderMob {
    private BlockState[][][] trunk;
    private BlockState[][][][] roots;

    public LumberingAspenEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        this.trunk = new BlockState[1][this.getRandom().nextInt(5, 10)][1];
        for (int x = 0; x < trunk.length; x++) {
            for (int y = 0; y < trunk[0].length; y++) {
                for (int z = 0; z < trunk[0][0].length; z++) {
                    BlockState state = ClinkerBlocks.SWAMP_ASPEN_LOG.get().defaultBlockState();
                    int stumpAttempts = this.getRandom().nextInt(5);
                    if (stumpAttempts != 0) {
                        //state = state.setValue(SwampAspenLogBlock.directionToProperty(Direction.getRandom(this.getRandom())), true);
                    }
                    this.trunk[x][y][z] = state;
                }
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    public BlockState getStateInTrunk(int x, int y, int z) {
        if (this.trunk[x][y][z] != null) {
            return this.trunk[x][y][z];
        }

        return Blocks.AIR.defaultBlockState();
    }

    public Vec3i getTrunkSize() {
        return new Vec3i(this.trunk.length, this.trunk[0].length, trunk[0][0].length);
    }
}
