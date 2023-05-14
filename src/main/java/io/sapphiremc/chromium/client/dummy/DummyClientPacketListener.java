/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.client.dummy;

import com.mojang.serialization.Lifecycle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DeathMessageType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;

public class DummyClientPacketListener extends ClientPacketListener {

    private static DummyClientPacketListener instance;

    public static DummyClientPacketListener getInstance() {
        if (instance == null) instance = new DummyClientPacketListener();
        return instance;
    }

    private final RegistryAccess dummyRegistryManager;
    private final DummyPlayerInfo dummyPlayerInfo;

    private DummyClientPacketListener() {
        super(Minecraft.getInstance(), null, new Connection(PacketFlow.CLIENTBOUND), null, Minecraft.getInstance().getUser().getGameProfile(), null);
        this.dummyRegistryManager = dummyRegistryManager();
        this.dummyPlayerInfo = new DummyPlayerInfo();
    }

    @NotNull
    @Override
    public RegistryAccess registryAccess() {
        return dummyRegistryManager;
    }

    @Nullable
    @Override
    public PlayerInfo getPlayerInfo(UUID uuid) {
        return dummyPlayerInfo;
    }

    private RegistryAccess dummyRegistryManager() {
        final var registries = new ArrayList<Registry<?>>();
        final var damageTypeRegistry = new MappedRegistry<>(Registries.DAMAGE_TYPE, Lifecycle.stable(), false);
        damageTypeRegistry.register(DamageTypes.IN_FIRE, new DamageType("inFire", 0.1F, DamageEffects.BURNING), Lifecycle.stable());
        damageTypeRegistry.register(DamageTypes.LIGHTNING_BOLT, new DamageType("lightningBolt", 0.1F), Lifecycle.stable());
        damageTypeRegistry.register(DamageTypes.ON_FIRE, new DamageType("onFire", 0, DamageEffects.BURNING), Lifecycle.stable());
        damageTypeRegistry.register(DamageTypes.LAVA, new DamageType("lava", 0.1F, DamageEffects.BURNING), Lifecycle.stable());
        damageTypeRegistry.register(DamageTypes.HOT_FLOOR, new DamageType("hotFloor", 0.1F, DamageEffects.BURNING), Lifecycle.stable());
        damageTypeRegistry.register(DamageTypes.IN_WALL, new DamageType("inWall", 0), Lifecycle.stable());
        damageTypeRegistry.register(DamageTypes.CRAMMING, new DamageType("cramming", 0), Lifecycle.stable());
        damageTypeRegistry.register(DamageTypes.DROWN, new DamageType("drown", 0, DamageEffects.DROWNING), Lifecycle.stable());
        damageTypeRegistry.register(DamageTypes.STARVE, new DamageType("starve", 0), Lifecycle.stable());
        damageTypeRegistry.register(DamageTypes.CACTUS, new DamageType("cactus", 0.1F), Lifecycle.stable());
        damageTypeRegistry.register(DamageTypes.FALL, new DamageType("fall", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0, DamageEffects.HURT, DeathMessageType.FALL_VARIANTS), Lifecycle.stable());
        damageTypeRegistry.register(DamageTypes.FLY_INTO_WALL, new DamageType("flyIntoWall", 0), Lifecycle.stable());
        damageTypeRegistry.register(DamageTypes.FELL_OUT_OF_WORLD, new DamageType("outOfWorld", 0), Lifecycle.stable());
        damageTypeRegistry.register(DamageTypes.GENERIC, new DamageType("generic", 0), Lifecycle.stable());
        damageTypeRegistry.register(DamageTypes.MAGIC, new DamageType("magic", 0), Lifecycle.stable());
        damageTypeRegistry.register(DamageTypes.WITHER, new DamageType("wither", 0), Lifecycle.stable());
        damageTypeRegistry.register(DamageTypes.DRAGON_BREATH, new DamageType("dragonBreath", 0), Lifecycle.stable());
        damageTypeRegistry.register(DamageTypes.DRY_OUT, new DamageType("dryout", 0.1F), Lifecycle.stable());
        damageTypeRegistry.register(DamageTypes.SWEET_BERRY_BUSH, new DamageType("sweetBerryBush", 0.1F, DamageEffects.POKING), Lifecycle.stable());
        damageTypeRegistry.register(DamageTypes.FREEZE, new DamageType("freeze", 0, DamageEffects.FREEZING), Lifecycle.stable());
        damageTypeRegistry.register(DamageTypes.STALAGMITE, new DamageType("stalagmite", 0), Lifecycle.stable());
        damageTypeRegistry.register(DamageTypes.OUTSIDE_BORDER, new DamageType("outsideBorder", 0), Lifecycle.stable());
        damageTypeRegistry.register(DamageTypes.GENERIC_KILL, new DamageType("genericKill", 0), Lifecycle.stable());
        registries.add(damageTypeRegistry);

        final var biomeRegistry = new MappedRegistry<>(Registries.BIOME, Lifecycle.stable(), false);
        final var fakePlains = new Biome.BiomeBuilder()
                .temperature(0).downfall(0)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .fogColor(0)
                        .waterColor(0)
                        .waterFogColor(0)
                        .skyColor(0)
                        .build())
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(new BiomeGenerationSettings.PlainBuilder().build())
                .build();
        biomeRegistry.register(Biomes.PLAINS, fakePlains, Lifecycle.stable());
        registries.add(biomeRegistry);

        return new RegistryAccess.ImmutableRegistryAccess(registries);
    }

    public static class DummyPlayerInfo extends PlayerInfo {
        public DummyPlayerInfo() {
            super(Minecraft.getInstance().getUser().getGameProfile(), false);
        }

        @Nullable
        @Override
        public ResourceLocation getCapeLocation() {
            return null; //TODO: Fix cape rendering (Disabled due to incorrect display)
        }
    }
}
