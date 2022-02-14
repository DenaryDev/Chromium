/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.client.compat.bobby.mixin;

import de.johni0702.minecraft.bobby.FakeChunkManager;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FakeChunkManager.class)
public class MixinFakeChunkManager {

    @Inject(method = "<init>",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private static void chromium$checkWorld(@NotNull ClientWorld world, ClientChunkManager manager, CallbackInfo ci) {
        if (world.getRegistryKey() == null) ci.cancel();
    }
}
