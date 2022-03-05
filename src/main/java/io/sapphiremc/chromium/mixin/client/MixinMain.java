/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.mixin.client;

import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Main.class)
public class MixinMain {

    @ModifyArg(method = "main",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/WindowSettings;<init>(IILjava/util/OptionalInt;Ljava/util/OptionalInt;Z)V"
            ),
            index = 0
    )
    private static int chromium$getWidth(int width) {
        return Math.max(width, 1000);
    }

    @ModifyArg(method = "main",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/WindowSettings;<init>(IILjava/util/OptionalInt;Ljava/util/OptionalInt;Z)V"
            ),
            index = 1
    )
    private static int chromium$getHeight(int height) {
        return Math.max(height, 700);
    }
}
