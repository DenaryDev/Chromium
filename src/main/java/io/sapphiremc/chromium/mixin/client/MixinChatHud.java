/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.mixin.client;

import io.sapphiremc.chromium.ChromiumMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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

@Mixin(ChatHud.class)
public abstract class MixinChatHud extends DrawableHelper {

    @Shadow @Final private MinecraftClient client;

    @ModifyConstant(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V",
            constant = @Constant(intValue = 100)
    )
    private int chromium$getMaxMessages(int max) {
        return ChromiumMod.getConfig().getMaxMessages();
    }

    @ModifyArg(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/util/ChatMessages;breakRenderedChatMessageLines(Lnet/minecraft/text/StringVisitable;ILnet/minecraft/client/font/TextRenderer;)Ljava/util/List;"
            ),
            index = 1
    )
    private int chromium$getLineLenght(int width) {
        return ChromiumMod.getConfig().isShowMessagesTime() ? width - this.client.textRenderer.getWidth("[HH:mm:ss] ") : width;
    }

    @ModifyArg(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/ChatHudLine$Visible;<init>(ILnet/minecraft/text/OrderedText;Lnet/minecraft/client/gui/hud/MessageIndicator;Z)V"
            ),
            index = 1
    )
    private OrderedText chromium$addMessageTimePrefix(OrderedText message) {
        if (ChromiumMod.getConfig().isShowMessagesTime()) {
            final var hoverText = Text.translatable(Formatting.YELLOW + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss ").format(new Date()) + TimeZone.getDefault().getID());
            final var timeText = Text.translatable(Formatting.GRAY + new SimpleDateFormat("[HH:mm:ss] ").format(new Date()) + Formatting.RESET).styled(
                    (style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText))));
            message = OrderedText.concat(timeText.asOrderedText(), message);
        }
        return message;
    }
}
