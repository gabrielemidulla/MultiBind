package me.nixuge.multibind.accessors;

import java.util.List;

import me.nixuge.multibind.binds.AlternativeKeyBinding;

public interface KeyBindAccessor {
    List<AlternativeKeyBinding> getAlternativeKeybinds();
    void addAlternativeBind(int keyCode);
    void setSelectedBindIndex(int index);
}
