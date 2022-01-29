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
package io.sapphiremc.client.compat.sodium.mixin.gui;

import io.sapphiremc.client.SapphireClientMod;
import io.sapphiremc.client.config.Config;
import me.jellysquid.mods.sodium.client.gui.SodiumGameOptionPages;
import me.jellysquid.mods.sodium.client.gui.options.control.ControlValueFormatter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SodiumGameOptionPages.class)
public class MixinSodiumGameOptionPages {

    @ModifyArg(method = "lambda$general$9",
            at = @At(value = "INVOKE",
                    target = "Lme/jellysquid/mods/sodium/client/gui/options/control/SliderControl;<init>(Lme/jellysquid/mods/sodium/client/gui/options/Option;IIILme/jellysquid/mods/sodium/client/gui/options/control/ControlValueFormatter;)V"
            ),
            index = 2,
            remap = false
    )
    private static int sapphireclient$maxGuiScale(int value) {
        if (SapphireClientMod.getInstance().getConfig().getTitleScreenProvider().equals(Config.TitleScreenProvider.SAPPHIRECLIENT)) {
            return 4;
        } else {
            return MinecraftClient.getInstance().getWindow().calculateScaleFactor(0, MinecraftClient.getInstance().forcesUnicodeFont());
        }
    }

    @ModifyArg(method = "lambda$performance$52",
            at = @At(value = "INVOKE",
                    target = "Lme/jellysquid/mods/sodium/client/gui/options/control/SliderControl;<init>(Lme/jellysquid/mods/sodium/client/gui/options/Option;IIILme/jellysquid/mods/sodium/client/gui/options/control/ControlValueFormatter;)V"
            ),
            index = 4,
            remap = false
    )
    private static @NotNull ControlValueFormatter sapphireclient$quantityOrDisabled(ControlValueFormatter formatter) {
        return ControlValueFormatter.quantityOrDisabled(new TranslatableText("sodium.options.chunk_update_threads.threads").getString(), new TranslatableText("sodium.options.chunk_update_threads.default").getString());
    }
}
