/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.mixin.client;

import io.sapphiremc.chromium.ChromiumMod;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityRenderer.class)
public interface BlockEntityRendererMixin<T extends BlockEntity> {

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    default void shouldRender(T blockEntity, Vec3 pos, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(blockEntity instanceof BeaconBlockEntity ?
                Vec3.atCenterOf(blockEntity.getBlockPos()).multiply(1.0, 0.0, 1.0).closerThan(pos.multiply(1.0, 0.0, 1.0), this.getRenderDistance(blockEntity)) :
                Vec3.atCenterOf(blockEntity.getBlockPos()).closerThan(pos, this.getRenderDistance(blockEntity)));
    }

    @Unique
    default double getRenderDistance(T blockEntity) {
        final var config = ChromiumMod.getConfig();

        if (blockEntity instanceof BannerBlockEntity) {
            return config.bannerRenderDistance;
        } else if (blockEntity instanceof ChestBlockEntity
                || blockEntity instanceof EnderChestBlockEntity) {
            return config.chestRenderDistance;
        } else if (blockEntity instanceof ShulkerBoxBlockEntity) {
            return config.shulkerBoxRenderDistance;
        } else if (blockEntity instanceof SignBlockEntity) {
            return config.signRenderDistance;
        } else if (blockEntity instanceof SkullBlockEntity) {
            return config.skullRenderDistance;
        } else {
            return 64;
        }
    }
}
