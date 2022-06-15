package birsy.clinker.common.alchemy.anatomy;

import birsy.clinker.common.alchemy.chemicals.Chemical;
import birsy.clinker.common.alchemy.chemicals.Mixture;
import net.minecraft.world.entity.LivingEntity;

import java.util.Arrays;
import java.util.List;

public class EntityBody {
    final EntityBodyPart[] bodyParts;
    final float totalBlood;
    public Chemical bloodMaterial;
    public final LivingEntity bodyOwner;

    public EntityBody( LivingEntity bodyOwner, float totalBlood, EntityBodyPart... bodyParts ) {
        this.totalBlood = totalBlood;
        this.bodyOwner = bodyOwner;
        this.bodyParts = bodyParts;
    }

    public class EntityBodyPart {
        public final EntityBodyPart parent;
        public List<EntityBodyPart> children;
        final boolean isVital;
        public List<EntityOrgan> organs;
        public Mixture chemicalMakeup;
        public Chemical fleshMaterial;
        boolean hasBloodflow = true;

        public EntityBodyPart(EntityBodyPart parent, boolean isVital, EntityOrgan... organs) {
            this.parent = parent;
            this.parent.children.add(this);

            this.isVital = isVital;
            this.organs = Arrays.asList(organs);
        }

        public void tick() {
            organs.forEach(organ -> organ.tick());
        }
    }

    public class EntityOrgan {

        public EntityOrgan() {}

        public void tick() {

        }
    }
}
