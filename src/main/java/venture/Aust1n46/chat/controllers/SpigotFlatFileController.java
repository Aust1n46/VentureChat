package venture.Aust1n46.chat.controllers;

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

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import venture.Aust1n46.chat.initiators.application.VentureChat;
import venture.Aust1n46.chat.model.ChatChannel;
import venture.Aust1n46.chat.model.MuteContainer;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.service.ConfigService;
import venture.Aust1n46.chat.service.UuidService;
import venture.Aust1n46.chat.service.PlayerApiService;
import venture.Aust1n46.chat.utilities.FormatUtils;

/**
 * Class for reading and writing player data.
 *
 * @author Aust1n46
 */
@Singleton
public class SpigotFlatFileController {
	@Inject
	private VentureChat plugin;
	@Inject
	private UuidService uuidService;
	@Inject
	private PlayerApiService ventureChatApi;
	@Inject
	private ConfigService configService;

	private String playerDataDirectoryPath;

	@Inject
	public void postConstruct() {
		playerDataDirectoryPath = plugin.getDataFolder().getAbsolutePath() + "/PlayerData";
	}

	public void loadLegacyPlayerData() {
		File legacyPlayerDataFile = new File(plugin.getDataFolder().getAbsolutePath(), "Players.yml");
		if (!legacyPlayerDataFile.isFile()) {
			return;
		}
		try {
			plugin.getServer().getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&c - Detected Legacy Player Data!"));
			plugin.getServer().getConsoleSender()
					.sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&c - Converting to new structure and deleting old Players.yml file!"));
			FileConfiguration playerData = YamlConfiguration.loadConfiguration(legacyPlayerDataFile);
			for (String uuidString : playerData.getConfigurationSection("players").getKeys(false)) {
				UUID uuid = UUID.fromString(uuidString);
				if (uuidService.shouldSkipOfflineUUID(uuid)) {
					plugin.getServer().getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&c - Skipping Offline UUID: " + uuid));
					continue;
				}
				String name = playerData.getConfigurationSection("players." + uuid).getString("name");
				String currentChannelName = playerData.getConfigurationSection("players." + uuid).getString("current");
				ChatChannel currentChannel = configService.isChannel(currentChannelName) ? configService.getChannel(currentChannelName) : configService.getDefaultChannel();
				Set<UUID> ignores = new HashSet<UUID>();
				StringTokenizer i = new StringTokenizer(playerData.getConfigurationSection("players." + uuidString).getString("ignores"), ",");
				while (i.hasMoreTokens()) {
					ignores.add(UUID.fromString(i.nextToken()));
				}
				Set<String> listening = new HashSet<String>();
				StringTokenizer l = new StringTokenizer(playerData.getConfigurationSection("players." + uuidString).getString("listen"), ",");
				while (l.hasMoreTokens()) {
					String channel = l.nextToken();
					if (configService.isChannel(channel)) {
						listening.add(channel);
					}
				}
				HashMap<String, MuteContainer> mutes = new HashMap<String, MuteContainer>();
				StringTokenizer m = new StringTokenizer(playerData.getConfigurationSection("players." + uuidString).getString("mutes"), ",");
				while (m.hasMoreTokens()) {
					String[] parts = m.nextToken().split(":");
					if (configService.isChannel(parts[0])) {
						if (parts[1].equals("null")) {
							plugin.getServer().getConsoleSender().sendMessage("[VentureChat] Null Mute Time: " + parts[0] + " " + name);
							continue;
						}
						String channelName = parts[0];
						mutes.put(channelName, new MuteContainer(channelName, Long.parseLong(parts[1]), ""));
					}
				}
				Set<String> blockedCommands = new HashSet<String>();
				StringTokenizer b = new StringTokenizer(playerData.getConfigurationSection("players." + uuidString).getString("blockedcommands"), ",");
				while (b.hasMoreTokens()) {
					blockedCommands.add(b.nextToken());
				}
				boolean host = playerData.getConfigurationSection("players." + uuidString).getBoolean("host");
				UUID party = playerData.getConfigurationSection("players." + uuidString).getString("party").length() > 0
						? UUID.fromString(playerData.getConfigurationSection("players." + uuidString).getString("party"))
						: null;
				boolean filter = playerData.getConfigurationSection("players." + uuidString).getBoolean("filter");
				boolean notifications = playerData.getConfigurationSection("players." + uuidString).getBoolean("notifications");
				String jsonFormat = "Default";
				boolean spy = playerData.getConfigurationSection("players." + uuidString).getBoolean("spy", false);
				boolean commandSpy = playerData.getConfigurationSection("players." + uuidString).getBoolean("commandspy", false);
				boolean rangedSpy = playerData.getConfigurationSection("players." + uuidString).getBoolean("rangedspy", false);
				boolean messageToggle = playerData.getConfigurationSection("players." + uuidString).getBoolean("messagetoggle", true);
				boolean bungeeToggle = playerData.getConfigurationSection("players." + uuidString).getBoolean("bungeetoggle", true);
				final VentureChatPlayer mcp = VentureChatPlayer.builder()
						.uuid(uuid)
						.name(name)
						.currentChannel(currentChannel)
						.ignores(ignores)
						.listening(listening)
						.mutes(mutes)
						.blockedCommands(blockedCommands)
						.host(host)
						.party(party)
						.filter(filter)
						.notifications(notifications)
						.jsonFormat(jsonFormat)
						.spy(spy)
						.commandSpy(commandSpy)
						.rangedSpy(rangedSpy)
						.messageToggle(messageToggle)
						.bungeeToggle(bungeeToggle)
						.build();
				mcp.setModified(true);
				ventureChatApi.addMineverseChatPlayerToMap(mcp);
				ventureChatApi.addNameToMap(mcp);
			}
		} catch (Exception e) {
			ventureChatApi.clearMineverseChatPlayerMap();
			ventureChatApi.clearNameMap();
			plugin.getServer().getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&c - Error Loading Legacy Player Data!"));
			plugin.getServer().getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&c - Deleted Players.yml file!"));
		} finally {
			legacyPlayerDataFile.delete();
		}
	}

