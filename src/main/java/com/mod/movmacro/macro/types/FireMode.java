package com.mod.movmacro.macro.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public enum FireMode {
	ONCE,
	MANUAL,
	REPEAT;

	public JsonElement getJsonElement() { return new JsonPrimitive(this.name().toLowerCase()); }
}
