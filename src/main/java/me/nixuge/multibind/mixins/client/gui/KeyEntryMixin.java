package me.nixuge.multibind.mixins.client.gui;

import java.lang.reflect.Method;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.nixuge.multibind.areflections.ReflectionUtils;
import me.nixuge.multibind.binds.AlternativeKeyBinding;
import me.nixuge.multibind.binds.DataSaver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiKeyBindingList.KeyEntry;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;

@Mixin(KeyEntry.class)
public class KeyEntryMixin {
    // Due to Accessors amazingly not working in 1.8,
    // I have to rely on reflections.
    // Hope ull like it (:
    // Ps. If ANYONE finds a way to use accessor in here instead of reflections,
    // send me a dm
    // (even if it's a random Mixin fork)
    private final static Method getAlternativeKeybindsMethod;
    private final static Method addAlternativeBindMethod;
    static {
        getAlternativeKeybindsMethod = ReflectionUtils.getMethodFromNameAlone(KeyBinding.class,
                "getAlternativeKeybinds");
        addAlternativeBindMethod = ReflectionUtils.getMethodFromNameAlone(KeyBinding.class, "addAlternativeBind");
    }

    private GuiButton btnPrevious;
    private GuiButton btnNextNew;
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

    @SuppressWarnings("unchecked")
    private void grabAlternativeBinds() {
        try {
            this.alternativeBinds = (List<AlternativeKeyBinding>) getAlternativeKeybindsMethod.invoke(keybinding);
            this.alternativeCount = alternativeBinds.size();
        } catch (Exception e) {
            // lol
            System.out.println("Bad dev exception 1: " + e);
            System.out.println(1 / 0);
        }
    }

    private void addAlternativeBind() {
        try {
            addAlternativeBindMethod.invoke(keybinding, keybinding.getKeyCodeDefault());
        } catch (Exception e) {
            System.out.println("Bad dev exception 2: " + e);
            System.out.println(1 / 0);
        }
    }

    public int getKeyCodeAtCurrentIndex() {
        if (selectedBindIndex < 0)
            return keybinding.getKeyCode();

        return alternativeBinds.get(selectedBindIndex).getKeyCode();
    }

    public boolean isLastBind() {
        return selectedBindIndex == alternativeCount - 1;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void constructor(CallbackInfo ci) {
        this.btnReset.setWidth(30);
        // Note: i NEED a 3 char word here, so unfortunately ignoring all localization
        this.btnReset.displayString = "Res";

        this.btnPrevious = new GuiButton(0, 0, 0, 10, 20, "<");
        this.btnNextNew = new GuiButton(0, 0, 0, 10, 20, ">");

        grabAlternativeBinds();
    }

    // Injecting then cancelling not optimal,
    // but need to change some positions.
    @Inject(method = "drawEntry", at = @At("HEAD"), cancellable = true)
    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY,
            boolean isSelected, CallbackInfo ci) {
        // Modified vanilla buttons part
        boolean changingKeybindFlag = DataSaver.getGuiControls().buttonId == this.keybinding;
        mc.fontRendererObj.drawString(this.keyDesc,
                x + 90 - DataSaver.getMaxListLabelWidth(),
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
        // keybinding.
        // Added buttons part
        this.btnPrevious.xPosition = x + 114; // 105-1 for a bit more space
        this.btnPrevious.yPosition = y;
        this.btnPrevious.enabled = (selectedBindIndex != -1);
        this.btnPrevious.drawButton(mc, mouseX, mouseY);

        this.btnNextNew.xPosition = x + 231; // 230+1 for a bit more space
        this.btnNextNew.yPosition = y;
        this.btnNextNew.displayString = isLastBind() ? "+" : ">";
        this.btnNextNew.enabled = true;
        this.btnNextNew.drawButton(mc, mouseX, mouseY);

        ci.cancel();
    }

    @Inject(method = "mousePressed", at = @At("HEAD"), cancellable = true)
    public void mousePressed(int slotIndex, int mouseX, int mouseY, int _1, int _2, int _3,
            CallbackInfoReturnable<Boolean> cir) {
        System.out.println(selectedBindIndex);
        System.out.println(alternativeCount - 1);
        if (this.btnPrevious.mousePressed(mc, mouseX, mouseY)) {
            this.selectedBindIndex--;
            cir.setReturnValue(true);
        }
        if (this.btnNextNew.mousePressed(mc, mouseX, mouseY)) {
            if (isLastBind())
                addAlternativeBind();

            this.alternativeCount = alternativeBinds.size();
            this.selectedBindIndex++;
            cir.setReturnValue(true);
        }
        if (this.btnChangeKeyBinding.mousePressed(mc, mouseX, mouseY)) {
            // TODO: EDIT THIS TO SET THE KEY TO THE CORRECT ALTERNATIVE KEYBIND
            DataSaver.getGuiControls().buttonId = this.keybinding;
            cir.setReturnValue(true);
        } 
        if (this.btnReset.mousePressed(mc, mouseX, mouseY)) {
            mc.gameSettings.setOptionKeyBinding(this.keybinding,
                    this.keybinding.getKeyCodeDefault());
            KeyBinding.resetKeyBindingArrayAndHash();
            cir.setReturnValue(true);
        }
        cir.setReturnValue(false);
    }
}
