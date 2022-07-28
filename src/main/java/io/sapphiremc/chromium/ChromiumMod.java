/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium;

import io.sapphiremc.chromium.shared.config.ChromiumConfig;
import io.sapphiremc.chromium.shared.config.ConfigManager;
import io.sapphiremc.chromium.server.skins.SkinsManager;
import io.sapphiremc.chromium.mixin.client.MixinMinecraftClient;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChromiumMod implements ModInitializer {
	public static final String MOD_ID = "chromium";
	public static final Logger LOGGER = LoggerFactory.getLogger("Chromium");

	@Getter
	private static EnvType env;

	@Getter
	private static ConfigManager configManager;
	@Getter
	private static SkinsManager skinsManager;

	@Override
	public void onInitialize() {
		env = FabricLoader.getInstance().getEnvironmentType();
		LOGGER.info("Initializing chromium by SapphireMC");

		configManager = new ConfigManager();

		if (env == EnvType.SERVER) {
			skinsManager = new SkinsManager();
		}
	}

	public static ChromiumConfig getConfig() {
		return configManager.getConfig();
	}

	@Environment(EnvType.CLIENT)
	public static String getFpsString() {
		MinecraftClient client = MinecraftClient.getInstance();

		String maxFPS = (double) client.options.getMaxFps().getValue() == GameOptions.MAX_FRAMERATE ? "\u221E" : client.options.getMaxFps().getValue().toString();
		String vsync = client.options.getEnableVsync().getValue().toString();
		return Text.translatable("options.chromium.fps", ((MixinMinecraftClient) client).getCurrentFPS(), maxFPS, vsync).getString();
	}

	@Environment(EnvType.CLIENT)
	public static String getTime() {
		return Text.translatable("options.chromium.time", new SimpleDateFormat("HH:mm:ss dd/MM").format(new Date())).getString();
	}

	@Environment(EnvType.CLIENT)
	private static String cachedCoords = "";

	@Environment(EnvType.CLIENT)
	public static String getCoordsString(LivingEntity entity) {
		if (entity != null) {
			cachedCoords = Text.translatable("options.chromium.coordinates", entity.getBlockX(), entity.getBlockY(), entity.getBlockZ()).getString();
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
			cachedLight = Text.translatable("options.chromium.light", clientLight, skyLight, blockLight).getString();
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
			Biome biome = client.world.getBiome(blockPos).value();
			Identifier biomeId = biomes.getId(biome);
			if (biomeId != null) {
				cachedBiome = Text.translatable("options.chromium.biome", Text.translatable("biome.minecraft." + biomeId.getPath())).getString();
			}
		}
		return cachedBiome;
	}
}
