/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.mixin.shared;

import io.sapphiremc.chromium.ChromiumMod;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin {

    @ModifyArg(method = "tryMoveItems",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;setCooldown(I)V"
            )
    )
    private static int chromium$hopperTransfer$tryMoveItems(int cooldown) {
        return ChromiumMod.getConfig().getHopperTransfer();
    }

    @ModifyArg(method = "ejectItems",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/Container;removeItem(II)Lnet/minecraft/world/item/ItemStack;"
            ),
            index = 1
    )
    private static int chromium$hopperAmount$ejectItems(int amount) {
        return ChromiumMod.getConfig().getHopperAmount();
    }

    @ModifyArg(method = "tryTakeInItemFromSlot",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/Container;removeItem(II)Lnet/minecraft/world/item/ItemStack;"
            ),
            index = 1
    )
    private static int chromium$hopperAmount$tryTakeInItemFromSlot(int amount) {
        return ChromiumMod.getConfig().getHopperAmount();
    }

    @ModifyArg(method = "tryMoveInItem",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;setCooldown(I)V"
            )
    )
    private static int chromium$hopperTransfer$tryMoveInItem(int cooldown) {
        return ChromiumMod.getConfig().getHopperTransfer() - (8 - cooldown);
    }
}
