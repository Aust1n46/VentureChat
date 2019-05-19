package mineverse.Aust1n46.chat.irc.listeners;

import java.util.HashSet;
import java.util.Set;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.channel.ChatChannelInfo;
import mineverse.Aust1n46.chat.irc.command.IRCCommand;
import mineverse.Aust1n46.chat.irc.command.IRCCommandInfo;
import mineverse.Aust1n46.chat.irc.command.IRCCommandSender;
import mineverse.Aust1n46.chat.utilities.Format;

import org.bukkit.ChatColor;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

//This class listens on the IRC channel for messages to relay to the server through the bot.
@SuppressWarnings("rawtypes")
public class MessageListener extends ListenerAdapter {
	private MineverseChat plugin;
	private IRCCommandInfo ircc;
	private IRCCommandSender cmd;
	private ChatChannelInfo cc;

	public MessageListener(ChatChannelInfo cc, IRCCommandInfo ircc) {
		this.ircc = ircc;
		this.cc = cc;
		this.plugin = MineverseChat.getInstance();
	}

	@Override
	public void onMessage(MessageEvent event) {
		User user = event.getUser();
		Channel channel = event.getChannel();
		if(event.getMessage().startsWith(".")) {
			cmd = new IRCCommandSender(channel);
			for(IRCCommand c : ircc.getIRCCommands()) {
				if(event.getMessage().toLowerCase().substring(1).startsWith(c.getName().toLowerCase())) {
					if(c.hasMode()) {
						if(!hasPermission(user, c.getMode(), channel)) {
							channel.send().message("Sorry " + user.getNick() + " you do not have permission for this command.");
							return;
						}
					}
					for(String s : c.getComponents()) {
						plugin.getServer().dispatchCommand(cmd, s + event.getMessage().substring(1 + c.getName().length()));
					}
					return;
				}
			}
			channel.send().message("Unknown command, type .help for a list of valid commands.");
			return;
		}
		Set<MineverseChatPlayer> players = new HashSet<MineverseChatPlayer>();
		for(ChatChannel ci : cc.getChannelsInfo()) {
			if(ci.isIRC()) {
				for(MineverseChatPlayer p : MineverseChat.players) {
					if(p.isOnline() && p.getListening().contains(ci.getName())) {
						players.add(p);
					}
				}
			}
		}
		for(MineverseChatPlayer p : players) {
			p.getPlayer().sendMessage(ChatColor.RED + "<IRC> " + user.getNick() + ": " + ChatColor.WHITE + Format.FormatStringAll(event.getMessage()));
		}
		plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "<IRC> " + user.getNick() + ": " + ChatColor.WHITE + Format.FormatStringAll(event.getMessage()));
	}

	public boolean hasPermission(User user, String mode, Channel channel) {
		switch(mode) {
		case "v": {
			return user.getChannelsVoiceIn().contains(channel) || user.getChannelsOpIn().contains(channel) || user.getChannelsOwnerIn().contains(channel) || user.getChannelsHalfOpIn().contains(channel);
		}
		case "o": {
			return user.getChannelsOpIn().contains(channel) || user.getChannelsOwnerIn().contains(channel);
		}
		case "q": {
			return user.getChannelsOwnerIn().contains(channel);
		}
		case "h": {
			return user.getChannelsHalfOpIn().contains(channel) || user.getChannelsOpIn().contains(channel) || user.getChannelsOwnerIn().contains(channel);
		}
		}
		return false;
	}
}