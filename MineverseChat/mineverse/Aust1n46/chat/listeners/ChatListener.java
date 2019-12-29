package mineverse.Aust1n46.chat.listeners;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.messaging.PluginMessageRecipient;
import org.bukkit.util.EulerAngle;

import com.massivecraft.factions.entity.MPlayer;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.IllegalFormatException;

import mineverse.Aust1n46.chat.ChatMessage;
import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.api.events.ChannelJoinEvent;
import mineverse.Aust1n46.chat.api.events.ChatMessageEvent;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.channel.ChatChannelInfo;
import mineverse.Aust1n46.chat.database.DatabaseSender;
import mineverse.Aust1n46.chat.irc.Bot;
import mineverse.Aust1n46.chat.utilities.Format;
import mineverse.Aust1n46.chat.utilities.FormatTags;
import mineverse.Aust1n46.chat.versions.VersionHandler;

//This class listens to chat through the chat event and handles the bulk of the chat channels and formatting.
@SuppressWarnings("unused")
public class ChatListener implements Listener {
	private MineverseChat plugin;
	private ChatChannelInfo cc;
	private Bot bot;

	public ChatListener(MineverseChat plugin) {
		this.plugin = plugin;
	}

	public ChatListener(MineverseChat plugin, ChatChannelInfo cc, Bot bot) {
		this.plugin = plugin;
		this.cc = cc;
		this.bot = bot;
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
		boolean bungee = false;
		String evMessage;
		if(event.isCancelled()) {
			return;
		}
		MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer(event.getPlayer());
		ChatChannel eventChannel = mcp.getCurrentChannel();
		if(mcp.isEditing()) {
			mcp.getPlayer().sendMessage(Format.FormatStringAll(event.getMessage()));
			mcp.setEditing(false);
			event.setCancelled(true);
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
				String filtered = event.getMessage();
				String echo = "";
				String send = "";
				String spy = "";
				if(mcp.hasFilter()) {
					filtered = cc.FilterChat(event.getMessage());
				}
				if(mcp.getPlayer().hasPermission("venturechat.color")) {
					//filtered = Format.FormatStringColor(filtered);
				}
				if(mcp.getPlayer().hasPermission("venturechat.format")) {
					//filtered = Format.FormatString(filtered);
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
					DatabaseSender.writeToMySQL("ChatTime", "UUID", "Name", "Server", "Channel", "Text", "Type", date, mcp.getUUID().toString(), mcp.getName(), plugin.getServer().getName(), "Messaging_Component", event.getMessage().replace("'", "''"), "Chat");
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
						String filtered = event.getMessage();
						if(mcp.hasFilter()) {
							filtered = cc.FilterChat(event.getMessage());
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
						statement.executeUpdate("INSERT INTO `VentureChat` (`ChatTime`, `UUID`, `Name`, `Server`, `Channel`, `Text`, `Type`) VALUES ('" + date + "', '" + mcp.getUUID().toString() + "', '" + mcp.getName() + "', '" + plugin.getServer().getName() + "', 'Party_Component', '" + event.getMessage().replace("'", "''") + "', 'Chat');");
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

		if(event.getMessage().startsWith("@")) {
			for(MineverseChatPlayer p : MineverseChat.onlinePlayers) {
				if(p.isOnline() && event.getMessage().startsWith("@" + p.getPlayer().getDisplayName().replace("§r", ""))) {
					int add = 0;
					if(p.getPlayer().getDisplayName().contains("§r")) add = 2;
					String format = event.getMessage().substring(p.getPlayer().getDisplayName().length() + 1 - add);
					if(event.getMessage().length() <= p.getPlayer().getDisplayName().length() + 1 - add) {
						mcp.getPlayer().sendMessage(ChatColor.RED + "You must include a message.");
						event.setCancelled(true);
						return;
					}
					PluginManager pluginManager = plugin.getServer().getPluginManager();
					if(!mcp.getPlayer().canSee(p.getPlayer())) {
						break;
					}
					if(p.getIgnores().contains(mcp.getUUID())) {
						mcp.getPlayer().sendMessage(ChatColor.GOLD + p.getName() + " is currently ignoring your messages.");
						event.setCancelled(true);
						return;
					}
					if(!p.getMessageToggle()) {
						mcp.getPlayer().sendMessage(ChatColor.GOLD + p.getName() + " is currently blocking messages.");
						event.setCancelled(true);
						return;
					}
					String echo = "";
					String send = "";
					String spy = "";
					if(p.isAFK()) {
						mcp.getPlayer().sendMessage(ChatColor.GOLD + p.getPlayer().getDisplayName() + " is currently afk and might be unable to chat at this time.");
					}
					if(mcp.hasFilter()) {
						format = cc.FilterChat(format);
					}
					if(mcp.getPlayer().hasPermission("venturechat.color")) {
						format = Format.FormatStringColor(format);
					}
					if(mcp.getPlayer().hasPermission("venturechat.format")) {
						format = Format.FormatString(format);
					}
					if(plugin.getConfig().getString("tellformatto").equalsIgnoreCase("Default")) {
						echo = "You message " + p.getPlayer().getDisplayName() + ":" + ChatColor.valueOf(cc.tellColor.toUpperCase()) + format;
					}
					else {
						echo = Format.FormatStringAll(plugin.getConfig().getString("tellformatto").replace("{playerto}", p.getPlayer().getDisplayName()).replace("{playerfrom}", mcp.getPlayer().getDisplayName())) + format;
					}
					if(plugin.getConfig().getString("tellformatfrom").equalsIgnoreCase("Default")) {
						send = mcp.getPlayer().getDisplayName() + " messages you:" + ChatColor.valueOf(cc.tellColor.toUpperCase()) + format;
					}
					else {
						send = Format.FormatStringAll(plugin.getConfig().getString("tellformatfrom").replace("{playerto}", p.getPlayer().getDisplayName()).replace("{playerfrom}", mcp.getPlayer().getDisplayName())) + format;
					}
					if(plugin.getConfig().getString("tellformatspy").equalsIgnoreCase("Default")) {
						spy = p.getName() + " messages " + p.getName() + ":" + ChatColor.valueOf(cc.tellColor.toUpperCase()) + format;
					}
					else {
						spy = Format.FormatStringAll(plugin.getConfig().getString("tellformatspy").replace("{playerto}", p.getName()).replace("{playerfrom}", mcp.getName())) + format;
					}
					for(MineverseChatPlayer sp : MineverseChat.onlinePlayers) {
						if(sp.isSpy()) {
							sp.getPlayer().sendMessage(spy);
						}
					}
					p.getPlayer().sendMessage(send);
					mcp.getPlayer().sendMessage(echo);
					if(p.hasNotifications()) {
						if(VersionHandler.is1_8() || VersionHandler.is1_7_10() || VersionHandler.is1_7_2() || VersionHandler.is1_7_9()) {
							p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.valueOf("LEVEL_UP"), 1, 0);
						}
						else {
							p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1, 0);
						}
						p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1, 0);
					}
					p.setReplyPlayer(mcp.getUUID());
					mcp.setReplyPlayer(p.getUUID());
					Bukkit.getConsoleSender().sendMessage(mcp.getName() + " messages " + p.getName() + ":" + ChatColor.valueOf(cc.tellColor.toUpperCase()) + format);
					event.setCancelled(true);
					return;
				}
				if(event.getMessage().startsWith("@" + p.getName())) {
					String format = event.getMessage().substring(p.getName().length() + 1);
					if(event.getMessage().length() <= p.getName().length() + 1) {
						mcp.getPlayer().sendMessage(ChatColor.RED + "You must include a message.");
						event.setCancelled(true);
						return;
					}
					if(!mcp.getPlayer().canSee(p.getPlayer())) {
						break;
					}
					if(p.getIgnores().contains(mcp.getUUID())) {
						mcp.getPlayer().sendMessage(ChatColor.GOLD + p.getName() + " is currently ignoring your messages.");
						event.setCancelled(true);
						return;
					}
					if(!p.getMessageToggle()) {
						mcp.getPlayer().sendMessage(ChatColor.GOLD + p.getName() + " is currently blocking messages.");
						event.setCancelled(true);
						return;
					}
					String echo = "";
					String send = "";
					String spy = "";
					if(p.isAFK()) {
						mcp.getPlayer().sendMessage(ChatColor.GOLD + p.getPlayer().getDisplayName() + " is currently afk and might be unable to chat at this time.");
					}
					if(mcp.hasFilter()) {
						format = cc.FilterChat(format);
					}
					if(mcp.getPlayer().hasPermission("venturechat.color")) {
						format = Format.FormatStringColor(format);
					}
					if(mcp.getPlayer().hasPermission("venturechat.format")) {
						format = Format.FormatString(format);
					}
					if(plugin.getConfig().getString("tellformatto").equalsIgnoreCase("Default")) {
						echo = "You message " + p.getNickname() + ":" + ChatColor.valueOf(cc.tellColor.toUpperCase()) + format;
					}
					else {
						echo = Format.FormatStringAll(plugin.getConfig().getString("tellformatto").replace("{playerto}", p.getPlayer().getDisplayName()).replace("{playerfrom}", mcp.getPlayer().getDisplayName())) + format;
					}
					if(plugin.getConfig().getString("tellformatfrom").equalsIgnoreCase("Default")) {
						send = mcp.getNickname() + " messages you:" + ChatColor.valueOf(cc.tellColor.toUpperCase()) + format;
					}
					else {
						send = Format.FormatStringAll(plugin.getConfig().getString("tellformatfrom").replace("{playerto}", p.getPlayer().getDisplayName()).replace("{playerfrom}", mcp.getPlayer().getDisplayName())) + format;
					}
					if(plugin.getConfig().getString("tellformatspy").equalsIgnoreCase("Default")) {
						spy = mcp.getName() + " messages " + p.getName() + ":" + ChatColor.valueOf(cc.tellColor.toUpperCase()) + format;
					}
					else {
						spy = Format.FormatStringAll(plugin.getConfig().getString("tellformatspy").replace("{playerto}", p.getName()).replace("{playerfrom}", mcp.getName())) + format;
					}
					for(MineverseChatPlayer sp : MineverseChat.onlinePlayers) {
						if(sp.isSpy()) {
							sp.getPlayer().sendMessage(spy);
						}
					}
					p.getPlayer().sendMessage(send);
					mcp.getPlayer().sendMessage(echo);
					if(p.hasNotifications()) {
						if(VersionHandler.is1_8()) {
							p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.valueOf("LEVEL_UP"), 1, 0);
						}
						if(VersionHandler.is1_9()) {
							p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1, 0);
						}
					}
					p.setReplyPlayer(mcp.getUUID());
					mcp.setReplyPlayer(p.getUUID());
					Bukkit.getConsoleSender().sendMessage(mcp.getName() + " messages " + p.getName() + ":" + ChatColor.valueOf(cc.tellColor.toUpperCase()) + format);
					event.setCancelled(true);
					return;
				}
			}
		}
		evMessage = event.getMessage();
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
		String Channelformat;
		boolean irc = false;
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
		
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("dd:HH:mm:ss");
		String date = formatter.format(currentDate.getTime());
		//String[] datearray = date.split(":");
		//int time = (Integer.parseInt(datearray[0]) * 86400) + (Integer.parseInt(datearray[1]) * 3600) + (Integer.parseInt(datearray[2]) * 60) + (Integer.parseInt(datearray[3]));
		//int datetime = (Integer.parseInt(datearray[0]) * 1440) + (Integer.parseInt(datearray[1]) * 60) + (Integer.parseInt(datearray[2]));
		
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
			if(eventChannel.hasCooldown() && !event.isCancelled()) {
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
		if(eventChannel.isIRC()) {
			irc = true;
		}
		if(plugin.getConfig().getConfigurationSection("channels." + eventChannel.getName()).getString("format").equalsIgnoreCase("Default")) {
			if(curColor.equalsIgnoreCase("None")) {
				Channelformat = FormatTags.ChatFormat(ChatColor.valueOf(eventChannel.getColor().toUpperCase()) + "[" + eventChannel.getName() + "] {prefix}{name}" + ChatColor.valueOf(eventChannel.getColor().toUpperCase()) + ":", mcp.getPlayer(), plugin, cc, eventChannel, plugin.getConfig().getBoolean("jsonFormat"));
			}
			else {
				Channelformat = FormatTags.ChatFormat(ChatColor.valueOf(eventChannel.getColor().toUpperCase()) + "[" + eventChannel.getName() + "] {prefix}{name}" + ChatColor.valueOf(eventChannel.getColor().toUpperCase()) + ":" + ChatColor.valueOf(eventChannel.getChatColor().toUpperCase()), mcp.getPlayer(), plugin, cc, eventChannel, plugin.getConfig().getBoolean("jsonFormat"));
			}
		}
		else {
			Channelformat = FormatTags.ChatFormat(plugin.getConfig().getConfigurationSection("channels." + eventChannel.getName()).getString("format"), mcp.getPlayer(), plugin, cc, eventChannel, plugin.getConfig().getBoolean("jsonFormat"));
			if(plugin.getConfig().getBoolean("formatcleaner", false)) {
				Channelformat = Channelformat.replace("[]", " ");
				Channelformat = Channelformat.replace("    ", " ").replace("   ", " ").replace("  ", " ");
			}
		}
		filterthis = eventChannel.isFiltered();
		if(filterthis) {
			if(mcp.hasFilter()) {
				evMessage = cc.FilterChat(evMessage);
			}
		}
		Player[] pl = event.getRecipients().toArray(new Player[0]);
		PluginManager pluginManager = plugin.getServer().getPluginManager();
		int recipientSize = event.getRecipients().size();
		for(MineverseChatPlayer p : MineverseChat.onlinePlayers) {
			if(p.getPlayer() != mcp.getPlayer()) {
				if(!p.getListening().contains(eventChannel.getName())) {
					event.getRecipients().remove(p.getPlayer());
					recipientSize--;
					continue;
				}
				if(plugin.getConfig().getBoolean("ignorechat", false) && p.getIgnores().contains(mcp.getUUID())) {
					event.getRecipients().remove(p.getPlayer());
					recipientSize--;
					continue;
				}
				if(plugin.getConfig().getBoolean("enable_towny_channel") && pluginManager.isPluginEnabled("Towny")) {
					try {
						Resident r = TownyUniverse.getDataSource().getResident(p.getName());
						Resident pp = TownyUniverse.getDataSource().getResident(mcp.getName());
						if(eventChannel.getName().equalsIgnoreCase("Town")) {
							if(!pp.hasTown()) {
								event.getRecipients().remove(p.getPlayer());
								recipientSize--;
								continue;
							}
							else if(!r.hasTown()) {
								event.getRecipients().remove(p.getPlayer());
								recipientSize--;
								continue;
							}
							else if(!(r.getTown().getName().equals(pp.getTown().getName()))) {
								event.getRecipients().remove(p.getPlayer());
								recipientSize--;
								continue;
							}
						}
						if(eventChannel.getName().equalsIgnoreCase("Nation")) {
							if(!pp.hasNation()) {
								event.getRecipients().remove(p.getPlayer());
								recipientSize--;
								continue;
							}
							else if(!r.hasNation()) {
								event.getRecipients().remove(p.getPlayer());
								recipientSize--;
								continue;
							}
							else if(!(r.getTown().getNation().getName().equals(pp.getTown().getNation().getName()))) {
								event.getRecipients().remove(p.getPlayer());
								recipientSize--;
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
								event.getRecipients().remove(p.getPlayer());
							}
							else if(!mplayerp.hasFaction()) {
								event.getRecipients().remove(p.getPlayer());
							}
							else if(!(mplayer.getFactionName().equals(mplayerp.getFactionName()))) {
								event.getRecipients().remove(p.getPlayer());
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
							event.getRecipients().remove(p.getPlayer());
							recipientSize--;
							continue;
						}
						if(!mcp.getPlayer().canSee(p.getPlayer())) {
							recipientSize--;
							continue;
						}
					}
					else {
						event.getRecipients().remove(p.getPlayer());
						recipientSize--;
						continue;
					}
				}
				if(!mcp.getPlayer().canSee(p.getPlayer())) {
					recipientSize--;
					continue;
				}
			}
		}
		if(recipientSize == 1 && !bungee && !event.isCancelled()) {
			if(!plugin.getConfig().getString("emptychannelalert", "&6No one is listening to you.").equals("")) 
				mcp.getPlayer().sendMessage(Format.FormatStringAll(plugin.getConfig().getString("emptychannelalert", "&6No one is listening to you.")));	
		}
		try {
			if(mcp.getPlayer().hasPermission("venturechat.color")) {
				evMessage = Format.FormatStringColor(evMessage);
			}
			if(mcp.getPlayer().hasPermission("venturechat.format")) {
				evMessage = Format.FormatString(evMessage);
			}
			if(!mcp.isQuickChat()) {
				evMessage = " " + evMessage;
			}
			if(curColor.equalsIgnoreCase("None")) {
				event.setMessage(evMessage);
			}
			else {
				event.setMessage(ChatColor.valueOf(curColor) + evMessage);
			}
			mcp.setQuickChat(false);
			String message = String.format(Channelformat + "%s", new Object[] { event.getMessage() });
			event.setFormat(message.replace("%", "%%"));
			message = message.replaceAll("(§([a-z0-9]))", "");
			String format = Channelformat;
			String chat = event.getMessage();
			if(curColor.equalsIgnoreCase("None")) {
				chat = Format.getLastCode(format) + chat;
				event.setMessage(chat);
			}
			MineverseChat.lastChatMessage = new ChatMessage(mcp.getPlayer().getName(), message, message.hashCode(), format, chat, eventChannel.getName());
			MineverseChat.lastJson = Format.convertToJson(MineverseChat.lastChatMessage);
			
			
			/* Temp disabled for 1.14 
			 * ChatMessageEvent chatMessageEvent = new ChatMessageEvent(mcp, eventChannel, bungee, MineverseChat.lastChatMessage, MineverseChat.lastJson);
			Bukkit.getServer().getPluginManager().callEvent(chatMessageEvent);
			*/
			
			
			if(irc && plugin.irc) {
				if(bot.bot.isConnected()) {
					bot.bot.getUserChannelDao().getChannel(bot.channel).send().message(mcp.getName() + ":" + evMessage);
				}
			}
			if(plugin.mysql) {
				Statement statement;
				currentDate = Calendar.getInstance();
				formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				date = formatter.format(currentDate.getTime());
				try {
					statement = plugin.c.createStatement();
					statement.executeUpdate("INSERT INTO `VentureChat` (`ChatTime`, `UUID`, `Name`, `Server`, `Channel`, `Text`, `Type`) VALUES ('" + date + "', '" + mcp.getUUID().toString() + "', '" + mcp.getName() + "', '" + plugin.getServer().getName() + "', '" + eventChannel.getName() + "', '" + event.getMessage().replace("'", "''") + "', 'Chat');");
				}
				catch(SQLException e) {
					e.printStackTrace();
				}
			}
			if(bungee && !event.isCancelled()) {
				message = String.format(Channelformat + event.getMessage(), new Object[] { event.getPlayer().getDisplayName(), event.getMessage() }).replaceAll("(§([a-z0-9]))", "");
				format = String.format(Channelformat, new Object[] { event.getPlayer().getDisplayName() });
				chat = event.getMessage();
				//MineverseChat.lastChatMessage = new ChatMessage(mcp.getPlayer().getName(), message, message.hashCode(), format, chat, eventChannel.getName());
				//MineverseChat.lastJson = Format.convertToJson(MineverseChat.lastChatMessage);
				event.setCancelled(true);
				ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(byteOutStream);
				try {
					out.writeUTF("Chat");
					out.writeUTF(eventChannel.getName());
					out.writeUTF(Channelformat + event.getMessage());
					out.writeUTF(mcp.getName());
					out.writeBoolean(mcp.getBungeeToggle());
					out.writeUTF(message);
					out.writeUTF(format);
					out.writeUTF(chat);
					if(plugin.getConfig().getString("loglevel", "info").equals("debug")) {
						System.out.println(out.size() + " size bytes without json");
					}
					out.writeUTF(MineverseChat.lastJson);
					if(plugin.getConfig().getString("loglevel", "info").equals("debug")) {
						System.out.println(out.size() + " bytes size with json");
					}
					mcp.getPlayer().sendPluginMessage(plugin, MineverseChat.PLUGIN_MESSAGING_CHANNEL, byteOutStream.toByteArray());
					
					//PluginMessageRecipient test = (PluginMessageRecipient) mcp.getPlayer();
					//System.out.println("Listening plugin channels?");
					//System.out.println(test.getListeningPluginChannels());
					//System.out.println("PluginMessageRecipient methods?");
					//for(Method m : test.getClass().getMethods()) {
						//System.out.println(m.getName());
					//}
					out.close();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				
			}
		}
		catch(IllegalFormatException ex) {
			// plugin.getLogger().log(Level.INFO,
			// "Message Format issue: {0}:{1}", new Object[] { ex.getMessage(),
			// evMessage });
			event.setMessage(Channelformat + evMessage);
		}
	}
}