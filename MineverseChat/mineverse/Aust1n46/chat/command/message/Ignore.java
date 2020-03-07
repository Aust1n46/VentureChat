package mineverse.Aust1n46.chat.command.message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.command.MineverseCommand;

public class Ignore extends MineverseCommand {
	private MineverseChat plugin;

	public Ignore(String name) {
		super(name);
		this.plugin = MineverseChat.getInstance();
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(!(sender instanceof Player)) {
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "This command must be run by a player.");
			return;
		}
		MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer((Player) sender);
		if(args.length == 0) {
			mcp.getPlayer().sendMessage(ChatColor.RED + "Invalid command: /ignore [player] or /ignore list");
			return;
		}
		if(args[0].equalsIgnoreCase("list")) {
			String ignoreList = "";
			for(UUID ignore : mcp.getIgnores()) {
				MineverseChatPlayer i = MineverseChatAPI.getMineverseChatPlayer(ignore);
				String iName = ignore.toString();
				if(i != null) {
					iName = i.getName();
				}
				ignoreList += ChatColor.RED + iName + ChatColor.WHITE + ", ";
			}
			mcp.getPlayer().sendMessage(ChatColor.GOLD + "You are currently ignoring these players:");
			if(ignoreList.length() > 0) {
				mcp.getPlayer().sendMessage(ignoreList.substring(0, ignoreList.length() - 2));
			}
			return;
		}
		MineverseChatPlayer player = MineverseChatAPI.getMineverseChatPlayer(args[0]);
		if(mcp.getName().equalsIgnoreCase(args[0])) {
			mcp.getPlayer().sendMessage(ChatColor.RED + "You can not ignore yourself!");
			return;
		}
		if(plugin.getConfig().getBoolean("bungeecordmessaging", true)) {
			ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(byteOutStream);
			try {
				out.writeUTF("Ignore");
				out.writeUTF("Send");
				out.writeUTF(args[0]);
				out.writeUTF(mcp.getUUID().toString());
				mcp.getPlayer().sendPluginMessage(plugin, MineverseChat.PLUGIN_MESSAGING_CHANNEL, byteOutStream.toByteArray());
				out.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return;
		}
		if(player == null || !player.isOnline()) {
			mcp.getPlayer().sendMessage(ChatColor.RED + "Player: " + ChatColor.GOLD + args[0] + ChatColor.RED + " is not online.");
			return;
		}
		if(mcp.getIgnores().contains(player.getUUID())) {
			mcp.getPlayer().sendMessage(ChatColor.GOLD + "You are no longer ignoring player: " + ChatColor.RED + player.getName());
			mcp.removeIgnore(player.getUUID());
			plugin.synchronize(mcp, true);
			return;
		}		
		if(player.getPlayer().hasPermission("venturechat.ignore.bypass")) {
			mcp.getPlayer().sendMessage(ChatColor.RED + "You cannot ignore player: " + ChatColor.GOLD + player.getName() + ChatColor.RED + ".");
			return;
		}
		mcp.getPlayer().sendMessage(ChatColor.GOLD + "You are now ignoring player: " + ChatColor.RED + player.getName());
		mcp.addIgnore(player.getUUID());
		plugin.synchronize(mcp, true);
		return;
	}
}