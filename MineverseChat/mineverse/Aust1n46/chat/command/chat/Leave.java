package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.command.MineverseCommand;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;

public class Leave extends MineverseCommand {
	private MineverseChat plugin = MineverseChat.getInstance();;
	
	public Leave(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(!(sender instanceof Player)) {
			plugin.getServer().getConsoleSender().sendMessage(LocalizedMessage.COMMAND_MUST_BE_RUN_BY_PLAYER.toString());
			return;
		}		
		MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer((Player) sender);
		if(args.length > 0) {
			ChatChannel channel = ChatChannel.getChannel(args[0]);
			if(channel == null) {
				mcp.getPlayer().sendMessage(LocalizedMessage.INVALID_CHANNEL.toString()
						.replace("{args}", args[0]));
				return;
			}
			mcp.removeListening(channel.getName());	
			mcp.getPlayer().sendMessage(LocalizedMessage.LEAVE_CHANNEL.toString()
					.replace("{channel_color}", ChatColor.valueOf(channel.getColor().toUpperCase()) + "")
					.replace("{channel_name}", channel.getName()));
			if(mcp.getListening().size() == 0) {
				mcp.addListening(ChatChannel.getDefaultChannel().getName());
				mcp.setCurrentChannel(ChatChannel.getDefaultChannel());
				mcp.getPlayer().sendMessage(LocalizedMessage.MUST_LISTEN_ONE_CHANNEL.toString());
				mcp.getPlayer().sendMessage(LocalizedMessage.SET_CHANNEL.toString()
						.replace("{channel_color}", ChatColor.valueOf(ChatChannel.getDefaultColor().toUpperCase()) + "")
						.replace("{channel_name}", ChatChannel.getDefaultChannel().getName()));
			}
			if(channel.getBungee()) {
				MineverseChat.getInstance().synchronize(mcp, true);
			}
			return;
		}
		mcp.getPlayer().sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString()
				.replace("{command}", "/leave")
				.replace("{args}", "[channel]"));
	}
}