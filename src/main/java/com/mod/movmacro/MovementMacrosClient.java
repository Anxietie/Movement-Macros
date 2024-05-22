package com.mod.movmacro;

import com.mod.movmacro.events.ClientCommandRegisterEvent;
import com.mod.movmacro.events.ClientStartEvent;
import com.mod.movmacro.macro.MacroManager;
import com.mod.movmacro.events.ClientEndTickEvent;
import com.mod.movmacro.macro.types.MovementType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class MovementMacrosClient implements ClientModInitializer {
	public static final String MODID = "movmacro";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitializeClient() {
		ClientStartEvent.registerClientStart();
		ClientEndTickEvent.registerClientEndTicks();
		ClientCommandRegisterEvent.registerClientCommands();

		/* TODO:
		 * add event based macros
		 * add triggered stops (either by keybind or using ids)
		 */
	}
}
