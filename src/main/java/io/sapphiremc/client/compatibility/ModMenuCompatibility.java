package io.sapphiremc.client.compatibility;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.sapphiremc.client.gui.SapphireClientOptionsScreen;

public class ModMenuCompatibility implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return SapphireClientOptionsScreen::new;
    }
}
