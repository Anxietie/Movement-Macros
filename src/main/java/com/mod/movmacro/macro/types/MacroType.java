package com.mod.movmacro.macro.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum MacroType {
	MOVEMENT,
	CAMERA,
	STOP,
	STOP_ALL;

	public JsonElement getJsonElement() { return new JsonPrimitive(this.name().toLowerCase()); }
}
