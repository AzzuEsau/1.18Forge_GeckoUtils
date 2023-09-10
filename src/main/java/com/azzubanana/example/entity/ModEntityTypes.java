package com.azzubanana.example.entity;

import com.azzubanana.example.entity.tameableEntity.WolfTamableExample;
import com.azzubanana.gecko_utils.GeckoUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, GeckoUtils.MOD_ID);
    public static void register (IEventBus eventBus)
    {
        ENTITY_TYPES.register(eventBus);
    }




    public static final RegistryObject<EntityType<WolfTamableExample>> TAMEABLE_ENTITY = ENTITY_TYPES.register("tameable_entity",
            () -> EntityType.Builder.of(WolfTamableExample::new, MobCategory.CREATURE)
                    .sized(1.2F, 1.2F)
                    .build(new ResourceLocation(GeckoUtils.MOD_ID, "tameable_entity").toString()));
}
