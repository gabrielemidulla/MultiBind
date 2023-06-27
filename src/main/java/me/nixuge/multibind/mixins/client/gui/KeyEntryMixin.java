package me.nixuge.multibind.mixins.client.gui;

import java.lang.reflect.Field;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.nixuge.multibind.binds.DataSaver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiKeyBindingList.KeyEntry;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;

@Mixin(KeyEntry.class)
public class KeyEntryMixin {
    private GuiButton btnPrevious;
    private GuiButton btnNextNew;
    // TODO: delete current keybind button
    // -1 = normal, other = alternative list index
    private int currentlySelectedBind = -1;

    private final Minecraft mc = Minecraft.getMinecraft();

    @Shadow
    private GuiButton btnReset;
    @Shadow
    private GuiButton btnChangeKeyBinding;
    @Shadow
    private KeyBinding keybinding;
    @Shadow
    private String keyDesc;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void constructor(CallbackInfo ci) {
        this.btnPrevious = new GuiButton(0, 0, 0, 10, 20, "<");
        this.btnNextNew = new GuiButton(0, 0, 0, 10, 20, ">");
        this.btnReset.setWidth(30);
        // Note: i NEED a 3 char word here, so unfortunately ignoring all localization
        this.btnReset.displayString = "Res";
    }

    // Injecting then cancelling not optimal,
    // but need to change some positions.
    @Inject(method = "drawEntry", at = @At("HEAD"), cancellable = true)
    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY,
            boolean isSelected, CallbackInfo ci) {
        // Vanilla part
        boolean changingKeybindFlag = DataSaver.getGuiControls().buttonId == this.keybinding;
        mc.fontRendererObj.drawString(this.keyDesc,
                x + 90 - DataSaver.getMaxListLabelWidth(),
                y + slotHeight / 2 - mc.fontRendererObj.FONT_HEIGHT / 2, 16777215);
        this.btnReset.xPosition = x + 200;
        this.btnReset.yPosition = y;
        this.btnReset.enabled = this.keybinding.getKeyCode() != this.keybinding.getKeyCodeDefault();
        this.btnReset.drawButton(mc, mouseX, mouseY);
        this.btnChangeKeyBinding.xPosition = x + 125; // Changed lin
        this.btnChangeKeyBinding.yPosition = y;
        this.btnChangeKeyBinding.displayString = GameSettings.getKeyDisplayString(this.keybinding.getKeyCode());
        boolean duplicateKeybindFlag = false;

        if (this.keybinding.getKeyCode() != 0) {
            for (KeyBinding keybinding : mc.gameSettings.keyBindings) {
                if (keybinding != this.keybinding && keybinding.getKeyCode() == this.keybinding.getKeyCode()) {
                    duplicateKeybindFlag = true;
                    break;
                }
            }
        }

        if (changingKeybindFlag) {
            this.btnChangeKeyBinding.displayString = EnumChatFormatting.WHITE + "> " +
                    EnumChatFormatting.YELLOW
                    + this.btnChangeKeyBinding.displayString + EnumChatFormatting.WHITE + " <";
        } else if (duplicateKeybindFlag) {
            this.btnChangeKeyBinding.displayString = EnumChatFormatting.RED + this.btnChangeKeyBinding.displayString;
        }
        this.btnChangeKeyBinding.drawButton(mc, mouseX, mouseY);

        // Modded part
        this.btnPrevious.xPosition = x + 114; // 105-1 for a bit more space
        this.btnPrevious.yPosition = y;
        this.btnPrevious.enabled = (currentlySelectedBind == -1);
        this.btnPrevious.drawButton(mc, mouseX, mouseY);
        
        this.btnNextNew.xPosition = x + 231; // 230+1 for a bit more space
        this.btnNextNew.yPosition = y;
        this.btnNextNew.enabled = true;
        this.btnNextNew.drawButton(mc, mouseX, mouseY);

        ci.cancel();
    }
}
