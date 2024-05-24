package com.mod.movmacro.macro;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mod.movmacro.events.ClientEndTickEvent;
import com.mod.movmacro.macro.types.*;
import net.minecraft.client.MinecraftClient;

public class EventMacro extends Macro {
	private EventType eventType;
	private MacroString macro = new MacroString();
	private String macroName = "";
	private FireMode mode;
	private int fireCount = 1;
	private int ranCount = 0;
	private boolean ran = false;

	public EventMacro(EventType eventType, FireMode fireMode) {
		super(MacroType.EVENT, PressType.TAP, 0);
		this.eventType = eventType;
		this.mode = fireMode;
	}
	public EventMacro() { this(EventType.PLAYER_LAND, FireMode.ONCE); }

	@Override
	public void run(MinecraftClient client, TickType tickType) {
		switch (tickType) {
			case START -> {
				if ((mode == FireMode.ONCE || mode == FireMode.MANUAL) && ranCount >= fireCount) {
					this.getParent().endEventMacro();
					return;
				}


				ClientEndTickEvent.addToLoop(this);
			}
			case TICK -> {
				if (!ran) {
					macro.run(client);
					ran = true;
					++ranCount;
					return;
				}

				if (macro.isRunning())
					return;

				ClientEndTickEvent.removeFromLoop(this);

				if (this.getParent().isRunning())
					ClientEndTickEvent.lockInput(this.getParent());
			}
			case END -> {
				this.getParent().endEventMacro();

				ran = false;
			}
		}
	}

	public EventType getEventType() { return this.eventType; }
	public MacroString getMacro() { return this.macro; }
	public void resetRanCount() { this.ranCount = 0; }
	public void reloadMacro() { this.macro = MacroManager.getMacro(macroName); }

	@Override
	public JsonElement getJsonValue() {
		JsonObject json = new JsonObject();
		json.add("macro_type", this.getMacroType().getJsonElement());
		json.add("event_type", eventType.getJsonElement());
		json.add("macro", new JsonPrimitive(macro.getName()));
		return null;
	}

	@Override
	public void setJsonValue(JsonElement element) {
		JsonObject json = element.getAsJsonObject();
		eventType = EventType.valueOf(json.get("event_type").getAsString().toUpperCase());
		macroName = json.get("macro").getAsString();
		macro = MacroManager.getMacro(macroName);
		mode = FireMode.valueOf(json.get("fire_mode").getAsString().toUpperCase());
		if (mode == FireMode.MANUAL)
			fireCount = json.get("count").getAsInt();
	}
}
