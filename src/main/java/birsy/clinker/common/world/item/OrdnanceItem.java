package birsy.clinker.common.world.item;

import birsy.clinker.common.world.entity.OrdnanceEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OrdnanceItem extends Item {
    public static final int MAX_FUSE_DURATION = 6 * 20;

    public OrdnanceItem(Properties pProperties) {
        super(pProperties);
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.TRIDENT_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!pLevel.isClientSide) {
            OrdnanceEntity entity = OrdnanceEntity.toss(pLevel, pPlayer);

            pLevel.addFreshEntity(entity);
        }

        pPlayer.awardStat(Stats.ITEM_USED.get(this));
        if (!pPlayer.getAbilities().instabuild) {
            itemstack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
        //ItemStack itemstack = pPlayer.getItemInHand(pHand);
        //pPlayer.startUsingItem(pHand);
        //return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
    }


//    @Override
//    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int pTimeCharged) {
//        if (pTimeCharged == MAX_FUSE_DURATION) {
//            OrdnanceEntity.createOrdnanceExplosion(pLivingEntity.getEyePosition(), pLevel, pLivingEntity, null);
//            return;
//        }
//
//        pLevel.playSound(null, pLivingEntity.getX(), pLivingEntity.getY(), pLivingEntity.getZ(), SoundEvents.TRIDENT_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
//        if (!pLevel.isClientSide) {
//            OrdnanceEntity entity = OrdnanceEntity.toss(pLevel, pLivingEntity);
//            entity.shootFromRotation(pLivingEntity, pLivingEntity.getXRot(), pLivingEntity.getYRot(), 0.0F, 0.8F, 0.0F);
//            entity.setPos(pLivingEntity.getEyePosition().add(entity.getDeltaMovement().normalize()));
//            entity.setFuseTime(pTimeCharged);
//            pLevel.addFreshEntity(entity);
//        }
//
//
//        if (pLivingEntity instanceof Player pPlayer) {
//            pPlayer.awardStat(Stats.ITEM_USED.get(this));
//            if (!pPlayer.getAbilities().instabuild) {
//                pStack.shrink(1);
//            }
//        } else {
//            pStack.shrink(1);
//        }
//  }

//    @Override
//    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
//        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
//    }
//
//    public int getUseDuration(ItemStack pStack) {
//        return MAX_FUSE_DURATION;
//    }
//
//    public UseAnim getUseAnimation(ItemStack pStack) {
//        return UseAnim.SPEAR;
//    }
//
//
//    @SubscribeEvent
//    protected static void onStartUsing(LivingEntityUseItemEvent.Start event) {
//        if (event.getItem().getItem() instanceof OrdnanceItem ordnance && !event.getEntity().level().isClientSide()) {
//            //FUSE_TIME_BY_ENTITY_ID.put(event.getEntity().getUUID(), event.getDuration());
//        }
//    }
//    @SubscribeEvent
//    protected static void onStopUsing(LivingEntityUseItemEvent event) {
//        if (event.getItem().getItem() instanceof OrdnanceItem ordnance && !event.getEntity().level().isClientSide()) {
//            //FUSE_TIME_BY_ENTITY_ID.remove(event.getEntity().getUUID(), event.getDuration());
//        }
//    }
//
//    @SubscribeEvent
//    protected static void onServerTick(TickEvent.ServerTickEvent event) {
////        for (ServerLevel allLevel : event.getServer().getAllLevels()) {
////            for (UUID id : FUSE_TIME_BY_ENTITY_ID.keySet()) {
////                Entity entity = allLevel.getEntity(id);
////                if (entity == null) {
////                    FUSE_TIME_BY_ENTITY_ID.remove(id);
////                    continue;
////                }
////
////                int remainingFuse = FUSE_TIME_BY_ENTITY_ID.get(id);
////                FUSE_TIME_BY_ENTITY_ID.replace(id, remainingFuse - 1);
////                if (remainingFuse == 0) {
////                    OrdnanceEntity.createOrdnanceExplosion(entity.getEyePosition(), entity.level(), entity, null);
////                    FUSE_TIME_BY_ENTITY_ID.remove(id);
////                }
////            }
////        }
////        for (Entity entity : FUSE_TIME_BY_ENTITY.keySet()) {
////            int remainingFuse = FUSE_TIME_BY_ENTITY.get(entity);
////            FUSE_TIME_BY_ENTITY.replace(entity, remainingFuse - 1);
////            if (remainingFuse == 0) {
////                OrdnanceEntity.createOrdnanceExplosion(entity.getEyePosition(), entity.level(), entity, null);
////                FUSE_TIME_BY_ENTITY.remove(entity);
////            }
////        }
//    }
}
