package birsy.clinker.common.item;

import birsy.clinker.common.tileentity.HeatedIronCauldronTileEntity;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import net.minecraft.world.item.Item.Properties;

import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;

public class LadleItem extends Item {
    public LadleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (context.getClickedFace() == Direction.UP && world.getBlockState(pos).is(ClinkerBlocks.HEATED_IRON_CAULDRON.get())) {
            HeatedIronCauldronTileEntity tileEntity = (HeatedIronCauldronTileEntity) world.getBlockEntity(pos);
            int itemIndex = tileEntity.getLastItemInInventory();

            if (itemIndex != -1) {
                world.playSound(null, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                tileEntity.dropItems(itemIndex, player);
                context.getItemInHand().hurtAndBreak(1, player, (playerEntity) -> playerEntity.broadcastBreakEvent(context.getHand()));
                player.awardStat(Stats.ITEM_USED.get(this));

                return InteractionResult.SUCCESS;
            } else {
                return super.useOn(context);
            }
        } else {
            return super.useOn(context);
        }
    }
}
