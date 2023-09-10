package com.azzubanana.gecko_utils;

import com.azzubanana.example.client.entity.renderer.WolfTamableRendererExample;
import com.azzubanana.example.entity.ModEntityTypes;
import com.azzubanana.gecko_utils.entity.defaults.DefaultTamable;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(GeckoUtils.MOD_ID)
public class GeckoUtils
{
    public static final String MOD_ID = "gecko_utils";
    public static boolean isDevelopment = true;
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public GeckoUtils()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);

        if (isDevelopment) {
            modEventBus.addListener(this::clientSetup);
            modEventBus.addListener(this::entityAttributeEvent);


            ModEntityTypes.register(modEventBus);
        }

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntityTypes.TAMEABLE_ENTITY.get(), WolfTamableRendererExample::new);
    }

    private void entityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.TAMEABLE_ENTITY.get(), DefaultTamable.settAtributes());
    }
}
