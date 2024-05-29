package com.mod.movmacro.macro.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public enum EventType {
	PLAYER_LAND,
	PLAYER_JUMP;

	public JsonElement getJsonElement() { return new JsonPrimitive(this.name().toLowerCase()); }
}
