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
import lombok.Setter;
import me.nixuge.multibind.accessors.KeyBindAccessor;
import me.nixuge.multibind.binds.AlternativeKeyBinding;
import net.minecraft.client.settings.KeyBinding;

@Mixin(KeyBinding.class)
public class KeyBindingMixin implements KeyBindAccessor {
    @Shadow
    private boolean pressed;
    @Shadow
    private int pressTime;

    @Getter
    private List<AlternativeKeyBinding> alternativeKeybinds;

    @Setter
    private int selectedBindIndex = -1;

    public void addAlternativeBind(int keyCode) {
        this.alternativeKeybinds.add(
                new AlternativeKeyBinding((KeyBinding) (Object) this, keyCode));
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

    public boolean isPressedReplica() {
        if (this.pressTime == 0) {
            return false;
        } else {
            --this.pressTime;
            return true;
        }
    }

    @Inject(method = "isPressed", at = @At("HEAD"), cancellable = true)
    private void isPressed(CallbackInfoReturnable<Boolean> cir) {
        boolean otherKeybindPressed = alternativeKeybinds.stream().anyMatch(keybind -> keybind.isPressed());
        cir.setReturnValue(this.isPressedReplica() || otherKeybindPressed);
    }

    // @Inject(method = "getKeyCode", at = @At("RETURN"), cancellable = true)
    // private void getKeyCode(CallbackInfoReturnable<Integer> cir) {
    // This is unfortunately used in Minecraft.class's dispatchKeyPresses,
    // So I have to mixin this function :/
    // }

    @Inject(method = "setKeyCode", at = @At("HEAD"), cancellable = true)
    private void setKeyCode(int keyCode, CallbackInfo ci) {
        if (this.selectedBindIndex >= 0) {
            this.alternativeKeybinds.get(this.selectedBindIndex).setKeyCode(keyCode);
            ci.cancel();
        }
        // negative = normal bind, just process that
    }
}
