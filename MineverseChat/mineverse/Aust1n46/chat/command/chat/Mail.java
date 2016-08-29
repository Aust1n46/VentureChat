package mineverse.Aust1n46.chat.command.chat;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannelInfo;
import mineverse.Aust1n46.chat.command.MineverseCommand;

@SuppressWarnings("unused")
public class Mail extends MineverseCommand {
	private MineverseChat plugin;
	private ChatChannelInfo cc = MineverseChat.ccInfo;

	public Mail(String name) {
		super(name);
		this.plugin = MineverseChat.getInstance();
	}

	//@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		/*if(!(sender instanceof Player)) {
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "This command must be run by a player.");
			return;
		}
		MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer((Player) sender);
		if(mcp.getPlayer().hasPermission("venturechat.mail")) {
			try {
				switch(args[0]) {
				case "send": {
					try {
						Player p = Bukkit.getPlayer(args[1]);
						if(p == null) {
							OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
							if(op.hasPlayedBefore()) {
								if(plugin.mail.containsKey(op.getUniqueId().toString())) {
									try {
										String msg = args[2];
										for(int x = 3; x < args.length; x++) {
											if(args[x].length() > 0) msg += " " + args[x];
										}
										plugin.mail.get(op.getUniqueId().toString()).add(player.getName() + ": " + msg);
										player.sendMessage(ChatColor.GOLD + "Sent mail: " + ChatColor.RED + cc.FormatStringAll(msg) + ChatColor.GOLD + " to: " + ChatColor.RED + op.getName());
										return;
									}
									catch(Exception e) {
										player.sendMessage(ChatColor.RED + "Invalid arguments, /mail send [player] [message]");
										return;
									}
								}
								try {
									String msg = args[2];
									for(int x = 3; x < args.length; x++) {
										if(args[x].length() > 0) msg += " " + args[x];
									}
									ArrayList<String> newlist = new ArrayList<String>();
									newlist.add(player.getName() + ": " + msg);
									player.sendMessage(ChatColor.GOLD + "Sent mail: " + ChatColor.RED + cc.FormatStringAll(msg) + ChatColor.GOLD + " to: " + ChatColor.RED + op.getName());
									plugin.mail.put(op.getUniqueId().toString(), newlist);
									return;
								}
								catch(Exception e) {
									player.sendMessage(ChatColor.RED + "Invalid arguments, /mail send [player] [message]");
									return;
								}
							}
							if(args.length < 3) {
								player.sendMessage(ChatColor.RED + "Invalid arguments, /mail send [player] [message]");
								return;
							}
							player.sendMessage(ChatColor.RED + "Player: " + op.getName() + " has never played before.");
							return;
						}
						try {
							String msg = args[2];
							for(int x = 3; x < args.length; x++) {
								if(args[x].length() > 0) msg += " " + args[x];
							}
							p.setMetadata("MineverseChat.mail", new FixedMetadataValue(plugin, plugin.getMetadataString(p, "MineverseChat.mail", plugin) + player.getName() + ": " + msg + "\n"));
							player.sendMessage(ChatColor.GOLD + "Sent mail: " + ChatColor.RED + cc.FormatStringAll(msg) + ChatColor.GOLD + " to: " + ChatColor.RED + p.getName());
							p.sendMessage(ChatColor.RED + player.getName() + ChatColor.GOLD + " just sent you mail. /mail read");
							return;
						}
						catch(Exception e) {
							player.sendMessage(ChatColor.RED + "Invalid arguments: /mail send [player] [message]");
							return;
						}
					}
					catch(Exception e) {
						player.sendMessage(ChatColor.RED + "Invalid arguments: /mail send [player] [message]");
					}
					return;
				}
				case "read": {
					for(String s : plugin.getMetadataString(player, "MineverseChat.mail", plugin).split("\n")) {
						if(s.length() > 0) {
							player.sendMessage(ChatColor.GOLD + cc.FormatStringAll(s));
						}
					}
					player.sendMessage(ChatColor.GOLD + "To clear mail, type /mail clear");
					return;
				}
				case "clear": {
					player.removeMetadata("MineverseChat.mail", plugin);
					player.sendMessage(ChatColor.GOLD + "Cleared your mail.");
					return;
				}
				default: {
					player.sendMessage(ChatColor.RED + "Invalid arguments, /mail [send, read, sendall, clear]");
					return;
				}
				}
			}
			catch(Exception e) {
				player.sendMessage(ChatColor.RED + "Invalid arguments, /mail [send, read, sendall, clear]");
			}
			return;
		}*/
	}
}