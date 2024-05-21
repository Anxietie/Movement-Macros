package com.mod.movmacro.events;

import com.mod.movmacro.macro.MacroManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ClientCommandRegisterEvent {
	public static void registerClientCommands() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registry) -> {
			dispatcher.register(
					ClientCommandManager.literal("macros")
							.then(ClientCommandManager.literal("reload")
									.executes(context -> {
										boolean b = MacroManager.reload();
										context.getSource().sendFeedback(Text.literal(b ? "macros reloaded" : "error reloading macros"));
										return 1;
									})
							)
			);
		});
	}
}
