package com.mod.movmacro;

import com.mod.movmacro.events.*;
import com.mod.movmacro.macro.MacroManager;
import com.mod.movmacro.macro.hotkey.Hotkey;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class MovementMacrosClient implements ClientModInitializer {
	public static final String MODID = "movmacro";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
	public static final Hotkey DEBUG_KEY = new Hotkey(
			"key.movmacro.debug_key",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_GRAVE_ACCENT,
			"category.movmacro.movement_macros"
	);

	@Override
	public void onInitializeClient() {
		ClientStartEvent.register();
		ClientEndTickEvent.register();
		ClientCommandRegisterEvent.register();
		ClientPlayerLandingEvent.register();
		ClientPlayerJumpEvent.register();
		LOGGER.info("events registered");

		DEBUG_KEY.setCallback(MacroManager::toggleDebugMode);
		KeyBindingHelper.registerKeyBinding(DEBUG_KEY);
		LOGGER.info("debug key registered");
	}
}
