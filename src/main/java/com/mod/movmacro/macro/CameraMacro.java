package com.mod.movmacro.macro;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mod.movmacro.events.ClientEndTickEvent;
import com.mod.movmacro.macro.types.MacroType;
import com.mod.movmacro.macro.types.PressType;
import com.mod.movmacro.macro.types.TickType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

import java.util.LinkedList;

import static com.mod.movmacro.MovementMacrosClient.LOGGER;

@Environment(EnvType.CLIENT)
public class CameraMacro extends Macro {
	private float angle; // in degrees
	private LinkedList<Float> angles;
	private final LinkedList<Float> completed;
	private boolean interpolate = false;

	public CameraMacro(float angle, int delay) {
		super(MacroType.CAMERA, PressType.TAP, delay);
		this.angle = angle;
		this.angles = new LinkedList<>();
		this.completed = new LinkedList<>();
	}
	public CameraMacro() { this(0, 0); }

	@Override
	public void run(MinecraftClient client, TickType tickType) {
		switch (tickType) {
			case START -> {
				if (this.getDelay() != 0) {
					ClientEndTickEvent.addToLoop(this);
					break;
				}

				if (client.player != null) {
					if (interpolate && !angles.isEmpty()) {
						ClientEndTickEvent.addToLoop(this);
						float a = angles.remove();
						completed.add(a);
						client.player.setYaw(client.player.getYaw() + a);
						break;
					}

					client.player.setYaw(client.player.getYaw() + angle);
				}

				this.run(client, TickType.END);
			}
			case TICK -> {
				int tickDelta = this.getParent().getTickDelta();
				if (tickDelta < this.getDelay())
					break;

				if (client.player != null) {
					if (interpolate && !angles.isEmpty()) {
						float a = angles.remove();
						completed.add(a);
						client.player.setYaw(client.player.getYaw() + a);
						break;
					}

					client.player.setYaw(client.player.getYaw() + angle);
				}

				ClientEndTickEvent.removeFromLoop(this);
			}
			case END -> {
				this.getParent().decrementRunning(this);
				angles.addAll(completed);
				completed.clear();
			}
		}
	}

	@Override
	public void setJsonValue(JsonElement element) {
		JsonObject json = element.getAsJsonObject();
		this.setDelay(json.get("delay").getAsInt());
		if (json.get("angle") != null) {
			angle = json.get("angle").getAsFloat();
			interpolate = false;
		}
		else {
			String angleFile = json.get("angles").getAsString();
			angles = MacroManager.getAngleFile(angleFile);
			if (angles == null) {
				LOGGER.error("Invalid angle file \"{}\" at \"{}\"", angleFile, this.getParent().getName());
				angles = new LinkedList<>();
			}
			interpolate = true;
		}
	}
}
