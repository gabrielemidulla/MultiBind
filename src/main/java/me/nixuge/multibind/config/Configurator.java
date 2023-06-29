package me.nixuge.multibind.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import me.nixuge.multibind.McMod;

// Just reimplementing from scratch because why not :D
public class Configurator {
    private final File config;

    public Configurator(File config) {
        this.config = config;
    }

}
