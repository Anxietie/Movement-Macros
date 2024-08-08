package com.mod.movmacro.mixin.client;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(KeyBinding.class)
public interface KeyBindingMixin {
	@Accessor("KEY_TO_BINDINGS")
	static Map<InputUtil.Key, KeyBinding> movmacro$getKeyToBindings() { throw new AssertionError(); }

	@Accessor("KEYS_BY_ID")
	static Map<String, KeyBinding> movmacro$getKeysById() { throw new AssertionError(); }
}
