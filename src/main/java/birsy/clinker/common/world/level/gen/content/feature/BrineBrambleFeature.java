package birsy.clinker.common.world.level.gen.content.feature;

import birsy.clinker.common.world.block.plant.ThornyStemBlock;
import birsy.clinker.core.registry.ClinkerBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class BrineBrambleFeature extends Feature<NoneFeatureConfiguration> {
    public BrineBrambleFeature(Codec<NoneFeatureConfiguration> config) {
        super(config);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos origin = context.origin();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        NoneFeatureConfiguration placementState = context.config();

        BlockPos tgt;
        ArrayList<BlockPos> ps = new ArrayList<>();
        ArrayList<BlockPos> escrow = new ArrayList<>();
        int budget;

        for(Direction d : Direction.values()) {
            escrow.clear();
            tgt = origin.mutable();
            budget = 12;
            if(d == Direction.DOWN)
                continue;
            ps.add(tgt.relative(d));
            escrow.add(tgt.relative(d));


            propagateBramble(ps, escrow, level, d, tgt, random, budget);
        }

        placeBramble(ps, level, origin, random);

        return true;
    }

    private static final ArrayList<BlockPos> tgtEscrowEscrow = new ArrayList<>();

    private static int propagateBramble(ArrayList<BlockPos> tgts, ArrayList<BlockPos> tgtEscrow, WorldGenLevel level, Direction d, BlockPos bp, RandomSource rs, int budget) {
        if(budget <= 0) {
            return 0;
        }
        boolean br = false;
        tgtEscrowEscrow.clear();
        tgtEscrowEscrow.addAll(tgtEscrow);
        tgtEscrow.clear();

        for(BlockPos pos : tgtEscrowEscrow) {
            for(Direction nd : Direction.values()) {
                if(d.getOpposite() == d)
                    continue;

                BlockPos tgt = pos.relative(nd);
                BlockState s = level.getBlockState(bp.relative(nd));

                if(s.canBeReplaced() && rs.nextFloat() > (d == Direction.UP ? 0.75 : 0.5)) {
                    tgts.add(tgt);
                    tgtEscrow.add(tgt);

                    budget -= 1;
                    if(budget <= 0) {
                        br=true;
                    }

                }
                if(br)
                    break;
            }
            if(br)
                break;
        }

        if(budget > 0) {
            budget = propagateBramble(tgts, tgtEscrow, level, d, bp, rs, budget);
        }

        return budget;
    }

    private static final Vec3 up = new Vec3(0., 1., 0.);

    private void placeBramble(ArrayList<BlockPos> positions, WorldGenLevel l, BlockPos p, RandomSource r) {
        for(BlockPos pos : positions) {
            Block ds = (pos.getCenter().subtract(p.getBottomCenter()).dot(up) < .5 + r.nextFloat() * 2.) ? ClinkerBlocks.THORNY_STEM.get() : ClinkerBlocks.SALTY_STEM.get();

            BlockState s = getStateForPlacement(l, pos, positions, ds.defaultBlockState());
            if(s != null)
                l.setBlock(pos, s, 3);
        }
    }

    protected static boolean contains(ArrayList<BlockPos> positions, BlockPos pos) {
        return positions.stream().anyMatch(p -> p.closerThan(pos, 0.01));
    }

    public static @Nullable BlockState getStateForPlacement(WorldGenLevel l, BlockPos tgt, ArrayList<BlockPos> positions, BlockState state) {
        boolean connected = false;
        BlockPos.MutableBlockPos neighborPos = tgt.mutable();
        for (Direction direction : Direction.values()) {
            neighborPos = neighborPos.set(tgt).move(direction);
            boolean shouldConnect = contains(positions, tgt);
            if (shouldConnect) connected = true;
            state = state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction), shouldConnect);
        }

        if (!connected)
            return null;

        state = state.setValue(ThornyStemBlock.WATERLOGGED, l.getFluidState(tgt).getType() == Fluids.WATER);
        return state;
    }

}

