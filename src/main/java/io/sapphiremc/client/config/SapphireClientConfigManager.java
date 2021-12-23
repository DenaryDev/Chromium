/*
 * Copyright (c) 2021 DenaryDev
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
package io.sapphiremc.client.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import io.sapphiremc.client.SapphireClientMod;
import io.sapphiremc.client.config.option.BooleanConfigOption;
import io.sapphiremc.client.config.option.ConfigOptionStorage;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;

public class SapphireClientConfigManager {
	private static File file;

	private static void prepareConfigFile() {
		if (file != null) {
			return;
		}
		file = new File(FabricLoader.getInstance().getConfigDir().toFile(), SapphireClientMod.MOD_ID + ".json");
	}

	public static void initializeConfig() {
		load();
	}

	private static void load() {
		prepareConfigFile();

		try {
			if (!file.exists()) {
				save();
			}
			if (file.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(file));
				JsonObject json = new JsonParser().parse(br).getAsJsonObject();

				for (Field field : SapphireClientConfig.class.getDeclaredFields()) {
					if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
						if (BooleanConfigOption.class.isAssignableFrom(field.getType())) {
							JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive(field.getName().toLowerCase(Locale.ROOT));
							if (jsonPrimitive != null && jsonPrimitive.isBoolean()) {
								BooleanConfigOption option = (BooleanConfigOption) field.get(null);
								ConfigOptionStorage.setBoolean(option.getKey(), jsonPrimitive.getAsBoolean());
							}
						}
					}
				}
			}
		} catch (FileNotFoundException | IllegalAccessException e) {
			System.err.println("Couldn't load Mod Menu configuration file; reverting to defaults");
			e.printStackTrace();
		}
	}

	public static void save() {
		prepareConfigFile();

		JsonObject config = new JsonObject();

		try {
			for (Field field : SapphireClientConfig.class.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
					if (BooleanConfigOption.class.isAssignableFrom(field.getType())) {
						BooleanConfigOption option = (BooleanConfigOption) field.get(null);
						config.addProperty(field.getName().toLowerCase(Locale.ROOT), ConfigOptionStorage.getBoolean(option.getKey()));
					}
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		String jsonString = SapphireClientMod.GSON.toJson(config);

		try (FileWriter fileWriter = new FileWriter(file)) {
			fileWriter.write(jsonString);
		} catch (IOException e) {
			System.err.println("Couldn't save Mod Menu configuration file");
			e.printStackTrace();
		}
	}
}
