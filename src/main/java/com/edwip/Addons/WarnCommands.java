package com.edwip.Addons;

import com.edwip.Menu.ModConfig;
import com.edwip.Utils.CommandSuggestionProviders;
import com.edwip.Utils.SendMessages;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.edwip.Utils.Prefixes.REASON_SUGGESTIONS;
import static com.edwip.Utils.ToTitleCase.toTitleCase;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class WarnCommands {
    public static void doWarnCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            // Set up "Kick Commands" mod, the command itself is still there, but it won't run anything if the setting is turned to OFF
            dispatcher.register(
                    literal("mpwarn")
                            .then(
                                    argument("playerName", StringArgumentType.string())
                                            .suggests(CommandSuggestionProviders.playerNameSuggester)
                                            .executes(context -> {
                                                String playerName = StringArgumentType.getString(context, "playerName");
                                                return warnCommand(playerName, "");
                                            })
                                            .then(
                                                    argument("reason", StringArgumentType.greedyString())
                                                            .suggests((CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) -> {
                                                                String remaining = builder.getRemaining().toLowerCase();

                                                                for (String suggestion : REASON_SUGGESTIONS) {
                                                                    if (suggestion.toLowerCase().startsWith(remaining)) {
                                                                        builder.suggest(suggestion);
                                                                    }
                                                                }

                                                                return builder.buildFuture();
                                                            })
                                                            .executes(context -> {
                                                                String playerName = StringArgumentType.getString(context, "playerName");
                                                                String reason = StringArgumentType.getString(context, "reason");
                                                                return warnCommand(playerName, reason);
                                                            })
                                            )
                            )
            );
        });
    }

    private static int warnCommand(String playerName, String reason) {
        assert MinecraftClient.getInstance().player != null;
        if (ModConfig.enableWarn && !ModConfig.disableAll) {
            if (!reason.isEmpty()) {
                reason = switch (ModConfig.warnReasonLetters.getPrefix()) {
                    case "No Change" -> reason;
                    case "Lower All" -> reason.toLowerCase();
                    case "Upper All" -> reason.toUpperCase();
                    case "First Letter" -> {
                        reason = reason.toLowerCase();
                        reason = Character.toUpperCase(reason.charAt(0)) + reason.substring(1);
                        yield reason;
                    }
                    case "First Every Letter" -> toTitleCase(reason);
                    default -> reason;
                };
                reason = " " + ModConfig.warnPrefix.replace("<REASON>", reason);
            }
            SendMessages.sendMessage("/warn " + playerName + reason);
        } else {
            MinecraftClient.getInstance().player.sendMessage(Text.literal("Warn Commands mod is Disabled!").formatted(Formatting.RED), false);
        }
        return 1;
    }
}
