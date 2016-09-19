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
import mineverse.Aust1n46.chat.utilities.Format;

@SuppressWarnings("unused")
public class Mail extends MineverseCommand {
	private MineverseChat plugin;
	private ChatChannelInfo cc = MineverseChat.ccInfo;

	public Mail(String name) {
		super(name);
		this.plugin = MineverseChat.getInstance();
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if (!(sender instanceof Player)) {
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "This command must be run by a player.");
			return;
		}
		MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer((Player) sender);
		if (mcp.getPlayer().hasPermission("venturechat.mail")) {
			try {
				switch (args[0]) {
				case "send": {
					try {
						MineverseChatPlayer tp = MineverseChatAPI.getMineverseChatPlayer(args[1]);
						if (tp == null) {
							mcp.getPlayer().sendMessage(ChatColor.RED + "Player: " + args[1] + " has never played before.");
							return;
						}
						String msg = args[2];
						for (int x = 3; x < args.length; x++) {
							if (args[x].length() > 0)
								msg += " " + args[x];
						}
						mcp.getPlayer().sendMessage(ChatColor.GOLD + "Sent mail: " + ChatColor.RED + Format.FormatStringAll(msg) + ChatColor.GOLD + " to: " + ChatColor.RED + tp.getName());
						tp.addMail(msg);
						if(tp.isOnline()) {
							tp.getPlayer().sendMessage(ChatColor.RED + mcp.getName() + ChatColor.GOLD + " has sent you mail. /mail read");
						}
						return;
					} 
					catch (Exception e) {
						mcp.getPlayer().sendMessage(ChatColor.RED + "Invalid arguments, /mail send [player] [message]");
						return;
					}
				}
				case "read": {
					for (String s : mcp.getMail()) {
						if (s.length() > 0) {
							mcp.getPlayer().sendMessage(ChatColor.GOLD + Format.FormatStringAll(s));
						}
					}
					mcp.getPlayer().sendMessage(ChatColor.GOLD + "To clear mail, type /mail clear");
					return;
				}
				case "clear": {
					mcp.clearMail();
					mcp.getPlayer().sendMessage(ChatColor.GOLD + "Cleared your mail.");
					return;
				}
				default: {
					mcp.getPlayer().sendMessage(ChatColor.RED + "Invalid arguments, /mail [send, read, sendall, clear]");
					return;
				}
				}
			} 
			catch (Exception e) {
				mcp.getPlayer().sendMessage(ChatColor.RED + "Invalid arguments, /mail [send, read, sendall, clear]");
			}
			return;
		}
	}
}