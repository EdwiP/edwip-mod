package com.edwip.Addons;

import com.edwip.Main;
import com.edwip.Menu.ModConfig;
import com.edwip.Utils.Prefixes;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordChatLog {
    private static final BlockingQueue<Map.Entry<String, Integer>> messageQueue = new LinkedBlockingQueue<>();
    private static String lastServer = "";

    public static void doDiscordChatLog() {
        new Thread(DiscordChatLog::processQueue).start();
        if (!ModConfig.discordOpenGameMessage.isEmpty()) {
            sendMessage(ModConfig.discordOpenGameMessage.replace("<PLAYER>", ModConfig.discordUserName));
        }
        ClientLifecycleEvents.CLIENT_STOPPING.register(minecraftClient -> {
            if (!ModConfig.discordCloseGameMessage.isEmpty()) {
                sendMessage(ModConfig.discordCloseGameMessage.replace("<PLAYER>", ModConfig.discordUserName));
            }
        });
        ClientPlayConnectionEvents.JOIN.register((clientPlayNetworkHandler, packetSender, minecraftClient) -> {
            String serverName = minecraftClient.getCurrentServerEntry() != null
                    ? minecraftClient.getCurrentServerEntry().address
                    : "Singleplayer";
            lastServer = serverName;
            if (!ModConfig.discordJoinServerMessage.isEmpty()) {
                sendMessage(ModConfig.discordJoinServerMessage.replace("<SERVER>", serverName).replace("<PLAYER>", ModConfig.discordUserName));
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, sender) -> {
            if (!ModConfig.discordLeaveServerMessage.isEmpty()) {
                sendMessage(ModConfig.discordLeaveServerMessage.replace("<SERVER>", lastServer).replace("<PLAYER>", ModConfig.discordUserName));
            }
        });

        ClientReceiveMessageEvents.CHAT.register((text, signedMessage, gameProfile, parameters, instant) -> sendMessage(text.getString()));
        ClientReceiveMessageEvents.GAME.register((text, bob) -> sendMessage(text.getString()));
    }


    private static void sendMessage(String content) {
        if (ModConfig.enableDiscordChatLog)
            messageQueue.offer(new AbstractMap.SimpleEntry<>(content, Math.toIntExact(Instant.now().getEpochSecond())));
    }

    private static void processQueue() {
        while (true) {
            try {
                Map.Entry<String, Integer> entry = messageQueue.take();

                sendDiscordMessage(entry.getKey(), entry.getValue());
                Thread.sleep(250); // ~4 msg/sec (Discord allows 5/sec)
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private static void sendDiscordMessage(String messageContent, Integer timeStamp) {
        try {
            messageContent = messageContent.replaceAll("§.", "");
            if (Prefixes.matchesAnyRegex(ModConfig.discordMineplayPrivacy, messageContent)) return;

            boolean isSpecial = false;
            Pattern specialPattern = Pattern.compile(
                    "^(\\S+) was kicked by (\\S+) for '(.*?)'.$" +
                            "|^(\\S+) was kicked by (\\S+).$" +
                            "|^(\\S+) has been kicked from Roblox by (\\S+) for (.*?).$" +
                            "|^(\\S+) temp IP-banned (\\S+) for (.*?) for '(.*?)'$" +
                            "|^(\\S+) tempbanned (\\S+) for (.*?) for '(.*?)'$" +
                            "|^(\\S+) banned (\\S+) for '(.*?)'$" +
                            "|^(\\S+) has been banned from Roblox by (\\S+) for (.*?).$" +
                            "|^(\\S+) warned (\\S+) for '(.*?)'$" +
                            "|^(\\S+) tempmuted (\\S+) for (.*?) for '(.*?)'$" +
                            "|^(\\S+) tempmuted (\\S+) for '(.*?)'$" +
                            "|^(\\S+) unbanned (\\S+) for '(.*?)'$" +
                            "|^(\\S+) ummuted (\\S+) for '(.*?)'$"
            );
            if (specialPattern.matcher(messageContent).find()) {
                // Make the text bold in discord format
                isSpecial = true;
            }
            if (!isSpecial) {
                Pattern joinPattern = Pattern.compile(
                        "^\\[\\+]"
                );
                if (joinPattern.matcher(messageContent).find()) {
                    // Make the text italic in discord format
                    messageContent = "*" + messageContent + "*";
                }
            }

            messageContent = applyRegexConversions(messageContent);

            Pattern atPattern = Pattern.compile("(@\\S+)");
            Matcher atMatcher = atPattern.matcher(messageContent);

            messageContent = atMatcher.replaceAll("`$1`");

            JSONObject json = new JSONObject();
            json.put("content", ModConfig.discordTimeStamp.replace("TIMESTAMP", timeStamp.toString()) + messageContent);
            String jsonString = json.toString();

            if (isSpecial) {
                Map<String, String> list = banKick(messageContent);

                int color = switch (list.get("Type")) {
                    case "Ban" -> 0xFF0000;
                    case "Warn", "Mute" -> 0xFF8000;
                    case "Kick" -> 0xFFFF00;
                    case "Unban", "Unmute" -> 0x00FF00;
                    default -> 0x808080;
                };

                JSONObject payload = new JSONObject();

                JSONObject embed = new JSONObject();
                embed.put("title", list.get("Platform") + " Player " + list.get("Type"));
                embed.put("color", color);
                embed.put("description",
                        "**Player:** " + list.get("User") +
                                "\n**By: **" + list.get("By") +
                                "\n**Time: **" + "<t:" + timeStamp + ":F> (<t:" + timeStamp + ":R>)"
                );

                JSONArray fields = new JSONArray();

                if (!list.get("Duration").equals("None")) {
                    JSONObject fieldDuration = new JSONObject();
                    fieldDuration.put("name", "Duration");
                    fieldDuration.put("value", list.get("Duration"));
                    fieldDuration.put("inline", true);
                    fields.appendElement(fieldDuration);
                }

                JSONObject fieldReason = new JSONObject();
                fieldReason.put("name", "Reason");
                fieldReason.put("value", list.get("Reason"));
                fieldReason.put("inline", true);
                fields.appendElement(fieldReason);

                embed.put("fields", fields);

                payload.put("content", "");
                payload.put("embeds", new JSONArray().appendElement(embed));
                jsonString = payload.toString();
            }

            HttpURLConnection connection = getHttpURLConnection(isSpecial, jsonString);

            // Read response
            int responseCode = connection.getResponseCode();
            if (responseCode != 204) {
                Main.LOGGER.warn("Discord webhook responded with code: {}", responseCode);
                try (var in = connection.getErrorStream()) {
                    if (in != null) {
                        String error = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                        Main.LOGGER.warn("Error response from Discord: {}", error);
                    }
                }
            }

            connection.disconnect();
        } catch (Exception e) {
            Main.LOGGER.warn("Exception while sending Discord message", e);
        }
    }

    private static HttpURLConnection getHttpURLConnection(boolean isSpecial, String jsonString) throws URISyntaxException, IOException {
        URI uri = new URI(isSpecial ? ModConfig.discordModeratorWebhookUrl : ModConfig.discordMainWebhookUrl);
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Send request
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        return connection;
    }

    private static String applyRegexConversions(String input) {
        for (int i = 0; i < Math.min(ModConfig.regexPatterns.size(), ModConfig.regexResults.size()); i++) {
            String pattern = ModConfig.regexPatterns.get(i);
            String format = ModConfig.regexResults.get(i);
            try {
                Pattern p = Pattern.compile(pattern);
                Matcher matcher = p.matcher(input);
                if (matcher.find()) {
                    StringBuilder sb = new StringBuilder();
                    Pattern placeholderPattern = Pattern.compile("\\((\\d+)\\)");
                    Matcher formatMatcher = placeholderPattern.matcher(format);

                    int lastEnd = 0;
                    while (formatMatcher.find()) {
                        sb.append(format, lastEnd, formatMatcher.start());
                        int groupNum = Integer.parseInt(formatMatcher.group(1));
                        String groupText = "";
                        if (groupNum <= matcher.groupCount() && matcher.group(groupNum) != null) {
                            groupText = matcher.group(groupNum);
                        }
                        sb.append(groupText);
                        lastEnd = formatMatcher.end();
                    }
                    sb.append(format.substring(lastEnd));

                    return sb.toString();
                }
            } catch (Exception e) {
                if (MinecraftClient.getInstance().player != null) {
                    MinecraftClient.getInstance().player.sendMessage(
                            Text.literal("Invalid regex or format!\n" + pattern + "\n" + format).formatted(Formatting.RED),
                            false);
                }
            }
        }
        return input;
    }

    public static Map<String, String> banKick(String message) {
        System.out.println(message);
        Map<String, String> list = new HashMap<>();
        Pattern kickPattern = Pattern.compile(
                "^(\\S+) was kicked by (\\S+) for '(.*?)'.$" +
                        "|^(\\S+) was kicked by (\\S+).$"
        );
        Pattern kickRobloxPattern = Pattern.compile(
                "^(\\S+) has been kicked from Roblox by (\\S+) for (.*?).$"
        );
        Pattern banPattern = Pattern.compile(
                "^(\\S+) temp IP-banned (\\S+) for (.*?) for '(.*?)'$" +
                        "|^(\\S+) tempbanned (\\S+) for (.*?) for '(.*?)'$" +
                        "|^(\\S+) banned (\\S+) for '(.*?)'$"
        );
        Pattern banRobloxPattern = Pattern.compile(
                "^(\\S+) has been banned from Roblox by (\\S+) for (.*?).$"
        );
        Pattern mutePattern = Pattern.compile(
                "^(\\S+) tempmuted (\\S+) for (.*?) for '(.*?)'$" +
                        "|^(\\S+) muted (\\S+) for '(.*?)'$"
        );
        Pattern warnPattern = Pattern.compile(
                "^(\\S+) warned (\\S+) for '(.*?)'$"
        );
        Pattern unbanPattern = Pattern.compile(
                "^(\\S+) unbanned (\\S+) for '(.*?)'$"
        );
        Pattern unmutePattern = Pattern.compile(
                "^(\\S+) ummuted (\\S+) for '(.*?)'$"
        );
        list.put("Type", "Unknown");
        list.put("Platform", "Unknown");
        list.put("User", "Unknown");
        list.put("By", "Unknown");
        list.put("Reason", "No reason specified.");
        list.put("Duration", "None");
        if (kickPattern.matcher(message).find()) {
            Matcher matcher = kickPattern.matcher(message);
            matcher.find();
            list.put("Type", "Kick");
            list.put("Platform", "Minecraft");
            if (matcher.group(1) != null && matcher.group(3) != null) {
                // kicked with reason
                list.put("User", matcher.group(1));
                list.put("By", matcher.group(2));
                list.put("Reason", matcher.group(3));
            } else if (matcher.group(4) != null) {
                // kicked without reason
                list.put("User", matcher.group(4));
                list.put("By", matcher.group(5));
            }
        } else if (kickRobloxPattern.matcher(message).find()) {
            Matcher matcher = kickRobloxPattern.matcher(message);
            matcher.find();
            list.put("Type", "Kick");
            list.put("Platform", "Roblox");
            list.put("User", matcher.group(1));
            list.put("By", matcher.group(2));
            list.put("Reason", matcher.group(3));
        } else if (banPattern.matcher(message).find()) {
            Matcher matcher = banPattern.matcher(message);
            matcher.find();
            list.put("Type", "Ban");
            list.put("Platform", "Minecraft");
            if (matcher.group(1) != null && matcher.group(4) != null) {
                // temp IP-banned
                list.put("By", matcher.group(1));
                list.put("User", matcher.group(2));
                list.put("Duration", matcher.group(3));
                list.put("Reason", matcher.group(4));
            } else if (matcher.group(5) != null && matcher.group(8) != null) {
                // temp-banned
                list.put("By", matcher.group(5));
                list.put("User", matcher.group(6));
                list.put("Duration", matcher.group(7));
                list.put("Reason", matcher.group(8));
            } else if (matcher.group(9) != null && matcher.group(11) != null) {
                // banned
                list.put("By", matcher.group(9));
                list.put("User", matcher.group(10));
                list.put("Duration", "Permanent");
                list.put("Reason", matcher.group(11));
            }
        } else if (banRobloxPattern.matcher(message).find()) {
            Matcher matcher = banRobloxPattern.matcher(message);
            matcher.find();
            list.put("Type", "Ban");
            list.put("Platform", "Roblox");
            list.put("User", matcher.group(1));
            list.put("By", matcher.group(2));
            list.put("Reason", matcher.group(3));
        } else if (warnPattern.matcher(message).find()) {
            Matcher matcher = warnPattern.matcher(message);
            matcher.find();
            list.put("Type", "Warn");
            list.put("Platform", "Minecraft");
            list.put("By", matcher.group(1));
            list.put("User", matcher.group(2));
            list.put("Reason", matcher.group(3));
        } else if (mutePattern.matcher(message).find()) {
            Matcher matcher = mutePattern.matcher(message);
            matcher.find();
            list.put("Type", "Mute");
            list.put("Platform", "Minecraft");
            if (matcher.group(1) != null && matcher.group(4) != null) {
                // temp muted
                list.put("By", matcher.group(1));
                list.put("User", matcher.group(2));
                list.put("Duration", matcher.group(3));
                list.put("Reason", matcher.group(4));
            } else if (matcher.group(5) != null && matcher.group(7) != null) {
                // muted
                list.put("By", matcher.group(5));
                list.put("User", matcher.group(6));
                list.put("Duration", "Permanent");
                list.put("Reason", matcher.group(7));
            }
        } else if (unbanPattern.matcher(message).find()) {
            Matcher matcher = unbanPattern.matcher(message);
            matcher.find();
            list.put("Type", "Unban");
            list.put("Platform", "Minecraft");
            list.put("By", matcher.group(1));
            list.put("User", matcher.group(2));
            list.put("Reason", matcher.group(3));
        } else if (unmutePattern.matcher(message).find()) {
            Matcher matcher = banRobloxPattern.matcher(message);
            matcher.find();
            list.put("Type", "Unmute");
            list.put("Platform", "Minecraft");
            list.put("By", matcher.group(1));
            list.put("User", matcher.group(2));
            list.put("Reason", matcher.group(3));
        } else {
            System.out.println("List not matched.");
        }
        return list;
    }
}
