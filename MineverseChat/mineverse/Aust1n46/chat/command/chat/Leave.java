package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.command.MineverseCommand;

public class Leave extends MineverseCommand {
	private MineverseChat plugin = MineverseChat.getInstance();;
	
	public Leave(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(!(sender instanceof Player)) {
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "This command must be run by a player.");
			return;
		}		
		MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer((Player) sender);
		if(args.length > 0) {
			ChatChannel channel = ChatChannel.getChannel(args[0]);
			if(channel == null) {
				mcp.getPlayer().sendMessage(ChatColor.RED + "Invalid channel: " + args[0]);
				return;
			}
			mcp.removeListening(channel.getName());
			String format = ChatColor.valueOf(channel.getColor().toUpperCase()) + "[" + channel.getName() + "]";
			mcp.getPlayer().sendMessage("Leaving channel: " + format);				
			if(mcp.getListening().size() == 0) {
				mcp.addListening(ChatChannel.getDefaultChannel().getName());
				mcp.setCurrentChannel(ChatChannel.getDefaultChannel());
				mcp.getPlayer().sendMessage(ChatColor.RED + "You need to be listening on at least one channel, setting you into the default channel.");
				mcp.getPlayer().sendMessage("Channel Set: " + ChatColor.valueOf(ChatChannel.getDefaultColor().toUpperCase()) + "[" + ChatChannel.getDefaultChannel().getName() + "]");
			}
			if(channel.getBungee()) {
				MineverseChat.getInstance().synchronize(mcp, true);
			}
			return;
		}
		mcp.getPlayer().sendMessage(ChatColor.RED + "Invalid command: /leave [channelname]");
	}
}