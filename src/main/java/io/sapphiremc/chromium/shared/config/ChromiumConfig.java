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

    @Getter @Setter @Expose TitleScreenProvider titleScreenProvider = TitleScreenProvider.CHROMIUM;

    @Getter @Setter @Expose int hopperTransfer = 8;
    @Getter @Setter @Expose int hopperAmount = 1;

    public enum TitleScreenProvider {
        CHROMIUM,
        MINECRAFT
    }
}
