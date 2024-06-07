package com.mod.movmacro.macro;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mod.movmacro.events.ClientEndTickEvent;
import com.mod.movmacro.macro.types.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class EventMacro extends Macro {
	private EventType eventType;
	private MacroString macro = new MacroString();
	private String macroName = "";
	private FireMode mode;
	private int fireCount = 1;
	private int ranCount = 0;

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
				ClientEndTickEvent.addToLoop(this);

				macro.run(client);
				++ranCount;
			}
			case TICK -> {
				if (macro.isRunning()) {
					macro.incrementTickDelta();
					return;
				}

				ClientEndTickEvent.removeFromLoop(this);
			}
			case END -> {
				this.getParent().endEventMacro(this);
				macro.resetTickDelta();
			}
		}
	}

	public EventType getEventType() { return this.eventType; }
	public MacroString getMacro() { return this.macro; }
	public void resetRanCount() { this.ranCount = 0; }
	public void reloadMacro() { this.macro = MacroManager.getMacro(macroName); }
	public boolean canRun() { return this.mode == FireMode.REPEAT || this.ranCount < this.fireCount; }

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
