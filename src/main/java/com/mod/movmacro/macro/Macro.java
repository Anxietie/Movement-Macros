package com.mod.movmacro.macro;

import com.google.gson.JsonElement;
import com.mod.movmacro.events.ClientEndTickEvent;
import com.mod.movmacro.macro.types.MacroType;
import com.mod.movmacro.macro.types.PressType;
import com.mod.movmacro.macro.types.TickType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public abstract class Macro {
	private MacroString parent;
	private final MacroType macroType;
	private PressType pressType;
	private int delay;
	private int tickDelta = 0;

	public Macro(MacroType macroType, PressType pressType, int delay) {
		this.macroType = macroType;
		this.pressType = pressType;
		this.delay = delay;
	}

	public void setParent(MacroString string) { this.parent = string; }
	public MacroString getParent() { return this.parent; }

	public abstract void run(MinecraftClient client, TickType tickType);
	public abstract JsonElement getJsonValue();
	public abstract void setJsonValue(JsonElement element);

	public void setPressType(PressType pressType) { this.pressType = pressType; }

	MacroType getMacroType() { return this.macroType; }
	PressType getPressType() { return this.pressType; }

	public int getTickDelta() { return this.tickDelta; }
	public void incrementTickDelta() { ++this.tickDelta; }
	public void resetTickDelta() { this.tickDelta = 0; }
	public int getDelay() { return this.delay; }
	public void setDelay(int delay) { this.delay = delay; }

	public void stop(MinecraftClient client) {
		ClientEndTickEvent.removeFromLoop(this);
	}
}
