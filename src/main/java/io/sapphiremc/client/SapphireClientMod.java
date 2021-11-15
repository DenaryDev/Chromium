package io.sapphiremc.client;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.sapphiremc.client.config.SapphireClientConfigManager;
import io.sapphiremc.client.dummy.DummyClientWorld;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class SapphireClientMod implements ClientModInitializer {

	public static final String MOD_ID = "sapphireclient";
	public static final Logger LOGGER = LogManager.getLogger("sapphireclient");
	public static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();

	public static final boolean RUNNING_IN_IDE = false;

	public static LivingEntity livingEntity = null;
	private static final Random RANDOM = new Random();

	@Override
	public void onInitializeClient() {
		SapphireClientConfigManager.initializeConfig();
		ClientTickEvents.END_CLIENT_TICK.register((client -> {
			if (livingEntity == null) {
				List<EntityType<?>> collect = Registry.ENTITY_TYPE.stream()
						.filter((e) -> e.getSpawnGroup() != SpawnGroup.MISC
						&& !e.equals(EntityType.WITHER)).collect(Collectors.toList());
				Entity entity = collect.get(RANDOM.nextInt(collect.size())).create(DummyClientWorld.getInstance());
				if (entity instanceof LivingEntity) livingEntity = (LivingEntity) entity;
 			}
		}));

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
}
