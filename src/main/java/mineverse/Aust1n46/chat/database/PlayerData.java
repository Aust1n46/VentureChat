package mineverse.Aust1n46.chat.database;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.command.mute.MuteContainer;
import mineverse.Aust1n46.chat.utilities.Format;
import mineverse.Aust1n46.chat.utilities.UUIDFetcher;

/**
 * Class for reading and writing player data.
 *
 * @author Aust1n46
 */
public class PlayerData {
    private static MineverseChat plugin = MineverseChat.getInstance();
    private static final String PLAYER_DATA_DIRECTORY_PATH = plugin.getDataFolder().getAbsolutePath() + "/PlayerData";

    public static void loadLegacyPlayerData() {
        File legacyPlayerDataFile = new File(plugin.getDataFolder().getAbsolutePath(), "Players.yml");
        if (!legacyPlayerDataFile.isFile()) {
            return;
        }
        try {
            Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&c - Detected Legacy Player Data!"));
            Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&c - Converting to new structure and deleting old Players.yml file!"));
            FileConfiguration playerData = YamlConfiguration.loadConfiguration(legacyPlayerDataFile);
            for (String uuidString : playerData.getConfigurationSection("players").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidString);
                if (UUIDFetcher.shouldSkipOfflineUUID(uuid)) {
                    Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&c - Skipping Offline UUID: " + uuid));
                    continue;
                }
                String name = playerData.getConfigurationSection("players." + uuid).getString("name");
                String currentChannelName = playerData.getConfigurationSection("players." + uuid).getString("current");
                ChatChannel currentChannel = ChatChannel.isChannel(currentChannelName) ? ChatChannel.getChannel(currentChannelName) : ChatChannel.getDefaultChannel();
                Set<UUID> ignores = new HashSet<UUID>();
                StringTokenizer i = new StringTokenizer(playerData.getConfigurationSection("players." + uuidString).getString("ignores"), ",");
                while (i.hasMoreTokens()) {
                    ignores.add(UUID.fromString(i.nextToken()));
                }
                Set<String> listening = new HashSet<String>();
                StringTokenizer l = new StringTokenizer(playerData.getConfigurationSection("players." + uuidString).getString("listen"), ",");
                while (l.hasMoreTokens()) {
                    String channel = l.nextToken();
                    if (ChatChannel.isChannel(channel)) {
                        listening.add(channel);
                    }
                }
                HashMap<String, MuteContainer> mutes = new HashMap<String, MuteContainer>();
                StringTokenizer m = new StringTokenizer(playerData.getConfigurationSection("players." + uuidString).getString("mutes"), ",");
                while (m.hasMoreTokens()) {
                    String[] parts = m.nextToken().split(":");
                    if (ChatChannel.isChannel(parts[0])) {
                        if (parts[1].equals("null")) {
                            Bukkit.getConsoleSender().sendMessage("[VentureChat] Null Mute Time: " + parts[0] + " " + name);
                            continue;
                        }
                        String channelName = parts[0];
                        mutes.put(channelName, new MuteContainer(channelName, Long.parseLong(parts[1])));
                    }
                }
                Set<String> blockedCommands = new HashSet<String>();
                StringTokenizer b = new StringTokenizer(playerData.getConfigurationSection("players." + uuidString).getString("blockedcommands"), ",");
                while (b.hasMoreTokens()) {
                    blockedCommands.add(b.nextToken());
                }
                boolean host = playerData.getConfigurationSection("players." + uuidString).getBoolean("host");
                UUID party = playerData.getConfigurationSection("players." + uuidString).getString("party").length() > 0 ? UUID.fromString(playerData.getConfigurationSection("players." + uuidString).getString("party")) : null;
                boolean filter = playerData.getConfigurationSection("players." + uuidString).getBoolean("filter");
                boolean notifications = playerData.getConfigurationSection("players." + uuidString).getBoolean("notifications");
                String nickname = playerData.getConfigurationSection("players." + uuidString).getString("nickname");
                String jsonFormat = "Default";
                boolean spy = playerData.getConfigurationSection("players." + uuidString).getBoolean("spy", false);
                boolean commandSpy = playerData.getConfigurationSection("players." + uuidString).getBoolean("commandspy", false);
                boolean rangedSpy = playerData.getConfigurationSection("players." + uuidString).getBoolean("rangedspy", false);
                boolean messageToggle = playerData.getConfigurationSection("players." + uuidString).getBoolean("messagetoggle", true);
                boolean bungeeToggle = playerData.getConfigurationSection("players." + uuidString).getBoolean("bungeetoggle", true);
                MineverseChatPlayer mcp = new MineverseChatPlayer(uuid, name, currentChannel, ignores, listening, mutes, blockedCommands, host, party, filter, notifications, nickname, jsonFormat, spy, commandSpy, rangedSpy, messageToggle, bungeeToggle);
                mcp.setModified(true);
                MineverseChatAPI.addMineverseChatPlayerToMap(mcp);
                MineverseChatAPI.addNameToMap(mcp);
            }
        } catch (Exception e) {
            MineverseChatAPI.clearMineverseChatPlayerMap();
            MineverseChatAPI.clearNameMap();
            Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&c - Error Loading Legacy Player Data!"));
            Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&c - Deleted Players.yml file!"));
        } finally {
            legacyPlayerDataFile.delete();
        }
    }

    public static void loadPlayerData() {
        try {
            File playerDataDirectory = new File(PLAYER_DATA_DIRECTORY_PATH);
            if (!playerDataDirectory.exists()) {
                playerDataDirectory.mkdirs();
            }
            Files.walk(Paths.get(PLAYER_DATA_DIRECTORY_PATH))
                    .filter(Files::isRegularFile)
                    .forEach((path) -> readPlayerDataFile(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the player data file for a specific player. Corrupt/invalid data files are skipped and deleted.
     *
     * @param path
     */
    private static void readPlayerDataFile(Path path) {
        MineverseChatPlayer mcp;
        File playerDataFile = path.toFile();
        if (!playerDataFile.exists()) {
            return;
        }
        try {
            FileConfiguration playerDataFileYamlConfiguration = YamlConfiguration.loadConfiguration(playerDataFile);
            String uuidString = playerDataFile.getName().replace(".yml", "");
            UUID uuid = UUID.fromString(uuidString);
            if (UUIDFetcher.shouldSkipOfflineUUID(uuid)) {
                Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&c - Skipping Offline UUID: " + uuid));
                Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&c - File will be skipped and deleted."));
                playerDataFile.delete();
                return;
            }
            String name = playerDataFileYamlConfiguration.getString("name");
            String currentChannelName = playerDataFileYamlConfiguration.getString("current");
            ChatChannel currentChannel = ChatChannel.isChannel(currentChannelName) ? ChatChannel.getChannel(currentChannelName) : ChatChannel.getDefaultChannel();
            Set<UUID> ignores = new HashSet<UUID>();
            StringTokenizer i = new StringTokenizer(playerDataFileYamlConfiguration.getString("ignores"), ",");
            while (i.hasMoreTokens()) {
                ignores.add(UUID.fromString(i.nextToken()));
            }
            Set<String> listening = new HashSet<String>();
            StringTokenizer l = new StringTokenizer(playerDataFileYamlConfiguration.getString("listen"), ",");
            while (l.hasMoreTokens()) {
                String channel = l.nextToken();
                if (ChatChannel.isChannel(channel)) {
                    listening.add(channel);
                }
            }
            HashMap<String, MuteContainer> mutes = new HashMap<String, MuteContainer>();
            ConfigurationSection muteSection = playerDataFileYamlConfiguration.getConfigurationSection("mutes");
            for (String channelName : muteSection.getKeys(false)) {
                ConfigurationSection channelSection = muteSection.getConfigurationSection(channelName);
                mutes.put(channelName, new MuteContainer(channelName, channelSection.getLong("time"), channelSection.getString("reason")));
            }

            Set<String> blockedCommands = new HashSet<String>();
            StringTokenizer b = new StringTokenizer(playerDataFileYamlConfiguration.getString("blockedcommands"), ",");
            while (b.hasMoreTokens()) {
                blockedCommands.add(b.nextToken());
            }
            boolean host = playerDataFileYamlConfiguration.getBoolean("host");
            UUID party = playerDataFileYamlConfiguration.getString("party").length() > 0 ? UUID.fromString(playerDataFileYamlConfiguration.getString("party")) : null;
            boolean filter = playerDataFileYamlConfiguration.getBoolean("filter");
            boolean notifications = playerDataFileYamlConfiguration.getBoolean("notifications");
            String nickname = playerDataFileYamlConfiguration.getString("nickname");
            String jsonFormat = "Default";
            boolean spy = playerDataFileYamlConfiguration.getBoolean("spy", false);
            boolean commandSpy = playerDataFileYamlConfiguration.getBoolean("commandspy", false);
            boolean rangedSpy = playerDataFileYamlConfiguration.getBoolean("rangedspy", false);
            boolean messageToggle = playerDataFileYamlConfiguration.getBoolean("messagetoggle", true);
            boolean bungeeToggle = playerDataFileYamlConfiguration.getBoolean("bungeetoggle", true);
            mcp = new MineverseChatPlayer(uuid, name, currentChannel, ignores, listening, mutes, blockedCommands, host, party, filter, notifications, nickname, jsonFormat, spy, commandSpy, rangedSpy, messageToggle, bungeeToggle);
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&c - Error Loading Data File: " + playerDataFile.getName()));
            Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&c - File will be skipped and deleted."));
            playerDataFile.delete();
            return;
        }
        if (mcp != null) {
            MineverseChatAPI.addMineverseChatPlayerToMap(mcp);
            MineverseChatAPI.addNameToMap(mcp);
        }
    }

    public static void savePlayerData(MineverseChatPlayer mcp) {
        if (mcp == null || UUIDFetcher.shouldSkipOfflineUUID(mcp.getUUID()) || (!mcp.isOnline() && !mcp.wasModified())) {
            return;
        }
        try {
            File playerDataFile = new File(PLAYER_DATA_DIRECTORY_PATH, mcp.getUUID() + ".yml");
            FileConfiguration playerDataFileYamlConfiguration = YamlConfiguration.loadConfiguration(playerDataFile);
            if (!playerDataFile.exists()) {
                playerDataFileYamlConfiguration.save(playerDataFile);
            }

            playerDataFileYamlConfiguration.set("name", mcp.getName());
            playerDataFileYamlConfiguration.set("current", mcp.getCurrentChannel().getName());
            String ignores = "";
            for (UUID s : mcp.getIgnores()) {
                ignores += s.toString() + ",";
            }
            playerDataFileYamlConfiguration.set("ignores", ignores);
            String listening = "";
            for (String channel : mcp.getListening()) {
                ChatChannel c = ChatChannel.getChannel(channel);
                listening += c.getName() + ",";
            }
            String blockedCommands = "";
            for (String s : mcp.getBlockedCommands()) {
                blockedCommands += s + ",";
            }
            if (listening.length() > 0) {
                listening = listening.substring(0, listening.length() - 1);
            }
            playerDataFileYamlConfiguration.set("listen", listening);

            ConfigurationSection muteSection = playerDataFileYamlConfiguration.createSection("mutes");
            for (MuteContainer mute : mcp.getMutes()) {
                ConfigurationSection channelSection = muteSection.createSection(mute.getChannel());
                channelSection.set("time", mute.getDuration());
                channelSection.set("reason", mute.getReason());
            }

            playerDataFileYamlConfiguration.set("blockedcommands", blockedCommands);
            playerDataFileYamlConfiguration.set("host", mcp.isHost());
            playerDataFileYamlConfiguration.set("party", mcp.hasParty() ? mcp.getParty().toString() : "");
            playerDataFileYamlConfiguration.set("filter", mcp.hasFilter());
            playerDataFileYamlConfiguration.set("notifications", mcp.hasNotifications());
            playerDataFileYamlConfiguration.set("nickname", mcp.getNickname());
            playerDataFileYamlConfiguration.set("spy", mcp.isSpy());
            playerDataFileYamlConfiguration.set("commandspy", mcp.hasCommandSpy());
            playerDataFileYamlConfiguration.set("rangedspy", mcp.getRangedSpy());
            playerDataFileYamlConfiguration.set("messagetoggle", mcp.getMessageToggle());
            playerDataFileYamlConfiguration.set("bungeetoggle", mcp.getBungeeToggle());
            Calendar currentDate = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MMM/dd HH:mm:ss");
            String dateNow = formatter.format(currentDate.getTime());
            playerDataFileYamlConfiguration.set("date", dateNow);
            mcp.setModified(false);

            playerDataFileYamlConfiguration.save(playerDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void savePlayerData() {
        for (MineverseChatPlayer p : MineverseChatAPI.getMineverseChatPlayers()) {
            savePlayerData(p);
        }
    }
}
