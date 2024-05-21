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
public class CameraTurnMacro extends Macro {
	private double angle; // in degrees

	public CameraTurnMacro(double angle, int delay) {
		super(MacroType.CAMERA, PressType.TAP, delay);
		this.angle = angle;
	}
	public CameraTurnMacro() { this(0, 0); }

	@Override
	public void run(MinecraftClient client, TickType tickType) {
		switch (tickType) {
			case START -> {
				if (this.getDelay() != 0) {
					ClientEndTickEvent.addToLoop(this);
					break;
				}

				if (client.player != null)
					client.player.setYaw((float) (client.player.getYaw() + angle));
			}
			case TICK -> {
				if (this.getTickDelta() < this.getDelay())
					break;

				if (client.player != null)
					client.player.setYaw((float) (client.player.getYaw() + angle));

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
		json.add("angle", new JsonPrimitive(angle));
		json.add("delay", new JsonPrimitive(this.getDelay()));
		return json;
	}

	@Override
	public void setJsonValue(JsonElement element) {
		JsonObject json = element.getAsJsonObject();
		angle = json.get("angle").getAsDouble();
		this.setDelay(json.get("delay").getAsInt());
	}
}
