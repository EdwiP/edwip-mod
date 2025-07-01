package com.edwip.Addons;

import com.edwip.Main;
import com.edwip.Menu.ModConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkinCloner {
    private static final Set<UUID> processedPlayers = new HashSet<>();
    private static final Set<String> existingNames = new HashSet<>();
    private static boolean tracking = false;
    private static Path currentMonthFolder;

    public static void doSkinCloner() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && client.world != null && ModConfig.enableSkinCloner && !ModConfig.disableAll) {
                if (!tracking) {
                    tracking = true;
                    processedPlayers.clear();
                    existingNames.clear();

                    String monthFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM"));
                    currentMonthFolder = Paths.get("skins", monthFolder);

                    try {
                        Files.createDirectories(currentMonthFolder);
                        loadExistingSkinNames(currentMonthFolder);
                    } catch (IOException e) {
                        //LOGGER.error("Failed to prepare skin folder: {}", currentMonthFolder, e);
                        return;
                    }

                    Main.LOGGER.info("Joined world. Scanning visible skins...");
                    saveSkinsFromWorldEntities(client);
                } else {
                    for (PlayerEntity player : client.world.getPlayers()) {
                        if (!processedPlayers.contains(player.getUuid())) {
                            saveSkinFromEntity(client, player);
                        }
                    }
                }
            } else {
                tracking = false;
                processedPlayers.clear();
            }
        });
    }

    private static void loadExistingSkinNames(Path folderPath) throws IOException {
        Pattern pattern = Pattern.compile("^(.*?)(?: \\(\\d+\\))?\\.png$");

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath, "*.png")) {
            for (Path file : stream) {
                String fileName = file.getFileName().toString();
                Matcher matcher = pattern.matcher(fileName);
                if (matcher.matches()) {
                    String baseName = matcher.group(1);
                    existingNames.add(baseName);
                }
            }
        }
    }

    private static void saveSkinsFromWorldEntities(MinecraftClient client) {
        new Thread(() -> {
            if (client.world == null) return;

            for (PlayerEntity player : client.world.getPlayers()) {
                saveFromEntity(currentMonthFolder, client, player);
                try {
                    Thread.sleep(100); // optional delay
                } catch (InterruptedException ignored) {
                }
            }
        }).start();
    }

    private static void saveSkinFromEntity(MinecraftClient client, PlayerEntity player) {
        new Thread(() -> saveFromEntity(currentMonthFolder, client, player)).start();
    }

    private static void saveFromEntity(Path folderPath, MinecraftClient client, PlayerEntity player) {
        if (!(player instanceof AbstractClientPlayerEntity clientPlayer)) return;
        if (!player.isAlive() || player.isInvisible()) return;

        UUID uuid = player.getUuid();
        String name = player.getName().getString();

        if (processedPlayers.contains(uuid) || existingNames.contains(name)) return;

        Identifier skinId = clientPlayer.getSkinTextures().texture();
        var texture = client.getTextureManager().getTexture(skinId);

        if (texture instanceof NativeImageBackedTexture nativeTex) {
            NativeImage nativeImage = nativeTex.getImage();
            if (nativeImage != null) {
                BufferedImage image = nativeToBufferedImage(nativeImage);
                saveSkinWithSuffix(folderPath, name, image);
                processedPlayers.add(uuid);
            }
        }
    }

    private static BufferedImage nativeToBufferedImage(NativeImage nativeImage) {
        BufferedImage buffered = new BufferedImage(
                nativeImage.getWidth(),
                nativeImage.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        for (int x = 0; x < nativeImage.getWidth(); x++) {
            for (int y = 0; y < nativeImage.getHeight(); y++) {
                int color = nativeImage.getColorArgb(x, y);
                buffered.setRGB(x, y, color);
            }
        }
        return buffered;
    }

    private static void saveSkinWithSuffix(Path folderPath, String baseName, BufferedImage newImage) {
        try {
            int suffix = 1;
            String name = baseName;
            Path file = folderPath.resolve(name + ".png");

            while (Files.exists(file)) {
                BufferedImage existing = ImageIO.read(file.toFile());
                if (imagesAreEqual(existing, newImage)) {
                    // Same image, skip saving
                    return;
                }
                suffix++;
                name = baseName + " (" + suffix + ")";
                file = folderPath.resolve(name + ".png");
            }

            ImageIO.write(newImage, "PNG", file.toFile());
            Main.LOGGER.info("Saved skin: {}", file);
            existingNames.add(baseName); // baseName is always the raw name

        } catch (IOException e) {
            //LOGGER.error("Failed to save skin for {}", baseName, e);
        }
    }

    private static boolean imagesAreEqual(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight())
            return false;

        for (int x = 0; x < img1.getWidth(); x++) {
            for (int y = 0; y < img1.getHeight(); y++) {
                if (img1.getRGB(x, y) != img2.getRGB(x, y))
                    return false;
            }
        }
        return true;
    }
}
