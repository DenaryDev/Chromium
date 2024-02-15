/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.chromium.config;

public final class ChromiumConfig {
    public boolean showFps = false;
    public boolean showTime = false;
    public boolean showCoords = true;
    public boolean showLight = false;
    public boolean showBiome = false;

    public boolean showTimestamp = false;
    public int messagesHistorySize = 100;
    public boolean messageAnimations = false;

    public boolean showPingAmount = false;
    public boolean replacePingBars = true;
    public boolean pingAmountAutoColor = false;
    public String pingAmountColor = "#A0A0A0";
    public String pingAmountFormat = "<num>ms";

    public int hopperTransfer = 8;
    public int hopperAmount = 1;

    public int bannerRenderDistance = 64;
    public int chestRenderDistance = 64;
    public int shulkerBoxRenderDistance = 64;
    public int signRenderDistance = 64;
    public int skullRenderDistance = 64;
}
