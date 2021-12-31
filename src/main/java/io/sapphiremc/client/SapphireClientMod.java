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
package io.sapphiremc.client;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.sapphiremc.client.config.SapphireClientConfigManager;
import io.sapphiremc.client.dummy.DummyClientPlayerEntity;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Option;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SapphireClientMod implements ClientModInitializer {

	public static final String MOD_ID = "sapphireclient";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();
	public static final MinecraftClient MC = MinecraftClient.getInstance();

	public static DummyClientPlayerEntity dummyClientPlayer;

	@Override
	public void onInitializeClient() {
		SapphireClientConfigManager.initializeConfig();
		dummyClientPlayer = DummyClientPlayerEntity.getInstance();
	}

	public static String getFpsString() {
		Field fpsField;
		int currentFps;

		try {
			fpsField = MinecraftClient.class.getDeclaredField("currentFps");
		} catch (NoSuchFieldException ex) {
			try {
				fpsField = MinecraftClient.class.getDeclaredField("field_1738");
			} catch (NoSuchFieldException ex2) {
				fpsField = null;
			}
		}

		if (fpsField != null) {
			try {
				fpsField.setAccessible(true);
				currentFps = fpsField.getInt(MinecraftClient.class);
			} catch (IllegalAccessException ex) {
				currentFps = -1;
			}
		} else {
			currentFps = -1;
		}

		String maxFPS = (double) MC.options.maxFps == Option.FRAMERATE_LIMIT.getMax() ? "\u221E" : String.valueOf(MC.options.maxFps);
		String vsync = String.valueOf(MC.options.enableVsync);
		return new TranslatableText("sapphireclient.fps", currentFps, maxFPS, vsync).getString();
	}

	public static String getTime() {
		return new TranslatableText("sapphireclient.time", new SimpleDateFormat("HH:mm:ss dd/MM").format(new Date())).getString();
	}

	private static String cachedCoords = "";

	public static String getCoordsString(LivingEntity entity) {
		if (entity != null) {
			cachedCoords = new TranslatableText("sapphireclient.coordinates", entity.getBlockX(), entity.getBlockY(), entity.getBlockZ()).getString();
		}
		return cachedCoords;
	}

	private static String cachedLight = "";

	public static String getLightString(BlockPos blockPos) {
		if (blockPos != null && MC.world != null) {
			int clientLight = MC.world.getChunkManager().getLightingProvider().getLight(blockPos, 0);
			int skyLight = MC.world.getLightLevel(LightType.SKY, blockPos);
			int blockLight = MC.world.getLightLevel(LightType.BLOCK, blockPos);
			cachedLight = new TranslatableText("sapphireclient.light", clientLight, skyLight, blockLight).getString();
		}
		return cachedLight;
	}

	private static String cachedBiome = "";

	public static String getBiomeString(BlockPos blockPos) {
		if (blockPos != null && MC.world != null) {
			Registry<Biome> biomes = MC.world.getRegistryManager().get(Registry.BIOME_KEY);
			Biome biome = MC.world.getBiome(blockPos);
			Identifier biomeId = biomes.getId(biome);
			if (biomeId != null) {
				cachedBiome = new TranslatableText("sapphireclient.biome", new TranslatableText("biome.minecraft." + biomeId.getPath())).getString();
			}
		}
		return cachedBiome;
	}
}
