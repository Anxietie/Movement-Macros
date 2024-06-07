package com.mod.movmacro.events;

import com.mod.movmacro.macro.Macro;
import com.mod.movmacro.macro.MacroManager;
import com.mod.movmacro.macro.types.TickType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// thanks https://github.com/DanilMK/macrofactory

@Environment(EnvType.CLIENT)
public class ClientEndTickEvent {
	private static final List<Macro> macrosInLoop = new ArrayList<>();
	private static final Set<Macro> remover = new HashSet<>();
	private static boolean breaking;

	private static boolean shouldCount = false;
	private static long countingTicks = 0;

	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register((client) -> {
			if (client.player == null || client.world == null) return;
			if (client.currentScreen instanceof ChatScreen) return;
			if (client.currentScreen != null || breaking) {
				breaking = false;
				breakLoop(client);
			}

			for (Macro macro : List.copyOf(macrosInLoop)) { // avoid concurrent modification exception
				if (remover.contains(macro)) {
					macro.run(client, TickType.END);
					macrosInLoop.remove(macro);
					remover.remove(macro);
					continue;
				}

				macro.run(client, TickType.TICK);
			}

			if (shouldCount)
				++countingTicks;

			if (MacroManager.hasRunningMacro())
				MacroManager.incrementTickDelta();
		});
	}

	public static void addToLoop(Macro macro) { macrosInLoop.add(macro); }
	public static void removeFromLoop(Macro macro) { remover.add(macro); }
	public static void breakLoop() { breaking = true; }
	private static void breakLoop(MinecraftClient client) {
		for (Macro macro : macrosInLoop) macro.run(client, TickType.END);
		macrosInLoop.clear();
	}

	public static void startCounting() { shouldCount = true; }
	public static void stopCounting() { shouldCount = false; }
	public static long getCountedTicks() { return countingTicks; }
	public static void resetCountingTicks() {
		stopCounting();
		countingTicks = 0;
	}
}
