package com.mod.movmacro.macro;

import com.google.gson.JsonParser;
import com.mod.movmacro.events.ClientEndTickEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import static com.mod.movmacro.MovementMacrosClient.MODID;

@Environment(EnvType.CLIENT)
public class MacroManager {
	public static final Map<KeyBinding, MacroString> triggers = new HashMap<>();
	private static final String CONFIG_DIR = FabricLoader.getInstance().getConfigDir().toString() + File.separator + MODID;

	public static boolean load() {
		File dir = new File(CONFIG_DIR);
		if (!dir.exists()) {
			if (!dir.mkdir())
				return false;
		}

		File[] files = dir.listFiles();
		if (files == null) return false;

		try {
			for (File f : files) {
				MacroString string = new MacroString();
				string.setJsonValue(JsonParser.parseReader(new FileReader(f)).getAsJsonObject());
				triggers.put(string.getKeybind(), string);
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static boolean reload() {
		triggers.clear();
		ClientEndTickEvent.breakLoop();
		return load();
	}
}
