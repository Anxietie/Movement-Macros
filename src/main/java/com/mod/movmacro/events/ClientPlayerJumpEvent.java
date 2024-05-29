package com.mod.movmacro.events;

import com.mod.movmacro.macro.MacroString;
import com.mod.movmacro.macro.types.EventType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public interface ClientPlayerJumpEvent {
	Event<ClientPlayerJumpEvent> EVENT = EventFactory.createArrayBacked(ClientPlayerJumpEvent.class,
			(listeners) -> (player) -> {
				for (ClientPlayerJumpEvent listener : listeners)
					listener.interact(player);
			});

	void interact(PlayerEntity player);

	static void register() {
		EVENT.register((player) -> {
			MacroString string = ClientEndTickEvent.getRunningMacro();
			if (string != null)
				string.runEventMacro(MinecraftClient.getInstance(), EventType.PLAYER_JUMP);
		});
	}
}
