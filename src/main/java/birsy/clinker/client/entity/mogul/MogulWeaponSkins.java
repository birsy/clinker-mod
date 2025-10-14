package birsy.clinker.client.entity.mogul;

import birsy.clinker.core.Clinker;
import foundry.veil.api.client.necromancer.Skeleton;
import foundry.veil.api.client.necromancer.render.Skin;
import net.minecraft.resources.ResourceLocation;

public class MogulWeaponSkins {
    public static final ResourceLocation WARHOOK_TEXTURE_LOCATION = Clinker.resource("textures/entity/mogul_warhook.png");
    public static final ResourceLocation WARHOOK_CHAIN_TEXTURE_LOCATION = Clinker.resource("textures/entity/mogul_warhook_chain.png");

    public static Skin createWarhookModel(String parentName) {
        Skin.Builder builder = Skin.builder(32, 32);

        builder.startBone(parentName);
        builder.addCube(1F, 4F, 2F, 0F, 16.5F, -6F, 0F, 0F, 0F, 6F, 23F, false);
        builder.addCube(2F, 14F, 3F, -0.5F, -9.5F, -1.5F, -0.2F, -0.2F, -0.2F, 6F, 0F, false);
        builder.addCube(1F, 29F, 2F, 0F, -17.5F, -1F, 0F, 0F, 0F, 0F, 0F, false);
        builder.addCube(1F, 1F, 4F, 0F, -18.5F, -2F, 0F, 0F, 0F, 16F, 23F, false);
        builder.addCube(1F, 1F, 4F, 0F, -21.5F, -2F, 0F, 0F, 0F, 22F, 24F, false);
        builder.addCube(1F, 2F, 1F, 0F, -20.5F, -2F, 0F, 0F, 0F, 16F, 4F, false);
        builder.addCube(1F, 2F, 1F, 0F, -20.5F, 1F, 0F, 0F, 0F, 25F, 4F, false);
        builder.addCube(2F, 1F, 6F, -0.5F, -15.5F, -3F, 0F, 0F, 0F, 6F, 23F, false);
        builder.addCube(1F, 2F, 7F, 0F, 9.5F, 1F, 0F, 0F, 0F, 16F, 0F, false);
        builder.addCube(1F, 9F, 2F, 0F, 11.5F, 6F, 0F, 0F, 0F, 18F, 9F, false);
        builder.addCube(1F, 2F, 2F, 0F, 18.5F, 4F, 0F, 0F, 0F, 16F, 0F, false);
        builder.addCube(1F, 2F, 2F, 0F, 18.5F, -4F, 0F, 0F, 0F, 25F, 0F, false);
        builder.addCube(1F, 2F, 10F, 0F, 20.5F, -4F, 0F, 0F, 0F, 6F, 11F, false);

        return builder.build();
    }
}
