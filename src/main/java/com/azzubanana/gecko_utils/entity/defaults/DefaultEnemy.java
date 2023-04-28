package com.azzubanana.gecko_utils.entity.defaults;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class DefaultEnemy extends Monster implements IAnimatable {
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure;
    protected DefaultEnemy(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    protected InteractionHand getLeftHand() {
        return this.isLeftHanded() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
    }

    protected InteractionHand getRightHand() {
        return !this.isLeftHanded() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
    }

// Animations
    private <E extends IAnimatable> PlayState movementPredicate(AnimationEvent<E> event) {
        if (this.animationprocedure == "empty") {
            if (!event.isMoving() && event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F) {
                if (this.isDeadOrDying()) {
                    event.getController().setAnimation((new AnimationBuilder()).addAnimation("death", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
                    return PlayState.CONTINUE;
                } else if (this.isInWaterOrBubble()) {
                    event.getController().setAnimation((new AnimationBuilder()).addAnimation("swim", ILoopType.EDefaultLoopTypes.LOOP));
                    return PlayState.CONTINUE;
                } else if (this.isShiftKeyDown()) {
                    event.getController().setAnimation((new AnimationBuilder()).addAnimation("sneak", ILoopType.EDefaultLoopTypes.LOOP));
                    return PlayState.CONTINUE;
                } else if (this.isSprinting()) {
                    event.getController().setAnimation((new AnimationBuilder()).addAnimation("sprint", ILoopType.EDefaultLoopTypes.LOOP));
                    return PlayState.CONTINUE;
                } else {
                    event.getController().setAnimation((new AnimationBuilder()).addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP));
                    return PlayState.CONTINUE;
                }
            } else {
                event.getController().setAnimation((new AnimationBuilder()).addAnimation("walk", ILoopType.EDefaultLoopTypes.LOOP));
                return PlayState.CONTINUE;
            }
        } else {
            return PlayState.STOP;
        }
    }

    private <E extends IAnimatable> PlayState attackingPredicate(AnimationEvent<E> event) {
        double d1 = this.getX() - this.xOld;
        double d0 = this.getZ() - this.zOld;
        float velocity = (float)Math.sqrt(d1 * d1 + d0 * d0);
        if (this.getAttackAnim(event.getPartialTick()) > 0.0F && !this.swinging) {
            this.swinging = true;
            this.lastSwing = this.level.getGameTime();
        }

        if (this.swinging && this.lastSwing + 7L <= this.level.getGameTime()) {
            this.swinging = false;
        }

        if (this.swinging && event.getController().getAnimationState().equals(AnimationState.Stopped)) {
            event.getController().markNeedsReload();
            event.getController().setAnimation((new AnimationBuilder()).addAnimation("attack", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
            return PlayState.CONTINUE;
        } else {
            return PlayState.CONTINUE;
        }
    }

    private <E extends IAnimatable> PlayState procedurePredicate(AnimationEvent<E> event) {
        if (this.animationprocedure != "empty" && event.getController().getAnimationState().equals(AnimationState.Stopped)) {
            event.getController().setAnimation((new AnimationBuilder()).addAnimation(this.animationprocedure, ILoopType.EDefaultLoopTypes.PLAY_ONCE));
            if (event.getController().getAnimationState().equals(AnimationState.Stopped)) {
                this.animationprocedure = "empty";
                event.getController().markNeedsReload();
            }
        }

        return PlayState.CONTINUE;
    }

    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "movement", 0, this::movementPredicate));
        data.addAnimationController(new AnimationController(this, "attacking", 0, this::attackingPredicate));
        data.addAnimationController(new AnimationController(this, "procedure", 0, this::procedurePredicate));
    }

    @Override
    public AnimationFactory getFactory() { return factory; }
}
