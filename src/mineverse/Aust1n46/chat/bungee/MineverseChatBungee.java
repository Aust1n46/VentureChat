package mineverse.Aust1n46.chat.bungee;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.UUID;

import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.SynchronizedMineverseChatPlayer;
import mineverse.Aust1n46.chat.command.mute.MuteContainer;
import mineverse.Aust1n46.chat.database.BungeePlayerData;
import mineverse.Aust1n46.chat.database.TemporaryDataInstance;
import mineverse.Aust1n46.chat.utilities.UUIDFetcher;
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
	}

	@Override
	public void onDisable() {
		BungeePlayerData.saveBungeePlayerData();
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
			if(subchannel.equals("Mute")) {
				String identifier = in.readUTF();
				if(identifier.equals("Send")) {
					String server = ser.getInfo().getName();
					String senderIdentifier = in.readUTF();
					String playerToMute = in.readUTF();
					String channelName = in.readUTF();
					long time = in.readLong();
					String reason = in.readUTF();
					UUID temporaryDataInstanceUUID = TemporaryDataInstance.createTemporaryDataInstance();
					out.writeUTF("Mute");
					out.writeUTF("Send");
					out.writeUTF(server);
					out.writeUTF(senderIdentifier);
					out.writeUTF(temporaryDataInstanceUUID.toString());
					out.writeUTF(playerToMute);
					out.writeUTF(channelName);
					out.writeLong(time);
					out.writeUTF(reason);
					for(String send : getProxy().getServers().keySet()) {
						if(getProxy().getServers().get(send).getPlayers().size() > 0) {
							getProxy().getServers().get(send).sendData(MineverseChatBungee.PLUGIN_MESSAGING_CHANNEL, outstream.toByteArray());
						}
					}
				}
				if(identifier.equals("Valid")) {
					String server = in.readUTF();
					String senderIdentifier = in.readUTF();
					String playerToMute = in.readUTF();
					String channelName = in.readUTF();
					long time = in.readLong();
					String reason = in.readUTF();
					out.writeUTF("Mute");
					out.writeUTF("Valid");
					out.writeUTF(senderIdentifier);
					out.writeUTF(playerToMute);
					out.writeUTF(channelName);
					out.writeLong(time);
					out.writeUTF(reason);
					if(getProxy().getServers().get(server).getPlayers().size() > 0) {
						getProxy().getServers().get(server).sendData(MineverseChatBungee.PLUGIN_MESSAGING_CHANNEL, outstream.toByteArray());
					}
				}
				if(identifier.equals("Offline")) {
					String server = in.readUTF();
					UUID temporaryDataInstanceUUID = UUID.fromString(in.readUTF());
					String senderIdentifier = in.readUTF();
					String playerToMute = in.readUTF();
					TemporaryDataInstance temporaryDataInstance = TemporaryDataInstance.getTemporaryDataInstance(temporaryDataInstanceUUID);
					temporaryDataInstance.incrementMessagePackets();
					int servers = 0;
					for(String send : getProxy().getServers().keySet()) {
						if(getProxy().getServers().get(send).getPlayers().size() > 0) {
							servers ++;
						}
					}
					if(temporaryDataInstance.getMessagePackets() >= servers) {
						temporaryDataInstance.destroyInstance();
						out.writeUTF("Mute");
						out.writeUTF("Offline");
						out.writeUTF(senderIdentifier);
						out.writeUTF(playerToMute);
						if(getProxy().getServers().get(server).getPlayers().size() > 0) {
							getProxy().getServers().get(server).sendData(MineverseChatBungee.PLUGIN_MESSAGING_CHANNEL, outstream.toByteArray());
						}
					}	
				}
				if(identifier.equals("AlreadyMuted")) {
					String server = in.readUTF();
					String senderIdentifier = in.readUTF();
					String playerToMute = in.readUTF();
					String channelName = in.readUTF();
					out.writeUTF("Mute");
					out.writeUTF("AlreadyMuted");
					out.writeUTF(senderIdentifier);
					out.writeUTF(playerToMute);
					out.writeUTF(channelName);
					if(getProxy().getServers().get(server).getPlayers().size() > 0) {
						getProxy().getServers().get(server).sendData(MineverseChatBungee.PLUGIN_MESSAGING_CHANNEL, outstream.toByteArray());
					}
				}
			}
			if(subchannel.equals("Unmute")) {
				String identifier = in.readUTF();
				if(identifier.equals("Send")) {
					String server = ser.getInfo().getName();
					String senderIdentifier = in.readUTF();
					String playerToUnmute = in.readUTF();
					String channelName = in.readUTF();
					UUID temporaryDataInstanceUUID = TemporaryDataInstance.createTemporaryDataInstance();
					out.writeUTF("Unmute");
					out.writeUTF("Send");
					out.writeUTF(server);
					out.writeUTF(senderIdentifier);
					out.writeUTF(temporaryDataInstanceUUID.toString());
					out.writeUTF(playerToUnmute);
					out.writeUTF(channelName);
					for(String send : getProxy().getServers().keySet()) {
						if(getProxy().getServers().get(send).getPlayers().size() > 0) {
							getProxy().getServers().get(send).sendData(MineverseChatBungee.PLUGIN_MESSAGING_CHANNEL, outstream.toByteArray());
						}
					}
				}
				if(identifier.equals("Valid")) {
					String server = in.readUTF();
					String senderIdentifier = in.readUTF();
					String playerToUnmute = in.readUTF();
					String channelName = in.readUTF();
					out.writeUTF("Unmute");
					out.writeUTF("Valid");
					out.writeUTF(senderIdentifier);
					out.writeUTF(playerToUnmute);
					out.writeUTF(channelName);
					if(getProxy().getServers().get(server).getPlayers().size() > 0) {
						getProxy().getServers().get(server).sendData(MineverseChatBungee.PLUGIN_MESSAGING_CHANNEL, outstream.toByteArray());
					}
				}
				if(identifier.equals("Offline")) {
					String server = in.readUTF();
					UUID temporaryDataInstanceUUID = UUID.fromString(in.readUTF());
					String senderIdentifier = in.readUTF();
					String playerToUnmute = in.readUTF();
					TemporaryDataInstance temporaryDataInstance = TemporaryDataInstance.getTemporaryDataInstance(temporaryDataInstanceUUID);
					temporaryDataInstance.incrementMessagePackets();
					int servers = 0;
					for(String send : getProxy().getServers().keySet()) {
						if(getProxy().getServers().get(send).getPlayers().size() > 0) {
							servers ++;
						}
					}
					if(temporaryDataInstance.getMessagePackets() >= servers) {
						temporaryDataInstance.destroyInstance();
						out.writeUTF("Unmute");
						out.writeUTF("Offline");
						out.writeUTF(senderIdentifier);
						out.writeUTF(playerToUnmute);
						if(getProxy().getServers().get(server).getPlayers().size() > 0) {
							getProxy().getServers().get(server).sendData(MineverseChatBungee.PLUGIN_MESSAGING_CHANNEL, outstream.toByteArray());
						}
					}	
				}
				if(identifier.equals("NotMuted")) {
					String server = in.readUTF();
					String senderIdentifier = in.readUTF();
					String playerToUnmute = in.readUTF();
					String channelName = in.readUTF();
					out.writeUTF("Unmute");
					out.writeUTF("NotMuted");
					out.writeUTF(senderIdentifier);
					out.writeUTF(playerToUnmute);
					out.writeUTF(channelName);
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
					String receiverUUID = in.readUTF();
					String sender = in.readUTF();
					String sName = in.readUTF();
					String echo = in.readUTF();
					String spy = in.readUTF();
					out.writeUTF("Message");
					out.writeUTF("Echo");
					out.writeUTF(player);
					out.writeUTF(receiverUUID);
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
						smcp = new SynchronizedMineverseChatPlayer(uuid);
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
					int muteCount = smcp.getMutes().size();
					//System.out.println(muteCount);
					out.write(muteCount);
					for(MuteContainer muteContainer : smcp.getMutes()) {
						out.writeUTF(muteContainer.getChannel());
						out.writeLong(muteContainer.getDuration());
						out.writeUTF(muteContainer.getReason());
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
						smcp = new SynchronizedMineverseChatPlayer(uuid);
						MineverseChatAPI.addSynchronizedMineverseChatPlayerToMap(smcp);
					}		
					smcp.getListening().clear();
					smcp.clearMutes();
					smcp.getIgnores().clear();
					int sizeL = in.read();
					//System.out.println(sizeL + " listening");
					for(int a = 0; a < sizeL; a++) {
						smcp.addListening(in.readUTF());
					}
					int sizeM = in.read();
					for(int b = 0; b < sizeM; b++) {
						String mute = in.readUTF();
						long muteTime = in.readLong();
						String muteReason = in.readUTF();
						//System.out.println(mute);
						smcp.addMute(mute, muteTime, muteReason);
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
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}