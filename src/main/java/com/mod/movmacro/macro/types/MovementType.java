package com.mod.movmacro.macro.types;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

// thanks https://github.com/DanilMK/macrofactory

@Environment(EnvType.CLIENT)
public enum MovementType {
	FORWARD,
	LEFT,
	BACK,
	RIGHT,
	JUMP,
	SNEAK,
	SPRINT;

	private KeyBinding keybind;

	public static void init(MinecraftClient client) {
		FORWARD.keybind = client.options.forwardKey;
		LEFT.keybind = client.options.leftKey;
		BACK.keybind = client.options.backKey;
		RIGHT.keybind = client.options.rightKey;
		JUMP.keybind = client.options.jumpKey;
		SNEAK.keybind = client.options.sneakKey;
		SPRINT.keybind = client.options.sprintKey;
	}

	public void setPressed(boolean pressed) { this.keybind.setPressed(pressed); }
	public boolean isPressed() { return this.keybind.isPressed(); }
}
