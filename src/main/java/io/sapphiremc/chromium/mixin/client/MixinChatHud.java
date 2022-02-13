/*
 * Copyright (c) 2022 DenaryDev
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
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Mixin(ChatHud.class)
public abstract class MixinChatHud extends DrawableHelper {

    @Shadow @Final private List<ChatHudLine<Text>> messages;
    @Shadow @Final private List<ChatHudLine<OrderedText>> visibleMessages;
    @Shadow @Final private MinecraftClient client;

    @Shadow private boolean hasUnreadNewMessages;
    @Shadow private int scrolledLines;

    @Shadow protected abstract void removeMessage(int messageId);
    @Shadow public abstract void scroll(double amount);

    @Shadow protected abstract boolean isChatFocused();
    @Shadow public abstract double getChatScale();
    @Shadow public abstract int getWidth();

    /**
     * @author DenaryDev
     * @reason Add messages time
     */
    @Overwrite
    private void addMessage(Text message, int messageId, int timestamp, boolean refresh) {
        TranslatableText prefixedMessage;
        if (ChromiumMod.getConfig().isShowMessagesTime()) {
            Text hoverText = new TranslatableText(Formatting.YELLOW + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss ").format(new Date()) + TimeZone.getDefault().getID());
            Text timeText = new TranslatableText(Formatting.GRAY + new SimpleDateFormat("[HH:mm:ss] ").format(new Date()) + Formatting.RESET).styled(
                    (style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText))));
            prefixedMessage = new TranslatableText("%s%s", timeText, message);
        } else {
            prefixedMessage = new TranslatableText("%s", message);
        }

        if (messageId != 0) {
            this.removeMessage(messageId);
        }
        int i = MathHelper.floor((double)this.getWidth() / this.getChatScale());
        List<OrderedText> list = ChatMessages.breakRenderedChatMessageLines(prefixedMessage, i, this.client.textRenderer);
        boolean bl = this.isChatFocused();
        for (OrderedText orderedText : list) {
            if (bl && this.scrolledLines > 0) {
                this.hasUnreadNewMessages = true;
                this.scroll(1.0);
            }
            this.visibleMessages.add(0, new ChatHudLine<>(timestamp, orderedText, messageId));
        }
        while (this.visibleMessages.size() > 200) {
            this.visibleMessages.remove(this.visibleMessages.size() - 1);
        }
        if (!refresh) {
            this.messages.add(0, new ChatHudLine<>(timestamp, prefixedMessage, messageId));
            while (this.messages.size() > 200) {
                this.messages.remove(this.messages.size() - 1);
            }
        }
    }
}
