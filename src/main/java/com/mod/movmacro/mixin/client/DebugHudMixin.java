package com.mod.movmacro.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mod.movmacro.macro.MacroManager;
import com.mod.movmacro.macro.MacroString;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(DebugHud.class)
public abstract class DebugHudMixin {
	@ModifyReturnValue(method = "getLeftText", at = @At("RETURN"))
	public List<String> movmacro$addMacroDebugInfo(List<String> original) {
		if (!MacroManager.inDebugMode())
			return original;

		MacroString string = MacroManager.getRunningMacro();
		original.add("Running Macro String: " + (string == null ? "none" : string.getName()));
		original.add("Running Inputs: " + (string == null ? "[]" : string.getRunningMacros().toString()));
		original.add("Last Jump Time: " + MacroManager.getLastJumpTime());

		return original;
	}
}
