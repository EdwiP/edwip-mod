package com.edwip.Addons;

import com.edwip.Main;
import com.edwip.Menu.ModConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatsWriter {
    private static String lastServer;

    private static void sendText(String text) {
        if (!ModConfig.enableChatsWriter || ModConfig.disableAll) return;
        text = text.replaceAll("§.", "");
        if (ModConfig.chatsWriterFileName.isEmpty()) return;
        appendLine("[" + getCurrentTimestamp() + "] " + text);
    }

    private static void appendLine(String line) {
        try (FileWriter writer = new FileWriter("chats.txt", true)) {
            writer.write(line + System.lineSeparator());
        } catch (IOException e) {
			Main.LOGGER.warn(e.toString());
        }
    }

    private static String getCurrentTimestamp() {
		if (ModConfig.chatsWriterDateFormat.isEmpty()) return "";
        try {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ModConfig.chatsWriterDateFormat);
            return now.format(formatter);
        } catch (Exception exception) {
            assert MinecraftClient.getInstance().player != null;
            MinecraftClient.getInstance().player.sendMessage(
                    Text.literal("Invalid format! " + ModConfig.chatsWriterDateFormat +
                            "\n " + exception).formatted(Formatting.RED),
                    false);
        }
        return "";
    }

    public static void doChatsWriter() {
        if (!ModConfig.chatsWriterOpenGameMessage.isEmpty()) {
            sendText(ModConfig.chatsWriterOpenGameMessage.replace("<PLAYER>", ModConfig.chatsWriterUserName));
        }
        ClientLifecycleEvents.CLIENT_STOPPING.register(minecraftClient -> {
            if (!ModConfig.chatsWriterCloseGameMessage.isEmpty()) {
                sendText(ModConfig.chatsWriterCloseGameMessage.replace("<PLAYER>", ModConfig.chatsWriterUserName));
            }
        });
        ClientPlayConnectionEvents.JOIN.register((clientPlayNetworkHandler, packetSender, minecraftClient) -> {
            if (!ModConfig.chatsWriterJoinServerMessage.isEmpty()) {
                String serverName = minecraftClient.getCurrentServerEntry() != null
                        ? minecraftClient.getCurrentServerEntry().address
                        : "Singleplayer";
                lastServer = serverName;
                sendText(ModConfig.chatsWriterJoinServerMessage.replace("<SERVER>", serverName).replace("<PLAYER>", ModConfig.chatsWriterUserName));
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, sender) -> {
            if (!ModConfig.chatsWriterLeaveServerMessage.isEmpty()) {
                sendText(ModConfig.chatsWriterLeaveServerMessage.replace("<SERVER>", lastServer).replace("<PLAYER>", ModConfig.chatsWriterUserName));
            }
        });
        ClientReceiveMessageEvents.GAME.register((text, overlay) -> sendText(text.getString()));
        ClientReceiveMessageEvents.CHAT.register((text, signedMessage,gameProfile,parameters,instant) -> sendText(text.getString()));
    }
}