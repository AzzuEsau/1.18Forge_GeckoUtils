package com.azzubanana.example.client.renderer;

import com.azzubanana.gecko_utils.GeckoUtils;
import com.azzubanana.gecko_utils.client.model.DefaultTamableModel;
import com.azzubanana.gecko_utils.client.renderer.DefaultTamableRender;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class WolfTamableRendererExample extends DefaultTamableRender {
    public WolfTamableRendererExample(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultTamableModel(GeckoUtils.MOD_ID, "freminet"));
    }
}
