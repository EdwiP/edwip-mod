package com.edwip.Menu;

import com.edwip.Utils.LettersCapitalization;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ModConfig {
    private static final File CONFIG_FILE = new File("config/edwip-mods.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Static config fields
    public static boolean disableAll = false;
    public static boolean enableKick = false;
    public static boolean enableWarn = false;
    public static boolean enableDiscordChatLog = false;
    // Kick
    public static String kickPrefix = "Please stop <REASON>, if you continue you will be banned.";
    public static LettersCapitalization.lettersCapitalization kickReasonLetters = LettersCapitalization.lettersCapitalization.LOWER_ALL;
    public static int kickSecondTime = 500;
    public static String kickSecondCommand = "/warn -s <PLAYER> <REASON>, kicked warn";
    public static LettersCapitalization.lettersCapitalization kickSecondLetters = LettersCapitalization.lettersCapitalization.LOWER_ALL;
    // Warn
    public static String warnPrefix = "Please stop <REASON>, if you continue you will be banned.";
    public static LettersCapitalization.lettersCapitalization warnReasonLetters = LettersCapitalization.lettersCapitalization.LOWER_ALL;
    // Discord Chat Log
    public static String discordMainWebhookUrl = "";
    public static String discordModeratorWebhookUrl = "";
    public static String discordTimeStamp = "<t:TIMESTAMP:T>";
    public static String discordUserName = "You";
    public static String discordOpenGameMessage = "Opened the game.";
    public static String discordCloseGameMessage = "Closed the game.";
    public static String discordJoinServerMessage = "Joined server <SERVER>.";
    public static String discordLeaveServerMessage = "Left server <SERVER>.";
    public static List<String> discordMineplayPrivacy = new ArrayList<>(List.of("lobby", "mineplay-48"));

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(new Data(), writer);  // Save all values in one pass
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        if (!CONFIG_FILE.exists()) return;
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            Data data = GSON.fromJson(reader, Data.class);
            if (data != null) {
                disableAll = data.disableAll;
                enableKick = data.enableKick;
                enableWarn = data.enableWarn;
                enableDiscordChatLog = data.enableDiscordChatLog;
                // Kick Command
                kickPrefix = data.kickPrefix;
                kickSecondCommand = data.kickSecondCommand;
                kickSecondTime = data.kickSecondTime;
                // Warn Command
                warnPrefix = data.warnPrefix;
                // Discord Chat Log
                discordMainWebhookUrl = data.discordMainWebhookUrl;
                discordModeratorWebhookUrl = data.discordModeratorWebhookUrl;
                discordTimeStamp = data.discordTimeStamp;
                discordUserName = data.discordUserName;
                discordOpenGameMessage = data.discordOpenGameMessage;
                discordCloseGameMessage = data.discordCloseGameMessage;
                discordJoinServerMessage = data.discordJoinServerMessage;
                discordLeaveServerMessage = data.discordLeaveServerMessage;
                discordMineplayPrivacy = new ArrayList<>(data.discordMineplayPrivacy);
                try {
                    kickReasonLetters = LettersCapitalization.lettersCapitalization.valueOf(data.kickReasonLetters);
                    kickSecondLetters = LettersCapitalization.lettersCapitalization.valueOf(data.kickSecondLetters);
                    warnReasonLetters = LettersCapitalization.lettersCapitalization.valueOf(data.warnReasonLetters);
                } catch (IllegalArgumentException e) {
                    kickReasonLetters = LettersCapitalization.lettersCapitalization.LOWER_ALL; // fallback default
                    kickSecondLetters = LettersCapitalization.lettersCapitalization.LOWER_ALL;
                    warnReasonLetters = LettersCapitalization.lettersCapitalization.LOWER_ALL;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class Data {
        boolean disableAll = ModConfig.disableAll;
        boolean enableKick = ModConfig.enableKick;
        boolean enableWarn = ModConfig.enableWarn;
        boolean enableDiscordChatLog = ModConfig.enableDiscordChatLog;
        // Kick Commands
        String kickPrefix = ModConfig.kickPrefix;
        String kickReasonLetters = ModConfig.kickReasonLetters.name();
        String kickSecondCommand = ModConfig.kickSecondCommand;
        int kickSecondTime = ModConfig.kickSecondTime;
        String kickSecondLetters = ModConfig.kickSecondLetters.name();
        // Warn Commands
        String warnPrefix = ModConfig.warnPrefix;
        String warnReasonLetters = ModConfig.warnReasonLetters.name();
        // Discord Chat Log
        String discordMainWebhookUrl = ModConfig.discordMainWebhookUrl;
        String discordModeratorWebhookUrl = ModConfig.discordModeratorWebhookUrl;
        String discordTimeStamp = ModConfig.discordTimeStamp;
        String discordUserName = ModConfig.discordUserName;
        String discordOpenGameMessage = ModConfig.discordOpenGameMessage;
        String discordCloseGameMessage = ModConfig.discordCloseGameMessage;
        String discordJoinServerMessage = ModConfig.discordJoinServerMessage;
        String discordLeaveServerMessage = ModConfig.discordLeaveServerMessage;
        List<String> discordMineplayPrivacy = new ArrayList<>(ModConfig.discordMineplayPrivacy);
    }
}

