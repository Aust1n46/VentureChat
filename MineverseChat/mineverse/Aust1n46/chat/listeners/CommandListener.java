package mineverse.Aust1n46.chat.listeners;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.alias.Alias;
import mineverse.Aust1n46.chat.alias.AliasInfo;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.channel.ChatChannelInfo;
import mineverse.Aust1n46.chat.gui.GuiSlot;
import mineverse.Aust1n46.chat.irc.Bot;
import mineverse.Aust1n46.chat.utilities.Format;
import mineverse.Aust1n46.chat.utilities.FormatTags;

//import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
//import org.bukkit.plugin.Plugin;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import me.clip.placeholderapi.PlaceholderAPI;

//This class listens for commands (Any chat that begins with a /) to use in the command spy and
//in the custom commands such as aliases.
public class CommandListener implements CommandExecutor, Listener {
	private MineverseChat plugin;
	private ChatChannelInfo cc;
	private AliasInfo aa;
	private Bot bot;

	public CommandListener(MineverseChat plugin, ChatChannelInfo cc, AliasInfo aa, Bot bot) {
		this.plugin = plugin;
		this.cc = cc;
		this.aa = aa;
		this.bot = bot;
	}

	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) throws FileNotFoundException {
		ConfigurationSection cs = plugin.getConfig().getConfigurationSection("commandspy");
		Boolean wec = cs.getBoolean("worldeditcommands", true);
		MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer(event.getPlayer());
		for(MineverseChatPlayer p : MineverseChat.players) {
			if(p.hasCommandSpy() && p.isOnline()) {
				if(wec) {
					p.getPlayer().sendMessage(ChatColor.GOLD + mcp.getName() + ": " + event.getMessage());
				}
				else {
					if(!(event.getMessage().toLowerCase().startsWith("//"))) {
						p.getPlayer().sendMessage(ChatColor.GOLD + mcp.getName() + ": " + event.getMessage());
					}
				}
			}
		}

		if(!event.getMessage().startsWith("/afk")) {
			if(mcp.isAFK()) {
				mcp.setAFK(false);
				mcp.getPlayer().sendMessage(ChatColor.GOLD + "You are no longer AFK.");
				if(plugin.getConfig().getBoolean("broadcastafk")) {
					for(MineverseChatPlayer p : MineverseChat.players) {
						if(p.isOnline() && mcp.getName() != p.getName()) {
							p.getPlayer().sendMessage(ChatColor.GOLD + mcp.getName() + " is no longer AFK.");
						}
					}
				}
			}
		}

		String[] blocked = event.getMessage().split(" ");
		if(mcp.getBlockedCommands().contains(blocked[0])) {
			mcp.getPlayer().sendMessage(ChatColor.RED + "You are blocked from entering this command: " + event.getMessage());
			event.setCancelled(true);
			return;
		}

		String message = event.getMessage();
		/*
		 * boolean cus = false; if((message.startsWith("/pl") ||
		 * message.startsWith("/plugins")) &&
		 * plugin.getConfig().getBoolean("modifypluginlist", true)) {
		 * if(message.contains(" ")) { if(message.split(" ")[0].equals("/pl") ||
		 * message.split(" ")[0].equals("/plugins")) { cus = true; } }
		 * if(message.equals("/pl") || message.equals("/plugins")) { cus = true;
		 * } if(cus && mcp.getPlayer().hasPermission("bukkit.command.plugins"))
		 * { String pluginlist = ""; for(Plugin p :
		 * Bukkit.getPluginManager().getPlugins()) { pluginlist +=
		 * ChatColor.GREEN + p.getName().replace("VentureChat",
		 * plugin.getConfig().getString("pluginname", "VentureChat")) +
		 * ChatColor.WHITE + ", "; } if(pluginlist.length() > 2) { pluginlist =
		 * pluginlist.substring(0, pluginlist.length() - 2); }
		 * mcp.getPlayer().sendMessage("Plugins (" +
		 * Bukkit.getPluginManager().getPlugins().length + "): " + pluginlist);
		 * event.setCancelled(true); return; } }
		 */

		if(plugin.mysql) {
			Statement statement;
			Calendar currentDate = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = formatter.format(currentDate.getTime());
			try {
				statement = plugin.c.createStatement();
				statement.executeUpdate("INSERT INTO `VentureChat` (`ChatTime`, `UUID`, `Name`, `Server`, `Channel`, `Text`, `Type`) VALUES ('" + date + "', '" + mcp.getUUID().toString() + "', '" + mcp.getName() + "', '" + plugin.getServer().getServerName() + "', 'Command_Component', '" + event.getMessage().replace("'", "''") + "', 'Command');");
			}
			catch(SQLException error) {
				error.printStackTrace();
			}
		}

		for(Alias a : aa.getAliases()) {
			if(message.toLowerCase().substring(1).split(" ")[0].equals(a.getName().toLowerCase())) {
				for(String s : a.getComponents()) {
					if(!mcp.getPlayer().hasPermission(a.getPermission()) && a.hasPermission()) {
						mcp.getPlayer().sendMessage(ChatColor.RED + "You do not have permission for this alias.");
						event.setCancelled(true);
						return;
					}
					int num = 1;
					if(message.length() < a.getName().length() + 2 || a.getArguments() == 0) num = 0;
					int arg = 0;
					if(message.substring(a.getName().length() + 1 + num).length() == 0) arg = 1;
					String[] args = message.substring(a.getName().length() + 1 + num).split(" ");
					String send = "";
					if(args.length - arg < a.getArguments()) {
						String keyword = "arguments.";
						if(a.getArguments() == 1) keyword = "argument.";
						mcp.getPlayer().sendMessage(ChatColor.RED + "Invalid arguments for this alias, enter at least " + a.getArguments() + " " + keyword);
						event.setCancelled(true);
						return;
					}
					for(int b = 0; b < args.length; b++) {
						send += " " + args[b];
					}
					if(send.length() > 0) send = send.substring(1);
					s = Format.FormatStringAll(s);
					if(mcp.getPlayer().hasPermission("venturechat.color")) send = Format.FormatStringColor(send);
					if(mcp.getPlayer().hasPermission("venturechat.format")) send = Format.FormatString(send);
					if(s.startsWith("Command:")) {
						mcp.getPlayer().chat(s.substring(9).replace("$", send));
						event.setCancelled(true);
					}
					if(s.startsWith("Message:")) {
						mcp.getPlayer().sendMessage(s.substring(9).replace("$", send));
						event.setCancelled(true);
					}
					if(s.startsWith("Broadcast:")) {
						plugin.getServer().broadcastMessage(s.substring(11).replace("$", send));
						event.setCancelled(true);
					}
				}
			}
		}

		if(!plugin.quickchat) {
			for(ChatChannel channel : cc.getChannelsInfo()) {
				if(!channel.hasPermission() || mcp.getPlayer().hasPermission(channel.getPermission())) {
					if(message.equals("/" + channel.getAlias())) {
						mcp.getPlayer().sendMessage("Channel Set: " + ChatColor.valueOf(channel.getColor().toUpperCase()) + "[" + channel.getName() + "]");
						if(mcp.hasConversation()) {
							for(MineverseChatPlayer p : MineverseChat.onlinePlayers) {
								if(p.isSpy()) {
									p.getPlayer().sendMessage(mcp.getName() + " is no longer in a private conversation with " + MineverseChatAPI.getMineverseChatPlayer(mcp.getConversation()).getName() + ".");
								}
							}
							mcp.getPlayer().sendMessage("You are no longer in private conversation with " + MineverseChatAPI.getMineverseChatPlayer(mcp.getConversation()).getName() + ".");
							mcp.setConversation(null);
						}
						mcp.addListening(channel.getName());
						mcp.setCurrentChannel(channel);
						event.setCancelled(true);
						return;
					}
					if(message.toLowerCase().startsWith("/" + channel.getAlias() + " ")) {
						message = message.substring(channel.getAlias().length() + 1);
						mcp.addListening(channel.getName());
						mcp.setQuickChannel(channel);
						String format = "";
						if(plugin.getConfig().getConfigurationSection("channels." + channel.getName()).getString("format").equalsIgnoreCase("Default")) {
							format = FormatTags.ChatFormat(ChatColor.valueOf(channel.getColor().toUpperCase()) + "[" + channel.getName() + "] {prefix}{name}" + ChatColor.valueOf(channel.getColor().toUpperCase()) + ":" + ChatColor.valueOf(channel.getChatColor().toUpperCase()), mcp.getPlayer(), plugin, cc, channel, plugin.getConfig().getBoolean("jsonFormat"));
						}
						else {
							format = FormatTags.ChatFormat(plugin.getConfig().getConfigurationSection("channels." + channel.getName()).getString("format"), mcp.getPlayer(), plugin, cc, channel, plugin.getConfig().getBoolean("jsonFormat"));
							if(plugin.getConfig().getBoolean("formatcleaner", false)) {
								format = format.replace("[]", " ");
								format = format.replace("    ", " ").replace("   ", " ").replace("  ", " ");
							}
						}
						mcp.setQuickChat(true);
						mcp.getPlayer().chat(message);
						event.setCancelled(true);
					}
				}
			}
		}
	}

	//old 1.8 command map
	@EventHandler
	public void onServerCommand(ServerCommandEvent event) {
		if(plugin.irc && bot.bot.isConnected() && !event.getCommand().equalsIgnoreCase("say ") && event.getCommand().toLowerCase().startsWith("say ")) {
			bot.bot.getUserChannelDao().getChannel(bot.channel).send().message("[Server] " + event.getCommand().substring(4));
		}
		if(plugin.mysql) {
			Statement statement;
			Calendar currentDate = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = formatter.format(currentDate.getTime());
			try {
				statement = plugin.c.createStatement();
				statement.executeUpdate("INSERT INTO `VentureChat` (`ChatTime`, `UUID`, `Name`, `Server`, `Channel`, `Text`, `Type`) VALUES ('" + date + "', 'N/A', 'Console', '" + plugin.getServer().getServerName() + "', 'Command_Component', '" + event.getCommand().replace("'", "''") + "', 'Command');");
			}
			catch(SQLException error) {
				error.printStackTrace();
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "This command must be run by a player.");
			return true;
		}
		MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer((Player) sender);
		for(ChatChannel channel : cc.getChannelsInfo()) {
			if(command.getName().toLowerCase().equals(channel.getAlias())) {
				if(args.length == 0) {
					mcp.getPlayer().sendMessage(ChatColor.RED + "Invalid command: /" + channel.getAlias() + " message");
					return true;
				}
				mcp.setQuickChat(true);
				mcp.setQuickChannel(channel);
				mcp.addListening(channel.getName());
				String msg = "";
				for(int x = 0; x < args.length; x++) {
					if(args[x].length() > 0) msg += " " + args[x];
				}
				mcp.getPlayer().chat(msg);
				return true;
			}
		}
		return false;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void InventoryClick(InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		if(item == null || !e.getInventory().getTitle().contains("VentureChat")) {
			return;
		}
		e.setCancelled(true);
		MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer((Player) e.getWhoClicked());
		MineverseChatPlayer target = MineverseChatAPI.getMineverseChatPlayer(e.getInventory().getTitle().replace(" GUI", "").replace("VentureChat: ", ""));
		ItemStack skull = e.getInventory().getItem(0);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		ChatChannel channel = MineverseChat.ccInfo.getChannelInfo(ChatColor.stripColor(skullMeta.getLore().get(0)).replace("Channel: ", ""));
		int hash = Integer.parseInt(ChatColor.stripColor(skullMeta.getLore().get(1).replace("Hash: ", "")));
		if(item.getType() == Material.BARRIER) {
			mcp.getPlayer().closeInventory();
		}
		for(GuiSlot g : MineverseChat.gsInfo.getGuiSlots()) {
			if(g.getIcon() == item.getType() && g.getDurability() == item.getDurability() && g.getSlot() == e.getSlot()) {
				String command = g.getCommand().replace("{channel}", channel.getName()).replace("{hash}", hash + "").replace("{player_name}", target.getName());
				if(target.isOnline()) {
					command = PlaceholderAPI.setBracketPlaceholders(target.getPlayer(), command);
				}
				mcp.getPlayer().chat(command);
			}
		}
	}
}