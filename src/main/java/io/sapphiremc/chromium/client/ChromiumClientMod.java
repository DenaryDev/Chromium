/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.client;

import com.google.common.io.ByteArrayDataOutput;
import com.mojang.blaze3d.platform.InputConstants;
import io.sapphiremc.chromium.client.dummy.DummyClientWorld;
import io.sapphiremc.chromium.client.gui.OptionsScreenBuilder;
import io.sapphiremc.chromium.client.network.Packet;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.Random;

public class ChromiumClientMod implements ClientModInitializer {

    private KeyMapping configKey;

    private final int protocolId = 0;
    private final ResourceLocation hello = new ResourceLocation("chromium", "client");

    @Override
    public void onInitializeClient() {
        if (Boolean.getBoolean("chromium.killmclauncher") && Util.getPlatform().equals(Util.OS.WINDOWS)) {
            try {
                Runtime.getRuntime().exec("taskkill /F /IM Minecraft.exe");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        configKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.chromium.config",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_N,
                "key.chromium.category"
        ));

        ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) -> {
            if (!client.isSingleplayer()) {
                ByteArrayDataOutput out = Packet.out();
                out.writeInt(protocolId);
                Packet.send(hello, out);
            }
        }));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (configKey.isDown()) {
                client.setScreen(OptionsScreenBuilder.build());
            }
        });
        ScreenEvents.BEFORE_INIT.register(((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof TitleScreen) updateRandomEntity();
        }));
    }

    private static final Random RANDOM = new Random();
    @Getter
    private static LivingEntity randomEntity;

    public static void updateRandomEntity() {
        final var entities = BuiltInRegistries.ENTITY_TYPE.stream()
                .filter(e -> e.getCategory() != MobCategory.MISC)
                .filter(e -> e != EntityType.ENDER_DRAGON && e != EntityType.WITHER)
                .toList();
        final var entity = entities.get(RANDOM.nextInt(entities.size())).create(DummyClientWorld.getInstance());
        if (entity instanceof LivingEntity livingEntity) randomEntity = livingEntity;
    }
}
