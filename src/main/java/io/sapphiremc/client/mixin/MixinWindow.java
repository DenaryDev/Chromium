/*
 * Copyright (c) 2021 DenaryDev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 */
package io.sapphiremc.client.mixin;

import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.util.MonitorTracker;
import net.minecraft.client.util.Window;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created 24.12.2021
 *
 * @author DenaryDev
 */
@Mixin(Window.class)
public abstract class MixinWindow {

    @Shadow @Final private long handle;

    @Shadow private int width;
    @Shadow private int height;

    @Shadow private int windowedWidth;

    @Shadow private int windowedHeight;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwMakeContextCurrent(J)V"))
    private void sapphireclient$setMinWinSize(WindowEventHandler eventHandler, MonitorTracker monitorTracker, WindowSettings settings, @Nullable String videoMode, String title, CallbackInfo callbackInfo) {
        if (this.width < 1000) this.width = 1000;
        if (this.height < 700) this.height = 700;
        if (this.windowedWidth < 1000) this.windowedWidth = 1000;
        if (this.windowedHeight < 700) this.windowedHeight = 700;

        GLFW.glfwSetWindowSizeLimits(handle, 1000, 700, GLFW.GLFW_DONT_CARE, GLFW.GLFW_DONT_CARE);
    }
}
