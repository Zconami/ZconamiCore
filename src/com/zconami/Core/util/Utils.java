package com.zconami.Core.util;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;

import com.zconami.Core.ZconamiPlugin;

public class Utils {

    // ===================================
    // CONSTANTS
    // ===================================

    public static final int TICKS_PER_SECOND = 20;
    private static final int PAGE_SIZE = 5;

    // ===================================
    // CONSTRUCTORS
    // ===================================

    private Utils() {
    }

    // ===================================
    // PUBLIC METHODS
    // ===================================

    public static Optional<OfflinePlayer> getPlayer(String name) {
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (name.equalsIgnoreCase(offlinePlayer.getName())) {
                return Optional.of(offlinePlayer);
            }
        }
        return Optional.empty();
    }

    public static OfflinePlayer getPlayer(UUID uuid) {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public static int ticksFromSeconds(int seconds) {
        return seconds * TICKS_PER_SECOND;
    }

    public static Logger getLogger(String pluginName) {
        return getPlugin(pluginName).getLogger();
    }

    public static void broadcastMessage(String from, String message) {
        Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "[" + from + "->Server] " + ChatColor.WHITE + message);
    }

    public static void sendMessage(CommandSender sender, String from, String message) {
        sender.sendMessage(ChatColor.DARK_GRAY + "[" + from + "->You] " + ChatColor.WHITE + message);
    }

    public static <I> void sendTable(CommandSender sender, int pageNumber, String title, String description,
            List<I> allItems, ItemCallback<I> callback) {

        sender.sendMessage(makeHeader(title));
        sender.sendMessage(ChatColor.GOLD + description);
        sender.sendMessage(ChatColor.GOLD + "");

        final List<I> currentPage;
        if (allItems.size() <= PAGE_SIZE) {
            pageNumber = 1;
            currentPage = allItems;
        } else {
            final int pageStart = (pageNumber - 1) * PAGE_SIZE;
            currentPage = allItems.subList(pageStart, pageStart + PAGE_SIZE);
        }

        final List<String> entries = currentPage.stream().map(callback::itemEntry).collect(Collectors.toList());
        // Page number is NOT 0 indexed, so correct
        int entryIndex = (PAGE_SIZE * (pageNumber - 1)) + 1;
        for (String entry : entries) {
            entry = String.format(" %d: %s", entryIndex, entry);
            sender.sendMessage(entry);
            entryIndex++;
        }

        int requiredBlankLines = PAGE_SIZE - currentPage.size();
        for (int i = 0; i < requiredBlankLines; i++) {
            sender.sendMessage(ChatColor.GOLD + "");
        }

        sender.sendMessage(ChatColor.GOLD + "");
        sender.sendMessage(ChatColor.GOLD + " Page " + pageNumber + " of " + Math.max(allItems.size() / PAGE_SIZE, 1));

    }

    public static boolean isSignBlock(Block block) {
        return block.getState() instanceof Sign;
    }

    public static BukkitScheduler getScheduler() {
        return Bukkit.getScheduler();
    }

    public static Collection<? extends Player> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers();
    }

    public static ScoreboardManager getScoreboardManager() {
        return Bukkit.getScoreboardManager();
    }

    public static FileConfiguration getPluginConfig(String pluginName) {
        return getPlugin(pluginName).getConfig();
    }

    public static ZconamiPlugin getPlugin(String pluginName) {
        return (ZconamiPlugin) Bukkit.getServer().getPluginManager().getPlugin(pluginName);
    }

    public static Inventory getRemoteInventory(final String title) {
        return Bukkit.createInventory(null, 9, title);
    }

    private static String makeHeader(String text) {
        final StringBuilder stringBuilder = new StringBuilder(ChatColor.GOLD + "▀▀▀ " + text + " ");
        for (int i = 0; i < 25 - text.length(); i++) {
            stringBuilder.append("▀");
        }
        return stringBuilder.toString();
    }

}
