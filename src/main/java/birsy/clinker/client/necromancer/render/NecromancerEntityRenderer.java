package birsy.clinker.client.necromancer.render;

import birsy.clinker.client.necromancer.animate.Bone;
import birsy.clinker.client.necromancer.animate.Skeleton;
import birsy.clinker.client.necromancer.animate.SkeletonOwner;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class NecromancerEntityRenderer<T extends Entity & SkeletonOwner> extends EntityRenderer<T> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/placeholder.png");
    private final float[] finalBoneTransforms = new float[256 * 3 * 4]; // maximum of 256 bones
    private final Skin skin;
    private final Skeleton skeleton;

    protected NecromancerEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);

        SkinBuilder skinBuilder = new SkinBuilder(64, 32);
        skinBuilder.cube(-8, -8, -8, 16, 16, 16, 0, 0, 0);
        skinBuilder.cube(-4, -4, -4, 8, 8, 8, 8, 8, 0);
        this.skin = skinBuilder.build();

        this.skeleton = new Skeleton()
                .addRoot(new Bone(0, 0, 0)
                        .addChild(new Bone(0, 8, 0))
                );
    }

    @Override
    public void render(T pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        for (Bone root : this.skeleton.roots()) {
            root.precalculatePoseSpaceMatricies();
        }
        for (int i = 0; i < this.skeleton.boneByIndex().size(); i++) {
            this.skeleton.boneByIndex().get(i).poseSpaceMatrix().get(this.finalBoneTransforms, i * 4 * 3);
        }

        ShaderInstance shader = RenderSystem.getShader();
        shader.safeGetUniform("BoneTransforms").set(this.finalBoneTransforms);

        skin.draw(pPoseStack);
    }

    @Override
    public ResourceLocation getTextureLocation(T pEntity) {
        return TEXTURE;
    }
}
