package com.edwip;

import com.edwip.Addons.*;
import com.edwip.Menu.ModConfig;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ClientModInitializer {
    public static final String MOD_ID = "kick-command";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.

    public static void register() {
        // Set up "/" commands events
        KickCommands.doKickCommands();
        WarnCommands.doWarnCommands();
        DiscordChatLog.doDiscordChatLog();
        ChatsWriter.doChatsWriter();
        SkinCloner.doSkinCloner();
        ServerSwitcher.doSwitchServer();
    }

    @Override
    public void onInitializeClient() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        ModConfig.load();
        register();
        LOGGER.info("EdwiP Mods Loaded.");
    }

}