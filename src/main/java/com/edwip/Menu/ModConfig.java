package com.edwip.Menu;

import com.edwip.Utils.Prefixes;
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
    public static boolean enableChatsWriter = false;
    public static boolean enableSkinCloner = false;
    public static boolean enableServerSwitcher = false;
    // Kick
    public static String kickPrefix = "Please stop <REASON>, if you continue you will be banned.";
    public static Prefixes.lettersCapitalization kickReasonLetters = Prefixes.lettersCapitalization.LOWER_ALL;
    public static int kickSecondDelay = 500;
    public static String kickSecondCommand = "/warn -s <PLAYER> <REASON>, kicked warn";
    public static Prefixes.lettersCapitalization kickSecondLetters = Prefixes.lettersCapitalization.LOWER_ALL;
    // Warn
    public static String warnPrefix = "Please stop <REASON>, if you continue you will be banned.";
    public static Prefixes.lettersCapitalization warnReasonLetters = Prefixes.lettersCapitalization.LOWER_ALL;
    // Discord Chat Log
    public static String discordMainWebhookUrl = "";
    public static String discordModeratorWebhookUrl = "";
    public static int discordDelay = 10;
    public static String discordTimeStamp = "<t:TIMESTAMP:T>";
    public static String discordUserName = "You";
    public static String discordOpenGameMessage = "Opened the game.";
    public static String discordCloseGameMessage = "Closed the game.";
    public static String discordJoinServerMessage = "Joined server <SERVER>.";
    public static String discordLeaveServerMessage = "Left server <SERVER>.";
    public static List<String> discordMineplayPrivacy = Prefixes.skipPatterns;
    public static List<String> regexPatterns = Prefixes.inputRegex;
    public static List<String> regexResults = Prefixes.outputFormat;
    // ChatsWriter
    public static String chatsWriterFileName = "chats";
    public static String chatsWriterUserName = "You";
    public static String chatsWriterDateFormat = "yyyy/MM/dd HH:mm:ss";
    public static String chatsWriterOpenGameMessage = "Opened the game.";
    public static String chatsWriterCloseGameMessage = "Closed the game.";
    public static String chatsWriterJoinServerMessage = "Joined server <SERVER>.";
    public static String chatsWriterLeaveServerMessage = "Left server <SERVER>.";
    // Server Switcher
    public static boolean serverSwitcherAutoSwitch = true;
    public static int serverSwitcherAutoSwitchDelay = 5;
    public static int serverSwitcherDelayBeforeError = 100;
    public static String serverSwitcherPrefix = "Teleport";
    public static List<String> serverSwitcherResponse = Prefixes.serverSwitcherResponse;
    public static List<String> serverSwitcherTeleported = Prefixes.serverSwitcherTeleported;
    public static List<String> serverSwitcherNotExist = Prefixes.serverSwitcherNotExist;
    public static List<String> serverSwitcherAlreadyHere = Prefixes.serverSwitcherAlreadyHere;
    public static List<String> serverSwitcherError = Prefixes.serverSwitcherError;

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(new Data(), writer);  // Save all values in one pass
        } catch (Exception e) {
            //noinspection CallToPrintStackTrace
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
                enableChatsWriter = data.enableChatsWriter;
                enableSkinCloner = data.enableSkinCloner;
                enableServerSwitcher = data.enableServerSwitcher;
                // Kick Command
                kickPrefix = data.kickPrefix;
                kickSecondCommand = data.kickSecondCommand;
                kickSecondDelay = data.kickSecondTime;
                // Warn Command
                warnPrefix = data.warnPrefix;
                // Discord Chat Log
                discordMainWebhookUrl = data.discordMainWebhookUrl;
                discordModeratorWebhookUrl = data.discordModeratorWebhookUrl;
                discordDelay = data.discordDelay;
                discordTimeStamp = data.discordTimeStamp;
                discordUserName = data.discordUserName;
                discordOpenGameMessage = data.discordOpenGameMessage;
                discordCloseGameMessage = data.discordCloseGameMessage;
                discordJoinServerMessage = data.discordJoinServerMessage;
                discordLeaveServerMessage = data.discordLeaveServerMessage;
                discordMineplayPrivacy = new ArrayList<>(data.discordMineplayPrivacy);
                regexPatterns = new ArrayList<>(data.regexPatterns);
                regexResults = new ArrayList<>(data.regexResults);
                // Chat Writer
                chatsWriterFileName = data.chatsWriterFileName;
                chatsWriterUserName = data.chatsWriterUserName;
                chatsWriterDateFormat = data.chatsWriterTimeStamp;
                chatsWriterOpenGameMessage = data.chatsWriterOpenGameMessage;
                chatsWriterCloseGameMessage = data.chatsWriterCloseGameMessage;
                chatsWriterJoinServerMessage = data.chatsWriterJoinServerMessage;
                chatsWriterLeaveServerMessage = data.chatsWriterLeaveServerMessage;
                // Server Switcher
                serverSwitcherAutoSwitch = data.serverSwitcherAutoSwitch;
                serverSwitcherAutoSwitchDelay = data.serverSwitcherAutoSwitchDelay;
                serverSwitcherDelayBeforeError = data.serverSwitcherDelayBeforeError;
                serverSwitcherPrefix = data.serverSwitcherPrefix;
                serverSwitcherResponse = data.serverSwitcherResponse;
                serverSwitcherTeleported = data.serverSwitcherTeleported;
                serverSwitcherNotExist = data.serverSwitcherNotExist;
                serverSwitcherAlreadyHere = data.serverSwitcherAlreadyHere;
                serverSwitcherError = data.serverSwitcherError;
                try {
                    kickReasonLetters = Prefixes.lettersCapitalization.valueOf(data.kickReasonLetters);
                    kickSecondLetters = Prefixes.lettersCapitalization.valueOf(data.kickSecondLetters);
                    warnReasonLetters = Prefixes.lettersCapitalization.valueOf(data.warnReasonLetters);
                } catch (IllegalArgumentException e) {
                    kickReasonLetters = Prefixes.lettersCapitalization.LOWER_ALL; // fallback default
                    kickSecondLetters = Prefixes.lettersCapitalization.LOWER_ALL;
                    warnReasonLetters = Prefixes.lettersCapitalization.LOWER_ALL;
                }
            }
        } catch (Exception e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static class Data {
        boolean disableAll = ModConfig.disableAll;
        boolean enableKick = ModConfig.enableKick;
        boolean enableWarn = ModConfig.enableWarn;
        boolean enableDiscordChatLog = ModConfig.enableDiscordChatLog;
        boolean enableChatsWriter = ModConfig.enableChatsWriter;
        boolean enableSkinCloner = ModConfig.enableSkinCloner;
        boolean enableServerSwitcher = ModConfig.enableServerSwitcher;
        // Kick Commands
        String kickPrefix = ModConfig.kickPrefix;
        String kickReasonLetters = ModConfig.kickReasonLetters.name();
        String kickSecondCommand = ModConfig.kickSecondCommand;
        int kickSecondTime = ModConfig.kickSecondDelay;
        String kickSecondLetters = ModConfig.kickSecondLetters.name();
        // Warn Commands
        String warnPrefix = ModConfig.warnPrefix;
        String warnReasonLetters = ModConfig.warnReasonLetters.name();
        // Discord Chat Log
        String discordMainWebhookUrl = ModConfig.discordMainWebhookUrl;
        String discordModeratorWebhookUrl = ModConfig.discordModeratorWebhookUrl;
        int discordDelay = ModConfig.discordDelay;
        String discordTimeStamp = ModConfig.discordTimeStamp;
        String discordUserName = ModConfig.discordUserName;
        String discordOpenGameMessage = ModConfig.discordOpenGameMessage;
        String discordCloseGameMessage = ModConfig.discordCloseGameMessage;
        String discordJoinServerMessage = ModConfig.discordJoinServerMessage;
        String discordLeaveServerMessage = ModConfig.discordLeaveServerMessage;
        List<String> discordMineplayPrivacy = new ArrayList<>(ModConfig.discordMineplayPrivacy);
        List<String> regexPatterns = new ArrayList<>(ModConfig.regexPatterns);
        List<String> regexResults = new ArrayList<>(ModConfig.regexResults);
        // Chats writer
        String chatsWriterFileName = ModConfig.chatsWriterFileName;
        String chatsWriterUserName = ModConfig.chatsWriterUserName;
        String chatsWriterTimeStamp = ModConfig.chatsWriterDateFormat;
        String chatsWriterOpenGameMessage = ModConfig.chatsWriterOpenGameMessage;
        String chatsWriterCloseGameMessage = ModConfig.chatsWriterCloseGameMessage;
        String chatsWriterJoinServerMessage = ModConfig.chatsWriterJoinServerMessage;
        String chatsWriterLeaveServerMessage = ModConfig.chatsWriterLeaveServerMessage;
        // Server Switcher
        boolean serverSwitcherAutoSwitch = ModConfig.serverSwitcherAutoSwitch;
        int serverSwitcherAutoSwitchDelay = ModConfig.serverSwitcherAutoSwitchDelay;
        int serverSwitcherDelayBeforeError = ModConfig.serverSwitcherDelayBeforeError;
        String serverSwitcherPrefix = ModConfig.serverSwitcherPrefix;
        List<String> serverSwitcherResponse = ModConfig.serverSwitcherResponse;
        List<String> serverSwitcherTeleported = ModConfig.serverSwitcherTeleported;
        List<String> serverSwitcherNotExist = ModConfig.serverSwitcherNotExist;
        List<String> serverSwitcherAlreadyHere = ModConfig.serverSwitcherAlreadyHere;
        List<String> serverSwitcherError = ModConfig.serverSwitcherError;
    }
}

