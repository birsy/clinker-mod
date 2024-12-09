package birsy.clinker.common.world.item;

import birsy.clinker.client.sound.OrdnanceSoundInstance;
import birsy.clinker.common.world.entity.projectile.OrdnanceEntity;
import birsy.clinker.common.world.item.components.FuseTimer;
import birsy.clinker.common.world.item.components.OrdnanceEffects;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerDataComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OrdnanceItem extends Item {
    public static Map<UUID, OrdnanceSoundInstance> sounds = new HashMap<>();

    public OrdnanceItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemstack = super.getDefaultInstance();
        itemstack.set(ClinkerDataComponents.ORDNANCE_EFFECTS.get(), OrdnanceEffects.DEFAULT);
        itemstack.set(ClinkerDataComponents.FUSE_TIMER.get(), new FuseTimer(0, false));
        return itemstack;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        OrdnanceEffects effects = getOrdnanceEffects(stack);
        effects.addToTooltip(context, tooltipComponents::add, tooltipFlag);
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    private OrdnanceEffects getOrdnanceEffects(ItemStack stack) {
        return stack.getOrDefault(ClinkerDataComponents.ORDNANCE_EFFECTS.get(), OrdnanceEffects.DEFAULT);
    }
    private FuseTimer getFuseTimer(ItemStack stack) {
        return stack.getOrDefault(ClinkerDataComponents.FUSE_TIMER.get(), FuseTimer.EMPTY);
    }



    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        pPlayer.startUsingItem(pHand);

        FuseTimer fuseTimer = getFuseTimer(stack);
        if (fuseTimer.lit()) {
            pLevel.playSound(null,
                    pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(),
                    SoundEvents.TNT_PRIMED, SoundSource.PLAYERS,
                    0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 1F)
            );
        } else {
            stack.set(ClinkerDataComponents.FUSE_TIMER.get(), new FuseTimer(fuseTimer.tickCount(), true));
        }

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return this.getOrdnanceEffects(stack).fuseTime() + 1;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level pLevel, LivingEntity pEntityLiving, int pTimeLeft) {
        pLevel.playSound(null,
                pEntityLiving.getX(), pEntityLiving.getY(), pEntityLiving.getZ(),
                SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS,
                0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        FuseTimer fuseTimer = getFuseTimer(stack);
        OrdnanceEffects effects = getOrdnanceEffects(stack);
        if (!pLevel.isClientSide) {
            OrdnanceEntity ordnance = OrdnanceEntity.toss(pLevel, pEntityLiving);
            ordnance.setEffects(effects);
            ordnance.setFuseTime(fuseTimer.tickCount());
            pLevel.addFreshEntity(ordnance);
        }

        use(pEntityLiving, stack);
    }

    private void use(Entity entity, ItemStack stack) {
        if (entity instanceof Player player) {
            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            player.getCooldowns().addCooldown(this, 40);
        } else {
            stack.shrink(1);
        }
        stack.set(ClinkerDataComponents.FUSE_TIMER.get(), new FuseTimer(0, false));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }


    @Override
    public void inventoryTick(ItemStack stack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(stack, pLevel, pEntity, pSlotId, pIsSelected);
        FuseTimer fuseTimer = getFuseTimer(stack);
        if (!fuseTimer.lit()) return;

        OrdnanceEffects effects = getOrdnanceEffects(stack);

        if (fuseTimer.tickCount() > effects.fuseTime()) {
            OrdnanceEntity.createOrdnanceExplosion(pEntity.position(), pLevel, pEntity, null, effects);
            this.use(pEntity, stack);
        } else {
            stack.set(ClinkerDataComponents.FUSE_TIMER.get(), new FuseTimer(fuseTimer.tickCount() + 1, true));
        }
        //if (!pStack.getOrCreateTag().getBoolean("Lit")) return;

//        if (pLevel.isClientSide() && pStack.getOrCreateTag().hasUUID("SoundID")) {
//            UUID soundID = pStack.getOrCreateTag().getUUID("SoundID");
//            if (!sounds.containsKey(soundID)) {
//                OrdnanceSoundInstance sound = new OrdnanceSoundInstance(pEntity, pStack.getOrCreateTag().getInt("MaxFuseTime"), () -> (float)pStack.getOrCreateTag().getInt("FuseTime"));
//                sounds.put(soundID, sound);
//                Minecraft.getInstance().getSoundManager().play(sound);
//            }
//
//        }

//        int fuseTime = pStack.getOrCreateTag().getInt("FuseTime") + 1;
//        pStack.getOrCreateTag().putInt("FuseTime", fuseTime);
//
//        if (fuseTime >= pStack.getOrCreateTag().getInt("MaxFuseTime")) {
//            if (pEntity instanceof LivingEntity livingEntity) {
//                if (livingEntity.getUseItem() == pStack) {
//                    livingEntity.releaseUsingItem();
//                } else {
//                    pStack.releaseUsing(pLevel, livingEntity, 0);
//                    pStack.onStopUsing(livingEntity, 0);
//                }
//            }
//        }
    }
}
