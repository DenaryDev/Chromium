/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium;

import io.sapphiremc.chromium.common.config.ChromiumConfig;
import io.sapphiremc.chromium.common.config.ConfigManager;
import io.sapphiremc.chromium.client.dummy.DummyClientPlayerEntity;
import io.sapphiremc.chromium.common.manager.Manager;
import io.sapphiremc.chromium.common.skins.SkinsManager;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Option;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChromiumMod implements ModInitializer {
	@Getter
	private static final String modId = "chromium";
	@Getter
	private static final Logger logger = LogManager.getLogger(modId);

	@Getter
	private static EnvType env;

	private final List<Manager> managers = new ArrayList<>();
	@Getter
	private static ConfigManager configManager;
	@Getter
	private static SkinsManager skinsManager;

	@Override
	public void onInitialize() {
		env = FabricLoader.getInstance().getEnvironmentType();
		logger.info((env.equals(EnvType.CLIENT) ? "[Chromium] " : "") + "Initializing chromium by SapphireMC");
		logger.info((env.equals(EnvType.CLIENT) ? "[Chromium] " : "") + "Running on " + env.name().toLowerCase() + "-side");

		configManager = new ConfigManager();
		managers.add(configManager);
		skinsManager = new SkinsManager();
		managers.add(skinsManager);

		initializeManagers();
		logger.info((env.equals(EnvType.CLIENT) ? "[Chromium] " : "") + "Chromium successfully initialized!");
	}

	private void initializeManagers() {
		for (Manager manager : managers) {
			if (manager.getEnv().equals(Manager.Env.BOTH)) {
				manager.initialize();
			} else if (manager.getEnv().equals(Manager.Env.CLIENT) && env.equals(EnvType.CLIENT)) {
				manager.initialize();
			} else if (manager.getEnv().equals(Manager.Env.SERVER) && env.equals(EnvType.SERVER)) {
				manager.initialize();
			}
		}
	}

	public static ChromiumConfig getConfig() {
		return configManager.getConfig();
	}

	@Environment(EnvType.CLIENT)
	public static String getFpsString() {
		MinecraftClient client = MinecraftClient.getInstance();
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

		String maxFPS = (double) client.options.maxFps == Option.FRAMERATE_LIMIT.getMax() ? "\u221E" : String.valueOf(client.options.maxFps);
		String vsync = String.valueOf(client.options.enableVsync);
		return new TranslatableText("options.chromium.fps", currentFps, maxFPS, vsync).getString();
	}

	@Environment(EnvType.CLIENT)
	public static String getTime() {
		return new TranslatableText("options.chromium.time", new SimpleDateFormat("HH:mm:ss dd/MM").format(new Date())).getString();
	}

	@Environment(EnvType.CLIENT)
	private static String cachedCoords = "";

	@Environment(EnvType.CLIENT)
	public static String getCoordsString(LivingEntity entity) {
		if (entity != null) {
			cachedCoords = new TranslatableText("options.chromium.coordinates", entity.getBlockX(), entity.getBlockY(), entity.getBlockZ()).getString();
		}
		return cachedCoords;
	}

	@Environment(EnvType.CLIENT)
	private static String cachedLight = "";

	@Environment(EnvType.CLIENT)
	public static String getLightString(PlayerEntity player) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (player != null && client.world != null) {
			BlockPos blockPos = player.getBlockPos();
			int clientLight = client.world.getChunkManager().getLightingProvider().getLight(blockPos, 0);
			int skyLight = client.world.getLightLevel(LightType.SKY, blockPos);
			int blockLight = client.world.getLightLevel(LightType.BLOCK, blockPos);
			cachedLight = new TranslatableText("options.chromium.light", clientLight, skyLight, blockLight).getString();
		}
		return cachedLight;
	}

	@Environment(EnvType.CLIENT)
	private static String cachedBiome = "";

	@Environment(EnvType.CLIENT)
	public static String getBiomeString(PlayerEntity player) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (player != null && client.world != null) {
			BlockPos blockPos = player.getBlockPos();
			Registry<Biome> biomes = client.world.getRegistryManager().get(Registry.BIOME_KEY);
			Biome biome = client.world.getBiome(blockPos);
			Identifier biomeId = biomes.getId(biome);
			if (biomeId != null) {
				cachedBiome = new TranslatableText("options.chromium.biome", new TranslatableText("biome.minecraft." + biomeId.getPath())).getString();
			}
		}
		return cachedBiome;
	}
}
