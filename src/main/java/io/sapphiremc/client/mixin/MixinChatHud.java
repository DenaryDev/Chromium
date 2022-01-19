/*
 * Copyright (c) 2022 DenaryDev
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

import io.sapphiremc.client.SapphireClientMod;
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
        if (SapphireClientMod.getInstance().getConfig().isShowMessagesTime()) {
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
