package com.mod.movmacro.events;

import com.mod.movmacro.macro.MacroManager;
import com.mod.movmacro.macro.types.MovementType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

import static com.mod.movmacro.MovementMacrosClient.LOGGER;

@Environment(EnvType.CLIENT)
public class ClientStartEvent {
	public static void registerClientStart() {
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			MovementType.init(client);
			if (!MacroManager.load()) LOGGER.error("FATAL ERROR: CONFIG UNABLE TO INITIALIZE", new Exception());
		});
	}
}
