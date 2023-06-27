package me.nixuge.multibind.mixins.client.gui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.nixuge.multibind.binds.DataSaver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiKeyBindingList;

@Mixin(GuiKeyBindingList.class)
public class GuiKeyBindingListMixin {
    @Shadow
    private int maxListLabelWidth;
    
    @Inject(method = "<init>", at = @At("RETURN"))
    public void GuiKeyBindingList(GuiControls controls, Minecraft mcIn, CallbackInfo ci) {
        DataSaver.setGuiControls(controls);
        DataSaver.setMaxListLabelWidth(maxListLabelWidth);
    }
}
