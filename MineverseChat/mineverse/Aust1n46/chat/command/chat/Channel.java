package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.api.events.ChannelJoinEvent;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.command.MineverseCommand;

public class Channel extends MineverseCommand implements Listener {
	private MineverseChat plugin = MineverseChat.getInstance();
	
	public Channel() {}
	
	public Channel(String name) {
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
			if(!ChatChannel.isChannel(args[0])) {
				mcp.getPlayer().sendMessage(ChatColor.RED + "Invalid channel: " + args[0]);
				return;
			}			
			ChatChannel channel = ChatChannel.getChannel(args[0]);						
			plugin.getServer().getPluginManager().callEvent(new ChannelJoinEvent(mcp.getPlayer(), channel, "Channel Set: " + ChatColor.valueOf(channel.getColor().toUpperCase()) + "[" + channel.getName() + "]"));			
			return;
		}
		mcp.getPlayer().sendMessage(ChatColor.RED + "Invalid command: /" + command + " [channel]");
		return;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChannelJoin(ChannelJoinEvent event) {
		if(event.isCancelled()) 
			return;		
		ChatChannel channel = event.getChannel();
		MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer(event.getPlayer());		
		if(channel.hasPermission()) {
			if(!mcp.getPlayer().hasPermission(channel.getPermission())) {
				mcp.removeListening(channel.getName());
				mcp.getPlayer().sendMessage(ChatColor.RED + "You do not have permission for this channel.");
				return;
			}
		}		
		if(mcp.hasConversation()) {
			for(MineverseChatPlayer p : MineverseChat.onlinePlayers) {
				if(p.isSpy()) {
					p.getPlayer().sendMessage(mcp.getName() + " is no longer in a private conversation with " + MineverseChatAPI.getMineverseChatPlayer(mcp.getConversation()).getName() + ".");
				}
			}
			mcp.getPlayer().sendMessage("You are no longer in private conversation with " + MineverseChatAPI.getMineverseChatPlayer(mcp.getConversation()).getName() + ".");
			mcp.setConversation(null);
		}
		mcp.addListening(channel.getName());		
		mcp.setCurrentChannel(channel);
		mcp.getPlayer().sendMessage(event.getMessage());
		if(channel.getBungee()) {
			MineverseChat.getInstance().synchronize(mcp, true);
		}
		return;
	}
}