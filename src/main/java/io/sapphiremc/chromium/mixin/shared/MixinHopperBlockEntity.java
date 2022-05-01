/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.mixin.shared;

import io.sapphiremc.chromium.ChromiumMod;
import net.minecraft.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(HopperBlockEntity.class)
public class MixinHopperBlockEntity {

    @Shadow private int transferCooldown;

    @ModifyArg(method = "transfer(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/item/ItemStack;ILnet/minecraft/util/math/Direction;)Lnet/minecraft/item/ItemStack;",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/block/entity/HopperBlockEntity;setTransferCooldown(I)V"
            ),
            index = 0
    )
    private static int chromium$getTransferCooldown$transfer(int transferCooldown) {
        return ChromiumMod.getConfig().getHopperTransfer() - (8 - transferCooldown);
    }

    @ModifyArg(method = "insertAndExtract",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/block/entity/HopperBlockEntity;setTransferCooldown(I)V"
            ),
            index = 0
    )
    private static int chromium$getTransferCooldown$insertAndExtract(int transferCooldown) {
        return ChromiumMod.getConfig().getHopperTransfer() - (8 - transferCooldown);
    }

    @ModifyArg(method = "insert",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/inventory/Inventory;removeStack(II)Lnet/minecraft/item/ItemStack;"
            ),
            index = 1
    )
    private static int chromium$getAmount$insert(int amount) {
        return ChromiumMod.getConfig().getHopperAmount();
    }

    @ModifyArg(method = "extract(Lnet/minecraft/block/entity/Hopper;Lnet/minecraft/inventory/Inventory;ILnet/minecraft/util/math/Direction;)Z",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/inventory/Inventory;removeStack(II)Lnet/minecraft/item/ItemStack;"
            ),
            index = 1
    )
    private static int chromium$getAmount$extract(int amount) {
        return ChromiumMod.getConfig().getHopperAmount();
    }

    /**
     * @reason Customisable hoppers
     * @author DenaryDev
     */
    @Overwrite
    private boolean isDisabled() {
        return transferCooldown > ChromiumMod.getConfig().getHopperTransfer();
    }
}
