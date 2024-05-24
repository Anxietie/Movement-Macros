package com.mod.movmacro.events;

import com.mod.movmacro.macro.MacroManager;
import com.mod.movmacro.macro.Macro;
import com.mod.movmacro.macro.MacroString;
import com.mod.movmacro.macro.types.TickType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.KeyBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// thanks https://github.com/DanilMK/macrofactory

@Environment(EnvType.CLIENT)
public class ClientEndTickEvent {
	private static final List<Macro> macrosInLoop = new ArrayList<>();
	private static final List<Macro> remover = new ArrayList<>();
	private static MacroString running = null;
	private static boolean breaking;

	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register((client) -> {
			if (client.player == null || client.world == null) return;
			if (client.currentScreen instanceof ChatScreen) return;
			if (client.currentScreen != null || breaking) {
				breaking = false;
				breakLoop(client);
			}

			for (Macro macro : remover) {
				macro.run(client, TickType.END);
				macrosInLoop.remove(macro);
			}
			remover.clear();

			for (Macro macro : List.copyOf(macrosInLoop)) // avoid concurrent modification exception
				macro.run(client, TickType.TICK);

			if (running != null) return;

			for (Map.Entry<KeyBinding, MacroString> e : MacroManager.triggers.entrySet()) {
				if (e.getKey().wasPressed()) {
					e.getValue().run(client);
					lockInput(e.getValue());
					break;
				}
			}
		});
	}

	public static void addToLoop(Macro macro) { macrosInLoop.add(macro); }
	public static void removeFromLoop(Macro macro) { if (!remover.contains(macro)) remover.add(macro); }
	public static void breakLoop() { breaking = true; }
	private static void breakLoop(MinecraftClient client) {
		for (Macro macro : macrosInLoop) macro.run(client, TickType.END);
		macrosInLoop.clear();
	}
	public static void lockInput(MacroString string) { running = string; }
	public static void unlockInput() { running = null; }
	public static MacroString getRunningMacro() { return running; }
}
