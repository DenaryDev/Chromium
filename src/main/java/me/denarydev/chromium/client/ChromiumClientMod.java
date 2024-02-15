/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.chromium.client;

import com.google.common.io.ByteArrayDataOutput;
import me.denarydev.chromium.client.dummy.DummyClientLevel;
import me.denarydev.chromium.client.gui.OptionsScreenBuilder;
import me.denarydev.chromium.client.network.Packet;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.Random;

public class ChromiumClientMod implements ClientModInitializer {

    private KeyBinding configKey;

    private final int protocolId = 0;
    private final Identifier hello = new Identifier("chromium", "client");

    @Override
    public void onInitializeClient() {
        if (Boolean.getBoolean("chromium.killmclauncher") && Util.getOperatingSystem().equals(Util.OperatingSystem.WINDOWS)) {
            try {
                Runtime.getRuntime().exec("taskkill /F /IM Minecraft.exe");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        configKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.chromium.config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_N,
                "key.chromium.category"
        ));

        ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) -> {
            if (!client.isConnectedToLocalServer()) {
                ByteArrayDataOutput out = Packet.out();
                out.writeInt(protocolId);
                Packet.send(hello, out);
            }
        }));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (configKey.isPressed()) {
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
        final var entities = Registries.ENTITY_TYPE.stream()
                .filter(e -> e.getSpawnGroup() != SpawnGroup.MISC)
                .filter(e -> e != EntityType.ENDER_DRAGON && e != EntityType.WITHER)
                .toList();
        final var entity = entities.get(RANDOM.nextInt(entities.size())).create(DummyClientLevel.getInstance());
        if (entity instanceof LivingEntity livingEntity) randomEntity = livingEntity;
    }
}
