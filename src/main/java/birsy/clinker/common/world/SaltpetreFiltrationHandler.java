package birsy.clinker.common.world;

import birsy.clinker.common.networking.packet.ClientboundSaltpetreLeachPacket;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.ClinkerParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SaltpetreFiltrationHandler {
    public static final BooleanProperty SALTPETRE_LEACHED_PROPERTY = BooleanProperty.create("saltpetre_leached");

    @OnlyIn(Dist.CLIENT)
    public static void doClientEffects(ClientLevel level, BlockPos origin, List<BlockPos> positions) {
        level.playLocalSound(origin.getX() + 0.5, origin.getY() + 0.5, origin.getZ() + 0.5,
                SoundEvents.DEEPSLATE_BREAK, SoundSource.BLOCKS,
                0.02F, 1.2F,
                false);
        level.playLocalSound(origin.getX() + 0.5, origin.getY() + 0.5, origin.getZ() + 0.5,
                SoundEvents.GRAVEL_BREAK, SoundSource.BLOCKS,
                0.05F, 0.5F,
                false);

        for (BlockPos pos : positions) {
            ParticleUtils.spawnParticlesOnBlockFaces(level, pos, ClinkerParticles.SALTPETRE_LEACH.get(), UniformInt.of(3, 5));
        }
    }

    public static void tickFarmland(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int moisture = state.getValue(FarmBlock.MOISTURE);
        boolean saltpetreLeached = state.getValue(SALTPETRE_LEACHED_PROPERTY);
        boolean raining = level.isRainingAt(pos.above());
        if (saltpetreLeached && (moisture > 3 || raining)) {
            // much more effective when it's raining
            if (random.nextInt(raining ? 3 : 15) == 0) {
                BlockPos belowPos = pos.below();
                if (level.getBlockState(belowPos).is(BlockTags.DIRT))
                    leachIntoDirt(level, belowPos, random);
                if (random.nextInt(2) == 0)
                    level.setBlockAndUpdate(pos, state.setValue(SALTPETRE_LEACHED_PROPERTY, false));
            }
        }
    }

    private static void leachIntoDirt(ServerLevel level, BlockPos initialDirtPos, RandomSource random) {
        Set<BlockPos> leachedPositions = new HashSet<>();
        BlockPos.MutableBlockPos pos = initialDirtPos.mutable();
        if (setSaltpetre(level, pos, random)) leachedPositions.add(pos);

        for (int i = 0; i < random.nextInt(0, 4); i++) {
            // search neighbors for dirt blocks.
            // if dirt is found, place leach saltpetre into it,
            // then continue the search.
            for (Direction direction : Direction.allShuffled(random)) {
                if (direction == Direction.UP) continue;
                pos = pos.move(direction);
                BlockState neighborState = level.getBlockState(pos);
                if (neighborState.is(BlockTags.DIRT)) {
                    if (setSaltpetre(level, pos, random)) leachedPositions.add(pos);
                    break;
                } else {
                    // move back to old position, try again...
                    pos = pos.move(direction.getOpposite());
                }
            }
        }

        // inform client for particles and such
        if (!leachedPositions.isEmpty()) {
            BlockPos origin = initialDirtPos.above();
            leachedPositions.add(origin);
            PacketDistributor.sendToPlayersTrackingChunk(level, new ChunkPos(initialDirtPos), new ClientboundSaltpetreLeachPacket(origin, leachedPositions.stream().toList()));
        }
    }

    private static boolean setSaltpetre(ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.getBlockState(pos).is(ClinkerBlocks.SALTPETRE_LEACHED_DIRT)) {
            level.setBlockAndUpdate(pos, ClinkerBlocks.SALTPETRE_LEACHED_DIRT.get().defaultBlockState());
            return true;
        }
        return false;
    }
}
