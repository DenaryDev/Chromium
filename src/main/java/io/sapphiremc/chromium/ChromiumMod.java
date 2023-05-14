/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium;

import io.sapphiremc.chromium.config.ChromiumConfig;
import io.sapphiremc.chromium.config.ConfigManager;
import io.sapphiremc.chromium.mixin.client.accessors.MinecraftAccessor;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChromiumMod implements ModInitializer {
    public static final String MOD_ID = "chromium";
    public static final Logger LOGGER = LoggerFactory.getLogger("Chromium");

    @Getter
    private static ConfigManager configManager;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing chromium by SapphireMC");

        configManager = new ConfigManager();
    }

    public static ChromiumConfig getConfig() {
        return configManager.getConfig();
    }

    @Environment(EnvType.CLIENT)
    public static String getFpsString() {
        final var client = Minecraft.getInstance();

        final var maxFPS = (double) client.options.framerateLimit().get() == Options.UNLIMITED_FRAMERATE_CUTOFF ? "∞" : client.options.framerateLimit().get().toString();
        final var vsync = client.options.enableVsync().get().toString();
        return Component.translatable("options.chromium.fps", ((MinecraftAccessor) client).getFrames(), maxFPS, vsync).getString();
    }

    @Environment(EnvType.CLIENT)
    public static String getTimeString() {
        return Component.translatable("options.chromium.time", new SimpleDateFormat("HH:mm:ss dd/MM").format(new Date())).getString();
    }

    @Environment(EnvType.CLIENT)
    private static String cachedCoords = "";

    @Environment(EnvType.CLIENT)
    public static String getCoordsString(LivingEntity entity) {
        if (entity != null) {
            cachedCoords = Component.translatable("options.chromium.coordinates", entity.getBlockX(), entity.getBlockY(), entity.getBlockZ()).getString();
        }
        return cachedCoords;
    }

    @Environment(EnvType.CLIENT)
    private static String cachedLight = "";

    @Environment(EnvType.CLIENT)
    public static String getLightString(Player player) {
        final var client = Minecraft.getInstance();
        if (player != null && client.level != null) {
            final var blockPos = player.getOnPos();
            final int clientLight = client.level.getChunkSource().getLightEngine().getRawBrightness(blockPos, 0);
            final int skyLight = client.level.getBrightness(LightLayer.SKY, blockPos);
            final int blockLight = client.level.getBrightness(LightLayer.BLOCK, blockPos);
            cachedLight = Component.translatable("options.chromium.light", clientLight, skyLight, blockLight).getString();
        }
        return cachedLight;
    }

    @Environment(EnvType.CLIENT)
    private static String cachedBiome = "";

    @Environment(EnvType.CLIENT)
    public static String getBiomeString(Player player) {
        final var client = Minecraft.getInstance();
        if (player != null && client.level != null) {
            final var blockPos = player.getOnPos();
            final var biomes = client.level.registryAccess().registryOrThrow(Registries.BIOME);
            final var biome = client.level.getBiome(blockPos).value();
            final var biomeId = biomes.getKey(biome);
            if (biomeId != null) {
                cachedBiome = Component.translatable("options.chromium.biome", Component.translatable("biome.minecraft." + biomeId.getPath())).getString();
            }
        }
        return cachedBiome;
    }
}
