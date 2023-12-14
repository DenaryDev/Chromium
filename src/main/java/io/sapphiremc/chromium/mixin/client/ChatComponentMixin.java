/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import io.sapphiremc.chromium.ChromiumMod;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {

    @Shadow @Final private Minecraft minecraft;

    @Shadow protected abstract int getLineHeight();

    @Shadow private int chatScrollbarPos;
    @Shadow @Final private List<GuiMessage.Line> trimmedMessages;
    @Unique private final ArrayList<Long> messageTimestamps = new ArrayList<>();

    @Unique private final float fadeOffsetYScale = 0.8f; // scale * lineHeight
    @Unique private final float fadeTime = 130;

    @Unique private int chatLineIndex;
    @Unique private int chatDisplacementY = 0;


    // Change message history size
    @ModifyConstant(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V",
            constant = @Constant(intValue = 100)
    )
    private int chromium$getMaxMessages(int max) {
        return ChromiumMod.getConfig().messagesHistorySize;
    }

    // Timestamp prefix
    @ModifyArg(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/ComponentRenderUtils;wrapComponents(Lnet/minecraft/network/chat/FormattedText;ILnet/minecraft/client/gui/Font;)Ljava/util/List;"
            ),
            index = 1
    )
    private int chromium$getLineLength(int width) {
        return ChromiumMod.getConfig().showTimestamp ? width - this.minecraft.font.width("[HH:mm:ss] ") : width;
    }

    @ModifyArg(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/GuiMessage$Line;<init>(ILnet/minecraft/util/FormattedCharSequence;Lnet/minecraft/client/GuiMessageTag;Z)V"
            ),
            index = 1
    )
    private FormattedCharSequence chromium$addMessageTimePrefix(FormattedCharSequence message) {
        if (ChromiumMod.getConfig().showTimestamp) {
            final var hoverText = Component.translatable(ChatFormatting.YELLOW + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss ").format(new Date()) + TimeZone.getDefault().getID());
            final var timeText = Component.translatable(ChatFormatting.GRAY + new SimpleDateFormat("[HH:mm:ss] ").format(new Date()) + ChatFormatting.RESET).withStyle(
                    (style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText))));
            message = FormattedCharSequence.composite(timeText.getVisualOrderText(), message);
        }
        return message;
    }

    // Chat messages animations
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/GuiMessage$Line;addedTime()I"))
    private void chromium$getChatLineIndex(CallbackInfo ci, @Local(ordinal = 13) int chatLineIndex) {
        if (!ChromiumMod.getConfig().messageAnimations) return;
        this.chatLineIndex = chatLineIndex;
    }

    @Unique
    private void calculateYOffset() {
        // Calculate current required offset to achieve slide in from bottom effect
        try {
            int lineHeight = this.getLineHeight();
            float maxDisplacement = (float) lineHeight * fadeOffsetYScale;
            long timestamp = messageTimestamps.get(chatLineIndex);
            long timeAlive = System.currentTimeMillis() - timestamp;
            if (chatLineIndex == 0 && timeAlive < fadeTime && this.chatScrollbarPos == 0) {
                chatDisplacementY = (int) (maxDisplacement - ((timeAlive / fadeTime) * maxDisplacement));
            }
        } catch (Exception ignored) {
        }
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V", ordinal = 1), index = 1)
    private float chromium$applyYOffset(float y) {
        if (!ChromiumMod.getConfig().messageAnimations) return y;
        calculateYOffset();

        // Raised mod compatibility
        if (FabricLoader.getInstance().getObjectShare().get("raised:hud") instanceof Integer distance) {
            y -= distance;
        } else if (FabricLoader.getInstance().getObjectShare().get("raised:distance") instanceof Integer distance) {
            y -= distance;
        }

        return y + chatDisplacementY;
    }

    @ModifyVariable(method = "render", at = @At("STORE"), ordinal = 2)
    private double chromium$modifyOpacity(double originalOpacity) {
        if (!ChromiumMod.getConfig().messageAnimations) return originalOpacity;
        double opacity = originalOpacity;

        try {
            long timestamp = messageTimestamps.get(chatLineIndex);
            long timeAlive = System.currentTimeMillis() - timestamp;
            if (timeAlive < fadeTime && this.chatScrollbarPos == 0) {
                opacity = opacity * (0.5 + Mth.clamp(timeAlive / fadeTime, 0, 1) / 2);
            }
        } catch (Exception ignored) {
        }
        return opacity;
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "render", at = @At("STORE"))
    private GuiMessageTag chromium$removeMessageTag(GuiMessageTag originalMessageTag) {
        if (!ChromiumMod.getConfig().messageAnimations) return originalMessageTag;
        return null; // Don't allow the chat indicator bar to be rendered
    }

    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V",
            at = @At("TAIL")
    )
    private void chromium$addMessage(Component message, MessageSignature signature, GuiMessageTag tag, CallbackInfo ci) {
        if (!ChromiumMod.getConfig().messageAnimations) return;
        messageTimestamps.add(0, System.currentTimeMillis());
        while (this.messageTimestamps.size() > this.trimmedMessages.size()) {
            this.messageTimestamps.remove(this.messageTimestamps.size() - 1);
        }
    }
}
