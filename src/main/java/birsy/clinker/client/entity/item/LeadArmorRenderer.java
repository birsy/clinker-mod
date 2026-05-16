package birsy.clinker.client.entity.item;

import birsy.clinker.core.Clinker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.common.util.Lazy;

public class LeadArmorRenderer implements IClientItemExtensions {
    public static final ResourceLocation TEXTURE_LOCATION = Clinker.resource("textures/models/armor/lead_armor.png");
    final Lazy<Model> model;

    public LeadArmorRenderer() {
        this.model = Lazy.of(() -> {
            EntityModelSet modelSet = Minecraft.getInstance().getEntityModels();
            return new LeadArmorRenderer.Model(modelSet.bakeLayer(Model.LAYER_LOCATION));
        });
    }

    @Override
    public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
        return equipmentSlot == EquipmentSlot.CHEST || equipmentSlot == EquipmentSlot.HEAD ? model.get() : original;
    }

    @Override
    public void setupModelAnimations(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, net.minecraft.client.model.Model model, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (equipmentSlot != EquipmentSlot.LEGS && model instanceof Model leadArmorModel) {
            leadArmorModel.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
        IClientItemExtensions.super.setupModelAnimations(livingEntity, itemStack, equipmentSlot, model, limbSwing, limbSwingAmount, partialTick, ageInTicks, netHeadYaw, headPitch);
    }

    public static class Model extends HumanoidModel<LivingEntity> {
        public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Clinker.resource("lead_armor"), "main");
        public final ModelPart tassetFront, tassetBack, tassetRight, tassetLeft;
        public final ModelPart aventailBack, aventailRight, aventailLeft;

        public Model(ModelPart root) {
            super(root);
            this.tassetFront = body.getChild("tasset_front");
            this.tassetBack = body.getChild("tasset_back");
            this.tassetRight = body.getChild("tasset_right");
            this.tassetLeft = body.getChild("tasset_left");
            this.aventailBack = head.getChild("aventail_back");
            this.aventailRight = head.getChild("aventail_right");
            this.aventailLeft = head.getChild("aventail_left");
        }

        public static LayerDefinition createMesh() {
            MeshDefinition meshdefinition = HumanoidModel.createMesh(LayerDefinitions.OUTER_ARMOR_DEFORMATION, 0.0F);
            PartDefinition root = meshdefinition.getRoot();

            // chestpiece tassets
            PartDefinition body = meshdefinition.getRoot().getChild("body");
            CubeDeformation zTassetDeformation = new CubeDeformation(0.5F, 0.0F, 0.0F);
            body.addOrReplaceChild(
                    "tasset_front",
                    CubeListBuilder.create()
                            .texOffs(18, 40)
                            .addBox(-2.0F, 0.0F, -3.0F + 0.2F, 4.0F, 5.0F, 1.0F, zTassetDeformation),
                    PartPose.offset(0.0F, 12.0F, 0F)
            );
            body.addOrReplaceChild(
                    "tasset_back",
                    CubeListBuilder.create()
                            .texOffs(28, 40)
                            .addBox(-2.0F, -1.0F, 2.0F + 0.1F, 4.0F, 5.0F, 1.0F, zTassetDeformation),
                    PartPose.offset(0.0F, 12.0F, 0.0F)
            );
            CubeDeformation xTassetDeformation = new CubeDeformation(0.0F, 0.0F, 1.0F);
            body.addOrReplaceChild(
                    "tasset_right",
                    CubeListBuilder.create()
                            .texOffs(18, 46)
                            .addBox(-0.5F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, xTassetDeformation),
                    PartPose.offset(-4.5F, 9.5F, 0.0F)
            );
            body.addOrReplaceChild(
                    "tasset_left",
                    CubeListBuilder.create()
                            .texOffs(18, 46).mirror()
                            .addBox(-0.5F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, xTassetDeformation),
                    PartPose.offset(4.5F, 9.5F, 0.0F)
            );

            // helmet stuffs
            PartDefinition head = root.addOrReplaceChild(
                    "head",
                    CubeListBuilder.create()
                            .texOffs(0, 0)
                            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, LayerDefinitions.INNER_ARMOR_DEFORMATION)
                            .texOffs(0, 32)
                            .addBox(-3, -10.5F, -3, 6.0F, 2.0F, 6.0F),
                    PartPose.offset(0.0F, 0.0F, 0.0F)
            );
            head.addOrReplaceChild(
                    "aventail_back",
                    CubeListBuilder.create()
                            .texOffs(0, 40)
                            .addBox(-4.0F, -0.0F, -1.0F, 8.0F, 4.0F, 1.0F),
                    PartPose.offset(0.0F, -5.3F, 4.5F)
            );
            head.addOrReplaceChild(
                    "aventail_right",
                    CubeListBuilder.create()
                            .texOffs(0, 45)
                            .addBox(-0.0F, -0.0F, -4.0F, 1.0F, 4.0F, 8.0F),
                    PartPose.offset(-4.5F, -5.3F, 0.0F)
            );
            head.addOrReplaceChild(
                    "aventail_left",
                    CubeListBuilder.create()
                            .texOffs(0, 45).mirror()
                            .addBox(-1.0F, -0.0F, -4.0F, 1.0F, 4.0F, 8.0F),
                    PartPose.offset(4.5F, -5.3F, 0.0F)
            );

            return LayerDefinition.create(meshdefinition, 64, 64);
        }

        @Override
        public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            //super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            this.aventailLeft.zRot = -0.3F;
            this.aventailRight.zRot = 0.3F;
            this.aventailBack.xRot = 0.3F;

            this.tassetFront.xRot = Math.min(0, Math.min(this.leftLeg.xRot, this.rightLeg.xRot) - this.body.xRot) - 0.2F;
            this.tassetBack.xRot = Math.max(0, Math.max(this.leftLeg.xRot, this.rightLeg.xRot) - this.body.xRot) + 0.2F;
            this.tassetLeft.zRot = -0.3F;
            this.tassetRight.zRot = 0.3F;

            if (entity.isCrouching()) {
                this.tassetFront.z = this.tassetFront.getInitialPose().z - 1.2F;
                this.tassetFront.y = this.tassetFront.getInitialPose().y - 1.2F;
                this.tassetBack.xRot -= 0.4F;
            } else {
                this.tassetFront.z = this.tassetFront.getInitialPose().z;
                this.tassetFront.y = this.tassetFront.getInitialPose().y;
            }

            this.head.y = this.head.getInitialPose().y;
            this.head.xScale = 1.0F;
            this.aventailLeft.y = this.aventailLeft.getInitialPose().y;
            this.aventailRight.y = this.aventailRight.getInitialPose().y;
            if (entity instanceof Piglin || entity instanceof ZombifiedPiglin) {
                this.head.y -= 1.0F;
                this.head.xScale = 1.2F;

                this.aventailLeft.zRot -= 0.4F;
                this.aventailRight.zRot += 0.4F;
                this.aventailLeft.y -= 0.5F;
                this.aventailRight.y -= 0.5F;
            }
        }
    }

}
