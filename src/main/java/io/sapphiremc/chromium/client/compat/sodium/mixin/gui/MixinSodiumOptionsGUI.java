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
package io.sapphiremc.chromium.client.compat.sodium.mixin.gui;

import me.jellysquid.mods.sodium.client.gui.SodiumOptionsGUI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SodiumOptionsGUI.class)
public class MixinSodiumOptionsGUI {

    @ModifyArg(method = "rebuildGUIOptions",
            at = @At(value = "INVOKE",
                    target = "Lme/jellysquid/mods/sodium/client/util/Dim2i;<init>(IIII)V"),
            index = 2,
            remap = false
    )
    public int sapphireclient$boxWidthOne(int value) {
        return 242;
    }

    @ModifyVariable(method = "renderOptionTooltip",
            ordinal = 2,
            at = @At(value = "STORE"),
            require = 1,
            remap = false
    )
    public int sapphireclient$boxWidthTwo(int value) {
        return 242;
    }
}
