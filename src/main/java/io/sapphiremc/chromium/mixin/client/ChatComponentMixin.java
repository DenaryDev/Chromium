/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.mixin.client;

import io.sapphiremc.chromium.ChromiumMod;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {

    @Shadow @Final private Minecraft minecraft;

    @ModifyConstant(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V",
            constant = @Constant(intValue = 100)
    )
    private int chromium$getMaxMessages(int max) {
        return ChromiumMod.getConfig().getMaxMessages();
    }

    @ModifyArg(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/ComponentRenderUtils;wrapComponents(Lnet/minecraft/network/chat/FormattedText;ILnet/minecraft/client/gui/Font;)Ljava/util/List;"
            ),
            index = 1
    )
    private int chromium$getLineLenght(int width) {
        return ChromiumMod.getConfig().isShowMessagesTime() ? width - this.minecraft.font.width("[HH:mm:ss] ") : width;
    }

    @ModifyArg(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/GuiMessage$Line;<init>(ILnet/minecraft/util/FormattedCharSequence;Lnet/minecraft/client/GuiMessageTag;Z)V"
            ),
            index = 1
    )
    private FormattedCharSequence chromium$addMessageTimePrefix(FormattedCharSequence message) {
        if (ChromiumMod.getConfig().isShowMessagesTime()) {
            final var hoverText = Component.translatable(ChatFormatting.YELLOW + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss ").format(new Date()) + TimeZone.getDefault().getID());
            final var timeText = Component.translatable(ChatFormatting.GRAY + new SimpleDateFormat("[HH:mm:ss] ").format(new Date()) + ChatFormatting.RESET).withStyle(
                    (style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText))));
            message = FormattedCharSequence.composite(timeText.getVisualOrderText(), message);
        }
        return message;
    }
}
