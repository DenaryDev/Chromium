/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.sapphiremc.chromium.ChromiumMod;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ConfigManager {

    @Getter
    private ChromiumConfig config;

    private final Gson gson;
    private final File configFile;

    private final Executor executor = Executors.newSingleThreadExecutor();

    public ConfigManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
        this.configFile = new File(FabricLoader.getInstance().getConfigDir().toString() + File.separator + ChromiumMod.MOD_ID, "settings.json");
        readConfig(false);
    }

    public void readConfig(boolean async) {
        final Runnable task = () -> {
            try {
                if (configFile.exists()) {
                    final var content = FileUtils.readFileToString(configFile, Charset.defaultCharset());
                    config = gson.fromJson(content, ChromiumConfig.class);

                    boolean changed = false;
                    if (config.getHopperTransfer() < 2) {
                        ChromiumMod.LOGGER.warn("Hopper transfer must not be less than 2");
                        config.setHopperTransfer(2);
                        changed = true;
                    } else if (config.getHopperTransfer() > 200) {
                        ChromiumMod.LOGGER.warn("Hopper transfer must not be greater than 200");
                        config.setHopperTransfer(200);
                        changed = true;
                    } else if (config.getHopperAmount() < 1) {
                        ChromiumMod.LOGGER.warn("Hopper amount must not be less than 1");
                        config.setHopperAmount(1);
                        changed = true;
                    } else if (config.getHopperAmount() > 64) {
                        ChromiumMod.LOGGER.warn("Hopper amount must not be greater than 64");
                        config.setHopperAmount(64);
                        changed = true;
                    } else if (config.getMaxMessages() < 100) {
                        ChromiumMod.LOGGER.warn("Max messages must not be greater than 100");
                        config.setMaxMessages(100);
                        changed = true;
                    } else if (config.getMaxMessages() > 32767) {
                        ChromiumMod.LOGGER.warn("Max messages must not be greater than 32767");
                        config.setMaxMessages(32767);
                        changed = true;
                    }
                    if (changed) writeConfig(async);
                } else {
                    writeNewConfig();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                writeNewConfig();
            }
        };

        if (async) executor.execute(task);
        else task.run();
    }

    public void writeNewConfig() {
        config = new ChromiumConfig();
        writeConfig(false);
    }

    public void writeConfig(boolean async) {
        Runnable task = () -> {
            try {
                if (config != null) {
                    String serialized = gson.toJson(config);
                    FileUtils.writeStringToFile(configFile, serialized, Charset.defaultCharset());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        if (async) executor.execute(task);
        else task.run();
    }
}
