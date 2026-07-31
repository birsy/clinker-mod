package birsy.clinker.common.world.level.gen.content.feature;

import birsy.clinker.common.block.plant.ThornyStemBlock;
import birsy.clinker.core.registry.ClinkerBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        ps.add(origin.mutable());

        for(Direction d : Direction.values()) {
            if(d == Direction.DOWN)
                continue;

            escrow.clear();
            tgt = origin.mutable();
            budget = 37;

            ps.add(tgt.relative(d));
            escrow.add(tgt.relative(d));


            propagateBrambleDown(ps, level, tgt, random, budget);
            //propagateBramble(ps, escrow, level, d, tgt, random, budget);
        }

        escrow.addAll(ps);

        int top = 8;

        for(BlockPos p : escrow) {
            if(random.nextFloat() > 1f - 0.3f/(1f+Math.sqrt(p.distSqr(origin)))) {
                top -= 1;
                budget = 15;
                propagateBrambleDown(ps, level, p, random, budget);

                if(top <= 0) {
                    break;
                }
            }
        }

        placeBramble(ps, level, origin, random);

        return true;
    }

    private static final ArrayList<BlockPos> tgtEscrowEscrow = new ArrayList<>();

    private static int propagateBramble(ArrayList<BlockPos> tgts, ArrayList<BlockPos> tgtEscrow, WorldGenLevel level, Direction d, BlockPos bp, RandomSource rs, int budget) {
        if(budget <= 0 || tgtEscrow.isEmpty()) {
            return 0;
        }
        boolean br = false;
        tgtEscrowEscrow.clear();
        tgtEscrowEscrow.addAll(tgtEscrow);
        tgtEscrow.clear();

        for(BlockPos pos : tgtEscrowEscrow) {
            for(Direction nd : Direction.values()) {
                if(nd.getOpposite() == d)
                    continue;

                BlockPos tgt = pos.relative(nd);
                BlockState s = level.getBlockState(bp.relative(nd));
                BlockState s2 = level.getBlockState(bp.relative(nd).relative(Direction.DOWN));
                BlockState s3 = level.getBlockState(bp.relative(nd).relative(Direction.DOWN, 2));

                if(s.canBeReplaced() && (s2.isSolid() || s3.isSolid()) && rs.nextFloat() > (d == Direction.UP ? 0.9 : d == Direction.DOWN ? 0. : 0.35)) {
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
            budget -= 1;
        }

        if(budget > 0) {
            budget = propagateBramble(tgts, tgtEscrow, level, d, bp, rs, budget);
        }

        return budget;
    }

    private static final List<Direction> dirs = Arrays.stream(Direction.values()).filter(Direction.Plane.HORIZONTAL).toList();



    private static int propagateBrambleDown(ArrayList<BlockPos> tgts, WorldGenLevel level, BlockPos bp, RandomSource rs, int budget) {
        if(budget <= 0) {
            return 0;
        }

        BlockPos.MutableBlockPos tgt = bp.mutable();
        BlockPos.MutableBlockPos tgtB = bp.relative(Direction.DOWN).mutable();
        while(budget > 0) {
            if(!level.getBlockState(tgtB).canBeReplaced()) {
                int r = rs.nextInt(4);
                boolean noEsc = true;
                for(int ui = 0; ui < 4; ui++) {
                    int i = (ui + r) % 4;
                    Direction dir = dirs.get(i);
                    tgt.move(dir);
                    if(level.getBlockState(tgt).canBeReplaced()) {
                        tgts.add(tgt.immutable());
                        tgtB.move(dir);
                        budget -= 1;
                        noEsc = false;
                        break;
                    }
                }
                if(noEsc) {
                    budget = 0;
                }
            } else {
                if(rs.nextFloat() > 0.7) {
                    Direction d2 = Direction.fromYRot(budget % 2 * 180 + (((budget + 1)*0.5) % 2 * 90));
                    if(level.getBlockState(tgt.relative(d2)).canBeReplaced()) {
                        tgt.move(d2);
                        tgts.add(tgt.immutable());
                        tgtB.move(d2);
                    }
                }
                tgt.move(Direction.DOWN);
                tgts.add(tgt.immutable());
                tgtB.move(Direction.DOWN);
                budget -= 1;
            }

        }

        return budget;
    }

    private static final Vec3 up = new Vec3(0., 1., 0.);

    private void placeBramble(ArrayList<BlockPos> positions, WorldGenLevel l, BlockPos p, RandomSource r) {
        for(BlockPos pos : positions) {
            if(pos.getY() > p.getY() + 2)
                continue;
            Block ds = (-pos.getCenter().subtract(p.getBottomCenter()).dot(up) < .5 + r.nextFloat() * 2.) ? ClinkerBlocks.THORNY_STEM.get() : ClinkerBlocks.SALTY_STEM.get();
            if(!l.getBlockState(pos).canBeReplaced())
                continue;

            BlockState s = getStateForPlacement(l, pos, p, positions, ds.defaultBlockState());
            if(s != null)
                l.setBlock(pos, s, 3);
        }
    }

    protected static boolean contains(ArrayList<BlockPos> positions, BlockPos pos) {
        return positions.stream().anyMatch(p -> p.closerThan(pos, 0.01));
    }

    public static @Nullable BlockState getStateForPlacement(WorldGenLevel l, BlockPos tgt, BlockPos orig, ArrayList<BlockPos> positions, BlockState state) {
        boolean connected = false;
        BlockPos.MutableBlockPos neighborPos = tgt.mutable();

        for (Direction direction : Direction.values()) {
            neighborPos = neighborPos.set(tgt).move(direction);
            boolean shouldConnect = contains(positions, neighborPos);
            if (shouldConnect) connected = true;
            state = state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction), shouldConnect);
        }
        //stem
        if(tgt.closerThan(orig, 0.01))
            state = state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(Direction.DOWN), true);

        if (!connected)
            return null;

        state = state.setValue(ThornyStemBlock.WATERLOGGED, l.getFluidState(tgt).getType() == Fluids.WATER);
        return state;
    }

}

