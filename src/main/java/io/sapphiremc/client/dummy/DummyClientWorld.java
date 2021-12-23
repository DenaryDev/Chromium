/*
 * Copyright (c) 2021 DenaryDev
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
package io.sapphiremc.client.dummy;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.dimension.DimensionType;

import java.util.OptionalLong;

public class DummyClientWorld extends ClientWorld {

    private static DummyClientWorld instance;

    private static final DimensionType DUMMY_OVERWORLD = DimensionType.create(OptionalLong.empty(), true, false, false, false, 1.0, false, false, false, false, false, 0, 256, 256, BlockTags.INFINIBURN_OVERWORLD.getId(), DimensionType.OVERWORLD_ID, 1.0f);

    public static DummyClientWorld getInstance() {
        if (instance == null) instance = new DummyClientWorld();
        return instance;
    }

    private DummyClientWorld() {
        super(DummyClientPlayNetworkHandler.getInstance(), new Properties(Difficulty.EASY, false, true), null, DUMMY_OVERWORLD, 0, 0, () -> null, null, false, 0L);
    }
}
