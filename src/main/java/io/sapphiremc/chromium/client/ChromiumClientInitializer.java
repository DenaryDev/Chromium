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
import io.sapphiremc.chromium.ChromiumMod;
import io.sapphiremc.chromium.client.gui.ChromiumTitleScreen;
import io.sapphiremc.chromium.client.gui.OptionsScreenBuilder;
import io.sapphiremc.chromium.client.network.Packet;
import io.sapphiremc.chromium.config.ChromiumConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

public class ChromiumClientInitializer implements ClientModInitializer {

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
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof TitleScreen && ChromiumMod.getConfig().getTitleScreenProvider().equals(ChromiumConfig.TitleScreenProvider.CHROMIUM)) {
                client.setScreen(new ChromiumTitleScreen());
            }
        });
    }
}
