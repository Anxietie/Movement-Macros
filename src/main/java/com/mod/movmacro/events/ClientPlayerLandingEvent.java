package com.mod.movmacro.events;

import com.mod.movmacro.macro.MacroManager;
import com.mod.movmacro.macro.MacroString;
import com.mod.movmacro.macro.types.EventType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public interface ClientPlayerLandingEvent {
	Event<ClientPlayerLandingEvent> EVENT = EventFactory.createArrayBacked(ClientPlayerLandingEvent.class,
			(listeners) -> (player, pos) -> {
				for (ClientPlayerLandingEvent listener : listeners)
					listener.interact(player, pos);
			});

	void interact(PlayerEntity player, BlockPos pos);

	static void register() {
		EVENT.register((player, pos) -> {
			MinecraftClient client = MinecraftClient.getInstance();

			if (MacroManager.inDebugMode()) {
				ClientEndTickEvent.stopCounting();
				MacroManager.updateJumpTime();
				ClientEndTickEvent.resetCountingTicks();
			}

			MacroString string = MacroManager.getRunningMacro();
			if (string != null)
				string.runEventMacro(client, EventType.PLAYER_LAND);
		});
	}
}
