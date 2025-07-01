package com.edwip.Utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Prefixes {
    public static final List<String> REASON_SUGGESTIONS = List.of(
            "Griefing",
            "Spamming",
            "Spamming blocks",
            "Racism",
            "Being racist",
            "Inappropriate builds",
            "Creating inappropriate builds",
            "Inappropriate skin",
            "Using inappropriate skin",
            "Inappropriate behavior",
            "Acting inappropriately",
            "Homophobia",
            "Being homophobic",
            "Transphobia",
            "Being transphobic",
            "Hate speech",
            "Using hate speech",
            "Links",
            "Sending links",
            "QR builds",
            "Creating QR builds",
            "Harassment",
            "Harassing",
            "Excessive sweaing",
            "Swearing excessively",
            "Sex",
            "Doing sex",
            "England",
            "Being England",
            "Juan",
            "Being Juan"
    );
    public static final List<String> skipPatterns = List.of(
            "^Invalid regex!",
            "^Invalid regex or format!",
            "^Invalid format!",
            "^\\[Staff\\]",
            "^\\(Silent\\)",
            "^\\[\\S+ -> \\S+]",
            "^\\(\\d{2}/\\d{2} \\d{2}:\\d{2}\\)",
            "^Showing block data for: (-?\\d+), (-?\\d+), (-?\\d+) \\(Max of 10\\)",
            "^-- \\[.*?ago] --",
            "^History for (\\S+) \\(Limit: \\d+\\):",
            "^You received the inspector stick.$",
            "^You are already connected to this server!$",
            "^The specified server (.*?) does not exist.$",
            "^You did the teleporting to (\\S+).$",
            "^(\\S+) has been kicked.$",
            "^(\\S+) is already banned, and you do not have permission to replace existing bans.$",
            "^⛏ Gamemode ┃ Gamemode set to CREATIVE.$",
            // "^⇄ Servers ┃ Connecting you to mineplay-\\d+.$", // optional: commented out
            "^⇄ Teleport ┃ Player not found.$",
            "^(\\S+) has been banned.$",
            "^Switched gamemode to (\\S+)$",
            "^You were frozen.$",
            "^You were unfrozen.$",
            "^Player (\\S+) is online on server mineplay-(\\S+).$",
            "No player matching (\\S+) is connected to this server.$",
            "^\\[Mineplay\\] Do /blocks to choose your blocks!$",
            "^You're chatting too fast. Please wait a moment.$",
            "^Available servers: lobby(?:, mineplay-\\d+)*$"
    );

    public static final List<String> serverSwitcherResponse = List.of(
            "Got you. Moving to <SERVER>.",
            "Te pillé. Trasladándose al <SERVER>."
    );
    public static final List<String> serverSwitcherTeleported = List.of(
        "Moved to server <SERVER>."
    );
    public static final List<String> serverSwitcherNotExist = List.of(
            "The server <SERVER> does not exist."
    );
    public static final List<String> serverSwitcherAlreadyHere = List.of(
            "I'm already in <SERVER>."
    );
    public static final List<String> serverSwitcherError = List.of(
            "Error trying to teleport to the server <SERVER>."
    );
    public static final List<String> inputRegex = List.of(
            "<(.*?)> (.*)",
            "(\\S*@\\S*)"
    );
    public static final List<String> outputFormat = List.of(
            "(1): (2)",
            "`(1)`"
    );

    public enum lettersCapitalization {
        NO_CHANGE("No Change"),
        LOWER_ALL("Lower All"),
        FIRST_LETTER("First Letter"),
        FIRST_EVERY_LETTER("First Every Letter"),
        UPPER_ALL("Upper All");


        private final String prefix;

        lettersCapitalization(String prefix) {
            this.prefix = prefix;
        }

        public String getPrefix() {
            return prefix;
        }

        @Override
        public String toString() {
            return prefix;
        }
    }

    public static boolean matchesAnyRegex(List<String> regexList, String message) {
        for (String regex : regexList) {
            try {
                if (Pattern.compile(regex).matcher(message).find()) {
                    return true;
                }
            } catch (PatternSyntaxException e) {
                assert MinecraftClient.getInstance().player != null;
                MinecraftClient.getInstance().player.sendMessage(Text.literal("Invalid regex!\n" +regex).formatted(Formatting.RED), false);
            }
        }
        return false;
    }
}