package birsy.clinker.common.block;

import birsy.clinker.common.block.blockentity.EmbeddedAmberBlockEntity;
import birsy.clinker.common.world.level.AmberBreakageSystem;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class AmberBlock extends TransparentBlock {
    public AmberBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.getAbilities().instabuild) {
            // clicking an amber block in creative mode will convert it to an Embedded Amber Block...
            // and put the item inside!
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.setBlock(pos, ClinkerBlocks.EMBEDDED_AMBER_BLOCK.get().defaultBlockState(), 2);
                if (level.getBlockEntity(pos) instanceof EmbeddedAmberBlockEntity blockEntity) {
                    if (stack.getItem() instanceof SpawnEggItem egg) {
                        blockEntity.setEmbeddedEntity(egg.getType(stack).create(serverLevel));
                    } else {
                        blockEntity.setEmbeddedItem(stack.copyAndClear());
                    }
                }
            }
            return ItemInteractionResult.CONSUME;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
        super.attack(state, level, pos, player);
        if (!level.isClientSide() && !player.getAbilities().instabuild) {
            level.scheduleTick(pos, this, 1);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        int currentBreakageProgress = -1;
        // looping over every server player is probably bad.
        // but, there's only ever like a hundred of them, Ever. So. Maybe it's not terrible, actually.
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            if (player.getAbilities().instabuild) continue;
            // is a player destroying this block?
            if (player.gameMode.isDestroyingBlock && player.gameMode.destroyPos.equals(pos)) {
                // see: ServerPlayerGamemode.incrementDestroyProgress
                int ticksSpentBreaking = player.gameMode.gameTicks - player.gameMode.destroyProgressStart;
                float f = state.getDestroyProgress(player, level, pos) * (ticksSpentBreaking + 1);
                int breakageProgress = (int)(f * 10.0F);

                currentBreakageProgress = Math.max(currentBreakageProgress, breakageProgress);
            }
        }

        // if nothing's breaking us, cancel!
        if (currentBreakageProgress == -1) return;

        AmberBreakageSystem breakageSystem = AmberBreakageSystem.get(level);
        int lastBreakageProgress = breakageSystem.getBreakageProgress(pos);
        if (lastBreakageProgress != currentBreakageProgress) {
            breakageSystem.updateBreakage(pos, currentBreakageProgress);
            // "crack" a bunch of arms outwards!
            BlockPos.MutableBlockPos crackPos = pos.mutable();
            for (int i = 0; i < 12; i++) {
                crackPos.set(pos);
                for (int j = 0; j < 3; j++) {
                    crackPos.move(Direction.getRandom(level.random));
                    if (!(level.getBlockState(crackPos).getBlock() instanceof AmberBlock)) break;
                    breakageSystem.addBreakageUpTo(crackPos, currentBreakageProgress);
                }
            }
        }
        // make sure we tick next time too
        level.scheduleTick(pos, this, 1);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (level instanceof ServerLevel serverLevel && !player.getAbilities().instabuild) {
            AmberBreakageSystem breakageSystem = AmberBreakageSystem.get(serverLevel);

            Direction[] directions = Direction.values();
            Set<BlockPos> explored = new HashSet<>(),
                          frontier = new HashSet<>();

            frontier.add(pos);
            while (!frontier.isEmpty()) {
                explored.addAll(frontier);

                Set<BlockPos> newFrontier = new HashSet<>(frontier.size() * directions.length);
                for (BlockPos blockPos : frontier) {
                    for (Direction direction : directions) {
                        BlockPos newPos = blockPos.relative(direction);
                        if (explored.contains(newPos)) continue;
                        if (breakageSystem.getBreakageProgress(newPos) < 8) continue;
                        if (!(level.getBlockState(newPos).getBlock() instanceof AmberBlock)) continue;
                        newFrontier.add(newPos);
                    }
                }
                frontier = newFrontier;
            }
            // remove the initial pos, that'll be broken anyway...
            explored.remove(pos);

            for (BlockPos exploredPos : explored) {
                serverLevel.destroyBlock(exploredPos, true);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        // clear out the breakage progress whenever this block is broken
        if (level instanceof ServerLevel serverLevel)
            AmberBreakageSystem.get(serverLevel).clearBreakage(pos);
    }

    // "cracks" a bunch of neighbors with random walkers
    private void crack(ServerLevel level, BlockPos pos, Function<BlockPos, Boolean> updateAndShouldStop) {
        BlockPos.MutableBlockPos mPos = pos.mutable();
        for (int i = 0; i < 12; i++) {
            // crack arms!
            mPos.set(pos);
            for (int j = 0; j < 3; j++) {
                mPos.move(Direction.getRandom(level.random));
                if (!(level.getBlockState(mPos).getBlock() instanceof AmberBlock)) break;
                if (updateAndShouldStop.apply(mPos)) break;
            }
        }
    }
}
