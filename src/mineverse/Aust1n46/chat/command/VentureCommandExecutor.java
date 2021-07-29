package mineverse.Aust1n46.chat.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.command.chat.Broadcast;
import mineverse.Aust1n46.chat.command.chat.BungeeToggle;
import mineverse.Aust1n46.chat.command.chat.Channel;
import mineverse.Aust1n46.chat.command.chat.Channelinfo;
import mineverse.Aust1n46.chat.command.chat.Chatinfo;
import mineverse.Aust1n46.chat.command.chat.Chatreload;
import mineverse.Aust1n46.chat.command.chat.Chlist;
import mineverse.Aust1n46.chat.command.chat.Chwho;
import mineverse.Aust1n46.chat.command.chat.Clearchat;
import mineverse.Aust1n46.chat.command.chat.Commandblock;
import mineverse.Aust1n46.chat.command.chat.Commandspy;
import mineverse.Aust1n46.chat.command.chat.Config;
import mineverse.Aust1n46.chat.command.chat.Edit;
import mineverse.Aust1n46.chat.command.chat.Filter;
import mineverse.Aust1n46.chat.command.chat.Force;
import mineverse.Aust1n46.chat.command.chat.Forceall;
import mineverse.Aust1n46.chat.command.chat.Kickchannel;
import mineverse.Aust1n46.chat.command.chat.Kickchannelall;
import mineverse.Aust1n46.chat.command.chat.Leave;
import mineverse.Aust1n46.chat.command.chat.Listen;
import mineverse.Aust1n46.chat.command.chat.Me;
import mineverse.Aust1n46.chat.command.chat.Nick;
import mineverse.Aust1n46.chat.command.chat.Party;
import mineverse.Aust1n46.chat.command.chat.RangedSpy;
import mineverse.Aust1n46.chat.command.chat.Removemessage;
import mineverse.Aust1n46.chat.command.chat.Setchannel;
import mineverse.Aust1n46.chat.command.chat.Setchannelall;
import mineverse.Aust1n46.chat.command.chat.VentureChatGui;
import mineverse.Aust1n46.chat.command.chat.Venturechat;
import mineverse.Aust1n46.chat.command.message.IgnoreCommandExecutor;
import mineverse.Aust1n46.chat.command.message.MessageCommandExecutor;
import mineverse.Aust1n46.chat.command.message.MessageToggle;
import mineverse.Aust1n46.chat.command.message.Notifications;
import mineverse.Aust1n46.chat.command.message.Reply;
import mineverse.Aust1n46.chat.command.message.Spy;
import mineverse.Aust1n46.chat.command.mute.Mute;
import mineverse.Aust1n46.chat.command.mute.Muteall;
import mineverse.Aust1n46.chat.command.mute.Unmute;
import mineverse.Aust1n46.chat.command.mute.Unmuteall;

/**
 * Class that initializes and executes the plugin's commands.
 */
public class VentureCommandExecutor implements TabExecutor {
	private static Map<String, VentureCommand> commands = new HashMap<String, VentureCommand>();
	private static MineverseChat plugin = MineverseChat.getInstance();
	private static VentureCommandExecutor commandExecutor;

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] parameters) {
		commands.get(command.getName()).execute(sender, command.getName(), parameters);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return commands.get(command.getName()).onTabComplete(sender, command, label, args);
	}
	
	public static void initialize() {
		commandExecutor = new VentureCommandExecutor();
		commands.put("broadcast", new Broadcast());
		commands.put("channel", new Channel());
		commands.put("join", new Channel());
		commands.put("channelinfo", new Channelinfo());
		commands.put("chatinfo", new Chatinfo());
		commands.put("chatreload", new Chatreload());
		commands.put("chlist", new Chlist());
		commands.put("chwho", new Chwho());
		commands.put("clearchat", new Clearchat());
		commands.put("commandblock", new Commandblock());
		commands.put("commandspy", new Commandspy());
		commands.put("config", new Config());
		commands.put("edit", new Edit());
		commands.put("filter", new Filter());
		commands.put("force", new Force());
		commands.put("forceall", new Forceall());
		commands.put("kickchannel", new Kickchannel());
		commands.put("kickchannelall", new Kickchannelall());
		commands.put("leave", new Leave());
		commands.put("listen", new Listen());
		commands.put("me", new Me());
		commands.put("venturechat", new Venturechat());
		commands.put("setnickname", new Nick());
		commands.put("notifications", new Notifications());
		commands.put("party", new Party());
		commands.put("rangedspy", new RangedSpy());
		commands.put("removemessage", new Removemessage());
		commands.put("setchannel", new Setchannel());
		commands.put("setchannelall", new Setchannelall());
		commands.put("spy", new Spy());
		commands.put("venturechatgui", new VentureChatGui());
		commands.put("messagetoggle", new MessageToggle());
		commands.put("bungeetoggle", new BungeeToggle());
		for(String command : commands.keySet()) {
			registerCommand(command, commandExecutor);
		}
		
		plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
			VentureCommand reply = new Reply();
			commands.put("reply", reply);
			commands.put("r", reply);
			registerCommand("reply", commandExecutor);
			registerCommand("r", commandExecutor);
			
			commands.put("mute", new Mute());
			commands.put("muteall", new Muteall());
			commands.put("unmute", new Unmute());
			commands.put("unmuteall", new Unmuteall());
			registerCommand("mute", commandExecutor);
			registerCommand("muteall", commandExecutor);
			registerCommand("unmute", commandExecutor);
			registerCommand("unmuteall", commandExecutor);
			
			MessageCommandExecutor messageCommandExecutor = new MessageCommandExecutor();
			registerCommand("message", messageCommandExecutor);
			registerCommand("msg", messageCommandExecutor);
			registerCommand("tell", messageCommandExecutor);
			registerCommand("whisper", messageCommandExecutor);
			
			registerCommand("ignore", new IgnoreCommandExecutor());
		}, 0);
	}
	
	private static void registerCommand(String command, CommandExecutor commandExecutor) {
		if(plugin.getCommand(command) != null) {
			plugin.getCommand(command).setExecutor(commandExecutor);
		}
	}
}
