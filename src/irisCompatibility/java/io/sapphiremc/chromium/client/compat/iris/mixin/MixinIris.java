/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.client.compat.iris.mixin;

import net.coderbot.iris.Iris;
import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(Iris.class)
public class MixinIris {

    @Redirect(method = "onEarlyInitialize",
            at = @At(value = "INVOKE",
                    target = "Lnet/fabricmc/loader/api/FabricLoader;isModLoaded(Ljava/lang/String;)Z"
            ),
            remap = false
    )
    private boolean chromium$hasNEC(FabricLoader instance, String s) {
        return !Objects.equals(s, "notenoughcrashes") && instance.isModLoaded(s);
    }
}
