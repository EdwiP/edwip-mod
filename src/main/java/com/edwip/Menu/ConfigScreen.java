package com.edwip.Menu;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

public class ConfigScreen {
    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("text.autoconfig.kickcommand.title"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // GENERAL CATEGORY
        ConfigCategory general = builder.getOrCreateCategory(Text.of("General Settings"));

        general.addEntry(entryBuilder
                .startBooleanToggle(Text.of("Disable all"), ModConfig.disableAll)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> {
                    ModConfig.disableAll = newValue;
                    ModConfig.save();
                })
                .setTooltip(Text.of("Disable all mods."))
                .build());

        general.addEntry(entryBuilder
                .startBooleanToggle(Text.of("Enable Kick Commands"), ModConfig.enableKick)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> ModConfig.enableKick = newValue)
                .setTooltip(Text.of("Enable kick commands."))
                .build());
        general.addEntry(entryBuilder
                .startBooleanToggle(Text.of("Enable Warn Commands"), ModConfig.enableWarn)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> ModConfig.enableWarn = newValue)
                .setTooltip(Text.of("Enable Warn commands."))
                .build());



        // Kick Commands

        ConfigCategory kick = builder.getOrCreateCategory(Text.of("Kick Settings"));
        kick.addEntry(entryBuilder
                .startTextField(Text.of("Kick Reason"), ModConfig.kickPrefix)
                .setDefaultValue("<REASON>, if you continue you will be banned")
                .setSaveConsumer(newValue -> ModConfig.kickPrefix = newValue)
                .setTooltip(Text.of("""
                        Kick reason (replaced by <REASON>).
                        /mpkick <Player> <Reason>
                        /mprkick <Player> <Reason>
                        <Reason> is optional."""))
                .build()
        );
        kick.addEntry(entryBuilder
                .startStringDropdownMenu(Text.of("Kick Letters Capitalization"), ModConfig.kickReasonLetters)
                .setSelections(List.of("Lower All", "Upper All", "First Letter" , "First Every Letter"))
                .setDefaultValue("Lower All")
                .setSaveConsumer(newValue -> ModConfig.kickReasonLetters = newValue)
                .setTooltip(Text.of("""
                        Change letters capitalization.
                        
                        Example: heLLo WOrlD!
                        Lower all -> hello world!
                        Upper All -> HELLO WORLD!
                        First Letter -> Hello world!
                        First Every Letter -> Hello World!
                        
                        Option not in the list will keep Reason as the same."""))
                .build()
        );
        kick.addEntry(entryBuilder
                .startTextField(Text.of("Second Command Execution"), ModConfig.kickSecondCommand)
                .setDefaultValue("/warn -s <PLAYER> <REASON>, kicked warn")
                .setSaveConsumer(newValue -> ModConfig.kickSecondCommand = newValue)
                .setTooltip(Text.of("""
                        Execute second command after kicking (works only for Minecraft players)
                        Can include <PLAYER> and <REASON>."""))
                .build()
        );
        kick.addEntry(entryBuilder
                .startStringDropdownMenu(Text.of("Second Command Letters Capitalization"), ModConfig.kickSecondLetters)
                .setSelections(List.of("Lower All", "Upper All", "First Letter" , "First Every Letter"))
                .setDefaultValue("Lower All")
                .setSaveConsumer(newValue -> ModConfig.kickSecondLetters = newValue)
                .setTooltip(Text.of("""
                        Change letters capitalization for second command executor.
                        
                        Example: heLLo WOrlD!
                        Lower all -> hello world!
                        Upper All -> HELLO WORLD!
                        First Letter -> Hello world!
                        First Every Letter -> Hello World!
                        
                        Option not in the list will keep Reason as the same."""))
                .build()
        );

        kick.addEntry(entryBuilder
                .startIntSlider(Text.of("Second Command Time"), ModConfig.kickSecondTime,0,1000)
                .setDefaultValue(500)
                .setSaveConsumer(newValue -> ModConfig.kickSecondTime = newValue)
                .setTooltip(Text.of("""
                        Time to delay the second command execution (in ms) after kicking."""))
                .build()
        );
        // Warn

        ConfigCategory warn = builder.getOrCreateCategory(Text.of("Warn Settings"));
        warn.addEntry(entryBuilder
                .startTextField(Text.of("Warn Reason"), ModConfig.warnPrefix)
                .setDefaultValue("Please stop <REASON>, if you continue you will be banned")
                .setSaveConsumer(newValue -> ModConfig.warnPrefix = newValue)
                .setTooltip(Text.of("""
                        Warn reason (replaced by <REASON>).
                        /mpwarn <Player> <Reason>
                        <Reason> is optional."""))
                .build()
        );
        warn.addEntry(entryBuilder
                .startStringDropdownMenu(Text.of("Warn Letters Capitalization"), ModConfig.warnReasonLetters)
                .setSelections(List.of("Lower All", "Upper All", "First Letter" , "First Every Letter"))
                .setDefaultValue("Lower All")
                .setSaveConsumer(newValue -> ModConfig.warnReasonLetters = newValue)
                .setTooltip(Text.of("""
                        Change letters capitalization.
                        
                        Example: heLLo WOrlD!
                        Lower all -> hello world!
                        Upper All -> HELLO WORLD!
                        First Letter -> Hello world!
                        First Every Letter -> Hello World!
                        
                        Option not in the list will keep Reason as the same."""))
                .build()
        );

        // Discord Chat Log
        ConfigCategory discord = builder.getOrCreateCategory(Text.of("Discord Chat Log"));
        discord.addEntry(entryBuilder
                .startTextDescription(Text.of("General:"))
                .build()
        );
        discord.addEntry(entryBuilder
                .startTextField(Text.of("Main Discord Webhook URL"), ModConfig.discordMainWebhookUrl)
                .setDefaultValue("")
                .setSaveConsumer(newValue -> ModConfig.discordMainWebhookUrl = newValue)
                .build()
        );
        discord.addEntry(entryBuilder
                .startTextField(Text.of("Time Stamp"),ModConfig.discordTimeStamp)
                .setDefaultValue("<t:TIMESTAMP:T>")
                .setSaveConsumer(newValue -> ModConfig.discordTimeStamp = newValue)
                .setTooltip(Text.of("""
                        Time stamp format at the beginning of each message. Leave empty for no time stamp."""))
                .build()
        );
        discord.addEntry(entryBuilder
                .startTextField(Text.of("User Name"),ModConfig.discordUserName)
                .setDefaultValue("You")
                .setSaveConsumer(newValue -> ModConfig.discordUserName = newValue)
                .setTooltip(Text.of("""
                        Replace <PLAYER>. Can be used in 4 options below."""))
                .build()
        );
        discord.addEntry(entryBuilder
                .startTextField(Text.of("Open Game Message"),ModConfig.discordOpenGameMessage)
                .setDefaultValue("Opened the game.")
                .setSaveConsumer(newValue -> ModConfig.discordOpenGameMessage = newValue)
                .setTooltip(Text.of("""
                        Message after opening game. Leave empty for no message.
                        Can be used with <PLAYER>"""))
                .build()
        );
        discord.addEntry(entryBuilder
                .startTextField(Text.of("Close Game Message"),ModConfig.discordCloseGameMessage)
                .setDefaultValue("Closed the game.")
                .setSaveConsumer(newValue -> ModConfig.discordCloseGameMessage = newValue)
                .setTooltip(Text.of("""
                        Message after closing game. Leave empty for no message.
                        Can be used with <PLAYER>"""))
                .build()
        );
        discord.addEntry(entryBuilder
                .startTextField(Text.of("Join Server Message"),ModConfig.discordJoinServerMessage)
                .setDefaultValue("Joined server <SERVER>.")
                .setSaveConsumer(newValue -> ModConfig.discordJoinServerMessage = newValue)
                .setTooltip(Text.of("""
                        Message after joining servers. Leave empty for no message.
                        Can be used with <PLAYER>"""))
                .build()
        );
        discord.addEntry(entryBuilder
                .startTextField(Text.of("Leave Server Message"),ModConfig.discordLeaveServerMessage)
                .setDefaultValue("Left server <SERVER>.")
                .setSaveConsumer(newValue -> ModConfig.discordLeaveServerMessage = newValue)
                .setTooltip(Text.of("""
                        Message after leaving servers. Leave empty for no message.
                        Can be used with <PLAYER>"""))
                .build()
        );
        discord.addEntry(entryBuilder
                .startTextDescription(Text.of("Mineplay:"))
                .build()
        );
        discord.addEntry(entryBuilder
                .startStrList(Text.of("Custom Messages"), ModConfig.discordMineplayPrivacy)
                .setDefaultValue(List.of("Hello", "Goodbye"))
                .setSaveConsumer(newValue -> ModConfig.discordMineplayPrivacy = newValue)
                .build());

        builder.setSavingRunnable(ModConfig::save);
        return builder.build();
    }
}
