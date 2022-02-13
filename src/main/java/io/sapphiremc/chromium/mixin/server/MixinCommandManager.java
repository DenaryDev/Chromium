/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.mixin.server;

import net.minecraft.server.command.CommandManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CommandManager.class)
public class MixinCommandManager {
}
