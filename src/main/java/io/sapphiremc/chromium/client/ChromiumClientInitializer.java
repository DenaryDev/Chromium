/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.client;

import com.google.common.io.ByteArrayDataOutput;
import io.sapphiremc.chromium.ChromiumMod;
import io.sapphiremc.chromium.client.gui.ChromiumTitleScreen;
import io.sapphiremc.chromium.client.gui.OptionsScreenBuilder;
import io.sapphiremc.chromium.client.network.Packet;
import io.sapphiremc.chromium.common.config.ChromiumConfig;
import io.sapphiremc.chromium.common.util.MultiplayerConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ChromiumClientInitializer implements ClientModInitializer {

    private KeyBinding configKey;

    @Override
    public void onInitializeClient() {
        configKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.chromium.config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_N,
                "Chromium"
        ));

        ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) -> {
            if (!client.isInSingleplayer()) {
                ByteArrayDataOutput out = Packet.out();
                out.writeInt(MultiplayerConstants.PROTOCOL_ID);
                Packet.send(MultiplayerConstants.HELLO, out);
            }
        }));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (configKey.isPressed()) {
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
