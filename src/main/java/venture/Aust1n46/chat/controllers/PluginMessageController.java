package venture.Aust1n46.chat.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.comphenix.protocol.events.PacketContainer;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import me.clip.placeholderapi.PlaceholderAPI;
import venture.Aust1n46.chat.api.events.PrivateMessageEvent;
import venture.Aust1n46.chat.api.events.VentureChatEvent;
import venture.Aust1n46.chat.initators.commands.MuteContainer;
import venture.Aust1n46.chat.initiators.application.VentureChat;
import venture.Aust1n46.chat.localization.LocalizedMessage;
import venture.Aust1n46.chat.model.ChatChannel;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.service.ConfigService;
import venture.Aust1n46.chat.service.VentureChatDatabaseService;
import venture.Aust1n46.chat.service.VentureChatFormatService;
import venture.Aust1n46.chat.service.VentureChatPlayerApiService;
import venture.Aust1n46.chat.utilities.FormatUtils;

@Singleton
public class PluginMessageController {
	public static final String PLUGIN_MESSAGING_CHANNEL = "venturechat:data";

	@Inject
	private VentureChat plugin;
	@Inject
	private VentureChatDatabaseService databaseService;
	@Inject
	private VentureChatFormatService formatService;
	@Inject
	private VentureChatPlayerApiService playerApiService;
	@Inject
	private ConfigService configService;

	public void sendPluginMessage(ByteArrayOutputStream byteOutStream) {
		if (playerApiService.getOnlineMineverseChatPlayers().size() > 0) {
			playerApiService.getOnlineMineverseChatPlayers().iterator().next().getPlayer().sendPluginMessage(plugin, PLUGIN_MESSAGING_CHANNEL, byteOutStream.toByteArray());
		}
	}

