/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.chromium.client.dummy;

import com.mojang.serialization.Lifecycle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
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
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;

public class DummyClientPacketListener extends ClientPlayNetworkHandler {

    private static DummyClientPacketListener instance;

    public static DummyClientPacketListener getInstance() {
        if (instance == null) instance = new DummyClientPacketListener();
        return instance;
    }

    private final DynamicRegistryManager dummyRegistryManager;
    private final DummyPlayerListEntry dummyPlayerListEntry;

    private DummyClientPacketListener() {
        super(MinecraftClient.getInstance(), new ClientConnection(NetworkSide.CLIENTBOUND), new ClientConnectionState(MinecraftClient.getInstance().getGameProfile(), null, null, FeatureSet.of(FeatureFlags.VANILLA), null, null, null));
        this.dummyRegistryManager = dummyRegistryManager();
        this.dummyPlayerListEntry = new DummyPlayerListEntry();
    }

    @NotNull
    @Override
    public DynamicRegistryManager.Immutable getRegistryManager() {
        return dummyRegistryManager.toImmutable();
    }

    @Nullable
    @Override
    public PlayerListEntry getPlayerListEntry(@NotNull UUID uuid) {
        return dummyPlayerListEntry;
    }

    private DynamicRegistryManager dummyRegistryManager() {
        final var registries = new ArrayList<Registry<?>>();
        final var damageTypeRegistry = new SimpleRegistry<>(RegistryKeys.DAMAGE_TYPE, Lifecycle.stable(), false);
        damageTypeRegistry.add(DamageTypes.IN_FIRE, new DamageType("inFire", 0.1F, DamageEffects.BURNING), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.LIGHTNING_BOLT, new DamageType("lightningBolt", 0.1F), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.ON_FIRE, new DamageType("onFire", 0, DamageEffects.BURNING), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.LAVA, new DamageType("lava", 0.1F, DamageEffects.BURNING), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.HOT_FLOOR, new DamageType("hotFloor", 0.1F, DamageEffects.BURNING), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.IN_WALL, new DamageType("inWall", 0), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.CRAMMING, new DamageType("cramming", 0), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.DROWN, new DamageType("drown", 0, DamageEffects.DROWNING), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.STARVE, new DamageType("starve", 0), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.CACTUS, new DamageType("cactus", 0.1F), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.FALL, new DamageType("fall", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0, DamageEffects.HURT, DeathMessageType.FALL_VARIANTS), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.FLY_INTO_WALL, new DamageType("flyIntoWall", 0), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.OUT_OF_WORLD, new DamageType("outOfWorld", 0), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.GENERIC, new DamageType("generic", 0), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.MAGIC, new DamageType("magic", 0), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.WITHER, new DamageType("wither", 0), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.DRAGON_BREATH, new DamageType("dragonBreath", 0), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.DRY_OUT, new DamageType("dryout", 0.1F), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.SWEET_BERRY_BUSH, new DamageType("sweetBerryBush", 0.1F, DamageEffects.POKING), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.FREEZE, new DamageType("freeze", 0, DamageEffects.FREEZING), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.STALAGMITE, new DamageType("stalagmite", 0), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.OUTSIDE_BORDER, new DamageType("outsideBorder", 0), Lifecycle.stable());
        damageTypeRegistry.add(DamageTypes.GENERIC_KILL, new DamageType("genericKill", 0), Lifecycle.stable());
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
        private final SkinTextures skinWithoutCape;

        public DummyPlayerListEntry() {
            super(MinecraftClient.getInstance().getGameProfile(), false);
            final var currentSkin = getSkinTextures();
            this.skinWithoutCape = new SkinTextures(currentSkin.texture(), currentSkin.textureUrl(), null, currentSkin.elytraTexture(), currentSkin.model(), currentSkin.secure()); //TODO: Fix cape texture rendering
        }

        @Override
        public @NotNull SkinTextures getSkinTextures() {
            return super.getSkinTextures();
        }
    }
}
