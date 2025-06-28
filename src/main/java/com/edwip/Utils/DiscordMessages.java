package com.edwip.Utils;

import com.edwip.Menu.ModConfig;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordMessages {
    public static final String MOD_ID = "chat-log";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    //WEBHOOK_URL   WEBHOOK_TEST
    
    public static void sendDiscordMessage(String messageContent, Integer timeStamp) {
        try {
            messageContent = messageContent.replaceAll("§.", "");
            Pattern skipPattern = Pattern.compile("\\[Staff]" +
                    "|^\\(Silent\\)" +
                    "|^\\[\\S+ -> \\S+]" +
                    "|^\\(\\d{2}/\\d{2} \\d{2}:\\d{2}\\)" +
                    "|^Showing block data for: (-?\\d+), (-?\\d+), (-?\\d+) \\(Max of 10\\)" +
                    "|^-- \\[.*?ago] --" +
                    "|^History for (\\S+) \\(Limit: \\d+\\):" +
                    "|^You received the inspector stick.$" +
                    "|^You are already connected to this server!$" +
                    "|^The specified server (.*?) does not exist.$" +
                    "|^You did the teleporting to (\\S+).$" +
                    "|^(\\S+) has been kicked.$" +
                    "|^(\\S+) is already banned, and you do not have permission to replace existing bans.$" +
                    "|^⛏ Gamemode ┃ Gamemode set to CREATIVE.$" +
                    //"|^⇄ Servers ┃ Connecting you to mineplay-\\d+.$"
                    "|^⇄ Teleport ┃ Player not found.$" +
                    "|^(\\S+) has been banned.$" +
                    "|^Switched gamemode to (\\S+)$" +
                    "|^You were frozen.$" +
                    "|^You were unfrozen.$" +
                    "|^Player (\\S+) is online on server mineplay-(\\S+).$" +
                    "|No player matching (\\S+) is connected to this server.$" +
                    "|^\\[Mineplay] Do /blocks to choose your blocks!$" +
                    "|^You're chatting too fast. Please wait a moment.$" +
                    "|^Available servers: lobby(?:, mineplay-\\d+)*$"
            );
            if (skipPattern.matcher(messageContent).find()) {
                // Skip this line
                return;
            }

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

                Pattern messagePattern = Pattern.compile("^.*? (\\S+): .+$");
                Matcher messageMatch = messagePattern.matcher(messageContent);

                if (messageMatch.find()) {
                    String name = messageMatch.group(1);
                    String boldedName = "**" + name + "**";

                    // Replace the exact name only when followed by colon (":")
                    messageContent = messageContent.replaceFirst(Pattern.quote(name) + "(?=:\\s)", boldedName);
                }
            }

            Pattern atPattern = Pattern.compile("(@\\S+)");
            Matcher atMatcher = atPattern.matcher(messageContent);

            messageContent = atMatcher.replaceAll("`$1`");

            JSONObject json = new JSONObject();
            json.put("content", ModConfig.discordTimeStamp.replace("TIMESTAMP", timeStamp.toString()) + messageContent);
            String jsonString = json.toString();

            if (isSpecial) {
                Map<String, String> list = BanKick.banKick(messageContent);

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
                embed.put("color", color); // red color
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

                payload.put("content", ""); // Or " " — must be present if no username/avatar
                payload.put("embeds", new JSONArray().appendElement(embed));
                jsonString = payload.toString();
            }

            HttpURLConnection connection = getHttpURLConnection(isSpecial, jsonString);

            // Read response
            int responseCode = connection.getResponseCode();
            if (responseCode != 204) {
                LOGGER.warn("Discord webhook responded with code: {}", responseCode);
                try (var in = connection.getErrorStream()) {
                    if (in != null) {
                        String error = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                        LOGGER.warn("Error response from Discord: {}", error);
                    }
                }
            }

            connection.disconnect();
        } catch (Exception e) {
            LOGGER.error("Exception while sending Discord message", e);
        }
    }

    private static @NotNull HttpURLConnection getHttpURLConnection(boolean isSpecial, String jsonString) throws URISyntaxException, IOException {
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
}
