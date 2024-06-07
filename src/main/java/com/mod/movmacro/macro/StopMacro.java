package com.mod.movmacro.macro;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

				this.getParent().getById(this.id).stop(client);
				this.run(client, TickType.END);
			}
			case TICK -> {
				if (this.getParent().getTickDelta() < this.getDelay())
					break;

				this.getParent().getById(this.id).stop(client);
				ClientEndTickEvent.removeFromLoop(this);
			}
			case END -> this.getParent().decrementRunning(this);
		}
	}

	@Override
	public void setJsonValue(JsonElement element) {
		JsonObject json = element.getAsJsonObject();
		this.setDelay(json.get("delay").getAsInt());
		this.id = json.get("id").getAsInt();
	}
}
