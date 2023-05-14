/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.client.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.gui.ModsScreen;
import io.sapphiremc.chromium.client.gui.OptionsScreenBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class ModMenuCompat implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> OptionsScreenBuilder.build();
    }

    public static void openModsList(Minecraft client, Screen prev) {
        client.setScreen(new ModsScreen(prev));
    }
}
