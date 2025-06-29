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
                .setTooltip(Text.translatable("edwipmod.config.kick.letter.tool_tip")
                        .append("\n\n").append(Text.translatable("edwipmod.prefix.letter")))
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
                .setTooltip(Text.translatable("edwipmod.config.kick.letter.second_command.letter.tool_tip")
                        .append("\n\n").append(Text.translatable("edwipmod.prefix.letter")))
                .build()
        );

        kick.addEntry(entryBuilder
                .startIntSlider(Text.translatable("edwipmod.config.kick.letter.second_command.time"), ModConfig.kickSecondTime, 0, 1000/20)
                .setDefaultValue(500/20)
                .setSaveConsumer(newValue -> ModConfig.kickSecondTime = newValue)
                .setTooltip(Text.translatable("edwipmod.config.kick.letter.second_command.time.tool_tip"))
                .build()
        );
        // Warn
        ConfigCategory warn = builder.getOrCreateCategory(Text.translatable("edwipmod.config.warn.title"));
        warn.addEntry(entryBuilder
                .startTextDescription(Text.translatable("edwipmod.title.general"))
                .build()
        );
        warn.addEntry(entryBuilder
                .startTextField(Text.translatable("edwipmod.config.warn.reason"), ModConfig.warnPrefix)
                .setDefaultValue("Please stop <REASON>, if you continue you will be banned")
                .setSaveConsumer(newValue -> ModConfig.warnPrefix = newValue)
                .setTooltip(Text.translatable("edwipmod.config.warn.reason.tool_tip"))
                .build()
        );
        warn.addEntry(entryBuilder
                .startEnumSelector(Text.translatable("edwipmod.config.warn.letter"), Prefixes.lettersCapitalization.class, ModConfig.warnReasonLetters)
                .setDefaultValue(Prefixes.lettersCapitalization.LOWER_ALL)
                .setSaveConsumer(newValue -> ModConfig.warnReasonLetters = newValue)
                .setTooltip(Text.translatable("edwipmod.config.warn.letter.tool_tip")
                        .append("\n\n").append(Text.translatable("edwipmod.prefix.letter")))
                .build()
        );

        // Discord Chat Log
        ConfigCategory discord = builder.getOrCreateCategory(Text.translatable("edwipmod.config.discord.category"));
        discord.addEntry(entryBuilder
                .startTextDescription(Text.translatable("edwipmod.title.general"))
                .build());

        discord.addEntry(entryBuilder
                .startTextField(Text.translatable("edwipmod.config.discord.webhook"), ModConfig.discordMainWebhookUrl)
                .setDefaultValue("")
                .setSaveConsumer(newValue -> ModConfig.discordMainWebhookUrl = newValue)
                .build());

        discord.addEntry(entryBuilder
                .startTextField(Text.translatable("edwipmod.config.discord.timestamp"), ModConfig.discordTimeStamp)
                .setDefaultValue("<t:TIMESTAMP:T>")
                .setSaveConsumer(newValue -> ModConfig.discordTimeStamp = newValue)
                .setTooltip(Text.translatable("edwipmod.config.discord.timestamp.tooltip")
                        .append("\n").append(Text.translatable("edwipmod.prefix.discord.discord_format")))
                .build());

        discord.addEntry(entryBuilder
                .startTextField(Text.translatable("edwipmod.config.discord.username"), ModConfig.discordUserName)
                .setDefaultValue("You")
                .setSaveConsumer(newValue -> ModConfig.discordUserName = newValue)
                .setTooltip(Text.translatable("edwipmod.config.discord.username.tooltip")
                        .append("\n").append(Text.translatable("edwipmod.prefix.discord.discord_format")))
                .build());

        discord.addEntry(entryBuilder
                .startTextField(Text.translatable("edwipmod.config.discord.open"), ModConfig.discordOpenGameMessage)
                .setDefaultValue("Opened the game.")
                .setSaveConsumer(newValue -> ModConfig.discordOpenGameMessage = newValue)
                .setTooltip(Text.translatable("edwipmod.config.discord.open.tooltip")
                        .append("\n").append(Text.translatable("edwipmod.prefix.discord.no_message"))
                        .append("\n").append(Text.translatable("edwipmod.prefix.discord.player"))
                        .append("\n").append(Text.translatable("edwipmod.prefix.discord.discord_format")))
                .build());

        discord.addEntry(entryBuilder
                .startTextField(Text.translatable("edwipmod.config.discord.close"), ModConfig.discordCloseGameMessage)
                .setDefaultValue("Closed the game.")
                .setSaveConsumer(newValue -> ModConfig.discordCloseGameMessage = newValue)
                .setTooltip(Text.translatable("edwipmod.config.discord.close.tooltip")
                        .append("\n").append(Text.translatable("edwipmod.prefix.discord.no_message"))
                        .append("\n").append(Text.translatable("edwipmod.prefix.discord.player"))
                        .append("\n").append(Text.translatable("edwipmod.prefix.discord.discord_format")))
                .build());

        discord.addEntry(entryBuilder
                .startTextField(Text.translatable("edwipmod.config.discord.join"), ModConfig.discordJoinServerMessage)
                .setDefaultValue("Joined server <SERVER>.")
                .setSaveConsumer(newValue -> ModConfig.discordJoinServerMessage = newValue)
                .setTooltip(Text.translatable("edwipmod.config.discord.join.tooltip")
                        .append("\n").append(Text.translatable("edwipmod.prefix.discord.no_message"))
                        .append("\n").append(Text.translatable("edwipmod.prefix.discord.player"))
                        .append("\n").append(Text.translatable("edwipmod.prefix.discord.discord_format")))
                .build());

        discord.addEntry(entryBuilder
                .startTextField(Text.translatable("edwipmod.config.discord.leave"), ModConfig.discordLeaveServerMessage)
                .setDefaultValue("Left server <SERVER>.")
                .setSaveConsumer(newValue -> ModConfig.discordLeaveServerMessage = newValue)
                .setTooltip(Text.translatable("edwipmod.config.discord.leave.tooltip")
                        .append("\n").append(Text.translatable("edwipmod.prefix.discord.no_message"))
                        .append("\n").append(Text.translatable("edwipmod.prefix.discord.player"))
                        .append("\n").append(Text.translatable("edwipmod.prefix.discord.discord_format")))
                .build());

        discord.addEntry(entryBuilder
                .startTextDescription(Text.translatable("edwipmod.title.mineplay"))
                .build());

        discord.addEntry(entryBuilder
                .startStrList(Text.translatable("edwipmod.config.discord.exclude"), ModConfig.discordMineplayPrivacy)
                .setDefaultValue(Prefixes.skipPatterns)
                .setSaveConsumer(newValue -> ModConfig.discordMineplayPrivacy = newValue)
                .setTooltip(Text.translatable("edwipmod.config.discord.exclude.tooltip"))
                .build());

        discord.addEntry(entryBuilder
                .startStrList(Text.translatable("edwipmod.config.discord.matchers"), ModConfig.regexPatterns)
                .setDefaultValue(Prefixes.inputRegex)
                .setSaveConsumer(newValue -> ModConfig.regexPatterns = newValue)
                .build());

        discord.addEntry(entryBuilder
                .startStrList(Text.translatable("edwipmod.config.discord.outputs"), ModConfig.regexResults)
                .setDefaultValue(Prefixes.outputFormat)
                .setSaveConsumer(newValue -> ModConfig.regexResults = newValue)
                .setTooltip(Text.translatable("edwipmod.config.discord.outputs.tooltip"))
                .build());


        builder.setSavingRunnable(ModConfig::save);
        return builder.build();
    }
}
