package com.mod.movmacro.macro.hotkey;

import net.minecraft.client.option.KeyBinding;

public class Hotkey extends KeyBinding {
	private HotkeyCallback callback;

	public Hotkey(String translationKey, int code, String category) { super(translationKey, code, category); }

	@Override
	public void setPressed(boolean pressed) {
		super.setPressed(pressed);
		if (pressed)
			callback.onPressed();
	}

	public void setCallback(HotkeyCallback callback) { this.callback = callback; }
}
