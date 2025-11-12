package birsy.clinker.client.particle;

import birsy.clinker.core.registry.ClinkerParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.material.Fluids;

public class ClinkerDripParticles {
    public static TextureSheetParticle createSaltPetreDripHangParticle(
            SimpleParticleType type, ClientLevel level,
            double x, double y, double z,
            double xSpeed, double ySpeed, double zSpeed
    ) {
        DripParticle.DripHangParticle particle = new DripParticle.DripHangParticle(
                level, x, y, z, Fluids.EMPTY, ClinkerParticles.FALLING_SALTPETRE.get()
        );
        particle.gravity *= 0.01F;
        particle.setLifetime(100);
        particle.setColor(204F/255F, 196F/255F, 181F/255F);
        return particle;
    }

    public static TextureSheetParticle createSaltPetreDripFallParticle(
            SimpleParticleType type, ClientLevel level,
            double x, double y, double z,
            double xSpeed, double ySpeed, double zSpeed
    ) {
        DripParticle.FallAndLandParticle particle = new DripParticle.FallAndLandParticle(
                level, x, y, z, Fluids.EMPTY, ClinkerParticles.LANDING_SALTPETRE.get()
        );
        particle.gravity = 0.01F;
        particle.setColor(204F/255F, 196F/255F, 181F/255F);
        return particle;
    }

    public static TextureSheetParticle createSaltPetreDripLandParticle(
            SimpleParticleType type, ClientLevel level,
            double x, double y, double z,
            double xSpeed, double ySpeed, double zSpeed
    ) {
        DripParticle.DripLandParticle particle = new DripParticle.DripLandParticle(level, x, y, z, Fluids.EMPTY);
        particle.setLifetime((int) (28.0 / (Math.random() * 0.8 + 0.2)));
        particle.setColor(204F/255F, 196F/255F, 181F/255F);
        return particle;
    }
}
