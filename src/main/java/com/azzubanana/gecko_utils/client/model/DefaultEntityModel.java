package com.azzubanana.gecko_utils.client.model;

import com.azzubanana.gecko_utils.entity.defaults.DefaultEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class DefaultEntityModel extends AnimatedGeoModel<DefaultEntity> {
    protected String name;
    protected String textureName;
    protected String modId;
    public DefaultEntityModel(String modId, String name) {
        this(modId, name, name);
    }

    public DefaultEntityModel(String modId, String name, String textureName)
    {
        this.modId = modId;
        this.name = name;
        this.textureName = textureName;
    }
    @Override
    public ResourceLocation getModelLocation(DefaultEntity object) {
        return new ResourceLocation(modId, "geo/"+name+".geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(DefaultEntity object) {
        return new ResourceLocation(modId, "textures/entity/"+textureName+".png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(DefaultEntity animatable) {
        return new ResourceLocation(modId, "animations/"+name+".animation.json");
    }

    @Override
    public void setCustomAnimations(DefaultEntity animatable, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(animatable, instanceId, animationEvent);
        IBone head = this.getAnimationProcessor().getBone("head");

        EntityModelData extraData = (EntityModelData) animationEvent.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationX(extraData.headPitch * Mth.DEG_TO_RAD);
            head.setRotationY(extraData.netHeadYaw * Mth.DEG_TO_RAD);
        }
    }

}
