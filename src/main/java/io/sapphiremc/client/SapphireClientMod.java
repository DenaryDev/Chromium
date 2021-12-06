package io.sapphiremc.client;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.sapphiremc.client.config.SapphireClientConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Option;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class SapphireClientMod implements ClientModInitializer {

	public static final String MOD_ID = "sapphireclient";
	public static final Logger LOGGER = LogManager.getLogger("sapphireclient");
	public static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();

	public static final boolean RUNNING_IN_IDE = false;

	//private static final Random RANDOM = new Random();
	//public static LivingEntity livingEntity = null;

	@Override
	public void onInitializeClient() {
		SapphireClientConfigManager.initializeConfig();
		/*
		ClientTickEvents.END_CLIENT_TICK.register((client -> {
			if (livingEntity == null) {
				List<EntityType<?>> collect = Registry.ENTITY_TYPE.stream()
						.filter((e) -> e.getSpawnGroup() != SpawnGroup.MISC
						&& !e.equals(EntityType.WITHER)).collect(Collectors.toList());
				Entity entity = collect.get(RANDOM.nextInt(collect.size())).create(DummyClientWorld.getInstance());
				if (entity instanceof LivingEntity) livingEntity = (LivingEntity) entity;
 			}
		}));
		*/

		if (RUNNING_IN_IDE) {
			LOGGER.warn("You are running this in IDE!");
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					if (MinecraftClient.getInstance() != null) {
						if (MinecraftClient.getInstance().getWindow() != null) {
							MinecraftClient.getInstance().getWindow().setWindowedSize(1000, 700);
							cancel();
						}
					}
				}
			}, 200L, 200L);
		}
	}

	public static String getFpsString() {
		String fpsField = RUNNING_IN_IDE ? "currentFps" : "field_1738";
		MinecraftClient client = MinecraftClient.getInstance();
		int currentFps;
		try {
			Field field = MinecraftClient.class.getDeclaredField(fpsField);
			field.setAccessible(true);
			currentFps = field.getInt(MinecraftClient.class);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			LOGGER.error("Field " + fpsField + " not found!");
			currentFps = -1;
		}

		String maxFPS = (double) client.options.maxFps == Option.FRAMERATE_LIMIT.getMax() ? "\u221E" : String.valueOf(client.options.maxFps);
		String vsync = String.valueOf(client.options.enableVsync);
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
}
