package me.nixuge.multibind;

import lombok.Getter;
import lombok.Setter;
import me.nixuge.multibind.config.Configurator;
import net.minecraftforge.fml.common.Mod;
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
    // Todo: fix clearing keys
    // Todo: fix Ping!
    public static final String MOD_ID = "multibind";
    public static final String NAME = "Multi Bind";
    public static final String VERSION = "0.1.3";

    @Getter
    @Mod.Instance(value = McMod.MOD_ID)
    private static McMod instance;

    @Getter
    private Configurator configurator;
    private String configDirectory;

    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        this.configDirectory = event.getModConfigurationDirectory().toString();
        final File path = new File(this.configDirectory + File.separator + McMod.MOD_ID + ".cfg");
        this.configurator = new Configurator(path);
        configurator.loadKeybinds();
    }
}
