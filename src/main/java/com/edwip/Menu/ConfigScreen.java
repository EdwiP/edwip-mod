package com.edwip.Menu;

import com.edwip.Utils.Prefixes;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ConfigScreen {
    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("edwipmod.title.title"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // GENERAL CATEGORY
        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("edwipmod.config.general"));
        general.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable("edwipmod.config.general.disable_all"), ModConfig.disableAll)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> {
                    ModConfig.disableAll = newValue;
                    ModConfig.save();
                })
                .setTooltip(Text.translatable("edwipmod.config.general.disable_all.tool_tip"))
                .build());

        general.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable("edwipmod.config.general.kick"), ModConfig.enableKick)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> ModConfig.enableKick = newValue)
                .setTooltip(Text.translatable("edwipmod.config.general.kick.tool_tip"))
                .build());
        general.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable("edwipmod.config.general.warn"), ModConfig.enableWarn)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> ModConfig.enableWarn = newValue)
                .setTooltip(Text.translatable("edwipmod.config.general.warn.tool_tip"))
                .build());
        general.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable("edwipmod.config.general.discord"), ModConfig.enableDiscordChatLog)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> ModConfig.enableDiscordChatLog = newValue)
                .setTooltip(Text.translatable("edwipmod.config.general.discord.tool_tip"))
                .build());


        // Kick Commands
        ConfigCategory kick = builder.getOrCreateCategory(Text.translatable("edwipmod.config.kick.title"));
        kick.addEntry(entryBuilder
                .startTextDescription(Text.translatable("edwipmod.title.general"))
                .build()
        );
        kick.addEntry(entryBuilder
                .startTextField(Text.translatable("edwipmod.config.kick.reason"), ModConfig.kickPrefix)
                .setDefaultValue("<REASON>, if you continue you will be banned")
                .setSaveConsumer(newValue -> ModConfig.kickPrefix = newValue)
                .setTooltip(Text.translatable("edwipmod.config.kick.reason.tool_tip"))
                .build()
        );
        kick.addEntry(entryBuilder
                .startEnumSelector(Text.translatable("edwipmod.config.kick.letter"), Prefixes.lettersCapitalization.class, ModConfig.kickReasonLetters)
                .setDefaultValue(Prefixes.lettersCapitalization.LOWER_ALL)
                .setSaveConsumer(newValue -> ModConfig.kickReasonLetters = newValue)
                .setTooltip(Text.translatable("edwipmod.config.kick.letter.tool_tip").append("\n\n").append(Text.translatable("edwipmod.prefix.letter")))
                .build()
        );
        kick.addEntry(entryBuilder
                .startTextField(Text.translatable("edwipmod.config.kick.letter.second_command"), ModConfig.kickSecondCommand)
                .setDefaultValue("/warn -s <PLAYER> <REASON>, kicked warn")
                .setSaveConsumer(newValue -> ModConfig.kickSecondCommand = newValue)
                .setTooltip(Text.translatable("edwipmod.config.kick.letter.second_command.tool_tip"))
                .build()
        );
        kick.addEntry(entryBuilder
                .startEnumSelector(Text.translatable("edwipmod.config.kick.letter.second_command.letter"), Prefixes.lettersCapitalization.class, ModConfig.kickSecondLetters)
                .setDefaultValue(Prefixes.lettersCapitalization.LOWER_ALL)
                .setSaveConsumer(newValue -> ModConfig.kickSecondLetters = newValue)
                .setTooltip(Text.translatable("edwipmod.config.kick.letter.second_command.letter.tool_tip").append("\n\n").append(Text.translatable("edwipmod.prefix.letter")))
                .build()
        );

        kick.addEntry(entryBuilder
                .startIntSlider(Text.of("Second Command Time"), ModConfig.kickSecondTime, 0, 1000)
                .setDefaultValue(500)
                .setSaveConsumer(newValue -> ModConfig.kickSecondTime = newValue)
                .setTooltip(Text.of("""
                        Time to delay the second command execution (in ms) after kicking."""))
                .build()
        );
        // Warn
        ConfigCategory warn = builder.getOrCreateCategory(Text.of("Warn Settings"));
        warn.addEntry(entryBuilder
                .startTextDescription(Text.of("General:"))
                .build()
        );
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
                .startEnumSelector(Text.of("Warn Letters Capitalization"), Prefixes.lettersCapitalization.class, ModConfig.warnReasonLetters)
                .setDefaultValue(Prefixes.lettersCapitalization.LOWER_ALL)
                .setSaveConsumer(newValue -> ModConfig.warnReasonLetters = newValue)
                .setTooltip(Text.of("""
                        Change letters capitalization.
                        
                        Example: heLLo WOrlD!
                        Lower All -> hello world!
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
                .startTextField(Text.of("Time Stamp"), ModConfig.discordTimeStamp)
                .setDefaultValue("<t:TIMESTAMP:T>")
                .setSaveConsumer(newValue -> ModConfig.discordTimeStamp = newValue)
                .setTooltip(Text.of("""
                        Time stamp format at the beginning of each message. Leave empty for no time stamp.
                        Can be used with Discord format."""))
                .build()
        );
        discord.addEntry(entryBuilder
                .startTextField(Text.of("User Name"), ModConfig.discordUserName)
                .setDefaultValue("You")
                .setSaveConsumer(newValue -> ModConfig.discordUserName = newValue)
                .setTooltip(Text.of("""
                        Replace <PLAYER>. Can be used in 4 options below.
                        Can be used with Discord format."""))
                .build()
        );
        discord.addEntry(entryBuilder
                .startTextField(Text.of("Open Game Message"), ModConfig.discordOpenGameMessage)
                .setDefaultValue("Opened the game.")
                .setSaveConsumer(newValue -> ModConfig.discordOpenGameMessage = newValue)
                .setTooltip(Text.of("""
                        Message after opening game. Leave empty for no message.
                        Can be used with <PLAYER>
                        Can be used with Discord format."""))
                .build()
        );
        discord.addEntry(entryBuilder
                .startTextField(Text.of("Close Game Message"), ModConfig.discordCloseGameMessage)
                .setDefaultValue("Closed the game.")
                .setSaveConsumer(newValue -> ModConfig.discordCloseGameMessage = newValue)
                .setTooltip(Text.of("""
                        Message after closing game. Leave empty for no message.
                        Can be used with <PLAYER>
                        Can be used with Discord format."""))
                .build()
        );
        discord.addEntry(entryBuilder
                .startTextField(Text.of("Join Server Message"), ModConfig.discordJoinServerMessage)
                .setDefaultValue("Joined server <SERVER>.")
                .setSaveConsumer(newValue -> ModConfig.discordJoinServerMessage = newValue)
                .setTooltip(Text.of("""
                        Message after joining servers. Leave empty for no message.
                        Can be used with <PLAYER> and <SERVER>
                        Can be used with Discord format."""))
                .build()
        );
        discord.addEntry(entryBuilder
                .startTextField(Text.of("Leave Server Message"), ModConfig.discordLeaveServerMessage)
                .setDefaultValue("Left server <SERVER>.")
                .setSaveConsumer(newValue -> ModConfig.discordLeaveServerMessage = newValue)
                .setTooltip(Text.of("""
                        Message after leaving servers. Leave empty for no message.
                        Can be used with <PLAYER> and <SERVER>
                        Can be used with Discord format."""))
                .build()
        );
        discord.addEntry(entryBuilder
                .startTextDescription(Text.of("Mineplay:"))
                .build()
        );
        discord.addEntry(entryBuilder
                .startStrList(Text.of("Exclude Regexes"), ModConfig.discordMineplayPrivacy)
                .setDefaultValue(Prefixes.skipPatterns)
                .setSaveConsumer(newValue -> ModConfig.discordMineplayPrivacy = newValue)
                .setTooltip(Text.of("""
                        Exclude messages with matched regexes.
                        Visit regex101.com to know more about regex."""))
                .build());

        discord.addEntry(entryBuilder
                .startStrList(Text.of("Regex Matchers"), ModConfig.regexPatterns)
                .setDefaultValue(Prefixes.inputRegex)
                .setSaveConsumer(newValue -> ModConfig.regexPatterns = newValue)
                .build());

        discord.addEntry(entryBuilder
                .startStrList(Text.of("Regex Outputs"), ModConfig.regexResults)
                .setDefaultValue(Prefixes.outputFormat)
                .setSaveConsumer(newValue -> ModConfig.regexResults = newValue)
                .setTooltip(Text.of("""
                        Can be used with Discord format. Each group is captured by (1), (2), ect."""))
                .build());

        builder.setSavingRunnable(ModConfig::save);
        return builder.build();
    }
}
