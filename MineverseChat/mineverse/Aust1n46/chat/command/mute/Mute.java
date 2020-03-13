package mineverse.Aust1n46.chat.command.mute;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.command.MineverseCommand;

public class Mute extends MineverseCommand {

	public Mute(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(sender.hasPermission("venturechat.mute")) {
			if(args.length < 2) {
				sender.sendMessage(ChatColor.RED + "Invalid command: /mute [player] [channel] {time}");
				return;
			}
			MineverseChatPlayer player = MineverseChatAPI.getMineverseChatPlayer(args[0]);
			if(player == null || (!player.isOnline() && !sender.hasPermission("venturechat.mute.offline"))) {
				sender.sendMessage(ChatColor.RED + "Player: " + ChatColor.GOLD + args[0] + ChatColor.RED + " is not online.");
				return;
			}
			if(args.length == 2) {
				if(ChatChannel.isChannel(args[1])) {
					ChatChannel channel = ChatChannel.getChannel(args[1]);
					if(player.isMuted(channel.getName())) {
						sender.sendMessage(ChatColor.GOLD + player.getName() + ChatColor.RED + " is already muted in channel: " + ChatColor.valueOf(channel.getColor().toUpperCase()) + channel.getName());
						return;
					}
					if(channel.isMutable()) {
						player.addMute(channel.getName(), 0);
						sender.sendMessage(ChatColor.RED + "Muted player " + ChatColor.GOLD + player.getName() + ChatColor.RED + " in: " + ChatColor.valueOf(channel.getColor().toUpperCase()) + channel.getName());
						if(player.isOnline()) 
							player.getPlayer().sendMessage(ChatColor.RED + "You have just been muted in: " + ChatColor.valueOf(channel.getColor().toUpperCase()) + channel.getName());
						else 
							player.setModified(true);
						if(channel.getBungee()) {
							MineverseChat.getInstance().synchronize(player, true);
						}
						return;
					}
					sender.sendMessage(ChatColor.RED + "You cannot mute players in this channel: " + ChatColor.valueOf(channel.getColor().toUpperCase()) + channel.getName());
					return;
				}
				sender.sendMessage(ChatColor.RED + "Invalid channel: " + args[1]);
				return;
			}			
			if(ChatChannel.isChannel(args[1])) {
				ChatChannel channel = ChatChannel.getChannel(args[1]);
				if(player.isMuted(channel.getName())) {
					sender.sendMessage(ChatColor.GOLD + player.getName() + ChatColor.RED + " is already muted in channel: " + ChatColor.valueOf(channel.getColor().toUpperCase()) + channel.getName());
					return;
				}
				if(channel.isMutable()) {
					try {
						//Calendar currentDate = Calendar.getInstance();
						//SimpleDateFormat formatter = new SimpleDateFormat("dd:HH:mm:ss");
						//String date = formatter.format(currentDate.getTime());
						//String[] datearray = date.split(":");
						//int datetime = (Integer.parseInt(datearray[0]) * 1440) + (Integer.parseInt(datearray[1]) * 60) + (Integer.parseInt(datearray[2]));
						
						int datetime = (int) (System.currentTimeMillis() / 60000);
						
						int time = Integer.parseInt(args[2]);
						if(time > 0) {
							player.addMute(channel.getName(), datetime + time);
							String keyword = "minutes";
							if(time == 1) 
								keyword = "minute";
							sender.sendMessage(ChatColor.RED + "Muted player " + ChatColor.GOLD + player.getName() + ChatColor.RED + " in: " + ChatColor.valueOf(channel.getColor().toUpperCase()) + channel.getName() + ChatColor.RED + " for " + time + " " + keyword);
							if(player.isOnline())
								player.getPlayer().sendMessage(ChatColor.RED + "You have just been muted in: " + ChatColor.valueOf(channel.getColor().toUpperCase()) + channel.getName() + ChatColor.RED + " for " + time + " " + keyword);
							else
								player.setModified(true);
							if(channel.getBungee()) {
								MineverseChat.getInstance().synchronize(player, true);
							}
							return;
						}
						sender.sendMessage(ChatColor.RED + "Invalid time: " + args[2]);
					}
					catch(Exception e) {
						sender.sendMessage(ChatColor.RED + "Invalid time: " + args[2]);
					}
					return;
				}
				sender.sendMessage(ChatColor.RED + "You cannot mute players in this channel: " + ChatColor.valueOf(channel.getColor().toUpperCase()) + channel.getName());
				return;
			}
			sender.sendMessage(ChatColor.RED + "Invalid channel: " + args[1]);
			return;
		}
		sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
	}
}