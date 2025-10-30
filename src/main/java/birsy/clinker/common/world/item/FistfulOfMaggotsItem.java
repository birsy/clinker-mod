package birsy.clinker.common.world.item;

import birsy.clinker.common.world.entity.projectile.RerollFlaskEntity;
import birsy.clinker.core.registry.ClinkerParticles;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FistfulOfMaggotsItem extends Item {
    public FistfulOfMaggotsItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);

        // spawn a load of particles
        if (true) {
            Vec3 facingDirection = player.getLookAngle();
            Vec3 eyePos = player.getEyePosition().add(0, -0.2F, 0);

            double speed = 0.4;
            double posRandom = 0.2F;
            double speedRandom = 0.1F;
            for (int i = 0; i < 64; i++) {
                level.addParticle(
                        ClinkerParticles.WRITHING_MAGGOT.get(), false,
                        eyePos.x() + facingDirection.x() * 0.5 + level.random.nextGaussian() * posRandom,
                        eyePos.y() + facingDirection.y() * 0.5 + level.random.nextGaussian() * posRandom,
                        eyePos.z() + facingDirection.z() * 0.5 + level.random.nextGaussian() * posRandom,
                        facingDirection.x() * speed + level.random.nextGaussian() * speedRandom,
                        facingDirection.y() * speed * 2 + level.random.nextGaussian() * speedRandom,
                        facingDirection.z() * speed + level.random.nextGaussian() * speedRandom
                );
            }
        }
        player.getCooldowns().addCooldown(this, 20);
        if (!player.getAbilities().instabuild) itemStack.shrink(1);
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }
}
