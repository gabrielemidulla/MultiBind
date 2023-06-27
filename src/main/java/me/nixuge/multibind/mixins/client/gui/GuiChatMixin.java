package me.nixuge.multibind.mixins.client.gui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiChat;

@Mixin(GuiChat.class)
public class GuiChatMixin {
    // Debugging mixin - shouldn't be left in final build
    @Inject(method = "keyTyped", at = @At("HEAD"))
    protected void keyTyped(char typedChar, int keyCode, CallbackInfo ci) {
        System.out.println("char: " + typedChar + ", code: " + keyCode);
    }
}
