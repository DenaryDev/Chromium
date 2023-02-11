/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.mixin.client;

import io.sapphiremc.chromium.ChromiumMod;
import io.sapphiremc.chromium.config.ChromiumConfig;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BlockEntityRenderer.class)
public interface MixinBlockEntityRenderer<T extends BlockEntity> {

    /**
     * @author DenaryDev
     * @reason per-block-entity render distance settings
     */
    @Overwrite
    default boolean isInRenderDistance(T blockEntity, Vec3d pos) {
        return blockEntity instanceof BeaconBlockEntity ?
        Vec3d.ofCenter(blockEntity.getPos()).multiply(1.0, 0.0, 1.0).isInRange(pos.multiply(1.0, 0.0, 1.0), this.getRenderDistance(blockEntity)) :
        Vec3d.ofCenter(blockEntity.getPos()).isInRange(pos, this.getRenderDistance(blockEntity));
    }

    default double getRenderDistance(T blockEntity) {
        final var config = ChromiumMod.getConfig();

        if (blockEntity instanceof BannerBlockEntity) {
            return config.getBannerRenderDistance();
        } else if (blockEntity instanceof ChestBlockEntity
                || blockEntity instanceof EnderChestBlockEntity) {
            return config.getChestRenderDistance();
        } else if (blockEntity instanceof ShulkerBoxBlockEntity) {
            return config.getShulkerBoxRenderDistance();
        } else if (blockEntity instanceof SignBlockEntity) {
            return config.getSignRenderDistance();
        } else if (blockEntity instanceof SkullBlockEntity) {
            return config.getSkullRenderDistance();
        } else {
            return 64;
        }
    }
}
