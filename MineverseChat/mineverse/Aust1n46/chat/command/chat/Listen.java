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
import mineverse.Aust1n46.chat.localization.LocalizedMessage;

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
			plugin.getServer().getConsoleSender().sendMessage(LocalizedMessage.COMMAND_MUST_BE_RUN_BY_PLAYER.toString());
			return;
		}
		MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer((Player) sender);
		if(args.length > 0) {
			ChatChannel channel = cc.getChannelInfo(args[0]);
			if(channel == null) {
				mcp.getPlayer().sendMessage(LocalizedMessage.INVALID_CHANNEL.toString()
						.replace("{args}", args[0]));
				return;
			}
			if(channel.hasPermission()) {
				if(!mcp.getPlayer().hasPermission(channel.getPermission())) {
					mcp.removeListening(channel.getName());
					mcp.getPlayer().sendMessage(LocalizedMessage.CHANNEL_NO_PERMISSION.toString());
					return;
				}
			}
			mcp.addListening(channel.getName());
			mcp.getPlayer().sendMessage(LocalizedMessage.LISTEN_CHANNEL.toString()
			.replace("{channel_color}", ChatColor.valueOf(channel.getColor().toUpperCase()) + "")
			.replace("{channel_name}", channel.getName()));
			return;
		}
		mcp.getPlayer().sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString()
				.replace("{command}", "/listen")
				.replace("{args}", "[channel]"));
	}
}