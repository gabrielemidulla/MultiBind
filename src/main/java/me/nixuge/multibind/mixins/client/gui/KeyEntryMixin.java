package me.nixuge.multibind.mixins.client.gui;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.nixuge.multibind.accessors.KeyBindAccessor;
import me.nixuge.multibind.binds.AlternativeKeyBinding;
import me.nixuge.multibind.mixins.accessors.GuiKBLMixinAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiKeyBindingList;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;

@Mixin({GuiKeyBindingList.KeyEntry.class})
public class KeyEntryMixin {
    // ===== Vars =====
    private GuiButton btnPrevious;
    private GuiButton btnNextNew;
    private GuiButton btnDeleteCurrentBind;
    // TODO: delete current keybind button

    // -1 = normal, other = alternative list index
    private int selectedBindIndex = -1;

    private List<AlternativeKeyBinding> alternativeBinds;
    private int alternativeCount;

    private final Minecraft mc = Minecraft.getMinecraft();

    @Shadow
    private GuiButton btnReset;
    @Shadow
    private GuiButton btnChangeKeyBinding;
    @Shadow
    private KeyBinding keybinding;
    @Shadow
    private String keyDesc;
    
    @Shadow(aliases = {"this$0", "field_148284_a"})
    private GuiKeyBindingList outer;


    // Random "utils" functions
    public int getKeyCodeAtCurrentIndex() {
        if (selectedBindIndex < 0)
            return keybinding.getKeyCode();

            return alternativeBinds.get(selectedBindIndex).getKeyCode();
    }

    public boolean isLastBind() {
        return selectedBindIndex == alternativeCount - 1;
    }


    // Mixins
    @Inject(method = "<init>", at = @At("RETURN"))
    public void constructor(CallbackInfo ci) {
        this.btnReset.setWidth(30);
        // Note: i NEED a 3 char word here, so unfortunately ignoring all localization
        this.btnReset.displayString = "Res";

        this.btnPrevious = new GuiButton(0, 0, 0, 10, 20, "<");
        this.btnNextNew = new GuiButton(0, 0, 0, 10, 20, ">");
        this.btnDeleteCurrentBind = new GuiButton(0, 0, 0, 10, 20, "§cX");

        this.alternativeBinds = ((KeyBindAccessor)keybinding).getAlternativeKeybinds();
        this.alternativeCount = alternativeBinds.size();
    }

    @Overwrite
    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY,
            boolean isSelected) {
        // Modified vanilla buttons part
        boolean changingKeybindFlag = ((GuiKBLMixinAccessor)outer).getGuiControls().buttonId == this.keybinding;
        String finalKeyStr = (this.selectedBindIndex != -1) ? 
            this.keyDesc + " (alt " + (this.selectedBindIndex+1) + ")" :
            this.keyDesc;
        
        mc.fontRendererObj.drawString(finalKeyStr,
                x + 90 - ((GuiKBLMixinAccessor)outer).getMaxListLabelWidth(),
                y + slotHeight / 2 - mc.fontRendererObj.FONT_HEIGHT / 2, 16777215);
        this.btnReset.xPosition = x + 200;
        this.btnReset.yPosition = y;
        this.btnReset.enabled = (this.keybinding.getKeyCode() != this.keybinding.getKeyCodeDefault()
                || this.alternativeCount > 0);
        this.btnReset.drawButton(mc, mouseX, mouseY);
        this.btnChangeKeyBinding.xPosition = x + 125;
        this.btnChangeKeyBinding.yPosition = y;
        this.btnChangeKeyBinding.displayString = GameSettings.getKeyDisplayString(getKeyCodeAtCurrentIndex());
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

        // Added buttons part
        this.btnPrevious.xPosition = x + 114; // 115-1 for a bit more space
        this.btnPrevious.yPosition = y;
        this.btnPrevious.enabled = (selectedBindIndex != -1);
        this.btnPrevious.drawButton(mc, mouseX, mouseY);

        this.btnNextNew.xPosition = x + 231; // 230+1 for a bit more space
        this.btnNextNew.yPosition = y;
        this.btnNextNew.displayString = isLastBind() ? "+" : ">";
        this.btnNextNew.enabled = true;
        this.btnNextNew.drawButton(mc, mouseX, mouseY);

        this.btnDeleteCurrentBind.xPosition = x + 243; // 230+1 for a bit more space
        this.btnDeleteCurrentBind.yPosition = y;
        this.btnDeleteCurrentBind.enabled = (selectedBindIndex >= 0);
        this.btnDeleteCurrentBind.drawButton(mc, mouseX, mouseY);
    }

    @Overwrite
    public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int _1, int _2, int _3) {
        if (this.btnPrevious.mousePressed(mc, mouseX, mouseY)) {
            this.selectedBindIndex--;
            ((KeyBindAccessor)keybinding).setSelectedBindIndex(selectedBindIndex);

            return true;
        }
        if (this.btnNextNew.mousePressed(mc, mouseX, mouseY)) {
            if (isLastBind())
                ((KeyBindAccessor)keybinding).addAlternativeBind(keybinding.getKeyCodeDefault());

            this.alternativeCount = alternativeBinds.size();
            this.selectedBindIndex++;
            ((KeyBindAccessor)keybinding).setSelectedBindIndex(selectedBindIndex);

            return true;
        }
        if (this.btnDeleteCurrentBind.mousePressed(mc, mouseX, mouseY)) {
            ((KeyBindAccessor)keybinding).removeAlternativeKeybinding(alternativeBinds.get(selectedBindIndex));

            this.alternativeCount = alternativeBinds.size();
            this.selectedBindIndex--;

            return true;
        }
        if (this.btnChangeKeyBinding.mousePressed(mc, mouseX, mouseY)) {
            ((GuiKBLMixinAccessor)outer).getGuiControls().buttonId = this.keybinding;

            return true;
        } 
        if (this.btnReset.mousePressed(mc, mouseX, mouseY)) {
            mc.gameSettings.setOptionKeyBinding(this.keybinding, this.keybinding.getKeyCodeDefault());
            KeyBinding.resetKeyBindingArrayAndHash();

            ((KeyBindAccessor)keybinding).removeAllAlternativeKeybindings();
            this.alternativeCount = alternativeBinds.size();
            this.selectedBindIndex = -1;

            return true;
        }
        return false;
    }
}
