package com.edwip.Addons;

import com.edwip.Main;
import com.edwip.Menu.ModConfig;
import com.edwip.Utils.CommandSuggestionProviders;
import com.edwip.Utils.SendMessages;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.desktop.UserSessionEvent;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.concurrent.CompletableFuture;
import java.util.List;

import static com.edwip.Utils.ToTitleCase.toTitleCase;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class KickCommands {
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final List<String> REASON_SUGGESTIONS = List.of(
            "Griefing",
            "Spamming",
            "Spamming blocks",
            "Racism",
            "Inappropriate builds",
            "Inappropriate skin",
            "Inappropriate behavior",
            "Homophobia",
            "Transphobia",
            "Hate speech",
            "Links",
            "QR builds",
            "Harassment"

    );
    public static void doKickCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            // Set up "Kick Commands" mod, the command itself is still there, but it won't run anything if the setting is turned to OFF
            dispatcher.register(
                    literal("mpkick")
                            .then(
                                    argument("playerName", StringArgumentType.string())
                                            .suggests(CommandSuggestionProviders.playerNameSuggester)
                                            .executes(context -> {
                                                String playerName = StringArgumentType.getString(context, "playerName");
                                                return kickCommand(playerName, "");
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
                                                                return kickCommand(playerName, reason);
                                                            })
                                            )
                            )
            );
            dispatcher.register(
                    literal("mprkick")
                            .then(
                                    argument("playerName", StringArgumentType.string())
                                            .suggests(CommandSuggestionProviders.playerNameSuggester)
                                            .executes(context -> {
                                                String playerName = StringArgumentType.getString(context, "playerName");
                                                return robloxKickCommand(playerName, "");
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
                                                                return robloxKickCommand(playerName, reason);
                                                            })
                                            )
                            )
            );
        });
    }

    private static int kickCommand(String playerName, String reason) {
        assert MinecraftClient.getInstance().player != null;
        if (ModConfig.enableKick && !ModConfig.disableAll) {
            String oldReason = reason;
            scheduler.schedule(() -> {
                if (!ModConfig.kickSecondCommand.isEmpty()) {
                    String newReason = oldReason.isEmpty() ? "No reason specified" : oldReason;
                    newReason = switch (ModConfig.kickSecondLetters) {
                        case "Lower All" -> newReason.toLowerCase();
                        case "Upper All" -> newReason.toUpperCase();
                        case "First Letter" -> {
                            newReason = newReason.toLowerCase();
                            newReason = Character.toUpperCase(newReason.charAt(0)) + newReason.substring(1);
                            yield newReason;
                        }
                        case "First Every Letter" -> toTitleCase(newReason);
                        default -> newReason;
                    };
                    String finalCommand = ModConfig.kickSecondCommand.replace("<PLAYER>",playerName).replace("<REASON>",newReason);
                    SendMessages.sendMessage(finalCommand);
                }
            }, ModConfig.kickSecondTime, TimeUnit.MILLISECONDS);
            if (!reason.isEmpty()) {
                reason = switch (ModConfig.kickReasonLetters) {
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
                reason = " " + ModConfig.kickPrefix.replace("<REASON>", reason);
            }
            SendMessages.sendMessage("/kick " + playerName + reason);
        } else {
            MinecraftClient.getInstance().player.sendMessage(Text.literal("Kick Commands mod is Disabled!").formatted(Formatting.RED), false);
        }
        return 1;
    }

    private static int robloxKickCommand(String playerName, String reason) {
        assert MinecraftClient.getInstance().player != null;
        if (ModConfig.enableKick && !ModConfig.disableAll) {
             if (!reason.isEmpty()) {
                reason = switch (ModConfig.kickReasonLetters.getPrefix()) {
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
                reason = " " + ModConfig.kickPrefix.replace("<REASON>", reason);
            }
            SendMessages.sendMessage("/rkick " + playerName + reason);
        } else {
            MinecraftClient.getInstance().player.sendMessage(Text.literal("Kick Commands mod is Disabled!").formatted(Formatting.RED), false);
        }
        return 1;
    }
}
