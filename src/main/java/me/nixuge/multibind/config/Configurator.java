package me.nixuge.multibind.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import me.nixuge.multibind.McMod;
import me.nixuge.multibind.accessors.KeyBindAccessor;
import me.nixuge.multibind.binds.AlternativeKeyBinding;
import me.nixuge.multibind.mixins.accessors.KeyBindingMixinAccessor;
import net.minecraft.client.settings.KeyBinding;

// Just reimplementing from scratch because why not :D
// Note: this class is far from the most optimized, especially since we're 
// redoing the entire save process everytime a keybind is changed,
// but since you're not supposed to change your keybinds as often as you render a frame,
// I'm just letting it pass like that
public class Configurator {
    private final File configFile;

    public Configurator(File configFile) {
        this.configFile = configFile;
    }

    public void resetConfig() {
        writeToConfig("");
    }
    
    public void writeToConfig(String str) {
        try (
            BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            writer.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveKeybinds() {
        StringBuilder stringBuilder = new StringBuilder();

        List<KeyBinding> keyBindings = KeyBindingMixinAccessor.getKeybindArray();
        for (KeyBinding keyBinding : keyBindings) {
            List<AlternativeKeyBinding> alternativeKeyBindings = ((KeyBindAccessor)keyBinding).getAlternativeKeybinds();
            
            for (int i = 0; i < alternativeKeyBindings.size(); i++) {
                AlternativeKeyBinding alternativeKeyBinding = alternativeKeyBindings.get(i);
                stringBuilder.append("altkey_");
                stringBuilder.append(keyBinding.getKeyDescription());
                stringBuilder.append('$');
                stringBuilder.append(i);
                stringBuilder.append(':');
                stringBuilder.append(alternativeKeyBinding.getKeyCode());
                stringBuilder.append("\n");
            }
        }
        writeToConfig(stringBuilder.toString());
    }

    public void loadKeybinds() {
        try {
            Scanner scanner = new Scanner(configFile);

            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
        // List<KeyBinding> keyBindings = KeyBindingMixinAccessor.getKeybindArray();
        // for (KeyBinding keyBinding : keyBindings) {
        //     List<AlternativeKeyBinding> alternativeKeyBindings = ((KeyBindAccessor)keyBinding).getAlternativeKeybinds();
            
        //     for (int i = 0; i < alternativeKeyBindings.size(); i++) {
        //         AlternativeKeyBinding alternativeKeyBinding = alternativeKeyBindings.get(i);
        //         stringBuilder.append("altkey_");
        //         stringBuilder.append(keyBinding.getKeyDescription());
        //         stringBuilder.append('$');
        //         stringBuilder.append(i);
        //         stringBuilder.append(':');
        //         stringBuilder.append(alternativeKeyBinding.getKeyCode());
        //         stringBuilder.append("\n");
        //     }
        // }
        // writeToConfig(stringBuilder.toString());

            }
            scanner.close();
        } catch (FileNotFoundException e) {
            resetConfig();
        }
    }
}
