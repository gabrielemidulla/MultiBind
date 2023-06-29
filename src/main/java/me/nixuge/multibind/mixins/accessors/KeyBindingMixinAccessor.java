package me.nixuge.multibind.mixins.accessors;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.settings.KeyBinding;

@Mixin(KeyBinding.class)
public interface KeyBindingMixinAccessor {
    @Accessor
    static List<KeyBinding> getKeybindArray(){return null;}
}
