package com.mod.movmacro;

import com.mod.movmacro.events.*;
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
		ClientPlayerJumpEvent.register();

		/* TODO:
		 * add debug mode
		 * make camera macro better
		 */
	}
}
