package me.nixuge.multibind.binds;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.IntHashMap;

public class AlternativeKeyBinding {
    private static final List<AlternativeKeyBinding> alternativeKeybindArray = Lists.<AlternativeKeyBinding>newArrayList();
    private static final IntHashMap<AlternativeKeyBinding> alternativeHash = new IntHashMap<AlternativeKeyBinding>();
    private int keyCode;
    private boolean pressed;
    private int pressTime;
    private KeyBinding parentKeybind;

    public AlternativeKeyBinding(KeyBinding parent, int keyCode) {
        this.parentKeybind = parent;
        this.keyCode = keyCode;
        alternativeKeybindArray.add(this);
        alternativeHash.addKey(keyCode, this);
    }

    // New functions
    public static void removeAlternativeKeybindFromArray(AlternativeKeyBinding keyBinding) {
        alternativeKeybindArray.remove(keyBinding);
    }

    // Vanilla-keybind like functions
    public static void onTick(int keyCode) {
        if (keyCode == 0)
            return;

        AlternativeKeyBinding keybinding = (AlternativeKeyBinding) alternativeHash.lookup(keyCode);

        if (keybinding != null)
            ++keybinding.pressTime;
    }

    public static void setKeyBindState(int keyCode, boolean pressed) {
        if (keyCode == 0)
            return;

        AlternativeKeyBinding keybinding = (AlternativeKeyBinding) alternativeHash.lookup(keyCode);

        if (keybinding != null)
            keybinding.pressed = pressed;
    }

    public static void unPressAllKeys() {
        for (AlternativeKeyBinding keybinding : alternativeKeybindArray) {
            keybinding.unpressKey();
        }
    }

    public static void resetKeyBindingArrayAndHash() {
        alternativeHash.clearMap();

        for (AlternativeKeyBinding keybinding : alternativeKeybindArray) {
            alternativeHash.addKey(keybinding.keyCode, keybinding);
        }
    }

    public boolean isKeyDown() {
        return this.pressed;
    }

    public boolean isPressed() {
        if (this.pressTime == 0) {
            return false;
        } else {
            --this.pressTime;
            return true;
        }
    }

    private void unpressKey() {
        this.pressTime = 0;
        this.pressed = false;
    }

    public int getKeyCode() {
        return this.keyCode;
    }

    public KeyBinding getParentKeybind() {
        return this.parentKeybind;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    // Todo: reimplement?
    // public int compareTo(KeyBinding p_compareTo_1_) {
    //     int i = I18n.format(this.keyCategory, new Object[0])
    //             .compareTo(I18n.format(p_compareTo_1_.keyCategory, new Object[0]));

    //     if (i == 0) {
    //         i = I18n.format(this.keyDescription, new Object[0])
    //                 .compareTo(I18n.format(p_compareTo_1_.keyDescription, new Object[0]));
    //     }

    //     return i;
    // }
}
