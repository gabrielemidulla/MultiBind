package me.nixuge.multibind.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

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

    // Pretty dirty ikr
    // Best in the west for dirty parsers
    // Yes i could just use desc:listid:keycode instead of desc$listid:keycode
    // for cleaner parsing, but this looks better in the config lmao
    private ConfigLine parseLine(String line) {
        line = line.replaceAll("altkey_", "");
        String[] split1 = line.split("\\$");
        String desc = split1[0];
        String[] split2 = split1[1].split(":");
        return new ConfigLine(desc, split2[0], split2[1]);
    }

    public void loadKeybinds() {
        try {
            List<KeyBinding> keyBindings = KeyBindingMixinAccessor.getKeybindArray();
            Scanner scanner = new Scanner(configFile);

            while (scanner.hasNextLine()) {
                ConfigLine line = parseLine(scanner.nextLine());        
                for (KeyBinding keyBinding : keyBindings) {
                    // Note: may be out of order
                    // Not like it really matters & shouldn't be, but possible. Didn't test extensively.
                    if (keyBinding.getKeyDescription().equals(line.getKeyDesc())) {
                        ((KeyBindAccessor)keyBinding).addAlternativeBind(line.getKeyCode());
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            resetConfig();
        }
    }
}
