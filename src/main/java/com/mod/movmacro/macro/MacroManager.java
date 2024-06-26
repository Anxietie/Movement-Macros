package com.mod.movmacro.macro;

import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.mod.movmacro.events.ClientEndTickEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static com.mod.movmacro.MovementMacrosClient.MODID;
import static com.mod.movmacro.MovementMacrosClient.LOGGER;

@Environment(EnvType.CLIENT)
public class MacroManager {
	public static final Map<String, MacroString> MACRO_NAMES = new HashMap<>();
	public static final Map<String, LinkedList<Float>> ANGLE_FILES = new HashMap<>();
	public static final String CONFIG_DIR = FabricLoader.getInstance().getConfigDir().toString() + File.separator + MODID;
	public static MacroString cache = null;

	private static MacroString running = null;
	private static boolean debug = false;
	private static long jumpTime = 0; // in ticks

	public static boolean load() {
		File dir = new File(CONFIG_DIR);
		if (!dir.exists() && !dir.mkdir())
			return false;

		File[] files = dir.listFiles();
		if (files == null) return false;

		try {
			for (File f : files) {
				String fextension = FilenameUtils.getExtension(f.getName());
				if (fextension.equalsIgnoreCase("macro")) {
					Map.Entry<String, LinkedList<Float>> e = processAngleFile(f);
					if (e == null)
						continue;

					ANGLE_FILES.put(e.getKey(), e.getValue());
					continue;
				}

				if (!fextension.equalsIgnoreCase("json"))
					continue;

				Map.Entry<String, MacroString> e = processMacroString(f);
				MACRO_NAMES.put(e.getKey(), e.getValue());
			}
		}
		catch (IOException e) {
			LOGGER.error("IOException thrown while loading macros in movmacro!", e);
			return false;
		}

		return true;
	}

	private static Map.Entry<String, MacroString> processMacroString(File f) throws IOException {
		MacroString string = new MacroString();
		JsonReader jreader = new JsonReader(new FileReader(f));
		jreader.setLenient(true);
		string.setJsonValue(JsonParser.parseReader(jreader).getAsJsonObject());
		jreader.close();
		return Map.entry(string.getName(), string);
	}

	private static Map.Entry<String, LinkedList<Float>> processAngleFile(File f) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line = br.readLine();

		if (line == null || !line.matches("MOVMACRO:(.*)")) // matches text of the form "MOVMACRO:<angle file name>" (with spaces)
			return null;

		String name = line.substring(line.indexOf(':') + 1).trim();
		line = br.readLine();

		LinkedList<Float> angles = new LinkedList<>();
		while (line != null) {
			line = line.trim();

			if (line.isBlank() || line.charAt(0) == '#') {
				line = br.readLine();
				continue;
			}

			angles.add(Float.parseFloat(line));
			line = br.readLine();
		}

		br.close();
		return Map.entry(name, angles);
	}

	public static boolean reload() {
		ClientEndTickEvent.breakLoop(); // break loop first lol
		MACRO_NAMES.clear();
		ANGLE_FILES.clear();
		return load();
	}

	public static MacroString getMacro(String name) { return MACRO_NAMES.get(name); }
	public static LinkedList<Float> getAngleFile(String name) { return ANGLE_FILES.get(name); }

	public static void lockInput(MacroString string) { running = string; }
	public static void unlockInput() { running = null; }
	public static boolean hasRunningMacro() { return running != null; }
	public static MacroString getRunningMacro() { return running; }
	public static void incrementTickDelta() { running.incrementTickDelta(); }

	public static void toggleDebugMode() {
		debug = !debug;
		jumpTime = 0;
	}
	public static boolean inDebugMode() { return debug; }
	public static void updateJumpTime() { jumpTime = ClientEndTickEvent.getCountedTicks(); }
	public static long getLastJumpTime() { return jumpTime; }
}
