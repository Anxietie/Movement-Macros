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

@Environment(EnvType.CLIENT)
public interface ClientPlayerJumpEvent {
	Event<ClientPlayerJumpEvent> EVENT = EventFactory.createArrayBacked(ClientPlayerJumpEvent.class,
			(listeners) -> (player) -> {
				for (ClientPlayerJumpEvent listener : listeners)
					listener.interact(player);
			});

	void interact(PlayerEntity player);

	static void register() {
		EVENT.register((player) -> {
			MinecraftClient client = MinecraftClient.getInstance();

			if (MacroManager.inDebugMode())
				ClientEndTickEvent.startCounting();

			MacroString string = MacroManager.getRunningMacro();
			if (string != null)
				string.runEventMacro(client, EventType.PLAYER_JUMP);
		});
	}
}
