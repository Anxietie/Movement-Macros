package com.mod.movmacro.macro.types;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum TickType {
	START,
	TICK,
	END;
}
