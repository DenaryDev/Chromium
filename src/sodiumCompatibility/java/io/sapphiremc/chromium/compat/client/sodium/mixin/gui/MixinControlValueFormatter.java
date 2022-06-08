/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.compat.client.sodium.mixin.gui;

import me.jellysquid.mods.sodium.client.gui.options.control.ControlValueFormatter;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ControlValueFormatter.class)
public interface MixinControlValueFormatter {

    /**
     * @author DenaryDev
     * @reason Add default gamma string
     */
    @Overwrite(remap = false)
    static @NotNull ControlValueFormatter brightness() {
        return (v) -> {
            if (v == 0) {
                return Text.translatable("options.gamma.min").getString();
            } else if (v == 50) {
                return Text.translatable("options.gamma.default").getString();
            } else {
                return v == 100 ? Text.translatable("options.gamma.max").getString() : v + "%";
            }
        };
    }
}
