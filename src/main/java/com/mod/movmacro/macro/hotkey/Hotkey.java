package com.mod.movmacro.macro.hotkey;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

@Environment(EnvType.CLIENT)
public class Hotkey extends KeyBinding {
	private HotkeyCallback callback;

	public Hotkey(String translationKey, int code, String category) { super(translationKey, code, category); }
	public Hotkey(String translationKey, InputUtil.Type type, int code, String category) { super(translationKey, type, code, category); }

	@Override
	public void setPressed(boolean pressed) {
		super.setPressed(pressed);
		if (pressed)
			callback.onPressed();
	}

	public void setCallback(HotkeyCallback callback) { this.callback = callback; }
}
