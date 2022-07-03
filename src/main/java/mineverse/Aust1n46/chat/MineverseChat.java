package mineverse.Aust1n46.chat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitScheduler;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;

import me.clip.placeholderapi.PlaceholderAPI;
import mineverse.Aust1n46.chat.alias.Alias;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.api.events.VentureChatEvent;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.channel.ChatChannelInfo;
import mineverse.Aust1n46.chat.command.VentureCommandExecutor;
import mineverse.Aust1n46.chat.command.chat.Channel;
import mineverse.Aust1n46.chat.command.mute.MuteContainer;
import mineverse.Aust1n46.chat.database.Database;
import mineverse.Aust1n46.chat.database.PlayerData;
import mineverse.Aust1n46.chat.gui.GuiSlot;
import mineverse.Aust1n46.chat.json.JsonFormat;
import mineverse.Aust1n46.chat.listeners.ChatListener;
import mineverse.Aust1n46.chat.listeners.CommandListener;
import mineverse.Aust1n46.chat.listeners.LoginListener;
import mineverse.Aust1n46.chat.listeners.PacketListenerLegacyChat;
import mineverse.Aust1n46.chat.listeners.SignListener;
import mineverse.Aust1n46.chat.localization.Localization;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;
import mineverse.Aust1n46.chat.utilities.Format;
import mineverse.Aust1n46.chat.versions.VersionHandler;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

/**
 * VentureChat Minecraft plugin for servers running Spigot or Paper software.
 *
 * @author Aust1n46
 */
public class MineverseChat extends JavaPlugin implements PluginMessageListener {
	// Plugin Messaging Channel
	public static final String PLUGIN_MESSAGING_CHANNEL = "venturechat:data";
	
	// Event constants
	public static final boolean ASYNC = true;
	public static final boolean SYNC = false;
	
	public static final int LINE_LENGTH = 40;
	
	// DiscordSRV backwards compatibility
	@Deprecated
	public static ChatChannelInfo ccInfo;

	@Deprecated
	public static Set<MineverseChatPlayer> players = new HashSet<MineverseChatPlayer>();
	@Deprecated
	public static Set<MineverseChatPlayer> onlinePlayers = new HashSet<MineverseChatPlayer>();

	// Vault
	private static Permission permission = null;
	private static Chat chat = null;
	
	@Override
	public void onEnable() {
		ccInfo = new ChatChannelInfo();
		
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
			saveResource("example_config_always_up_to_date!.yml", true);
		}
		catch(Exception ex) {
			Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - &cCould not load configuration! Something unexpected went wrong!"));
		}
		
		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Checking for Vault..."));
		
