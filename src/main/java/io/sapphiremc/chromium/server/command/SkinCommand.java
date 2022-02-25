/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.server.command;

import com.mojang.authlib.properties.Property;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.sapphiremc.chromium.ChromiumMod;
import io.sapphiremc.chromium.common.skins.SkinVariant;
import io.sapphiremc.chromium.common.skins.provider.MineSkinSkinsProvider;
import io.sapphiremc.chromium.common.skins.provider.MojangSkinsProvider;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;
import net.minecraft.text.TranslatableText;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SkinCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("skin")
                .then(literal("set")
                        .then(literal("mojang")
                                .then(argument("skin_name", StringArgumentType.word())
                                        .executes(context ->
                                                setSkin(Collections.singleton(context.getSource().getPlayer()), false,
                                                        () -> MojangSkinsProvider.getSkin(StringArgumentType.getString(context, "skin_name"))))
                                        .then(argument("targets", EntityArgumentType.players()).requires(source -> source.hasPermissionLevel(3))
                                                .executes(context ->
                                                        setSkin(EntityArgumentType.getPlayers(context, "targets"), true,
                                                                () -> MojangSkinsProvider.getSkin(StringArgumentType.getString(context, "skin_name")))))))
                        .then(literal("web")
                                .then(literal("classic")
                                        .then(argument("url", StringArgumentType.string())
                                                .executes(context ->
                                                        setSkin(Collections.singleton(context.getSource().getPlayer()), false,
                                                                () -> MineSkinSkinsProvider.getSkin(StringArgumentType.getString(context, "url"), SkinVariant.CLASSIC)))
                                                .then(argument("targets", EntityArgumentType.players()).requires(source -> source.hasPermissionLevel(3))
                                                        .executes(context ->
                                                                setSkin(EntityArgumentType.getPlayers(context, "targets"), true,
                                                                        () -> MineSkinSkinsProvider.getSkin(StringArgumentType.getString(context, "url"), SkinVariant.CLASSIC))))))
                                .then(literal("slim")
                                        .then(argument("url", StringArgumentType.string())
                                                .executes(context ->
                                                        setSkin(Collections.singleton(context.getSource().getPlayer()), false,
                                                                () -> MineSkinSkinsProvider.getSkin(StringArgumentType.getString(context, "url"), SkinVariant.SLIM)))
                                                .then(argument("targets", EntityArgumentType.players()).requires(source -> source.hasPermissionLevel(3))
                                                        .executes(context ->
                                                                setSkin(EntityArgumentType.getPlayers(context, "targets"), true,
                                                                        () -> MineSkinSkinsProvider.getSkin(StringArgumentType.getString(context, "url"), SkinVariant.SLIM))))))))
                .then(literal("clear")
                        .executes(context ->
                                clearSkin(Collections.singleton(context.getSource().getPlayer()), false,
                                        () -> null))
                        .then(argument("targets", EntityArgumentType.players()).executes(context ->
                                clearSkin(EntityArgumentType.getPlayers(context, "targets"), true,
                                        () -> null))))
        );
    }

    private static int setSkin(Collection<ServerPlayerEntity> targets, boolean setByOperator, Supplier<Property> skinSupplier) {
        new Thread(() -> {
            if (!setByOperator)
                targets.stream().findFirst().get().sendMessage(new TranslatableText("message.chromium.skin.downloading"), true);

            Property skin = skinSupplier.get();

            for (ServerPlayerEntity player : targets) {
                ChromiumMod.getSkinsManager().setSkin(player, skin, true);

                if (setByOperator)
                    player.sendMessage(new TranslatableText("message.chromium.skin.set.operator"), true);
                else
                    player.sendMessage(new TranslatableText("message.chromium.skin.set"), true);
            }
        }).start();

        return targets.size();
    }

    private static int clearSkin(Collection<ServerPlayerEntity> targets, boolean clearByOperator, Supplier<Property> skinSupplier) {
        new Thread(() -> {
            for (ServerPlayerEntity player : targets) {
                ChromiumMod.getSkinsManager().clearSkin(player);

                if (clearByOperator)
                    player.sendMessage(new LiteralText("message.chromium.skin.reset.operator"), true);
                else
                    player.sendMessage(new LiteralText("message.chromium.reset.set"), true);
            }
        }).start();

        return targets.size();
    }
}
