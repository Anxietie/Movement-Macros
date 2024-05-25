package com.mod.movmacro.macro;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mod.movmacro.events.ClientEndTickEvent;
import com.mod.movmacro.macro.types.EventType;
import com.mod.movmacro.macro.types.MacroType;
import com.mod.movmacro.macro.types.TickType;
import com.mod.movmacro.macro.hotkey.Hotkey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.*;

// thanks https://github.com/DanilMK/macrofactory

@Environment(EnvType.CLIENT)
public class MacroString {
	private final List<Macro> macros = new ArrayList<>();
	private final Map<EventType, EventMacro> eventMacros = new HashMap<>();
	private final Map<Integer, Macro> stops = new HashMap<>();
	private int running = 0;
	private int runningEvent = 0;
	private String name;
	private boolean enabled = true;
	private int tickDelta = 0;

	public MacroString() {}

	public void run(MinecraftClient client) {
		for (Macro macro : macros) {
			this.incrementRunning();
			macro.run(client, TickType.START);
		}

		incrementTickDelta();
	}

	public void runEventMacro(MinecraftClient client, EventType eventType) {
		EventMacro macro = eventMacros.get(eventType);
		if (macro == null) return;
		if (!macro.canRun())
			return;

		if (!macro.getMacro().isRunning()) {
			macro.run(client, TickType.START);
			++runningEvent;
			incrementRunning();
		}
	}

	public void incrementRunning() { ++this.running; }
	public void decrementRunning() {
		--this.running;
		if (!this.isRunning() && runningEvent == 0) {
			if (ClientEndTickEvent.getRunningMacro().equals(this))
				ClientEndTickEvent.unlockInput();
			resetTickDelta();
			this.eventMacros.values().forEach(EventMacro::resetRanCount);
		}
	}
	public boolean isRunning() { return this.running > 0; }
	public String getName() { return this.name; }
	public void endEventMacro() {
		--runningEvent;
		decrementRunning();
	}
	public void putById(int id, Macro macro) { stops.put(id, macro); }
	public Macro getById(int id) { return stops.get(id); }
	public void reloadEventMacros() {
		for (EventMacro macro : eventMacros.values()) {
			macro.reloadMacro();
			macro.getMacro().updateStops(this.stops); // have to do this AFTER reloading macro so the macrostring is actually present (not null)
		}
	}
	public void updateStops(Map<Integer, Macro> stops) {
		for (Map.Entry<Integer, Macro> e : stops.entrySet())
			this.stops.putIfAbsent(e.getKey(), e.getValue());
	}
	public int getTickDelta() { return this.tickDelta; }
	public void incrementTickDelta() { ++this.tickDelta; }
	public void resetTickDelta() { this.tickDelta = 0; }

	public void setJsonValue(JsonElement element) {
		JsonObject json = element.getAsJsonObject();
		name = json.get("name").getAsString();
		enabled = json.get("enabled").getAsBoolean();
		String translationKey = "key.keyboard." + json.get("trigger").getAsString();

		Hotkey trigger = new Hotkey("key." + json.get("trigger").getAsString(), InputUtil.fromTranslationKey(translationKey).getCode(), KeyBinding.MISC_CATEGORY);
		trigger.setCallback(() -> {
			if (!ClientEndTickEvent.isRunning() && enabled) {
				this.run(MinecraftClient.getInstance());
				ClientEndTickEvent.lockInput(this);
			}
		});

		JsonArray inputs = json.getAsJsonArray("inputs");

		for (JsonElement e : inputs) {
			Macro macro = createMacroFromJson(e, this);
			if (macro instanceof EventMacro eventMacro) {
				eventMacros.put(eventMacro.getEventType(), eventMacro);
				continue;
			}
			this.macros.add(macro);
		}
	}

	private Macro createMacroFromJson(JsonElement e, MacroString parent) {
		Macro macro;
		MacroType type = MacroType.valueOf(e.getAsJsonObject().get("macro_type").getAsString().toUpperCase());

		macro = switch (type) {
			case MOVEMENT -> new MovementMacro();
			case CAMERA -> new CameraTurnMacro();
			case STOP -> new StopMacro();
			case STOP_ALL -> new StopAllMacro();
			case EVENT -> new EventMacro();
		};

		macro.setParent(parent);
		macro.setJsonValue(e);
		return macro;
	}
}
