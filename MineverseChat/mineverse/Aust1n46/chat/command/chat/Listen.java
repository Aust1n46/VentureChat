package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.channel.ChatChannelInfo;
import mineverse.Aust1n46.chat.command.MineverseCommand;

public class Listen extends MineverseCommand {
	private MineverseChat plugin;
	private ChatChannelInfo cc = MineverseChat.ccInfo;

	public Listen(String name) {
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
		if(args.length > 0) {
			ChatChannel channel = cc.getChannelInfo(args[0]);
			if(channel == null) {
				mcp.getPlayer().sendMessage(ChatColor.RED + "Invalid channel: " + args[0]);
				return;
			}
			if(channel.hasPermission()) {
				if(!mcp.getPlayer().hasPermission(channel.getPermission())) {
					mcp.removeListening(channel.getName());
					mcp.getPlayer().sendMessage(ChatColor.RED + "You do not have permission for this channel.");
					return;
				}
			}
			String format = ChatColor.valueOf(channel.getColor().toUpperCase()) + "[" + channel.getName() + "]";
			mcp.addListening(channel.getName());
			mcp.getPlayer().sendMessage("Listening to Channel: " + format);
			return;
		}
		mcp.getPlayer().sendMessage(ChatColor.RED + "Invalid command: /listen [channel]");
	}
}