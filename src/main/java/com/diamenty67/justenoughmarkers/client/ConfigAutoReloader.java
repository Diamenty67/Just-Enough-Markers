package com.diamenty67.justenoughmarkers.client;

import com.diamenty67.justenoughmarkers.JEMConfig;
import com.diamenty67.justenoughmarkers.JEMConstants;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

/**
 * Reloads the config while the game is running.
 * <p>
 * Forge already watches the config file, but its watcher ignores events that are reported twice
 * and never sees a file replaced by another one, which is what most editors do when saving
 * (notably on Windows). Changes made while the game runs were therefore often lost.
 * This watcher simply polls the file and reloads it through the same {@link net.minecraftforge.common.ForgeConfigSpec}
 * entry point Forge uses, so invalid values are still corrected and the cached values are cleared.
 */
public final class ConfigAutoReloader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final long POLL_INTERVAL_MS = 500;
    // Default name Forge gives to a config registered without an explicit file name
    private static final String CONFIG_FILE_NAME = JEMConstants.MOD_ID + "-" + ModConfig.Type.COMMON.extension() + ".toml";

    private static boolean started;

    private ConfigAutoReloader() {}

    public static synchronized void start() {
        if (started) return;
        started = true;

        Thread thread = new Thread(ConfigAutoReloader::watch, "JEM Config Reloader");
        thread.setDaemon(true);
        thread.start();
    }

    private static void watch() {
        FileStamp lastStamp = null;
        boolean changed = false;

        while (true) {
            try {
                Thread.sleep(POLL_INTERVAL_MS);
            } catch (InterruptedException e) {
                return;
            }

            try {
                ModConfig modConfig = ConfigTracker.INSTANCE.fileMap().get(CONFIG_FILE_NAME);
                // The config is loaded by Forge after the mod is constructed
                if (modConfig == null || modConfig.getConfigData() == null) continue;

                FileStamp stamp = FileStamp.of(modConfig.getFullPath());
                // File missing or being replaced right now, try again on the next tick
                if (stamp == null) continue;

                if (lastStamp == null) {
                    lastStamp = stamp;
                } else if (!stamp.equals(lastStamp)) {
                    // Still being written: wait until it is stable before reading it
                    lastStamp = stamp;
                    changed = true;
                } else if (changed) {
                    changed = false;
                    reload(modConfig);
                    // Reloading can rewrite the file (correction of invalid values)
                    FileStamp after = FileStamp.of(modConfig.getFullPath());
                    if (after != null) lastStamp = after;
                }
            } catch (Exception e) {
                LOGGER.warn("Could not check the config file for changes", e);
            }
        }
    }

    private static void reload(ModConfig modConfig) {
        Path path = modConfig.getFullPath();
        CommentedConfig data = modConfig.getConfigData();
        try {
            // Parse into a separate config first so a syntax error does not wipe the current values
            try (CommentedFileConfig probe = CommentedFileConfig.of(path)) {
                probe.load();
            }
            if (data instanceof FileConfig fileConfig) {
                fileConfig.load();
            }
            JEMConfig.COMMON_SPEC.setConfig(data);
            LOGGER.info("Config file {} changed, reloaded", modConfig.getFileName());
        } catch (Exception e) {
            LOGGER.warn("Config file {} could not be reloaded, keeping the previous values", modConfig.getFileName(), e);
        }
    }

    private record FileStamp(FileTime lastModified, long size) {
        static FileStamp of(Path path) {
            try {
                return new FileStamp(Files.getLastModifiedTime(path), Files.size(path));
            } catch (IOException e) {
                return null;
            }
        }
    }
}
