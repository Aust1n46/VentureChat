package mineverse.Aust1n46.chat.listeners;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.PluginManager;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.massivecraft.factions.entity.MPlayer;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.api.events.ChannelJoinEvent;
import mineverse.Aust1n46.chat.api.events.VentureChatEvent;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.channel.ChatChannelInfo;
import mineverse.Aust1n46.chat.database.DatabaseSender;
import mineverse.Aust1n46.chat.utilities.Format;
import mineverse.Aust1n46.chat.utilities.FormatTags;
import mineverse.Aust1n46.chat.versions.VersionHandler;

//This class listens to chat through the chat event and handles the bulk of the chat channels and formatting.
public class ChatListener implements Listener {
	private MineverseChat plugin = MineverseChat.getInstance();
	private ChatChannelInfo cc;

	public ChatListener(ChatChannelInfo cc) {
		this.cc = cc;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onChannelJoin(ChannelJoinEvent event) {

	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent event) {
		/*
		 * MineverseChatPlayer mcp =
		 * MineverseChatAPI.getMineverseChatPlayer(event.getPlayer());
		 * if(mcp.isAFK()) { mcp.setAFK(false);
		 * mcp.getPlayer().sendMessage(ChatColor.GOLD + "You are no longer AFK."
		 * ); if(plugin.getConfig().getBoolean("broadcastafk")) {
		 * for(MineverseChatPlayer p : MineverseChat.players) { if(p.isOnline()
		 * && !p.getName().equals(mcp.getName())) {
		 * p.getPlayer().sendMessage(ChatColor.GOLD + mcp.getName() +
		 * " is no longer AFK."); } } } }
		 */
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
		if(event.isCancelled()) {
			return;
		}
		boolean bungee = false;
		String chat = event.getMessage();
		String format;
		Set<Player> recipients = event.getRecipients();
		MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer(event.getPlayer());
		ChatChannel eventChannel = mcp.getCurrentChannel();
		
		if(mcp.isEditing()) {
			mcp.getPlayer().sendMessage(Format.FormatStringAll(chat));
			mcp.setEditing(false);
			event.setCancelled(true);
			return;
		}
		
		if(mcp.isQuickChat()) {
			eventChannel = mcp.getQuickChannel();
		}
		
		if(mcp.isAFK()) {
			mcp.setAFK(false);
			mcp.getPlayer().sendMessage(ChatColor.GOLD + "You are no longer AFK.");
			if(plugin.getConfig().getBoolean("broadcastafk")) {
				for(MineverseChatPlayer p : MineverseChat.onlinePlayers) {
					if(!p.getName().equals(mcp.getName())) {
						p.getPlayer().sendMessage(ChatColor.GOLD + mcp.getName() + " is no longer AFK.");
					}
				}
			}
		}
		
		if(mcp.hasConversation() && !mcp.isQuickChat()) {
			MineverseChatPlayer tp = MineverseChatAPI.getMineverseChatPlayer(mcp.getConversation());
			if(!tp.isOnline()) {
				mcp.getPlayer().sendMessage(ChatColor.RED + tp.getName() + " is not available.");
				if(!mcp.getPlayer().hasPermission("venturechat.spy.override")) {
					for(MineverseChatPlayer p : MineverseChat.onlinePlayers) {
						if(p.isSpy()) {
							p.getPlayer().sendMessage(mcp.getName() + " is no longer in a private conversation with " + tp.getName() + ".");
						}
					}
				}
				mcp.setConversation(null);
			}
			else {
				if(tp.getIgnores().contains(mcp.getUUID())) {
					mcp.getPlayer().sendMessage(ChatColor.GOLD + tp.getName() + " is currently ignoring your messages.");
					event.setCancelled(true);
					return;
				}
				if(!tp.getMessageToggle()) {
					mcp.getPlayer().sendMessage(ChatColor.GOLD + tp.getName() + " is currently blocking messages.");
					event.setCancelled(true);
					return;
				}
				String filtered = chat;
				String echo = "";
				String send = "";
				String spy = "";
				if(mcp.hasFilter()) {
					filtered = cc.FilterChat(filtered);
				}
				if(mcp.getPlayer().hasPermission("venturechat.color")) {
					filtered = Format.FormatStringColor(filtered);
				}
				if(mcp.getPlayer().hasPermission("venturechat.format")) {
					filtered = Format.FormatString(filtered);
				}
				filtered = " " + filtered;
				if(plugin.getConfig().getString("tellformatto").equalsIgnoreCase("Default")) {
					echo = "You message " + tp.getPlayer().getDisplayName() + ":" + ChatColor.valueOf(cc.tellColor.toUpperCase()) + filtered;
				}
				else {
					echo = Format.FormatStringAll(plugin.getConfig().getString("tellformatto").replace("{playerto}", tp.getPlayer().getDisplayName()).replace("{playerfrom}", mcp.getPlayer().getDisplayName())) + filtered;
				}
				if(plugin.getConfig().getString("tellformatfrom").equalsIgnoreCase("Default")) {
					send = mcp.getPlayer().getDisplayName() + " messages you:" + ChatColor.valueOf(cc.tellColor.toUpperCase()) + filtered;
				}
				else {
					send = Format.FormatStringAll(plugin.getConfig().getString("tellformatfrom").replace("{playerto}", tp.getPlayer().getDisplayName()).replace("{playerfrom}", mcp.getPlayer().getDisplayName())) + filtered;
				}
				if(plugin.getConfig().getString("tellformatspy").equalsIgnoreCase("Default")) {
					spy = mcp.getName() + " messages " + tp.getName() + ":" + ChatColor.valueOf(cc.tellColor.toUpperCase()) + filtered;
				}
				else {
					spy = Format.FormatStringAll(plugin.getConfig().getString("tellformatspy").replace("{playerto}", tp.getName()).replace("{playerfrom}", mcp.getName())) + filtered;
				}
				if(!mcp.getPlayer().hasPermission("venturechat.spy.override")) {
					for(MineverseChatPlayer p : MineverseChat.onlinePlayers) {
						if(p.isOnline() && p.isSpy()) {
							p.getPlayer().sendMessage(spy);
						}
					}
				}
				tp.getPlayer().sendMessage(send);
				mcp.getPlayer().sendMessage(echo);
				if(tp.hasNotifications()) {
					if(VersionHandler.is1_8()) {
						tp.getPlayer().playSound(tp.getPlayer().getLocation(), Sound.valueOf("LEVEL_UP"), 1, 0);
					}
					if(VersionHandler.is1_9()) {
						tp.getPlayer().playSound(tp.getPlayer().getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1, 0);
					}
				}
				mcp.setReplyPlayer(tp.getUUID());
				tp.setReplyPlayer(mcp.getUUID());
				Bukkit.getConsoleSender().sendMessage(mcp.getName() + " messages " + tp.getName() + ":" + ChatColor.valueOf(cc.tellColor.toUpperCase()) + filtered);
				if(plugin.mysql) {
					Calendar currentDate = Calendar.getInstance();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String date = formatter.format(currentDate.getTime());
					DatabaseSender.writeToMySQL("ChatTime", "UUID", "Name", "Server", "Channel", "Text", "Type", date, mcp.getUUID().toString(), mcp.getName(), plugin.getServer().getName(), "Messaging_Component", chat.replace("'", "''"), "Chat");
				}
			}
			event.setCancelled(true);
			return;
		}

		if(mcp.isPartyChat() && !mcp.isQuickChat()) {
			if(mcp.hasParty()) {
				String partyformat = "";
				for(MineverseChatPlayer p : MineverseChat.onlinePlayers) {
					if((p.hasParty() && p.getParty().toString().equals(mcp.getParty().toString()) || p.isSpy())) {
						String filtered = chat;
						if(mcp.hasFilter()) {
							filtered = cc.FilterChat(filtered);
						}
						if(mcp.getPlayer().hasPermission("venturechat.color")) {
							filtered = Format.FormatStringColor(filtered);
						}
						if(mcp.getPlayer().hasPermission("venturechat.format")) {
							filtered = Format.FormatString(filtered);
						}
						filtered = " " + filtered;
						if(plugin.getConfig().getString("partyformat").equalsIgnoreCase("Default")) {
							partyformat = ChatColor.GREEN + "[" + MineverseChatAPI.getMineverseChatPlayer(mcp.getParty()).getName() + "'s Party] " + mcp.getName() + ":" + filtered;
						}
						else {
							partyformat = Format.FormatStringAll(plugin.getConfig().getString("partyformat").replace("{host}", MineverseChatAPI.getMineverseChatPlayer(mcp.getParty()).getName()).replace("{player}", mcp.getName())) + filtered;
						}
						p.getPlayer().sendMessage(partyformat);
					}
				}
				Bukkit.getConsoleSender().sendMessage(partyformat);
				if(plugin.mysql) {
					Statement statement;
					Calendar currentDate = Calendar.getInstance();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String date = formatter.format(currentDate.getTime());
					try {
						statement = plugin.c.createStatement();
						statement.executeUpdate("INSERT INTO `VentureChat` (`ChatTime`, `UUID`, `Name`, `Server`, `Channel`, `Text`, `Type`) VALUES ('" + date + "', '" + mcp.getUUID().toString() + "', '" + mcp.getName() + "', '" + plugin.getServer().getName() + "', 'Party_Component', '" + chat.replace("'", "''") + "', 'Chat');");
					}
					catch(SQLException e) {
						e.printStackTrace();
					}
				}
				event.setCancelled(true);
				return;
			}
			mcp.getPlayer().sendMessage(ChatColor.RED + "You are not in a party.");
			event.setCancelled(true);
			return;
		}
		
		Location locreceip;
		Location locsender = mcp.getPlayer().getLocation();
		Location diff;
		Boolean filterthis = true;
		mcp.addListening(eventChannel.getName());
		if(mcp.isMuted(eventChannel.getName())) {
			String timedMute = "";
			if(mcp.getMutes().get(eventChannel.getName()).intValue() > 0) {
				//Calendar currentDate = Calendar.getInstance();
				//SimpleDateFormat formatter = new SimpleDateFormat("dd:HH:mm:ss");
				//String date = formatter.format(currentDate.getTime());
				//String[] datearray = date.split(":");
				//int datetime = (Integer.parseInt(datearray[0]) * 1440) + (Integer.parseInt(datearray[1]) * 60) + (Integer.parseInt(datearray[2]));
				
				int time = (int) (System.currentTimeMillis() / 60000);
				
				String keyword = "minutes";
				int timemark = mcp.getMutes().get(eventChannel.getName()).intValue();
				int remaining = timemark - time;
				if(remaining <= 0) remaining = 1;
				if(remaining == 1) keyword = "minute";
				timedMute = ChatColor.RED + " for " + remaining + " more " + keyword;
			}
			mcp.getPlayer().sendMessage(ChatColor.RED + "You are muted in this channel: " + ChatColor.valueOf(eventChannel.getColor().toUpperCase()) + eventChannel.getName() + timedMute);
			mcp.setQuickChat(false);
			event.setCancelled(true);
			return;
		}
		Double chDistance = (double) 0;
		int chCooldown = 0;
		String curColor = "";
		if(eventChannel.hasPermission() && !mcp.getPlayer().hasPermission(eventChannel.getPermission())) {
			mcp.getPlayer().sendMessage(ChatColor.RED + "You do not have permission for this channel.");
			mcp.setQuickChat(false);
			mcp.removeListening(eventChannel.getName());
			mcp.setCurrentChannel(cc.getDefaultChannel());
			event.setCancelled(true);
			return;
		}
		curColor = eventChannel.getChatColor().toUpperCase();
		bungee = eventChannel.getBungee();
		
		int time = (int) (System.currentTimeMillis() / 1000);
		
		if(eventChannel.hasCooldown()) {
			chCooldown = eventChannel.getCooldown();
		}
		try {
			if(mcp.hasCooldown(eventChannel)) {
				int timemark = mcp.getCooldowns().get(eventChannel).intValue();
				if(time < timemark) {
					int remaining = timemark - time;
					String keyword = "seconds";
					if(remaining == 1) keyword = "second";
					mcp.getPlayer().sendMessage(ChatColor.RED + "" + remaining + " " + keyword + " of cooldown remaining.");
					mcp.setQuickChat(false);
					event.setCancelled(true);
					bungee = false;
					return;
				}
			}
			if(eventChannel.hasCooldown()) {
				if(!mcp.getPlayer().hasPermission("venturechat.cooldown.bypass")) {
					mcp.addCooldown(eventChannel, time + chCooldown);
				}
			}
		}
		catch(NumberFormatException e) {
			e.printStackTrace();
		}
		
		if(mcp.hasSpam(eventChannel) && plugin.getConfig().getConfigurationSection("antispam").getBoolean("enabled") && !mcp.getPlayer().hasPermission("venturechat.spam.bypass")) {
			int spamcount = mcp.getSpam().get(eventChannel).get(0);
			int spamtime = mcp.getSpam().get(eventChannel).get(1);
			int spamtimeconfig = plugin.getConfig().getConfigurationSection("antispam").getInt("spamnumber");
			int mutedfor = plugin.getConfig().getConfigurationSection("antispam").getInt("mutetime", 0);
			
			int datetime = time/60;
			if(time < spamtime + plugin.getConfig().getConfigurationSection("antispam").getInt("spamtime")) {
				if(spamcount + 1 >= spamtimeconfig) {
					mcp.addMute(eventChannel.getName(), datetime + mutedfor);
					String timedmute = "";
					if(mutedfor > 0) {
						String keyword = "minutes";
						if(mutedfor == 1) keyword = "minute";
						timedmute = ChatColor.RED + " for " + mutedfor + " " + keyword;
					}
					mcp.getSpam().get(eventChannel).set(0, 0);
					mcp.getPlayer().sendMessage(ChatColor.RED + "You have been muted for spamming in: " + ChatColor.valueOf(eventChannel.getColor().toUpperCase()) + eventChannel.getName() + timedmute);
					mcp.setQuickChat(false);
					event.setCancelled(true);
					return;
				}
				else {
					if(spamtimeconfig % 2 != 0) spamtimeconfig++;
					if(spamcount + 1 == spamtimeconfig / 2) {
						mcp.getPlayer().sendMessage(ChatColor.RED + "Slow down your chat! You're halfway to being muted for spam!");
					}
					mcp.getSpam().get(eventChannel).set(0, spamcount + 1);
				}
			}
			else {
				mcp.getSpam().get(eventChannel).set(0, 1);
				mcp.getSpam().get(eventChannel).set(1, time);
			}
		}
		else {
			mcp.addSpam(eventChannel);
			mcp.getSpam().get(eventChannel).add(0, 1);
			mcp.getSpam().get(eventChannel).add(1, time);
		}
		
		if(eventChannel.hasDistance()) {
			chDistance = eventChannel.getDistance();
		}
		if(plugin.getConfig().getConfigurationSection("channels." + eventChannel.getName()).getString("format").equalsIgnoreCase("Default")) {
			if(curColor.equalsIgnoreCase("None")) {
				format = FormatTags.ChatFormat(ChatColor.valueOf(eventChannel.getColor().toUpperCase()) + "[" + eventChannel.getName() + "] {prefix}{name}" + ChatColor.valueOf(eventChannel.getColor().toUpperCase()) + ":", mcp.getPlayer(), plugin, cc, eventChannel, plugin.getConfig().getBoolean("jsonFormat"));
			}
			else {
				format = FormatTags.ChatFormat(ChatColor.valueOf(eventChannel.getColor().toUpperCase()) + "[" + eventChannel.getName() + "] {prefix}{name}" + ChatColor.valueOf(eventChannel.getColor().toUpperCase()) + ":" + ChatColor.valueOf(eventChannel.getChatColor().toUpperCase()), mcp.getPlayer(), plugin, cc, eventChannel, plugin.getConfig().getBoolean("jsonFormat"));
			}
		}
		else {
			format = FormatTags.ChatFormat(plugin.getConfig().getConfigurationSection("channels." + eventChannel.getName()).getString("format"), mcp.getPlayer(), plugin, cc, eventChannel, plugin.getConfig().getBoolean("jsonFormat"));
			if(plugin.getConfig().getBoolean("formatcleaner", false)) {
				format = format.replace("[]", " ");
				format = format.replace("    ", " ").replace("   ", " ").replace("  ", " ");
			}
		}
		filterthis = eventChannel.isFiltered();
		if(filterthis) {
			if(mcp.hasFilter()) {
				chat = cc.FilterChat(chat);
			}
		}
		PluginManager pluginManager = plugin.getServer().getPluginManager();
		for(MineverseChatPlayer p : MineverseChat.onlinePlayers) {
			if(p.getPlayer() != mcp.getPlayer()) {
				if(!p.getListening().contains(eventChannel.getName())) {
					recipients.remove(p.getPlayer());
					continue;
				}
				if(plugin.getConfig().getBoolean("ignorechat", false) && p.getIgnores().contains(mcp.getUUID())) {
					recipients.remove(p.getPlayer());
					continue;
				}
				if(plugin.getConfig().getBoolean("enable_towny_channel") && pluginManager.isPluginEnabled("Towny")) {
					try {
						Resident r = TownyUniverse.getDataSource().getResident(p.getName());
						Resident pp = TownyUniverse.getDataSource().getResident(mcp.getName());
						if(eventChannel.getName().equalsIgnoreCase("Town")) {
							if(!pp.hasTown()) {
								recipients.remove(p.getPlayer());
								continue;
							}
							else if(!r.hasTown()) {
								recipients.remove(p.getPlayer());
								continue;
							}
							else if(!(r.getTown().getName().equals(pp.getTown().getName()))) {
								recipients.remove(p.getPlayer());
								continue;
							}
						}
						if(eventChannel.getName().equalsIgnoreCase("Nation")) {
							if(!pp.hasNation()) {
								recipients.remove(p.getPlayer());
								continue;
							}
							else if(!r.hasNation()) {
								recipients.remove(p.getPlayer());
								continue;
							}
							else if(!(r.getTown().getNation().getName().equals(pp.getTown().getNation().getName()))) {
								recipients.remove(p.getPlayer());
								continue;
							}
						}
					}
					catch(Exception ex) {
						ex.printStackTrace();
					}
				}

				if(plugin.getConfig().getBoolean("enable_factions_channel") && pluginManager.isPluginEnabled("Factions")) {
					try {
						MPlayer mplayer = MPlayer.get(mcp.getPlayer());
						MPlayer mplayerp = MPlayer.get(p.getPlayer());
						if(eventChannel.getName().equalsIgnoreCase("Faction")) {
							if(!mplayer.hasFaction()) {
								recipients.remove(p.getPlayer());
							}
							else if(!mplayerp.hasFaction()) {
								recipients.remove(p.getPlayer());
							}
							else if(!(mplayer.getFactionName().equals(mplayerp.getFactionName()))) {
								recipients.remove(p.getPlayer());
							}
						}
					}
					catch(Exception ex) {
						ex.printStackTrace();
					}
				}

				if(chDistance > (double) 0 && !bungee && !p.getRangedSpy()) {
					locreceip = p.getPlayer().getLocation();
					if(locreceip.getWorld() == mcp.getPlayer().getWorld()) {
						diff = locreceip.subtract(locsender);
						if(Math.abs(diff.getX()) > chDistance || Math.abs(diff.getZ()) > chDistance) {
							recipients.remove(p.getPlayer());
							continue;
						}
						if(!mcp.getPlayer().canSee(p.getPlayer())) {
							recipients.remove(p.getPlayer());
							continue;
						}
					}
					else {
						recipients.remove(p.getPlayer());
						continue;
					}
				}
				if(!mcp.getPlayer().canSee(p.getPlayer())) {
					recipients.remove(p.getPlayer());
					continue;
				}
			}
		}
		
		if(mcp.getPlayer().hasPermission("venturechat.color")) {
			chat = Format.FormatStringColor(chat);
		}
		if(mcp.getPlayer().hasPermission("venturechat.format")) {
			chat = Format.FormatString(chat);
		}
		if(!mcp.isQuickChat()) {
			chat = " " + chat;
		}
		mcp.setQuickChat(false);
		if(curColor.equalsIgnoreCase("None")) {
			chat = Format.getLastCode(format) + chat;
		}
		else {
			chat = ChatColor.valueOf(curColor) + chat;
		}
		
		String globalJSON = Format.convertToJson(mcp, format, chat);
		String consoleChat = format + chat;
		String message = consoleChat.replaceAll("(§([a-z0-9]))", "");
		int hash = message.hashCode();
		
		VentureChatEvent ventureChatEvent = new VentureChatEvent(mcp, eventChannel, recipients, format, chat, globalJSON, hash, bungee);
		Bukkit.getServer().getPluginManager().callEvent(ventureChatEvent);
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onVentureChatEvent(VentureChatEvent event) {
		MineverseChatPlayer mcp = event.getMineverseChatPlayer();
		ChatChannel channel = event.getChannel();
		Set<Player> recipients = event.getRecipients();
		String chat = event.getChat();
		String consoleChat = event.getConsoleChat();
		String globalJSON = event.getGlobalJSON();
		int hash = event.getHash();
		boolean bungee = event.isBungee();
		
		if(plugin.mysql) {
			Statement statement;
			Calendar currentDate = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = formatter.format(currentDate.getTime());
			try {
				statement = plugin.c.createStatement();
				statement.executeUpdate("INSERT INTO `VentureChat` (`ChatTime`, `UUID`, `Name`, `Server`, `Channel`, `Text`, `Type`) VALUES ('" + date + "', '" + mcp.getUUID().toString() + "', '" + mcp.getName() + "', '" + plugin.getServer().getName() + "', '" + channel.getName() + "', '" + chat.replace("'", "''") + "', 'Chat');");
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		if(!bungee) {
			if(recipients.size() == 1) {
				if(!plugin.getConfig().getString("emptychannelalert", "&6No one is listening to you.").equals("")) {
					mcp.getPlayer().sendMessage(Format.FormatStringAll(plugin.getConfig().getString("emptychannelalert", "&6No one is listening to you.")));	
				}
			}
			for(Player p : recipients) {
				String json = Format.formatModerationGUI(globalJSON, p, mcp.getName(), channel.getName(), hash);
				WrappedChatComponent chatComponent = WrappedChatComponent.fromJson(json);
				PacketContainer packet = Format.createPacketPlayOutChat(chatComponent);
				Format.sendPacketPlayOutChat(p, packet);
			}
			Bukkit.getConsoleSender().sendMessage(consoleChat);
			return;
		}
		else {
			ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(byteOutStream);
			try {
				out.writeUTF("Chat");
				out.writeUTF(channel.getName());
				out.writeUTF(mcp.getName());
				out.writeUTF(mcp.getUUID().toString());
				out.writeBoolean(mcp.getBungeeToggle());
				out.writeInt(hash);
				out.writeUTF(consoleChat);
				if(plugin.getConfig().getString("loglevel", "info").equals("debug")) {
					System.out.println(out.size() + " size bytes without json");
				}
				out.writeUTF(globalJSON);
				if(plugin.getConfig().getString("loglevel", "info").equals("debug")) {
					System.out.println(out.size() + " bytes size with json");
				}
				mcp.getPlayer().sendPluginMessage(plugin, MineverseChat.PLUGIN_MESSAGING_CHANNEL, byteOutStream.toByteArray());
				
				out.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return;
		}
	}
}