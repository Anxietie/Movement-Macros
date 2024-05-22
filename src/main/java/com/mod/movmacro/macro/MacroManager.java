package com.mod.movmacro.macro;

import com.google.gson.JsonParser;
import com.mod.movmacro.events.ClientEndTickEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.mod.movmacro.MovementMacrosClient.MODID;

@Environment(EnvType.CLIENT)
public class MacroManager {
	public static final Map<KeyBinding, MacroString> triggers = new HashMap<>();
	private static final String CONFIG_DIR = FabricLoader.getInstance().getConfigDir().toString() + File.separator + MODID;
	private static final String DEFAULT_MACRO = FabricLoader.getInstance().getModContainer(MODID).get().findPath("assets\\movmacro\\hh.json").get().toString();

	public static boolean load() {
		File dir = new File(CONFIG_DIR);
		if (!dir.exists()) {
			if (!dir.mkdir())
				return false;
		}

		File[] files = dir.listFiles();
		if (files == null) return false;

		if (files.length == 0) {
			try {
				File src = new File(DEFAULT_MACRO);
				FileUtils.copyToDirectory(src, dir);
				return reload();
			}
			catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		try {
			for (File f : files) {
				MacroString string = new MacroString();
				FileReader reader = new FileReader(f);
				string.setJsonValue(JsonParser.parseReader(reader).getAsJsonObject());
				reader.close();
				if (string.isEnabled())
					triggers.put(string.getKeybind(), string);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static boolean reload() {
		ClientEndTickEvent.breakLoop(); // break loop first lol
		triggers.clear();
		return load();
	}
}
