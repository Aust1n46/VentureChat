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
import mineverse.Aust1n46.chat.localization.LocalizedMessage;

public class Ignore extends MineverseCommand {
	private MineverseChat plugin;

	public Ignore(String name) {
		super(name);
		this.plugin = MineverseChat.getInstance();
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(!(sender instanceof Player)) {
			plugin.getServer().getConsoleSender().sendMessage(LocalizedMessage.COMMAND_MUST_BE_RUN_BY_PLAYER.toString());
			return;
		}
		MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer((Player) sender);
		if(args.length == 0) {
			mcp.getPlayer().sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS_IGNORE.toString());
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
			mcp.getPlayer().sendMessage(LocalizedMessage.IGNORE_LIST_HEADER.toString());
			if(ignoreList.length() > 0) {
				mcp.getPlayer().sendMessage(ignoreList.substring(0, ignoreList.length() - 2));
			}
			return;
		}
		MineverseChatPlayer player = MineverseChatAPI.getMineverseChatPlayer(args[0]);

		if(player == null) {
			mcp.getPlayer().sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString()
					.replace("{args}", args[0]));
			return;
		}
		if(mcp.getIgnores().contains(player.getUUID())) {
			mcp.getPlayer().sendMessage(LocalizedMessage.IGNORE_PLAYER_OFF.toString()
					.replace("{player}", player.getName()));
			mcp.removeIgnore(player.getUUID());
			plugin.synchronize(mcp, true);
			return;
		}
		if(mcp.getName().equalsIgnoreCase(player.getName())) {
			mcp.getPlayer().sendMessage(LocalizedMessage.IGNORE_YOURSELF.toString());
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
		if(!player.isOnline()) {
			mcp.getPlayer().sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString()
					.replace("{args}", player.getName()));
			return;
		}
		if(mcp.getIgnores().contains(player.getUUID())) {
			mcp.getPlayer().sendMessage(ChatColor.GOLD + "You are no longer ignoring player: " + ChatColor.RED + player.getName());
			mcp.removeIgnore(player.getUUID());
			plugin.synchronize(mcp, true);
			return;
		}		
		if(player.getPlayer().hasPermission("venturechat.ignore.bypass")) {
			mcp.getPlayer().sendMessage(LocalizedMessage.IGNORE_PLAYER_CANT.toString()
					.replace("{player}", player.getName()));
			return;
		}
		mcp.getPlayer().sendMessage(LocalizedMessage.IGNORE_PLAYER_ON.toString()
				.replace("{player}", player.getName()));
		mcp.addIgnore(player.getUUID());
		plugin.synchronize(mcp, true);
		return;
	}
}