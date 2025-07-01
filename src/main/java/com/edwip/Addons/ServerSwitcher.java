package com.edwip.Addons;

import com.edwip.Main;
import com.edwip.Menu.ModConfig;
import com.edwip.Utils.SendMessages;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerSwitcher {
    private static final Pattern alreadyConnected = Pattern.compile("^You are already connected to this server!$");
    private static final Pattern notExist = Pattern.compile("^The specified server (.*?) does not exist.$");
    private static final Pattern serverConnect = Pattern.compile("^\\[Mineplay] Do /blocks to choose your blocks!$");
    private static final Pattern unableToConnect = Pattern.compile("^Unable to connect you to (.*?). Please try again later.$");

    private static boolean allowToPerform = false;
    private static String server = "lobby";
    private static boolean isTeleportable = false;
    private static String oldServer = "lobby";
    private static boolean isTeleporting = false;
    private static final List<String> failedServers = new ArrayList<>();

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.

    private static void handleMessage(String message) {
        if (ModConfig.disableAll || ModConfig.serverSwitcherPrefix.isEmpty() || !allowToPerform) return;
        new Thread(() -> {
            Matcher matcher = Pattern.compile("PREFIX (mineplay-\\d+|lobby)".replace("PREFIX", ModConfig.serverSwitcherPrefix)).matcher(message);
            Main.LOGGER.warn(matcher.toString());
            if (matcher.find() && !isTeleporting) {
                String target = matcher.group(1);

                // Pick a random message
                String randomMessage = getRandomTemplate(ModConfig.serverSwitcherResponse).replace("<SERVER>", target);

                SendMessages.sendMessage(randomMessage);
                SendMessages.sendMessage("/server " + target);
                server = target;
                isTeleporting = true;
                try {
                    Thread.sleep(ModConfig.serverSwitcherDelayBeforeError / 20 * 1000L);
                    if (isTeleporting) {
                        isTeleporting = false;
                        SendMessages.sendMessage(getRandomTemplate(ModConfig.serverSwitcherError).replace("<SERVER>", target));
                    }
                } catch (Exception exception) {
                    //exception.printStackTrace();
                    Main.LOGGER.error("Error while trying to teleport", exception);
                }
            }

            matcher = alreadyConnected.matcher(message);
            if (matcher.find()) {
                SendMessages.sendMessage(getRandomTemplate(ModConfig.serverSwitcherAlreadyHere).replace("<SERVER>", oldServer));
                server = oldServer;
                isTeleporting = false;
            }

            matcher = notExist.matcher(message);
            if (matcher.find()) {
                isTeleporting = false;
                SendMessages.sendMessage(getRandomTemplate(ModConfig.serverSwitcherNotExist).replace("<SERVER>", server));
                server = oldServer;
            }

            matcher = serverConnect.matcher(message);
            if (matcher.find()) {
                oldServer = server;
                if (isTeleporting) {
                    isTeleporting = false;
                    SendMessages.sendMessage(getRandomTemplate(ModConfig.serverSwitcherTeleported).replace("<SERVER>", oldServer));
                }
            }

            matcher = unableToConnect.matcher(message);
            if (matcher.find()) {
                failedServers.add(matcher.group(1));
                tryAutoSwitch(MinecraftClient.getInstance().currentScreen);
            }
        }).start();
    }

    public static void doSwitchServer() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayConnectionEvents.JOIN.register((clientPlayNetworkHandler, packetSender, minecraftClient) -> {
            Main.LOGGER.warn(Objects.requireNonNull(minecraftClient.getCurrentServerEntry()).address);
            allowToPerform = Objects.requireNonNull(minecraftClient.getCurrentServerEntry()).address.equals("mc.mineplay.nl")
                    || minecraftClient.getCurrentServerEntry().address.equals("pe.mineplay.nl");
            if (ModConfig.disableAll || !ModConfig.serverSwitcherAutoSwitch || !allowToPerform) return;
            Main.LOGGER.warn("This");
            new Thread(() -> {
                try {
                    Thread.sleep(ModConfig.serverSwitcherAutoSwitchDelay / 20 * 1000L);
                    Main.LOGGER.warn("Why");
                    assert client.player != null;
                    PlayerInventory playerInventory = client.player.getInventory();
                    playerInventory.setSelectedSlot(4);
                    if (playerInventory.getSelectedStack().getItem().equals(Items.COMPASS)) {
                        assert client.interactionManager != null;
                        isTeleportable = true;
                        client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);
                    }
                } catch (InterruptedException exception) {
                    //exception.printStackTrace();
                    Main.LOGGER.error("Error while trying to switch server", exception);
                }
            }).start();
        });
        ClientPlayConnectionEvents.DISCONNECT.register((clientPlayNetworkHandler, minecraftClient) -> {
            allowToPerform = false;
            isTeleportable = false;
            isTeleporting = false;
            server = "lobby";
            oldServer = "lobby";
            failedServers.clear();
        });
        ScreenEvents.AFTER_INIT.register((minecraftClient, screen, scaledWidth, scaledHeight) -> {
            tryAutoSwitch(screen);
        });
        ClientReceiveMessageEvents.GAME.register((text, overlay) -> {
            handleMessage(text.getString());
        });
        ClientReceiveMessageEvents.CHAT.register((text, signedMessage, gameProfile, parameters, instant) -> {
            handleMessage(text.getString());
        });
    }

    private static void tryAutoSwitch(Screen screen) {
        if (ModConfig.disableAll || !ModConfig.serverSwitcherAutoSwitch || !allowToPerform) return;
        new Thread(() -> {
            try {
                if (screen instanceof GenericContainerScreen genericContainerScreen) {
                    if (!isTeleportable) return;
                    isTeleportable = false;
                    ItemStack result = ItemStack.EMPTY;
                    int chestSlotCount = genericContainerScreen.getScreenHandler().slots.size() - 36;
                    Thread.sleep(100);
                    for (int i = chestSlotCount - 1; i >= 0; i--) {
                        Slot slot = genericContainerScreen.getScreenHandler().slots.get(i);
                        ItemStack stack = slot.getStack();
                        Main.LOGGER.warn(stack.getFormattedName().getString());
                        if (!stack.isEmpty() && stack.getItem() == Items.GRASS_BLOCK) {
                            result = stack;
                            if (failedServers.contains(result.getFormattedName().getString())) continue;
                            SendMessages.sendMessage("/server " + result.getFormattedName().getString());
                            Main.LOGGER.warn(result.getFormattedName().getString());
                            break;
                        }
                    }
                }
            } catch (Exception exception) {
                //exception.printStackTrace();
                Main.LOGGER.error("Error while trying to auto switch server", exception);
            }
        }).start();
    }

    private static String getRandomTemplate(List<String> list) {
        if (list.isEmpty()) return "";
        Random random = new Random();
        return list.get(random.nextInt(list.size()));
    }
}