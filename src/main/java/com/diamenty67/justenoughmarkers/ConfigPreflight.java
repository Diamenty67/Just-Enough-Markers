package com.diamenty67.justenoughmarkers;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Forge does not recover from a config file that fails to parse at all (e.g. a typo while hand-
 * editing {@code categories}, like a missing quote): it crashes the entire game on startup instead
 * of just refusing that one mod. This runs before Forge opens the file, and if it cannot even be
 * parsed, moves it aside so the game still starts — with a fresh, default config — instead of
 * crashing outright.
 */
final class ConfigPreflight {
    private static final Logger LOGGER = LogUtils.getLogger();

    private ConfigPreflight() {}

    static void checkAndQuarantineIfBroken() {
        Path path = FMLPaths.CONFIGDIR.get().resolve(JEMConstants.MOD_ID + "-" + ModConfig.Type.COMMON.extension() + ".toml");
        if (!Files.isRegularFile(path)) return;

        try (CommentedFileConfig probe = CommentedFileConfig.of(path)) {
            probe.load();
        } catch (Exception e) {
            String stamp = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").format(LocalDateTime.now());
            Path quarantined = path.resolveSibling(path.getFileName() + ".broken-" + stamp);
            try {
                Files.move(path, quarantined, StandardCopyOption.REPLACE_EXISTING);
                LOGGER.error("Config file {} could not be parsed and was moved to {}; a fresh default config will be"
                                + " created so the game can start. Your previous settings are still in that file, in"
                                + " case you want to fix and restore them by hand.",
                        path.getFileName(), quarantined.getFileName(), e);
            } catch (IOException moveFailed) {
                LOGGER.error("Config file {} could not be parsed, and could not be moved aside either;"
                        + " the game may fail to start", path.getFileName(), e);
            }
        }
    }
}
