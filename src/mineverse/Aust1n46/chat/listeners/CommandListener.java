package mineverse.Aust1n46.chat.listeners;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.alias.Alias;
import mineverse.Aust1n46.chat.alias.AliasInfo;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.gui.GuiSlot;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;
import mineverse.Aust1n46.chat.utilities.Format;
import mineverse.Aust1n46.chat.versions.VersionHandler;

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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import me.clip.placeholderapi.PlaceholderAPI;

//This class listens for commands (Any chat that begins with a /) to use in the command spy and
//in the custom commands such as aliases.
public class CommandListener implements CommandExecutor, Listener {
	private MineverseChat plugin = MineverseChat.getInstance();
	private AliasInfo aa;

	public CommandListener(AliasInfo aa) {
		this.aa = aa;
	}

	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) throws FileNotFoundException {
		ConfigurationSection cs = plugin.getConfig().getConfigurationSection("commandspy");
		Boolean wec = cs.getBoolean("worldeditcommands", true);
		MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer(event.getPlayer());
		for(MineverseChatPlayer p : MineverseChatAPI.getOnlineMineverseChatPlayers()) {
			if(p.hasCommandSpy()) {
				if(wec) {
					p.getPlayer().sendMessage(Format.FormatStringAll(cs.getString("format").replace("{player}", mcp.getName()).replace("{command}", event.getMessage())));
				}
				else {
					if(!(event.getMessage().toLowerCase().startsWith("//"))) {
						p.getPlayer().sendMessage(Format.FormatStringAll(cs.getString("format").replace("{player}", mcp.getName()).replace("{command}", event.getMessage())));
					}
					else {
						if(!(event.getMessage().toLowerCase().startsWith("//"))) {
							p.getPlayer().sendMessage(ChatColor.GOLD + mcp.getName() + ": " + event.getMessage());
						}
					}
				}
			}
		}

		String[] blocked = event.getMessage().split(" ");
		if(mcp.getBlockedCommands().contains(blocked[0])) {
			mcp.getPlayer().sendMessage(LocalizedMessage.BLOCKED_COMMAND.toString()
					.replace("{command}", event.getMessage()));
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

		if(plugin.db != null) {
			Calendar currentDate = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = formatter.format(currentDate.getTime());
			plugin.db.writeVentureChat(date, mcp.getUUID().toString(), mcp.getName(), "Local", "Command_Component", event.getMessage().replace("'", "''"), "Command");
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
					if(mcp.getPlayer().hasPermission("venturechat.color.legacy")) {
						send = Format.FormatStringLegacyColor(send);
					}
					if(mcp.getPlayer().hasPermission("venturechat.color")) {
						send = Format.FormatStringColor(send);
					}
					if(mcp.getPlayer().hasPermission("venturechat.format")) {
						send = Format.FormatString(send);
					}
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
			for(ChatChannel channel : ChatChannel.getChannels()) {
				if(!channel.hasPermission() || mcp.getPlayer().hasPermission(channel.getPermission())) {
					if(message.equals("/" + channel.getAlias())) {
						mcp.getPlayer().sendMessage(LocalizedMessage.SET_CHANNEL.toString()
								.replace("{channel_color}", channel.getColor() + "")
								.replace("{channel_name}", channel.getName()));
						if(mcp.hasConversation()) {
							for(MineverseChatPlayer p : MineverseChatAPI.getOnlineMineverseChatPlayers()) {
								if(p.isSpy()) {
									p.getPlayer().sendMessage(LocalizedMessage.EXIT_PRIVATE_CONVERSATION_SPY.toString()
											.replace("{player_sender}", mcp.getName())
											.replace("{player_receiver}", MineverseChatAPI.getMineverseChatPlayer(mcp.getConversation()).getName()));
								}
							}
							mcp.getPlayer().sendMessage(LocalizedMessage.EXIT_PRIVATE_CONVERSATION.toString()
									.replace("{player_receiver}", MineverseChatAPI.getMineverseChatPlayer(mcp.getConversation()).getName()));
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
						/*String format = "";
						if(plugin.getConfig().getConfigurationSection("channels." + channel.getName()).getString("format").equalsIgnoreCase("Default")) {
							format = FormatTags.ChatFormat(ChatColor.valueOf(channel.getColor().toUpperCase()) + "[" + channel.getName() + "] {prefix}{name}" + ChatColor.valueOf(channel.getColor().toUpperCase()) + ":" + ChatColor.valueOf(channel.getChatColor().toUpperCase()), mcp.getPlayer(), plugin, cc, channel, plugin.getConfig().getBoolean("jsonFormat"));
						}
						else {
							format = FormatTags.ChatFormat(plugin.getConfig().getConfigurationSection("channels." + channel.getName()).getString("format"), mcp.getPlayer(), plugin, cc, channel, plugin.getConfig().getBoolean("jsonFormat"));
							if(plugin.getConfig().getBoolean("formatcleaner", false)) {
								format = format.replace("[]", " ");
								format = format.replace("    ", " ").replace("   ", " ").replace("  ", " ");
							}
						}*/
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
		if (plugin.db != null) {
			Calendar currentDate = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = formatter.format(currentDate.getTime());
			plugin.db.writeVentureChat(date, "N/A", "Console", "Local", "Command_Component", event.getCommand().replace("'", "''") , "Command");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "This command must be run by a player.");
			return true;
		}
		MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer((Player) sender);
		for(ChatChannel channel : ChatChannel.getChannels()) {
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

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW)
	public void InventoryClick(InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		if(item == null || !e.getView().getTitle().contains("VentureChat")) {
			return;
		}
		e.setCancelled(true);
		MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer((Player) e.getWhoClicked());
		String playerName = e.getView().getTitle().replace(" GUI", "").replace("VentureChat: ", "");
		MineverseChatPlayer target = MineverseChatAPI.getMineverseChatPlayer(playerName);
		ItemStack skull = e.getInventory().getItem(0);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		ChatChannel channel = ChatChannel.getChannel(ChatColor.stripColor(skullMeta.getLore().get(0)).replace("Channel: ", ""));
		int hash = Integer.parseInt(ChatColor.stripColor(skullMeta.getLore().get(1).replace("Hash: ", "")));
		if(VersionHandler.is1_7_10()) {
			if(item.getType() == Material.BEDROCK) {
				mcp.getPlayer().closeInventory();
			}
		}
		else {
			if(item.getType() == Material.BARRIER) {
				mcp.getPlayer().closeInventory();
			}
		}
		for(GuiSlot g : MineverseChat.gsInfo.getGuiSlots()) {
			if(g.getIcon() == item.getType() && g.getDurability() == item.getDurability() && g.getSlot() == e.getSlot()) {
				String command = g.getCommand().replace("{channel}", channel.getName()).replace("{hash}", hash + "");
				if(target != null) {
					command = command.replace("{player_name}", target.getName());
					if(target.isOnline()) {
						command = Format.FormatStringAll(PlaceholderAPI.setBracketPlaceholders(target.getPlayer(), command));
					}
				}
				else {
					command = command.replace("{player_name}", "Discord_Message");
				}
				mcp.getPlayer().chat(command);
			}
		}
	}
}