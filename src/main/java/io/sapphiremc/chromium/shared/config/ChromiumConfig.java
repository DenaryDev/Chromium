/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.shared.config;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

public class ChromiumConfig {

    @Getter @Setter @Expose boolean showFps = false;
    @Getter @Setter @Expose boolean showTime = false;
    @Getter @Setter @Expose boolean showCoords = true;
    @Getter @Setter @Expose boolean showLight = false;
    @Getter @Setter @Expose boolean showBiome = false;

    @Getter @Setter @Expose boolean showMessagesTime = false;
    @Getter @Setter @Expose int maxMessages = 100;

    @Getter @Setter @Expose TitleScreenProvider titleScreenProvider = TitleScreenProvider.CHROMIUM;

    @Getter @Setter @Expose int hopperTransfer = 8;
    @Getter @Setter @Expose int hopperAmount = 1;

    @Getter @Setter @Expose int bannerRenderDistance = 64;
    @Getter @Setter @Expose int chestRenderDistance = 64;
    @Getter @Setter @Expose int shulkerBoxRenderDistance = 64;
    @Getter @Setter @Expose int signRenderDistance = 64;
    @Getter @Setter @Expose int skullRenderDistance = 64;

    public enum TitleScreenProvider {
        CHROMIUM,
        MINECRAFT
    }
}
