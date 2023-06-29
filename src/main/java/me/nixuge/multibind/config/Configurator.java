package me.nixuge.multibind.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import me.nixuge.multibind.McMod;

// Just reimplementing from scratch because why not :D
public class Configurator {
    private final File configFile;

    public Configurator(File configFile) {
        this.configFile = configFile;
    }

    public void resetConfig() {
        try (
            BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadKeybinds() {
        try {
            Scanner scanner = new Scanner(configFile);

            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            resetConfig();
        }
    }
}
