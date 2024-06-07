package com.mod.movmacro.macro.hotkey;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface HotkeyCallback {
	void onPressed();
}
