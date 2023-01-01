/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.client.dummy;

import com.mojang.datafixers.util.Either;
import io.sapphiremc.chromium.ChromiumMod;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DummyClientWorld extends ClientWorld {

    private static DummyClientWorld instance;

    private static final DimensionType DUMMY = new DimensionType(OptionalLong.empty(), true, false, false, false, 1.0, false, false, 0, 256, 256, BlockTags.INFINIBURN_OVERWORLD, new Identifier(ChromiumMod.MOD_ID, "dummy_type"), 1.0f, new DimensionType.MonsterSettings(false, false, ConstantIntProvider.create(0), 0));
    private static final RegistryKey<DimensionType> DUMMY_TYPE_KEY = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, new Identifier(ChromiumMod.MOD_ID, "dummy_type"));
    private static final RegistryKey<World> WORLD_KEY = RegistryKey.of(RegistryKeys.WORLD, new Identifier(ChromiumMod.MOD_ID, "dummy"));

    public static DummyClientWorld getInstance() {
        if (instance == null) instance = new DummyClientWorld();
        return instance;
    }

    private DummyClientWorld() {
        super(DummyClientPlayNetworkHandler.getInstance(), new Properties(Difficulty.PEACEFUL, false, true), WORLD_KEY, new DummyDirect<>(DUMMY_TYPE_KEY, DUMMY), 0, 0, () -> null, null, false, 0L);
    }

    private record DummyDirect<T>(RegistryKey<T> key, T value) implements RegistryEntry<T> {
        @Override
        public boolean hasKeyAndValue() {
            return key != null && value != null;
        }

        @Override
        public boolean matchesId(Identifier id) {
            return key.getValue().equals(id);
        }

        @Override
        public boolean matchesKey(RegistryKey<T> key) {
            return key.equals(this.key);
        }

        @Override
        public boolean matches(Predicate<RegistryKey<T>> predicate) {
            return predicate.test(key);
        }

        @Override
        public boolean isIn(TagKey<T> tag) {
            return false;
        }

        @Override
        public Either<RegistryKey<T>, T> getKeyOrValue() {
            return Either.right(this.value);
        }

        @Override
        public Optional<RegistryKey<T>> getKey() {
            return Optional.of(key);
        }

        @Override
        public Type getType() {
            return RegistryEntry.Type.DIRECT;
        }

        @Override
        public boolean ownerEquals(RegistryEntryOwner<T> owner) {
            return false;
        }

        @Override
        public String toString() {
            return "DummyDirect{" + this.value + "}";
        }

        @Override
        public Stream<TagKey<T>> streamTags() {
            return Stream.of();
        }
    }
}
