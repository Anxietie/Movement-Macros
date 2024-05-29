package com.mod.movmacro.mixin.client;

import com.mod.movmacro.events.ClientPlayerJumpEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
	@Inject(method = "jump", at = @At("TAIL"))
	public void movmacro$jumpEventInvoker(CallbackInfo ci) {
		//noinspection ConstantValue
		if (!((Object) this instanceof ClientPlayerEntity))
			return;

		ClientPlayerJumpEvent.EVENT.invoker().interact((PlayerEntity) (LivingEntity) this);
	}

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) { super(entityType, world); }
}
