/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.client.network;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public abstract class Packet {
    public static void send(ResourceLocation channel, ByteArrayDataOutput out) {
        if (Minecraft.getInstance().getConnection() != null) {
            ClientPlayNetworking.send(channel, new FriendlyByteBuf(Unpooled.wrappedBuffer(out.toByteArray())));
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    public static ByteArrayDataOutput out() {
        return ByteStreams.newDataOutput();
    }

    @SuppressWarnings("UnstableApiUsage")
    public static ByteArrayDataInput in(byte[] bytes) {
        return ByteStreams.newDataInput(bytes);
    }
}
