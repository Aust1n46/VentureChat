package mineverse.Aust1n46.chat.database;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;

import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.SynchronizedMineverseChatPlayer;
import mineverse.Aust1n46.chat.bungee.MineverseChatBungee;
import mineverse.Aust1n46.chat.utilities.Format;
import mineverse.Aust1n46.chat.utilities.UUIDFetcher;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

/**
 * Class for reading and writing bungee player data.
 * 
 * @author Aust1n46
 */
public class BungeePlayerData {
	private static MineverseChatBungee bungee = MineverseChatBungee.getInstance();
	private static final String BUNGEE_PLAYER_DATA_DIRECTORY_PATH = bungee.getDataFolder().getAbsolutePath() + "/PlayerData";

	public static void loadLegacyBungeePlayerData() {
		File sync = new File(bungee.getDataFolder(), "BungeePlayers.yml");
		if(!sync.exists()) {
			return;
		}
		try {
			ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(Format.FormatStringAll("&8[&eVentureChat&8]&c - Detected Legacy Player Data!")));
			ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(Format.FormatStringAll("&8[&eVentureChat&8]&c - Converting to new structure and deleting old BungeePlayers.yml file!")));
			Configuration playerData = ConfigurationProvider.getProvider(YamlConfiguration.class).load(sync);
			for(String uuidString : playerData.getKeys()) {
				UUID uuid = UUID.fromString(uuidString);
				if(UUIDFetcher.shouldSkipOfflineUUIDBungee(uuid)) {
					ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(Format.FormatStringAll("&8[&eVentureChat&8]&c - Skipping Offline UUID: " + uuid)));
					continue;
				}
				Set<String> listening = new HashSet<String>();
				StringTokenizer l = new StringTokenizer(playerData.getString(uuidString + ".channels"), ",");
				while(l.hasMoreTokens()) {
					String channel = l.nextToken();
					listening.add(channel);
				}
				HashMap<String, Integer> mutes = new HashMap<String, Integer>();
				StringTokenizer m = new StringTokenizer(playerData.getString(uuidString + ".mutes"), ",");
				while(m.hasMoreTokens()) {
					String[] parts = m.nextToken().split(":");
					mutes.put(parts[0], Integer.parseInt(parts[1]));
				}
				HashSet<UUID> ignores = new HashSet<UUID>();
				StringTokenizer n = new StringTokenizer(playerData.getString(uuidString + ".ignores"), ",");
				while(n.hasMoreTokens()) {
					String ignore = n.nextToken();
					ignores.add(UUID.fromString(ignore));
				}
				boolean spy = playerData.getBoolean(uuidString + ".spy");
				boolean messageToggle = playerData.getBoolean(uuidString + ".messagetoggle");
				MineverseChatAPI.addSynchronizedMineverseChatPlayerToMap(new SynchronizedMineverseChatPlayer(uuid, listening, mutes, ignores, spy, messageToggle));
			}
		}
		catch (Exception e) {
			MineverseChatAPI.clearBungeePlayerMap();
			ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(Format.FormatStringAll("&8[&eVentureChat&8]&c - Error Loading Legacy Player Data!")));
			ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(Format.FormatStringAll("&8[&eVentureChat&8]&c - Deleted BungeePlayers.yml file!")));
		}
		finally {
			sync.delete();
		}
	}
	
	public static void loadBungeePlayerData() {
		try {
			File playerDataDirectory = new File(BUNGEE_PLAYER_DATA_DIRECTORY_PATH);
			if(!playerDataDirectory.exists()) {
				playerDataDirectory.mkdirs();
			}
			Files.walk(Paths.get(BUNGEE_PLAYER_DATA_DIRECTORY_PATH))
			 .filter(Files::isRegularFile)
			 .forEach((path) -> readBungeePlayerDataFile(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void readBungeePlayerDataFile(Path path) {
		SynchronizedMineverseChatPlayer smcp;
		File bungeePlayerDataFile = path.toFile();
		if(!bungeePlayerDataFile.exists()) {
			return;
		}
		try {
			Configuration bungeePlayerDataFileConfiguration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(bungeePlayerDataFile);
			String uuidString = bungeePlayerDataFile.getName().replace(".yml", "");
			UUID uuid = UUID.fromString(uuidString);
			if(UUIDFetcher.shouldSkipOfflineUUIDBungee(uuid)) {
				ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(Format.FormatStringAll("&8[&eVentureChat&8]&c - Skipping Offline UUID: " + uuid)));
				ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(Format.FormatStringAll("&8[&eVentureChat&8]&c - File will be skipped and deleted.")));
				bungeePlayerDataFile.delete();
				return;
			}
			Set<String> listening = new HashSet<String>();
			StringTokenizer l = new StringTokenizer(bungeePlayerDataFileConfiguration.getString("channels"), ",");
			while(l.hasMoreTokens()) {
				String channel = l.nextToken();
				listening.add(channel);
			}
			HashMap<String, Integer> mutes = new HashMap<String, Integer>();
			StringTokenizer m = new StringTokenizer(bungeePlayerDataFileConfiguration.getString("mutes"), ",");
			while(m.hasMoreTokens()) {
				String[] parts = m.nextToken().split(":");
				mutes.put(parts[0], Integer.parseInt(parts[1]));
			}
			HashSet<UUID> ignores = new HashSet<UUID>();
			StringTokenizer n = new StringTokenizer(bungeePlayerDataFileConfiguration.getString("ignores"), ",");
			while(n.hasMoreTokens()) {
				String ignore = n.nextToken();
				ignores.add(UUID.fromString(ignore));
			}
			boolean spy = bungeePlayerDataFileConfiguration.getBoolean("spy");
			boolean messageToggle = bungeePlayerDataFileConfiguration.getBoolean("messagetoggle");
			smcp = new SynchronizedMineverseChatPlayer(uuid, listening, mutes, ignores, spy, messageToggle);
		}
		catch(Exception e) {
			ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(Format.FormatStringAll("&8[&eVentureChat&8]&c - Error Loading Data File: " + bungeePlayerDataFile.getName())));
			ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(Format.FormatStringAll("&8[&eVentureChat&8]&c - File will be skipped and deleted.")));
			bungeePlayerDataFile.delete();
			return;
		}
		if(smcp != null) {
			MineverseChatAPI.addSynchronizedMineverseChatPlayerToMap(smcp);
		}
	}
	
	public static void saveBungeePlayerData() {
		try {
			for(SynchronizedMineverseChatPlayer p : MineverseChatAPI.getSynchronizedMineverseChatPlayers()) {
				if(UUIDFetcher.shouldSkipOfflineUUIDBungee(p.getUUID())) {
					return;
				}
				File bungeePlayerDataFile = new File(BUNGEE_PLAYER_DATA_DIRECTORY_PATH, p.getUUID() + ".yml");
				if(!bungeePlayerDataFile.exists()) {
					bungeePlayerDataFile.createNewFile();
				}
				Configuration bungeePlayerDataFileConfiguration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(bungeePlayerDataFile);
				
				String listen = "";
				for(String s : p.getListening())
					listen += s + ",";
				String mute = "";
				for(String s : p.getMutes().keySet())
					mute += s + ":0,";
				String ignore = "";
				for(UUID s : p.getIgnores()) 
					ignore += s.toString() + ",";
				if(listen.length() > 0)
					listen = listen.substring(0, listen.length() - 1);
				if(mute.length() > 0)
					mute = mute.substring(0, mute.length() - 1);
				if(ignore.length() > 0)
					ignore = ignore.substring(0, ignore.length() - 1);
				bungeePlayerDataFileConfiguration.set("channels", listen);
				bungeePlayerDataFileConfiguration.set("mutes", mute);
				bungeePlayerDataFileConfiguration.set("ignores", ignore);
				bungeePlayerDataFileConfiguration.set("spy", p.isSpy());
				bungeePlayerDataFileConfiguration.set("messagetoggle", p.getMessageToggle());
				
				ConfigurationProvider.getProvider(YamlConfiguration.class).save(bungeePlayerDataFileConfiguration, bungeePlayerDataFile);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
