package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;

public class Leave extends Command {
	public Leave() {
		super("leave");
	}

	@Override
	public boolean execute(CommandSender sender, String command, String[] args) {
		if (!(sender instanceof Player)) {
			Bukkit.getServer().getConsoleSender().sendMessage(LocalizedMessage.COMMAND_MUST_BE_RUN_BY_PLAYER.toString());
			return true;
		}
		MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer((Player) sender);
		if (args.length > 0) {
			ChatChannel channel = ChatChannel.getChannel(args[0]);
			if (channel == null) {
				mcp.getPlayer().sendMessage(LocalizedMessage.INVALID_CHANNEL.toString().replace("{args}", args[0]));
				return true;
			}
			mcp.removeListening(channel.getName());
			mcp.getPlayer().sendMessage(LocalizedMessage.LEAVE_CHANNEL.toString().replace("{channel_color}", channel.getColor() + "").replace("{channel_name}", channel.getName()));
			boolean isThereABungeeChannel = channel.getBungee();
			if (mcp.getListening().size() == 0) {
				mcp.addListening(ChatChannel.getDefaultChannel().getName());
				mcp.setCurrentChannel(ChatChannel.getDefaultChannel());
				if (ChatChannel.getDefaultChannel().getBungee()) {
					isThereABungeeChannel = true;
				}
				mcp.getPlayer().sendMessage(LocalizedMessage.MUST_LISTEN_ONE_CHANNEL.toString());
				mcp.getPlayer().sendMessage(LocalizedMessage.SET_CHANNEL.toString().replace("{channel_color}", ChatColor.valueOf(ChatChannel.getDefaultColor().toUpperCase()) + "")
						.replace("{channel_name}", ChatChannel.getDefaultChannel().getName()));
			}
			if (isThereABungeeChannel) {
				MineverseChat.synchronize(mcp, true);
			}
			return true;
		}
		mcp.getPlayer().sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString().replace("{command}", "/leave").replace("{args}", "[channel]"));
		return true;
	}
}
