package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.command.MineverseCommand;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;

public class Listen extends MineverseCommand {

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(!(sender instanceof Player)) {
			Bukkit.getServer().getConsoleSender().sendMessage(LocalizedMessage.COMMAND_MUST_BE_RUN_BY_PLAYER.toString());
			return;
		}
		MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer((Player) sender);
		if(args.length > 0) {
			ChatChannel channel = ChatChannel.getChannel(args[0]);
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
			.replace("{channel_color}", channel.getColor() + "")
			.replace("{channel_name}", channel.getName()));
			return;
		}
		mcp.getPlayer().sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString()
				.replace("{command}", "/listen")
				.replace("{args}", "[channel]"));
	}
}