	public void loadPlayerData() {
		try {
			File playerDataDirectory = new File(playerDataDirectoryPath);
			if (!playerDataDirectory.exists()) {
				playerDataDirectory.mkdirs();
			}
			Files.walk(Paths.get(playerDataDirectoryPath)).filter(Files::isRegularFile).forEach((path) -> readPlayerDataFile(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads the player data file for a specific player. Corrupt/invalid data files
	 * are skipped and deleted.
	 *
	 * @param path
	 */
	private void readPlayerDataFile(Path path) {
		VentureChatPlayer mcp;
		File playerDataFile = path.toFile();
		if (!playerDataFile.exists()) {
			return;
		}
		try {
			FileConfiguration playerDataFileYamlConfiguration = YamlConfiguration.loadConfiguration(playerDataFile);
			String uuidString = playerDataFile.getName().replace(".yml", "");
			UUID uuid = UUID.fromString(uuidString);
			if (uuidService.shouldSkipOfflineUUID(uuid)) {
				plugin.getServer().getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&c - Skipping Offline UUID: " + uuid));
				plugin.getServer().getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&c - File will be skipped and deleted."));
				playerDataFile.delete();
				return;
			}
			String name = playerDataFileYamlConfiguration.getString("name");
			String currentChannelName = playerDataFileYamlConfiguration.getString("current");
			ChatChannel currentChannel = configService.isChannel(currentChannelName) ? configService.getChannel(currentChannelName) : configService.getDefaultChannel();
			Set<UUID> ignores = new HashSet<UUID>();
			StringTokenizer i = new StringTokenizer(playerDataFileYamlConfiguration.getString("ignores"), ",");
			while (i.hasMoreTokens()) {
				ignores.add(UUID.fromString(i.nextToken()));
			}
			Set<String> listening = new HashSet<String>();
			StringTokenizer l = new StringTokenizer(playerDataFileYamlConfiguration.getString("listen"), ",");
			while (l.hasMoreTokens()) {
				String channel = l.nextToken();
				if (configService.isChannel(channel)) {
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
			String jsonFormat = "Default";
			boolean spy = playerDataFileYamlConfiguration.getBoolean("spy", false);
			boolean commandSpy = playerDataFileYamlConfiguration.getBoolean("commandspy", false);
			boolean rangedSpy = playerDataFileYamlConfiguration.getBoolean("rangedspy", false);
			boolean messageToggle = playerDataFileYamlConfiguration.getBoolean("messagetoggle", true);
			boolean bungeeToggle = playerDataFileYamlConfiguration.getBoolean("bungeetoggle", true);
			mcp = VentureChatPlayer.builder()
					.uuid(uuid)
					.name(name)
					.currentChannel(currentChannel)
					.ignores(ignores)
					.listening(listening)
					.mutes(mutes)
					.blockedCommands(blockedCommands)
					.host(host)
					.party(party)
					.filter(filter)
					.notifications(notifications)
					.jsonFormat(jsonFormat)
					.spy(spy)
					.commandSpy(commandSpy)
					.rangedSpy(rangedSpy)
					.messageToggle(messageToggle)
					.bungeeToggle(bungeeToggle)
					.build();
		} catch (Exception e) {
			plugin.getServer().getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&c - Error Loading Data File: " + playerDataFile.getName()));
			plugin.getServer().getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&c - File will be skipped and deleted."));
			playerDataFile.delete();
			return;
		}
		if (mcp != null) {
			ventureChatApi.addMineverseChatPlayerToMap(mcp);
			ventureChatApi.addNameToMap(mcp);
		}
	}

	public void savePlayerData(VentureChatPlayer mcp) {
		if (mcp == null || uuidService.shouldSkipOfflineUUID(mcp.getUuid()) || (!mcp.isOnline() && !mcp.isModified())) {
			return;
		}
		try {
			File playerDataFile = new File(playerDataDirectoryPath, mcp.getUuid() + ".yml");
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
				ChatChannel c = configService.getChannel(channel);
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
			for (MuteContainer mute : mcp.getMutes().values()) {
				ConfigurationSection channelSection = muteSection.createSection(mute.getChannel());
				channelSection.set("time", mute.getDuration());
				channelSection.set("reason", mute.getReason());
			}

			playerDataFileYamlConfiguration.set("blockedcommands", blockedCommands);
			playerDataFileYamlConfiguration.set("host", mcp.isHost());
			playerDataFileYamlConfiguration.set("party", mcp.getParty() != null ? mcp.getParty().toString() : "");
			playerDataFileYamlConfiguration.set("filter", mcp.isFilter());
			playerDataFileYamlConfiguration.set("notifications", mcp.isNotifications());
			playerDataFileYamlConfiguration.set("spy", configService.isSpy(mcp));
			playerDataFileYamlConfiguration.set("commandspy", configService.isCommandSpy(mcp));
			playerDataFileYamlConfiguration.set("rangedspy", configService.isRangedSpy(mcp));
			playerDataFileYamlConfiguration.set("messagetoggle", mcp.isMessageToggle());
			playerDataFileYamlConfiguration.set("bungeetoggle", mcp.isBungeeToggle());
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

	public void savePlayerData() {
		for (VentureChatPlayer p : ventureChatApi.getMineverseChatPlayers()) {
			savePlayerData(p);
		}
	}
}
