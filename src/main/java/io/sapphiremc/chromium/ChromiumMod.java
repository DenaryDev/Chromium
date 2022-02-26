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
import io.sapphiremc.chromium.common.manager.Manager;
import io.sapphiremc.chromium.common.skins.SkinsManager;
import io.sapphiremc.chromium.mixin.client.MixinMinecraftClient;
import lombok.Getter;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChromiumMod implements ModInitializer {
	public static final String MOD_ID = "chromium";
	public static final Logger LOGGER = LogManager.getLogger("Chromium");

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
		LOGGER.info("Initializing chromium by SapphireMC");

		configManager = new ConfigManager();
		managers.add(configManager);
		skinsManager = new SkinsManager();
		managers.add(skinsManager);

		initializeManagers();
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

		String maxFPS = (double) client.options.maxFps == Option.FRAMERATE_LIMIT.getMax() ? "\u221E" : String.valueOf(client.options.maxFps);
		String vsync = String.valueOf(client.options.enableVsync);
		return new TranslatableText("options.chromium.fps", ((MixinMinecraftClient) client).getCurrentFPS(), maxFPS, vsync).getString();
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