		if(!setupPermissions() || !setupChat()) {
			Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - &cCould not find Vault and/or a Vault compatible permissions plugin!"));
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		initializeConfigReaders();
		
		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Loading player data"));
		PlayerData.loadLegacyPlayerData();
		PlayerData.loadPlayerData();
		
		Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
			Database.initializeMySQL();
		});

		VentureCommandExecutor.initialize();

		registerListeners();
		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Registering Listeners"));
		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Attaching to Executors"));
		
		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Establishing BungeeCord"));
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, PLUGIN_MESSAGING_CHANNEL);
		Bukkit.getMessenger().registerIncomingPluginChannel(this, PLUGIN_MESSAGING_CHANNEL, this);
		
		PluginManager pluginManager = getServer().getPluginManager();
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
		if(pluginManager.isPluginEnabled("PlaceholderAPI")) {
			Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Enabling PlaceholderAPI Hook"));
		}
		
		new VentureChatPlaceholders().register();
		
		startRepeatingTasks();
		
		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Enabled Successfully"));	
	}
	
	@Override
	public void onDisable() {
		PlayerData.savePlayerData();
		MineverseChatAPI.clearMineverseChatPlayerMap();
		MineverseChatAPI.clearNameMap();
		MineverseChatAPI.clearOnlineMineverseChatPlayerMap();
		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Disabling..."));
		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Disabled Successfully"));
	}
	
	private void startRepeatingTasks() {
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.runTaskTimerAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				PlayerData.savePlayerData();
				if(getConfig().getString("loglevel", "info").equals("debug")) {
					Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Saving Player Data"));
				}
			}
		}, 0L, getConfig().getInt("saveinterval") * 1200); //one minute * save interval
		
		scheduler.runTaskTimerAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				for (MineverseChatPlayer p : MineverseChatAPI.getOnlineMineverseChatPlayers()) {
					long currentTimeMillis = System.currentTimeMillis();
					Iterator<MuteContainer> iterator = p.getMutes().iterator();
					while (iterator.hasNext()) {
						MuteContainer mute = iterator.next();
						if(ChatChannel.isChannel(mute.getChannel())) {
							ChatChannel channel = ChatChannel.getChannel(mute.getChannel());
							long timemark = mute.getDuration();
							if (timemark == 0) {
								continue;
							}
							if (getConfig().getString("loglevel", "info").equals("debug")) {
								System.out.println(currentTimeMillis + " " + timemark);
							}
							if (currentTimeMillis >= timemark) {
								iterator.remove();
								p.getPlayer().sendMessage(LocalizedMessage.UNMUTE_PLAYER_PLAYER.toString()
										.replace("{player}", p.getName()).replace("{channel_color}", channel.getColor())
										.replace("{channel_name}", mute.getChannel()));
								if(channel.getBungee()) {
									synchronize(p, true);
								}
							}
						}
					}
				}
				if (getConfig().getString("loglevel", "info").equals("trace")) {
					Bukkit.getConsoleSender()
							.sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Updating Player Mutes"));
				}
			}
		}, 0L, 60L); // three second interval
	}
	
	private void registerListeners() {
		PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(new Channel(), this);
		pluginManager.registerEvents(new ChatListener(), this);
		pluginManager.registerEvents(new SignListener(), this);
		pluginManager.registerEvents(new CommandListener(), this);
		pluginManager.registerEvents(new LoginListener(), this);
		if (VersionHandler.isUnder_1_19()) {
			ProtocolLibrary.getProtocolManager().addPacketListener(new PacketListenerLegacyChat());
		}
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
	
	public static MineverseChat getInstance() {
		return getPlugin(MineverseChat.class);	
	}
	
	public static void initializeConfigReaders() {
		Localization.initialize();
		Alias.initialize();
		JsonFormat.initialize();
		GuiSlot.initialize();
		ChatChannel.initialize();
	}
	
	public static Chat getVaultChat() {
		return chat;
	}
	
	public static Permission getVaultPermission() {
		return permission;
	}

	public static void synchronize(MineverseChatPlayer mcp, boolean changes) {
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
				Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(getInstance(), new Runnable() {
					@Override
					public void run() {
						if(!mcp.isOnline() || mcp.hasPlayed()) {
							return;
						}
						synchronize(mcp, false);
					}
				}, 20L); // one second delay before running again
			}
			else {
				out.writeUTF("Update");
				out.writeUTF(mcp.getUUID().toString());
				// out.writeUTF("Channels");
				int channelCount = 0;
				for(String c : mcp.getListening()) {
					ChatChannel channel = ChatChannel.getChannel(c);
					if(channel.getBungee()) {
						channelCount++;
					}
				}
				out.write(channelCount);
				for(String c : mcp.getListening()) {
					ChatChannel channel = ChatChannel.getChannel(c);
					if(channel.getBungee()) {
						out.writeUTF(channel.getName());
					}
				}
				// out.writeUTF("Mutes");
				int muteCount = 0;
				for(MuteContainer mute : mcp.getMutes()) {
					ChatChannel channel = ChatChannel.getChannel(mute.getChannel());
					if(channel.getBungee()) {
						muteCount++;
					}
				}
				// System.out.println(muteCount + " mutes");
				out.write(muteCount);
				for(MuteContainer mute : mcp.getMutes()) {
					ChatChannel channel = ChatChannel.getChannel(mute.getChannel());
					if(channel.getBungee()) {
						out.writeUTF(channel.getName());
						out.writeLong(mute.getDuration());
						out.writeUTF(mute.getReason());
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
			sendPluginMessage(outstream);
			// System.out.println("Sync start bottom...");
			out.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void sendPluginMessage(ByteArrayOutputStream byteOutStream) {
		if(MineverseChatAPI.getOnlineMineverseChatPlayers().size() > 0) {
			MineverseChatAPI.getOnlineMineverseChatPlayers().iterator().next().getPlayer().sendPluginMessage(getInstance(), PLUGIN_MESSAGING_CHANNEL, byteOutStream.toByteArray());
		}
	}
	
	public static void sendDiscordSRVPluginMessage(String chatChannel, String message) {
		if(MineverseChatAPI.getOnlineMineverseChatPlayers().size() == 0) {
			return;
		}
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteOutStream);
		try {
			out.writeUTF("DiscordSRV");
			out.writeUTF(chatChannel);
			out.writeUTF(message);
			sendPluginMessage(byteOutStream);
			out.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] inputStream) {
		if(!channel.equals(PLUGIN_MESSAGING_CHANNEL)) {
			return;
		}
		try {
			DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(inputStream));
			if(getConfig().getString("loglevel", "info").equals("debug")) {
				System.out.println(msgin.available() + " size on receiving end");
			}
			String subchannel = msgin.readUTF();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(stream);
			if(subchannel.equals("Chat")) {
				String server = msgin.readUTF();
				String chatchannel = msgin.readUTF();
				String senderName = msgin.readUTF();
				UUID senderUUID = UUID.fromString(msgin.readUTF());
				int hash = msgin.readInt();
				String format = msgin.readUTF();
				String chat = msgin.readUTF();
				String consoleChat = format + chat;
				String globalJSON = msgin.readUTF();
				String primaryGroup = msgin.readUTF();
				String nickname = msgin.readUTF();
				
				if(!ChatChannel.isChannel(chatchannel)) {
					return;
				}
				ChatChannel chatChannelObject = ChatChannel.getChannel(chatchannel);
				
				if(!chatChannelObject.getBungee()) {
					return;
				}
				
				Set<Player> recipients = new HashSet<Player>();
				for(MineverseChatPlayer p : MineverseChatAPI.getOnlineMineverseChatPlayers()) {
					if(p.isListening(chatChannelObject.getName())) {
						recipients.add(p.getPlayer());
					}
				}
				
				Bukkit.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
					@Override
					public void run() {
						//Create VentureChatEvent
						VentureChatEvent ventureChatEvent = new VentureChatEvent(null, senderName, nickname, primaryGroup, chatChannelObject, recipients, recipients.size(), format, chat, globalJSON, hash, false);
						//Fire event and wait for other plugin listeners to act on it
						Bukkit.getServer().getPluginManager().callEvent(ventureChatEvent);
					}
				});
				
				Bukkit.getConsoleSender().sendMessage(consoleChat);
				
				if(Database.isEnabled()) {
					Database.writeVentureChat(senderUUID.toString(), senderName, server, chatchannel, chat.replace("'", "''"), "Chat");
				}
				
				for(MineverseChatPlayer p : MineverseChatAPI.getOnlineMineverseChatPlayers()) {
					if(p.isListening(chatChannelObject.getName())) {
						if(!p.getBungeeToggle() && MineverseChatAPI.getOnlineMineverseChatPlayer(senderName) == null) {
							continue;
						}
						
						String json = Format.formatModerationGUI(globalJSON, p.getPlayer(), senderName, chatchannel, hash);
						PacketContainer packet = Format.createPacketPlayOutChat(json);
						
						if(getConfig().getBoolean("ignorechat", false)) {
							if(!p.getIgnores().contains(senderUUID)) {
								// System.out.println("Chat sent");
								Format.sendPacketPlayOutChat(p.getPlayer(), packet);							
							}
							continue;
						}
						Format.sendPacketPlayOutChat(p.getPlayer(), packet);	
					}
				}
			}
			if(subchannel.equals("DiscordSRV")) {
				String chatChannel = msgin.readUTF();
				String message = msgin.readUTF();
				if(!ChatChannel.isChannel(chatChannel)) {
					return;
				}
				ChatChannel chatChannelObj = ChatChannel.getChannel(chatChannel);
				if(!chatChannelObj.getBungee()) {
					return;
				}	
				
				String json = Format.convertPlainTextToJson(message, true);
				int hash = (message.replaceAll("([�]([a-z0-9]))", "")).hashCode();
				
				for(MineverseChatPlayer p : MineverseChatAPI.getOnlineMineverseChatPlayers()) {
					if(p.isListening(chatChannelObj.getName())) {
						String finalJSON = Format.formatModerationGUI(json, p.getPlayer(), "Discord", chatChannelObj.getName(), hash);
						PacketContainer packet = Format.createPacketPlayOutChat(finalJSON);
						Format.sendPacketPlayOutChat(p.getPlayer(), packet);
					}
				}	
			}
			if(subchannel.equals("PlayerNames")) {
				MineverseChatAPI.clearNetworkPlayerNames();
				int playerCount = msgin.readInt();
				for(int a = 0; a < playerCount; a ++) {
					MineverseChatAPI.addNetworkPlayerName(msgin.readUTF());
				}
			}
			if(subchannel.equals("Chwho")) {
				String identifier = msgin.readUTF();
				if(identifier.equals("Get")) {
					String server = msgin.readUTF();
					String sender = msgin.readUTF();
					String chatchannel = msgin.readUTF();
					List<String> listening = new ArrayList<String>();
					if(ChatChannel.isChannel(chatchannel)) {
						for(MineverseChatPlayer mcp : MineverseChatAPI.getOnlineMineverseChatPlayers()) {
							if(mcp.isListening(chatchannel)) {
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
					out.writeUTF(server);
					out.writeUTF(sender);
					out.writeUTF(chatchannel);
					out.writeInt(listening.size());
					for(String s : listening) {
						out.writeUTF(s);
					}
					sendPluginMessage(stream);
				}
				if(identifier.equals("Receive")) {
					String sender = msgin.readUTF();
					String stringchannel = msgin.readUTF();
					MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer(UUID.fromString(sender));
					ChatChannel chatchannel = ChatChannel.getChannel(stringchannel);
					String playerList = "";
					int size = msgin.readInt();
					for(int a = 0; a < size; a++) {
						playerList += msgin.readUTF() + ChatColor.WHITE + ", ";
					}
					if(playerList.length() > 2) {
						playerList = playerList.substring(0, playerList.length() - 2);
					}
					mcp.getPlayer().sendMessage(LocalizedMessage.CHANNEL_PLAYER_LIST_HEADER.toString()
							.replace("{channel_color}", chatchannel.getColor().toString())
							.replace("{channel_name}", chatchannel.getName()));
					mcp.getPlayer().sendMessage(Format.FormatStringAll(playerList));
				}
			}
			if(subchannel.equals("RemoveMessage")) {
				String hash = msgin.readUTF();
				getServer().dispatchCommand(this.getServer().getConsoleSender(), "removemessage " + hash);
			}
			if(subchannel.equals("Sync")) {
				if(getConfig().getString("loglevel", "info").equals("debug")) {
					Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Received update..."));
				}
				String uuid = msgin.readUTF();
				MineverseChatPlayer p = MineverseChatAPI.getOnlineMineverseChatPlayer(UUID.fromString(uuid));
				if(p == null || p.hasPlayed()) {
					return;
				}
				for(Object ch : p.getListening().toArray()) {
					String c = ch.toString();
					ChatChannel cha = ChatChannel.getChannel(c);
					if(cha.getBungee()) {
						p.removeListening(c);
					}
				}
				int size = msgin.read();
				for(int a = 0; a < size; a++) {
					String ch = msgin.readUTF();
					if(ChatChannel.isChannel(ch)) {
						ChatChannel cha = ChatChannel.getChannel(ch);
						if(!cha.hasPermission() || p.getPlayer().hasPermission(cha.getPermission())) {
							p.addListening(ch);
						}
					}
				}
				p.getMutes().removeIf(mute -> ChatChannel.getChannel(mute.getChannel()).getBungee());
				int sizeB = msgin.read();
				// System.out.println(sizeB + " mute size");
				for(int b = 0; b < sizeB; b++) {
					String ch = msgin.readUTF();
					long muteTime = msgin.readLong();
					String muteReason = msgin.readUTF();
					// System.out.println(ch);
					if(ChatChannel.isChannel(ch)) {
						p.addMute(ch, muteTime, muteReason);
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
					boolean isThereABungeeChannel = false;
					for(ChatChannel ch : ChatChannel.getAutojoinList()) {
						if((!ch.hasPermission() || p.getPlayer().hasPermission(ch.getPermission())) && !p.isListening(ch.getName())) {	
							p.addListening(ch.getName());	
							if(ch.getBungee()) {
								isThereABungeeChannel = true;
							}
						}
					}
					p.setHasPlayed(true);
					// Only run a sync update if the player joined a BungeeCord channel
					if(isThereABungeeChannel) {
						synchronize(p, true);
					}
				}
			}
			if(subchannel.equals("Ignore")) {
				String identifier = msgin.readUTF();
				if(identifier.equals("Send")) {
					String server = msgin.readUTF();
					String receiver = msgin.readUTF();
					MineverseChatPlayer p = MineverseChatAPI.getOnlineMineverseChatPlayer(receiver);
					UUID sender = UUID.fromString(msgin.readUTF());
					if(!getConfig().getBoolean("bungeecordmessaging", true) || p == null || !p.isOnline()) {
						out.writeUTF("Ignore");
						out.writeUTF("Offline");
						out.writeUTF(server);
						out.writeUTF(receiver);
						out.writeUTF(sender.toString());
						sendPluginMessage(stream);
						return;
					}
					out.writeUTF("Ignore");
					out.writeUTF("Echo");
					out.writeUTF(server);
					out.writeUTF(p.getUUID().toString());
					out.writeUTF(receiver);
					out.writeUTF(sender.toString());
					sendPluginMessage(stream);
					return;
				}
				if(identifier.equals("Offline")) {
					String receiver = msgin.readUTF();
					UUID sender = UUID.fromString(msgin.readUTF());
					MineverseChatPlayer p = MineverseChatAPI.getOnlineMineverseChatPlayer(sender);
					p.getPlayer().sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString()
							.replace("{args}", receiver));
				}
				if(identifier.equals("Echo")) {
					UUID receiver = UUID.fromString(msgin.readUTF());
					String receiverName = msgin.readUTF();
					UUID sender = UUID.fromString(msgin.readUTF());
					MineverseChatPlayer p = MineverseChatAPI.getOnlineMineverseChatPlayer(sender);
					
					if(p.getIgnores().contains(receiver)) {
						p.getPlayer().sendMessage(LocalizedMessage.IGNORE_PLAYER_OFF.toString()
								.replace("{player}", receiverName));
						p.removeIgnore(receiver);
						synchronize(p, true);
						return;
					}
					
					p.addIgnore(receiver);
					p.getPlayer().sendMessage(LocalizedMessage.IGNORE_PLAYER_ON.toString()
							.replace("{player}", receiverName));
					synchronize(p, true);
				}
			}
			if(subchannel.equals("Mute")) {
				String identifier = msgin.readUTF();
				if(identifier.equals("Send")) {
					String server = msgin.readUTF();
					String senderIdentifier = msgin.readUTF();
					String temporaryDataInstanceUUIDString = msgin.readUTF();
					String playerToMute = msgin.readUTF();
					String channelName = msgin.readUTF();
					long time = msgin.readLong();
					String reason = msgin.readUTF();
					MineverseChatPlayer playerToMuteMCP = MineverseChatAPI.getOnlineMineverseChatPlayer(playerToMute);
					if(playerToMuteMCP == null) {
						out.writeUTF("Mute");
						out.writeUTF("Offline");
						out.writeUTF(server);
						out.writeUTF(temporaryDataInstanceUUIDString);
						out.writeUTF(senderIdentifier);
						out.writeUTF(playerToMute);
						sendPluginMessage(stream);
						return;
					}
					if(!ChatChannel.isChannel(channelName)) {
						return;
					}
					ChatChannel chatChannelObj = ChatChannel.getChannel(channelName);
					if (playerToMuteMCP.isMuted(chatChannelObj.getName())) {
						out.writeUTF("Mute");
						out.writeUTF("AlreadyMuted");
						out.writeUTF(server);
						out.writeUTF(senderIdentifier);
						out.writeUTF(playerToMute);
						out.writeUTF(channelName);
						sendPluginMessage(stream);
						return;
					}
					if(time > 0) {
						long datetime = System.currentTimeMillis();
						if(reason.isEmpty()) {
							playerToMuteMCP.addMute(chatChannelObj.getName(), datetime + time);
							String timeString = Format.parseTimeStringFromMillis(time);
							playerToMuteMCP.getPlayer()
									.sendMessage(LocalizedMessage.MUTE_PLAYER_PLAYER_TIME.toString()
									.replace("{channel_color}", chatChannelObj.getColor())
									.replace("{channel_name}", chatChannelObj.getName())
									.replace("{time}", timeString));
						}
						else {
							playerToMuteMCP.addMute(chatChannelObj.getName(), datetime + time, reason);
							String timeString = Format.parseTimeStringFromMillis(time);
							playerToMuteMCP.getPlayer()
									.sendMessage(LocalizedMessage.MUTE_PLAYER_PLAYER_TIME_REASON.toString()
									.replace("{channel_color}", chatChannelObj.getColor())
									.replace("{channel_name}", chatChannelObj.getName())
									.replace("{time}", timeString)
									.replace("{reason}", reason));
						}
					}
					else {
						if(reason.isEmpty()) {
							playerToMuteMCP.addMute(chatChannelObj.getName());
							playerToMuteMCP.getPlayer()
									.sendMessage(LocalizedMessage.MUTE_PLAYER_PLAYER.toString()
									.replace("{channel_color}", chatChannelObj.getColor())
									.replace("{channel_name}", chatChannelObj.getName()));
						}
						else {
							playerToMuteMCP.addMute(chatChannelObj.getName(), reason);
							playerToMuteMCP.getPlayer()
									.sendMessage(LocalizedMessage.MUTE_PLAYER_PLAYER_REASON.toString()
									.replace("{channel_color}", chatChannelObj.getColor())
									.replace("{channel_name}", chatChannelObj.getName())
									.replace("{reason}", reason));
						}
					}
					synchronize(playerToMuteMCP, true);
					out.writeUTF("Mute");
					out.writeUTF("Valid");
					out.writeUTF(server);
					out.writeUTF(senderIdentifier);
					out.writeUTF(playerToMute);
					out.writeUTF(channelName);
					out.writeLong(time);
					out.writeUTF(reason);
					sendPluginMessage(stream);
					return;
				}
				if(identifier.equals("Valid")) {
					String senderIdentifier = msgin.readUTF();
					String playerToMute = msgin.readUTF();
					String channelName = msgin.readUTF();
					long time = msgin.readLong();
					String reason = msgin.readUTF();
					if(!ChatChannel.isChannel(channelName)) {
						return;
					}
					ChatChannel chatChannelObj = ChatChannel.getChannel(channelName);
					if(time > 0) {
						String timeString = Format.parseTimeStringFromMillis(time);
						if(reason.isEmpty()) {
							if(senderIdentifier.equals("VentureChat:Console")) {
								Bukkit.getConsoleSender().sendMessage(LocalizedMessage.MUTE_PLAYER_SENDER_TIME.toString()
										.replace("{player}", playerToMute)
										.replace("{channel_color}", chatChannelObj.getColor())
										.replace("{channel_name}", chatChannelObj.getName())
										.replace("{time}", timeString));
							}
							else {
								UUID sender = UUID.fromString(senderIdentifier);
								MineverseChatPlayer senderMCP = MineverseChatAPI.getOnlineMineverseChatPlayer(sender);
								senderMCP.getPlayer().sendMessage(LocalizedMessage.MUTE_PLAYER_SENDER_TIME.toString()
										.replace("{player}", playerToMute)
										.replace("{channel_color}", chatChannelObj.getColor())
										.replace("{channel_name}", chatChannelObj.getName())
										.replace("{time}", timeString));
							}
						}
						else {
							if(senderIdentifier.equals("VentureChat:Console")) {
								Bukkit.getConsoleSender().sendMessage(LocalizedMessage.MUTE_PLAYER_SENDER_TIME_REASON.toString()
										.replace("{player}", playerToMute)
										.replace("{channel_color}", chatChannelObj.getColor())
										.replace("{channel_name}", chatChannelObj.getName())
										.replace("{time}", timeString)
										.replace("{reason}", reason));
							}
							else {
								UUID sender = UUID.fromString(senderIdentifier);
								MineverseChatPlayer senderMCP = MineverseChatAPI.getOnlineMineverseChatPlayer(sender);
								senderMCP.getPlayer().sendMessage(LocalizedMessage.MUTE_PLAYER_SENDER_TIME_REASON.toString()
										.replace("{player}", playerToMute)
										.replace("{channel_color}", chatChannelObj.getColor())
										.replace("{channel_name}", chatChannelObj.getName())
										.replace("{time}", timeString)
										.replace("{reason}", reason));
							}
						}
					}
					else {
						if(reason.isEmpty()) {
							if(senderIdentifier.equals("VentureChat:Console")) {
								Bukkit.getConsoleSender().sendMessage(LocalizedMessage.MUTE_PLAYER_SENDER.toString()
										.replace("{player}", playerToMute)
										.replace("{channel_color}", chatChannelObj.getColor())
										.replace("{channel_name}", chatChannelObj.getName()));
							}
							else {
								UUID sender = UUID.fromString(senderIdentifier);
								MineverseChatPlayer senderMCP = MineverseChatAPI.getOnlineMineverseChatPlayer(sender);
								senderMCP.getPlayer().sendMessage(LocalizedMessage.MUTE_PLAYER_SENDER.toString()
										.replace("{player}", playerToMute)
										.replace("{channel_color}", chatChannelObj.getColor())
										.replace("{channel_name}", chatChannelObj.getName()));
							}
						}
						else {
							if(senderIdentifier.equals("VentureChat:Console")) {
								Bukkit.getConsoleSender().sendMessage(LocalizedMessage.MUTE_PLAYER_SENDER_REASON.toString()
										.replace("{player}", playerToMute)
										.replace("{channel_color}", chatChannelObj.getColor())
										.replace("{channel_name}", chatChannelObj.getName())
										.replace("{reason}", reason));
							}
							else {
								UUID sender = UUID.fromString(senderIdentifier);
								MineverseChatPlayer senderMCP = MineverseChatAPI.getOnlineMineverseChatPlayer(sender);
								senderMCP.getPlayer().sendMessage(LocalizedMessage.MUTE_PLAYER_SENDER_REASON.toString()
										.replace("{player}", playerToMute)
										.replace("{channel_color}", chatChannelObj.getColor())
										.replace("{channel_name}", chatChannelObj.getName())
										.replace("{reason}", reason));
							}
						}
					}
					return;
				}
				if(identifier.equals("Offline")) {
					String senderIdentifier = msgin.readUTF();
					String playerToMute = msgin.readUTF();
					if(senderIdentifier.equals("VentureChat:Console")) {
						Bukkit.getConsoleSender().sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString()
								.replace("{args}", playerToMute));
						return;
					}
					UUID sender = UUID.fromString(senderIdentifier);
					MineverseChatPlayer senderMCP = MineverseChatAPI.getOnlineMineverseChatPlayer(sender);
					senderMCP.getPlayer().sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString()
							.replace("{args}", playerToMute));
					return;
				}
				if(identifier.equals("AlreadyMuted")) {
					String senderIdentifier = msgin.readUTF();
					String playerToMute = msgin.readUTF();
					String channelName = msgin.readUTF();
					if(!ChatChannel.isChannel(channelName)) {
						return;
					}
					ChatChannel chatChannelObj = ChatChannel.getChannel(channelName);
					if(senderIdentifier.equals("VentureChat:Console")) {
						Bukkit.getConsoleSender().sendMessage(LocalizedMessage.PLAYER_ALREADY_MUTED.toString()
								.replace("{player}", playerToMute).replace("{channel_color}", chatChannelObj.getColor())
								.replace("{channel_name}", chatChannelObj.getName()));
						return;
					}
					UUID sender = UUID.fromString(senderIdentifier);
					MineverseChatPlayer senderMCP = MineverseChatAPI.getOnlineMineverseChatPlayer(sender);
					senderMCP.getPlayer().sendMessage(LocalizedMessage.PLAYER_ALREADY_MUTED.toString()
							.replace("{player}", playerToMute).replace("{channel_color}", chatChannelObj.getColor())
							.replace("{channel_name}", chatChannelObj.getName()));
					return;
				}
			}
			if(subchannel.equals("Unmute")) {
				String identifier = msgin.readUTF();
				if(identifier.equals("Send")) {
					String server = msgin.readUTF();
					String senderIdentifier = msgin.readUTF();
					String temporaryDataInstanceUUIDString = msgin.readUTF();
					String playerToUnmute = msgin.readUTF();
					String channelName = msgin.readUTF();
					MineverseChatPlayer playerToUnmuteMCP = MineverseChatAPI.getOnlineMineverseChatPlayer(playerToUnmute);
					if(playerToUnmuteMCP == null) {
						out.writeUTF("Unmute");
						out.writeUTF("Offline");
						out.writeUTF(server);
						out.writeUTF(temporaryDataInstanceUUIDString);
						out.writeUTF(senderIdentifier);
						out.writeUTF(playerToUnmute);
						sendPluginMessage(stream);
						return;
					}
					if(!ChatChannel.isChannel(channelName)) {
						return;
					}
					ChatChannel chatChannelObj = ChatChannel.getChannel(channelName);
					if (!playerToUnmuteMCP.isMuted(chatChannelObj.getName())) {
						out.writeUTF("Unmute");
						out.writeUTF("NotMuted");
						out.writeUTF(server);
						out.writeUTF(senderIdentifier);
						out.writeUTF(playerToUnmute);
						out.writeUTF(channelName);
						sendPluginMessage(stream);
						return;
					}
					playerToUnmuteMCP.removeMute(chatChannelObj.getName());
					playerToUnmuteMCP.getPlayer().sendMessage(LocalizedMessage.UNMUTE_PLAYER_PLAYER.toString()
							.replace("{player}", player.getName()).replace("{channel_color}", chatChannelObj.getColor())
							.replace("{channel_name}", chatChannelObj.getName()));
					synchronize(playerToUnmuteMCP, true);
					out.writeUTF("Unmute");
					out.writeUTF("Valid");
					out.writeUTF(server);
					out.writeUTF(senderIdentifier);
					out.writeUTF(playerToUnmute);
					out.writeUTF(channelName);
					sendPluginMessage(stream);
					return;
				}
				if(identifier.equals("Valid")) {
					String senderIdentifier = msgin.readUTF();
					String playerToUnmute = msgin.readUTF();
					String channelName = msgin.readUTF();
					if(!ChatChannel.isChannel(channelName)) {
						return;
					}
					ChatChannel chatChannelObj = ChatChannel.getChannel(channelName);
					if(senderIdentifier.equals("VentureChat:Console")) {
						Bukkit.getConsoleSender().sendMessage(LocalizedMessage.UNMUTE_PLAYER_SENDER.toString()
								.replace("{player}", playerToUnmute)
								.replace("{channel_color}", chatChannelObj.getColor())
								.replace("{channel_name}", chatChannelObj.getName()));
					}
					else {
						UUID sender = UUID.fromString(senderIdentifier);
						MineverseChatPlayer senderMCP = MineverseChatAPI.getOnlineMineverseChatPlayer(sender);
						senderMCP.getPlayer().sendMessage(LocalizedMessage.UNMUTE_PLAYER_SENDER.toString()
								.replace("{player}", playerToUnmute)
								.replace("{channel_color}", chatChannelObj.getColor())
								.replace("{channel_name}", chatChannelObj.getName()));
					}
					return;
				}
				if(identifier.equals("Offline")) {
					String senderIdentifier = msgin.readUTF();
					String playerToUnmute = msgin.readUTF();
					if(senderIdentifier.equals("VentureChat:Console")) {
						Bukkit.getConsoleSender().sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString()
								.replace("{args}", playerToUnmute));
						return;
					}
					UUID sender = UUID.fromString(senderIdentifier);
					MineverseChatPlayer senderMCP = MineverseChatAPI.getOnlineMineverseChatPlayer(sender);
					senderMCP.getPlayer().sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString()
							.replace("{args}", playerToUnmute));
					return;
				}
				if(identifier.equals("NotMuted")) {
					String senderIdentifier = msgin.readUTF();
					String playerToUnmute = msgin.readUTF();
					String channelName = msgin.readUTF();
					if(!ChatChannel.isChannel(channelName)) {
						return;
					}
					ChatChannel chatChannelObj = ChatChannel.getChannel(channelName);
					if(senderIdentifier.equals("VentureChat:Console")) {
						Bukkit.getConsoleSender().sendMessage(LocalizedMessage.PLAYER_NOT_MUTED.toString()
								.replace("{player}", playerToUnmute).replace("{channel_color}", chatChannelObj.getColor())
								.replace("{channel_name}", chatChannelObj.getName()));
						return;
					}
					UUID sender = UUID.fromString(senderIdentifier);
					MineverseChatPlayer senderMCP = MineverseChatAPI.getOnlineMineverseChatPlayer(sender);
					senderMCP.getPlayer().sendMessage(LocalizedMessage.PLAYER_NOT_MUTED.toString()
							.replace("{player}", playerToUnmute).replace("{channel_color}", chatChannelObj.getColor())
							.replace("{channel_name}", chatChannelObj.getName()));
					return;
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
					String send = msgin.readUTF();
					String echo = msgin.readUTF();
					String spy = msgin.readUTF();
					String msg = msgin.readUTF();
					if(!getConfig().getBoolean("bungeecordmessaging", true) || p == null) {
						out.writeUTF("Message");
						out.writeUTF("Offline");
						out.writeUTF(server);
						out.writeUTF(receiver);
						out.writeUTF(sender.toString());
						sendPluginMessage(stream);
						return;
					}
					if(p.getIgnores().contains(sender)) {
						out.writeUTF("Message");
						out.writeUTF("Ignore");
						out.writeUTF(server);
						out.writeUTF(receiver);
						out.writeUTF(sender.toString());
						sendPluginMessage(stream);
						return;
					}
					if(!p.getMessageToggle()) {
						out.writeUTF("Message");
						out.writeUTF("Blocked");
						out.writeUTF(server);
						out.writeUTF(receiver);
						out.writeUTF(sender.toString());
						sendPluginMessage(stream);
						return;
					}
					p.getPlayer().sendMessage(Format.FormatStringAll(PlaceholderAPI.setBracketPlaceholders(p.getPlayer(), send.replaceAll("receiver_", ""))) + msg);
					if(p.hasNotifications()) {
						Format.playMessageSound(p);
					}
					if(MineverseChatAPI.getMineverseChatPlayer(sender) == null) {
						MineverseChatPlayer senderMCP = new MineverseChatPlayer(sender, sName);
						MineverseChatAPI.addMineverseChatPlayerToMap(senderMCP);
						MineverseChatAPI.addNameToMap(senderMCP);
					}
					p.setReplyPlayer(sender);
					out.writeUTF("Message");
					out.writeUTF("Echo");
					out.writeUTF(server);
					out.writeUTF(receiver);
					out.writeUTF(p.getUUID().toString());
					out.writeUTF(sender.toString());
					out.writeUTF(sName);
					out.writeUTF(Format.FormatStringAll(PlaceholderAPI.setBracketPlaceholders(p.getPlayer(), echo.replaceAll("receiver_", ""))) + msg);
					out.writeUTF(Format.FormatStringAll(PlaceholderAPI.setBracketPlaceholders(p.getPlayer(), spy.replaceAll("receiver_", ""))) + msg);
					sendPluginMessage(stream);
					return;
				}
				if(identifier.equals("Offline")) {
					String receiver = msgin.readUTF();
					UUID sender = UUID.fromString(msgin.readUTF());
					MineverseChatPlayer p = MineverseChatAPI.getOnlineMineverseChatPlayer(sender);
					p.getPlayer().sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString()
							.replace("{args}", receiver));
					p.setReplyPlayer(null);
				}
				if(identifier.equals("Ignore")) {
					String receiver = msgin.readUTF();
					UUID sender = UUID.fromString(msgin.readUTF());
					MineverseChatPlayer p = MineverseChatAPI.getOnlineMineverseChatPlayer(sender);
					p.getPlayer().sendMessage(LocalizedMessage.IGNORING_MESSAGE.toString()
							.replace("{player}", receiver));
				}
				if(identifier.equals("Blocked")) {
					String receiver = msgin.readUTF();
					UUID sender = UUID.fromString(msgin.readUTF());
					MineverseChatPlayer p = MineverseChatAPI.getOnlineMineverseChatPlayer(sender);
					p.getPlayer().sendMessage(LocalizedMessage.BLOCKING_MESSAGE.toString()
							.replace("{player}", receiver));
				}
				if(identifier.equals("Echo")) {
					String receiverName = msgin.readUTF();
					UUID receiverUUID = UUID.fromString(msgin.readUTF());
					UUID senderUUID = UUID.fromString(msgin.readUTF());
					MineverseChatPlayer senderMCP = MineverseChatAPI.getOnlineMineverseChatPlayer(senderUUID);
					String echo = msgin.readUTF();
					if(MineverseChatAPI.getMineverseChatPlayer(receiverUUID) == null) {
						MineverseChatPlayer receiverMCP = new MineverseChatPlayer(receiverUUID, receiverName);
						MineverseChatAPI.addMineverseChatPlayerToMap(receiverMCP);
						MineverseChatAPI.addNameToMap(receiverMCP);
					}
					senderMCP.setReplyPlayer(receiverUUID);
					senderMCP.getPlayer().sendMessage(echo);
				}
				if(identifier.equals("Spy")) {
					String receiverName = msgin.readUTF();
					String senderName = msgin.readUTF();
					String spy = msgin.readUTF();
					if(!spy.startsWith("VentureChat:NoSpy")) {
						for(MineverseChatPlayer pl : MineverseChatAPI.getOnlineMineverseChatPlayers()) {
							if(pl.isSpy() && !pl.getName().equals(senderName) && !pl.getName().equals(receiverName)) {
								pl.getPlayer().sendMessage(spy);
							}
						}
					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
