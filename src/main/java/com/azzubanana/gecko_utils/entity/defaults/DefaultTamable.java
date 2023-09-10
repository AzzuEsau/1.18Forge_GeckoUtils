package com.azzubanana.gecko_utils.entity.defaults;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
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

public abstract class DefaultTamable extends TamableAnimal implements IAnimatable {
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure;

    protected Item itemToInteract;

    protected DefaultTamable(EntityType<? extends TamableAnimal> pEntityType, Level pLevel, Item itemToInteract) {
        super(pEntityType, pLevel);
        this.itemToInteract = itemToInteract;
    }

    public static AttributeSupplier settAtributes()
    {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 5000.00f)
                .add(Attributes.ATTACK_DAMAGE, 4.0f)
                .add(Attributes.ATTACK_SPEED, .75f)
                .add(Attributes.MOVEMENT_SPEED, .2).build();
    }


    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(7, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)).setAlertOthers());
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, null));
    }

// Animations ---------------------------------------------------------
    protected <E extends IAnimatable> PlayState movementPredicate(AnimationEvent<E> event) {
        if (this.animationprocedure == "empty") {
            if (this.isInSittingPose()) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("sit", ILoopType.EDefaultLoopTypes.LOOP));
                return PlayState.CONTINUE;
            }
            else {
                if (!event.isMoving() && event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F) {
                    if (this.isDeadOrDying()) {
                        event.getController().setAnimation((new AnimationBuilder()).addAnimation("death", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
                        return PlayState.CONTINUE;
                    } else if (this.isInWaterOrBubble()) {
                        event.getController().setAnimation((new AnimationBuilder()).addAnimation("swim", ILoopType.EDefaultLoopTypes.LOOP));
                        return PlayState.CONTINUE;
                    } else if (this.isCrouching()) {
                        event.getController().setAnimation((new AnimationBuilder()).addAnimation("sneak", ILoopType.EDefaultLoopTypes.LOOP));
                        return PlayState.CONTINUE;
                    } else {
                        event.getController().setAnimation((new AnimationBuilder()).addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP));
                        return PlayState.CONTINUE;
                    }
                } else {
                    if (this.isSprinting())
                        event.getController().setAnimation((new AnimationBuilder()).addAnimation("run", ILoopType.EDefaultLoopTypes.LOOP));
                    else if (this.isCrouching())
                        event.getController().setAnimation((new AnimationBuilder()).addAnimation("sneak", ILoopType.EDefaultLoopTypes.LOOP));
                    else
                        event.getController().setAnimation((new AnimationBuilder()).addAnimation("walk", ILoopType.EDefaultLoopTypes.LOOP));
                    return PlayState.CONTINUE;
                }
            }
        } else {
            return PlayState.STOP;
        }
    }

    protected <E extends IAnimatable> PlayState attackingPredicate(AnimationEvent<E> event) {
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

    protected <E extends IAnimatable> PlayState procedurePredicate(AnimationEvent<E> event) {
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

// ---------------------------------------------

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) { return null; }


    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pHand);
        Item item = itemStack.getItem();

        if (this.level.isClientSide) {
            // Acciones en el cliente (puedes dejar esto en blanco o agregar lógica específica del cliente si es necesario).
        }
        else {
            if(!isTame()) {
                if (item == itemToInteract) {
                    if (!pPlayer.getAbilities().instabuild)  itemStack.shrink(1);
                    ShrinkToPlayer(pPlayer);

                    return InteractionResult.SUCCESS;
                }
                else {
                    // Actions to do when the player just interact without be tamed and the right item
                    UntamedInteraction(pPlayer, pHand);
                }
            }
            else {
                // Actions to do when the player interact with the entity and it's tamed
                if ( this.isOwnedBy(pPlayer) ) {
                    TamedInteraction(pPlayer, pHand);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean wantsToAttack(LivingEntity pTarget, LivingEntity pOwner) {
        if (pTarget instanceof DefaultTamable) {
            DefaultTamable tameable = (DefaultTamable)pTarget;
            return !tameable.isTame() || tameable.getOwner() != pOwner;
        } else if (pTarget instanceof Player && pOwner instanceof Player && !((Player)pOwner).canHarmPlayer((Player)pTarget)) {
            return false;
        } else if (pTarget instanceof AbstractHorse && ((AbstractHorse)pTarget).isTamed()) {
            return false;
        } else if (pTarget instanceof Player) {
            return false;
        } else {
            return !(pTarget instanceof TamableAnimal) || !((TamableAnimal)pTarget).isTame();
        }
//        return super.wantsToAttack(pTarget, pOwner);
    }

    protected void ShrinkToPlayer(Player pPlayer) {
        this.tame(pPlayer);
        this.navigation.stop();
        this.setTarget((LivingEntity)null);
        this.setOrderedToSit(true);
        this.level.broadcastEntityEvent(this, (byte)7);
    }

    protected void SitOrStand() {
        this.setOrderedToSit(!this.isOrderedToSit());
        this.jumping = false;
        this.navigation.stop();
        this.setTarget((LivingEntity)null);
    }

    protected void TamedInteraction(Player player, InteractionHand pHand) {
            SitOrStand();
    }
    protected abstract void UntamedInteraction(Player player, InteractionHand pHand);
}
