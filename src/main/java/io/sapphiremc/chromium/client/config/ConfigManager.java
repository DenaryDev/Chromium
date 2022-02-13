/*
 * Copyright (c) 2022 DenaryDev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 */
package io.sapphiremc.chromium.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.sapphiremc.chromium.client.ChromiumClientMod;
import java.nio.charset.Charset;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import java.io.File;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FileUtils;

public class ConfigManager {

	@Getter
	private ChromiumConfig config;

	private final Gson gson;
	private final File configFile;

	private final Executor executor = Executors.newSingleThreadExecutor();

	public ConfigManager() {
		this.gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
		this.configFile = new File(FabricLoader.getInstance().getConfigDir().toString() + File.separator + ChromiumClientMod.getModId(), "settings.json");
		readConfig(false);
	}

	public void readConfig(boolean async) {
		Runnable task = () -> {
			try {
				if (configFile.exists()) {
					String content = FileUtils.readFileToString(configFile, Charset.defaultCharset());
					config = gson.fromJson(content, ChromiumConfig.class);
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
