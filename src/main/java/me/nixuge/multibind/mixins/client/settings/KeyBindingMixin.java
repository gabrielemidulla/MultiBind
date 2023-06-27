package me.nixuge.multibind.mixins.client.settings;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import lombok.Getter;
import me.nixuge.multibind.binds.AlternativeKeyBinding;
import net.minecraft.client.settings.KeyBinding;

@Mixin(KeyBinding.class)
public class KeyBindingMixin {
    @Shadow
    private boolean pressed;

    @Getter
    private List<AlternativeKeyBinding> alternativeKeybinds;

    private void addAlternativeBind(int keyCode) {
        this.alternativeKeybinds.add(
            new AlternativeKeyBinding((KeyBinding)(Object)this, keyCode)
        );
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void KeyBinding(String description, int keyCode, String category, CallbackInfo ci) {
        this.alternativeKeybinds = new ArrayList<>();
    }

    @Inject(method = "setKeyBindState", at = @At("RETURN"))
    private static void setKeyBindState(int keyCode, boolean pressed, CallbackInfo ci) {
        AlternativeKeyBinding.setKeyBindState(keyCode, pressed);
    }

    @Inject(method = "onTick", at = @At("RETURN"))
    private static void onTick(int keyCode, CallbackInfo ci) {
        AlternativeKeyBinding.onTick(keyCode);
    }

    @Inject(method = "unPressAllKeys", at = @At("RETURN"))
    private static void unPressAllKeys(CallbackInfo ci) {
        AlternativeKeyBinding.unPressAllKeys();
    }

    @Inject(method = "resetKeyBindingArrayAndHash", at = @At("RETURN"))
    private static void resetKeyBindingArrayAndHash(CallbackInfo ci) {
        AlternativeKeyBinding.resetKeyBindingArrayAndHash();
    }

    @Inject(method = "isKeyDown", at = @At("HEAD"), cancellable = true)
    private void isKeyDown(CallbackInfoReturnable<Boolean> cir) {
        boolean otherKeybindPressed = alternativeKeybinds.stream().anyMatch(keybind -> keybind.isKeyDown());
        cir.setReturnValue(this.pressed || otherKeybindPressed);
    }


    @Inject(method = "getKeyCode", at = @At("RETURN"), cancellable = true)
    private void getKeyCode(CallbackInfoReturnable<Integer> cir) {
        // After edit: this seems to only be used in the controls gui
        // to actually show the key, so should be fine without.
        // System.out.println("getKeyCode called. This is a problem since we possible have more than 1 keycodes");
        // System.out.println("Todo: check how this is handled & if it's problematic");
        // cir.setReturnValue(null);
    }

    @Inject(method = "setKeyCode", at = @At("RETURN"), cancellable = true)
    private void setKeyCode(int keyCode, CallbackInfo ci) {
        // System.out.println("setKeyCode called! new: " + keyCode);
        // addAlternativeBind(44);
        // addAlternativeBind(17);
        // addAlternativeBind(35); //h
    }
}
