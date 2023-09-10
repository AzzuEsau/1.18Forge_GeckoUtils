package com.azzubanana.example.entity.tameableEntity;

import com.azzubanana.gecko_utils.entity.defaults.DefaultTamable;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class WolfTamableExample extends DefaultTamable {
    public WolfTamableExample(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel, Items.BONE);
    }

    @Override
    protected void UntamedInteraction(Player player, InteractionHand pHand) {
        if (!this.level.isClientSide()) {
            ServerPlayer serverPlayer = (ServerPlayer)player;
            serverPlayer.server.getPlayerList().broadcastMessage(Component.nullToEmpty("Hola heroe, porque no abrimos league of legends?"), ChatType.CHAT, null);
        }
    }
}
