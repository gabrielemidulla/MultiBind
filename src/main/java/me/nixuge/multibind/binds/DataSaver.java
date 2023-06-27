package me.nixuge.multibind.binds;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiControls;
// import net.minecraft.client.gui.GuiKeyBindingList;

// Why does this class exist?
// Basically i wanted to use some dirty but working Mixin things to get some data
// Problem is: KeyEntry is nested inside of KeyBindingList,
// and there is NO (not compiler specific) WAY to access an outer class like that.
// So just saving those values in KeyBindingList's constructors,
// so they can be reused after with no problem
// https://stackoverflow.com/questions/763543/in-java-how-do-i-access-the-outer-class-when-im-not-in-the-inner-class

// Also accessors are broken on this mixin version iirc lmfao

public class DataSaver {
    @Setter
    @Getter
    private static GuiControls guiControls;
    @Setter
    @Getter
    private static int maxListLabelWidth;
}
