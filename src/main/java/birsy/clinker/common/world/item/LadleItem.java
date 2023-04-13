package birsy.clinker.common.world.item;

import net.minecraft.world.item.Item;


public class LadleItem extends Item {
    public LadleItem(Item.Properties properties) {
        super(properties);
    }

    /*
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        PlayerEntity player = context.getPlayer();

        if (context.getFace() == Direction.UP && world.getBlockState(pos).matchesBlock(ClinkerBlocks.HEATED_IRON_CAULDRON.get())) {
            HeatedIronCauldronTileEntity tileEntity = (HeatedIronCauldronTileEntity) world.getTileEntity(pos);
            int itemIndex = tileEntity.getLastItemInInventory();

            if (itemIndex != -1) {
                world.playSound(null, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                tileEntity.dropItems(itemIndex, player);
                context.getItem().damageItem(1, player, (playerEntity) -> playerEntity.sendBreakAnimation(context.getHand()));
                player.addStat(Stats.ITEM_USED.get(this));

                return ActionResultType.SUCCESS;
            } else {
                return super.onItemUse(context);
            }
        } else {
            return super.onItemUse(context);
        }
    }
     */
}
