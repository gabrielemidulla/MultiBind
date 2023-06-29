package me.nixuge.multibind.mixins.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiKeyBindingList;

@Mixin(GuiKeyBindingList.class)
public interface GuiKBLMixinAccessor {
    @Accessor("field_148191_k")
    GuiControls getGuiControls();

    @Accessor
    int getMaxListLabelWidth();
}
