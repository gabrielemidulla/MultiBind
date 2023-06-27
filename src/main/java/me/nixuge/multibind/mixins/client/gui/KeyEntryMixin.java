package me.nixuge.multibind.mixins.client.gui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiKeyBindingList.KeyEntry;

@Mixin(KeyEntry.class)
public class KeyEntryMixin {
    private GuiButton previousButton;
    private GuiButton nextButton;
    private GuiButton newButton;
    
    @Inject(method = "<init>", at = @At("RETURN"))
    public void constructor(CallbackInfo ci) {
        this.previousButton = new GuiButton(0, 0, 0, 20, 20, "<");
        this.nextButton = new GuiButton(0, 0, 0, 20, 20, ">");
        this.newButton = new GuiButton(0, 0, 0, 20, 20, "+");
    }
    @Inject(method = "drawEntry", at = @At("RETURN"))
    public void onDrawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, CallbackInfo ci) {

    }
}
