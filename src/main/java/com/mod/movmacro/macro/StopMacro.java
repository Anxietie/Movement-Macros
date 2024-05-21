package com.mod.movmacro.macro;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mod.movmacro.events.ClientEndTickEvent;
import com.mod.movmacro.macro.types.MacroType;
import com.mod.movmacro.macro.types.PressType;
import com.mod.movmacro.macro.types.TickType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class StopMacro extends Macro {
	private int id; // id of macro to stop

	public StopMacro(int delay, int id) {
		super(MacroType.STOP, PressType.TAP, delay);
		this.id = id;
	}
	public StopMacro() { this(0, 0); }

	@Override
	public void run(MinecraftClient client, TickType tickType) {
		switch (tickType) {
			case START -> {
				if (this.getDelay() != 0) {
					ClientEndTickEvent.addToLoop(this);
					break;
				}

				ClientEndTickEvent.removeFromLoop(this.getParent().getById(this.id));
			}
			case TICK -> {
				if (this.getTickDelta() < this.getDelay())
					break;

				ClientEndTickEvent.removeFromLoop(this.getParent().getById(this.id));
				ClientEndTickEvent.removeFromLoop(this);
			}
			case END -> {
				this.resetTickDelta();
				return;
			}
		}

		this.incrementTickDelta();
	}

	@Override
	public JsonElement getJsonValue() {
		JsonObject json = new JsonObject();
		json.add("macro_type", this.getMacroType().getJsonElement());
		json.add("delay", new JsonPrimitive(this.getDelay()));
		json.add("id", new JsonPrimitive(this.id));
		return json;
	}

	@Override
	public void setJsonValue(JsonElement element) {
		JsonObject json = element.getAsJsonObject();
		this.setDelay(json.get("delay").getAsInt());
		this.id = json.get("id").getAsInt();
	}
}
