/*
 * VentureChat plugin for Minecraft servers running Bukkit or Spigot software.
 * @author Aust1n46
 */
package mineverse.Aust1n46.chat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import mineverse.Aust1n46.chat.irc.Bot;
import mineverse.Aust1n46.chat.irc.command.IRCCommandInfo;
import mineverse.Aust1n46.chat.json.JsonFormatInfo;
import mineverse.Aust1n46.chat.listeners.CapeListener;
import mineverse.Aust1n46.chat.listeners.CommandListener;
import mineverse.Aust1n46.chat.listeners.LoginListener;
import mineverse.Aust1n46.chat.listeners.ChatListener;
import mineverse.Aust1n46.chat.listeners.PacketListener;
import mineverse.Aust1n46.chat.listeners.SignListener;
import mineverse.Aust1n46.chat.localization.Localization;
//import mineverse.Aust1n46.chat.alias.Alias;
import mineverse.Aust1n46.chat.alias.AliasInfo;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.channel.ChatChannelInfo;
//import mineverse.Aust1n46.chat.command.CCommand;
import mineverse.Aust1n46.chat.command.MineverseCommand;
import mineverse.Aust1n46.chat.command.MineverseCommandExecutor;
import mineverse.Aust1n46.chat.command.chat.Broadcast;
import mineverse.Aust1n46.chat.command.chat.BungeeToggle;
import mineverse.Aust1n46.chat.command.chat.Channel;
import mineverse.Aust1n46.chat.command.chat.Channelinfo;
import mineverse.Aust1n46.chat.command.chat.Chatinfo;
import mineverse.Aust1n46.chat.command.chat.Chatreload;
import mineverse.Aust1n46.chat.command.chat.Chlist;
import mineverse.Aust1n46.chat.command.chat.Chwho;
import mineverse.Aust1n46.chat.command.chat.Clearchat;
import mineverse.Aust1n46.chat.command.chat.Commandblock;
import mineverse.Aust1n46.chat.command.chat.Commandspy;
import mineverse.Aust1n46.chat.command.chat.Config;
import mineverse.Aust1n46.chat.command.chat.Edit;
import mineverse.Aust1n46.chat.command.chat.Filter;
import mineverse.Aust1n46.chat.command.chat.Force;
import mineverse.Aust1n46.chat.command.chat.Forceall;
import mineverse.Aust1n46.chat.command.chat.Kickchannel;
import mineverse.Aust1n46.chat.command.chat.Kickchannelall;
import mineverse.Aust1n46.chat.command.chat.Leave;
import mineverse.Aust1n46.chat.command.chat.Listen;
import mineverse.Aust1n46.chat.command.chat.Mail;
import mineverse.Aust1n46.chat.command.chat.Me;
import mineverse.Aust1n46.chat.command.chat.Nick;
import mineverse.Aust1n46.chat.command.chat.Party;
import mineverse.Aust1n46.chat.command.chat.RangedSpy;
import mineverse.Aust1n46.chat.command.chat.Removemessage;
import mineverse.Aust1n46.chat.command.chat.Setchannel;
import mineverse.Aust1n46.chat.command.chat.Setchannelall;
import mineverse.Aust1n46.chat.command.chat.VentureChatGui;
import mineverse.Aust1n46.chat.command.chat.Venturechat;
import mineverse.Aust1n46.chat.command.message.Afk;
import mineverse.Aust1n46.chat.command.message.Ignore;
import mineverse.Aust1n46.chat.command.message.Message;
import mineverse.Aust1n46.chat.command.message.MessageToggle;
import mineverse.Aust1n46.chat.command.message.Notifications;
import mineverse.Aust1n46.chat.command.message.Reply;
import mineverse.Aust1n46.chat.command.message.Spy;
import mineverse.Aust1n46.chat.command.mute.Mute;
import mineverse.Aust1n46.chat.command.mute.Muteall;
import mineverse.Aust1n46.chat.command.mute.Unmute;
import mineverse.Aust1n46.chat.command.mute.Unmuteall;
import mineverse.Aust1n46.chat.database.MySQL;
import mineverse.Aust1n46.chat.database.PlayerData;
import mineverse.Aust1n46.chat.gui.GuiSlotInfo;
import mineverse.Aust1n46.chat.utilities.Format;
import mineverse.Aust1n46.chat.versions.V1_8;
import mineverse.Aust1n46.chat.versions.VersionHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.Sound;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.utility.MinecraftReflection;

import me.clip.placeholderapi.PlaceholderAPI;

public class MineverseChat extends JavaPlugin implements PluginMessageListener {
	// Listeners --------------------------------
	private ChatListener chatListener;
	private LoginListener loginListener;
	private SignListener signListener;
	private CommandListener commandListener;
	private PacketListener packetListener;
	private CapeListener capeListener;
	private Channel channelListener;
	public static String[] playerlist;
	public static String playerlist_server;
	public boolean ircListen;
	public ProtocolManager protocolManager;
	public static ChatMessage lastChatMessage;
	public static String lastJson;
	public static Method messageMethod;
	public static Field posField;
	public static Class<?> chatMessageType;
	private static Field commandMap;
	private static Field knownCommands;

	// Executors --------------------------------
	private MineverseCommandExecutor commandExecutor;
	private Map<String, MineverseCommand> commands = new HashMap<String, MineverseCommand>();

	// MySQL ------------------------------------
	public Connection c = null;
	public MySQL MySQL;
	public boolean mysql = false;

	// SQLite -------------------------------------
	// public Connection lite = null;

	// Misc --------------------------------
	public static ChatChannelInfo ccInfo;
	public static AliasInfo aaInfo;
	public static JsonFormatInfo jfInfo;
	public static IRCCommandInfo ircInfo;
	public static GuiSlotInfo gsInfo;
	public boolean quickchat = true;
	private static final Logger log = Logger.getLogger("Minecraft");
	private static MineverseChat plugin;
	public static Set<MineverseChatPlayer> players = new HashSet<MineverseChatPlayer>();
	public static Set<MineverseChatPlayer> onlinePlayers = new HashSet<MineverseChatPlayer>();
	public static HashMap<String, String> networkPlayers = new HashMap<String, String>();
	public static ArmorStand cape;
	public static ItemStack banner;
	public static boolean capeToggle = false;
	private boolean firstRun = true;

	// Vault --------------------------------
	public static Permission permission = null;
	public static Chat chat = null;
	public static CommandMap cmap;

	// Offline data ----------------------------
	public Map<String, String> mutes = new HashMap<String, String>();
	public Map<String, List<String>> mail = new HashMap<String, List<String>>();

	// IRC Bot -----------
	public Bot bot;
	public boolean irc = false;

	private LogLevels curLogLevel;

	public long LINELENGTH = 40;

