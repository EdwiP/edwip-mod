package com.edwip.Utils;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;

import java.util.stream.Collectors;

public class CommandSuggestionProviders {
    public static final SuggestionProvider<FabricClientCommandSource> playerNameSuggester = (context, builder) -> {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world != null) {
            var names = client.world.getPlayers().stream()
                    .map(player -> player.getName().getString())
                    .collect(Collectors.toList());

            return CommandSource.suggestMatching(names, builder);
        }
        return builder.buildFuture();
    };
}
