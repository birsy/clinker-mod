package birsy.clinker.common.tileentity;

import birsy.clinker.core.registry.ClinkerTileEntities;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SoulWellTileEntity extends BlockEntity implements TickableBlockEntity {
    private int ageInTicks;
    private int prevAgeInTicks;

    private int chargeUpTime = 0;
    private int prevChargeUpTime;

    public SoulWellTileEntity() {
        super(ClinkerTileEntities.SOUL_WELL.get());
    }

    @Override
    public void tick() {
        this.prevAgeInTicks = this.ageInTicks;
        this.ageInTicks++;

        this.prevChargeUpTime = this.chargeUpTime;
        if (chargeUpTime > 60) {
            this.chargeUpTime++;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public float getChargeUpTime (float partialTicks) {
        return Mth.lerp(partialTicks, this.prevChargeUpTime, this.chargeUpTime);
    }

    @OnlyIn(Dist.CLIENT)
    public float getAgeInTicks (float partialTicks) {
        return Mth.lerp(partialTicks, this.prevAgeInTicks, this.ageInTicks);
    }
}
