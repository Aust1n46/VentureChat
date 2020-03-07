package mineverse.Aust1n46.chat.command.chat;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.channel.ChatChannelInfo;
import mineverse.Aust1n46.chat.command.MineverseCommand;

@SuppressWarnings("unused")
public class Chatinfo extends MineverseCommand {
	private MineverseChat plugin;
	private ChatChannelInfo cc = MineverseChat.ccInfo;

	public Chatinfo(String name) {
		super(name);
		this.plugin = MineverseChat.getInstance();
	}

	//@SuppressWarnings("unchecked")
	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(sender.hasPermission("venturechat.chatinfo")) {
			if(args.length == 0) {
				if(!(sender instanceof Player)) {
					plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "This command must be run by a player; use /ci [name]");
					return;
				}
				MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer((Player) sender);
				String listen = "";
				String mute = "";
				String blockedcommands = "";
				if(args.length < 1) {
					mcp.getPlayer().sendMessage(ChatColor.GOLD + "Player: " + ChatColor.GREEN + mcp.getName());
					for(String c : mcp.getListening()) {		
						ChatChannel channel = MineverseChat.ccInfo.getChannelInfo(c);
						listen += ChatColor.valueOf(channel.getColor().toUpperCase()) + channel.getName() + " ";						
					}
					for(String c : mcp.getMutes().keySet()) {
						ChatChannel channel = MineverseChat.ccInfo.getChannelInfo(c);
						mute += ChatColor.valueOf(channel.getColor().toUpperCase()) + channel.getName() + " ";						
					}
					for(String bc : mcp.getBlockedCommands()) {						
						blockedcommands += bc + " ";
					}
					mcp.getPlayer().sendMessage(ChatColor.GOLD + "Listening: " + listen);
					if(mute.length() > 0) {
						mcp.getPlayer().sendMessage(ChatColor.GOLD + "Mutes: " + mute);
					}
					else {
						mcp.getPlayer().sendMessage(ChatColor.GOLD + "Mutes: " + ChatColor.RED + "N/A");
					}
					if(blockedcommands.length() > 0) {
						mcp.getPlayer().sendMessage(ChatColor.GOLD + "Blocked Commands: " + ChatColor.RED + blockedcommands);
					}
					else {
						mcp.getPlayer().sendMessage(ChatColor.GOLD + "Blocked Commands: " + ChatColor.RED + "N/A");
					}
					if(mcp.hasConversation()) {
						mcp.getPlayer().sendMessage(ChatColor.GOLD + "Private conversation: " + ChatColor.GREEN + MineverseChatAPI.getMineverseChatPlayer(mcp.getConversation()).getName());
					}
					else {
						mcp.getPlayer().sendMessage(ChatColor.GOLD + "Private conversation: " + ChatColor.RED + "N/A");
					}
					if(mcp.isSpy()) {
						mcp.getPlayer().sendMessage(ChatColor.GOLD + "Spy: " + ChatColor.GREEN + "true");
					}
					else {
						mcp.getPlayer().sendMessage(ChatColor.GOLD + "Spy: " + ChatColor.RED + "false");
					}
					if(mcp.hasCommandSpy()) {
						mcp.getPlayer().sendMessage(ChatColor.GOLD + "Command spy: " + ChatColor.GREEN + "true");
					}
					else {
						mcp.getPlayer().sendMessage(ChatColor.GOLD + "Command spy: " + ChatColor.RED + "false");
					}
					if(mcp.hasFilter()) {
						mcp.getPlayer().sendMessage(ChatColor.GOLD + "Filter: " + ChatColor.GREEN + "true");
					}
					else {
						mcp.getPlayer().sendMessage(ChatColor.GOLD + "Filter: " + ChatColor.RED + "false");
					}
					return;
				}
			}
			if(sender.hasPermission("venturechat.chatinfo.others")) {
				String listen = "";
				String mute = "";
				String blockedcommands = "";
				MineverseChatPlayer p = MineverseChatAPI.getMineverseChatPlayer(args[0]);
				if(p == null) {
					sender.sendMessage(ChatColor.RED + "Player: " + ChatColor.GOLD + args[0] + ChatColor.RED + " is not online.");
					return;
				}
				sender.sendMessage(ChatColor.GOLD + "Player: " + ChatColor.GREEN + p.getName());
				for(String c : p.getListening()) {		
					ChatChannel channel = MineverseChat.ccInfo.getChannelInfo(c);
					listen += ChatColor.valueOf(channel.getColor().toUpperCase()) + channel.getName() + " ";						
				}
				for(String c : p.getMutes().keySet()) {
					ChatChannel channel = MineverseChat.ccInfo.getChannelInfo(c);
					mute += ChatColor.valueOf(channel.getColor().toUpperCase()) + channel.getName() + " ";						
				}
				for(String bc : p.getBlockedCommands()) {						
					blockedcommands += bc + " ";
				}
				sender.sendMessage(ChatColor.GOLD + "Listening: " + listen);
				if(mute.length() > 0) {
					sender.sendMessage(ChatColor.GOLD + "Mutes: " + mute);
				}
				else {
					sender.sendMessage(ChatColor.GOLD + "Mutes: " + ChatColor.RED + "N/A");
				}
				if(blockedcommands.length() > 0) {
					sender.sendMessage(ChatColor.GOLD + "Blocked Commands: " + ChatColor.RED + blockedcommands);
				}
				else {
					sender.sendMessage(ChatColor.GOLD + "Blocked Commands: " + ChatColor.RED + "N/A");
				}
				if(p.hasConversation()) {
					sender.sendMessage(ChatColor.GOLD + "Private conversation: " + ChatColor.GREEN + MineverseChatAPI.getMineverseChatPlayer(p.getConversation()).getName());
				}
				else {
					sender.sendMessage(ChatColor.GOLD + "Private conversation: " + ChatColor.RED + "N/A");
				}
				if(p.isSpy()) {
					sender.sendMessage(ChatColor.GOLD + "Spy: " + ChatColor.GREEN + "true");
				}
				else {
					sender.sendMessage(ChatColor.GOLD + "Spy: " + ChatColor.RED + "false");
				}
				if(p.hasCommandSpy()) {
					sender.sendMessage(ChatColor.GOLD + "Command spy: " + ChatColor.GREEN + "true");
				}
				else {
					sender.sendMessage(ChatColor.GOLD + "Command spy: " + ChatColor.RED + "false");
				}
				if(p.hasFilter()) {
					sender.sendMessage(ChatColor.GOLD + "Filter: " + ChatColor.GREEN + "true");
				}
				else {
					sender.sendMessage(ChatColor.GOLD + "Filter: " + ChatColor.RED + "false");
				}
				return;
			}
			else {
				sender.sendMessage(ChatColor.RED + "You do not have permission to check the chat info of others.");
			}
			return;
		}
		else {
			sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
			return;
		}
	}
}