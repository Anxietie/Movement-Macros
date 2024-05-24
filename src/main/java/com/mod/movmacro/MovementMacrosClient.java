package com.mod.movmacro;

import com.mod.movmacro.events.ClientCommandRegisterEvent;
import com.mod.movmacro.events.ClientPlayerLandingEvent;
import com.mod.movmacro.events.ClientStartEvent;
import com.mod.movmacro.events.ClientEndTickEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class MovementMacrosClient implements ClientModInitializer {
	public static final String MODID = "movmacro";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitializeClient() {
		ClientStartEvent.register();
		ClientEndTickEvent.register();
		ClientCommandRegisterEvent.register();
		ClientPlayerLandingEvent.register();

		/* TODO:
		 * add triggered stops (either by keybind or using ids)
		 * make more memory efficient lol this thing uses so much memory
		 */
	}
}