	@Override
	public void onEnable() {
		plugin = this;
		try {
			Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Initializing..."));
			if(!getDataFolder().exists()) {
				getDataFolder().mkdirs();
			}
			File file = new File(getDataFolder(), "config.yml");
			if(!file.exists()) {
				Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Config not found! Generating file."));
				saveDefaultConfig();
			}
			else {
				Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Config found! Loading file."));
			}

			if(!new File(getDataFolder(), "defaultconfig.yml").exists()) {
				saveResource("defaultconfig.yml", false);
			}
		}
		catch(Exception ex) {
			log.severe(String.format("[" + String.format("VentureChat") + "]" + " - Could not load configuration!\n " + ex, getDescription().getName()));
		}
		ccInfo = new ChatChannelInfo(this);
		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Checking for Vault..."));
		// Set up Vault
		if(!this.setupPermissions()) {
			Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - &cCould not find Vault dependency, disabling."));
			Bukkit.getPluginManager().disablePlugin(this);
		}
		this.setupChat();
		// Log completion of initialization
		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Enabled Successfully"));
		// Get config and handle
		// Configuration
		Localization.initialize();
		PlayerData.initialize();
		if(this.firstRun) {
			for(String uuidString : PlayerData.getPlayerData().getConfigurationSection("players").getKeys(false)) {
				UUID uuid = UUID.fromString(uuidString);
				String name = PlayerData.getPlayerData().getConfigurationSection("players." + uuid).getString("name");
				String currentChannelName = PlayerData.getPlayerData().getConfigurationSection("players." + uuid).getString("current");
				ChatChannel currentChannel = ccInfo.isChannel(currentChannelName) ? ccInfo.getChannelInfo(currentChannelName) : ccInfo.getDefaultChannel();
				Set<UUID> ignores = new HashSet<UUID>();
				StringTokenizer i = new StringTokenizer(PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getString("ignores"), ",");
				while(i.hasMoreTokens()) {
					ignores.add(UUID.fromString(i.nextToken()));
				}
				Set<String> listening = new HashSet<String>();
				StringTokenizer l = new StringTokenizer(PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getString("listen"), ",");
				while(l.hasMoreTokens()) {
					String channel = l.nextToken();
					if(ccInfo.isChannel(channel)) {
						listening.add(channel);
					}
				}
				HashMap<String, Integer> mutes = new HashMap<String, Integer>();
				StringTokenizer m = new StringTokenizer(PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getString("mutes"), ",");
				while(m.hasMoreTokens()) {
					String[] parts = m.nextToken().split(":");
					if(ccInfo.isChannel(parts[0])) {
						if(parts[1].equals("null")) {
							log.info("[VentureChat] Null Mute Time: " + parts[0] + " " + name);
							continue;
						}
						mutes.put(ccInfo.getChannelInfo(parts[0]).getName(), Integer.parseInt(parts[1]));
					}
				}
				Set<String> blockedCommands = new HashSet<String>();
				StringTokenizer b = new StringTokenizer(PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getString("blockedcommands"), ",");
				while(b.hasMoreTokens()) {
					blockedCommands.add(b.nextToken());
				}
				List<String> mail = new ArrayList<String>();
				StringTokenizer ma = new StringTokenizer(PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getString("mail"), ",");
				while(ma.hasMoreTokens()) {
					mail.add(ma.nextToken());
				}
				boolean host = PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getBoolean("host");
				UUID party = PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getString("party").length() > 0 ? UUID.fromString(PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getString("party")) : null;
				boolean filter = PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getBoolean("filter");
				boolean notifications = PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getBoolean("notifications");
				String nickname = PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getString("nickname");
				String jsonFormat = PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getString("jsonformat");
				boolean spy = PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getBoolean("spy", false);
				boolean commandSpy = PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getBoolean("commandspy", false);
				boolean rangedSpy = PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getBoolean("rangedspy", false);
				boolean messageToggle = PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getBoolean("messagetoggle", true);
				boolean bungeeToggle = PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getBoolean("bungeetoggle", true);
				players.add(new MineverseChatPlayer(uuid, name, currentChannel, ignores, listening, mutes, blockedCommands, mail, host, party, filter, notifications, nickname, jsonFormat, spy, commandSpy, rangedSpy, messageToggle, bungeeToggle));
			}
		}
		else {
			for(Player p : this.getServer().getOnlinePlayers()) {
				MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer(p);
				mcp.setName(p.getName());
				mcp.setOnline(true);
			}
		}

		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Registering Listeners"));
		// Channel information reference
		ircInfo = new IRCCommandInfo(this);
		aaInfo = new AliasInfo(this);
		jfInfo = new JsonFormatInfo(this);
		gsInfo = new GuiSlotInfo();

		if(ccInfo == null) {
			Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - &cConfiguration is BAD!"));
		}

		if(this.getConfig().getConfigurationSection("mysql").getBoolean("enabled")) {
			this.MySQL = new MySQL(this, getConfig().getConfigurationSection("mysql").getString("host"), getConfig().getConfigurationSection("mysql").getString("port"), getConfig().getConfigurationSection("mysql").getString("database"), getConfig().getConfigurationSection("mysql").getString("user"), getConfig().getConfigurationSection("mysql").getString("password"));
			this.mysql = true;
			try {
				c = MySQL.openConnection();
				Statement statement = c.createStatement();
				statement.executeUpdate("CREATE TABLE IF NOT EXISTS `VentureChat` (`rowid` INT(7) NOT NULL AUTO_INCREMENT, `ChatTime` TEXT(100), `UUID` TEXT(100), `Name` TEXT(100), `Server` TEXT(100), `Channel` TEXT(100), `Text` TEXT(300), `Type` TEXT(100), PRIMARY KEY (rowid));");
				Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Connecting to MySQL Database"));
			}
			catch(ClassNotFoundException | SQLException e) {
				Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - &cFailed to connect to MySQL Database, Reason: " + e));
				this.mysql = false;
			}
		}

		/*
		 * Future SQLite data storage option SQLite SQLite = new SQLite(this,
		 * "PlayerData.db"); try { lite = SQLite.openConnection(); Statement
		 * statement = lite.createStatement(); statement.executeUpdate(
		 * "CREATE TABLE IF NOT EXISTS `PlayerData` (`Player` TEXT(100), `UUID` TEXT(100), `Default Channel` TEXT(100), `Ignores` TEXT(1000), `Channels` TEXT(1000), `Mutes` TEXT(1000), `Timed Mutes` TEXT(1000), `Blocked Commands` TEXT(1000), `Date` TEXT(100));"
		 * ); //statement.executeUpdate(
		 * "INSERT INTO `PlayerData` (`Player`, `UUID`, `Default Channel`, `Ignores`, `Channels`, `Mutes`, `Timed Mutes`, `Blocked Commands`, `Date`) VALUES ('bob', 'derp', 'dered', '"
		 * +plugin.getServer().getServerName()+
		 * "', 'Messaging_Component', 'COMMAND', 'Chat', 'HI', ':D');");
		 * log.info(String.format("[" +
		 * String.format(getConfig().getString("pluginname", "VentureChat") +
		 * "]" + " - Connecting to SQLite Database",
		 * getDescription().getName()))); } catch(ClassNotFoundException |
		 * SQLException e) { e.printStackTrace(); }
		 */

		// this.loadCommandMap();
		// this.unregister("msg");

		if(this.getConfig().getConfigurationSection("irc").getBoolean("enabled", false)) {
			bot = new Bot(this, ccInfo, ircInfo);
			bot.init();
			irc = true;
		}

		commands.put("afk", new Afk("afk"));
		commands.put("broadcast", new Broadcast("broadcast"));
		commands.put("channel", new Channel("channel"));
		commands.put("join", new Channel("join"));
		commands.put("channelinfo", new Channelinfo("channelinfo"));
		commands.put("chatinfo", new Chatinfo("chatinfo"));
		commands.put("chatreload", new Chatreload("chatreload"));
		commands.put("chlist", new Chlist("chlist"));
		commands.put("chwho", new Chwho("chwho"));
		commands.put("clearchat", new Clearchat("clearchat"));
		commands.put("commandblock", new Commandblock("commandblock"));
		commands.put("commandspy", new Commandspy("commandspy"));
		commands.put("config", new Config("config"));
		commands.put("edit", new Edit("edit"));
		commands.put("filter", new Filter("filter"));
		commands.put("force", new Force("force"));
		commands.put("forceall", new Forceall("forceall"));
		commands.put("ignore", new Ignore("ignore"));
		commands.put("kickchannel", new Kickchannel("kickchannel"));
		commands.put("kickchannelall", new Kickchannelall("kickchannelall"));
		commands.put("leave", new Leave("leave"));
		commands.put("listen", new Listen("listen"));
		commands.put("mail", new Mail("mail"));
		commands.put("me", new Me("me"));
		commands.put("message", new Message("message"));
		commands.put("tell", new Message("tell"));
		commands.put("whisper", new Message("whisper"));
		commands.put("venturechat", new Venturechat("venturechat"));
		commands.put("mute", new Mute("mute"));
		commands.put("muteall", new Muteall("muteall"));
		commands.put("nick", new Nick("nick"));
		commands.put("notifications", new Notifications("notifications"));
		commands.put("party", new Party("party"));
		commands.put("rangedspy", new RangedSpy("rangedspy"));
		commands.put("removemessage", new Removemessage("removemessage"));
		commands.put("reply", new Reply("reply"));
		commands.put("setchannel", new Setchannel("setchannel"));
		commands.put("setchannelall", new Setchannelall("setchannelall"));
		commands.put("spy", new Spy("spy"));
		commands.put("unmute", new Unmute("unmute"));
		commands.put("unmuteall", new Unmuteall("unmuteall"));
		commands.put("venturechatgui", new VentureChatGui("venturechatgui"));
		commands.put("messagetoggle", new MessageToggle("messagetoggle"));
		commands.put("bungeetoggle", new BungeeToggle("bungeetoggle"));
		commandExecutor = new MineverseCommandExecutor(commands);
		for(String command : commands.keySet()) {
			this.getCommand(command).setExecutor(commandExecutor);
		}

		channelListener = new Channel();
		signListener = new SignListener(this, ccInfo);
		chatListener = new ChatListener(this, ccInfo, bot);
		commandListener = new CommandListener(this, ccInfo, aaInfo, bot);

		PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(channelListener, this);
		pluginManager.registerEvents(chatListener, this);
		pluginManager.registerEvents(signListener, this);
		pluginManager.registerEvents(commandListener, this);
		if(!VersionHandler.is1_7_10() && !VersionHandler.is1_7_9() && !VersionHandler.is1_7_2()) {
			capeListener = new CapeListener();
			pluginManager.registerEvents(capeListener, this);
		}
		loginListener = new LoginListener(this, ccInfo);
		pluginManager.registerEvents(loginListener, this);
		this.registerPacketListeners();
		this.loadNMS();
		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Attaching to Executors"));
		try {
			// if(VersionHandler.is1_7_9()) cmap = V1_7_9.v1_7_9();
			// if(VersionHandler.is1_7_10()) cmap = V1_7_10.v1_7_10();
			if(VersionHandler.is1_8()) cmap = V1_8.v1_8();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		this.quickchat = false;
		if(cmap == null) {
			this.quickchat = false;
			// log.info(String.format("[" + String.format("VentureChat" + "]" +
			// " - Unrecognized server version, Quickchat commands not
			// registering",
			// getDescription().getName())));
			// log.info(String.format("[" + String.format("VentureChat" + "]" +
			// " - Unrecognized server version, Alias commands not registering",
			// getDescription().getName())));
		}
		else {
			/*
			 * Don't run this code right now for(ChatChannel c :
			 * ccInfo.getChannelsInfo()) { CCommand cmd = new
			 * CCommand(c.getAlias()); cmap.register("", cmd);
			 * cmd.setExecutor(commandListener); } for(Alias a :
			 * aaInfo.getAliases()) { CCommand cmd = new CCommand(a.getName());
			 * cmap.register("", cmd); }
			 * Bukkit.getConsoleSender().sendMessage(Format.
			 * FormatStringAll("&8[&eVentureChat&8]&e - Registering Alias commands"
			 * )); Bukkit.getConsoleSender().sendMessage(Format.
			 * FormatStringAll("&8[&eVentureChat&8]&e - Registering Quickchat commands"
			 * ));
			 */
		}
		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Establishing BungeeCord"));
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "venturechat:");
		Bukkit.getMessenger().registerIncomingPluginChannel(this, "venturechat:", this);
		if(pluginManager.isPluginEnabled("Towny")) {
			Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Enabling Towny Formatting"));
		}
		if(pluginManager.isPluginEnabled("Jobs")) {
			Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Enabling Jobs Formatting"));
		}
		if(pluginManager.isPluginEnabled("Factions")) {
			String version = pluginManager.getPlugin("Factions").getDescription().getVersion();
			Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Enabling Factions Formatting version " + version));
		}
		if(pluginManager.isPluginEnabled("Heroes")) {
			Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Enabling Heroes Formatting"));
		}
		if(pluginManager.isPluginEnabled("PlaceholderAPI")) {
			Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Enabling PlaceholderAPI Hook"));
		}
		boolean hooked = PlaceholderAPI.registerPlaceholderHook("venturechat", new VentureChatPlaceholders());
		if(hooked) {
			Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Added placeholders to PlaceholderAPI!"));
		}
		else {
			Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - &cPlaceholders were not added to PlaceholderAPI!"));
		}
		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Loading player data"));
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				PlayerData.savePlayerData();
				if(getConfig().getString("loglevel", "info").equals("debug")) {
					Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Saving Player Data"));
				}
			}
		}, 0L, getConfig().getInt("saveinterval") * 1200);
		scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				for(MineverseChatPlayer p : MineverseChat.players) {
					// Calendar currentDate = Calendar.getInstance();
					// SimpleDateFormat formatter = new
					// SimpleDateFormat("dd:HH:mm:ss");
					// String date = formatter.format(currentDate.getTime());
					// String[] datearray = date.split(":");
					// int time = (Integer.parseInt(datearray[0]) * 1440) +
					// (Integer.parseInt(datearray[1]) * 60) +
					// (Integer.parseInt(datearray[2]));

					int time = (int) (System.currentTimeMillis() / 60000);

					for(String c : p.getMutes().keySet()) {
						ChatChannel channel = ccInfo.getChannelInfo(c);
						int timemark = p.getMutes().get(channel.getName());
						if(timemark == 0) return;
						// System.out.println(time + " " + timemark);
						if(time > timemark) {
							p.removeMute(channel.getName());
							if(p.isOnline()) p.getPlayer().sendMessage(ChatColor.RED + "You have just been unmuted in: " + ChatColor.valueOf(channel.getColor().toUpperCase()) + channel.getName());
							else p.setModified(true);
						}
					}
				}
				if(getConfig().getString("loglevel", "info").equals("debug")) {
					// log.info(String.format("[" +
					// String.format(getConfig().getString("pluginname",
					// "VentureChat") + "]" + " - Updating Player Mutes",
					// getDescription().getName())));
				}
			}
		}, 0L, 20L);
		this.firstRun = false;
	}

	@SuppressWarnings("unchecked")
	public void unregister(String name) {
		try {
			((Map<String, Command>) knownCommands.get((SimpleCommandMap) commandMap.get(Bukkit.getServer()))).remove(name);
		}
		catch(Exception e) {
		}
	}

	@SuppressWarnings("unused")
	private void loadCommandMap() {
		try {
			commandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			commandMap.setAccessible(true);
			knownCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
			knownCommands.setAccessible(true);
		}
		catch(Exception e) {
		}
	}

	public CommandMap getCommandMap() {
		return cmap;
	}

	public static MineverseChat getInstance() {
		return MineverseChat.plugin;
	}

	private void registerPacketListeners() {
		this.protocolManager = ProtocolLibrary.getProtocolManager();
		this.packetListener = new PacketListener(this);
		this.protocolManager.addPacketListener(this.packetListener);
	}
	
	public static String toPlainText(Object o, Class<?> c) { 
		List<Object> finalList = new ArrayList<>();
		StringBuilder stringbuilder = new StringBuilder();
		try {
			splitComponents(finalList, o, c);
			for(Object component : finalList) {
				if(VersionHandler.is1_7_10()) {
					stringbuilder.append((String) component.getClass().getMethod("e").invoke(component));
				}
				else {
					stringbuilder.append((String) component.getClass().getMethod("getText").invoke(component));
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		if(plugin.getConfig().getString("loglevel", "info").equals("debug")) {
			System.out.println("my string");
			System.out.println("my string");
			System.out.println("my string");
			System.out.println("my string");
			System.out.println("my string");
			System.out.println(stringbuilder.toString());
		}
		return stringbuilder.toString();
	}
	
	private static void splitComponents(List<Object> finalList, Object o, Class<?> c) throws Exception {
		if(plugin.getConfig().getString("loglevel", "info").equals("debug")) {
			for(Method m : c.getMethods()) {
				System.out.println(m.getName());
			}
		}
		if(VersionHandler.is1_7() || VersionHandler.is1_8() || VersionHandler.is1_9() || VersionHandler.is1_10() || VersionHandler.is1_11() || VersionHandler.is1_12() || VersionHandler.is1_13() || (VersionHandler.is1_14() && !VersionHandler.is1_14_4())) {
			ArrayList<?> list = (ArrayList<?>) c.getMethod("a").invoke(o, new Object[0]);
			for(Object component : list) {
				ArrayList<?> innerList = (ArrayList<?>) c.getMethod("a").invoke(component, new Object[0]);
				if(innerList.size() > 0) {
					splitComponents(finalList, component, c);
				}
				else {
					finalList.add(component);
				}
			}
		}
		else {
			ArrayList<?> list = (ArrayList<?>) c.getMethod("getSiblings").invoke(o, new Object[0]);
			for(Object component : list) {
				ArrayList<?> innerList = (ArrayList<?>) c.getMethod("getSiblings").invoke(component, new Object[0]);
				if(innerList.size() > 0) {
					splitComponents(finalList, component, c);
				}
				else {
					finalList.add(component);
				}
			}
		}
	}

	private void loadNMS() {	
		try {
			MineverseChat.posField = MinecraftReflection.getMinecraftClass("PacketPlayOutChat").getDeclaredField("b");
			MineverseChat.posField.setAccessible(true);
			
			
			//MineverseChat.messageMethod = MinecraftReflection.getMinecraftClass("ChatBaseComponent").getDeclaredMethod("getString");
			//MineverseChat.messageMethod.setAccessible(true);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		if(!VersionHandler.is1_7_10() && !VersionHandler.is1_8() && !VersionHandler.is1_9() && !VersionHandler.is1_10() && !VersionHandler.is1_11()) {
			try {
				MineverseChat.chatMessageType = getNMSClass("ChatMessageType");
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	private Class<?> getNMSClass(String name) {
		try {
			return Class.forName("net.minecraft.server." + getVersion() + "." + name);
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if(permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return(permission != null);
	}

	private boolean setupChat() {
		RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
		if(chatProvider != null) {
			chat = chatProvider.getProvider();
		}
		return(chat != null);
	}

	public long getLineLength() {
		return LINELENGTH;
	}

	@Override
	public void onDisable() {
		PlayerData.savePlayerData();
		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Disabling..."));
		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Disabled Successfully"));
		if(irc) {
			bot.terminate();
		}
		if(MineverseChat.cape != null) {
			MineverseChat.cape.remove();
		}
	}

	public void setLogLevel(String loglevel) {
		if(LogLevels.valueOf(loglevel) != null) {
			curLogLevel = LogLevels.valueOf(loglevel);
		}
		else {
			curLogLevel = LogLevels.INFO;
		}
	}

	public void logme(LogLevels level, String location, String logline) {
		if(level.ordinal() >= curLogLevel.ordinal()) {
			log.log(Level.INFO, "[VentureChat]: {0}:{1} : {2}", new Object[] { level.toString(), location, logline });
		}
	}

	public void synchronize(MineverseChatPlayer mcp, boolean changes) {
		// System.out.println("Sync started...");
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(outstream);
		try {
			out.writeUTF("Sync");
			if(!changes) {
				out.writeUTF("Receive");
				// System.out.println(mcp.getPlayer().getServer().getServerName());
				// out.writeUTF(mcp.getPlayer().getServer().getServerName());
				out.writeUTF(mcp.getUUID().toString());
			}
			else {
				out.writeUTF("Update");
				out.writeUTF(mcp.getUUID().toString());
				// out.writeUTF("Channels");
				int channelCount = 0;
				for(String c : mcp.getListening()) {
					ChatChannel channel = ccInfo.getChannelInfo(c);
					if(channel.getBungee()) {
						channelCount++;
					}
				}
				out.write(channelCount);
				for(String c : mcp.getListening()) {
					ChatChannel channel = ccInfo.getChannelInfo(c);
					if(channel.getBungee()) {
						out.writeUTF(channel.getName());
					}
				}
				// out.writeUTF("Mutes");
				int muteCount = 0;
				for(String c : mcp.getMutes().keySet()) {
					ChatChannel channel = ccInfo.getChannelInfo(c);
					if(channel.getBungee()) {
						muteCount++;
					}
				}
				// System.out.println(muteCount + " mutes");
				out.write(muteCount);
				for(String c : mcp.getMutes().keySet()) {
					ChatChannel channel = ccInfo.getChannelInfo(c);
					if(channel.getBungee()) {
						out.writeUTF(channel.getName());
					}
				}
				int ignoreCount = 0;
				for(@SuppressWarnings("unused")
				UUID c : mcp.getIgnores()) {
					ignoreCount++;
				}
				out.write(ignoreCount);
				for(UUID c : mcp.getIgnores()) {
					out.writeUTF(c.toString());
				}
				out.writeBoolean(mcp.isSpy());
				out.writeBoolean(mcp.getMessageToggle());
			}
			for(MineverseChatPlayer p : MineverseChat.onlinePlayers) {
				p.getPlayer().sendPluginMessage(this, "venturechat:", outstream.toByteArray());
				break;
			}
			// System.out.println("Sync start bottom...");
			out.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void updatePlayerList(MineverseChatPlayer mcp, boolean request) {
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(outstream);
		try {
			out.writeUTF("Sync");
			if(request) {
				out.writeUTF("PlayersReceive");
				// System.out.println(mcp.getPlayer().getServer().getServerName());
				out.writeUTF(this.getServer().getName());
			}
			else {
				out.writeUTF("PlayersUpdate");
				// System.out.println(networkPlayers.keySet().size());
				out.write(networkPlayers.keySet().size());
				for(String p : networkPlayers.keySet()) {
					out.writeUTF(p + "," + networkPlayers.get(p));
				}
			}
			mcp.getPlayer().sendPluginMessage(this, "venturechat:", outstream.toByteArray());
			// System.out.println("Sync start bottom...");
			out.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if(!channel.equals("venturechat:")) {
			return;
		}
		try {
			DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(message));
			if(plugin.getConfig().getString("loglevel", "info").equals("debug")) {
				System.out.println(msgin.available() + " size on receiving end");
			}
			String subchannel = msgin.readUTF();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(stream);
			if(subchannel.equals("Chat")) {
				String chatchannel = msgin.readUTF();
				String chat = msgin.readUTF();
				String playerName = msgin.readUTF();
				String lastMessage = msgin.readUTF();
				String f = msgin.readUTF();
				String c = msgin.readUTF();
				String json = msgin.readUTF();
				if(ccInfo.isChannel(chatchannel) && ccInfo.getChannelInfo(chatchannel).getBungee()) {
					MineverseChat.lastChatMessage = new ChatMessage(playerName, lastMessage, lastMessage.hashCode(), f, c, chatchannel);
					lastJson = json;
					Bukkit.getConsoleSender().sendMessage(chat);
					MineverseChatPlayer sender = MineverseChatAPI.getMineverseChatPlayer(playerName);
					for(MineverseChatPlayer p : MineverseChat.onlinePlayers) {
						//System.out.println(p.getName() + " received chat message");
						if(p.isOnline() && p.getListening().contains(ccInfo.getChannelInfo(chatchannel).getName())) {
							if(!p.getBungeeToggle() && MineverseChatAPI.getOnlineMineverseChatPlayer(playerName) == null) {
								continue;
							}
							if(plugin.getConfig().getBoolean("ignorechat", false)) {
								// System.out.println(p.getIgnores());
								if(sender == null) {
									// System.out.println("null sender");
									p.getPlayer().sendMessage(chat);
									continue;
								}
								if(!p.getIgnores().contains(sender.getUUID())) {
									// System.out.println("Chat sent");
									p.getPlayer().sendMessage(chat);
								}
								continue;
							}
							p.getPlayer().sendMessage(chat);
						}
					}
				}
			}
			if(subchannel.equals("Chwho")) {
				String identifier = msgin.readUTF();
				if(identifier.equals("Get")) {
					String sender = msgin.readUTF();
					String name = msgin.readUTF();
					String chatchannel = msgin.readUTF();
					List<String> listening = new ArrayList<String>();
					if(ccInfo.isChannel(chatchannel)) {
						for(MineverseChatPlayer mcp : onlinePlayers) {
							if(mcp.getListening().contains(chatchannel)) {
								String entry = "&f" + mcp.getName();
								if(mcp.isMuted(chatchannel)) {
									entry = "&c" + mcp.getName();
								}
								listening.add(entry);
							}
						}
					}
					out.writeUTF("Chwho");
					out.writeUTF("Receive");
					out.writeUTF(sender);
					out.writeUTF(name);
					out.writeUTF(chatchannel);
					out.writeInt(listening.size());
					for(String s : listening) {
						out.writeUTF(s);
					}
					player.sendPluginMessage(this, "venturechat:", stream.toByteArray());
				}
				if(identifier.equals("Receive")) {
					String sender = msgin.readUTF();
					String stringchannel = msgin.readUTF();
					MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer(UUID.fromString(sender));
					ChatChannel chatchannel = ccInfo.getChannelInfo(stringchannel);
					String playerList = "";
					int size = msgin.readInt();
					for(int a = 0; a < size; a++) {
						playerList += msgin.readUTF() + ChatColor.WHITE + ", ";
					}
					if(playerList.length() > 2) {
						playerList = playerList.substring(0, playerList.length() - 2);
					}
					mcp.getPlayer().sendMessage(ChatColor.GOLD + "Players in Channel: " + ChatColor.valueOf(chatchannel.getColor().toUpperCase()) + chatchannel.getName());
					mcp.getPlayer().sendMessage(Format.FormatStringAll(playerList));
				}
			}
			if(subchannel.equals("RemoveMessage")) {
				String hash = msgin.readUTF();
				this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "removemessage " + hash);
			}
			if(subchannel.equals("PlayersUpdate")) {
				networkPlayers.clear();
				int size = msgin.read();
				for(int a = 1; a <= size; a++) {
					String p = msgin.readUTF();
					String[] parts = p.split(",");
					networkPlayers.put(parts[0], parts[1]);
					System.out.print(p);
				}
			}
			if(subchannel.equals("Sync")) {
				if(plugin.getConfig().getString("loglevel", "info").equals("debug")) {
					Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Received update..."));
				}
				String uuid = msgin.readUTF();
				MineverseChatPlayer p = MineverseChatAPI.getMineverseChatPlayer(UUID.fromString(uuid));
				for(Object ch : p.getListening().toArray()) {
					String c = ch.toString();
					ChatChannel cha = ccInfo.getChannelInfo(c);
					if(cha.getBungee()) {
						p.removeListening(c);
					}
				}
				int size = msgin.read();
				// System.out.println(size);
				for(int a = 0; a < size; a++) {
					String ch = msgin.readUTF();
					if(ccInfo.isChannel(ch)) {
						ChatChannel cha = ccInfo.getChannelInfo(ch);
						if(cha.hasPermission() && p.getPlayer().hasPermission(cha.getPermission())) {
							p.addListening(ch);
						}
					}
				}
				for(Object o : p.getMutes().keySet().toArray()) {
					ChatChannel ch = ccInfo.getChannelInfo((String) o);
					if(ch.getBungee()) {
						p.removeMute(ch.getName());
					}
				}
				int sizeB = msgin.read();
				// System.out.println(sizeB + " mute size");
				for(int b = 0; b < sizeB; b++) {
					String ch = msgin.readUTF();
					// System.out.println(ch);
					if(ccInfo.isChannel(ch)) {
						p.addMute(ch, 0);
					}
				}
				// System.out.println(msgin.available() + " available before");
				p.setSpy(msgin.readBoolean());
				p.setMessageToggle(msgin.readBoolean());
				// System.out.println(msgin.available() + " available after");
				for(Object o : p.getIgnores().toArray()) {
					p.removeIgnore((UUID) o);
				}
				int sizeC = msgin.read();
				// System.out.println(sizeC + " ignore size");
				for(int c = 0; c < sizeC; c++) {
					String i = msgin.readUTF();
					// System.out.println(i);
					p.addIgnore(UUID.fromString(i));
				}
				if(!p.hasPlayed()) {
					for(ChatChannel ch : ccInfo.getAutojoinList()) {
						if(ch.hasPermission()) {
							if(p.getPlayer().hasPermission(ch.getPermission())) {
								p.addListening(ch.getName());
							}
						}
						else {
							p.addListening(ch.getName());
						}
					}
					p.setHasPlayed(true);
					plugin.synchronize(p, true);
				}
			}
			if(subchannel.equals("Ignore")) {
				String identifier = msgin.readUTF();
				if(identifier.equals("Send")) {
					String server = msgin.readUTF();
					String receiver = msgin.readUTF();
					MineverseChatPlayer p = MineverseChatAPI.getOnlineMineverseChatPlayer(receiver);
					UUID sender = UUID.fromString(msgin.readUTF());
					if(!plugin.getConfig().getBoolean("bungeecordmessaging", true) || p == null || !p.isOnline()) {
						out.writeUTF("Ignore");
						out.writeUTF("Offline");
						out.writeUTF(server);
						out.writeUTF(receiver);
						out.writeUTF(sender.toString());
						player.sendPluginMessage(this, "venturechat:", stream.toByteArray());
						return;
					}
					p.setReplyPlayer(sender);
					out.writeUTF("Ignore");
					out.writeUTF("Echo");
					out.writeUTF(server);
					out.writeUTF(p.getUUID().toString());
					out.writeUTF(sender.toString());
					player.sendPluginMessage(this, "venturechat:", stream.toByteArray());
					return;
				}
				if(identifier.equals("Offline")) {
					String receiver = msgin.readUTF();
					UUID sender = UUID.fromString(msgin.readUTF());
					MineverseChatPlayer p = MineverseChatAPI.getOnlineMineverseChatPlayer(sender);
					p.getPlayer().sendMessage(ChatColor.RED + "Player: " + ChatColor.GOLD + receiver + ChatColor.RED + " is not online.");
				}
				if(identifier.equals("Echo")) {
					UUID receiver = UUID.fromString(msgin.readUTF());
					UUID sender = UUID.fromString(msgin.readUTF());
					MineverseChatPlayer p = MineverseChatAPI.getOnlineMineverseChatPlayer(sender);
					MineverseChatPlayer r = MineverseChatAPI.getMineverseChatPlayer(receiver);
					String rName = receiver.toString();
					if(r != null) {
						rName = Format.FormatStringAll(r.getNickname());
					}
					p.addIgnore(receiver);
					p.getPlayer().sendMessage(ChatColor.GOLD + "You are now ignoring player: " + ChatColor.RED + rName);
					this.synchronize(p, true);
				}
			}
			if(subchannel.equals("Message")) {
				String identifier = msgin.readUTF();
				if(identifier.equals("Send")) {
					String server = msgin.readUTF();
					String receiver = msgin.readUTF();
					MineverseChatPlayer p = MineverseChatAPI.getOnlineMineverseChatPlayer(receiver);
					UUID sender = UUID.fromString(msgin.readUTF());
					String sName = msgin.readUTF();
					MineverseChatPlayer s = MineverseChatAPI.getMineverseChatPlayer(sender);
					String msg = msgin.readUTF();
					String echo = msgin.readUTF();
					String spy = msgin.readUTF();
					// System.out.println((p == null) + " null");
					if(p != null) {
						// System.out.println(p.isOnline() + " online");
					}
					if(!plugin.getConfig().getBoolean("bungeecordmessaging", true) || p == null || !p.isOnline()) {
						out.writeUTF("Message");
						out.writeUTF("Offline");
						out.writeUTF(server);
						out.writeUTF(receiver);
						out.writeUTF(sender.toString());
						player.sendPluginMessage(this, "venturechat:", stream.toByteArray());
						return;
					}
					if(p.getIgnores().contains(sender)) {
						out.writeUTF("Message");
						out.writeUTF("Ignore");
						out.writeUTF(server);
						out.writeUTF(receiver);
						out.writeUTF(sender.toString());
						player.sendPluginMessage(this, "venturechat:", stream.toByteArray());
						return;
					}
					if(!p.getMessageToggle()) {
						out.writeUTF("Message");
						out.writeUTF("Blocked");
						out.writeUTF(server);
						out.writeUTF(receiver);
						out.writeUTF(sender.toString());
						player.sendPluginMessage(this, "venturechat:", stream.toByteArray());
						return;
					}
					if(s != null) {
						sName = Format.FormatStringAll(s.getNickname());
					}
					else {
						UUID uuid = sender;
						String name = sName;
						ChatChannel current = ccInfo.getDefaultChannel();
						Set<UUID> ignores = new HashSet<UUID>();
						Set<String> listening = new HashSet<String>();
						listening.add(current.getName());
						HashMap<String, Integer> mutes = new HashMap<String, Integer>();
						Set<String> blockedCommands = new HashSet<String>();
						List<String> mail = new ArrayList<String>();
						String jsonFormat = "Default";
						s = new MineverseChatPlayer(uuid, name, current, ignores, listening, mutes, blockedCommands, mail, false, null, true, true, name, jsonFormat, false, false, false, true, true);
						MineverseChat.players.add(s);
					}
					p.getPlayer().sendMessage(msg.replace("{playerfrom}", sName).replace("{playerto}", Format.FormatStringAll(p.getNickname())));
					if(p.hasNotifications()) {
						if(VersionHandler.is1_8() || VersionHandler.is1_7_10() || VersionHandler.is1_7_2() || VersionHandler.is1_7_9()) {
							p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.valueOf("LEVEL_UP"), 1, 0);
						}
						else {
							p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1, 0);
						}
					}
					p.setReplyPlayer(sender);
					out.writeUTF("Message");
					out.writeUTF("Echo");
					out.writeUTF(server);
					out.writeUTF(p.getNickname());
					out.writeUTF(sender.toString());
					out.writeUTF(sName);
					out.writeUTF(echo);
					out.writeUTF(spy);
					player.sendPluginMessage(this, "venturechat:", stream.toByteArray());
					return;
				}
				if(identifier.equals("Offline")) {
					String receiver = msgin.readUTF();
					UUID sender = UUID.fromString(msgin.readUTF());
					MineverseChatPlayer p = MineverseChatAPI.getOnlineMineverseChatPlayer(sender);
					p.getPlayer().sendMessage(ChatColor.RED + "Player: " + ChatColor.GOLD + receiver + ChatColor.RED + " is not online.");
					p.setReplyPlayer(null);
				}
				if(identifier.equals("Ignore")) {
					String receiver = msgin.readUTF();
					UUID sender = UUID.fromString(msgin.readUTF());
					MineverseChatPlayer p = MineverseChatAPI.getOnlineMineverseChatPlayer(sender);
					p.getPlayer().sendMessage(ChatColor.GOLD + receiver + " is currently ignoring your messages.");
				}
				if(identifier.equals("Blocked")) {
					String receiver = msgin.readUTF();
					UUID sender = UUID.fromString(msgin.readUTF());
					MineverseChatPlayer p = MineverseChatAPI.getOnlineMineverseChatPlayer(sender);
					p.getPlayer().sendMessage(ChatColor.GOLD + receiver + " is currently blocking messages.");
				}
				if(identifier.equals("Echo")) {
					String receiver = msgin.readUTF();
					UUID sender = UUID.fromString(msgin.readUTF());
					MineverseChatPlayer p = MineverseChatAPI.getOnlineMineverseChatPlayer(sender);
					MineverseChatPlayer r = MineverseChatAPI.getMineverseChatPlayer(receiver);
					String echo = msgin.readUTF();
					String rName = Format.FormatStringAll(receiver);
					if(r != null) {
						rName = Format.FormatStringAll(r.getNickname());
						p.setReplyPlayer(r.getUUID());
					}
					p.getPlayer().sendMessage(echo.replace("{playerfrom}", Format.FormatStringAll(p.getNickname())).replace("{playerto}", rName));
				}
				if(identifier.equals("Spy")) {
					String receiver = msgin.readUTF();
					MineverseChatPlayer r = MineverseChatAPI.getMineverseChatPlayer(receiver);
					UUID sender = UUID.fromString(msgin.readUTF());
					MineverseChatPlayer p = MineverseChatAPI.getOnlineMineverseChatPlayer(sender);
					String sName = msgin.readUTF();
					String spy = msgin.readUTF();
					String rName = receiver;
					if(r != null) {
						rName = Format.FormatStringAll(r.getNickname());
					}
					if(p != null) {
						sName = Format.FormatStringAll(p.getNickname());
					}
					for(MineverseChatPlayer pl : onlinePlayers) {
						if(pl.isSpy() && !pl.getName().equals(sName) && !pl.getName().equals(rName)) {
							pl.getPlayer().sendMessage(spy.replace("{playerto}", rName).replace("{playerfrom}", sName));
						}
					}
				}
			}
			if(subchannel.equals("Mute")) {
				String sendplayer = msgin.readUTF();
				String mutePlayer = msgin.readUTF();
				String chatchannel = msgin.readUTF();
				String server = msgin.readUTF();
				String time = msgin.readUTF();
				int numtime = 0;
				MineverseChatPlayer p = MineverseChatAPI.getMineverseChatPlayer(mutePlayer);
				ChatChannel cc = ccInfo.getChannelInfo(chatchannel);
				if(cc == null) {
					try {
						out.writeUTF("Mute");
						out.writeUTF("Channel");
						out.writeUTF(sendplayer);
						out.writeUTF(chatchannel);
						player.sendPluginMessage(this, "venturechat:", stream.toByteArray());
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					return;
				}
				if(p == null) {
					try {
						out.writeUTF("Mute");
						out.writeUTF("Player");
						out.writeUTF(sendplayer);
						out.writeUTF(mutePlayer);
						out.writeUTF(server);
						player.sendPluginMessage(this, "venturechat:", stream.toByteArray());
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					return;
				}
				if(!cc.isMutable()) {
					try {
						out.writeUTF("Mute");
						out.writeUTF("Mutable");
						out.writeUTF(sendplayer);
						out.writeUTF(cc.getName());
						out.writeUTF(cc.getColor());
						player.sendPluginMessage(this, "venturechat:", stream.toByteArray());
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					return;
				}
				if(p.isMuted(cc.getName())) {
					try {
						out.writeUTF("Mute");
						out.writeUTF("Already");
						out.writeUTF(sendplayer);
						out.writeUTF(mutePlayer);
						out.writeUTF(cc.getName());
						out.writeUTF(cc.getColor());
						player.sendPluginMessage(this, "venturechat:", stream.toByteArray());
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					return;
				}
				if(!time.equals("None\n")) {
					try {
						numtime = Integer.parseInt(time);
						if(numtime > 0) {
							Calendar currentDate = Calendar.getInstance();
							SimpleDateFormat formatter = new SimpleDateFormat("dd:HH:mm:ss");
							String date = formatter.format(currentDate.getTime());
							String[] datearray = date.split(":");
							int datetime = (Integer.parseInt(datearray[0]) * 1440) + (Integer.parseInt(datearray[1]) * 60) + (Integer.parseInt(datearray[2]));
							p.addMute(cc.getName(), datetime + numtime);
							String keyword = "minutes";
							if(numtime == 1) keyword = "minute";
							if(p.isOnline()) p.getPlayer().sendMessage(ChatColor.RED + "You have just been muted in: " + ChatColor.valueOf(cc.getColor().toUpperCase()) + cc.getName() + ChatColor.RED + " for " + time + " " + keyword);
							else p.setModified(true);
							if(cc.getBungee()) {
								MineverseChat.getInstance().synchronize(p, true);
							}
							try {
								out.writeUTF("Mute");
								out.writeUTF("Valid");
								out.writeUTF(sendplayer);
								out.writeUTF(mutePlayer);
								out.writeUTF(cc.getName());
								out.writeUTF(cc.getColor());
								out.writeUTF(time);
								player.sendPluginMessage(this, "venturechat:", stream.toByteArray());
							}
							catch(Exception e) {
								e.printStackTrace();
							}
							return;
						}
						try {
							out.writeUTF("Mute");
							out.writeUTF("Time");
							out.writeUTF(sendplayer);
							out.writeUTF(time);
							player.sendPluginMessage(this, "venturechat:", stream.toByteArray());
						}
						catch(Exception e) {
							e.printStackTrace();
						}
						return;
					}
					catch(Exception e) {
						try {
							out.writeUTF("Mute");
							out.writeUTF("Time");
							out.writeUTF(sendplayer);
							out.writeUTF(time);
							player.sendPluginMessage(this, "venturechat:", stream.toByteArray());
						}
						catch(Exception e1) {
							e1.printStackTrace();
						}
						return;
					}
				}
				p.addMute(cc.getName(), 0);
				if(p.isOnline()) p.getPlayer().sendMessage(ChatColor.RED + "You have just been muted in: " + ChatColor.valueOf(cc.getColor().toUpperCase()) + cc.getName());
				else p.setModified(true);
				if(cc.getBungee()) {
					MineverseChat.getInstance().synchronize(p, true);
				}
				try {
					out.writeUTF("Mute");
					out.writeUTF("Valid");
					out.writeUTF(sendplayer);
					out.writeUTF(mutePlayer);
					out.writeUTF(cc.getName());
					out.writeUTF(cc.getColor());
					out.writeUTF(time);
					player.sendPluginMessage(this, "venturechat:", stream.toByteArray());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				return;
			}
			if(subchannel.equals("Muteall")) {
				String sendplayer = msgin.readUTF();
				String muteplayer = msgin.readUTF();
				String server = msgin.readUTF();
				Player mp = Bukkit.getPlayer(muteplayer);
				MineverseChatPlayer p = MineverseChatAPI.getMineverseChatPlayer(mp);
				if(mp == null) {
					try {
						out.writeUTF("Muteall");
						out.writeUTF("Player");
						out.writeUTF(sendplayer);
						out.writeUTF(muteplayer);
						out.writeUTF(server);
						player.sendPluginMessage(this, "venturechat:", stream.toByteArray());
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					return;
				}
				boolean bungee = false;
				for(ChatChannel c : ccInfo.getChannelsInfo()) {
					if(c.isMutable()) {
						p.addMute(c.getName(), 0);
						if(c.getBungee()) {
							bungee = true;
						}
					}
				}
				if(bungee) {
					MineverseChat.getInstance().synchronize(p, true);
				}
				if(p.isOnline()) {
					p.getPlayer().sendMessage(ChatColor.RED + "You have just been muted in all channels.");
				}
				else p.setModified(true);
				try {
					out.writeUTF("Muteall");
					out.writeUTF("Valid");
					out.writeUTF(sendplayer);
					out.writeUTF(muteplayer);
					player.sendPluginMessage(this, "venturechat:", stream.toByteArray());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				return;
			}
			if(subchannel.equals("Unmuteall")) {
				String sendplayer = msgin.readUTF();
				String muteplayer = msgin.readUTF();
				String server = msgin.readUTF();
				Player mp = Bukkit.getPlayer(muteplayer);
				MineverseChatPlayer p = MineverseChatAPI.getMineverseChatPlayer(mp);
				if(mp == null) {
					try {
						out.writeUTF("Unmuteall");
						out.writeUTF("Player");
						out.writeUTF(sendplayer);
						out.writeUTF(muteplayer);
						out.writeUTF(server);
						player.sendPluginMessage(this, "venturechat:", stream.toByteArray());
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					return;
				}
				boolean bungee = false;
				for(ChatChannel c : ccInfo.getChannelsInfo()) {
					p.removeMute(c.getName());
					if(c.getBungee()) {
						bungee = true;
					}
				}
				if(bungee) {
					MineverseChat.getInstance().synchronize(p, true);
				}
				if(p.isOnline()) {
					p.getPlayer().sendMessage(ChatColor.RED + "You have just been unmuted in all channels.");
				}
				else p.setModified(true);
				try {
					out.writeUTF("Unmuteall");
					out.writeUTF("Valid");
					out.writeUTF(sendplayer);
					out.writeUTF(muteplayer);
					player.sendPluginMessage(this, "venturechat:", stream.toByteArray());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				return;
			}
			if(subchannel.equals("Unmute")) {
				String sendplayer = msgin.readUTF();
				String mutePlayer = msgin.readUTF();
				String chatchannel = msgin.readUTF();
				String server = msgin.readUTF();
				MineverseChatPlayer p = MineverseChatAPI.getMineverseChatPlayer(mutePlayer);
				ChatChannel cc = ccInfo.getChannelInfo(chatchannel);
				if(cc == null) {
					try {
						out.writeUTF("Unmute");
						out.writeUTF("Channel");
						out.writeUTF(sendplayer);
						out.writeUTF(chatchannel);
						player.sendPluginMessage(this, "venturechat:", stream.toByteArray());
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					return;
				}
				if(p == null) {
					try {
						out.writeUTF("Unmute");
						out.writeUTF("Player");
						out.writeUTF(sendplayer);
						out.writeUTF(mutePlayer);
						out.writeUTF(server);
						player.sendPluginMessage(this, "venturechat:", stream.toByteArray());
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					return;
				}
				if(!p.isMuted(cc.getName())) {
					try {
						out.writeUTF("Unmute");
						out.writeUTF("Already");
						out.writeUTF(sendplayer);
						out.writeUTF(mutePlayer);
						out.writeUTF(cc.getName());
						out.writeUTF(cc.getColor());
						player.sendPluginMessage(this, "venturechat:", stream.toByteArray());
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					return;
				}
				p.removeMute(cc.getName());
				if(p.isOnline()) p.getPlayer().sendMessage(ChatColor.RED + "You have just been unmuted in: " + ChatColor.valueOf(cc.getColor().toUpperCase()) + cc.getName());
				else p.setModified(true);
				if(cc.getBungee()) {
					MineverseChat.getInstance().synchronize(p, true);
				}
				try {
					out.writeUTF("Unmute");
					out.writeUTF("Valid");
					out.writeUTF(sendplayer);
					out.writeUTF(mutePlayer);
					out.writeUTF(cc.getName());
					out.writeUTF(cc.getColor());
					player.sendPluginMessage(this, "venturechat:", stream.toByteArray());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				return;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}