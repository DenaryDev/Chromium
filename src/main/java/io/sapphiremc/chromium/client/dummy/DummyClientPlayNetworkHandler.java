/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.client.dummy;

import com.mojang.serialization.Lifecycle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.damage.DamageEffects;
import net.minecraft.entity.damage.DamageScaling;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.damage.DeathMessageType;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;

public class DummyClientPlayNetworkHandler extends ClientPlayNetworkHandler {

    private static DummyClientPlayNetworkHandler instance;

    public static DummyClientPlayNetworkHandler getInstance() {
        if (instance == null) instance = new DummyClientPlayNetworkHandler();
        return instance;
    }

    private final DynamicRegistryManager dummyRegistryManager;
    private final DummyPlayerListEntry dummyPlayerListEntry;

    private DummyClientPlayNetworkHandler() {
        super(MinecraftClient.getInstance(), null, new ClientConnection(NetworkSide.CLIENTBOUND), null, MinecraftClient.getInstance().getSession().getProfile(), null);
        this.dummyRegistryManager = dummyRegistryManager();
        this.dummyPlayerListEntry = new DummyPlayerListEntry();
    }

    @Override
    public DynamicRegistryManager getRegistryManager() {
        return dummyRegistryManager;
    }

    @Nullable
    @Override
    public PlayerListEntry getPlayerListEntry(UUID uuid) {
        return dummyPlayerListEntry;
    }

    private DynamicRegistryManager dummyRegistryManager() {
        final var registries = new ArrayList<Registry<?>>();
        final var damageTypeRegistry = new SimpleRegistry<>(RegistryKeys.DAMAGE_TYPE, Lifecycle.stable(), false);
        damageTypeRegistry.add(DamageTypes.IN_FIRE, new DamageType("inFire", 0.1f, DamageEffects.BURNING), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.LIGHTNING_BOLT, new DamageType("lightningBolt", 0.1f), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.ON_FIRE, new DamageType("onFire", 0.0f, DamageEffects.BURNING), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.LAVA, new DamageType("lava", 0.1f, DamageEffects.BURNING), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.HOT_FLOOR, new DamageType("hotFloor", 0.1f, DamageEffects.BURNING), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.IN_WALL, new DamageType("inWall", 0.0f), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.CRAMMING, new DamageType("cramming", 0.0f), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.DROWN, new DamageType("drown", 0.0f, DamageEffects.DROWNING), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.STARVE, new DamageType("starve", 0.0f), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.CACTUS, new DamageType("cactus", 0.1f), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.FALL, new DamageType("fall", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0f, DamageEffects.HURT, DeathMessageType.FALL_VARIANTS), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.FLY_INTO_WALL, new DamageType("flyIntoWall", 0.0f), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.OUT_OF_WORLD, new DamageType("outOfWorld", 0.0f), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.GENERIC, new DamageType("generic", 0.0f), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.MAGIC, new DamageType("magic", 0.0f), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.WITHER, new DamageType("wither", 0.0f), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.DRAGON_BREATH, new DamageType("dragonBreath", 0.0f), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.DRY_OUT, new DamageType("dryout", 0.1f), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.SWEET_BERRY_BUSH, new DamageType("sweetBerryBush", 0.1f, DamageEffects.POKING), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.FREEZE, new DamageType("freeze", 0.0f, DamageEffects.FREEZING), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.STALAGMITE, new DamageType("stalagmite", 0.0f), Lifecycle.stable());
        registries.add(damageTypeRegistry);

        final var biomeRegistry = new SimpleRegistry<>(RegistryKeys.BIOME, Lifecycle.stable(), false);
        final var fakePlains = new Biome.Builder()
                .temperature(0).downfall(0)
                .effects(new BiomeEffects.Builder()
                        .fogColor(0)
                        .waterColor(0)
                        .waterFogColor(0)
                        .skyColor(0)
                        .build())
                .spawnSettings(new SpawnSettings.Builder().build())
                .generationSettings(new GenerationSettings.Builder().build())
                .build();
        biomeRegistry.add(BiomeKeys.PLAINS, fakePlains, Lifecycle.stable());
        registries.add(biomeRegistry);

        return new DynamicRegistryManager.ImmutableImpl(registries);
    }

    public static class DummyPlayerListEntry extends PlayerListEntry {
        public DummyPlayerListEntry() {
            super(MinecraftClient.getInstance().getSession().getProfile(), false);
        }

        @Nullable
        @Override
        public Identifier getCapeTexture() {
            return null; //TODO: Fix cape rendering (Disabled due to incorrect display)
        }
    }
}
