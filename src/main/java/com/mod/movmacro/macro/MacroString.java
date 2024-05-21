package com.mod.movmacro.macro;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
	private final LinkedList<Macro> macros = new LinkedList<>();
	private final Map<Integer, Macro> stops = new HashMap<>();
	private String name;
	private boolean enabled = true;
	private KeyBinding trigger;

	public MacroString(String name, Macro... macros) {
		this.name = name;
		this.macros.addAll(Arrays.asList(macros));
	}
	public MacroString() {}

	public void run(MinecraftClient client) {
		for (Macro macro : macros)
			macro.run(client, TickType.START);
	}

	public boolean isEnabled() { return this.enabled; }
	/*
	public void enable() { this.enabled = true; }
	public void disable() { this.enabled = false; }
	public LinkedList<Macro> getMacros() { return this.macros; }
	public void setName(String name) { this.name = name; }
	public String getName() { return this.name; }
	 */
	public KeyBinding getKeybind() { return this.trigger; }
	// public void setKeybind(KeyBinding keybind) { this.trigger = keybind; }
	public void putById(int id, Macro macro) { stops.put(id, macro); }
	public Macro getById(int id) { return stops.get(id); }

	/*
	public JsonObject getJsonValue() {
		JsonArray array = new JsonArray();
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
			Macro macro;
			MacroType type = MacroType.valueOf(e.getAsJsonObject().get("macro_type").getAsString().toUpperCase());

			macro = switch (type) {
				case MOVEMENT -> new MovementMacro();
				case CAMERA -> new CameraTurnMacro();
				case STOP -> new StopMacro();
				case STOP_ALL -> new StopAllMacro();
			};

			macro.setParent(this);
			macro.setJsonValue(e);
			macros.add(macro);
		}
	}
}