	public void sendDiscordSRVPluginMessage(String chatChannel, String message) {
		if (playerApiService.getOnlineMineverseChatPlayers().size() == 0) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void synchronizeWithDelay(final VentureChatPlayer vcp, final boolean changes) {
		final long delayInTicks = 20L;
		plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
			public void run() {
				synchronize(vcp, false);
			}
		}, delayInTicks);
	}

	public void synchronize(VentureChatPlayer mcp, boolean changes) {
		// System.out.println("Sync started...");
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(outstream);
		try {
			out.writeUTF("Sync");
			if (!changes) {
				out.writeUTF("Receive");
				// System.out.println(mcp.getPlayer().getServer().getServerName());
				// out.writeUTF(mcp.getPlayer().getServer().getServerName());
				out.writeUTF(mcp.getUuid().toString());
				plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
					@Override
					public void run() {
						if (!mcp.isOnline() || mcp.isHasPlayed()) {
							return;
						}
						synchronize(mcp, false);
					}
				}, 20L); // one second delay before running again
			} else {
				out.writeUTF("Update");
				out.writeUTF(mcp.getUuid().toString());
				// out.writeUTF("Channels");
				int channelCount = 0;
				for (String c : mcp.getListening()) {
					ChatChannel channel = configService.getChannel(c);
					if (channel.getBungee()) {
						channelCount++;
					}
				}
				out.write(channelCount);
				for (String c : mcp.getListening()) {
					ChatChannel channel = configService.getChannel(c);
					if (channel.getBungee()) {
						out.writeUTF(channel.getName());
					}
				}
				// out.writeUTF("Mutes");
				int muteCount = 0;
				for (MuteContainer mute : mcp.getMutes()) {
					ChatChannel channel = configService.getChannel(mute.getChannel());
					if (channel.getBungee()) {
						muteCount++;
					}
				}
				// System.out.println(muteCount + " mutes");
				out.write(muteCount);
				for (MuteContainer mute : mcp.getMutes()) {
					ChatChannel channel = configService.getChannel(mute.getChannel());
					if (channel.getBungee()) {
						out.writeUTF(channel.getName());
						out.writeLong(mute.getDuration());
						out.writeUTF(mute.getReason());
					}
				}
				int ignoreCount = 0;
				for (@SuppressWarnings("unused")
				UUID c : mcp.getIgnores()) {
					ignoreCount++;
				}
				out.write(ignoreCount);
				for (UUID c : mcp.getIgnores()) {
					out.writeUTF(c.toString());
				}
				out.writeBoolean(mcp.isSpy());
				out.writeBoolean(mcp.isMessageToggle());
			}
			sendPluginMessage(outstream);
			// System.out.println("Sync start bottom...");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void processInboundPluginMessage(final String channel, final Player player, final byte[] inputStream) {
		if (!channel.equals(PLUGIN_MESSAGING_CHANNEL)) {
			return;
		}
		try {
			DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(inputStream));
			if (plugin.getConfig().getString("loglevel", "info").equals("debug")) {
				System.out.println(msgin.available() + " size on receiving end");
			}
			String subchannel = msgin.readUTF();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(stream);
			if (subchannel.equals("Chat")) {
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

				if (!configService.isChannel(chatchannel)) {
					return;
				}
				ChatChannel chatChannelObject = configService.getChannel(chatchannel);

				if (!chatChannelObject.getBungee()) {
					return;
				}

				Set<Player> recipients = new HashSet<Player>();
				for (VentureChatPlayer p : playerApiService.getOnlineMineverseChatPlayers()) {
					if (configService.isListening(p, chatChannelObject.getName())) {
						recipients.add(p.getPlayer());
					}
				}

				plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
					@Override
					public void run() {
						// Create VentureChatEvent
						VentureChatEvent ventureChatEvent = new VentureChatEvent(null, senderName, primaryGroup, chatChannelObject, recipients, recipients.size(), format, chat,
								globalJSON, hash, false);
						// Fire event and wait for other plugin listeners to act on it
						plugin.getServer().getPluginManager().callEvent(ventureChatEvent);
					}
				});

				plugin.getServer().getConsoleSender().sendMessage(consoleChat);

				if (databaseService.isEnabled()) {
					databaseService.writeVentureChat(senderUUID.toString(), senderName, server, chatchannel, chat.replace("'", "''"), "Chat");
				}

				for (VentureChatPlayer p : playerApiService.getOnlineMineverseChatPlayers()) {
					if (configService.isListening(p, chatChannelObject.getName())) {
						if (!p.isBungeeToggle() && playerApiService.getOnlineMineverseChatPlayer(senderName) == null) {
							continue;
						}

						String json = formatService.formatModerationGUI(globalJSON, p.getPlayer(), senderName, chatchannel, hash);
						PacketContainer packet = formatService.createPacketPlayOutChat(json);

						if (plugin.getConfig().getBoolean("ignorechat", false)) {
							if (!p.getIgnores().contains(senderUUID)) {
								// System.out.println("Chat sent");
								formatService.sendPacketPlayOutChat(p.getPlayer(), packet);
							}
							continue;
						}
						formatService.sendPacketPlayOutChat(p.getPlayer(), packet);
					}
				}
			}
			if (subchannel.equals("DiscordSRV")) {
				String chatChannel = msgin.readUTF();
				String message = msgin.readUTF();
				if (!configService.isChannel(chatChannel)) {
					return;
				}
				ChatChannel chatChannelObj = configService.getChannel(chatChannel);
				if (!chatChannelObj.getBungee()) {
					return;
				}

				String json = formatService.convertPlainTextToJson(message, true);
				int hash = (message.replaceAll("([�]([a-z0-9]))", "")).hashCode();

				for (VentureChatPlayer p : playerApiService.getOnlineMineverseChatPlayers()) {
					if (configService.isListening(p, chatChannelObj.getName())) {
						String finalJSON = formatService.formatModerationGUI(json, p.getPlayer(), "Discord", chatChannelObj.getName(), hash);
						PacketContainer packet = formatService.createPacketPlayOutChat(finalJSON);
						formatService.sendPacketPlayOutChat(p.getPlayer(), packet);
					}
				}
			}
			if (subchannel.equals("PlayerNames")) {
				playerApiService.clearNetworkPlayerNames();
				int playerCount = msgin.readInt();
				for (int a = 0; a < playerCount; a++) {
					playerApiService.addNetworkPlayerName(msgin.readUTF());
				}
			}
			if (subchannel.equals("Chwho")) {
				String identifier = msgin.readUTF();
				if (identifier.equals("Get")) {
					String server = msgin.readUTF();
					String sender = msgin.readUTF();
					String chatchannel = msgin.readUTF();
					List<String> listening = new ArrayList<String>();
					if (configService.isChannel(chatchannel)) {
						for (VentureChatPlayer mcp : playerApiService.getOnlineMineverseChatPlayers()) {
							if (configService.isListening(mcp, chatchannel)) {
								String entry = "&f" + mcp.getName();
								if (mcp.isMuted(chatchannel)) {
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
					for (String s : listening) {
						out.writeUTF(s);
					}
					sendPluginMessage(stream);
				}
				if (identifier.equals("Receive")) {
					String sender = msgin.readUTF();
					String stringchannel = msgin.readUTF();
					VentureChatPlayer mcp = playerApiService.getOnlineMineverseChatPlayer(UUID.fromString(sender));
					ChatChannel chatchannel = configService.getChannel(stringchannel);
					String playerList = "";
					int size = msgin.readInt();
					for (int a = 0; a < size; a++) {
						playerList += msgin.readUTF() + ChatColor.WHITE + ", ";
					}
					if (playerList.length() > 2) {
						playerList = playerList.substring(0, playerList.length() - 2);
					}
					mcp.getPlayer().sendMessage(LocalizedMessage.CHANNEL_PLAYER_LIST_HEADER.toString().replace("{channel_color}", chatchannel.getColor().toString())
							.replace("{channel_name}", chatchannel.getName()));
					mcp.getPlayer().sendMessage(FormatUtils.FormatStringAll(playerList));
				}
			}
			if (subchannel.equals("RemoveMessage")) {
				String hash = msgin.readUTF();
				plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "removemessage " + hash);
			}
			if (subchannel.equals("Sync")) {
				if (plugin.getConfig().getString("loglevel", "info").equals("debug")) {
					plugin.getServer().getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&e - Received update..."));
				}
				String uuid = msgin.readUTF();
				VentureChatPlayer p = playerApiService.getOnlineMineverseChatPlayer(UUID.fromString(uuid));
				if (p == null || p.isHasPlayed()) {
					return;
				}
				for (Object ch : p.getListening().toArray()) {
					String c = ch.toString();
					ChatChannel cha = configService.getChannel(c);
					if (cha.getBungee()) {
						p.removeListening(c);
					}
				}
				int size = msgin.read();
				for (int a = 0; a < size; a++) {
					String ch = msgin.readUTF();
					if (configService.isChannel(ch)) {
						ChatChannel cha = configService.getChannel(ch);
						if (!cha.hasPermission() || p.getPlayer().hasPermission(cha.getPermission())) {
							p.addListening(ch);
						}
					}
				}
				p.getMutes().removeIf(mute -> configService.getChannel(mute.getChannel()).getBungee());
				int sizeB = msgin.read();
				// System.out.println(sizeB + " mute size");
				for (int b = 0; b < sizeB; b++) {
					String ch = msgin.readUTF();
					long muteTime = msgin.readLong();
					String muteReason = msgin.readUTF();
					// System.out.println(ch);
					if (configService.isChannel(ch)) {
						p.addMute(ch, muteTime, muteReason);
					}
				}
				// System.out.println(msgin.available() + " available before");
				p.setSpy(msgin.readBoolean());
				p.setMessageToggle(msgin.readBoolean());
				// System.out.println(msgin.available() + " available after");
				for (Object o : p.getIgnores().toArray()) {
					p.removeIgnore((UUID) o);
				}
				int sizeC = msgin.read();
				// System.out.println(sizeC + " ignore size");
				for (int c = 0; c < sizeC; c++) {
					String i = msgin.readUTF();
					// System.out.println(i);
					p.addIgnore(UUID.fromString(i));
				}
				if (!p.isHasPlayed()) {
					boolean isThereABungeeChannel = false;
					for (ChatChannel ch : configService.getAutojoinList()) {
						if ((!ch.hasPermission() || p.getPlayer().hasPermission(ch.getPermission())) && !configService.isListening(p, ch.getName())) {
							p.addListening(ch.getName());
							if (ch.getBungee()) {
								isThereABungeeChannel = true;
							}
						}
					}
					p.setHasPlayed(true);
					// Only run a sync update if the player joined a BungeeCord channel
					if (isThereABungeeChannel) {
						synchronize(p, true);
					}
				}
			}
			if (subchannel.equals("Ignore")) {
				String identifier = msgin.readUTF();
				if (identifier.equals("Send")) {
					String server = msgin.readUTF();
					String receiver = msgin.readUTF();
					VentureChatPlayer p = playerApiService.getOnlineMineverseChatPlayer(receiver);
					UUID sender = UUID.fromString(msgin.readUTF());
					if (!plugin.getConfig().getBoolean("bungeecordmessaging", true) || p == null || !p.isOnline()) {
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
					out.writeUTF(p.getUuid().toString());
					out.writeUTF(receiver);
					out.writeUTF(sender.toString());
					sendPluginMessage(stream);
					return;
				}
				if (identifier.equals("Offline")) {
					String receiver = msgin.readUTF();
					UUID sender = UUID.fromString(msgin.readUTF());
					VentureChatPlayer p = playerApiService.getOnlineMineverseChatPlayer(sender);
					p.getPlayer().sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString().replace("{args}", receiver));
				}
				if (identifier.equals("Echo")) {
					UUID receiver = UUID.fromString(msgin.readUTF());
					String receiverName = msgin.readUTF();
					UUID sender = UUID.fromString(msgin.readUTF());
					VentureChatPlayer p = playerApiService.getOnlineMineverseChatPlayer(sender);

					if (p.getIgnores().contains(receiver)) {
						p.getPlayer().sendMessage(LocalizedMessage.IGNORE_PLAYER_OFF.toString().replace("{player}", receiverName));
						p.removeIgnore(receiver);
						synchronize(p, true);
						return;
					}

					p.addIgnore(receiver);
					p.getPlayer().sendMessage(LocalizedMessage.IGNORE_PLAYER_ON.toString().replace("{player}", receiverName));
					synchronize(p, true);
				}
			}
			if (subchannel.equals("Mute")) {
				String identifier = msgin.readUTF();
				if (identifier.equals("Send")) {
					String server = msgin.readUTF();
					String senderIdentifier = msgin.readUTF();
					String temporaryDataInstanceUUIDString = msgin.readUTF();
					String playerToMute = msgin.readUTF();
					String channelName = msgin.readUTF();
					long time = msgin.readLong();
					String reason = msgin.readUTF();
					VentureChatPlayer playerToMuteMCP = playerApiService.getOnlineMineverseChatPlayer(playerToMute);
					if (playerToMuteMCP == null) {
						out.writeUTF("Mute");
						out.writeUTF("Offline");
						out.writeUTF(server);
						out.writeUTF(temporaryDataInstanceUUIDString);
						out.writeUTF(senderIdentifier);
						out.writeUTF(playerToMute);
						sendPluginMessage(stream);
						return;
					}
					if (!configService.isChannel(channelName)) {
						return;
					}
					ChatChannel chatChannelObj = configService.getChannel(channelName);
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
					if (time > 0) {
						long datetime = System.currentTimeMillis();
						if (reason.isEmpty()) {
							playerToMuteMCP.addMute(chatChannelObj.getName(), datetime + time);
							String timeString = FormatUtils.parseTimeStringFromMillis(time);
							playerToMuteMCP.getPlayer().sendMessage(LocalizedMessage.MUTE_PLAYER_PLAYER_TIME.toString().replace("{channel_color}", chatChannelObj.getColor())
									.replace("{channel_name}", chatChannelObj.getName()).replace("{time}", timeString));
						} else {
							playerToMuteMCP.addMute(chatChannelObj.getName(), datetime + time, reason);
							String timeString = FormatUtils.parseTimeStringFromMillis(time);
							playerToMuteMCP.getPlayer().sendMessage(LocalizedMessage.MUTE_PLAYER_PLAYER_TIME_REASON.toString().replace("{channel_color}", chatChannelObj.getColor())
									.replace("{channel_name}", chatChannelObj.getName()).replace("{time}", timeString).replace("{reason}", reason));
						}
					} else {
						if (reason.isEmpty()) {
							playerToMuteMCP.addMute(chatChannelObj.getName());
							playerToMuteMCP.getPlayer().sendMessage(LocalizedMessage.MUTE_PLAYER_PLAYER.toString().replace("{channel_color}", chatChannelObj.getColor())
									.replace("{channel_name}", chatChannelObj.getName()));
						} else {
							playerToMuteMCP.addMute(chatChannelObj.getName(), reason);
							playerToMuteMCP.getPlayer().sendMessage(LocalizedMessage.MUTE_PLAYER_PLAYER_REASON.toString().replace("{channel_color}", chatChannelObj.getColor())
									.replace("{channel_name}", chatChannelObj.getName()).replace("{reason}", reason));
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
				if (identifier.equals("Valid")) {
					String senderIdentifier = msgin.readUTF();
					String playerToMute = msgin.readUTF();
					String channelName = msgin.readUTF();
					long time = msgin.readLong();
					String reason = msgin.readUTF();
					if (!configService.isChannel(channelName)) {
						return;
					}
					ChatChannel chatChannelObj = configService.getChannel(channelName);
					if (time > 0) {
						String timeString = FormatUtils.parseTimeStringFromMillis(time);
						if (reason.isEmpty()) {
							if (senderIdentifier.equals("VentureChat:Console")) {
								plugin.getServer().getConsoleSender().sendMessage(LocalizedMessage.MUTE_PLAYER_SENDER_TIME.toString().replace("{player}", playerToMute)
										.replace("{channel_color}", chatChannelObj.getColor()).replace("{channel_name}", chatChannelObj.getName()).replace("{time}", timeString));
							} else {
								UUID sender = UUID.fromString(senderIdentifier);
								VentureChatPlayer senderMCP = playerApiService.getOnlineMineverseChatPlayer(sender);
								senderMCP.getPlayer().sendMessage(LocalizedMessage.MUTE_PLAYER_SENDER_TIME.toString().replace("{player}", playerToMute)
										.replace("{channel_color}", chatChannelObj.getColor()).replace("{channel_name}", chatChannelObj.getName()).replace("{time}", timeString));
							}
						} else {
							if (senderIdentifier.equals("VentureChat:Console")) {
								plugin.getServer().getConsoleSender()
										.sendMessage(LocalizedMessage.MUTE_PLAYER_SENDER_TIME_REASON.toString().replace("{player}", playerToMute)
												.replace("{channel_color}", chatChannelObj.getColor()).replace("{channel_name}", chatChannelObj.getName())
												.replace("{time}", timeString).replace("{reason}", reason));
							} else {
								UUID sender = UUID.fromString(senderIdentifier);
								VentureChatPlayer senderMCP = playerApiService.getOnlineMineverseChatPlayer(sender);
								senderMCP.getPlayer()
										.sendMessage(LocalizedMessage.MUTE_PLAYER_SENDER_TIME_REASON.toString().replace("{player}", playerToMute)
												.replace("{channel_color}", chatChannelObj.getColor()).replace("{channel_name}", chatChannelObj.getName())
												.replace("{time}", timeString).replace("{reason}", reason));
							}
						}
					} else {
						if (reason.isEmpty()) {
							if (senderIdentifier.equals("VentureChat:Console")) {
								plugin.getServer().getConsoleSender().sendMessage(LocalizedMessage.MUTE_PLAYER_SENDER.toString().replace("{player}", playerToMute)
										.replace("{channel_color}", chatChannelObj.getColor()).replace("{channel_name}", chatChannelObj.getName()));
							} else {
								UUID sender = UUID.fromString(senderIdentifier);
								VentureChatPlayer senderMCP = playerApiService.getOnlineMineverseChatPlayer(sender);
								senderMCP.getPlayer().sendMessage(LocalizedMessage.MUTE_PLAYER_SENDER.toString().replace("{player}", playerToMute)
										.replace("{channel_color}", chatChannelObj.getColor()).replace("{channel_name}", chatChannelObj.getName()));
							}
						} else {
							if (senderIdentifier.equals("VentureChat:Console")) {
								plugin.getServer().getConsoleSender().sendMessage(LocalizedMessage.MUTE_PLAYER_SENDER_REASON.toString().replace("{player}", playerToMute)
										.replace("{channel_color}", chatChannelObj.getColor()).replace("{channel_name}", chatChannelObj.getName()).replace("{reason}", reason));
							} else {
								UUID sender = UUID.fromString(senderIdentifier);
								VentureChatPlayer senderMCP = playerApiService.getOnlineMineverseChatPlayer(sender);
								senderMCP.getPlayer().sendMessage(LocalizedMessage.MUTE_PLAYER_SENDER_REASON.toString().replace("{player}", playerToMute)
										.replace("{channel_color}", chatChannelObj.getColor()).replace("{channel_name}", chatChannelObj.getName()).replace("{reason}", reason));
							}
						}
					}
					return;
				}
				if (identifier.equals("Offline")) {
					String senderIdentifier = msgin.readUTF();
					String playerToMute = msgin.readUTF();
					if (senderIdentifier.equals("VentureChat:Console")) {
						plugin.getServer().getConsoleSender().sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString().replace("{args}", playerToMute));
						return;
					}
					UUID sender = UUID.fromString(senderIdentifier);
					VentureChatPlayer senderMCP = playerApiService.getOnlineMineverseChatPlayer(sender);
					senderMCP.getPlayer().sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString().replace("{args}", playerToMute));
					return;
				}
				if (identifier.equals("AlreadyMuted")) {
					String senderIdentifier = msgin.readUTF();
					String playerToMute = msgin.readUTF();
					String channelName = msgin.readUTF();
					if (!configService.isChannel(channelName)) {
						return;
					}
					ChatChannel chatChannelObj = configService.getChannel(channelName);
					if (senderIdentifier.equals("VentureChat:Console")) {
						plugin.getServer().getConsoleSender().sendMessage(LocalizedMessage.PLAYER_ALREADY_MUTED.toString().replace("{player}", playerToMute)
								.replace("{channel_color}", chatChannelObj.getColor()).replace("{channel_name}", chatChannelObj.getName()));
						return;
					}
					UUID sender = UUID.fromString(senderIdentifier);
					VentureChatPlayer senderMCP = playerApiService.getOnlineMineverseChatPlayer(sender);
					senderMCP.getPlayer().sendMessage(LocalizedMessage.PLAYER_ALREADY_MUTED.toString().replace("{player}", playerToMute)
							.replace("{channel_color}", chatChannelObj.getColor()).replace("{channel_name}", chatChannelObj.getName()));
					return;
				}
			}
			if (subchannel.equals("Unmute")) {
				String identifier = msgin.readUTF();
				if (identifier.equals("Send")) {
					String server = msgin.readUTF();
					String senderIdentifier = msgin.readUTF();
					String temporaryDataInstanceUUIDString = msgin.readUTF();
					String playerToUnmute = msgin.readUTF();
					String channelName = msgin.readUTF();
					VentureChatPlayer playerToUnmuteMCP = playerApiService.getOnlineMineverseChatPlayer(playerToUnmute);
					if (playerToUnmuteMCP == null) {
						out.writeUTF("Unmute");
						out.writeUTF("Offline");
						out.writeUTF(server);
						out.writeUTF(temporaryDataInstanceUUIDString);
						out.writeUTF(senderIdentifier);
						out.writeUTF(playerToUnmute);
						sendPluginMessage(stream);
						return;
					}
					if (!configService.isChannel(channelName)) {
						return;
					}
					ChatChannel chatChannelObj = configService.getChannel(channelName);
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
					playerToUnmuteMCP.getPlayer().sendMessage(LocalizedMessage.UNMUTE_PLAYER_PLAYER.toString().replace("{player}", player.getName())
							.replace("{channel_color}", chatChannelObj.getColor()).replace("{channel_name}", chatChannelObj.getName()));
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
				if (identifier.equals("Valid")) {
					String senderIdentifier = msgin.readUTF();
					String playerToUnmute = msgin.readUTF();
					String channelName = msgin.readUTF();
					if (!configService.isChannel(channelName)) {
						return;
					}
					ChatChannel chatChannelObj = configService.getChannel(channelName);
					if (senderIdentifier.equals("VentureChat:Console")) {
						plugin.getServer().getConsoleSender().sendMessage(LocalizedMessage.UNMUTE_PLAYER_SENDER.toString().replace("{player}", playerToUnmute)
								.replace("{channel_color}", chatChannelObj.getColor()).replace("{channel_name}", chatChannelObj.getName()));
					} else {
						UUID sender = UUID.fromString(senderIdentifier);
						VentureChatPlayer senderMCP = playerApiService.getOnlineMineverseChatPlayer(sender);
						senderMCP.getPlayer().sendMessage(LocalizedMessage.UNMUTE_PLAYER_SENDER.toString().replace("{player}", playerToUnmute)
								.replace("{channel_color}", chatChannelObj.getColor()).replace("{channel_name}", chatChannelObj.getName()));
					}
					return;
				}
				if (identifier.equals("Offline")) {
					String senderIdentifier = msgin.readUTF();
					String playerToUnmute = msgin.readUTF();
					if (senderIdentifier.equals("VentureChat:Console")) {
						plugin.getServer().getConsoleSender().sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString().replace("{args}", playerToUnmute));
						return;
					}
					UUID sender = UUID.fromString(senderIdentifier);
					VentureChatPlayer senderMCP = playerApiService.getOnlineMineverseChatPlayer(sender);
					senderMCP.getPlayer().sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString().replace("{args}", playerToUnmute));
					return;
				}
				if (identifier.equals("NotMuted")) {
					String senderIdentifier = msgin.readUTF();
					String playerToUnmute = msgin.readUTF();
					String channelName = msgin.readUTF();
					if (!configService.isChannel(channelName)) {
						return;
					}
					ChatChannel chatChannelObj = configService.getChannel(channelName);
					if (senderIdentifier.equals("VentureChat:Console")) {
						plugin.getServer().getConsoleSender().sendMessage(LocalizedMessage.PLAYER_NOT_MUTED.toString().replace("{player}", playerToUnmute)
								.replace("{channel_color}", chatChannelObj.getColor()).replace("{channel_name}", chatChannelObj.getName()));
						return;
					}
					UUID sender = UUID.fromString(senderIdentifier);
					VentureChatPlayer senderMCP = playerApiService.getOnlineMineverseChatPlayer(sender);
					senderMCP.getPlayer().sendMessage(LocalizedMessage.PLAYER_NOT_MUTED.toString().replace("{player}", playerToUnmute)
							.replace("{channel_color}", chatChannelObj.getColor()).replace("{channel_name}", chatChannelObj.getName()));
					return;
				}
			}
			if (subchannel.equals("Message")) {
				String identifier = msgin.readUTF();
				if (identifier.equals("Send")) {
					String server = msgin.readUTF();
					String receiver = msgin.readUTF();
					VentureChatPlayer p = playerApiService.getOnlineMineverseChatPlayer(receiver);
					UUID sender = UUID.fromString(msgin.readUTF());
					String sName = msgin.readUTF();
					String send = msgin.readUTF();
					String echo = msgin.readUTF();
					String spy = msgin.readUTF();
					String msg = msgin.readUTF();
					if (!plugin.getConfig().getBoolean("bungeecordmessaging", true) || p == null) {
						out.writeUTF("Message");
						out.writeUTF("Offline");
						out.writeUTF(server);
						out.writeUTF(receiver);
						out.writeUTF(sender.toString());
						sendPluginMessage(stream);
						return;
					}
					if (p.getIgnores().contains(sender)) {
						out.writeUTF("Message");
						out.writeUTF("Ignore");
						out.writeUTF(server);
						out.writeUTF(receiver);
						out.writeUTF(sender.toString());
						sendPluginMessage(stream);
						return;
					}
					if (!p.isMessageToggle()) {
						out.writeUTF("Message");
						out.writeUTF("Blocked");
						out.writeUTF(server);
						out.writeUTF(receiver);
						out.writeUTF(sender.toString());
						sendPluginMessage(stream);
						return;
					}

					send = FormatUtils.FormatStringAll(PlaceholderAPI.setBracketPlaceholders(p.getPlayer(), send.replaceAll("receiver_", ""))) + msg;
					echo = FormatUtils.FormatStringAll(PlaceholderAPI.setBracketPlaceholders(p.getPlayer(), echo.replaceAll("receiver_", ""))) + msg;
					spy = FormatUtils.FormatStringAll(PlaceholderAPI.setBracketPlaceholders(p.getPlayer(), spy.replaceAll("receiver_", ""))) + msg;

					PrivateMessageEvent privateMessageEvent = new PrivateMessageEvent(playerApiService.getOnlineMineverseChatPlayer(sender), p, msg, echo, send, spy, true);
					Bukkit.getPluginManager().callEvent(privateMessageEvent);
					send = privateMessageEvent.getSend();
					echo = privateMessageEvent.getEcho();
					spy = privateMessageEvent.getSpy();
					
					p.getPlayer().sendMessage(send);
					if (p.isNotifications()) {
						formatService.playMessageSound(p);
					}
					if (playerApiService.getMineverseChatPlayer(sender) == null) {
						VentureChatPlayer senderMCP = new VentureChatPlayer(sender, sName, configService.getDefaultChannel());
						playerApiService.addMineverseChatPlayerToMap(senderMCP);
						playerApiService.addNameToMap(senderMCP);
					}
					p.setReplyPlayer(sender);
					out.writeUTF("Message");
					out.writeUTF("Echo");
					out.writeUTF(server);
					out.writeUTF(receiver);
					out.writeUTF(p.getUuid().toString());
					out.writeUTF(sender.toString());
					out.writeUTF(sName);
					out.writeUTF(echo);
					out.writeUTF(spy);
					sendPluginMessage(stream);
					return;
				}
				if (identifier.equals("Offline")) {
					String receiver = msgin.readUTF();
					UUID sender = UUID.fromString(msgin.readUTF());
					VentureChatPlayer p = playerApiService.getOnlineMineverseChatPlayer(sender);
					p.getPlayer().sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString().replace("{args}", receiver));
					p.setReplyPlayer(null);
				}
				if (identifier.equals("Ignore")) {
					String receiver = msgin.readUTF();
					UUID sender = UUID.fromString(msgin.readUTF());
					VentureChatPlayer p = playerApiService.getOnlineMineverseChatPlayer(sender);
					p.getPlayer().sendMessage(LocalizedMessage.IGNORING_MESSAGE.toString().replace("{player}", receiver));
				}
				if (identifier.equals("Blocked")) {
					String receiver = msgin.readUTF();
					UUID sender = UUID.fromString(msgin.readUTF());
					VentureChatPlayer p = playerApiService.getOnlineMineverseChatPlayer(sender);
					p.getPlayer().sendMessage(LocalizedMessage.BLOCKING_MESSAGE.toString().replace("{player}", receiver));
				}
				if (identifier.equals("Echo")) {
					String receiverName = msgin.readUTF();
					UUID receiverUUID = UUID.fromString(msgin.readUTF());
					UUID senderUUID = UUID.fromString(msgin.readUTF());
					VentureChatPlayer senderMCP = playerApiService.getOnlineMineverseChatPlayer(senderUUID);
					String echo = msgin.readUTF();
					if (playerApiService.getMineverseChatPlayer(receiverUUID) == null) {
						VentureChatPlayer receiverMCP = new VentureChatPlayer(receiverUUID, receiverName, configService.getDefaultChannel());
						playerApiService.addMineverseChatPlayerToMap(receiverMCP);
						playerApiService.addNameToMap(receiverMCP);
					}
					senderMCP.setReplyPlayer(receiverUUID);
					senderMCP.getPlayer().sendMessage(echo);
				}
				if (identifier.equals("Spy")) {
					String receiverName = msgin.readUTF();
					String senderName = msgin.readUTF();
					String spy = msgin.readUTF();
					if (!spy.startsWith("VentureChat:NoSpy")) {
						for (VentureChatPlayer pl : playerApiService.getOnlineMineverseChatPlayers()) {
							if (pl.isSpy() && !pl.getName().equals(senderName) && !pl.getName().equals(receiverName)) {
								pl.getPlayer().sendMessage(spy);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
