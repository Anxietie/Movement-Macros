package com.mod.movmacro.macro;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mod.movmacro.events.ClientEndTickEvent;
import com.mod.movmacro.macro.types.EventType;
import com.mod.movmacro.macro.types.MacroType;
import com.mod.movmacro.macro.types.TickType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.*;

// thanks https://github.com/DanilMK/macrofactory

@Environment(EnvType.CLIENT)
public class MacroString {
	// private final MacroString parent = new MacroString();
	private final LinkedList<Macro> macros = new LinkedList<>();
	private final Map<EventType, EventMacro> eventMacros = new HashMap<>();
	private final Map<Integer, Macro> stops = new HashMap<>();
	private int running = 0;
	private int runningEvent = 0;
	private String name;
	private boolean enabled = true;
	private KeyBinding trigger;

	public MacroString(String name, Macro... macros) {
		this.name = name;
		for (Macro macro : macros) {
			macro.setParent(this);
			this.macros.add(macro);
		}
	}
	public MacroString() {}

	public void run(MinecraftClient client) {
		for (Macro macro : macros) {
			this.incrementRunning();
			macro.run(client, TickType.START);
		}
	}

	public void runEventMacro(MinecraftClient client, EventType eventType) {
		EventMacro macro = eventMacros.get(eventType);
		if (macro != null && macro.getMacro().canRun()) {
			macro.run(client, TickType.START);
			++runningEvent;
			incrementRunning();
		}
	}

	public boolean isEnabled() { return this.enabled; }
	public void incrementRunning() { ++this.running; }
	public void decrementRunning() {
		--this.running;
		if (canRun() && runningEvent == 0) {
			ClientEndTickEvent.unlockInput();
			eventMacros.values().forEach(EventMacro::resetRanCount);
		}
	}
	public boolean canRun() { return this.running == 0; }
	public boolean isRunning() { return this.running > 0; }
	public boolean hasEventMacros() { return !this.eventMacros.values().isEmpty(); }
	public String getName() { return this.name; }
	public LinkedList<Macro> getMacros() { return this.macros; }
	public KeyBinding getKeybind() { return this.trigger; }
	public void endEventMacro() {
		--runningEvent;
		decrementRunning();
	}
	public void putById(int id, Macro macro) { stops.put(id, macro); }
	public Macro getById(int id) { return stops.get(id); }
	public void reloadEventMacros() {
		for (EventMacro macro : eventMacros.values())
			macro.reloadMacro();
	}

	/*
	public JsonObject getJsonValue() {
		JsonArray array = new JsonArray();
		for (Macro macro : eventMacros.values()) array.add(macro.getJsonValue());
		for (Macro macro : macros) array.add(macro.getJsonValue());

		JsonObject json = new JsonObject();
		json.add("inputs", array);
		json.add("name", new JsonPrimitive(this.name));
		json.add("enabled", new JsonPrimitive(this.enabled));
		String key = InputUtil.fromTranslationKey(trigger.getTranslationKey()).toString().substring("key.keyboard.".length());
		json.add("trigger", new JsonPrimitive(key));
		return json;
	}
	 */

	public void setJsonValue(JsonElement element) {
		JsonObject json = element.getAsJsonObject();
		name = json.get("name").getAsString();
		enabled = json.get("enabled").getAsBoolean();
		String translationKey = "key.keyboard." + json.get("trigger").getAsString();
		trigger = new KeyBinding("key." + json.get("trigger").getAsString(), InputUtil.fromTranslationKey(translationKey).getCode(), KeyBinding.MISC_CATEGORY);

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
