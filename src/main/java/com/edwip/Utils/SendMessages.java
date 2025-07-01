package com.edwip.Utils;

import net.minecraft.client.MinecraftClient;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SendMessages {
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public static void scheduleTask(int delay, String message) {
        scheduler.schedule(() -> {
            // Execute the delayed action
            sendMessage(message);
        }, delay, TimeUnit.MILLISECONDS);
    }

    public static void sendMessage(String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && !message.isEmpty()) {

            if (message.startsWith("/")) {
                // It's a command
                client.player.networkHandler.sendChatCommand(message.replaceFirst("^/+", ""));
            } else {
                // It's a regular chat message
                client.player.networkHandler.sendChatMessage(message);
            }
        }
    }
}
