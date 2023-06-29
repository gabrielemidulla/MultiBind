package me.nixuge.multibind.mixins.client;

import java.io.File;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ScreenShotHelper;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow
    GuiScreen currentScreen;
    @Shadow
    GameSettings gameSettings;
    @Shadow
    GuiIngame ingameGUI;
    @Shadow
    File mcDataDir;
    @Shadow
    int displayWidth;
    @Shadow
    int displayHeight;
    @Shadow
    Framebuffer framebufferMc;

    private static long getSysTime() {
        return Sys.getTime() * 1000L / Sys.getTimerResolution();
    }

    @Shadow
    public void toggleFullscreen() {}

    // TODO: fix the other key press functions (see GuiContainer)
    // While at it removed every "twitch stream" bind
    // Note: has to be an "Inject" to avoid crashing other mods hooking on it
    @Inject(method = "dispatchKeypresses", at = @At("HEAD"), cancellable = true)
    public void dispatchKeypresses(CallbackInfo ci) {
        int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() : Keyboard.getEventKey();

        if ((i != 0 && !Keyboard.isRepeatEvent())
            && (!(this.currentScreen instanceof GuiControls) || ((GuiControls) this.currentScreen).time <= getSysTime() - 20L)
            && Keyboard.getEventKeyState()) 
        {
            if (this.gameSettings.keyBindFullscreen.isPressed())
                this.toggleFullscreen();
            else if (this.gameSettings.keyBindScreenshot.isPressed())
                this.ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot(this.mcDataDir, this.displayWidth, this.displayHeight, this.framebufferMc));
        }
        ci.cancel();
    }
}
