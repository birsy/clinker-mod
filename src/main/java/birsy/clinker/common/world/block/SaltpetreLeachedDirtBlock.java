package birsy.clinker.common.world.block;

import birsy.clinker.core.registry.ClinkerLootTables;
import birsy.clinker.core.registry.ClinkerParticles;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SaltpetreLeachedDirtBlock extends Block {
    public static final MapCodec<SaltpetreLeachedDirtBlock> CODEC = simpleCodec(SaltpetreLeachedDirtBlock::new);

    public SaltpetreLeachedDirtBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.getAbilities().mayBuild) {
            level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 11);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            // resolve the loot table and spawn items
            if (level.getServer() != null) {
                LootTable loottable = level.getServer().reloadableRegistries().getLootTable(ClinkerLootTables.SALT_PETRE_LEACHED_DIRT_EXTRACTION);
                if (player instanceof ServerPlayer)
                    CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayer)player, ClinkerLootTables.SALT_PETRE_LEACHED_DIRT_EXTRACTION);

                LootParams.Builder lootParams = new LootParams.Builder((ServerLevel) level)
                        .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                        .withParameter(LootContextParams.BLOCK_STATE, state)
                        .withLuck(player.getLuck())
                        .withParameter(LootContextParams.THIS_ENTITY, player);

                List<ItemStack> itemStacks = loottable.getRandomItems(lootParams.create(LootContextParamSets.BLOCK_USE));
                Direction facing = hitResult.getDirection();

                float speed = 0.1F, randomSpeed = 0.05F;
                float offset = 0.5F;
                if (facing == Direction.DOWN) {
                    offset += EntityType.ITEM.getHeight();
                } else if (facing != Direction.UP) {
                    offset += EntityType.ITEM.getWidth() * 0.5F;
                }
                Vec3 itemPos = Vec3.atCenterOf(pos).relative(facing, offset);
                for (ItemStack stack : itemStacks) {
                    ItemEntity itementity = new ItemEntity(level, itemPos.x, itemPos.y, itemPos.z, stack);
                    itementity.setDeltaMovement(
                            level.random.triangle(facing.getStepX() * speed, randomSpeed),
                            level.random.triangle(facing.getStepY() * speed, randomSpeed) * (facing == Direction.UP ? 2 : 1),
                            level.random.triangle(facing.getStepZ() * speed, randomSpeed)
                    );
                    itementity.setDefaultPickUpDelay();
                    level.addFreshEntity(itementity);
                }
            }
            // todo: custom sound?
            level.playSound(player, pos, SoundEvents.DEEPSLATE_BREAK, SoundSource.BLOCKS, 1.0F, 1.3F);
            level.playSound(player, pos, SoundEvents.GRAVEL_HIT, SoundSource.BLOCKS, 0.1F, 0.5F);

            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return super.useWithoutItem(state, level, pos, player, hitResult);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(5) == 0) {
            Direction direction = Direction.getRandom(random);
            if (direction != Direction.UP) {
                BlockPos blockpos = pos.relative(direction);
                BlockState blockstate = level.getBlockState(blockpos);
                if (!blockstate.isFaceSturdy(level, blockpos, direction.getOpposite())) {
                    double x = direction.getStepX() == 0 ? random.nextDouble() : 0.5 + direction.getStepX() * 0.6;
                    double y = direction.getStepY() == 0 ? random.nextDouble() : 0.5 + direction.getStepY() * 0.6;
                    double z = direction.getStepZ() == 0 ? random.nextDouble() : 0.5 + direction.getStepZ() * 0.6;
                    level.addParticle(
                            ClinkerParticles.DRIPPING_SALTPETRE.get(),
                            pos.getX() + x, pos.getY() + y, pos.getZ() + z,
                            0.0, 0.0, 0.0
                    );
                }
            }
        }
    }

    @Override
    public MapCodec<SaltpetreLeachedDirtBlock> codec() {
        return CODEC;
    }
}
