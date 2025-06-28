package com.edwip.Addons;

import com.edwip.Main;
import com.edwip.Menu.ModConfig;
import com.edwip.Utils.DiscordMessages;
import com.terraformersmc.modmenu.util.mod.Mod;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

import java.time.Instant;
import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DiscordChatLog {
    private static final BlockingQueue<Map.Entry<String, Integer>> messageQueue = new LinkedBlockingQueue<>();
    private static String lastServer = "";

    public static void doDiscordChatLog() {
        new Thread(DiscordChatLog::processQueue).start();
        if (!ModConfig.discordOpenGameMessage.isEmpty()) {
            sendMessage(ModConfig.discordOpenGameMessage.replace("<PLAYER>",ModConfig.discordUserName));
        }
        ClientLifecycleEvents.CLIENT_STOPPING.register(minecraftClient -> {
            // Send message to Discord when game is closing
            if (!ModConfig.discordCloseGameMessage.isEmpty()) {
                sendMessage(ModConfig.discordCloseGameMessage.replace("<PLAYER>",ModConfig.discordUserName));
            }
        });
        ClientPlayConnectionEvents.JOIN.register((clientPlayNetworkHandler, packetSender, minecraftClient) -> {
            String serverName = minecraftClient.getCurrentServerEntry() != null
                    ? minecraftClient.getCurrentServerEntry().address
                    : "Singleplayer";
            lastServer = serverName;
            if (!ModConfig.discordJoinServerMessage.isEmpty()) {
                sendMessage(ModConfig.discordJoinServerMessage.replace("<SERVER>",serverName).replace("<PLAYER>",ModConfig.discordUserName));
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, sender) -> {
            if (!ModConfig.discordLeaveServerMessage.isEmpty()) {
                sendMessage(ModConfig.discordLeaveServerMessage.replace("<SERVER>",lastServer).replace("<PLAYER>",ModConfig.discordUserName));
            }
        });

        ClientReceiveMessageEvents.CHAT.register((text, signedMessage, gameProfile, parameters, instant) -> {
            sendMessage(text.getString());
        });
        ClientReceiveMessageEvents.GAME.register((text, bob) -> {
            sendMessage(text.getString());
        });
    };


    private static void sendMessage(String content) {
        if (ModConfig.enableDiscordChatLog) messageQueue.offer(new AbstractMap.SimpleEntry<>(content, Math.toIntExact(Instant.now().getEpochSecond())));
    }
    private static void processQueue() {
        while (true) {
            try {
                Map.Entry<String, Integer> entry = messageQueue.take();

                DiscordMessages.sendDiscordMessage(entry.getKey(), entry.getValue());
                Thread.sleep(250); // ~4 msgs/sec (Discord allows 5/sec)
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }


}
