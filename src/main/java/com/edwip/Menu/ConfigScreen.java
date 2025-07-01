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
                .startBooleanToggle(Text.translatable("edwipmod.config.general.server_switch"), ModConfig.enableServerSwitcher)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> ModConfig.enableServerSwitcher = newValue)
                .setTooltip(Text.translatable("edwipmod.config.general.server_switch.tool_tip"))
                .build());
        general.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable("edwipmod.config.general.discord"), ModConfig.enableDiscordChatLog)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> ModConfig.enableDiscordChatLog = newValue)
                .setTooltip(Text.translatable("edwipmod.config.general.discord.tool_tip"))
                .build());
        general.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable("edwipmod.config.general.chats_writer"), ModConfig.enableChatsWriter)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> ModConfig.enableChatsWriter = newValue)
                .setTooltip(Text.translatable("edwipmod.config.general.chats_writer.tool_tip"))
                .build());
        general.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable("edwipmod.config.general.skin_cloner"), ModConfig.enableSkinCloner)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> ModConfig.enableSkinCloner = newValue)
                .setTooltip(Text.translatable("edwipmod.config.general.skin_cloner.tool_tip"))
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
                .startIntSlider(Text.translatable("edwipmod.config.kick.letter.second_command.time"), ModConfig.kickSecondDelay, 0, 1000 / 20)
                .setDefaultValue(500 / 20)
                .setSaveConsumer(newValue -> ModConfig.kickSecondDelay = newValue)
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

        // Server Switcher

        ConfigCategory serverSwitcher = builder.getOrCreateCategory(Text.translatable("edwipmod.config.server_switcher.title"));
        serverSwitcher.addEntry(entryBuilder
                .startTextDescription(Text.translatable("edwipmod.title.general"))
                .build());

        serverSwitcher.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable("edwipmod.config.server_switcher.auto_switch"),ModConfig.serverSwitcherAutoSwitch)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> ModConfig.serverSwitcherAutoSwitch = newValue)
                .setTooltip(Text.translatable("edwipmod.config.server_switcher.auto_switch.tool_tip"))
                .build());

        serverSwitcher.addEntry(entryBuilder
                .startIntSlider(Text.translatable("edwipmod.config.server_switcher.auto_switch.delay"),ModConfig.serverSwitcherAutoSwitchDelay,0,20)
                .setDefaultValue(5)
                        .setSaveConsumer(newValue -> ModConfig.serverSwitcherAutoSwitchDelay = newValue)
                        .setTooltip(Text.translatable("edwipmod.config.server_switcher.auto_switch.delay.tool_tip"))
                .build());

        serverSwitcher.addEntry(entryBuilder
                .startIntSlider(Text.translatable("edwipmod.config.server_switcher.auto_switch.delay_error"),ModConfig.serverSwitcherDelayBeforeError,0,100)
                .setDefaultValue(100)
                .setSaveConsumer(newValue -> ModConfig.serverSwitcherDelayBeforeError = newValue)
                .setTooltip(Text.translatable("edwipmod.config.server_switcher.auto_switch.delay_error.tool_tip"))
                .build()
        );

        serverSwitcher.addEntry(entryBuilder
                .startTextField(Text.translatable("edwipmod.config.server_switcher.auto_switch.prefix"),ModConfig.serverSwitcherPrefix)
                .setDefaultValue("Teleport")
                .setSaveConsumer(newValue -> ModConfig.serverSwitcherPrefix = newValue)
                .setTooltip(Text.translatable("edwipmod.config.server_switcher.auto_switch.prefix.tool_tip"))
                .build());

        serverSwitcher.addEntry(entryBuilder
                .startStrList(Text.translatable("edwipmod.config.server_switcher.auto_switch.response"),ModConfig.serverSwitcherResponse)
                .setDefaultValue(Prefixes.serverSwitcherResponse)
                .setSaveConsumer(newValue -> ModConfig.serverSwitcherResponse = newValue)
                .setTooltip(Text.translatable("edwipmod.config.server_switcher.auto_switch.response.tool_tip")
                        .append("\n").append(Text.translatable("edwipmod.prefix.server_switcher.server")))
                .build());

        serverSwitcher.addEntry(entryBuilder
                .startStrList(Text.translatable("edwipmod.config.server_switcher.auto_switch.teleported"),ModConfig.serverSwitcherTeleported)
                .setDefaultValue(Prefixes.serverSwitcherTeleported)
                .setSaveConsumer(newValue -> ModConfig.serverSwitcherTeleported = newValue)
                .setTooltip(Text.translatable("edwipmod.config.server_switcher.auto_switch.teleported.tool_tip")
                        .append("\n").append(Text.translatable("edwipmod.prefix.server_switcher.server")))
                .build());

        serverSwitcher.addEntry(entryBuilder
                .startStrList(Text.translatable("edwipmod.config.server_switcher.auto_switch.not_exist"),ModConfig.serverSwitcherNotExist)
                .setDefaultValue(Prefixes.serverSwitcherNotExist)
                .setSaveConsumer(newValue -> ModConfig.serverSwitcherNotExist = newValue)
                .setTooltip(Text.translatable("edwipmod.config.server_switcher.auto_switch.teleported.tool_tip")
                        .append("\n").append(Text.translatable("edwipmod.prefix.server_switcher.server")))
                .build());

        serverSwitcher.addEntry(entryBuilder
                .startStrList(Text.translatable("edwipmod.config.server_switcher.auto_switch.already_here"),ModConfig.serverSwitcherAlreadyHere)
                .setDefaultValue(Prefixes.serverSwitcherAlreadyHere)
                .setSaveConsumer(newValue -> ModConfig.serverSwitcherAlreadyHere = newValue)
                .setTooltip(Text.translatable("edwipmod.config.server_switcher.auto_switch.already_here.tool_tip")
                        .append("\n").append(Text.translatable("edwipmod.prefix.server_switcher.server")))
                .build());

        serverSwitcher.addEntry(entryBuilder
                .startStrList(Text.translatable("edwipmod.config.server_switcher.auto_switch.error"),ModConfig.serverSwitcherError)
                .setDefaultValue(Prefixes.serverSwitcherError)
                .setSaveConsumer(newValue -> ModConfig.serverSwitcherError = newValue)
                .setTooltip(Text.translatable("edwipmod.config.server_switcher.auto_switch.error.tool_tip")
                        .append("\n").append(Text.translatable("edwipmod.prefix.server_switcher.server")))
                .build());


        // Discord Chat Log
        ConfigCategory discord = builder.getOrCreateCategory(Text.translatable("edwipmod.config.discord.title"));
        discord.addEntry(entryBuilder
                .startTextDescription(Text.translatable("edwipmod.title.general"))
                .build());

        discord.addEntry(entryBuilder
                .startTextField(Text.translatable("edwipmod.config.discord.webhook"), ModConfig.discordMainWebhookUrl)
                .setDefaultValue("")
                .setSaveConsumer(newValue -> ModConfig.discordMainWebhookUrl = newValue)
                .build());

        discord.addEntry(entryBuilder
                        .startIntSlider(Text.translatable("edwipmod.config.discord.delay"),ModConfig.discordDelay,5,20)
                        .setDefaultValue(10)
                        .setSaveConsumer(newValue -> ModConfig.discordDelay = newValue)
                        .setTooltip(Text.translatable("edwipmod.config.discord.delay.tool_tip"))
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
                        .append("\n").append(Text.translatable("edwipmod.prefix.no_message"))
                        .append("\n").append(Text.translatable("edwipmod.prefix.discord.player"))
                        .append("\n").append(Text.translatable("edwipmod.prefix.discord.discord_format")))
                .build());

        discord.addEntry(entryBuilder
                .startTextField(Text.translatable("edwipmod.config.discord.close"), ModConfig.discordCloseGameMessage)
                .setDefaultValue("Closed the game.")
                .setSaveConsumer(newValue -> ModConfig.discordCloseGameMessage = newValue)
                .setTooltip(Text.translatable("edwipmod.config.discord.close.tooltip")
                        .append("\n").append(Text.translatable("edwipmod.prefix.no_message"))
                        .append("\n").append(Text.translatable("edwipmod.prefix.discord.player"))
                        .append("\n").append(Text.translatable("edwipmod.prefix.discord.discord_format")))
                .build());

        discord.addEntry(entryBuilder
                .startTextField(Text.translatable("edwipmod.config.discord.join"), ModConfig.discordJoinServerMessage)
                .setDefaultValue("Joined server <SERVER>.")
                .setSaveConsumer(newValue -> ModConfig.discordJoinServerMessage = newValue)
                .setTooltip(Text.translatable("edwipmod.config.discord.join.tooltip")
                        .append("\n").append(Text.translatable("edwipmod.prefix.no_message"))
                        .append("\n").append(Text.translatable("edwipmod.prefix.discord.player"))
                        .append("\n").append(Text.translatable("edwipmod.prefix.discord.discord_format")))
                .build());

        discord.addEntry(entryBuilder
                .startTextField(Text.translatable("edwipmod.config.discord.leave"), ModConfig.discordLeaveServerMessage)
                .setDefaultValue("Left server <SERVER>.")
                .setSaveConsumer(newValue -> ModConfig.discordLeaveServerMessage = newValue)
                .setTooltip(Text.translatable("edwipmod.config.discord.leave.tooltip")
                        .append("\n").append(Text.translatable("edwipmod.prefix.no_message"))
                        .append("\n").append(Text.translatable("edwipmod.prefix.discord.player"))
                        .append("\n").append(Text.translatable("edwipmod.prefix.discord.discord_format")))
                .build());

        discord.addEntry(entryBuilder
                .startTextDescription(Text.translatable("edwipmod.title.mineplay"))
                .build());

        discord.addEntry(entryBuilder
                .startTextField(Text.translatable("edwipmod.config.discord.moderator_webhook"), ModConfig.discordModeratorWebhookUrl)
                .setDefaultValue("")
                .setSaveConsumer(newValue -> ModConfig.discordModeratorWebhookUrl = newValue)
                .setTooltip(Text.translatable("edwipmod.config.discord.moderator_webhook.tool_tip"))
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

        // Chats Writer
        ConfigCategory chatsWriter = builder.getOrCreateCategory(Text.translatable("edwipmod.config.chats_writer.title"));

        chatsWriter.addEntry(entryBuilder
                .startTextDescription(Text.translatable("edwipmod.title.general"))
                .build());

        chatsWriter.addEntry(entryBuilder
                .startStrField(Text.translatable("edwipmod.config.chats_writer.file_name"), ModConfig.chatsWriterFileName)
                .setDefaultValue("chats")
                .setSaveConsumer(newValue -> ModConfig.chatsWriterFileName = newValue)
                .setTooltip(Text.translatable("edwipmod.config.chats_writer.file_name.tool_tip"))
                .build());

        chatsWriter.addEntry(entryBuilder
                .startStrField(Text.translatable("edwipmod.config.chats_writer.username"), ModConfig.chatsWriterUserName)
                .setDefaultValue("You")
                .setSaveConsumer(newValue -> ModConfig.chatsWriterUserName = newValue)
                .setTooltip(Text.translatable("edwipmod.config.chats_writer.username.tool_tip"))
                .build());

        chatsWriter.addEntry(entryBuilder
                .startStrField(Text.translatable("edwipmod.config.chats_writer.date_format"), ModConfig.chatsWriterDateFormat)
                .setDefaultValue("yyyy/MM/dd HH:mm:ss")
                .setSaveConsumer(newValue -> ModConfig.chatsWriterDateFormat = newValue)
                .setTooltip(Text.translatable("edwipmod.config.chats_writer.date_format.tool_tip")
                        .append("\n").append(Text.translatable("edwipmod.prefix.no_message")))
                .build());


        chatsWriter.addEntry(entryBuilder
                .startTextField(Text.translatable("edwipmod.config.chats_writer.open"), ModConfig.discordOpenGameMessage)
                .setDefaultValue("Opened the game.")
                .setSaveConsumer(newValue -> ModConfig.discordOpenGameMessage = newValue)
                .setTooltip(Text.translatable("edwipmod.config.chats_writer.open.tooltip")
                        .append("\n").append(Text.translatable("edwipmod.prefix.no_message")))
                .build());

        chatsWriter.addEntry(entryBuilder
                .startTextField(Text.translatable("edwipmod.config.chats_writer.close"), ModConfig.discordCloseGameMessage)
                .setDefaultValue("Closed the game.")
                .setSaveConsumer(newValue -> ModConfig.discordCloseGameMessage = newValue)
                .setTooltip(Text.translatable("edwipmod.config.chats_writer.close.tooltip")
                        .append("\n").append(Text.translatable("edwipmod.prefix.no_message")))
                .build());

        chatsWriter.addEntry(entryBuilder
                .startTextField(Text.translatable("edwipmod.config.chats_writer.join"), ModConfig.discordJoinServerMessage)
                .setDefaultValue("Joined server <SERVER>.")
                .setSaveConsumer(newValue -> ModConfig.discordJoinServerMessage = newValue)
                .setTooltip(Text.translatable("edwipmod.config.chats_writer.join.tooltip")
                        .append("\n").append(Text.translatable("edwipmod.prefix.no_message")))
                .build());

        chatsWriter.addEntry(entryBuilder
                .startTextField(Text.translatable("edwipmod.config.chats_writer.leave"), ModConfig.discordLeaveServerMessage)
                .setDefaultValue("Left server <SERVER>.")
                .setSaveConsumer(newValue -> ModConfig.discordLeaveServerMessage = newValue)
                .setTooltip(Text.translatable("edwipmod.config.chats_writer.leave.tooltip")
                        .append("\n").append(Text.translatable("edwipmod.prefix.no_message")))
                .build());

        // Skin Cloner

        builder.setSavingRunnable(ModConfig::save);
        return builder.build();
    }
}
