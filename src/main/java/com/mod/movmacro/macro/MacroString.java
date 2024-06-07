package com.mod.movmacro.macro;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
	private final List<String> runningMacros = new ArrayList<>();
	private final Map<EventType, Queue<EventMacro>> eventMacros = new HashMap<>();
	private final Map<Integer, Macro> stops = new HashMap<>();
	private int running = 0;
	private int runningEvent = 0;
	private String name;
	private boolean enabled = true;
	private int tickDelta = 0;

	public MacroString() {}

	public void run(MinecraftClient client) {
		for (Macro macro : macros) {
			this.incrementRunning(macro);
			macro.run(client, TickType.START);
		}

		incrementTickDelta();
	}

	public void runEventMacro(MinecraftClient client, EventType eventType) {
		if (!this.equals(MacroManager.cache)) {
			this.reloadEventMacros();
			MacroManager.cache = this;
		}

		Queue<EventMacro> q = eventMacros.get(eventType);
		if (q == null)
			return;

		EventMacro macro = q.poll();
		eventMacros.get(eventType).add(macro);
		if (macro == null) return;
		if (!macro.canRun())
			return;

		if (!macro.getMacro().isRunning()) {
			macro.run(client, TickType.START);
			++runningEvent;
			incrementRunning(macro);
		}
	}

	public void incrementRunning(Macro macro) {
		++this.running;
		runningMacros.add(macro.getMacroType().name().toLowerCase());
	}
	public void decrementRunning(Macro macro) {
		--this.running;
		runningMacros.remove(macro.getMacroType().name().toLowerCase());
		if (!this.isRunning() && runningEvent == 0) {
			if (this.equals(MacroManager.getRunningMacro()))
				MacroManager.unlockInput();
			resetTickDelta();
			this.eventMacros.values().forEach(q -> q.forEach(EventMacro::resetRanCount));
		}
	}
	public boolean isRunning() { return this.running > 0; }
	public List<String> getRunningMacros() { return this.runningMacros; }
	public String getName() { return this.name; }
	public void endEventMacro(Macro macro) {
		--runningEvent;
		decrementRunning(macro);
	}
	public void putById(int id, Macro macro) { stops.put(id, macro); }
	public Macro getById(int id) { return stops.get(id); }
	public void reloadEventMacros() {
		for (Queue<EventMacro> q : eventMacros.values()) {
			for (EventMacro macro : q) {
				macro.reloadMacro();
				macro.getMacro().updateStops(this.stops); // have to do this AFTER reloading macro so the macrostring is actually present (not null)
			}
		}
	}
	public void updateStops(Map<Integer, Macro> stops) { this.stops.putAll(stops); }
	public int getTickDelta() { return this.tickDelta; }
	public void incrementTickDelta() { ++this.tickDelta; }
	public void resetTickDelta() { this.tickDelta = 0; }

	public void setJsonValue(JsonElement element) {
		JsonObject json = element.getAsJsonObject();
		name = json.get("name").getAsString();
		enabled = json.get("enabled").getAsBoolean();
		String translationKey = "key.keyboard." + json.get("trigger").getAsString();

		if (enabled) {
			Hotkey trigger = new Hotkey("key." + json.get("trigger").getAsString(), InputUtil.fromTranslationKey(translationKey).getCode(), KeyBinding.MISC_CATEGORY);
			trigger.setCallback(() -> {
				if (!MacroManager.hasRunningMacro() && this.enabled) {
					MacroManager.lockInput(this);
					this.run(MinecraftClient.getInstance());
				}
			});
		}

		JsonArray inputs = json.getAsJsonArray("inputs");

		for (JsonElement e : inputs) {
			Macro macro = createMacroFromJson(e, this);
			if (macro instanceof EventMacro eventMacro) {
				Queue<EventMacro> q = eventMacros.getOrDefault(eventMacro.getEventType(), new LinkedList<>());
				q.add(eventMacro);
				eventMacros.put(eventMacro.getEventType(), q);
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
			case CAMERA -> new CameraMacro();
			case STOP -> new StopMacro();
			case STOP_ALL -> new StopAllMacro();
			case EVENT -> new EventMacro();
		};

		macro.setParent(parent);
		macro.setJsonValue(e);
		return macro;
	}
}
