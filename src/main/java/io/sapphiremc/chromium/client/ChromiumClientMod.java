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
package io.sapphiremc.chromium.client;

import com.google.common.io.ByteArrayDataOutput;
import io.sapphiremc.chromium.client.config.ChromiumConfig;
import io.sapphiremc.chromium.client.config.ConfigManager;
import io.sapphiremc.chromium.client.gui.OptionsScreenBuilder;
import io.sapphiremc.chromium.client.gui.ChromiumTitleScreen;
import io.sapphiremc.chromium.client.util.Constants;
import io.sapphiremc.chromium.client.dummy.DummyClientPlayerEntity;
import io.sapphiremc.chromium.client.network.Packet;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Option;
import net.minecraft.client.util.InputUtil;
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
import org.lwjgl.glfw.GLFW;

public class ChromiumClientMod implements ClientModInitializer {

	@Getter
	private static ChromiumClientMod instance;
	@Getter
	private static final String modId = "chromium";
	@Getter
	private static final Logger logger = LogManager.getLogger(modId);

	@Getter
	private static DummyClientPlayerEntity dummyClientPlayer;

	@Getter
	private ConfigManager configManager;

	private KeyBinding configKey;

	@Override
	public void onInitializeClient() {
		instance = this;

		dummyClientPlayer = DummyClientPlayerEntity.getInstance();

		configManager = new ConfigManager();

		configKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.chromium.config",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_N,
				"Chromium"
		));

		ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) -> {
			if (!client.isInSingleplayer()) {
				ByteArrayDataOutput out = Packet.out();
				out.writeInt(Constants.PROTOCOL_ID);
				Packet.send(Constants.HELLO, out);
			}
		}));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (configKey.isPressed()) {
				client.setScreen(OptionsScreenBuilder.build(this));
			}
		});
		ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof TitleScreen && getConfig().getTitleScreenProvider().equals(ChromiumConfig.TitleScreenProvider.CHROMIUM)) {
				client.setScreen(new ChromiumTitleScreen());
			}
		});
	}

	public ChromiumConfig getConfig() {
		return configManager.getConfig();
	}

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

	public static String getTime() {
		return new TranslatableText("options.chromium.time", new SimpleDateFormat("HH:mm:ss dd/MM").format(new Date())).getString();
	}

	private static String cachedCoords = "";

	public static String getCoordsString(LivingEntity entity) {
		if (entity != null) {
			cachedCoords = new TranslatableText("options.chromium.coordinates", entity.getBlockX(), entity.getBlockY(), entity.getBlockZ()).getString();
		}
		return cachedCoords;
	}

	private static String cachedLight = "";

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

	private static String cachedBiome = "";

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
