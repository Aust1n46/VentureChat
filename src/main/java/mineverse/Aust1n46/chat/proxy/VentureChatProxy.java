package mineverse.Aust1n46.chat.proxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.SynchronizedMineverseChatPlayer;
import mineverse.Aust1n46.chat.command.mute.MuteContainer;
import mineverse.Aust1n46.chat.database.TemporaryDataInstance;

public class VentureChatProxy {
	public static String PLUGIN_MESSAGING_CHANNEL_NAMESPACE = "venturechat";
	public static String PLUGIN_MESSAGING_CHANNEL_NAME = "data";
	public static String PLUGIN_MESSAGING_CHANNEL_STRING = "venturechat:data";
	
	public static void onPluginMessage(byte[] data, String serverName, VentureChatProxySource source) {
		ByteArrayInputStream instream = new ByteArrayInputStream(data);
		DataInputStream in = new DataInputStream(instream);
		try {
			String subchannel = in.readUTF();
			//System.out.println(subchannel);
			final ByteArrayOutputStream outstream = new ByteArrayOutputStream();
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
				out.writeUTF(serverName);
				out.writeUTF(chatchannel);
				out.writeUTF(senderName);
				out.writeUTF(senderUUID);
				out.writeInt(hash);
				out.writeUTF(format);
				out.writeUTF(chat);
				out.writeUTF(json);
				out.writeUTF(primaryGroup);
				out.writeUTF(nickname);
				for(VentureChatProxyServer send : source.getServers()) {
					if(!send.isEmpty()) {
						if(!bungeeToggle && !send.getName().equalsIgnoreCase(serverName)) {
							continue;
						}
						source.sendPluginMessage(send.getName(), outstream.toByteArray());
					}
				}
			}
			if(subchannel.equals("DiscordSRV")) {
				String chatchannel = in.readUTF();
				String message = in.readUTF();
				out.writeUTF("DiscordSRV");
				out.writeUTF(chatchannel);
				out.writeUTF(message);
				source.getServers().forEach(send -> {
					if(!send.isEmpty()) {
						source.sendPluginMessage(send.getName(), outstream.toByteArray());
					}
				});
			}
			if(subchannel.equals("Chwho")) {
				String identifier = in.readUTF();
				if(identifier.equals("Get")) {
					String server = serverName;
					String sender = in.readUTF();
					String channel = in.readUTF();
					SynchronizedMineverseChatPlayer smcp = MineverseChatAPI.getSynchronizedMineverseChatPlayer(UUID.fromString(sender));
					if(smcp == null) {
						source.sendConsoleMessage("&8[&eVentureChat&8]&c Synchronized player instance is null!  This shouldn't be!");
						source.sendConsoleMessage("&8[&eVentureChat&8]&c You probably have an issue with your player data saving and/or your login data sync!");
						return;
					}
					smcp.clearMessagePackets();
					smcp.clearMessageData();
					out.writeUTF("Chwho");
					out.writeUTF("Get");
					out.writeUTF(server);
					out.writeUTF(sender);
					out.writeUTF(channel);
					source.getServers().forEach(send -> {
						if(!send.isEmpty()) {
							source.sendPluginMessage(send.getName(), outstream.toByteArray());
						}
					});
				}
				if(identifier.equals("Receive")) {
					String server = in.readUTF();
					String sender = in.readUTF();
					String channel = in.readUTF();
					SynchronizedMineverseChatPlayer smcp = MineverseChatAPI.getSynchronizedMineverseChatPlayer(UUID.fromString(sender));
					if(smcp == null) {
						source.sendConsoleMessage("&8[&eVentureChat&8]&c Synchronized player instance is null!  This shouldn't be!");
						source.sendConsoleMessage("&8[&eVentureChat&8]&c You probably have an issue with your player data saving and/or your login data sync!");
						return;
					}
					smcp.incrementMessagePackets();
					int players = in.readInt();
					for(int a = 0; a < players; a++) {
						smcp.addData(in.readUTF());
					}
					AtomicInteger servers = new AtomicInteger(0);
					source.getServers().forEach(send -> {
						if(!send.isEmpty()) {
							servers.incrementAndGet();
						}
					});
					if(smcp.getMessagePackets() >= servers.get()) {
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
						source.sendPluginMessage(server, outstream.toByteArray());
					}	
				}
			}
			if(subchannel.equals("RemoveMessage")) {
				String hash = in.readUTF();
				out.writeUTF("RemoveMessage");
				out.writeUTF(hash);
				source.getServers().forEach(send -> {
					if(!send.isEmpty()) {
						source.sendPluginMessage(send.getName(), outstream.toByteArray());
					}
				});
			}
			if(subchannel.equals("Ignore")) {
				String identifier = in.readUTF();
				if(identifier.equals("Send")) {
					String server = serverName;
					String player = in.readUTF();
					String sender = in.readUTF();
					SynchronizedMineverseChatPlayer smcp = MineverseChatAPI.getSynchronizedMineverseChatPlayer(UUID.fromString(sender));
					if(smcp == null) {
						source.sendConsoleMessage("&8[&eVentureChat&8]&c Synchronized player instance is null!  This shouldn't be!");
						source.sendConsoleMessage("&8[&eVentureChat&8]&c You probably have an issue with your player data saving and/or your login data sync!");
						return;
					}
					smcp.clearMessagePackets();
					out.writeUTF("Ignore");
					out.writeUTF("Send");
					out.writeUTF(server);
					out.writeUTF(player);
					out.writeUTF(sender);
					source.getServers().forEach(send -> {
						if(!send.isEmpty()) {
							source.sendPluginMessage(send.getName(), outstream.toByteArray());
						}
					});
				}
				if(identifier.equals("Offline")) {
					String server = in.readUTF();
					String player = in.readUTF();
					String sender = in.readUTF();
					SynchronizedMineverseChatPlayer smcp = MineverseChatAPI.getSynchronizedMineverseChatPlayer(UUID.fromString(sender));
					if(smcp == null) {
						source.sendConsoleMessage("&8[&eVentureChat&8]&c Synchronized player instance is null!  This shouldn't be!");
						source.sendConsoleMessage("&8[&eVentureChat&8]&c You probably have an issue with your player data saving and/or your login data sync!");
						return;
					}
					smcp.incrementMessagePackets();
					AtomicInteger servers = new AtomicInteger(0);
					source.getServers().forEach(send -> {
						if(!send.isEmpty()) {
							servers.incrementAndGet();
						}
					});
					if(smcp.getMessagePackets() >= servers.get()) {
						smcp.clearMessagePackets();
						out.writeUTF("Ignore");
						out.writeUTF("Offline");
						out.writeUTF(player);
						out.writeUTF(sender);
						if(!source.getServer(server).isEmpty()) {
							source.sendPluginMessage(server, outstream.toByteArray());
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
					if(!source.getServer(server).isEmpty()) {
						source.sendPluginMessage(server, outstream.toByteArray());
					}
				}
			}
			if(subchannel.equals("Mute")) {
				String identifier = in.readUTF();
				if(identifier.equals("Send")) {
					String server = serverName;
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
					source.getServers().forEach(send -> {
						if(!send.isEmpty()) {
							source.sendPluginMessage(send.getName(), outstream.toByteArray());
						}
					});
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
					if(!source.getServer(server).isEmpty()) {
						source.sendPluginMessage(server, outstream.toByteArray());
					}
				}
				if(identifier.equals("Offline")) {
					String server = in.readUTF();
					UUID temporaryDataInstanceUUID = UUID.fromString(in.readUTF());
					String senderIdentifier = in.readUTF();
					String playerToMute = in.readUTF();
					TemporaryDataInstance temporaryDataInstance = TemporaryDataInstance.getTemporaryDataInstance(temporaryDataInstanceUUID);
					temporaryDataInstance.incrementMessagePackets();
					AtomicInteger servers = new AtomicInteger(0);
					source.getServers().forEach(send -> {
						if(!send.isEmpty()) {
							servers.incrementAndGet();
						}
					});
					if(temporaryDataInstance.getMessagePackets() >= servers.get()) {
						temporaryDataInstance.destroyInstance();
						out.writeUTF("Mute");
						out.writeUTF("Offline");
						out.writeUTF(senderIdentifier);
						out.writeUTF(playerToMute);
						if(!source.getServer(server).isEmpty()) {
							source.sendPluginMessage(server, outstream.toByteArray());
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
					if(!source.getServer(server).isEmpty()) {
						source.sendPluginMessage(server, outstream.toByteArray());
					}
				}
			}
			if(subchannel.equals("Unmute")) {
				String identifier = in.readUTF();
				if(identifier.equals("Send")) {
					String server = serverName;
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
					source.getServers().forEach(send -> {
						if(!send.isEmpty()) {
							source.sendPluginMessage(send.getName(), outstream.toByteArray());
						}
					});
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
					if(!source.getServer(server).isEmpty()) {
						source.sendPluginMessage(server, outstream.toByteArray());
					}
				}
				if(identifier.equals("Offline")) {
					String server = in.readUTF();
					UUID temporaryDataInstanceUUID = UUID.fromString(in.readUTF());
					String senderIdentifier = in.readUTF();
					String playerToUnmute = in.readUTF();
					TemporaryDataInstance temporaryDataInstance = TemporaryDataInstance.getTemporaryDataInstance(temporaryDataInstanceUUID);
					temporaryDataInstance.incrementMessagePackets();
					AtomicInteger servers = new AtomicInteger(0);
					source.getServers().forEach(send -> {
						if(!send.isEmpty()) {
							servers.incrementAndGet();
						}
					});
					if(temporaryDataInstance.getMessagePackets() >= servers.get()) {
						temporaryDataInstance.destroyInstance();
						out.writeUTF("Unmute");
						out.writeUTF("Offline");
						out.writeUTF(senderIdentifier);
						out.writeUTF(playerToUnmute);
						if(!source.getServer(server).isEmpty()) {
							source.sendPluginMessage(server, outstream.toByteArray());
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
					if(!source.getServer(server).isEmpty()) {
						source.sendPluginMessage(server, outstream.toByteArray());
					}
				}
			}
			if(subchannel.equals("Message")) {
				String identifier = in.readUTF();
				if(identifier.equals("Send")) {
					String server = serverName;
					String player = in.readUTF();
					String sender = in.readUTF();
					String sName = in.readUTF();
					String send = in.readUTF();
					String echo = in.readUTF();
					String spy = in.readUTF();
					String msg = in.readUTF();
					SynchronizedMineverseChatPlayer smcp = MineverseChatAPI.getSynchronizedMineverseChatPlayer(UUID.fromString(sender));
					if(smcp == null) {
						source.sendConsoleMessage("&8[&eVentureChat&8]&c Synchronized player instance is null!  This shouldn't be!");
						source.sendConsoleMessage("&8[&eVentureChat&8]&c You probably have an issue with your player data saving and/or your login data sync!");
						return;
					}
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
					source.getServers().forEach(serv -> {
						if(!serv.isEmpty()) {
							source.sendPluginMessage(serv.getName(), outstream.toByteArray());
						}
					});
				}
				if(identifier.equals("Offline")) {
					String server = in.readUTF();
					String player = in.readUTF();
					String sender = in.readUTF();
					SynchronizedMineverseChatPlayer smcp = MineverseChatAPI.getSynchronizedMineverseChatPlayer(UUID.fromString(sender));
					if(smcp == null) {
						source.sendConsoleMessage("&8[&eVentureChat&8]&c Synchronized player instance is null!  This shouldn't be!");
						source.sendConsoleMessage("&8[&eVentureChat&8]&c You probably have an issue with your player data saving and/or your login data sync!");
						return;
					}
					smcp.incrementMessagePackets();
					AtomicInteger servers = new AtomicInteger(0);
					source.getServers().forEach(send -> {
						if(!send.isEmpty()) {
							servers.incrementAndGet();
						}
					});
					if(smcp.getMessagePackets() >= servers.get()) {
						smcp.clearMessagePackets();
						out.writeUTF("Message");
						out.writeUTF("Offline");
						out.writeUTF(player);
						out.writeUTF(sender);
						if(!source.getServer(server).isEmpty()) {
							source.sendPluginMessage(server, outstream.toByteArray());
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
					if(!source.getServer(server).isEmpty()) {
						source.sendPluginMessage(server, outstream.toByteArray());
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
					if(!source.getServer(server).isEmpty()) {
						source.sendPluginMessage(server, outstream.toByteArray());
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
					if(!source.getServer(server).isEmpty()) {
						source.sendPluginMessage(server, outstream.toByteArray());
					}
					outstream.reset();
					out = new DataOutputStream(outstream);
					out.writeUTF("Message");
					out.writeUTF("Spy");
					out.writeUTF(player);
					out.writeUTF(sName);
					out.writeUTF(spy);
					source.getServers().forEach(send -> {
						if(!send.isEmpty()) {
							source.sendPluginMessage(send.getName(), outstream.toByteArray());
						}
					});
				}
			}
			if(subchannel.equals("Sync")) {
				//System.out.println("Sync received...");
				String identifier = in.readUTF();
				if(identifier.equals("Receive")) {
					//System.out.println("Sending update...");
					String server = serverName;
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
					if(!source.getServer(server).isEmpty()) {
						source.sendPluginMessage(server, outstream.toByteArray());
					}
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
