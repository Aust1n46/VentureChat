package mineverse.Aust1n46.chat.command.mute;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.channel.ChatChannelInfo;
import mineverse.Aust1n46.chat.command.MineverseCommand;

public class Unmute extends MineverseCommand {
	private ChatChannelInfo cc = MineverseChat.ccInfo;

	public Unmute(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(sender.hasPermission("venturechat.mute")) {
			if(args.length < 2) {
				sender.sendMessage(ChatColor.RED + "Invalid command: /unmute [player] [channel]");
				return;
			}
			MineverseChatPlayer player = MineverseChatAPI.getMineverseChatPlayer(args[0]);
			if(player == null || (!player.isOnline() && !sender.hasPermission("venturechat.mute.offline"))) {
				sender.sendMessage(ChatColor.RED + "Player: " + ChatColor.GOLD + args[0] + ChatColor.RED + " is not online.");
				return;
			}
			for(ChatChannel channel : cc.getChannelsInfo()) {
				if(channel.getName().equalsIgnoreCase(args[1]) || channel.getAlias().equalsIgnoreCase(args[1])) {
					if(!player.isMuted(channel.getName())) {
						sender.sendMessage(ChatColor.GOLD + player.getName() + ChatColor.RED + " is not muted in channel: " + ChatColor.valueOf(channel.getColor().toUpperCase()) + channel.getName());
						return;
					}
					player.removeMute(channel.getName());					
					sender.sendMessage(ChatColor.RED + "Unmuted player " + ChatColor.GOLD + player.getName() + ChatColor.RED + " in: " + ChatColor.valueOf(channel.getColor().toUpperCase()) + channel.getName());
					if(player.isOnline()) {
						player.getPlayer().sendMessage(ChatColor.RED + "You have just been unmuted in: " + ChatColor.valueOf(channel.getColor().toUpperCase()) + channel.getName());
					}
					else {
						player.setModified(true);
					}
					if(channel.getBungee()) {
						MineverseChat.getInstance().synchronize(player, true);
					}
					return;
				}				
			}
			sender.sendMessage(ChatColor.RED + "Invalid channel: " + args[1]);
			return;
		}
		else {
			sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
			return;
		}
	}
}