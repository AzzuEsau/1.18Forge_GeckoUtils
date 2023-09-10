package com.azzubanana.gecko_utils.client.entity.renderer;


import com.azzubanana.gecko_utils.entity.defaults.DefaultEnemy;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.example.client.DefaultBipedBoneIdents;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;

public class DefaultEnemyRender extends ExtendedGeoEntityRenderer<DefaultEnemy> {
    protected ItemStack mainHandItem, offHandItem, helmetItem, chestplateItem, leggingsItem, bootsItem;

    protected DefaultEnemyRender(EntityRendererProvider.Context renderManager, AnimatedGeoModel<DefaultEnemy> modelProvider) {
        super(renderManager, modelProvider);
    }


    @Override
    public void renderEarly(DefaultEnemy animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource,
                            VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float partialTicks) {
        super.renderEarly(animatable, poseStack, partialTick, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, partialTicks);

        this.mainHandItem = animatable.getItemBySlot(EquipmentSlot.MAINHAND);
        this.offHandItem = animatable.getItemBySlot(EquipmentSlot.OFFHAND);
        this.helmetItem = animatable.getItemBySlot(EquipmentSlot.HEAD);
        this.chestplateItem = animatable.getItemBySlot(EquipmentSlot.CHEST);
        this.leggingsItem = animatable.getItemBySlot(EquipmentSlot.LEGS);
        this.bootsItem = animatable.getItemBySlot(EquipmentSlot.FEET);
    }

    @Override
    protected boolean isArmorBone(GeoBone bone) {
        return false;
    }

    @Nullable
    @Override
    protected ResourceLocation getTextureForBone(String boneName, DefaultEnemy animatable) {
        return null;
    }

    @Nullable
    @Override
    protected ItemStack getHeldItemForBone(String boneName, DefaultEnemy animatable) {
        return switch (boneName) {
            case DefaultBipedBoneIdents.LEFT_HAND_BONE_IDENT -> animatable.isLeftHanded() ? mainHandItem : offHandItem;
            case DefaultBipedBoneIdents.RIGHT_HAND_BONE_IDENT -> animatable.isLeftHanded() ? offHandItem : mainHandItem;
            default -> null;
        };
    }

    @Override
    protected ItemTransforms.TransformType getCameraTransformForItemAtBone(ItemStack stack, String boneName) {
        return switch (boneName) {
            case DefaultBipedBoneIdents.LEFT_HAND_BONE_IDENT, DefaultBipedBoneIdents.RIGHT_HAND_BONE_IDENT -> ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND; // Do Defaults
            default -> ItemTransforms.TransformType.NONE;
        };
    }

    @Nullable
    @Override
    protected BlockState getHeldBlockForBone(String boneName, DefaultEnemy animatable) {
        return null;
    }

    @Override
    protected void preRenderItem(PoseStack stack, ItemStack item, String boneName, DefaultEnemy animatable, IBone bone) {
        if (item == this.mainHandItem) {
            stack.mulPose(Vector3f.XP.rotationDegrees(-90f));

            if (item.getItem() instanceof ShieldItem)
                stack.translate(0, 0.125, -0.25);
        }
        else if (item == this.offHandItem) {
            stack.mulPose(Vector3f.XP.rotationDegrees(-90f));

            if (item.getItem() instanceof ShieldItem) {
                stack.translate(0, 0.125, 0.25);
                stack.mulPose(Vector3f.YP.rotationDegrees(180));
            }
        }
    }

    @Override
    protected void preRenderBlock(PoseStack poseStack, BlockState state, String boneName, DefaultEnemy animatable) {

    }

    @Override
    protected void postRenderItem(PoseStack poseStack, ItemStack stack, String boneName, DefaultEnemy animatable, IBone bone) {

    }

    @Override
    protected void postRenderBlock(PoseStack poseStack, BlockState state, String boneName, DefaultEnemy animatable) {

    }
}
