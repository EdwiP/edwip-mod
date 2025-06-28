package com.edwip.Menu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class ModConfig {
    private static final File CONFIG_FILE = new File("config/edwip-mods.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Static config fields
    public static boolean disableAll = false;
    public static boolean enableKick = false;
    public static boolean enableWarn = false;
    // Kick
    public static String kickPrefix = "Please stop <REASON>, if you continue you will be banned.";
    public static String kickReasonLetters = "Lower All";
    public static int kickSecondTime = 500;
    public static String kickSecondCommand = "/warn -s <PLAYER> <REASON>, kicked warn";
    public static String kickSecondLetters = "Lower All";
    // Warn
    public static String warnPrefix = "Please stop <REASON>, if you continue you will be banned.";
    public static String warnReasonLetters = "Lower All";


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
                // Kick Command
                kickPrefix = data.kickPrefix;
                kickReasonLetters = data.kickReasonLetters;
                kickSecondCommand = data.kickSecondCommand;
                kickSecondTime = data.kickSecondTime;
                kickSecondLetters = data.getKickSecondLetters;
                // Warn Command
                warnPrefix = data.warnPrefix;
                warnReasonLetters = data.warnReasonLetters;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class Data {
        boolean disableAll = ModConfig.disableAll;
        boolean enableKick = ModConfig.enableKick;
        boolean enableWarn = ModConfig.enableWarn;
        // Kick
        String kickPrefix = ModConfig.kickPrefix;
        String kickReasonLetters = ModConfig.kickReasonLetters;
        String kickSecondCommand = ModConfig.kickSecondCommand;
        int kickSecondTime = ModConfig.kickSecondTime;
        String getKickSecondLetters = ModConfig.kickSecondLetters;
        // Warn
        String warnPrefix = ModConfig.warnPrefix;
        String warnReasonLetters = ModConfig.warnReasonLetters;

    }
}

