package com.mod.movmacro.macro;

import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.mod.movmacro.events.ClientEndTickEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.mod.movmacro.MovementMacrosClient.MODID;
import static com.mod.movmacro.MovementMacrosClient.LOGGER;

@Environment(EnvType.CLIENT)
public class MacroManager {
	public static final Map<String, MacroString> names = new HashMap<>();
	public static MacroString cache = null;
	private static final String CONFIG_DIR = FabricLoader.getInstance().getConfigDir().toString() + File.separator + MODID;

	public static boolean load() {
		File dir = new File(CONFIG_DIR);
		if (!dir.exists() && !dir.mkdir())
			return false;

		File[] files = dir.listFiles();
		if (files == null) return false;

		try {
			for (File f : files) {
				if (!FilenameUtils.getExtension(f.getName()).equalsIgnoreCase("json"))
					continue;

				MacroString string = new MacroString();
				JsonReader reader = new JsonReader(new FileReader(f));
				reader.setLenient(true);
				string.setJsonValue(JsonParser.parseReader(reader).getAsJsonObject());
				names.put(string.getName(), string);

				reader.close();
			}
		}
		catch (IOException e) {
			LOGGER.error("IOException thrown while loading macros in movmacro!", e);
			return false;
		}

		return true;
	}

	public static boolean reload() {
		ClientEndTickEvent.breakLoop(); // break loop first lol
		return load();
	}

	public static MacroString getMacro(String name) { return names.get(name); }
}
