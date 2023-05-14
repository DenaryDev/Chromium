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
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DummyClientWorld extends ClientLevel {

    private static DummyClientWorld instance;

    private static final DimensionType DUMMY = new DimensionType(OptionalLong.empty(), true, false, false, false, 1.0, false, false, 0, 256, 256, BlockTags.INFINIBURN_OVERWORLD, new ResourceLocation(ChromiumMod.MOD_ID, "dummy_type"), 1, new DimensionType.MonsterSettings(false, false, ConstantInt.of(0), 0));
    private static final ResourceKey<DimensionType> DUMMY_TYPE_KEY = ResourceKey.create(Registries.DIMENSION_TYPE, new ResourceLocation(ChromiumMod.MOD_ID, "dummy_type"));
    private static final ResourceKey<Level> WORLD_KEY = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(ChromiumMod.MOD_ID, "dummy"));

    public static DummyClientWorld getInstance() {
        if (instance == null) instance = new DummyClientWorld();
        return instance;
    }

    private DummyClientWorld() {
        super(DummyClientPacketListener.getInstance(), new ClientLevelData(Difficulty.PEACEFUL, false, true), WORLD_KEY, new DummyDirect<>(DUMMY_TYPE_KEY, DUMMY), 0, 0, () -> null, null, false, 0);
    }

    private record DummyDirect<T>(ResourceKey<T> key, T value) implements Holder<T> {
        @Override
        public boolean isBound() {
            return key != null && value != null;
        }

        @Override
        public boolean is(ResourceLocation id) {
            return key.location().equals(id);
        }

        @Override
        public boolean is(ResourceKey<T> key) {
            return key.equals(this.key);
        }

        @Override
        public boolean is(Predicate<ResourceKey<T>> predicate) {
            return predicate.test(key);
        }

        @Override
        public boolean is(TagKey<T> tag) {
            return false;
        }

        @NotNull
        @Override
        public Stream<TagKey<T>> tags() {
            return Stream.of();
        }

        @NotNull
        @Override
        public Either<ResourceKey<T>, T> unwrap() {
            return Either.right(this.value);
        }

        @NotNull
        @Override
        public Optional<ResourceKey<T>> unwrapKey() {
            return Optional.of(key);
        }

        @NotNull
        @Override
        public Kind kind() {
            return Kind.DIRECT;
        }

        @Override
        public boolean canSerializeIn(HolderOwner<T> owner) {
            return false;
        }

        @Override
        public String toString() {
            return "DummyDirect{" + this.value + "}";
        }
    }
}
