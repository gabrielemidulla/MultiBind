package me.nixuge.multibind;

import lombok.Getter;
import lombok.Setter;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

@Mod(
        modid = McMod.MOD_ID,
        name = McMod.NAME,
        version = McMod.VERSION,
        clientSideOnly = true
)
// @Log4j2
@Getter
@Setter
public class McMod {
    // Original: NMUK
    // Todo: add searching capatiblity & rename "BetterBinds" (or do another mod)
    // Todo: add saving
    public static final String MOD_ID = "multibind";
    public static final String NAME = "Multi Bind";
    public static final String VERSION = "1.0.0";

    @Getter
    @Mod.Instance(value = McMod.MOD_ID)
    private static McMod instance;

    private Configuration configuration;
    private String configDirectory;

    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        this.configDirectory = event.getModConfigurationDirectory().toString();
        final File path = new File(this.configDirectory + File.separator + McMod.MOD_ID + ".cfg");
        this.configuration = new Configuration(path);
    }

    @Mod.EventHandler
    public void init(final FMLInitializationEvent event) {
    }


    // 2do maybe?: handle c07packetplayerdigging, c08packetplayerblockplacement packets
    // TODO: get config working
    // Last commit with forge config: d3b7dc2f9cabafd80945261022f9c3c35ce3977c
    // Files in config/ gui/ & McMod.java

    // TODO: fix
    //Caused by: java.lang.IllegalArgumentException: Cannot get property PropertyDirection{name=facing, clazz=class net.minecraft.util.EnumFacing, values=[north, south, west, east]} as it does not exist in BlockState{block=minecraft:air, properties=[]}
}
