package com.mod.movmacro.macro;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mod.movmacro.events.ClientEndTickEvent;
import com.mod.movmacro.macro.types.MacroType;
import com.mod.movmacro.macro.types.MovementType;
import com.mod.movmacro.macro.types.PressType;
import com.mod.movmacro.macro.types.TickType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class MovementMacro extends Macro {
	private MovementType movementType;
	private int duration; // in ticks

	public MovementMacro(MovementType movementType, PressType pressType, int delay, int duration) {
		super(MacroType.MOVEMENT, pressType, delay);
		this.movementType = movementType;
		this.duration = duration;
	}
	public MovementMacro() { this(MovementType.FORWARD, PressType.TAP, 0, 0); }

	@Override
	public void run(@NotNull MinecraftClient client, TickType tickType) {
		switch (tickType) {
			case START -> {
				ClientEndTickEvent.addToLoop(this);

				if (this.getDelay() == 0)
					movementType.setPressed(true);
			}
			case TICK -> {
				if (this.getParent().getTickDelta() < this.getDelay())
					break;

				boolean input = true;

				switch (this.getPressType()) {
					case HOLD_DURATION -> {
						if (this.getParent().getTickDelta() - this.getDelay() >= duration) {
							ClientEndTickEvent.removeFromLoop(this);
							input = false;
						}
					}
					case TAP -> ClientEndTickEvent.removeFromLoop(this);
				}

				if (!movementType.isPressed()) movementType.setPressed(input);
			}
			case END -> {
				movementType.setPressed(false);
				this.getParent().decrementRunning(this);
			}
		}
	}

	// for tick precision timing
	public void stop(MinecraftClient client) {
		movementType.setPressed(false);
		super.stop(client);
	}

	@Override
	public void setJsonValue(JsonElement element) {
		JsonObject json = element.getAsJsonObject();
		JsonElement e = json.get("id");
		if (e != null)
			this.getParent().putById(e.getAsInt(), this);
		movementType = MovementType.valueOf(json.get("movement_type").getAsString().toUpperCase());
		this.setPressType(PressType.valueOf(json.get("press_type").getAsString().toUpperCase()));
		this.setDelay(json.get("delay").getAsInt());
		duration = json.get("duration").getAsInt();
	}
}
