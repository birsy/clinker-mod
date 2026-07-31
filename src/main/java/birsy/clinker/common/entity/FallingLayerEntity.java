package birsy.clinker.common.entity;

import birsy.clinker.common.block.FallingLayerBlock;
import birsy.clinker.core.registry.entity.ClinkerEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

public class FallingLayerEntity extends FallingBlockEntity {
    public FallingLayerEntity(EntityType<? extends FallingBlockEntity> entityType, Level level) {
        super(entityType, level);
    }

    private FallingLayerEntity(Level level, double x, double y, double z, BlockState state) {
        this(ClinkerEntities.FALLING_LAYER.get(), level);
        this.blockState = state;
        this.blocksBuilding = true;
        this.setPos(x, y, z);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.setStartPos(this.blockPosition());
    }

    public static FallingLayerEntity fall(Level level, BlockPos pos, BlockState blockState) {
        FallingLayerEntity entity = new FallingLayerEntity(
                level,
                pos.getX() + 0.5,
                pos.getY(),
                pos.getZ() + 0.5,
                blockState.hasProperty(BlockStateProperties.WATERLOGGED) ? blockState.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(false)) : blockState
        );
        level.setBlock(pos, blockState.getFluidState().createLegacyBlock(), 3);
        level.addFreshEntity(entity);
        return entity;
    }

    @Override
    public void tick() {
        if (this.blockState.isAir()) {
            this.discard();
        } else {
            Block block = this.blockState.getBlock();
            this.time++;
            this.applyGravity();
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.handlePortal();

            if (!this.level().isClientSide && (this.isAlive() || this.forceTickAfterTeleportToDuplicate)) {
                BlockPos currentPos = this.blockPosition();
                if (this.onGround()) {
                    BlockState stateAtPos = this.level().getBlockState(currentPos);
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));
                    if (!stateAtPos.is(Blocks.MOVING_PISTON)) {
                        if (!this.cancelDrop) {
                            boolean canBeReplaced = stateAtPos.canBeReplaced(new DirectionalPlaceContext(this.level(), currentPos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
                            boolean shouldKeepFalling = FallingBlock.isFree(this.level().getBlockState(currentPos.below()));
                            boolean canLand = this.blockState.canSurvive(this.level(), currentPos) && !shouldKeepFalling;

                            if (stateAtPos.getBlock() == blockState.getBlock() && blockState.getBlock() instanceof FallingLayerBlock) {
                                this.discard();
                                int newLevel = stateAtPos.getValue(FallingLayerBlock.LAYERS) + blockState.getValue(FallingLayerBlock.LAYERS);
                                if (newLevel <= 8) {
                                    this.level().setBlock(currentPos, stateAtPos.setValue(FallingLayerBlock.LAYERS, newLevel), 3);
                                    ((ServerLevel)this.level()).getChunkSource().chunkMap
                                            .broadcast(this, new ClientboundBlockUpdatePacket(currentPos, this.level().getBlockState(currentPos)));
                                } else {
                                    this.level().setBlock(currentPos, stateAtPos.setValue(FallingLayerBlock.LAYERS, 8), 3);
                                    ((ServerLevel)this.level()).getChunkSource().chunkMap
                                            .broadcast(this, new ClientboundBlockUpdatePacket(currentPos, this.level().getBlockState(currentPos)));

                                    this.level().setBlock(currentPos.above(), stateAtPos.setValue(FallingLayerBlock.LAYERS, newLevel - 8), 3);
                                    ((ServerLevel)this.level()).getChunkSource().chunkMap
                                            .broadcast(this, new ClientboundBlockUpdatePacket(currentPos.above(), this.level().getBlockState(currentPos.above())));
                                }
                            } else if (canBeReplaced && canLand) {
                                if (this.blockState.hasProperty(BlockStateProperties.WATERLOGGED)
                                        && this.level().getFluidState(currentPos).getType() == Fluids.WATER) {
                                    this.blockState = this.blockState.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(true));
                                }

                                if (this.level().setBlock(currentPos, this.blockState, 3)) {
                                    ((ServerLevel)this.level()).getChunkSource().chunkMap
                                            .broadcast(this, new ClientboundBlockUpdatePacket(currentPos, this.level().getBlockState(currentPos)));
                                    this.discard();

                                    if (block instanceof Fallable fallable)
                                        fallable.onLand(this.level(), currentPos, this.blockState, stateAtPos, this);

                                    if (this.blockData != null && this.blockState.hasBlockEntity()) {
                                        BlockEntity blockentity = this.level().getBlockEntity(currentPos);
                                        if (blockentity != null) {
                                            CompoundTag compoundtag = blockentity.saveWithoutMetadata(this.level().registryAccess());

                                            for (String s : this.blockData.getAllKeys()) {
                                                compoundtag.put(s, this.blockData.get(s).copy());
                                            }

                                            blockentity.loadWithComponents(compoundtag, this.level().registryAccess());

                                            blockentity.setChanged();
                                        }
                                    }
                                } else if (this.dropItem && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                                    this.discard();
                                    this.callOnBrokenAfterFall(block, currentPos);
                                    this.spawnAtLocation(block);
                                }
                            } else {
                                this.discard();
                                if (this.dropItem && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                                    this.callOnBrokenAfterFall(block, currentPos);
                                    this.spawnAtLocation(block);
                                }
                            }
                        } else {
                            this.discard();
                            this.callOnBrokenAfterFall(block, currentPos);
                        }
                    }
                } else if (!this.level().isClientSide && (
                        this.time > 100 && (currentPos.getY() <= this.level().getMinBuildHeight() || currentPos.getY() > this.level().getMaxBuildHeight())
                                || this.time > 600
                )) {
                    if (this.dropItem && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                        this.spawnAtLocation(block);
                    }

                    this.discard();
                }
            }

            this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
        }
    }
}
