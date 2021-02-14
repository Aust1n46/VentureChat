package mineverse.Aust1n46.chat.bungee;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.SynchronizedMineverseChatPlayer;
import mineverse.Aust1n46.chat.bungee.command.GlobalMute;
import mineverse.Aust1n46.chat.bungee.command.GlobalMuteAll;
import mineverse.Aust1n46.chat.bungee.command.GlobalUnmute;
import mineverse.Aust1n46.chat.bungee.command.GlobalUnmuteAll;
import mineverse.Aust1n46.chat.database.BungeePlayerData;
import mineverse.Aust1n46.chat.utilities.UUIDFetcher;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

//This is the main class for the BungeeCord version of the plugin.
public class MineverseChatBungee extends Plugin implements Listener {
	private static MineverseChatBungee instance;
	private Configuration bungeeConfig;
	public static String PLUGIN_MESSAGING_CHANNEL = "venturechat:data";

	@Override
	public void onEnable() {
		instance = this;
		
		if(!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		File config = new File(getDataFolder(), "bungeeconfig.yml");
		try {
			if(!config.exists()) {
				Files.copy(getResourceAsStream("bungeeconfig.yml"), config.toPath());
			}
			bungeeConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "bungeeconfig.yml"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		BungeePlayerData.loadLegacyBungeePlayerData();
		BungeePlayerData.loadBungeePlayerData();
		
		this.getProxy().registerChannel(MineverseChatBungee.PLUGIN_MESSAGING_CHANNEL);
		this.getProxy().getPluginManager().registerListener(this, this);
		
		registerBungeeCordMuteCommands();
	}

	@Override
	public void onDisable() {
		BungeePlayerData.saveBungeePlayerData();
	}
	
	/**
	 * Old BungeeCord mute commands that pretty much no one even knows about let alone uses...
	 * Slated for removal when the mute system is reworked.
	 */
	private void registerBungeeCordMuteCommands() {
		getProxy().getPluginManager().registerCommand(this, new GlobalMute(this, "globalmute"));
		getProxy().getPluginManager().registerCommand(this, new GlobalMute(this, "gmute"));
		getProxy().getPluginManager().registerCommand(this, new GlobalMuteAll(this, "globalmuteall"));
		getProxy().getPluginManager().registerCommand(this, new GlobalMuteAll(this, "gmuteall"));
		getProxy().getPluginManager().registerCommand(this, new GlobalUnmute(this, "globalunmute"));
		getProxy().getPluginManager().registerCommand(this, new GlobalUnmute(this, "gunmute"));
		getProxy().getPluginManager().registerCommand(this, new GlobalUnmuteAll(this, "globalunmuteall"));
		getProxy().getPluginManager().registerCommand(this, new GlobalUnmuteAll(this, "gunmuteall"));
	}
	
	public static MineverseChatBungee getInstance() {
		return instance;
	}
	
	public Configuration getBungeeConfig() {
		return bungeeConfig;
	}
	
	@EventHandler
	public void onPlayerJoin(ServerSwitchEvent event) {
		updatePlayerNames();
	}
	
	@EventHandler
	public void onPlayerLeave(ServerDisconnectEvent event) {
		updatePlayerNames();
	}
	
	@EventHandler
	public void onPlayerJoinNetwork(PostLoginEvent event) {
		UUIDFetcher.checkOfflineUUIDWarningBungee(event.getPlayer().getUniqueId());
	}
	
	private void updatePlayerNames() {
		try {
			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(outstream);
			out.writeUTF("PlayerNames");
			out.writeInt(this.getProxy().getPlayers().size());
			for(ProxiedPlayer pp : this.getProxy().getPlayers()) {
				out.writeUTF(pp.getName());
			}
			
			for(String send : getProxy().getServers().keySet()) {
				if(getProxy().getServers().get(send).getPlayers().size() > 0) {
					getProxy().getServers().get(send).sendData(MineverseChatBungee.PLUGIN_MESSAGING_CHANNEL, outstream.toByteArray());
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onPluginMessage(PluginMessageEvent ev) {
		//System.out.println(ev.getTag() + "," + ev.getSender().toString() + "," + (ev.getSender() instanceof Server));
		if(!ev.getTag().equals(MineverseChatBungee.PLUGIN_MESSAGING_CHANNEL) && !ev.getTag().contains("viaversion:")) {
			return;
		}
		if(!(ev.getSender() instanceof Server)) {
			return;
		}
		Server ser = (Server) ev.getSender();
		ByteArrayInputStream instream = new ByteArrayInputStream(ev.getData());
		DataInputStream in = new DataInputStream(instream);
		try {
			String subchannel = in.readUTF();
			//System.out.println(subchannel);
			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(outstream);
			if(subchannel.equals("Chat")) {
				String chatchannel = in.readUTF();
				String senderName = in.readUTF();
				String senderUUID = in.readUTF();
				boolean bungeeToggle = in.readBoolean();
				int hash = in.readInt();
				String format = in.readUTF();
				String chat = in.readUTF();
				String json = in.readUTF();
				String primaryGroup = in.readUTF();
				String nickname = in.readUTF();
				out.writeUTF("Chat");
				out.writeUTF(ser.getInfo().getName());
				out.writeUTF(chatchannel);
				out.writeUTF(senderName);
				out.writeUTF(senderUUID);
				out.writeInt(hash);
				out.writeUTF(format);
				out.writeUTF(chat);
				out.writeUTF(json);
				out.writeUTF(primaryGroup);
				out.writeUTF(nickname);
				for(String send : getProxy().getServers().keySet()) {
					if(getProxy().getServers().get(send).getPlayers().size() > 0) {
						if(!bungeeToggle && !getProxy().getServers().get(send).getName().equalsIgnoreCase(ser.getInfo().getName())) {
							continue;
						}
						getProxy().getServers().get(send).sendData(MineverseChatBungee.PLUGIN_MESSAGING_CHANNEL, outstream.toByteArray());
					}
				}
			}
			if(subchannel.equals("DiscordSRV")) {
				String chatchannel = in.readUTF();
				String message = in.readUTF();
				out.writeUTF("DiscordSRV");
				out.writeUTF(chatchannel);
				out.writeUTF(message);
				for(String send : getProxy().getServers().keySet()) {
					if(getProxy().getServers().get(send).getPlayers().size() > 0) {
						getProxy().getServers().get(send).sendData(MineverseChatBungee.PLUGIN_MESSAGING_CHANNEL, outstream.toByteArray());
					}
				}
			}
			if(subchannel.equals("Chwho")) {
				String identifier = in.readUTF();
				if(identifier.equals("Get")) {
					String sender = in.readUTF();
					String name = in.readUTF();
					String channel = in.readUTF();
					SynchronizedMineverseChatPlayer smcp = MineverseChatAPI.getSynchronizedMineverseChatPlayer(UUID.fromString(sender));
					smcp.clearMessagePackets();
					smcp.clearMessageData();
					out.writeUTF("Chwho");
					out.writeUTF("Get");
					out.writeUTF(sender);
					out.writeUTF(name);
					out.writeUTF(channel);
					for(String send : getProxy().getServers().keySet()) {
						if(getProxy().getServers().get(send).getPlayers().size() > 0) {
							getProxy().getServers().get(send).sendData(MineverseChatBungee.PLUGIN_MESSAGING_CHANNEL, outstream.toByteArray());
						}
					}
				}
				if(identifier.equals("Receive")) {
					String sender = in.readUTF();
					String name = in.readUTF();
					String channel = in.readUTF();
					SynchronizedMineverseChatPlayer smcp = MineverseChatAPI.getSynchronizedMineverseChatPlayer(UUID.fromString(sender));
					smcp.incrementMessagePackets();
					int players = in.readInt();
					for(int a = 0; a < players; a++) {
						smcp.addData(in.readUTF());
					}
					int servers = 0;
					for(String send : getProxy().getServers().keySet()) {
						if(getProxy().getServers().get(send).getPlayers().size() > 0) {
							servers ++;
						}
					}
					if(smcp.getMessagePackets() >= servers) {
						smcp.clearMessagePackets();
						out.writeUTF("Chwho");
						out.writeUTF("Receive");
						out.writeUTF(sender);
						out.writeUTF(channel);
						out.writeInt(smcp.getMessageData().size());
						for(String s : smcp.getMessageData()) {
							out.writeUTF(s);
						}
						smcp.clearMessageData();
						Server server = getProxy().getPlayer(name).getServer();
						server.sendData(MineverseChatBungee.PLUGIN_MESSAGING_CHANNEL, outstream.toByteArray());
					}	
				}
			}
			if(subchannel.equals("RemoveMessage")) {
				String hash = in.readUTF();
				out.writeUTF("RemoveMessage");
				out.writeUTF(hash);
				for(String send : getProxy().getServers().keySet()) {
					if(getProxy().getServers().get(send).getPlayers().size() > 0) {
						getProxy().getServers().get(send).sendData(MineverseChatBungee.PLUGIN_MESSAGING_CHANNEL, outstream.toByteArray());
					}
				}
			}
			if(subchannel.equals("Ignore")) {
				String identifier = in.readUTF();
				if(identifier.equals("Send")) {
					String server = ser.getInfo().getName();
					String player = in.readUTF();
					String sender = in.readUTF();
					SynchronizedMineverseChatPlayer smcp = MineverseChatAPI.getSynchronizedMineverseChatPlayer(UUID.fromString(sender));
					smcp.clearMessagePackets();
					out.writeUTF("Ignore");
					out.writeUTF("Send");
					out.writeUTF(server);
					out.writeUTF(player);
					out.writeUTF(sender);
					for(String send : getProxy().getServers().keySet()) {
						if(getProxy().getServers().get(send).getPlayers().size() > 0) {
							getProxy().getServers().get(send).sendData(MineverseChatBungee.PLUGIN_MESSAGING_CHANNEL, outstream.toByteArray());
						}
					}
				}
				if(identifier.equals("Offline")) {
					String server = in.readUTF();
					String player = in.readUTF();
					String sender = in.readUTF();
					SynchronizedMineverseChatPlayer smcp = MineverseChatAPI.getSynchronizedMineverseChatPlayer(UUID.fromString(sender));
					smcp.incrementMessagePackets();
					int servers = 0;
					for(String send : getProxy().getServers().keySet()) {
						if(getProxy().getServers().get(send).getPlayers().size() > 0) {
							servers ++;
						}
					}
					if(smcp.getMessagePackets() >= servers) {
						smcp.clearMessagePackets();
						out.writeUTF("Ignore");
						out.writeUTF("Offline");
						out.writeUTF(player);
						out.writeUTF(sender);
						if(getProxy().getServers().get(server).getPlayers().size() > 0) {
							getProxy().getServers().get(server).sendData(MineverseChatBungee.PLUGIN_MESSAGING_CHANNEL, outstream.toByteArray());
						}
					}	
				}
				if(identifier.equals("Echo")) {
					String server = in.readUTF();
					String player = in.readUTF();
					String receiverName = in.readUTF();
					String sender = in.readUTF();
					out.writeUTF("Ignore");
					out.writeUTF("Echo");
					out.writeUTF(player);
					out.writeUTF(receiverName);
					out.writeUTF(sender);
					if(getProxy().getServers().get(server).getPlayers().size() > 0) {
						getProxy().getServers().get(server).sendData(MineverseChatBungee.PLUGIN_MESSAGING_CHANNEL, outstream.toByteArray());
					}
				}
			}
			if(subchannel.equals("Message")) {
				String identifier = in.readUTF();
				if(identifier.equals("Send")) {
					String server = ser.getInfo().getName();
					String player = in.readUTF();
					String sender = in.readUTF();
					String sName = in.readUTF();
					String send = in.readUTF();
					String echo = in.readUTF();
					String spy = in.readUTF();
					String msg = in.readUTF();
					SynchronizedMineverseChatPlayer smcp = MineverseChatAPI.getSynchronizedMineverseChatPlayer(UUID.fromString(sender));
					smcp.clearMessagePackets();
					out.writeUTF("Message");
					out.writeUTF("Send");
					out.writeUTF(server);
					out.writeUTF(player);
					out.writeUTF(sender);
					out.writeUTF(sName);
					out.writeUTF(send);
					out.writeUTF(echo);
					out.writeUTF(spy);
					out.writeUTF(msg);
					for(String serv : getProxy().getServers().keySet()) {
						if(getProxy().getServers().get(serv).getPlayers().size() > 0) {
							getProxy().getServers().get(serv).sendData(MineverseChatBungee.PLUGIN_MESSAGING_CHANNEL, outstream.toByteArray());
						}
					}
				}
				if(identifier.equals("Offline")) {
					String server = in.readUTF();
					String player = in.readUTF();
					String sender = in.readUTF();
					SynchronizedMineverseChatPlayer smcp = MineverseChatAPI.getSynchronizedMineverseChatPlayer(UUID.fromString(sender));
					smcp.incrementMessagePackets();
					int servers = 0;
					for(String send : getProxy().getServers().keySet()) {
						if(getProxy().getServers().get(send).getPlayers().size() > 0) {
							servers ++;
						}
					}
					if(smcp.getMessagePackets() >= servers) {
						smcp.clearMessagePackets();
						out.writeUTF("Message");
						out.writeUTF("Offline");
						out.writeUTF(player);
						out.writeUTF(sender);
						if(getProxy().getServers().get(server).getPlayers().size() > 0) {
							getProxy().getServers().get(server).sendData(MineverseChatBungee.PLUGIN_MESSAGING_CHANNEL, outstream.toByteArray());
						}
					}	
				}
				if(identifier.equals("Ignore")) {
					String server = in.readUTF();
					String player = in.readUTF();
					String sender = in.readUTF();
					out.writeUTF("Message");
					out.writeUTF("Ignore");
					out.writeUTF(player);
					out.writeUTF(sender);
					if(getProxy().getServers().get(server).getPlayers().size() > 0) {
						getProxy().getServers().get(server).sendData(MineverseChatBungee.PLUGIN_MESSAGING_CHANNEL, outstream.toByteArray());
					}
				}
				if(identifier.equals("Blocked")) {
					String server = in.readUTF();
					String player = in.readUTF();
					String sender = in.readUTF();
					out.writeUTF("Message");
					out.writeUTF("Blocked");
					out.writeUTF(player);
					out.writeUTF(sender);
					if(getProxy().getServers().get(server).getPlayers().size() > 0) {
						getProxy().getServers().get(server).sendData(MineverseChatBungee.PLUGIN_MESSAGING_CHANNEL, outstream.toByteArray());
					}
				}
				if(identifier.equals("Echo")) {
					String server = in.readUTF();
					String player = in.readUTF();
					String sender = in.readUTF();
					String sName = in.readUTF();
					String echo = in.readUTF();
					String spy = in.readUTF();
					out.writeUTF("Message");
					out.writeUTF("Echo");
					out.writeUTF(player);
					out.writeUTF(sender);
					out.writeUTF(echo);
					if(getProxy().getServers().get(server).getPlayers().size() > 0) {
						getProxy().getServers().get(server).sendData(MineverseChatBungee.PLUGIN_MESSAGING_CHANNEL, outstream.toByteArray());
					}
					outstream = new ByteArrayOutputStream();
					out = new DataOutputStream(outstream);
					out.writeUTF("Message");
					out.writeUTF("Spy");
					out.writeUTF(player);
					out.writeUTF(sName);
					out.writeUTF(spy);
					for(String send : getProxy().getServers().keySet()) {
						if(getProxy().getServers().get(send).getPlayers().size() > 0) {
							getProxy().getServers().get(send).sendData(MineverseChatBungee.PLUGIN_MESSAGING_CHANNEL, outstream.toByteArray());
						}
					}
				}
			}
			if(subchannel.equals("Sync")) {
				//System.out.println("Sync received...");
				String identifier = in.readUTF();
				if(identifier.equals("Receive")) {
					//System.out.println("Sending update...");
					String server = ser.getInfo().getName();
					UUID uuid = UUID.fromString(in.readUTF());
					SynchronizedMineverseChatPlayer smcp = MineverseChatAPI.getSynchronizedMineverseChatPlayer(uuid);
					if(smcp == null) {
						smcp = new SynchronizedMineverseChatPlayer(uuid, new HashSet<String>(), new HashMap<String, Integer>(), new HashSet<UUID>(), false, true);
						MineverseChatAPI.addSynchronizedMineverseChatPlayerToMap(smcp);
					}
					out.writeUTF("Sync");
					out.writeUTF(uuid.toString());
					int channelCount = smcp.getListening().size();
					//System.out.println(channelCount);
					out.write(channelCount);
					for(String channel : smcp.getListening()) {
						out.writeUTF(channel);
					}
					int muteCount = smcp.getMutes().keySet().size();
					//System.out.println(muteCount);
					out.write(muteCount);
					for(String channel : smcp.getMutes().keySet()) {
						//System.out.println(channel);
						out.writeUTF(channel);
					}
					//System.out.println(smcp.isSpy() + " spy value");
					//System.out.println(out.size() + " size before");
					out.writeBoolean(smcp.isSpy());
					out.writeBoolean(smcp.getMessageToggle());
					//System.out.println(out.size() + " size after");
					int ignoreCount = smcp.getIgnores().size();
					//System.out.println(ignoreCount + " ignore size");
					out.write(ignoreCount);
					for(UUID ignore : smcp.getIgnores()) {
						out.writeUTF(ignore.toString());
					}
					if(getProxy().getServers().get(server).getPlayers().size() > 0) 
						getProxy().getServers().get(server).sendData(MineverseChatBungee.PLUGIN_MESSAGING_CHANNEL, outstream.toByteArray());
				}
				if(identifier.equals("Update")) {
					UUID uuid = UUID.fromString(in.readUTF());
					SynchronizedMineverseChatPlayer smcp = MineverseChatAPI.getSynchronizedMineverseChatPlayer(uuid);
					if(smcp == null) {
						smcp = new SynchronizedMineverseChatPlayer(uuid, new HashSet<String>(), new HashMap<String, Integer>(), new HashSet<UUID>(), false, true);
						MineverseChatAPI.addSynchronizedMineverseChatPlayerToMap(smcp);
					}		
					smcp.getListening().clear();
					smcp.getMutes().clear();
					smcp.getIgnores().clear();
					int sizeL = in.read();
					//System.out.println(sizeL + " listening");
					for(int a = 0; a < sizeL; a++) {
						smcp.addListening(in.readUTF());
					}
					int sizeM = in.read();
					//System.out.println(size + " mutes");
					for(int b = 0; b < sizeM; b++) {
						String mute = in.readUTF();
						//System.out.println(mute);
						smcp.addMute(mute);
					}
					int sizeI = in.read();
					for(int c = 0; c < sizeI; c++) {
						String ignore = in.readUTF();
						//System.out.println(mute);
						smcp.addIgnore(MineverseChatAPI.getSynchronizedMineverseChatPlayer(UUID.fromString(ignore)));
					}
					smcp.setSpy(in.readBoolean());
					smcp.setMessageToggle(in.readBoolean());
				}
			}
			if(subchannel.equals("Mute")) {
				String identifier = in.readUTF();
				if(identifier.equals("Channel")) {
					String player = in.readUTF();
					String channel = in.readUTF();
					ProxiedPlayer p = getProxy().getPlayer(player);
					p.sendMessage(new TextComponent(ChatColor.RED + "Invalid channel: " + channel));
				}
				if(identifier.equals("Player")) {
					String player = in.readUTF();
					String muteplayer = in.readUTF();
					String server = in.readUTF();
					ProxiedPlayer p = getProxy().getPlayer(player);
					p.sendMessage(new TextComponent(ChatColor.RED + "Player: " + ChatColor.GOLD + muteplayer + ChatColor.RED + " is not connected to server: " + server));
				}
				if(identifier.equals("Mutable")) {
					String player = in.readUTF();
					String channel = in.readUTF();
					String color = in.readUTF();
					ProxiedPlayer p = getProxy().getPlayer(player);
					p.sendMessage(new TextComponent(ChatColor.RED + "You cannot mute players in this channel: " + ChatColor.valueOf(color.toUpperCase()) + channel));
				}
				if(identifier.equals("Already")) {
					String player = in.readUTF();
					String muteplayer = in.readUTF();
					String channel = in.readUTF();
					String color = in.readUTF();
					ProxiedPlayer p = getProxy().getPlayer(player);
					p.sendMessage(new TextComponent(ChatColor.GOLD + muteplayer + ChatColor.RED + " is already muted in channel: " + ChatColor.valueOf(color.toUpperCase()) + channel));
				}
				if(identifier.equals("Time")) {
					String player = in.readUTF();
					String muteplayer = in.readUTF();
					String channel = in.readUTF();
					String color = in.readUTF();
					ProxiedPlayer p = getProxy().getPlayer(player);
					p.sendMessage(new TextComponent(ChatColor.RED + "Muted player " + ChatColor.GOLD + muteplayer + ChatColor.RED + " in: " + ChatColor.valueOf(color.toUpperCase()) + channel));
				}
				if(identifier.equals("Valid")) {
					String player = in.readUTF();
					String muteplayer = in.readUTF();
					String channel = in.readUTF();
					String color = in.readUTF();
					String time = in.readUTF();
					ProxiedPlayer p = getProxy().getPlayer(player);
					String timedmute = "";
					if(!time.equals("None\n")) {
						String keyword = "minutes";
						if(time.equals("1")) keyword = "minute";
						timedmute = ChatColor.RED + " for " + time + " " + keyword;
					}
					p.sendMessage(new TextComponent(ChatColor.RED + "Muted player " + ChatColor.GOLD + muteplayer + ChatColor.RED + " in: " + ChatColor.valueOf(color.toUpperCase()) + channel + timedmute));
				}
			}
			if(subchannel.equals("Muteall")) {
				String identifier = in.readUTF();
				if(identifier.equals("Player")) {
					String player = in.readUTF();
					String muteplayer = in.readUTF();
					String server = in.readUTF();
					ProxiedPlayer p = getProxy().getPlayer(player);
					p.sendMessage(new TextComponent(ChatColor.RED + "Player: " + ChatColor.GOLD + muteplayer + ChatColor.RED + " is not connected to server: " + server));
				}
				if(identifier.equals("Valid")) {
					String player = in.readUTF();
					String muteplayer = in.readUTF();
					ProxiedPlayer p = getProxy().getPlayer(player);
					p.sendMessage(new TextComponent(ChatColor.RED + "Muted player " + ChatColor.GOLD + muteplayer + ChatColor.RED + " in all channels."));
				}
			}
			if(subchannel.equals("Unmuteall")) {
				String identifier = in.readUTF();
				if(identifier.equals("Player")) {
					String player = in.readUTF();
					String muteplayer = in.readUTF();
					String server = in.readUTF();
					ProxiedPlayer p = getProxy().getPlayer(player);
					p.sendMessage(new TextComponent(ChatColor.RED + "Player: " + ChatColor.GOLD + muteplayer + ChatColor.RED + " is not connected to server: " + server));
				}
				if(identifier.equals("Valid")) {
					String player = in.readUTF();
					String muteplayer = in.readUTF();
					ProxiedPlayer p = getProxy().getPlayer(player);
					p.sendMessage(new TextComponent(ChatColor.RED + "Unmuted player " + ChatColor.GOLD + muteplayer + ChatColor.RED + " in all channels."));
				}
			}
			if(subchannel.equals("Unmute")) {
				String identifier = in.readUTF();
				if(identifier.equals("Channel")) {
					String player = in.readUTF();
					String channel = in.readUTF();
					ProxiedPlayer p = getProxy().getPlayer(player);
					p.sendMessage(new TextComponent(ChatColor.RED + "Invalid channel: " + channel));
				}
				if(identifier.equals("Player")) {
					String player = in.readUTF();
					String muteplayer = in.readUTF();
					String server = in.readUTF();
					ProxiedPlayer p = getProxy().getPlayer(player);
					p.sendMessage(new TextComponent(ChatColor.RED + "Player: " + ChatColor.GOLD + muteplayer + ChatColor.RED + " is not connected to server: " + server));
				}
				if(identifier.equals("Already")) {
					String player = in.readUTF();
					String muteplayer = in.readUTF();
					String channel = in.readUTF();
					String color = in.readUTF();
					ProxiedPlayer p = getProxy().getPlayer(player);
					p.sendMessage(new TextComponent(ChatColor.GOLD + muteplayer + ChatColor.RED + " is not muted in channel: " + ChatColor.valueOf(color.toUpperCase()) + channel));
				}
				if(identifier.equals("Valid")) {
					String player = in.readUTF();
					String muteplayer = in.readUTF();
					String channel = in.readUTF();
					String color = in.readUTF();
					ProxiedPlayer p = getProxy().getPlayer(player);
					p.sendMessage(new TextComponent(ChatColor.RED + "Unmuted player " + ChatColor.GOLD + muteplayer + ChatColor.RED + " in: " + ChatColor.valueOf(color.toUpperCase()) + channel));
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}