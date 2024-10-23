package birsy.clinker.common.world.item;

import birsy.clinker.client.sound.OrdnanceSoundInstance;
import birsy.clinker.common.world.entity.projectile.OrdnanceEffects;
import birsy.clinker.common.world.entity.projectile.OrdnanceEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OrdnanceItem extends Item {
    public static Map<UUID, OrdnanceSoundInstance> sounds = new HashMap<>();

    public OrdnanceItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        pPlayer.startUsingItem(pHand);

        pLevel.playSound(null,
                pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(),
                SoundEvents.TNT_PRIMED, SoundSource.PLAYERS,
                0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F)
        );
        if (!itemstack.getOrCreateTag().getBoolean("Lit")) {
            itemstack.getOrCreateTag().putBoolean("Lit", true);
            itemstack.getOrCreateTag().putInt("MaxFuseTime", OrdnanceEffects.DEFAULT_EFFECT_PARAMS.maxFuseTime());
            itemstack.getOrCreateTag().putInt("FuseTime", 0);
//            itemstack.getOrCreateTag().putUUID("SoundID", UUID.randomUUID());

//            if (pLevel.isClientSide()) {
//                OrdnanceSoundInstance sound = new OrdnanceSoundInstance(pPlayer, itemstack.getOrCreateTag().getInt("MaxFuseTime"), () -> (float)itemstack.getOrCreateTag().getInt("FuseTime"));
//                sounds.put(itemstack.getOrCreateTag().getUUID("SoundID"), sound);
//                Minecraft.getInstance().getSoundManager().play(sound);
//            }
        }

        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving, int pTimeLeft) {
        pLevel.playSound(null,
                pEntityLiving.getX(), pEntityLiving.getY(), pEntityLiving.getZ(),
                SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS,
                0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F)
        );
        pStack.getOrCreateTag().putBoolean("Lit", false);
        if (!pLevel.isClientSide) {
            int fuseTime = pStack.getOrCreateTag().getInt("FuseTime");
            if (fuseTime >= pStack.getOrCreateTag().getInt("MaxFuseTime")) {
                OrdnanceEntity.createOrdnanceExplosion(pEntityLiving.getEyePosition(), pLevel, pEntityLiving, null, OrdnanceEffects.DEFAULT_EFFECT_PARAMS);
            } else {
                OrdnanceEntity entity = OrdnanceEntity.toss(pLevel, pEntityLiving);
                entity.setFuseTime(fuseTime);
                pLevel.addFreshEntity(entity);
            }
        } else {
//            if (pStack.getOrCreateTag().hasUUID("SoundID")) {
//                UUID soundID = pStack.getOrCreateTag().getUUID("SoundID");
//                if (sounds.containsKey(soundID)) {
//                    sounds.get(soundID).stopPlaying();
//                    sounds.remove(soundID);
//                } else {
//                    Clinker.LOGGER.info("a");
//                }
//            } else {
//                Clinker.LOGGER.info("b");
//            }
//
//            for (OrdnanceSoundInstance value : sounds.values()) {
//                value.stopPlaying();
//            }
//            sounds.clear();

        }

        if (pEntityLiving instanceof Player player) {
            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                pStack.shrink(1);
            }
            player.getCooldowns().addCooldown(this, 40);
        }

    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
        if (!pStack.getOrCreateTag().getBoolean("Lit")) return;

//        if (pLevel.isClientSide() && pStack.getOrCreateTag().hasUUID("SoundID")) {
//            UUID soundID = pStack.getOrCreateTag().getUUID("SoundID");
//            if (!sounds.containsKey(soundID)) {
//                OrdnanceSoundInstance sound = new OrdnanceSoundInstance(pEntity, pStack.getOrCreateTag().getInt("MaxFuseTime"), () -> (float)pStack.getOrCreateTag().getInt("FuseTime"));
//                sounds.put(soundID, sound);
//                Minecraft.getInstance().getSoundManager().play(sound);
//            }
//
//        }

        int fuseTime = pStack.getOrCreateTag().getInt("FuseTime") + 1;
        pStack.getOrCreateTag().putInt("FuseTime", fuseTime);

        if (fuseTime >= pStack.getOrCreateTag().getInt("MaxFuseTime")) {
            if (pEntity instanceof LivingEntity livingEntity) {
                if (livingEntity.getUseItem() == pStack) {
                    livingEntity.releaseUsingItem();
                } else {
                    pStack.releaseUsing(pLevel, livingEntity, 0);
                    pStack.onStopUsing(livingEntity, 0);
                }
            }
        }
    }
}
