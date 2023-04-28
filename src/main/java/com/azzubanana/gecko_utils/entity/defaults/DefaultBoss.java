package com.azzubanana.gecko_utils.entity.defaults;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class DefaultBoss extends DefaultEnemy{
    private final ServerBossEvent bossEvent;

    protected DefaultBoss(EntityType<? extends Monster> pEntityType, Level pLevel, String name, BossEvent.BossBarColor color) {
        super(pEntityType, pLevel);
        bossEvent =  new ServerBossEvent(Component.nullToEmpty(name), color, BossEvent.BossBarOverlay.PROGRESS);
    }

    @Override
    protected void actuallyHurt(DamageSource p_21240_, float p_21241_) {
        super.actuallyHurt(p_21240_, p_21241_);
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    public void startSeenByPlayer(ServerPlayer serverPlayer) {
        super.startSeenByPlayer(serverPlayer);
        this.bossEvent.addPlayer(serverPlayer);
    }

    public void stopSeenByPlayer(ServerPlayer serverPlayer) {
        super.stopSeenByPlayer(serverPlayer);
        this.bossEvent.removePlayer(serverPlayer);
    }


}
