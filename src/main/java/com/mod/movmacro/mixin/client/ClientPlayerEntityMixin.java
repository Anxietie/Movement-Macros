package com.mod.movmacro.mixin.client;

import com.mod.movmacro.events.ClientPlayerLandingEvent;
import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntity {
	@Unique
	private boolean falling = false;

	@Inject(method = "tickMovement", at = @At("TAIL"))
	private void movmacro$landEventInvoker(CallbackInfo ci) {
		if (!canFall(this))
			return;

		if (this.isOnGround()) {
			if (this.falling) {
				//noinspection deprecation
				ClientPlayerLandingEvent.EVENT.invoker().interact(this, this.getLandingPos());
				this.falling = false;
			}
		}
		else
			this.falling = true;
	}

	@Unique
	private boolean canFall(PlayerEntity player) {
		return !player.isTouchingWater() &&
				!player.getAbilities().flying &&
				!player.isFallFlying() && // fallflying is elytra apparently
				!player.isClimbing() &&
				!player.hasVehicle();
	}

	public ClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) { super(world, pos, yaw, gameProfile); }
	@Override
	public boolean isSpectator() { return false; }
	@Override
	public boolean isCreative() { return false; }
}
